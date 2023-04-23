/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game;

/**
 * List of available orientations.
 * <p>
 * This class is Thread-Safe.
 * </p>
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

    /**
     * Get the orientation depending of the speed.
     * 
     * @param sx The horizontal speed.
     * @param sy The vertical speed.
     * @return The corresponding orientation (<code>null</code> if unchanged).
     */
    public static Orientation get(int sx, int sy)
    {
        return get(0, 0, sx, sy);
    }

    /**
     * Get the orientation depending of the current tile index and destination tile index.
     * 
     * @param stx The starting horizontal tile index.
     * @param sty The starting vertical tile index.
     * @param dtx The destination horizontal tile index.
     * @param dty The destination vertical tile index.
     * @return The corresponding orientation (<code>null</code> if unchanged).
     */
    public static Orientation get(int stx, int sty, int dtx, int dty)
    {
        final Orientation orientation;
        if (sty < dty)
        {
            orientation = getNorth(stx, dtx);
        }
        else if (sty > dty)
        {
            orientation = getSouth(stx, dtx);
        }
        else
        {
            orientation = getEstWest(stx, dtx);
        }
        return orientation;
    }

    /**
     * Get the orientation on north side.
     * 
     * @param stx The starting horizontal tile index.
     * @param dtx The destination horizontal tile index.
     * @return The corresponding orientation.
     */
    private static Orientation getNorth(int stx, int dtx)
    {
        final Orientation orientation;
        if (stx < dtx)
        {
            orientation = Orientation.NORTH_EAST;
        }
        else if (stx > dtx)
        {
            orientation = Orientation.NORTH_WEST;
        }
        else
        {
            orientation = Orientation.NORTH;
        }
        return orientation;
    }

    /**
     * Get the orientation on south side.
     * 
     * @param stx The starting horizontal tile index.
     * @param dtx The destination horizontal tile index.
     * @return The corresponding orientation.
     */
    private static Orientation getSouth(int stx, int dtx)
    {
        final Orientation orientation;
        if (stx > dtx)
        {
            orientation = Orientation.SOUTH_WEST;
        }
        else if (stx < dtx)
        {
            orientation = Orientation.SOUTH_EAST;
        }
        else
        {
            orientation = Orientation.SOUTH;
        }
        return orientation;
    }

    /**
     * Get the orientation on horizontal side.
     * 
     * @param stx The starting horizontal tile index.
     * @param dtx The destination horizontal tile index.
     * @return The corresponding orientation, <code>null</code> if unchanged.
     */
    private static Orientation getEstWest(int stx, int dtx)
    {
        final Orientation orientation;
        if (stx < dtx)
        {
            orientation = Orientation.EAST;
        }
        else if (stx > dtx)
        {
            orientation = Orientation.WEST;
        }
        else
        {
            orientation = null;
        }
        return orientation;
    }
}
