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
package com.b3dgs.lionengine.game.object;

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.game.Configurer;

/**
 * Performs a list of {@link Setup} considering their corresponding {@link Media} pointing to an XML file. This way it
 * is possible to create new object instances related to their {@link Setup} by sharing the same resources.
 * <p>
 * Any object created by the factory from a {@link Media} must have the following public constructor:
 * </p>
 * <ul>
 * <li>{@link ObjectGame#ObjectGame(Setup, Services)}</li>
 * </ul>
 * <p>
 * The factory uses the {@link ClassLoader#getSystemClassLoader()}, but it is possible to set a custom one with
 * {@link #setClassLoader(ClassLoader)}. Should be used in an OSGI environment for example.
 * </p>
 */
public class Factory
{
    /** Data file extension. */
    public static final String FILE_DATA_EXTENSION = "xml";
    /** Setup class error. */
    private static final String ERROR_SETUP_CLASS = "Setup class not found !";
    /** Construction error. */
    private static final String ERROR_CONSTRUCTOR_MISSING = "No recognized constructor found for: ";

    /** Setups list. */
    private final Map<Media, Setup> setups = new HashMap<Media, Setup>();
    /** Services reference. */
    private final Services services;
    /** Class loader. */
    private ClassLoader classLoader;

    /**
     * Create a factory.
     * 
     * @param services The services reference.
     */
    public Factory(Services services)
    {
        this.services = services;
        classLoader = ClassLoader.getSystemClassLoader();
    }

    /**
     * Create an object from its {@link Media} using a generic way. The concerned classes to instantiate and its
     * constructor must be public, and must have the following parameters: ({@link Setup}, {@link Services}).
     * 
     * @param <O> The object type.
     * @param media The object media.
     * @return The object instance.
     * @throws LionEngineException If {@link Media} is <code>null</code>, {@link Setup} not found, or {@link Services}
     *             missing service.
     * @see ObjectGame#ObjectGame(Setup, Services)
     */
    public <O extends ObjectGame> O create(Media media)
    {
        final Setup setup = getSetup(media);
        final Class<?> type = setup.getConfigClass(classLoader);
        try
        {
            return createObject(type, setup);
        }
        catch (final NoSuchMethodException exception)
        {
            throw new LionEngineException(exception, ERROR_CONSTRUCTOR_MISSING + media);
        }
    }

    /**
     * Create an object from its {@link Media} using a generic way. The concerned classes to instantiate and its
     * constructor must be public, and must have the following parameters: ({@link Setup}, {@link Services}).
     * 
     * @param <O> The object type.
     * @param media The object media.
     * @param type The specific class to use (override the one in the media).
     * @return The object instance.
     * @throws LionEngineException If {@link Media} is <code>null</code>, {@link Setup} not found, or {@link Services}
     *             missing service.
     * @see ObjectGame#ObjectGame(Setup, Services)
     */
    public <O extends ObjectGame> O create(Media media, Class<O> type)
    {
        final Setup setup = getSetup(media);
        try
        {
            return createObject(type, setup);
        }
        catch (final NoSuchMethodException exception)
        {
            throw new LionEngineException(exception, ERROR_CONSTRUCTOR_MISSING + media);
        }
    }

    /**
     * Clear all loaded setup and their configuration.
     */
    public void clear()
    {
        for (final Setup setup : setups.values())
        {
            setup.clear();
        }
        setups.clear();
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
     * Get a setup reference from its media.
     * 
     * @param media The setup media.
     * @return The setup reference.
     * @throws LionEngineException If no setup found for the media.
     */
    public Setup getSetup(Media media)
    {
        Check.notNull(media);
        if (!setups.containsKey(media))
        {
            setups.put(media, createSetup(media));
        }
        return setups.get(media);
    }

    /**
     * Create a setup from its media.
     * 
     * @param media The media reference.
     * @return The setup instance.
     */
    private Setup createSetup(Media media)
    {
        final Configurer configurer = new Configurer(media);
        try
        {
            final ObjectConfig configObject = ObjectConfig.create(configurer);
            final Class<?> setupClass = classLoader.loadClass(configObject.getSetupName());
            return UtilReflection.create(setupClass, new Class<?>[]
            {
                Media.class
            }, media);
        }
        catch (final ClassNotFoundException exception)
        {
            throw new LionEngineException(exception, ERROR_SETUP_CLASS);
        }
        catch (final NoSuchMethodException exception)
        {
            throw new LionEngineException(exception, ERROR_CONSTRUCTOR_MISSING + media.getPath());
        }
    }

    /**
     * Create the object.
     * 
     * @param <O> The object type.
     * @param type The object type.
     * @param setup The associated setup.
     * @return The object instance.
     * @throws NoSuchMethodException If missing method.
     */
    private <O extends ObjectGame> O createObject(Class<?> type, Setup setup) throws NoSuchMethodException
    {
        final O object = UtilReflection.create(type, new Class<?>[]
        {
            setup.getClass(), Services.class
        }, setup, services);

        object.prepareTraits(services);

        return object;
    }
}
