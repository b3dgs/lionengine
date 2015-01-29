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

import java.util.Collection;

/**
 * Represents the collision group.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class CollisionGroup
{
    /** Name. */
    private final String name;
    /** Tile pattern. */
    private final int pattern;
    /** Starting tile. */
    private final int start;
    /** Ending tile. */
    private final int end;
    /** The collision formulas name. */
    private final Collection<String> formulas;

    /**
     * Load collisions from configuration media.
     * 
     * @param name The tile collision name.
     * @param pattern The tile pattern.
     * @param start The starting tile number.
     * @param end The ending tile number.
     * @param formulas The collision formulas names.
     */
    public CollisionGroup(String name, int pattern, int start, int end, Collection<String> formulas)
    {
        this.name = name;
        this.pattern = pattern;
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
     * Get the pattern value.
     * 
     * @return The pattern value.
     */
    public int getPattern()
    {
        return pattern;
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
    public Collection<String> getFormulas()
    {
        return formulas;
    }
}
