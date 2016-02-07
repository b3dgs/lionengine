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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents something that can support features.
 * 
 * @param <F> The feature type used.
 * @see Features
 */
public interface Featurable<F>
{
    /**
     * Add a map feature for external processing.
     * 
     * @param feature The feature to add.
     */
    void addFeature(F feature);

    /**
     * Get a feature instance from its type.
     * 
     * @param <C> The custom feature type.
     * @param feature The feature type.
     * @return The feature instance.
     * @throws LionEngineException If feature not found.
     */
    <C extends F> C getFeature(Class<C> feature);

    /**
     * Get the supported features.
     * 
     * @return The supported features.
     */
    Iterable<? extends F> getFeatures();

    /**
     * Check a feature existence from its type.
     * 
     * @param <C> The custom feature type.
     * @param feature The feature type.
     * @return <code>true</code> if feature exists, <code>false</code> else.
     */
    <C extends F> boolean hasFeature(Class<C> feature);
}
