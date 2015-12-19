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
package com.b3dgs.lionengine.game.pathfinding;

/**
 * Represents the data associated to a path.
 */
public class PathData
{
    /** Path cost. */
    private final double cost;
    /** Blocking flag. */
    private final boolean blocking;

    /**
     * Create a path data.
     * 
     * @param cost The cost value.
     * @param blocking The blocking flag.
     */
    public PathData(double cost, boolean blocking)
    {
        this.cost = cost;
        this.blocking = blocking;
    }

    /**
     * Get the cost value.
     * 
     * @return The cost value.
     */
    public double getCost()
    {
        return cost;
    }

    /**
     * Get the blocking state.
     * 
     * @return <code>true</code> if blocking, <code>false</code> else.
     */
    public boolean isBlocking()
    {
        return blocking;
    }
}
