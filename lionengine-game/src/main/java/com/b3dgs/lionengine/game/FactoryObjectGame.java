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
public abstract class FactoryObjectGame<T extends Enum<T> & ObjectType, S extends SetupGame, O extends ObjectGame>
        extends FactoryGame<T, S>
{
    /** Unknown entity error message. */
    public static final String UNKNOWN_TYPE_ERROR = "Unknown type: ";

    /** Entities folder. */
    private final String folder;

    /**
     * Constructor.
     * 
     * @param keyType The class of the enum type defined.
     * @param folder The objects folder.
     */
    public FactoryObjectGame(Class<T> keyType, String folder)
    {
        super(keyType);
        this.folder = folder;
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
     * Create an item from its type using a generic way. The concerned classes to instantiate and their
     * constructors must be public (at least the target one).
     * 
     * @param type The object type.
     * @return The object instance.
     */
    public <E extends O> E create(T type)
    {
        final Class<?> objectClass = type.getTargetClass();
        E instance = null;
        for (final Constructor<?> constructor : objectClass.getConstructors())
        {
            try
            {
                instance = (E) constructor.newInstance(getSetup(type));
            }
            catch (final InvocationTargetException exception)
            {
                throw new LionEngineException(exception.getCause(), "Unable to create the following type: "
                        + type.getTargetClass());
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
                    + " and its constructor(s) must exist and be public !"), "Unable to create the following type: "
                    + type.getTargetClass());
        }
        return instance;
    }

    /*
     * FactoryGame
     */

    @Override
    protected S createSetup(T type)
    {
        final Media config = Media.get(folder, type.getPathName() + ".xml");
        return createSetup(type, config);
    }
}
