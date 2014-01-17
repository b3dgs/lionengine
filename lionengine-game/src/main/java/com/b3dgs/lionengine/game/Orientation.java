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
package com.b3dgs.lionengine.game;

/**
 * List of available orientations.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum Orientation
{
    /** Looking north. */
    NORTH,
    /** Looking north east. */
    NORTH_EAST,
    /** Looking east. */
    EAST,
    /** Looking south east. */
    SOUTH_EAST,
    /** Looking down. */
    SOUTH,
    /** Looking south west. */
    SOUTH_WEST,
    /** Looking west. */
    WEST,
    /** Looking north west. */
    NORTH_WEST;

    /** Orientations. */
    private static final Orientation[] ORIENTATIONS = Orientation.values();
    /** Orientations number. */
    public static final int ORIENTATIONS_NUMBER = Orientation.ORIENTATIONS.length;
    /** Number of elements on one side. */
    public static final int ORIENTATIONS_NUMBER_HALF = Orientation.ORIENTATIONS_NUMBER / 2;

    /**
     * Get the next orientation from the source plus an offset.
     * 
     * @param from The source orientation.
     * @param offset The offset to apply.
     * @return The next orientation from the source.
     */
    public static Orientation next(Orientation from, int offset)
    {
        return Orientation.ORIENTATIONS[(from.ordinal() + offset) % Orientation.ORIENTATIONS_NUMBER];
    }
}
