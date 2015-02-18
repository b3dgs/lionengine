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

import com.b3dgs.lionengine.game.configurer.ConfigCollisionGroup;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.Tile;

/**
 * Represents the collision group, which can be applied to a {@link Tile}. It allows to reference easily a set of
 * {@link CollisionFormula} previously defined on the {@link MapTile}.
 * Here a definition example:
 * 
 * <pre>
 * {@code<lionengine:groups xmlns:lionengine="http://lionengine.b3dgs.com">}
 *    {@code<lionengine:group name="block" sheet="0" start="0" end="5">}
 *       {@code<lionengine:formula>top</lionengine:formula>}
 *       {@code<lionengine:formula>bottom</lionengine:formula>}
 *       {@code<lionengine:formula>left</lionengine:formula>}
 *       {@code<lionengine:formula>right</lionengine:formula>}
 *    {@code</lionengine:group>}
 * {@code</lionengine:groups>}
 * 
 * This will define 4 references to existing collisions from their name.
 * It will be applied for each tile of sheet 0, with an index between 0 and 5.
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see ConfigCollisionGroup
 * @see CollisionFormula
 */
public class CollisionGroup
{
    /** The group Name. */
    private final String name;
    /** Sheet number of the accepted tile. */
    private final int sheet;
    /** Starting tile number. */
    private final int start;
    /** Ending tile number. */
    private final int end;
    /** The collision formulas used. */
    private final Collection<CollisionFormula> formulas;

    /**
     * Create a collision group.
     * 
     * @param name The collision group name.
     * @param sheet The accepted sheet.
     * @param start The starting tile number.
     * @param end The ending tile number.
     * @param formulas The collision formulas reference.
     */
    public CollisionGroup(String name, int sheet, int start, int end, Collection<CollisionFormula> formulas)
    {
        this.name = name;
        this.sheet = sheet;
        this.start = start;
        this.end = end;
        this.formulas = formulas;
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

    /**
     * Get collision formulas reference.
     * 
     * @return The collision formulas reference.
     */
    public Collection<CollisionFormula> getFormulas()
    {
        return formulas;
    }
}
