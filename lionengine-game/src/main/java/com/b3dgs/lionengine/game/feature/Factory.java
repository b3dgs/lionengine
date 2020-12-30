/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature;

import java.lang.reflect.Constructor;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilReflection;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.game.Feature;

/**
 * Performs a list of {@link Setup} considering their corresponding {@link Media} pointing to an XML file. This way it
 * is possible to create new featurable instances related to their {@link Setup} by sharing the same resources.
 * <p>
 * Any featurable created by the factory from a {@link Media} must have a public constructor with a single argument
 * typed
 * (or sub type) of {@link Setup}.
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
    /** Constructor setup index. */
    private static final int SETUP_INDEX = 1;
    /** Setup class error. */
    private static final String ERROR_SETUP_CLASS = "Setup class not found !";
    /** Construction error. */
    static final String ERROR_CONSTRUCTOR_MISSING = "No recognized constructor found for: ";

    /** Setups list. */
    private final Map<Media, Setup> setups = new HashMap<>();
    /** Cached instances. */
    private final Map<Media, Deque<Featurable>> cache = new HashMap<>();
    /** Services reference. */
    private final Services services;
    /** Class loader. */
    private final ClassLoader classLoader;

    /**
     * Create a factory.
     * 
     * @param services The services reference.
     */
    public Factory(Services services)
    {
        super();

        this.services = services;
        classLoader = services.getOptional(ClassLoader.class).orElse(getClass().getClassLoader());
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
        final Class<O> type = setup.getConfigClass(classLoader);
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
        final Setup setup = getSetup(media, true);
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
     * Create cached medias from folder.
     * 
     * @param spawner The spawner reference.
     * @param folder The root folder.
     * @param count The caches number.
     */
    public void createCache(Spawner spawner, Media folder, int count)
    {
        for (final Media media : folder.getMedias())
        {
            if (media.getName().endsWith(FILE_DATA_DOT_EXTENSION))
            {
                final Collection<Featurable> cached = new ArrayList<>();
                for (int i = 0; i < count; i++)
                {
                    cached.add(spawner.spawn(media, 0, 0));
                }
                for (final Featurable featurable : cached)
                {
                    featurable.getFeature(Identifiable.class).destroy();
                    notifyHandlableRemoved(featurable);
                }
                cached.clear();
            }
        }
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
        return getSetup(media, false);
    }

    /**
     * Get a setup reference from its media.
     * 
     * @param media The setup media.
     * @param standard <code>true</code> for standard setup, <code>false</code> else.
     * @return The setup reference.
     * @throws LionEngineException If no setup found for the media.
     */
    private Setup getSetup(Media media, boolean standard)
    {
        Check.notNull(media);
        if (!setups.containsKey(media))
        {
            setups.put(media, createSetup(media, standard));
        }
        return setups.get(media);
    }

    /**
     * Create a setup from its media.
     * 
     * @param media The media reference.
     * @param standard <code>true</code> for standard setup, <code>false</code> else.
     * @return The setup instance.
     */
    @SuppressWarnings("unchecked")
    private Setup createSetup(Media media, boolean standard)
    {
        final Configurer configurer = new Configurer(media);
        try
        {
            final FeaturableConfig config = FeaturableConfig.imports(configurer);
            final String setup = config.getSetupName();
            final Class<? extends Setup> setupClass;
            if (standard)
            {
                setupClass = Setup.class;
            }
            else if (setup.isEmpty())
            {
                final Class<?> clazz = classLoader.loadClass(config.getClassName());
                final Constructor<?> constructor = UtilReflection.getCompatibleConstructorParent(clazz, new Class<?>[]
                {
                    Services.class, Setup.class
                });
                setupClass = (Class<? extends Setup>) constructor.getParameterTypes()[SETUP_INDEX];
            }
            else
            {
                setupClass = (Class<? extends Setup>) classLoader.loadClass(config.getSetupName());
            }
            return UtilReflection.create(setupClass, new Class<?>[]
            {
                Media.class
            }, media);
        }
        catch (final ClassNotFoundException exception)
        {
            throw new LionEngineException(exception, media, ERROR_SETUP_CLASS);
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
    private <O extends Featurable> O createFeaturable(Class<O> type, Setup setup) throws NoSuchMethodException
    {
        final O featurable = UtilReflection.createReduce(type, services, setup);
        if (featurable.isLoadFeaturesEnabled())
        {
            addFeatures(featurable, services, setup);
        }
        for (final Feature feature : featurable.getFeatures())
        {
            featurable.checkListener(feature);
            for (final Feature other : featurable.getFeatures())
            {
                if (feature != other)
                {
                    other.checkListener(feature);
                }
            }
        }
        return featurable;
    }

    /**
     * Add all features declared in configuration.
     * 
     * @param featurable The featurable to handle.
     * @param services The services reference.
     * @param setup The setup reference.
     */
    private void addFeatures(Featurable featurable, Services services, Setup setup)
    {
        final List<Feature> rawFeatures = FeaturableConfig.getFeatures(classLoader, services, setup);
        final int length = rawFeatures.size();
        for (int i = 0; i < length; i++)
        {
            final Feature feature = rawFeatures.get(i);
            featurable.addFeature(feature);
        }
        featurable.addAfter(services, setup);
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
                cache.put(media, new ArrayDeque<>());
            }
            cache.get(media).offer(featurable);
        }
    }
}
