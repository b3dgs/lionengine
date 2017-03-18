/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.lang.reflect.Constructor;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.FeaturableConfig;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.util.UtilReflection;

/**
 * Performs a list of {@link Setup} considering their corresponding {@link Media} pointing to an XML file. This way it
 * is possible to create new featurable instances related to their {@link Setup} by sharing the same resources.
 * <p>
 * Any featurable created by the factory from a {@link Media} must have a public constructor with a single argument
 * typed
 * (or sub type) of {@link Setup}.
 * </p>
 * <p>
 * The factory uses the {@link ClassLoader#getSystemClassLoader()}, but it is possible to set a custom one with
 * {@link #setClassLoader(ClassLoader)}. Should be used in an OSGI environment for example.
 * </p>
 * <p>
 * Destroyed {@link Featurable} can be cached to avoid {@link Featurable} creation if has {@link Recycler} and
 * {@link Recyclable} {@link Feature}s.
 * </p>
 */
public class Factory implements HandlerListener
{
    /** Data file extension. */
    public static final String FILE_DATA_EXTENSION = "xml";
    /** Data file extension with dot as prefix. */
    public static final String FILE_DATA_DOT_EXTENSION = Constant.DOT + FILE_DATA_EXTENSION;
    /** Setup class error. */
    private static final String ERROR_SETUP_CLASS = "Setup class not found !";
    /** Construction error. */
    private static final String ERROR_CONSTRUCTOR_MISSING = "No recognized constructor found for: ";

    /** Setups list. */
    private final Map<Media, Setup> setups = new HashMap<Media, Setup>();
    /** Cached instances. */
    private final Map<Media, Deque<Featurable>> cache = new HashMap<Media, Deque<Featurable>>();
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
        super();

        this.services = services;
        classLoader = getClass().getClassLoader();
    }

    /**
     * Create a {@link Featurable} from its {@link Media} using a generic way. The concerned class to instantiate and
     * its constructor must be public, and can have the following parameter: ({@link Setup}).
     * <p>
     * Automatically add {@link IdentifiableModel} if feature does not have {@link Identifiable} feature.
     * </p>
     * <p>
     * Destroyed {@link Featurable} can be cached to avoid {@link Featurable} creation if has {@link Recycler} and
     * {@link Recyclable} {@link Feature}s. If cache associated to media is available, it is
     * {@link Recyclable#recycle()} and then returned.
     * </p>
     * 
     * @param <O> The featurable type.
     * @param media The featurable media.
     * @return The featurable instance.
     * @throws LionEngineException If {@link Media} is <code>null</code> or {@link Setup} not found.
     */
    @SuppressWarnings("unchecked")
    public <O extends Featurable> O create(Media media)
    {
        if (cache.containsKey(media) && !cache.get(media).isEmpty())
        {
            final Featurable featurable = cache.get(media).poll();
            featurable.getFeature(Recycler.class).recycle();
            return (O) featurable;
        }
        final Setup setup = getSetup(media);
        final Class<?> type = setup.getConfigClass(classLoader);
        try
        {
            return createFeaturable(type, setup);
        }
        catch (final NoSuchMethodException exception)
        {
            throw new LionEngineException(exception, ERROR_CONSTRUCTOR_MISSING + media);
        }
    }

    /**
     * Create a featurable from its {@link Media} using a generic way. The concerned class to instantiate and its
     * constructor must be public, and can have the following parameter: ({@link Setup}).
     * <p>
     * Automatically add {@link IdentifiableModel} if feature does not have {@link Identifiable} feature.
     * </p>
     * <p>
     * Destroyed {@link Featurable} can be cached to avoid {@link Featurable} creation if has {@link Recycler} and
     * {@link Recyclable} {@link Feature}s. If cache associated to media is available, it is
     * {@link Recyclable#recycle()} and then returned.
     * </p>
     * 
     * @param <O> The featurable type.
     * @param media The featurable media.
     * @param type The specific class to use (override the one in the media).
     * @return The featurable instance.
     * @throws LionEngineException If {@link Media} is <code>null</code> or {@link Setup} not found.
     */
    @SuppressWarnings("unchecked")
    public <O extends Featurable> O create(Media media, Class<O> type)
    {
        if (cache.containsKey(media) && !cache.get(media).isEmpty())
        {
            final Featurable featurable = cache.get(media).poll();
            featurable.getFeature(Recycler.class).recycle();
            return (O) featurable;
        }
        final Setup setup = getSetup(media);
        try
        {
            return createFeaturable(type, setup);
        }
        catch (final NoSuchMethodException exception)
        {
            throw new LionEngineException(exception, ERROR_CONSTRUCTOR_MISSING + media);
        }
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
            final FeaturableConfig config = FeaturableConfig.imports(configurer);
            final String setup = config.getSetupName();
            final Class<?> setupClass;
            if (setup.isEmpty())
            {
                final Class<?> clazz = classLoader.loadClass(config.getClassName());
                final Constructor<?> constructor = UtilReflection.getCompatibleConstructorParent(clazz, new Class<?>[]
                {
                    Services.class, Setup.class
                });
                setupClass = constructor.getParameterTypes()[1];
            }
            else
            {
                setupClass = classLoader.loadClass(config.getSetupName());
            }
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
     * Create the featurable.
     * 
     * @param <O> The featurable type.
     * @param type The featurable type.
     * @param setup The associated setup.
     * @return The featurable instance.
     * @throws NoSuchMethodException If missing constructor.
     */
    private <O extends Featurable> O createFeaturable(Class<?> type, Setup setup) throws NoSuchMethodException
    {
        final O featurable = UtilReflection.createReduce(type, services, setup);
        prepare(featurable);

        return featurable;
    }

    /**
     * Prepare the featurable.
     * 
     * @param featurable The featurable to prepare.
     */
    private void prepare(Featurable featurable)
    {
        for (final Feature feature : featurable.getFeatures())
        {
            featurable.checkListener(feature);
        }
    }

    /*
     * HandlerListener
     */

    @Override
    public void notifyHandlableAdded(Featurable featurable)
    {
        // Nothing to do
    }

    @Override
    public void notifyHandlableRemoved(Featurable featurable)
    {
        final Media media = featurable.getMedia();
        if (media != null && featurable.hasFeature(Recycler.class))
        {
            if (!cache.containsKey(media))
            {
                cache.put(media, new ArrayDeque<Featurable>());
            }
            cache.get(media).offer(featurable);
        }
    }
}
