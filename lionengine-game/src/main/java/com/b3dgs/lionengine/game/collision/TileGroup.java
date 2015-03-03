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

import com.b3dgs.lionengine.game.configurer.ConfigTileGroup;
import com.b3dgs.lionengine.game.map.Tile;

/**
 * Represents the tile group, which can be applied to a {@link Tile}.
 * Here a definition example:
 * 
 * <pre>
 * {@code<lionengine:groups xmlns:lionengine="http://lionengine.b3dgs.com">}
 *    {@code<lionengine:group name="block" sheet="0" start="0" end="5"/>}
 * {@code</lionengine:groups>}
 * 
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see ConfigTileGroup
 */
public class TileGroup
{
    /** The group Name. */
    private final String name;
    /** Sheet number of the accepted tile. */
    private final int sheet;
    /** Starting tile number. */
    private final int start;
    /** Ending tile number. */
    private final int end;

    /**
     * Create a tile group.
     * 
     * @param name The group name.
     * @param sheet The accepted sheet.
     * @param start The starting tile number.
     * @param end The ending tile number.
     */
    public TileGroup(String name, int sheet, int start, int end)
    {
        this.name = name;
        this.sheet = sheet;
        this.start = start;
        this.end = end;
    }

    /**
     * Get the collision group name.
     * 
     * @return The collision group name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Get the sheet value.
     * 
     * @return The sheet value.
     */
    public int getSheet()
    {
        return sheet;
    }

    /**
     * Get the starting tile number.
     * 
     * @return The starting tile number.
     */
    public int getStart()
    {
        return start;
    }

    /**
     * Get the ending tile number.
     * 
     * @return The ending tile number.
     */
    public int getEnd()
    {
        return end;
    }
}
