/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit.generator;

import com.b3dgs.lionengine.game.feature.tile.map.MapTile;

/**
 * Describe a map generation preference element.
 */
public interface Preference extends Comparable<Preference>
{
    /**
     * Apply preference on the specified map.
     * 
     * @param map The map reference.
     */
    void apply(MapTile map);

    /**
     * Get the preference priority.
     * <p>
     * Priority is used to apply preferences in the right order. A low value means a high priority, starting at 0.
     * </p>
     * <p>
     * If there is many preferences with the same priority, they are applied in the order they where added.
     * </p>
     * 
     * @return The preference priority.
     */
    Integer getPrority();
}
