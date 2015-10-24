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
package com.b3dgs.lionengine.game.map;

/**
 * Represents the tile reference indexes.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class TileRef
{
    /** Sheet id. */
    private final Integer sheet;
    /** TIle number. */
    private final int number;

    /**
     * Create the tile reference.
     * 
     * @param tile The tile reference.
     */
    public TileRef(Tile tile)
    {
        this(tile.getSheet(), tile.getNumber());
    }

    /**
     * Create the tile reference.
     * 
     * @param sheet The tile sheet number.
     * @param number The tile number.
     */
    public TileRef(Integer sheet, int number)
    {
        this.sheet = sheet;
        this.number = number;
    }

    /**
     * Get the sheet number.
     * 
     * @return The sheet number.
     */
    public Integer getSheet()
    {
        return sheet;
    }

    /**
     * Get the tile number.
     * 
     * @return The tile number.
     */
    public int getNumber()
    {
        return number;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + number;
        result = prime * result + sheet.intValue();
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || !(obj instanceof TileRef))
        {
            return false;
        }
        final TileRef other = (TileRef) obj;
        if (number != other.number || sheet.intValue() != other.sheet.intValue())
        {
            return false;
        }
        return true;
    }
}
