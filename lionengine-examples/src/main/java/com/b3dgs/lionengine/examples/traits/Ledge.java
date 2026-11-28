/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.examples.traits;

import com.b3dgs.lionengine.physics.Box;
import com.b3dgs.lionengine.physics.ShapeQuery;

/**
 * The solid tiles of {@link Jumping}: a long lower floor and, on its left, a raised floor ending in a ledge, of 1 m
 * tiles, as a {@link ShapeQuery} written by the game (REQ-MAP-008, ADR-0028, ADR-0083).
 *
 * <p>
 * Same form as {@link Ground}: the boxes of the solid tiles, compared with the queried area by
 * {@link Box#overlaps(Box)} (touching is not overlapping), the overlapping ones written in tile order into the caller's
 * boxes, nothing allocated per query. Meters, Y up: the lower floor top at {@value #LOWER_TOP} m, the raised floor top
 * at {@value #UPPER_TOP} m, its ledge at {@code x =} {@value #LEDGE_X} m.
 * </p>
 *
 * @since 11.0.0
 */
public final class Ledge implements ShapeQuery
{
    /** Lower floor tiles, along X from {@code 0}. */
    public static final int LOWER_TILES = 32;
    /** Raised floor tiles, along X from {@code 0}, on the lower floor. */
    public static final int UPPER_TILES = 4;
    /** Top of the lower floor, in meters. */
    public static final double LOWER_TOP = 1.0;
    /** Top of the raised floor, in meters. */
    public static final double UPPER_TOP = 2.0;
    /** Right end of the raised floor, in meters. */
    public static final double LEDGE_X = UPPER_TILES;
    /** Tile size, in meters. */
    private static final double TILE = 1.0;
    /** Half a tile, in meters. */
    private static final double HALF_TILE = TILE / 2.0;

    /** The solid tiles. */
    private final Box[] tiles = new Box[LOWER_TILES + UPPER_TILES];

    /**
     * Creates the lower floor, from {@code x = 0} to {@value #LOWER_TILES} m, and the raised floor on it, from
     * {@code x = 0} to the ledge.
     */
    public Ledge()
    {
        for (int i = 0; i < LOWER_TILES; i++)
        {
            tiles[i] = new Box(i * TILE + HALF_TILE, LOWER_TOP - HALF_TILE, TILE, TILE);
        }
        for (int i = 0; i < UPPER_TILES; i++)
        {
            tiles[LOWER_TILES + i] = new Box(i * TILE + HALF_TILE, UPPER_TOP - HALF_TILE, TILE, TILE);
        }
    }

    @Override
    public int query(Box area, Box[] results)
    {
        if (area == null || results == null)
        {
            throw new IllegalArgumentException("Area or results are null");
        }
        int count = 0;
        for (final Box tile : tiles)
        {
            if (tile.overlaps(area))
            {
                if (count < results.length)
                {
                    results[count].set(tile);
                }
                count++;
            }
        }
        return count;
    }
}
