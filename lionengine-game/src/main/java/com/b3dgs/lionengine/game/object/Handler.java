/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.object;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.core.Updatable;

/**
 * Designed to handle {@link ObjectGame}. Maintain an objects list by updating and rendering them. Modifications on the
 * list can be done at any time because their are applied at the beginning of the next update.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see ObjectGame
 * @see ComponentUpdatable
 * @see ComponentRenderable
 */
public class Handler implements HandledObjects, Updatable, Renderable, ObjectGameListener
{
    /** Handler listeners. */
    private final Collection<HandlerListener> listeners;
    /** List of components. */
    private final Set<ComponentUpdatable> updatables;
    /** List of components. */
    private final Set<ComponentRenderable> renderables;
    /** List of items. */
    private final HandledObjectsImpl objects;
    /** To add list. */
    private final Collection<ObjectGame> toAdd;
    /** To delete list. */
    private final Collection<Integer> toDelete;
    /** Will delete flag. */
    private boolean willDelete;
    /** Will add flag. */
    private boolean willAdd;

    /**
     * Create a handler.
     */
    public Handler()
    {
        listeners = new ArrayList<HandlerListener>();
        updatables = new HashSet<ComponentUpdatable>();
        renderables = new HashSet<ComponentRenderable>();
        objects = new HandledObjectsImpl();
        toDelete = new ArrayList<Integer>();
        toAdd = new ArrayList<ObjectGame>();
        willDelete = false;
        willAdd = false;
    }

    /**
     * Add a handler listener.
     * 
     * @param listener The listener to add.
     */
    public final void addListener(HandlerListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Remove a handler listener.
     * 
     * @param listener The listener to remove.
     */
    public final void removeListener(HandlerListener listener)
    {
        listeners.remove(listener);
    }

    /**
     * Add an updatable component.
     * 
     * @param component The component to add.
     */
    public final void addUpdatable(ComponentUpdatable component)
    {
        updatables.add(component);
    }

    /**
     * Add a renderable component.
     * 
     * @param component The component to add.
     */
    public final void addRenderable(ComponentRenderable component)
    {
        renderables.add(component);
    }

    /**
     * Add an object to the list. Will be added at the beginning of {@link #update(double)} call. If this function is
     * called during {@link #update(double)}, it will be delayed to next {@link #update(double)} call.
     * 
     * @param object The object to add.
     */
    public final void add(ObjectGame object)
    {
        object.addListener(this);
        toAdd.add(object);
        willAdd = true;
    }

    /**
     * Remove an object from the remove list. Will be removed at the beginning of {@link #update(double)} call. If
     * this function is called during {@link #update(double)}, it will be delayed to next {@link #update(double)} call.
     * 
     * @param object The object to remove.
     */
    public final void remove(ObjectGame object)
    {
        toDelete.add(object.getId());
        willDelete = true;
    }

    /**
     * Remove all objects from the list. Will be removed at the beginning of {@link #update(double)} call. If
     * this function is called during {@link #update(double)}, it will be delayed to next {@link #update(double)} call.
     */
    public final void removeAll()
    {
        toDelete.addAll(objects.getIds());
        willDelete = true;
    }

    /**
     * Get the number of handled objects.
     * 
     * @return The number of handled objects.
     */
    public final int size()
    {
        return objects.getIds().size();
    }

    /**
     * Update the add list.
     */
    private void updateAdd()
    {
        if (willAdd)
        {
            for (final ObjectGame object : toAdd)
            {
                objects.add(object);
                for (final HandlerListener listener : listeners)
                {
                    listener.notifyObjectAdded(object);
                }
            }
            toAdd.clear();
            willAdd = false;
        }
    }

    /**
     * Update the remove list.
     */
    private void updateRemove()
    {
        if (willDelete)
        {
            for (final Integer id : toDelete)
            {
                for (final HandlerListener listener : listeners)
                {
                    listener.notifyObjectRemoved(objects.get(id));
                }
                objects.remove(id);
            }
            toDelete.clear();
            willDelete = false;
        }
    }

    /*
     * HandledObjects
     */

    @Override
    public final ObjectGame get(Integer id)
    {
        return objects.get(id);
    }

    @Override
    public <I> Iterable<I> get(Class<I> type)
    {
        return objects.get(type);
    }

    @Override
    public Iterable<ObjectGame> values()
    {
        return objects.values();
    }

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        updateRemove();
        updateAdd();
        for (final ComponentUpdatable component : updatables)
        {
            component.update(extrp, objects);
        }
    }

    /*
     * Renderable
     */

    @Override
    public void render(Graphic g)
    {
        for (final ComponentRenderable component : renderables)
        {
            component.render(g, objects);
        }
    }

    /*
     * ObjectGameListener
     */

    @Override
    public final void notifyDestroyed(ObjectGame object)
    {
        toDelete.add(object.getId());
        willDelete = true;
    }
}
