/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

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
    private final Set<Integer> indexs = new TreeSet<>();
    /** Layers to render. */
    private final Map<Integer, Collection<Refreshable>> layers = new HashMap<>();
    /** Layer to backup. */
    private final List<Transformable> toBackup = new ArrayList<>();
    /** Layer to update. */
    private final Collection<LayerUpdate> toUpdate = new ArrayList<>();
    /** Update flag. */
    private boolean updateRequested;
    /** To backup size. */
    private int backupCount;

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
    private Collection<Refreshable> getLayer(Integer layer)
    {
        final Collection<Refreshable> refreshables;
        if (!layers.containsKey(layer))
        {
            refreshables = new HashSet<>();
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
        final Collection<Refreshable> refreshables = getLayer(layer);
        refreshables.remove(refreshable);
        if (refreshables.isEmpty())
        {
            indexs.remove(layer);
        }
    }

    /*
     * ComponentUpdater
     */

    @Override
    public void update(double extrp, Handlables featurables)
    {
        for (int i = 0; i < backupCount; i++)
        {
            toBackup.get(i).backup();
        }

        for (final Integer layer : indexs)
        {
            for (final Refreshable refreshable : layers.get(layer))
            {
                refreshable.update(extrp);
            }
        }
        if (updateRequested)
        {
            for (final LayerUpdate update : toUpdate)
            {
                getLayer(update.layerOld).remove(update.refreshable);
                getLayer(update.layerNew).add(update.refreshable);
                indexs.add(update.layerNew);
            }
            toUpdate.clear();
            updateRequested = false;
        }
    }

    /*
     * HandlerListener
     */

    @Override
    public void notifyHandlableAdded(Featurable featurable)
    {
        if (featurable.hasFeature(Refreshable.class))
        {
            final Refreshable refreshable = featurable.getFeature(Refreshable.class);
            final Integer layer = getLayer(featurable);
            final Collection<Refreshable> refreshables = getLayer(layer);
            refreshables.add(refreshable);
            indexs.add(layer);
        }
        if (featurable.hasFeature(Layerable.class))
        {
            featurable.getFeature(Layerable.class).addListener(this);
        }
        featurable.ifIs(Transformable.class, t ->
        {
            toBackup.add(t);
            backupCount++;
        });
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
        featurable.ifIs(Transformable.class, t ->
        {
            toBackup.remove(t);
            backupCount--;
        });
    }

    /*
     * LayerableListener
     */

    @Override
    public void notifyLayerChanged(FeatureProvider provider,
                                   Integer layerRefreshOld,
                                   Integer layerRefreshNew,
                                   Integer layerDisplayOld,
                                   Integer layerDisplayNew)
    {
        if (provider.hasFeature(Refreshable.class))
        {
            final Refreshable refreshable = provider.getFeature(Refreshable.class);
            toUpdate.add(new LayerUpdate(refreshable, layerRefreshOld, layerRefreshNew));
            updateRequested = true;
        }
    }

    /**
     * Layer update data.
     */
    private static final class LayerUpdate
    {
        /** Refreshable reference. */
        private final Refreshable refreshable;
        /** Old layer. */
        private final Integer layerOld;
        /** New layer. */
        private final Integer layerNew;

        /**
         * Create data.
         * 
         * @param refreshable The refreshable reference.
         * @param layerOld The old layer.
         * @param layerNew The new layer.
         */
        private LayerUpdate(Refreshable refreshable, Integer layerOld, Integer layerNew)
        {
            super();

            this.refreshable = refreshable;
            this.layerOld = layerOld;
            this.layerNew = layerNew;
        }
    }
}
