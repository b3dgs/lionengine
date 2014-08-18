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
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.game.purview.Fabricable;

/**
 * It performs a list of {@link SetupGame} considering their corresponding media. This way it is possible to create new
 * instances of {@link Fabricable} related to their {@link Media} by sharing the same resources.
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
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class FactoryObjectGame<S extends SetupGame>
        extends FactoryGame<S>
{
    /** Data file extension. */
    public static final String FILE_DATA_EXTENSION = "xml";
    /** Folder error. */
    private static final String ERROR_FOLDER = "Folder must not be null !";
    /** Type error. */
    private static final String ERROR_MEDIA = "Media must not be null !";
    /** Setup not found error. */
    private static final String ERROR_SETUP = "Setup not found fhe following type: ";
    /** Constructor error. */
    private static final String ERROR_CONSTRUCTOR = "Unable to create the following type: ";
    /** Constructor not found. */
    private static final String ERROR_CONSTRUCTOR_NOT_FOUND = " must have a public constructor\n"
            + "\t\twith a single argument compatible with " + SetupGame.class + " !";

    /** Objects folder. */
    protected final String folder;
    /** Context reference. */
    private ContextGame context;
    /** External class loader (<code>null</code> if none). */
    private ClassLoader classLoader;

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
        classLoader = ClassLoader.getSystemClassLoader();
    }

    /**
     * Create an object from its type using a generic way. The concerned classes to instantiate and its
     * constructor must be public, and must be as the main one: {@link ObjectGame#ObjectGame(SetupGame)}.
     * 
     * @param media The object media.
     * @return The object instance.
     */
    public <E extends Fabricable> E create(Media media)
    {
        Check.notNull(media, FactoryObjectGame.ERROR_MEDIA);

        final S setup = getSetup(media);
        Check.notNull(setup, FactoryObjectGame.ERROR_SETUP, media.getPath());

        final E fabricable = create(setup);
        if (context == null)
        {
            fabricable.prepare(new ContextGame());
        }
        else
        {
            fabricable.prepare(context);
        }
        return fabricable;
    }

    /**
     * Set the context reference.
     * 
     * @param context The context reference.
     */
    public void setContext(ContextGame context)
    {
        this.context = context;
    }

    /**
     * Set an external class loader.
     * 
     * @param classLoader The external class loader.
     */
    public void setClassLoader(ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    /**
     * Get the main folder where are stored objects properties.
     * 
     * @return The main objects folder.
     */
    public String getFolder()
    {
        return folder;
    }

    /**
     * Create an object corresponding to the setup.
     * 
     * @param setup The setup reference.
     * @return The fabricable instance.
     */
    @SuppressWarnings("unchecked")
    private <E extends Fabricable> E create(S setup)
    {
        final Class<?> type = setup.getConfigClass(classLoader);
        try
        {
            final Constructor<?> constructor = type.getConstructor(setup.getClass());
            return (E) constructor.newInstance(setup);
        }
        catch (final InvocationTargetException
                     | ClassCastException exception)
        {
            throw new LionEngineException(exception, FactoryObjectGame.ERROR_CONSTRUCTOR + type);
        }
        catch (NoSuchMethodException
               | InstantiationException
               | IllegalArgumentException
               | IllegalAccessException exception)
        {
            throw new LionEngineException(new InstantiationException(type
                    + FactoryObjectGame.ERROR_CONSTRUCTOR_NOT_FOUND), FactoryObjectGame.ERROR_CONSTRUCTOR + type);
        }
    }
}
