/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.b3dgs.lionengine.game.FeatureProvider;

/**
 * Refresher component implementation which refreshes {@link Refreshable} elements with {@link Layerable} support to
 * order rendering. If there is not {@link Layerable} feature, {@link #LAYER_DEFAULT} will be used as default layer
 * value. The lower layer index is handled first.
 */
public class ComponentRefreshable implements ComponentUpdater, HandlerListener, LayerableListener
{
    /** Default layer value. */
    private static final Integer LAYER_DEFAULT = Integer.valueOf(0);

    /**
     * Get the featurable layer.
     * 
     * @param featurable The featurable reference.
     * @return The featurable layer if is {@link Layerable}, {@link #LAYER_DEFAULT} else.
     */
    private static Integer getLayer(Featurable featurable)
    {
        if (featurable.hasFeature(Layerable.class))
        {
            final Layerable layerable = featurable.getFeature(Layerable.class);
            return layerable.getLayerRefresh();
        }
        return LAYER_DEFAULT;
    }

    /** Sorted layers index. */
    private final List<Integer> indexs = new ArrayList<>();
    /** Sorted layers index. */
    private final Set<Integer> indexsSet = new HashSet<>();
    /** Layers to render. */
    private final Map<Integer, List<Refreshable>> layers = new HashMap<>();
    /** Layer to update. */
    private final List<LayerUpdate> toUpdate = new ArrayList<>();
    /** Update flag. */
    private boolean updateRequested;

    /**
     * Create component.
     */
    public ComponentRefreshable()
    {
        super();
    }

    /**
     * Get the layer set at the specified index. Creates an empty set if no set already defined.
     * 
     * @param layer The layer index.
     * @return The layer set reference.
     */
    private List<Refreshable> getLayer(Integer layer)
    {
        final List<Refreshable> refreshables;
        if (!layers.containsKey(layer))
        {
            refreshables = new ArrayList<>();
            layers.put(layer, refreshables);
        }
        else
        {
            refreshables = layers.get(layer);
        }
        return refreshables;
    }

    /**
     * Remove refreshable and its layer.
     * 
     * @param layer The layer index.
     * @param refreshable The refreshable to remove.
     */
    private void remove(Integer layer, Refreshable refreshable)
    {
        final List<Refreshable> refreshables = getLayer(layer);
        refreshables.remove(refreshable);
        if (refreshables.isEmpty())
        {
            indexs.remove(layer);
            indexsSet.remove(layer);
            Collections.sort(indexs);
        }
    }

    @Override
    public void update(double extrp, Handlables featurables)
    {
        for (int l = 0; l < indexs.size(); l++)
        {
            final List<Refreshable> refreshable = layers.get(indexs.get(l));
            final int count = refreshable.size();
            for (int i = 0; i < count; i++)
            {
                refreshable.get(i).update(extrp);
            }
        }
        if (updateRequested)
        {
            final int n = toUpdate.size();
            for (int i = 0; i < n; i++)
            {
                final LayerUpdate update = toUpdate.get(i);
                getLayer(update.layerOld).remove(update.refreshable);
                getLayer(update.layerNew).add(update.refreshable);

                if (indexsSet.remove(update.layerOld))
                {
                    indexs.remove(update.layerOld);
                }
                indexs.add(update.layerNew);
                indexsSet.add(update.layerNew);
            }
            Collections.sort(indexs);
            toUpdate.clear();
            updateRequested = false;
        }
    }

    @Override
    public void notifyHandlableAdded(Featurable featurable)
    {
        if (featurable.hasFeature(Refreshable.class))
        {
            final Refreshable refreshable = featurable.getFeature(Refreshable.class);
            final Integer layer = getLayer(featurable);
            final Collection<Refreshable> refreshables = getLayer(layer);
            refreshables.add(refreshable);
            if (indexsSet.add(layer))
            {
                indexs.add(layer);
                Collections.sort(indexs);
            }
        }
        if (featurable.hasFeature(Layerable.class))
        {
            featurable.getFeature(Layerable.class).addListener(this);
        }
    }

    @Override
    public void notifyHandlableRemoved(Featurable featurable)
    {
        if (featurable.hasFeature(Refreshable.class))
        {
            final Refreshable refreshable = featurable.getFeature(Refreshable.class);
            final Integer layer = getLayer(featurable);
            remove(layer, refreshable);
        }
        if (featurable.hasFeature(Layerable.class))
        {
            featurable.getFeature(Layerable.class).removeListener(this);
        }
    }

    @Override
    public void notifyLayerChanged(FeatureProvider provider,
                                   Integer layerRefreshOld,
                                   Integer layerRefreshNew,
                                   Integer layerDisplayOld,
                                   Integer layerDisplayNew)
    {
        if (!layerRefreshNew.equals(layerRefreshOld) && provider.hasFeature(Refreshable.class))
        {
            final Refreshable refreshable = provider.getFeature(Refreshable.class);
            toUpdate.add(new LayerUpdate(refreshable, layerRefreshOld, layerRefreshNew));
            updateRequested = true;
        }
    }

    /**
     * Layer update data.
     * 
     * @param refreshable The refreshable reference.
     * @param layerOld The old layer.
     * @param layerNew The new layer.
     */
    private record LayerUpdate(Refreshable refreshable, Integer layerOld, Integer layerNew)
    {
    }
}
