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
package com.b3dgs.lionengine.game.handler;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.object.ComponentRenderable;
import com.b3dgs.lionengine.game.object.ComponentUpdatable;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Designed to handler updating and rendering for a set of components.
 * Can handle {@link Handlable}. Maintain a handlable list by updating and rendering them. Modifications on the
 * list can be done at any time because their are applied at the beginning of the next update.
 * 
 * @see Updatable
 * @see Renderable
 * @see ComponentUpdatable
 * @see ComponentRenderable
 */
public class Handler implements Handlables, Updatable, Renderable, IdentifiableListener
{
    /** Handler listeners. */
    private final Collection<HandlerListener> listeners = new HashSet<HandlerListener>();
    /** List of components. */
    private final Set<ComponentUpdatable> updatables = new HashSet<ComponentUpdatable>();
    /** List of components. */
    private final Set<ComponentRenderable> renderables = new HashSet<ComponentRenderable>();
    /** List of items. */
    private final HandledHandlablesImpl handlables = new HandledHandlablesImpl();
    /** To add list. */
    private final Collection<Handlable> toAdd = new HashSet<Handlable>();
    /** To delete list. */
    private final Collection<Integer> toDelete = new HashSet<Integer>();
    /** Services reference. */
    private final Services services;
    /** Will delete flag. */
    private boolean willDelete;
    /** Will add flag. */
    private boolean willAdd;

    /**
     * Create a handler.
     * 
     * @param services The services reference.
     */
    public Handler(Services services)
    {
        super();
        this.services = services;
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
     * Add a handlable to the list. Will be added at the beginning of {@link #update(double)} call.
     * If this function is called during {@link #update(double)}, it will be delayed to next {@link #update(double)}
     * call.
     * 
     * @param handlable The handlable to add.
     */
    public final void add(Handlable handlable)
    {
        handlable.addListener(this);
        toAdd.add(handlable);
        willAdd = true;
    }

    /**
     * Remove a handlable from the remove list. Will be removed at the beginning of {@link #update(double)} call.
     * If this function is called during {@link #update(double)}, it will be delayed to next {@link #update(double)}
     * call.
     * 
     * @param handlable The handlable to remove.
     */
    public final void remove(Handlable handlable)
    {
        toDelete.add(handlable.getId());
        willDelete = true;
    }

    /**
     * Remove all handlables from the list. Will be removed at the beginning of {@link #update(double)} call.
     * If this function is called during {@link #update(double)}, it will be delayed to next {@link #update(double)}
     * call.
     */
    public final void removeAll()
    {
        toDelete.addAll(handlables.getIds());
        willDelete = true;
    }

    /**
     * Get the number of handled handlables.
     * 
     * @return The number of handled handlables.
     */
    public final int size()
    {
        return handlables.getIds().size();
    }

    /**
     * Update the add list.
     */
    private void updateAdd()
    {
        if (willAdd)
        {
            for (final Handlable handlable : toAdd)
            {
                handlable.prepareFeatures(handlable, services);
                handlables.add(handlable);
                for (final HandlerListener listener : listeners)
                {
                    listener.notifyHandlableAdded(handlable);
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
                final Handlable handlable = handlables.get(id);
                handlables.remove(id);
                handlable.notifyDestroyed();
                for (final HandlerListener listener : listeners)
                {
                    listener.notifyHandlableRemoved(handlable);
                }
            }
            toDelete.clear();
            willDelete = false;
        }
    }

    /*
     * HandledObjects
     */

    @Override
    public final Handlable get(Integer id)
    {
        return handlables.get(id);
    }

    @Override
    public <I> Iterable<I> get(Class<I> type)
    {
        return handlables.get(type);
    }

    @Override
    public Iterable<Handlable> values()
    {
        return handlables.values();
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
            component.update(extrp, handlables);
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
            component.render(g, handlables);
        }
    }

    /*
     * IdentifiableListener
     */

    @Override
    public final void notifyDestroyed(Integer id)
    {
        toDelete.add(id);
        willDelete = true;
    }
}
