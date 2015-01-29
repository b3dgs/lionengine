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
package com.b3dgs.lionengine.game.collision;

import com.b3dgs.lionengine.game.Axis;

/**
 * Represents the range of the collision for a specified axis.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CollisionRange
{
    /** Output target. */
    private final Axis output;
    /** Minimum horizontal value relative to tile. */
    private final int minX;
    /** Maximum horizontal value relative to tile. */
    private final int maxX;
    /** Minimum vertical value relative to tile. */
    private final int minY;
    /** Maximum vertical value relative to tile. */
    private final int maxY;
    /** Source used. */
    private final CollisionSource source;

    /**
     * Create a collision range.
     * 
     * @param output The output target.
     * @param minX The minimum accepted value.
     * @param maxX The maximum accepted value.
     * @param minY The minimum accepted value.
     * @param maxY The maximum accepted value.
     * @param source The soruce used.
     */
    public CollisionRange(Axis output, int minX, int maxX, int minY, int maxY, CollisionSource source)
    {
        this.output = output;
        this.minX = minX;
        this.maxX = maxX;
        this.minY = minY;
        this.maxY = maxY;
        this.source = source;
    }

    /**
     * Get the output target.
     * 
     * @return The output target.
     */
    public Axis getOutput()
    {
        return output;
    }

    /**
     * Get the minimum horizontal accepted value.
     * 
     * @return The minimum horizontal accepted value.
     */
    public int getMinX()
    {
        return minX;
    }

    /**
     * Get the maximum horizontal accepted value.
     * 
     * @return The maximum horizontal accepted value.
     */
    public int getMaxX()
    {
        return maxX;
    }

    /**
     * Get the minimum vertical accepted value.
     * 
     * @return The minimum vertical accepted value.
     */
    public int getMinY()
    {
        return minY;
    }

    /**
     * Get the maximum vertical accepted value.
     * 
     * @return The maximum vertical accepted value.
     */
    public int getMaxY()
    {
        return maxY;
    }

    /**
     * Get the collision source used.
     * 
     * @return The collision source used.
     */
    public CollisionSource getSource()
    {
        return source;
    }
}
