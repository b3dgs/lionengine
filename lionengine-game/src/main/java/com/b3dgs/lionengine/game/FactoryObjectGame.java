/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Media;

/**
 * It performs a list of {@link SetupGame} considering an input class type. This way it is possible to create new
 * instances of {@link ObjectGame} related to their {@link Class} by sharing the same data.
 * <p>
 * Sample implementation:
 * </p>
 * 
 * <pre>
 * public class FactoryObject
 *         extends FactoryObjectGame&lt;SetupGame, ObjectGame&gt;
 * {
 *     public FactoryObject()
 *     {
 *         super(&quot;objects&quot;);
 *     }
 * 
 *     &#064;Override
 *     protected SetupGame createSetup(Class&lt;? extends ObjectGame&gt; type, Media config)
 *     {
 *         return new SetupGame(config);
 *     }
 * }
 * </pre>
 * 
 * @param <S> The setup type used.
 * @param <O> The object type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class FactoryObjectGame<S extends SetupGame, O extends ObjectGame>
        extends FactoryGame<S, O>
{
    /** Folder error. */
    private static final String ERROR_FOLDER = "Folder must not be null !";
    /** Type error. */
    private static final String ERROR_TYPE = "Type must not be null !";
    /** Setup not found error. */
    private static final String ERROR_SETUP = "Setup not found fhe following type: ";
    /** Constructor error. */
    private static final String ERROR_CONSTRUCTOR = "Unable to create the following type: ";
    /** Constructor not found. */
    private static final String ERROR_CONSTRUCTOR_NOT_FOUND = " must have a public constructor\n"
            + "\t\twith a single argument compatible with " + SetupGame.class + " !";
    /** Data file extension. */
    private static final String FILE_DATA_EXTENSION = ".xml";

    /** Objects folder. */
    protected final String folder;

    /**
     * Constructor.
     * 
     * @param folder The objects folder.
     */
    public FactoryObjectGame(String folder)
    {
        super();
        Check.notNull(folder, FactoryObjectGame.ERROR_FOLDER);
        this.folder = folder;
    }

    /**
     * Create a setup from its media.
     * 
     * @param type The object type.
     * @param config The setup media config file.
     * @return The setup instance.
     */
    protected abstract S createSetup(Class<? extends O> type, Media config);

    /**
     * Create an object from its type using a generic way. The concerned classes to instantiate and its
     * constructor must be public, and must be as the main one: {@link ObjectGame#ObjectGame(SetupGame)}.
     * 
     * @param <E> The object type used.
     * @param type The object type.
     * @return The object instance.
     */
    public <E extends O> E create(Class<E> type)
    {
        Check.notNull(type, FactoryObjectGame.ERROR_TYPE);

        final S setup = getSetup(type);
        Check.notNull(setup, FactoryObjectGame.ERROR_SETUP, type.getName());

        E instance = null;
        for (final Constructor<?> constructor : type.getConstructors())
        {
            try
            {
                instance = type.cast(constructor.newInstance(setup));
            }
            catch (final InvocationTargetException exception)
            {
                throw new LionEngineException(exception, FactoryObjectGame.ERROR_CONSTRUCTOR + type);
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
            throw new LionEngineException(new InstantiationException(type
                    + FactoryObjectGame.ERROR_CONSTRUCTOR_NOT_FOUND), FactoryObjectGame.ERROR_CONSTRUCTOR + type);
        }
        return instance;
    }

    /*
     * FactoryGame
     */

    @Override
    protected S createSetup(Class<? extends O> type)
    {
        Check.notNull(type, FactoryObjectGame.ERROR_TYPE);

        final Media config = Core.MEDIA.create(folder, type.getSimpleName() + FactoryObjectGame.FILE_DATA_EXTENSION);
        return createSetup(type, config);
    }
}
