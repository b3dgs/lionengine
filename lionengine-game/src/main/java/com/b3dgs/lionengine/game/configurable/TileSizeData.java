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
package com.b3dgs.lionengine.game.configurable;

/**
 * Represents the size in tile data from a configurable node.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class TileSizeData
{
    /** The width in tile value. */
    private final int widthInTile;
    /** The height in tile value. */
    private final int heightInTile;

    /**
     * Constructor.
     * 
     * @param widthInTile The width in tile value.
     * @param heightInTile The height in tile value.
     */
    public TileSizeData(int widthInTile, int heightInTile)
    {
        this.widthInTile = widthInTile;
        this.heightInTile = heightInTile;
    }

    /**
     * Get the width in tile value.
     * 
     * @return The width in tile value.
     */
    public int getWidthInTile()
    {
        return widthInTile;
    }

    /**
     * Get the height in tile value.
     * 
     * @return The height in tile value.
     */
    public int getHeightInTile()
    {
        return heightInTile;
    }
}
