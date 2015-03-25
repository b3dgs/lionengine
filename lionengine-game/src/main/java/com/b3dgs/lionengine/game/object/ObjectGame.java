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

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
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
    /** Error create trait. */
    private static final String ERROR_CREATE_TRAIT = "Error when creating trait: ";

    /** Features provider. */
    private final Features<Trait> features;
    /** Types provided. */
    private final Map<Class<?>, Object> types;
    /** Trait to add. */
    private final Collection<Class<? extends Trait>> traitToAdd;
    /** Listeners. */
    private final Collection<ObjectGameListener> listeners;
    /** Media representation. */
    private final Media media;
    /** Configurer reference. */
    private final Configurer configurer;
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
        types = new HashMap<>();
        traitToAdd = new ArrayList<>();
        listeners = new HashSet<>(1);
        media = setup.getConfigFile();
        configurer = setup.getConfigurer();
        destroyed = false;
    }

    /**
     * Add a trait.
     * 
     * @param trait The trait to add.
     */
    public final void addTrait(Class<? extends Trait> trait)
    {
        traitToAdd.add(trait);
    }

    /**
     * Add a type.
     * 
     * @param type The type to add.
     */
    public final void addType(Object type)
    {
        types.put(type.getClass(), type);
    }

    /**
     * Prepare traits. Does nothing by default.
     */
    protected void prepareTraits()
    {
        // Nothing to do
    }

    /**
     * Create added traits.
     * 
     * @param setup The setup reference.
     * @param services The services reference.
     * @throws LionEngineException If error when creating instance.
     */
    void createTraits(Setup setup, Services services) throws LionEngineException
    {
        for (final Class<? extends Trait> trait : traitToAdd)
        {
            try
            {
                final Constructor<? extends Trait> constructor = trait.getConstructor(ObjectGame.class, Services.class);
                final Trait instance = constructor.newInstance(this, services);
                instance.prepare(services);
                features.add(instance);
            }
            catch (final ReflectiveOperationException exception)
            {
                throw new LionEngineException(exception, ERROR_CREATE_TRAIT, trait.getName());
            }
        }
        traitToAdd.clear();
        prepareTraits();
    }

    /**
     * Check if object has the following trait.
     * 
     * @param trait The trait to check (could be a {@link Trait} or a classic interface.
     * @return <code>true</code> if has trait, <code>false</code> else.
     */
    public final boolean hasTrait(Class<?> trait)
    {
        if (trait.isAssignableFrom(getClass()))
        {
            return true;
        }
        for (final Class<?> type : types.keySet())
        {
            if (trait.isAssignableFrom(type))
            {
                return true;
            }
        }
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
        if (trait.isAssignableFrom(getClass()))
        {
            return trait.cast(this);
        }
        for (final Map.Entry<Class<?>, Object> type : types.entrySet())
        {
            if (trait.isAssignableFrom(type.getKey()))
            {
                return trait.cast(type.getValue());
            }
        }
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
     * Get the media representing this object.
     * 
     * @return The media representing this object.
     */
    public final Media getMedia()
    {
        return media;
    }

    /**
     * Get the configurer reference.
     * 
     * @return The configurer reference.
     */
    public final Configurer getConfigurer()
    {
        return configurer;
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
