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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Feature;

/**
 * Features handler representation. Store features by type (must be annotated by {@link FeatureInterface}), allowing
 * quick access from an interface.
 */
public class Features
{
    /** Feature not annotated. */
    static final String ERROR_FEATURE_NOT_ANNOTATED = "Feature not annotated: ";
    /** Feature not found error. */
    static final String ERROR_FEATURE_NOT_FOUND = "Feature not found: ";
    /** Feature exists error. */
    static final String ERROR_FEATURE_EXISTS = "Feature already exists: ";
    /** Feature exists as error. */
    static final String AS = " as: ";
    /** Feature exists with error. */
    static final String WITH = " with: ";

    /**
     * Check if feature is annotated in its direct parents.
     * 
     * @param feature The feature to check.
     * @return <code>true</code> if annotated, <code>false</code> else.
     */
    private static boolean isAnnotated(Feature feature)
    {
        for (final Class<?> current : feature.getClass().getInterfaces())
        {
            if (current.isAnnotationPresent(FeatureInterface.class))
            {
                return true;
            }
        }
        return feature.getClass().isAnnotationPresent(FeatureInterface.class);
    }

    /** Features handled. */
    private final Map<Class<? extends Feature>, Feature> typeToFeature = new HashMap<>();
    /** Unique features. */
    private final Collection<Feature> features = new ArrayList<>();

    /**
     * Create features handler.
     */
    public Features()
    {
        super();
    }

    /**
     * Add a feature. Stores its interface, and all sub interfaces which describe also a {@link Feature} annotated by
     * {@link FeatureInterface}.
     * 
     * @param feature The feature to add.
     * @throws LionEngineException If feature is not annotated by {@link FeatureInterface} or already referenced.
     */
    public void add(Feature feature)
    {
        if (!isAnnotated(feature))
        {
            throw new LionEngineException(ERROR_FEATURE_NOT_ANNOTATED + feature.getClass());
        }
        final Feature old;
        // CHECKSTYLE IGNORE LINE: InnerAssignment
        if ((old = typeToFeature.put(feature.getClass(), feature)) != null)
        {
            throw new LionEngineException(ERROR_FEATURE_EXISTS + feature.getClass() + WITH + old.getClass());
        }
        checkTypeDepth(feature, feature.getClass());
        features.add(feature);
    }

    /**
     * Get a feature from its class or interface.
     * 
     * @param <C> The custom feature type.
     * @param feature The feature class or interface.
     * @return The feature instance.
     * @throws LionEngineException If the feature was not found.
     */
    public <C extends Feature> C get(Class<C> feature)
    {
        final Feature found = typeToFeature.get(feature);
        if (found != null)
        {
            return feature.cast(found);
        }
        throw new LionEngineException(ERROR_FEATURE_NOT_FOUND + feature.getName());
    }

    /**
     * Check if contains the following feature type.
     * 
     * @param <C> The custom feature type.
     * @param feature The feature to check.
     * @return <code>true</code> if contains, <code>false</code> else.
     */
    public <C extends Feature> boolean contains(Class<C> feature)
    {
        return typeToFeature.containsKey(feature);
    }

    /**
     * Get all features.
     * 
     * @return The features list.
     */
    public Iterable<Feature> getFeatures()
    {
        return features;
    }

    /**
     * Get all features types.
     * 
     * @return The features types.
     */
    public Iterable<Class<? extends Feature>> getFeaturesType()
    {
        return typeToFeature.keySet();
    }

    /**
     * Check annotated features parent recursively.
     * 
     * @param feature The main feature.
     * @param current The current parent.
     */
    private void checkTypeDepth(Feature feature, Class<?> current)
    {
        for (final Class<?> type : current.getInterfaces())
        {
            if (type.isAnnotationPresent(FeatureInterface.class))
            {
                checkAnnotation(feature, type);
                checkTypeDepth(feature, type);
            }
        }
        final Class<?> parent = current.getSuperclass();
        if (parent != null)
        {
            if (parent.isAnnotationPresent(FeatureInterface.class))
            {
                checkAnnotation(feature, parent);
            }
            checkTypeDepth(feature, parent);
        }
    }

    /**
     * Check annotation and update mapping.
     * 
     * @param feature The feature to check.
     * @param type The type to check.
     */
    private void checkAnnotation(Feature feature, Class<?> type)
    {
        final Feature old;
        // CHECKSTYLE IGNORE LINE: InnerAssignment
        if ((old = typeToFeature.put(type.asSubclass(Feature.class), feature)) != null)
        {
            throw new LionEngineException(ERROR_FEATURE_EXISTS
                                          + feature.getClass()
                                          + AS
                                          + type
                                          + WITH
                                          + old.getClass());
        }
    }
}
