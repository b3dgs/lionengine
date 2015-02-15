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
package com.b3dgs.lionengine.game.handler;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Features;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.factory.Factory;
import com.b3dgs.lionengine.game.factory.Setup;
import com.b3dgs.lionengine.game.trait.Trait;

/**
 * Game object minimal representation. Defined by a unique ID, the object is designed to be handled by a {@link Handler}
 * . To remove it from the handler, a simple call to {@link #destroy()} is needed.
 * <p>
 * An object can also be externally configured by using a {@link Configurer}, filled by an XML file.
 * </p>
 * <p>
 * Objects are also designed to be created by a {@link Factory}. In that case, they must have at least a constructor
 * with a single argument, which must be type of {@link Setup}.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Configurer
 * @see Factory
 * @see Handler
 */
public class ObjectGame
{
    /** Free id error. */
    private static final String ERROR_FREE_ID = "No more free id available !";
    /** Id used. */
    private static final Collection<Integer> IDS = new HashSet<>(16);
    /** Recycle id. */
    private static final Queue<Integer> RECYCLE = new LinkedList<>();
    /** Last id used. */
    private static int lastId = 0;

    /** Features provider. */
    private final Features<Trait> features;
    /** Listeners. */
    private final Collection<HandlableListener> listeners;
    /** Unique id. */
    private Integer id;
    /** Destroyed flag. */
    private boolean destroyed;

    /**
     * Get the next unused id.
     * 
     * @return The next unused id.
     * @throws LionEngineException If there is more than {@link Integer#MAX_VALUE} at the same time.
     */
    private static Integer getFreeId() throws LionEngineException
    {
        if (!RECYCLE.isEmpty())
        {
            return RECYCLE.poll();
        }
        if (IDS.size() >= Integer.MAX_VALUE)
        {
            throw new LionEngineException(ERROR_FREE_ID);
        }
        while (IDS.contains(Integer.valueOf(lastId)))
        {
            lastId++;
        }
        return Integer.valueOf(lastId);
    }

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     * @throws LionEngineException If there is more than {@link Integer#MAX_VALUE} objects at the same time.
     */
    public ObjectGame(Setup setup, Services services) throws LionEngineException
    {
        Check.notNull(setup);
        Check.notNull(services);

        id = getFreeId();
        features = new Features<>(Trait.class);
        listeners = new HashSet<>(1);
        IDS.add(id);
        destroyed = false;
    }

    /**
     * Add a trait.
     * 
     * @param trait The trait to add.
     */
    public final void addTrait(Trait trait)
    {
        features.add(trait);
    }

    /**
     * Get a trait from its class.
     * 
     * @param trait The trait class.
     * @return The trait instance (<code>null</code> if none).
     */
    public final <C> C getTrait(Class<C> trait)
    {
        return features.get(trait);
    }

    /**
     * Get all traits.
     * 
     * @return The traits list.
     */
    public final Iterable<Trait> getTraits()
    {
        return features.getAll();
    }

    /**
     * Get all traits types.
     * 
     * @return The traits types.
     */
    public final Iterable<Class<? extends Trait>> getTraitsType()
    {
        return features.getFeatures();
    }

    /**
     * Get the id (<code>null</code> will be returned once removed from {@link Handler} after a call to
     * {@link #destroy()}).
     * 
     * @return The id.
     */
    public final Integer getId()
    {
        return id;
    }

    /**
     * Declare as removable from handler. Will be removed on next {@link Handler#update(double)} call.
     */
    public final void destroy()
    {
        if (!destroyed)
        {
            destroyed = true;
            for (final HandlableListener listener : listeners)
            {
                listener.notifyDestroyed(this);
            }
        }
    }

    /**
     * Add a handlable listener.
     * 
     * @param listener The listener reference.
     */
    final void addListener(HandlableListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Called when has been removed from handler.
     */
    final void onRemoved()
    {
        if (destroyed)
        {
            IDS.remove(id);
            RECYCLE.add(id);
            id = null;
        }
    }
}
