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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Range;

/**
 * Represents the tile reference indexes.
 * Tile representation with the following data:
 * <ul>
 * <li><code>sheet</code> : tile sheet number [0 - {@link Integer#MAX_VALUE}].</li>
 * <li><code>number</code> : tile number inside tilesheet [0 - {@link Integer#MAX_VALUE}].</li>
 * </ul>
 * 
 * @see Tile
 */
public class TileRef
{
    /** Sheet number. */
    private final Integer sheet;
    /** Tile number on sheet. */
    private final int number;

    /**
     * Create the tile reference.
     * 
     * @param tile The tile reference.
     * @throws LionEngineException If invalid argument.
     */
    public TileRef(Tile tile)
    {
        Check.notNull(tile);

        sheet = tile.getSheet();
        number = tile.getNumber();
    }

    /**
     * Create the tile reference.
     * 
     * @param sheet The tile sheet number [0 - {@link Integer#MAX_VALUE}].
     * @param number The tile number [0 - {@link Integer#MAX_VALUE}].
     * @throws LionEngineException If invalid arguments.
     */
    public TileRef(int sheet, int number)
    {
        this(Integer.valueOf(sheet), number);
    }

    /**
     * Create the tile reference.
     * 
     * @param sheet The tile sheet number [0 - {@link Integer#MAX_VALUE}].
     * @param number The tile number [0 - {@link Integer#MAX_VALUE}].
     * @throws LionEngineException If invalid arguments.
     */
    public TileRef(Integer sheet, int number)
    {
        Check.notNull(sheet);
        Check.range(Range.INT_POSITIVE, sheet.intValue());
        Check.range(Range.INT_POSITIVE, number);

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
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || !(object instanceof TileRef))
        {
            return false;
        }
        final TileRef other = (TileRef) object;
        if (!sheet.equals(other.sheet) || number != other.number)
        {
            return false;
        }
        return true;
    }
}
