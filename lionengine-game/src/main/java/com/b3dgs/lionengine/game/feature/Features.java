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

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.Feature;

/**
 * Features handler representation. Store features by type, allowing quick access from an interface.
 */
public class Features
{
    /** Feature not found error. */
    static final String ERROR_FEATURE_NOT_FOUND = "Feature not found: ";

    /** Features handled. */
    private final Map<Class<? extends Feature>, Feature> typeToFeature = new HashMap<>();

    /**
     * Create features handler.
     */
    public Features()
    {
        super();
    }

    /**
     * Add a feature. Stores its interface, and all sub interfaces which describe also a {@link Feature}.
     * 
     * @param feature The feature to add.
     */
    public void add(Feature feature)
    {
        typeToFeature.put(feature.getClass(), feature);

        for (final Class<?> type : feature.getClass().getInterfaces())
        {
            if (Feature.class.isAssignableFrom(type))
            {
                typeToFeature.put(type.asSubclass(Feature.class), feature);
            }
        }
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
        final C value;
        if (typeToFeature.containsKey(feature))
        {
            value = feature.cast(typeToFeature.get(feature));
        }
        else
        {
            for (final Feature current : typeToFeature.values())
            {
                if (feature.isAssignableFrom(current.getClass()))
                {
                    return feature.cast(current);
                }
            }
            throw new LionEngineException(ERROR_FEATURE_NOT_FOUND + feature.getName());
        }
        return value;
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
        final boolean contains;
        if (typeToFeature.containsKey(feature))
        {
            contains = true;
        }
        else
        {
            for (final Feature current : typeToFeature.values())
            {
                if (feature.isAssignableFrom(current.getClass()))
                {
                    return true;
                }
            }
            contains = false;
        }
        return contains;
    }

    /**
     * Get all features.
     * 
     * @return The features list.
     */
    public Iterable<Feature> getFeatures()
    {
        return typeToFeature.values();
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
}
