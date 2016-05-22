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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.layer.ComponentDisplayerLayer;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Designed to handle {@link Handlable}, updating and rendering a set of components.
 * Modifications on the list can be done at any time because they are applied at the beginning of the next update.
 * 
 * @see HandlerListener
 * @see ComponentUpdater
 * @see ComponentRenderer
 */
public class Handler implements Handlables, Updatable, Renderable, IdentifiableListener
{
    /** Handler listeners. */
    private final Collection<HandlerListener> listeners = new HashSet<HandlerListener>();
    /** List of components. */
    private final Collection<ComponentUpdater> updaters = new ArrayList<ComponentUpdater>();
    /** List of components. */
    private final Collection<ComponentRenderer> renderers = new ArrayList<ComponentRenderer>();
    /** List of items. */
    private final HandlablesImpl handlables = new HandlablesImpl();
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
        addComponent(new ComponentRefreshable());
        addComponent(new ComponentDisplayerLayer());
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
     * Add an updater component. Automatically added to {@link Services} and {@link #addListener(HandlerListener)} if
     * interface compatible.
     * 
     * @param component The component to add.
     */
    public final void addComponent(ComponentUpdater component)
    {
        updaters.add(component);
        services.add(component);
        if (component instanceof HandlerListener)
        {
            addListener((HandlerListener) component);
        }
    }

    /**
     * Add a renderer component. Automatically added to {@link Services} and {@link #addListener(HandlerListener)} if
     * interface compatible.
     * 
     * @param component The component to add.
     */
    public final void addComponent(ComponentRenderer component)
    {
        renderers.add(component);
        services.add(component);
        if (component instanceof HandlerListener)
        {
            addListener((HandlerListener) component);
        }
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
     * Update the add list. Prepare features, add to main list and notify listeners.
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
     * Update the remove list. Remove from main list and notify listeners. Notify handlable destroyed.
     */
    private void updateRemove()
    {
        if (willDelete)
        {
            for (final Integer id : toDelete)
            {
                final Handlable handlable = handlables.get(id);
                handlables.remove(handlable);
                for (final HandlerListener listener : listeners)
                {
                    listener.notifyHandlableRemoved(handlable);
                }
                handlable.notifyDestroyed();
            }
            toDelete.clear();
            willDelete = false;
        }
    }

    /*
     * Handlables
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
        for (final ComponentUpdater component : updaters)
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
        for (final ComponentRenderer component : renderers)
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
