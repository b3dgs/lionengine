/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.feature;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Refresher component implementation which refreshes {@link Refreshable} elements with {@link Layerable} support to
 * order rendering. If there is not {@link Layerable} feature, {@link #LAYER_DEFAULT} will be used as default layer
 * value.
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
        for (final Integer layer : indexs)
        {
            for (final Refreshable refreshable : layers.get(layer))
            {
                refreshable.update(extrp);
            }
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
            getLayer(layerRefreshOld).remove(refreshable);
            getLayer(layerRefreshNew).add(refreshable);
            indexs.add(layerRefreshNew);
        }
    }
}
