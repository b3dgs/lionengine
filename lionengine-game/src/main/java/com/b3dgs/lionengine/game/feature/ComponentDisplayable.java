/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Renderer component implementation which render {@link Displayable} elements with {@link Layerable} support to order
 * rendering. If there is not {@link Layerable} feature, {@link #LAYER_DEFAULT} will be used as default layer value.
 * The lower layer index is handled first.
 */
public class ComponentDisplayable implements ComponentRenderer, HandlerListener, LayerableListener
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
            return layerable.getLayerDisplay();
        }
        return LAYER_DEFAULT;
    }

    /** Sorted layers index. */
    private final Set<Integer> indexs = new TreeSet<>();
    /** Layers to render. */
    private final Map<Integer, Collection<Displayable>> layers = new HashMap<>();

    /**
     * Create component.
     */
    public ComponentDisplayable()
    {
        super();
    }

    /**
     * Get the layer set at the specified index. Creates an empty set if no set already defined.
     * 
     * @param layer The layer index.
     * @return The layer set reference.
     */
    private Collection<Displayable> getLayer(Integer layer)
    {
        final Collection<Displayable> displayables;
        if (!layers.containsKey(layer))
        {
            displayables = new HashSet<>();
            layers.put(layer, displayables);
        }
        else
        {
            displayables = layers.get(layer);
        }
        return displayables;
    }

    /**
     * Remove displayable and its layer.
     * 
     * @param layer The layer index.
     * @param displayable The displayable to remove.
     */
    private void remove(Integer layer, Displayable displayable)
    {
        final Collection<Displayable> displayables = getLayer(layer);
        displayables.remove(displayable);
        if (displayables.isEmpty())
        {
            indexs.remove(layer);
        }
    }

    /*
     * ComponentRenderer
     */

    @Override
    public void render(Graphic g, Handlables featurables)
    {
        for (final Integer layer : indexs)
        {
            for (final Displayable displayable : layers.get(layer))
            {
                displayable.render(g);
            }
        }
    }

    /*
     * HandlerListener
     */

    @Override
    public void notifyHandlableAdded(Featurable featurable)
    {
        if (featurable.hasFeature(Displayable.class))
        {
            final Displayable displayable = featurable.getFeature(Displayable.class);
            final Integer layer = getLayer(featurable);
            final Collection<Displayable> displayables = getLayer(layer);
            displayables.add(displayable);
            indexs.add(layer);
        }
    }

    @Override
    public void notifyHandlableRemoved(Featurable featurable)
    {
        if (featurable.hasFeature(Displayable.class))
        {
            final Displayable displayable = featurable.getFeature(Displayable.class);
            final Integer layer = getLayer(featurable);
            remove(layer, displayable);
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
        if (provider.hasFeature(Displayable.class))
        {
            final Displayable displayable = provider.getFeature(Displayable.class);
            getLayer(layerDisplayOld).remove(displayable);
            getLayer(layerDisplayNew).add(displayable);
            indexs.add(layerDisplayNew);
        }
    }
}
