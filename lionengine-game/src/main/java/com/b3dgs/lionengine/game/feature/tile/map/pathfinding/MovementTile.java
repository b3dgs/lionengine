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
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

import com.b3dgs.lionengine.Nameable;
import com.b3dgs.lionengine.util.UtilConversion;

/**
 * Represents the possible movement from a tile.
 */
public enum MovementTile implements Nameable
{
    /** Up movement. */
    UP(0, 1),
    /** Down movement. */
    DOWN(0, -1),
    /** Left movement. */
    LEFT(-1, 0),
    /** Right movement. */
    RIGHT(1, 0),
    /** Diagonal up-left movement. */
    DIAGONAL_UP_LEFT(-1, 1),
    /** Diagonal up-right movement. */
    DIAGONAL_UP_RIGHT(1, 1),
    /** Diagonal down-left movement. */
    DIAGONAL_DOWN_LEFT(-1, -1),
    /** Diagonal down-right movement. */
    DIAGONAL_DOWN_RIGHT(1, -1),
    /** No movement. */
    NONE(0, 0);

    /** Horizontal movement side. */
    private final int sx;
    /** Vertical movement side. */
    private final int sy;

    /**
     * Create the movement.
     * 
     * @param sx The horizontal side.
     * @param sy The vertical side.
     */
    MovementTile(int sx, int sy)
    {
        this.sx = sx;
        this.sy = sy;
    }

    /**
     * Check if is movement.
     * 
     * @param sx The horizontal side.
     * @param sy The vertical side.
     * @return <code>true</code> if is movement, <code>false</code> else.
     */
    public boolean is(int sx, int sy)
    {
        return this.sx == sx && this.sy == sy;
    }

    /**
     * Get movement from tile movement.
     * 
     * @param sx The horizontal side.
     * @param sy The vertical side.
     * @return The associated tile movement.
     */
    public static MovementTile from(int sx, int sy)
    {
        for (final MovementTile movement : values())
        {
            if (movement.is(sx, sy))
            {
                return movement;
            }
        }
        return NONE;
    }

    /*
     * Nameable
     */

    @Override
    public String getName()
    {
        return UtilConversion.toTitleCase(name());
    }
}
