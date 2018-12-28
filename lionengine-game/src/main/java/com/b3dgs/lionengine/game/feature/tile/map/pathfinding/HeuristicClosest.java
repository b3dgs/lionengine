/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

/**
 * A heuristic that uses the tile that is closest to the target as the next best tile.
 */
public final class HeuristicClosest implements Heuristic
{
    /**
     * Create heuristic.
     */
    public HeuristicClosest()
    {
        super();
    }

    /*
     * Heuristic
     */

    @Override
    public double getCost(int sx, int sy, int dx, int dy)
    {
        final double x = dx - (double) sx;
        final double y = dy - (double) sy;
        return Math.sqrt(x * x + y * y);
    }
}
