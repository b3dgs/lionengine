/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

/**
 * Represents the featurables handled by the handler, providing quick access to them from their type.
 * 
 * @see Featurable
 */
public interface Handlables
{
    /**
     * Get the featurable from its Id.
     * 
     * @param id The featurable Id.
     * @return The featurable instance, <code>null</code> if none.
     */
    Featurable get(Integer id);

    /**
     * Get all featurables of this type.
     * 
     * @param <I> The featurable interface type.
     * @param type The expected type.
     * @return The featurables of this type.
     */
    <I> Iterable<I> get(Class<I> type);

    /**
     * Get all featurables.
     * 
     * @return The featurables.
     */
    Iterable<Featurable> values();
}
