/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

/**
 * Represents a coordinate in tile.
 */
public final class CoordTile
{
    /** Horizontal tile location. */
    private final int tx;
    /** Vertical tile location. */
    private final int ty;

    /**
     * Constructor.
     * 
     * @param tx The horizontal tile location.
     * @param ty The vertical tile location.
     */
    public CoordTile(int tx, int ty)
    {
        this.tx = tx;
        this.ty = ty;
    }

    /**
     * Get the horizontal tile location.
     * 
     * @return The horizontal tile location.
     */
    public int getX()
    {
        return tx;
    }

    /**
     * Get the vertical tile location.
     * 
     * @return The vertical tile location.
     */
    public int getY()
    {
        return ty;
    }
}
