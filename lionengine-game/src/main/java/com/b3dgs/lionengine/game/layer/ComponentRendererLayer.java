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

import com.b3dgs.lionengine.game.object.ComponentRenderable;
import com.b3dgs.lionengine.game.object.HandledObjects;
import com.b3dgs.lionengine.game.object.HandlerListener;
import com.b3dgs.lionengine.game.object.ObjectGame;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Renderer component implementation which render {@link Renderable} objects with {@link Layerable} support to order
 * rendering.
 */
public class ComponentRendererLayer implements ComponentRenderable, HandlerListener, LayerableListener
{
    /** Sorted layers index. */
    private final Set<Integer> indexs;
    /** Layers to render. */
    private final Map<Integer, Collection<Renderable>> layers;

    /**
     * Create a renderer component.
     */
    public ComponentRendererLayer()
    {
        indexs = new TreeSet<Integer>();
        layers = new HashMap<Integer, Collection<Renderable>>();
    }

    /**
     * Get the layer set at the specified index. Creates an empty set if no set already defined.
     * 
     * @param layer The layer index.
     * @return The layer set reference.
     */
    private Collection<Renderable> getLayer(Integer layer)
    {
        final Collection<Renderable> objects;
        if (!layers.containsKey(layer))
        {
            objects = new ArrayList<Renderable>();
            layers.put(layer, objects);
        }
        else
        {
            objects = layers.get(layer);
        }
        return objects;
    }

    /**
     * Remove renderable and its layer.
     * 
     * @param layer The layer index.
     * @param renderable The renderable to remove.
     */
    private void remove(Integer layer, Renderable renderable)
    {
        final Collection<Renderable> elements = getLayer(layer);
        elements.remove(renderable);
        if (elements.isEmpty())
        {
            indexs.remove(layer);
        }
    }

    /*
     * ComponentRenderable
     */

    @Override
    public void render(Graphic g, HandledObjects objects)
    {
        for (final Integer layer : indexs)
        {
            for (final Renderable renderable : layers.get(layer))
            {
                renderable.render(g);
            }
        }
    }

    /*
     * HandlerListener
     */

    @Override
    public void notifyObjectAdded(ObjectGame object)
    {
        if (object.hasTrait(Layerable.class) && object.hasTrait(Renderable.class))
        {
            final Layerable layerable = object.getTrait(Layerable.class);
            final Renderable renderable = object.getTrait(Renderable.class);
            final Integer layer = layerable.getLayer();
            final Collection<Renderable> objects = getLayer(layer);
            objects.add(renderable);
            indexs.add(layer);
        }
    }

    @Override
    public void notifyObjectRemoved(ObjectGame object)
    {
        if (object.hasTrait(Layerable.class) && object.hasTrait(Renderable.class))
        {
            final Layerable layerable = object.getTrait(Layerable.class);
            final Renderable renderable = object.getTrait(Renderable.class);
            final Integer layer = layerable.getLayer();
            remove(layer, renderable);
        }
    }

    @Override
    public void notifyLayerChanged(ObjectGame object, Integer oldLayer, Integer newLayer)
    {
        if (object.hasTrait(Renderable.class))
        {
            final Renderable renderable = object.getTrait(Renderable.class);
            getLayer(oldLayer).remove(renderable);
            getLayer(newLayer).add(renderable);
            indexs.add(newLayer);
        }
    }
}
