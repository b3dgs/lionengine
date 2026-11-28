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
 * The solid tiles of {@link Landing}: a floor and a wall of 1 m tiles, as a {@link ShapeQuery} written by the game
 * (REQ-MAP-008, ADR-0028, ADR-0083).
 *
 * <p>
 * The tile map of {@code tilemap} implements the shape query with its own batch; until then a game gives the collision
 * system any {@link ShapeQuery}. This one holds the boxes of its solid tiles, compares each one with the queried area
 * by {@link Box#overlaps(Box)} (touching is not overlapping) and writes the overlapping ones, in tile order, into the
 * caller's boxes; it allocates nothing per query. Meters, Y up, the floor top at {@value #FLOOR_TOP} m.
 * </p>
 *
 * @since 11.0.0
 */
public final class Ground implements ShapeQuery
{
    /** Floor tiles, along X from {@code 0}. */
    public static final int FLOOR_TILES = 8;
    /** Top of the floor, in meters. */
    public static final double FLOOR_TOP = 1.0;
    /** Left side of the wall, in meters. */
    public static final double WALL_LEFT = 6.0;
    /** Wall height, in tiles, standing on the floor. */
    public static final int WALL_TILES = 2;
    /** Tile size, in meters. */
    private static final double TILE = 1.0;
    /** Half a tile, in meters. */
    private static final double HALF_TILE = TILE / 2.0;

    /** The solid tiles. */
    private final Box[] tiles = new Box[FLOOR_TILES + WALL_TILES];

    /**
     * Creates the floor, from {@code x = 0} to {@value #FLOOR_TILES} m, and the wall, a column of tiles on the floor
     * whose left side is at {@value #WALL_LEFT} m.
     */
    public Ground()
    {
        for (int i = 0; i < FLOOR_TILES; i++)
        {
            tiles[i] = new Box(i * TILE + HALF_TILE, FLOOR_TOP - HALF_TILE, TILE, TILE);
        }
        for (int i = 0; i < WALL_TILES; i++)
        {
            tiles[FLOOR_TILES + i] = new Box(WALL_LEFT + HALF_TILE, FLOOR_TOP + i * TILE + HALF_TILE, TILE, TILE);
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
