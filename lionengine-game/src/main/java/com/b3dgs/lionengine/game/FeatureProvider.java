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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents the {@link Feature} provider, allowing to retrieve {@link Feature} from their type.
 */
public interface FeatureProvider
{
    /**
     * Get a feature instance from its type. Return the right instance event if {@link Feature} is not prepared.
     * 
     * @param <C> The custom feature type.
     * @param feature The feature type.
     * @return The feature instance.
     * @throws LionEngineException If feature not found.
     */
    <C extends Feature> C getFeature(Class<C> feature);

    /**
     * Get the supported features.
     * 
     * @return The supported features.
     */
    Iterable<Feature> getFeatures();

    /**
     * Get the supported features type.
     * 
     * @return The supported features type.
     */
    Iterable<Class<? extends Feature>> getFeaturesType();

    /**
     * Check a feature existence from its type.
     * 
     * @param feature The feature type.
     * @return <code>true</code> if feature exists, <code>false</code> else.
     */
    boolean hasFeature(Class<? extends Feature> feature);
}
