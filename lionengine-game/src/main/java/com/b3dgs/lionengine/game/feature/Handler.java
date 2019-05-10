/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.Renderable;

/**
 * Designed to handle {@link Featurable}, updating and rendering a set of components.
 * Modifications on the list can be done at any time because they are applied at the beginning of the next update.
 * 
 * @see HandlerListener
 * @see ComponentUpdater
 * @see ComponentRenderer
 */
public class Handler implements Handlables, Updatable, Renderable, IdentifiableListener
{
    /** Handler listeners. */
    private final Collection<HandlerListener> listeners = new HashSet<>();
    /** List of components updater. */
    private final Collection<ComponentUpdater> updaters = new ArrayList<>();
    /** List of components renderer. */
    private final Collection<ComponentRenderer> renderers = new ArrayList<>();
    /** List of featurables. */
    private final HandlablesImpl featurables = new HandlablesImpl();
    /** To add list. */
    private final Map<Integer, Featurable> toAdd = new HashMap<>();
    /** To remove list. */
    private final Collection<Integer> toRemove = new HashSet<>();
    /** Services reference. */
    private final Services services;
    /** Will remove flag. */
    private boolean willRemove;
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
     * Add a featurable to the list. Will be added at the beginning of {@link #update(double)} call.
     * If this function is called during {@link #update(double)}, it will be delayed to next {@link #update(double)}
     * call.
     * <p>
     * Automatically add {@link IdentifiableModel} if feature does not have {@link Identifiable} feature.
     * </p>
     * 
     * @param featurable The featurable to add.
     */
    public final void add(Featurable featurable)
    {
        final Identifiable identifiable = featurable.getFeature(Identifiable.class);
        identifiable.addListener(this);
        toAdd.put(identifiable.getId(), featurable);
        willAdd = true;
    }

    /**
     * Remove a featurable from the remove list. Will be removed at the beginning of {@link #update(double)} call.
     * If this function is called during {@link #update(double)}, it will be delayed to next {@link #update(double)}
     * call.
     * 
     * @param featurable The featurable to remove.
     */
    public final void remove(FeatureProvider featurable)
    {
        toRemove.add(featurable.getFeature(Identifiable.class).getId());
        willRemove = true;
    }

    /**
     * Remove all featurables from the list. Will be removed at the beginning of {@link #update(double)} call.
     * If this function is called during {@link #update(double)}, it will be delayed to next {@link #update(double)}
     * call.
     */
    public final void removeAll()
    {
        toRemove.addAll(featurables.getIds());
        willRemove = true;
    }

    /**
     * Get the number of handled featurables.
     * 
     * @return The number of handled featurables.
     */
    public final int size()
    {
        return featurables.getIds().size();
    }

    /**
     * Update the add list. Prepare features, add to main list and notify listeners.
     */
    private void updateAdd()
    {
        for (final Featurable featurable : toAdd.values())
        {
            featurables.add(featurable);
            for (final HandlerListener listener : listeners)
            {
                listener.notifyHandlableAdded(featurable);
            }
            if (featurable.hasFeature(Transformable.class))
            {
                final Transformable transformable = featurable.getFeature(Transformable.class);
                transformable.teleport(transformable.getX(), transformable.getY());
            }
        }
        toAdd.clear();
        willAdd = false;
    }

    /**
     * Update the remove list. Remove from main list and notify listeners. Notify featurable destroyed.
     */
    private void updateRemove()
    {
        for (final Integer id : toRemove)
        {
            if (toAdd.remove(id) == null)
            {
                final Featurable featurable = featurables.get(id);
                for (final HandlerListener listener : listeners)
                {
                    listener.notifyHandlableRemoved(featurable);
                }
                featurable.getFeature(Identifiable.class).notifyDestroyed();
                featurables.remove(featurable, id);
            }
        }
        toRemove.clear();
        willRemove = false;
    }

    /*
     * Handlables
     */

    @Override
    public final Featurable get(Integer id)
    {
        return featurables.get(id);
    }

    @Override
    public <I> Iterable<I> get(Class<I> type)
    {
        return featurables.get(type);
    }

    @Override
    public Iterable<Featurable> values()
    {
        return featurables.values();
    }

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        if (willRemove)
        {
            updateRemove();
        }
        if (willAdd)
        {
            updateAdd();
        }
        for (final ComponentUpdater component : updaters)
        {
            component.update(extrp, featurables);
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
            component.render(g, featurables);
        }
    }

    /*
     * IdentifiableListener
     */

    @Override
    public final void notifyDestroyed(Integer id)
    {
        toRemove.add(id);
        willRemove = true;
    }
}
