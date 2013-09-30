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
package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.patrol;

/**
 * List of entity movements.
 */
public enum Patrol
{
    /** No movement. */
    NONE(0),
    /** Horizontal movement. */
    HORIZONTAL(1),
    /** Vertical movement. */
    VERTICAL(2),
    /** Rotating movement. */
    ROTATING(3);

    /** Values. */
    private static final Patrol[] VALUES = Patrol.values();

    /**
     * Get the type from its index.
     * 
     * @param index The index.
     * @return The type.
     */
    public static Patrol get(int index)
    {
        return Patrol.VALUES[index];
    }

    /** Index value. */
    private final int index;

    /**
     * Constructor.
     * 
     * @param index The index value.
     */
    private Patrol(int index)
    {
        this.index = index;
    }

    /**
     * Get the index value.
     * 
     * @return The index value.
     */
    public int getIndex()
    {
        return index;
    }
}
