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
package com.b3dgs.lionengine.game.feature;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.lionengine.util.UtilReflection;

/**
 * Featurable model implementation.
 */
public class FeaturableModel implements Featurable
{
    /** Class constructor error. */
    private static final String ERROR_CLASS_CONSTRUCTOR = "Class constructor error: ";
    /** Class not found error. */
    private static final String ERROR_CLASS_PRESENCE = "Class not found: ";
    /** Inject service error. */
    private static final String ERROR_INJECT = "Error during service injection !";
    /** Class loader. */
    private static final ClassLoader LOADER = Configurer.class.getClassLoader();
    /** Class cache. */
    private static final Map<String, Constructor<?>> CLASS_CACHE = new HashMap<String, Constructor<?>>();
    /** Constructor with setup (<code>true</code> of default constructor (<code>false</code>). */
    private static final Map<String, Boolean> CONSTRUCTOR_TYPE = new HashMap<String, Boolean>();

    /**
     * Clear classes cache.
     */
    public static void clearCache()
    {
        CLASS_CACHE.clear();
        CONSTRUCTOR_TYPE.clear();
    }

    /**
     * Get all available features.
     * Default constructor of each feature must be available or with {@link Setup} as single parameter.
     * 
     * @param setup The setup reference.
     * @return The available features.
     * @throws LionEngineException If invalid class.
     */
    private static List<Feature> getFeatures(Setup setup)
    {
        final List<Feature> features = new ArrayList<Feature>();
        for (final XmlNode featureNode : setup.getRoot().getChildren(FeaturableConfig.NODE_FEATURE))
        {
            final String className = featureNode.getText();
            final Constructor<?> constructor = getConstructor(setup, className);
            final Feature feature;
            try
            {
                if (CONSTRUCTOR_TYPE.get(className).booleanValue())
                {
                    feature = (Feature) constructor.newInstance(setup);
                }
                else
                {
                    feature = (Feature) constructor.newInstance();
                }
            }
            catch (final InvocationTargetException exception)
            {
                throw new LionEngineException(exception);
            }
            catch (final IllegalAccessException exception)
            {
                throw new LionEngineException(exception);
            }
            catch (final InstantiationException exception)
            {
                throw new LionEngineException(exception);
            }
            features.add(feature);
        }
        return features;
    }

    /**
     * Get the class implementation from its name by using a custom constructor.
     * 
     * @param setup The setup reference.
     * @param className The class name.
     * @return The typed class instance.
     * @throws LionEngineException If invalid class.
     */
    private static Constructor<?> getConstructor(Setup setup, String className)
    {
        if (CLASS_CACHE.containsKey(className))
        {
            return CLASS_CACHE.get(className);
        }
        try
        {
            final Class<?> clazz = LOADER.loadClass(className);
            Constructor<?> constructor;
            try
            {
                constructor = UtilReflection.getCompatibleConstructor(clazz, UtilReflection.getParamTypes(setup));
                CONSTRUCTOR_TYPE.put(className, Boolean.TRUE);
            }
            catch (@SuppressWarnings("unused") final NoSuchMethodException exception)
            {
                constructor = clazz.getDeclaredConstructor();
                CONSTRUCTOR_TYPE.put(className, Boolean.FALSE);
            }
            if (!constructor.isAccessible())
            {
                UtilReflection.setAccessible(constructor, true);
            }
            CLASS_CACHE.put(className, constructor);
            return constructor;
        }
        catch (final NoSuchMethodException exception)
        {
            throw new LionEngineException(exception, ERROR_CLASS_CONSTRUCTOR, className);
        }
        catch (final ClassNotFoundException exception)
        {
            throw new LionEngineException(exception, ERROR_CLASS_PRESENCE, className);
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
        final List<Field> toInject = new ArrayList<Field>();
        Class<?> clazz = object.getClass();
        while (clazz != null)
        {
            final Field[] fields = clazz.getDeclaredFields();
            final int length = fields.length;
            for (int i = 0; i < length; i++)
            {
                final Field field = fields[i];
                if (field.isAnnotationPresent(Service.class))
                {
                    toInject.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return toInject;
    }

    /** Features to prepare. */
    private final List<Feature> featuresToPrepare = new ArrayList<Feature>();
    /** Features provider. */
    private final Features features = new Features();
    /** Associated media (<code>null</code> if none). */
    private final Media media;
    /** Services filled. */
    private boolean filled;

    /**
     * Create model.
     */
    public FeaturableModel()
    {
        super();

        media = null;
    }

    /**
     * Create model. All features are loaded from setup.
     * 
     * @param setup The setup reference.
     */
    public FeaturableModel(Setup setup)
    {
        super();

        media = setup.getMedia();
        addFeatures(setup);
    }

    /**
     * Fill services fields with their right instance.
     * 
     * @param object The object to update.
     * @param services The services reference.
     * @throws LionEngineException If error on setting service.
     */
    private void fillServices(Object object, Services services)
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
            setField(field, object, services, type);
        }
    }

    /**
     * Set the field service only if currently <code>null</code>.
     * 
     * @param field The field to set.
     * @param object The object to update.
     * @param services The services reference.
     * @param type The service type.
     * @throws LionEngineException If error on setting service.
     */
    private void setField(Field field, Object object, Services services, Class<?> type)
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
                    field.set(object, services.get(type));
                }
            }
        }
        catch (final IllegalAccessException exception)
        {
            throw new LionEngineException(exception,
                                          ERROR_INJECT,
                                          type.getSimpleName(),
                                          Constant.SLASH,
                                          field.getName());
        }
    }

    /*
     * Featurable
     */

    @Override
    public void prepareFeatures(Services services)
    {
        if (!filled)
        {
            fillServices(this, services);
            filled = true;
        }
        final int length = featuresToPrepare.size();
        for (int i = 0; i < length; i++)
        {
            final Feature feature = featuresToPrepare.get(i);
            fillServices(feature, services);
            feature.prepare(this, services);

            for (int j = 0; j < length; j++)
            {
                if (i != j)
                {
                    featuresToPrepare.get(j).checkListener(feature);
                }
            }
        }
        featuresToPrepare.clear();
    }

    @Override
    public final void addFeature(Feature feature)
    {
        featuresToPrepare.add(feature);
        features.add(feature);
    }

    @Override
    public final void addFeatures(Setup setup)
    {
        final List<Feature> features = getFeatures(setup);
        final int length = features.size();
        for (int i = 0; i < length; i++)
        {
            final Feature feature = features.get(i);
            addFeature(feature);
        }
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
    public final boolean isPrepared()
    {
        return filled;
    }

    @Override
    public Media getMedia()
    {
        return media;
    }
}
