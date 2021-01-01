/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * A description of an implementation that can find a path from one location on a tile map to another based on
 * information provided by that tile map.
 */
public interface PathFinder
{
    /**
     * Find a path from the starting location provided to the destination location avoiding blockages and attempting to
     * honor costs provided by the tile map.
     * 
     * @param mover The entity that will be moving along the path.
     * @param dtx The x coordinate of the destination location.
     * @param dty The y coordinate of the destination location.
     * @param ignoreRef The ignore map array reference checking (<code>true</code> to ignore references).
     * @return The path found from start to end, or null if no path can be found.
     */
    Path findPath(Pathfindable mover, int dtx, int dty, boolean ignoreRef);
}
