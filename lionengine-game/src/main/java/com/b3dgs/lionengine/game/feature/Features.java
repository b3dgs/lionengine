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

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Feature representation.
 */
public class Features
{
    /** Feature not found error. */
    private static final String ERROR_FEATURE_NOT_FOUND = "Feature not found: ";

    /** Features provided. */
    private final Map<Class<? extends Feature>, Feature> features;

    /**
     * Create a feature handler.
     */
    public Features()
    {
        features = new HashMap<Class<? extends Feature>, Feature>();
    }

    /**
     * Add a trait.
     * 
     * @param feature The feature to add.
     */
    public final void add(Feature feature)
    {
        features.put(feature.getClass(), feature);
        for (final Class<?> type : feature.getClass().getInterfaces())
        {
            if (Feature.class.isAssignableFrom(type))
            {
                features.put(type.asSubclass(Feature.class), feature);
            }
        }
    }

    /**
     * Get a feature from its class.
     * 
     * @param <C> The custom feature type.
     * @param feature The feature class.
     * @return The feature instance.
     * @throws LionEngineException If the feature was not found.
     */
    public final <C extends Feature> C get(Class<C> feature)
    {
        final C found = getFeature(feature);
        if (found == null)
        {
            throw new LionEngineException(ERROR_FEATURE_NOT_FOUND, feature.getName());
        }
        return found;
    }

    /**
     * Check if contains the following feature.
     * 
     * @param <C> The custom feature type.
     * @param feature The feature to check.
     * @return <code>true</code> if contains, <code>false</code> else.
     */
    public final <C extends Feature> boolean contains(Class<C> feature)
    {
        return getFeature(feature) != null;
    }

    /**
     * Get all features.
     * 
     * @return The features list.
     */
    public final Iterable<Feature> getFeatures()
    {
        return features.values();
    }

    /**
     * Get all features types.
     * 
     * @return The features types.
     */
    public final Iterable<Class<? extends Feature>> getFeaturesType()
    {
        return features.keySet();
    }

    /**
     * Get a feature from its class.
     * 
     * @param <C> The custom feature type.
     * @param feature The feature class.
     * @return The feature instance, <code>null</code> if not found.
     */
    private <C extends Feature> C getFeature(Class<C> feature)
    {
        final C value;
        if (features.containsKey(feature))
        {
            value = feature.cast(features.get(feature));
        }
        else
        {
            for (final Feature current : features.values())
            {
                if (feature.isAssignableFrom(current.getClass()))
                {
                    return feature.cast(current);
                }
            }
            value = null;
        }
        return value;
    }
}
