/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile;

import java.util.Collection;

import com.b3dgs.lionengine.Nameable;

/**
 * Represents the tile group, which can be applied to a {@link TileRef}.
 * Here a definition example:
 * 
 * <pre>
 * &lt;lionengine:groups xmlns:lionengine="http://lionengine.b3dgs.com"&gt;
 *    &lt;lionengine:group name="block"&gt; type="PLAIN"&gt;
 *      &lt;lionengine:tile sheet="0" number="1"/&gt;
 *      &lt;lionengine:tile sheet="1" number="5"/&gt;
 *    &lt;/lionengine:group&gt;
 *    &lt;lionengine:group name="top"&gt; type="TRANSITION"&gt;
 *      &lt;lionengine:tile sheet="0" number="2"/&gt;
 *      &lt;lionengine:tile sheet="0" number="3"/&gt;
 *    &lt;/lionengine:group&gt;
 * &lt;/lionengine:groups&gt;
 * </pre>
 * 
 * @see com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig
 */
public class TileGroup implements Nameable
{
    /** The group name. */
    private final String name;
    /** The group type. */
    private final TileGroupType type;
    /** Elements inside group. */
    private final Collection<TileRef> tiles;

    /**
     * Create a tile group.
     * 
     * @param name The group name.
     * @param type The group type.
     * @param tiles The tiles inside the group.
     */
    public TileGroup(String name, TileGroupType type, Collection<TileRef> tiles)
    {
        this.name = name;
        this.type = type;
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
        return contains(tile.getSheet(), tile.getNumber());
    }

    /**
     * Check if tile is contained by the group.
     * 
     * @param sheet The sheet number.
     * @param number The tile number.
     * @return <code>true</code> if part of the group, <code>false</code> else.
     */
    public boolean contains(Integer sheet, int number)
    {
        for (final TileRef current : tiles)
        {
            if (current.getSheet().equals(sheet) && current.getNumber() == number)
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the group type.
     * 
     * @return The group type.
     */
    public TileGroupType getType()
    {
        return type;
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

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + name.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final TileGroup other = (TileGroup) object;
        return name.equals(other.name);
    }
}
