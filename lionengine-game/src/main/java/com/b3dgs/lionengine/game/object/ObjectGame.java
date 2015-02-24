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

import java.util.Collection;
import java.util.HashSet;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Features;
import com.b3dgs.lionengine.game.configurer.Configurer;
import com.b3dgs.lionengine.game.trait.Trait;

/**
 * Object minimal representation. Defined by a unique ID, the object is designed to be handled by a {@link Handler} . To
 * remove it from the handler, a simple call to {@link #destroy()} is needed.
 * <p>
 * An object can also be externally configured by using a {@link Configurer}, filled from an XML file.
 * </p>
 * <p>
 * They are also designed to be created by a {@link Factory}. In that case, they must have at least a constructor with a
 * single argument, which must be a type of {@link Setup}.
 * </p>
 * <p>
 * It is possible to retrieve external {@link Services} when object is being constructed.
 * </p>
 * <p>
 * Instead of using traditional interface implementation, it is possible to use {@link com.b3dgs.lionengine.game.trait}
 * system, in order to reduce class complexity. The {@link Handler} is designed to work well with that system.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Configurer
 * @see Factory
 * @see Handler
 * @see Trait
 * @see Setup
 * @see Services
 */
public class ObjectGame
{
    /** Features provider. */
    private final Features<Trait> features;
    /** Listeners. */
    private final Collection<ObjectGameListener> listeners;
    /** Unique id. */
    private Integer id;
    /** Destroyed flag. */
    private boolean destroyed;

    /**
     * Create an object.
     * 
     * @param setup The setup reference (resources sharing entry point).
     * @param services The services reference (external services provider).
     */
    public ObjectGame(Setup setup, Services services)
    {
        Check.notNull(setup);
        Check.notNull(services);

        features = new Features<>(Trait.class);
        listeners = new HashSet<>(1);
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
     * Check if object has the following trait.
     * 
     * @param trait The trait to check (could be a {@link Trait} or a classic interface.
     * @return <code>true</code> if has trait, <code>false</code> else.
     */
    public final boolean hasTrait(Class<?> trait)
    {
        return features.contains(trait);
    }

    /**
     * Get a trait from its class.
     * 
     * @param <T> The trait class type used.
     * @param trait The trait class.
     * @return The trait instance.
     * @throws LionEngineException If feature was not found.
     */
    public final <T> T getTrait(Class<T> trait) throws LionEngineException
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
     * {@link #destroy()}, or if has never been added by {@link Handler#add(ObjectGame)}).
     * 
     * @return The object unique id.
     */
    public final Integer getId()
    {
        return id;
    }

    /**
     * Declare as removable from handler. Will be removed on next {@link Handler#update(double)} call.
     * Can be destroyed only one time.
     */
    public final void destroy()
    {
        if (!destroyed)
        {
            destroyed = true;
            for (final ObjectGameListener listener : listeners)
            {
                listener.notifyDestroyed(this);
            }
        }
    }

    /**
     * Add an object listener.
     * 
     * @param listener The listener reference.
     */
    final void addListener(ObjectGameListener listener)
    {
        listeners.add(listener);
    }

    /**
     * Set the object id. Should only be set when adding to handled list by {@link HandledObjects}.
     * 
     * @param id The id to set.
     */
    final void setId(Integer id)
    {
        this.id = id;
    }
}
