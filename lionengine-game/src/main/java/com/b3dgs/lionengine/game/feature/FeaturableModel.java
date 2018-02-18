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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Configurer;
import com.b3dgs.lionengine.io.Xml;
import com.b3dgs.lionengine.util.UtilReflection;

/**
 * Featurable model implementation.
 */
public class FeaturableModel implements Featurable
{
    /** Class not found error. */
    private static final String ERROR_CLASS_PRESENCE = "Class not found: ";
    /** Inject service error. */
    private static final String ERROR_INJECT = "Error during service injection !";
    /** Class loader. */
    private static final ClassLoader LOADER = Configurer.class.getClassLoader();
    /** Class cache. */
    private static final Map<String, Class<?>> CLASS_CACHE = new HashMap<>();

    /**
     * Clear classes cache.
     */
    public static void clearCache()
    {
        CLASS_CACHE.clear();
    }

    /**
     * Get all available features.
     * Default constructor of each feature must be available or with {@link Setup} as single parameter.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     * @return The available features.
     * @throws LionEngineException If invalid class.
     */
    private static List<Feature> getFeatures(Services services, Setup setup)
    {
        final Collection<Xml> children = setup.getRoot().getChildren(FeaturableConfig.NODE_FEATURE);
        final List<Feature> features = new ArrayList<>(children.size());
        for (final Xml featureNode : children)
        {
            final String className = featureNode.getText();
            final Feature feature;
            try
            {
                final Class<? extends Feature> clazz = getClass(className);
                feature = UtilReflection.createReduce(clazz, services, setup);
            }
            catch (final NoSuchMethodException exception)
            {
                throw new LionEngineException(exception);
            }
            features.add(feature);
        }
        return features;
    }

    /**
     * Get the class reference from its name using cache.
     * 
     * @param <T> The class type.
     * @param className The class name.
     * @return The typed class instance.
     * @throws LionEngineException If invalid class.
     */
    @SuppressWarnings("unchecked")
    private static <T> Class<T> getClass(String className)
    {
        if (CLASS_CACHE.containsKey(className))
        {
            return (Class<T>) CLASS_CACHE.get(className);
        }
        try
        {
            final Class<?> clazz = LOADER.loadClass(className);
            CLASS_CACHE.put(className, clazz);
            return (Class<T>) clazz;
        }
        catch (final ClassNotFoundException exception)
        {
            throw new LionEngineException(exception, ERROR_CLASS_PRESENCE + className);
        }
    }

    /**
     * Get all with that require an injected service.
     * 
     * @param object The object which requires injected services.
     * @return The field requiring injected services.
     */
    private static List<Field> getServiceFields(Object object)
    {
        final List<Field> toInject = new ArrayList<>();
        Class<?> clazz = object.getClass();
        while (clazz != null)
        {
            final Field[] fields = clazz.getDeclaredFields();
            final int length = fields.length;
            for (int i = 0; i < length; i++)
            {
                final Field field = fields[i];
                if (field.isAnnotationPresent(FeatureGet.class))
                {
                    toInject.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return toInject;
    }

    /** Features provider. */
    private final Features features = new Features();
    /** Associated media (<code>null</code> if none). */
    private final Media media;

    /**
     * Create model.
     */
    public FeaturableModel()
    {
        super();

        media = null;
        addFeature(new IdentifiableModel());
    }

    /**
     * Create model. All features are loaded from setup.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    public FeaturableModel(Services services, Setup setup)
    {
        super();

        media = setup.getMedia();
        addFeature(new IdentifiableModel());
        addFeatures(services, setup);
    }

    /**
     * Add all features declared in configuration.
     * 
     * @param services The services reference.
     * @param setup The setup reference.
     */
    private void addFeatures(Services services, Setup setup)
    {
        final List<Feature> features = getFeatures(services, setup);
        final int length = features.size();
        for (int i = 0; i < length; i++)
        {
            final Feature feature = features.get(i);
            addFeature(feature);
        }
    }

    /**
     * Fill services fields with their right instance.
     * 
     * @param object The object to update.
     * @throws LionEngineException If error on setting service.
     */
    private void fillServices(Object object)
    {
        final List<Field> fields = getServiceFields(object);
        final int length = fields.size();
        for (int i = 0; i < length; i++)
        {
            final Field field = fields.get(i);
            if (!field.isAccessible())
            {
                UtilReflection.setAccessible(field, true);
            }
            final Class<?> type = field.getType();
            setField(field, object, type);
        }
    }

    /**
     * Set the field service only if currently <code>null</code>.
     * 
     * @param field The field to set.
     * @param object The object to update.
     * @param type The service type.
     * @throws LionEngineException If error on setting service.
     */
    private void setField(Field field, Object object, Class<?> type)
    {
        try
        {
            if (field.get(object) == null)
            {
                final Class<? extends Feature> clazz;
                if (Feature.class.isAssignableFrom(type) && hasFeature(clazz = type.asSubclass(Feature.class)))
                {
                    field.set(object, getFeature(clazz));
                }
                else
                {
                    throw new LionEngineException(media, ERROR_CLASS_PRESENCE + String.valueOf(type));
                }
            }
        }
        catch (final IllegalAccessException exception)
        {
            throw new LionEngineException(exception,
                                          ERROR_INJECT + type.getSimpleName() + Constant.SLASH + field.getName());
        }
    }

    /*
     * Featurable
     */

    /**
     * {@inheritDoc}
     * <p>
     * Does nothing by default.
     * </p>
     */
    @Override
    public void checkListener(Object listener)
    {
        // Nothing by default
    }

    @Override
    public final void addFeature(Feature feature)
    {
        fillServices(feature);
        feature.prepare(this);
        features.add(feature);
    }

    @Override
    public final <T extends Feature> T addFeatureAndGet(T feature)
    {
        addFeature(feature);
        return feature;
    }

    @Override
    public final <C extends Feature> C getFeature(Class<C> feature)
    {
        return features.get(feature);
    }

    @Override
    public final Iterable<Feature> getFeatures()
    {
        return features.getFeatures();
    }

    @Override
    public final Iterable<Class<? extends Feature>> getFeaturesType()
    {
        return features.getFeaturesType();
    }

    @Override
    public final boolean hasFeature(Class<? extends Feature> feature)
    {
        return features.contains(feature);
    }

    @Override
    public Media getMedia()
    {
        return media;
    }
}
