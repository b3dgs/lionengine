/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Feature representation.
 * 
 * @param <F> The feature type used.
 */
public class Features<F>
{
    /** Feature not found error. */
    private static final String ERROR_FEATURE_NOT_FOUND = "Feature not found: ";

    /** Features provided. */
    private final Map<Class<? extends F>, F> features;
    /** Feature interface base. */
    private final Class<F> base;

    /**
     * Create a feature handler.
     * 
     * @param base The feature base interface.
     */
    public Features(Class<F> base)
    {
        this.base = base;
        features = new HashMap<Class<? extends F>, F>();
    }

    /**
     * Add a trait.
     * 
     * @param feature The feature to add.
     */
    public final void add(F feature)
    {
        for (final Class<?> type : feature.getClass().getInterfaces())
        {
            if (base.isAssignableFrom(type))
            {
                features.put(type.asSubclass(base), feature);
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
    public final <C> C get(Class<C> feature)
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
    public final <C> boolean contains(Class<C> feature)
    {
        return getFeature(feature) != null;
    }

    /**
     * Get all features.
     * 
     * @return The features list.
     */
    public final Iterable<F> getAll()
    {
        return features.values();
    }

    /**
     * Get all features types.
     * 
     * @return The features types.
     */
    public final Iterable<Class<? extends F>> getFeatures()
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
    private <C> C getFeature(Class<C> feature)
    {
        final C value;
        if (features.containsKey(feature))
        {
            value = feature.cast(features.get(feature));
        }
        else
        {
            for (final Object current : features.values())
            {
                if (feature.isAssignableFrom(current.getClass()))
                {
                    return feature.cast(current);
                }
            }
            if (feature.isAssignableFrom(getClass()))
            {
                value = feature.cast(this);
            }
            else
            {
                value = null;
            }
        }
        return value;
    }
}
