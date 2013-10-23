/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;

/**
 * Game type factory. It performs a list of available types from a directory considering an input enumeration.
 * Data are stored with an enumeration as key.
 * <p>
 * Sample implementation:
 * </p>
 * 
 * <pre>
 * public class Factory
 *         extends FactoryObjectGame&lt;EntityType, SetupGame, ObjectGame&gt;
 * {
 *     public Factory()
 *     {
 *         super(EntityType.class, &quot;objects&quot;);
 *         load();
 *     }
 * 
 *     &#064;Override
 *     protected SetupGame createSetup(EntityType type, Media config)
 *     {
 *         return new SetupGame(config);
 *     }
 * }
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @param <T> The enum containing all types.
 * @param <S> The setup object type used.
 * @param <O> The object type used.
 */
public abstract class FactoryObjectGame<T extends Enum<T> & ObjectType, S extends SetupGame, O>
{
    /** Unknown entity error message. */
    public static final String UNKNOWN_TYPE_ERROR = "Unknown type: ";

    /** Full name. */
    private final String name;
    /** Types list. */
    private final T[] types;
    /** Setups list. */
    private final Map<T, S> setups;
    /** Entities folder. */
    private final String folder;
    /** Arguments. */
    private Object[] arguments;

    /**
     * Constructor.
     * 
     * @param keyType The class of the enum type defined.
     * @param folder The objects folder.
     */
    public FactoryObjectGame(Class<T> keyType, String folder)
    {
        this.types = keyType.getEnumConstants();
        this.folder = folder;
        setups = new EnumMap<>(keyType);
        arguments = new Object[0];
        name = getClass().getPackage().getName() + ".";
    }

    /**
     * Create a setup from its media.
     * 
     * @param type The effect type.
     * @param config The setup media config file.
     * @return The setup instance.
     */
    protected abstract S createSetup(T type, Media config);

    /**
     * Load setup for each type.
     */
    public void load()
    {
        for (final T type : types)
        {
            addSetup(type, createSetup(type));
        }
    }

    /**
     * Set the object constructor additional arguments.
     * 
     * @param arguments The additional arguments.
     */
    public void setArguments(Object... arguments)
    {
        this.arguments = arguments;
    }

    /**
     * Create an item from its type using a generic way. The concerned classes to instantiate and their
     * constructors must be public (at least the target one).
     * 
     * @param type The object type.
     * @return The object instance.
     */
    public <E extends O> E create(T type)
    {
        try
        {
            final Class<?> objectClass = Class.forName(name.concat(type.asClassName()));
            final LinkedList<Object> finalArguments = new LinkedList<>(Arrays.asList(arguments));
            finalArguments.addFirst(getSetup(type));
            E instance = null;
            for (final Constructor<?> constructor : objectClass.getConstructors())
            {
                try
                {
                    instance = (E) constructor.newInstance(finalArguments.toArray());
                    finalArguments.clear();
                }
                catch (final InvocationTargetException exception)
                {
                    throw new LionEngineException(exception.getCause(), "Unable to create the following type: "
                            + type.asClassName());
                }
                catch (InstantiationException
                       | IllegalArgumentException
                       | IllegalAccessException exception)
                {
                    instance = null;
                }
            }
            if (instance == null)
            {
                throw new LionEngineException(new InstantiationException("Class " + objectClass.getName()
                        + " and its constructor(s) must exist and be public !"),
                        "Unable to create the following type: " + type.asClassName());
            }
            return instance;
        }
        catch (final ClassNotFoundException exception)
        {
            throw new LionEngineException(exception, FactoryObjectGame.UNKNOWN_TYPE_ERROR + type.asClassName());
        }
    }

    /**
     * Add a setup reference at the specified type.
     * 
     * @param type The reference type.
     * @param setup The setup reference.
     */
    public void addSetup(T type, S setup)
    {
        setups.put(type, setup);
    }

    /**
     * Get a setup reference from its type.
     * 
     * @param type The reference type.
     * @return The setup reference.
     */
    public S getSetup(T type)
    {
        return setups.get(type);
    }

    /**
     * Get the types list.
     * 
     * @return The types list.
     */
    public T[] getTypes()
    {
        return types;
    }

    /**
     * Get setup instance.
     * 
     * @param type The object type.
     * @return The setup instance.
     */
    protected S createSetup(T type)
    {
        final Media config = Media.get(folder, type.asPathName() + ".xml");
        return createSetup(type, config);
    }
}
