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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.game.map.Tile;

/**
 * Represents the map collision results.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CollisionResult
{
    /** Horizontal collision location (<code>null</code> if none). */
    private final Double x;
    /** Vertical collision location (<code>null</code> if none). */
    private final Double y;
    /** Collided tile. */
    private final Tile tile;

    /**
     * Create a collision result.
     * 
     * @param x The horizontal collision location (<code>null</code> if none).
     * @param y The vertical collision location (<code>null</code> if none).
     * @param tile The collided tile.
     */
    public CollisionResult(Double x, Double y, Tile tile)
    {
        Check.notNull(tile);
        this.x = x;
        this.y = y;
        this.tile = tile;
    }

    /**
     * Get the horizontal collision location.
     * 
     * @return The horizontal collision location (<code>null</code> if none).
     */
    public Double getX()
    {
        return x;
    }

    /**
     * Get the vertical collision location.
     * 
     * @return The vertical collision location (<code>null</code> if none).
     */
    public Double getY()
    {
        return y;
    }

    /**
     * Get the collided tile.
     * 
     * @return The collided tile.
     */
    public Tile getTile()
    {
        return tile;
    }
}
