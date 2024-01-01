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
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

/**
 * Heuristic interface. Define a function that is used to perform the cost of a path.
 */
public interface Heuristic
{
    /**
     * This controls the order in which tiles are searched while attempting to find a path to the target location. The
     * lower the cost the more likely the tile will be searched.
     * 
     * @param sx The x coordinate of the tile being evaluated.
     * @param sy The y coordinate of the tile being evaluated.
     * @param dx The x coordinate of the target location.
     * @param dy The y coordinate of the target location.
     * @return The cost associated with the given tile.
     */
    double getCost(int sx, int sy, int dx, int dy);
}
