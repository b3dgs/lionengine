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
package com.b3dgs.lionengine.game.collision;

import java.util.Collection;

import com.b3dgs.lionengine.Nameable;
import com.b3dgs.lionengine.game.map.Tile;

/**
 * Represents the tile group, which can be applied to a {@link TileRef}.
 * Here a definition example:
 * 
 * <pre>
 * &lt;lionengine:groups xmlns:lionengine="http://lionengine.b3dgs.com"&gt;
 *    &lt;lionengine:group name="block"&gt;
 *      &lt;lionengine:tile sheet="0" number="1"/&gt;
 *      &lt;lionengine:tile sheet="1" number="5"/&gt;
 *    &lt;/lionengine:group&gt;
 *    &lt;lionengine:group name="top"&gt;
 *      &lt;lionengine:tile sheet="0" number="2"/&gt;
 *      &lt;lionengine:tile sheet="0" number="3"/&gt;
 *    &lt;/lionengine:group&gt;
 * &lt;/lionengine:groups&gt;
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see com.b3dgs.lionengine.game.configurer.ConfigTileGroup
 */
public class TileGroup implements Nameable
{
    /** The group Name. */
    private final String name;
    /** Elements inside group. */
    private final Collection<TileRef> tiles;

    /**
     * Create a tile group.
     * 
     * @param name The group name.
     * @param tiles The tiles inside the group.
     */
    public TileGroup(String name, Collection<TileRef> tiles)
    {
        this.name = name;
        this.tiles = tiles;
    }

    /**
     * Check if tile is contained by the group.
     * 
     * @param tile The tile reference.
     * @return <code>true</code> if part of the group, <code>false</code> else.
     */
    public boolean contains(Tile tile)
    {
        for (final TileRef current : tiles)
        {
            if (current.getSheet().equals(tile.getSheet()) && current.getNumber() == tile.getNumber())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the tiles inside group.
     * 
     * @return The tiles inside group.
     */
    public Collection<TileRef> getTiles()
    {
        return tiles;
    }

    /*
     * Nameable
     */

    @Override
    public String getName()
    {
        return name;
    }

    /**
     * Represents the tile reference indexes.
     * 
     * @author Pierre-Alexandre (contact@b3dgs.com)
     */
    public static final class TileRef
    {
        /** Sheet id. */
        private final Integer sheet;
        /** TIle number. */
        private final int number;

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
}
