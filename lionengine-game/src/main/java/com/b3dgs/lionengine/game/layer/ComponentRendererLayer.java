/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.layer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.handler.ComponentRenderable;
import com.b3dgs.lionengine.game.handler.Handlable;
import com.b3dgs.lionengine.game.handler.Handlables;
import com.b3dgs.lionengine.game.handler.HandlerListener;
import com.b3dgs.lionengine.game.object.feature.displayable.Displayable;
import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Renderer component implementation which render {@link Displayable} elements with {@link Layerable} support to order
 * rendering. If there is not {@link Layerable} feature, {@link #LAYER_DEFAULT} will be used as default layer value.
 */
public class ComponentRendererLayer implements ComponentRenderable, HandlerListener, LayerableListener
{
    /** Default layer value. */
    private static final Integer LAYER_DEFAULT = Integer.valueOf(0);

    /**
     * Get the handlable layer.
     * 
     * @param handlable The handlable reference.
     * @return The handlable layer if is {@link Layerable}, {@link #LAYER_DEFAULT} else.
     */
    private static Integer getLayer(Handlable handlable)
    {
        if (handlable.hasFeature(Layerable.class))
        {
            final Layerable layerable = handlable.getFeature(Layerable.class);
            return layerable.getLayer();
        }
        return LAYER_DEFAULT;
    }

    /** Sorted layers index. */
    private final Set<Integer> indexs;
    /** Layers to render. */
    private final Map<Integer, Collection<Displayable>> layers;

    /**
     * Create a renderer component.
     */
    public ComponentRendererLayer()
    {
        indexs = new TreeSet<Integer>();
        layers = new HashMap<Integer, Collection<Displayable>>();
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
            displayables = new ArrayList<Displayable>();
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
     * ComponentRenderable
     */

    @Override
    public void render(Graphic g, Handlables handlables)
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
    public void notifyHandlableAdded(Handlable handlable)
    {
        if (handlable.hasFeature(Displayable.class))
        {
            final Displayable displayable = handlable.getFeature(Displayable.class);
            final Integer layer = getLayer(handlable);
            final Collection<Displayable> displayables = getLayer(layer);
            displayables.add(displayable);
            indexs.add(layer);
        }
    }

    @Override
    public void notifyHandlableRemoved(Handlable handlable)
    {
        if (handlable.hasFeature(Displayable.class))
        {
            final Displayable displayable = handlable.getFeature(Displayable.class);
            final Integer layer = getLayer(handlable);
            remove(layer, displayable);
        }
    }

    /*
     * LayerableListener
     */

    @Override
    public void notifyLayerChanged(Featurable featurable, Integer layerOld, Integer layerNew)
    {
        if (featurable.hasFeature(Displayable.class))
        {
            final Displayable displayable = featurable.getFeature(Displayable.class);
            getLayer(layerOld).remove(displayable);
            getLayer(layerNew).add(displayable);
            indexs.add(layerNew);
        }
    }
}
