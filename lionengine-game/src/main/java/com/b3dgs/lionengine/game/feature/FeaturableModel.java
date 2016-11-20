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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.stream.XmlNode;
import com.b3dgs.lionengine.util.UtilReflection;

/**
 * Featurable model implementation.
 */
public class FeaturableModel implements Featurable
{
    /** Inject service error. */
    private static final String ERROR_INJECT = "Error during service injection !";

    /**
     * Get all available features.
     * Default constructor of each feature must be available or with {@link Setup} as single parameter.
     * 
     * @param setup The setup reference.
     * @return The available features.
     * @throws LionEngineException If invalid class.
     */
    private static Collection<Feature> getFeatures(Setup setup)
    {
        final Collection<Feature> features = new ArrayList<Feature>();
        for (final XmlNode featureNode : setup.getRoot().getChildren(FeaturableConfig.NODE_FEATURE))
        {
            final String className = featureNode.getText();
            Feature feature;
            try
            {
                feature = setup.getImplementation(Configurer.class.getClassLoader(),
                                                  Feature.class,
                                                  UtilReflection.getParamTypes(setup),
                                                  Arrays.asList(setup),
                                                  className);
            }
            catch (@SuppressWarnings("unused") final LionEngineException exception)
            {
                feature = setup.getImplementation(Configurer.class.getClassLoader(),
                                                  Feature.class,
                                                  new Class<?>[0],
                                                  Collections.emptyList(),
                                                  className);
            }
            features.add(feature);
        }
        return features;
    }

    /** Features to prepare. */
    private final Collection<Feature> featuresToPrepare = new ArrayList<Feature>();
    /** Features provider. */
    private final Features features = new Features();
    /** Services filled. */
    private boolean filled;

    /**
     * Create model.
     */
    public FeaturableModel()
    {
        super();
    }

    /**
     * Create model. All features are loaded from setup.
     * 
     * @param setup The setup reference.
     */
    public FeaturableModel(Setup setup)
    {
        super();

        addFeatures(setup);
    }

    /**
     * Fill services fields with their right instance.
     * 
     * @param object The object to update.
     * @param services The services reference.
     */
    private void fillServices(Object object, Services services)
    {
        for (final Field field : getServiceFields(object))
        {
            final boolean accessible = field.isAccessible();
            if (!accessible)
            {
                UtilReflection.setAccessible(field, true);
            }

            final Class<?> type = field.getType();
            setField(field, object, services, type);

            if (!accessible)
            {
                UtilReflection.setAccessible(field, false);
            }
        }
    }

    /**
     * Get all with that require an injected service.
     * 
     * @param object The object which requires injected services.
     * @return The field requiring injected services.
     */
    private Collection<Field> getServiceFields(Object object)
    {
        final Collection<Field> toInject = new HashSet<Field>();
        Class<?> clazz = object.getClass();
        while (clazz != null)
        {
            for (final Field field : clazz.getDeclaredFields())
            {
                if (field.isAnnotationPresent(Service.class))
                {
                    toInject.add(field);
                }
            }
            clazz = clazz.getSuperclass();
        }
        return toInject;
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
        for (final Feature feature : featuresToPrepare)
        {
            fillServices(feature, services);
            feature.prepare(this, services);

            for (final Feature current : featuresToPrepare)
            {
                if (feature != current)
                {
                    current.checkListener(feature);
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
    public void addFeatures(Setup setup)
    {
        for (final Feature feature : getFeatures(setup))
        {
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
}
