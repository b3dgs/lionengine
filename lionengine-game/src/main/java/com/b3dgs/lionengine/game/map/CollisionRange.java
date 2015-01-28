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
package com.b3dgs.lionengine.game.map;

import com.b3dgs.lionengine.game.Axis;

/**
 * Represents the range of the collision for a specified axis.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CollisionRange
{
    /** Axis used. */
    private final Axis axis;
    /** Minimum value relative to tile. */
    private final int min;
    /** Maximum value relative to tile. */
    private final int max;

    /**
     * Create a collision range.
     * 
     * @param axis The axis used.
     * @param min The minimum accepted value.
     * @param max The maximum accepted value.
     */
    public CollisionRange(Axis axis, int min, int max)
    {
        this.axis = axis;
        this.min = min;
        this.max = max;
    }

    /**
     * Get the axis used.
     * 
     * @return The axis used.
     */
    public Axis getAxis()
    {
        return axis;
    }

    /**
     * Get the minimum accepted value.
     * 
     * @return The minimum accepted value.
     */
    public int getMin()
    {
        return min;
    }

    /**
     * Get the minimum accepted value.
     * 
     * @return The minimum accepted value.
     */
    public int getMax()
    {
        return max;
    }
}
