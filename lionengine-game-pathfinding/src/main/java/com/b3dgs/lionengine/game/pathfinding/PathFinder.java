/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.pathfinding;

/**
 * A description of an implementation that can find a path from one location on a tile map to another based on
 * information provided by that tile map.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
interface PathFinder
{
    /**
     * Find a path from the starting location provided to the destination location avoiding blockages and attempting to
     * honor costs provided by the tile map.
     * 
     * @param mover The entity that will be moving along the path.
     * @param sx The x coordinate of the start location.
     * @param sy The y coordinate of the start location.
     * @param dx The x coordinate of the destination location.
     * @param dy The y coordinate of the destination location.
     * @param ignoreRef The ignore map array reference checking (<code>true</code> to ignore references).
     * @return The path found from start to end, or null if no path can be found.
     */
    Path findPath(Pathfindable mover, int sx, int sy, int dx, int dy, boolean ignoreRef);
}
