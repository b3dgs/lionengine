/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test {@link TileGame}.
 */
final class TileGameTest
{
    /**
     * Test constructor with negative number.
     */
    @Test
    void testConstructorNegativeNumber()
    {
        assertThrows(() -> new TileGame(-1, 0, 0, 1, 1), "Invalid argument: -1 is not superior or equal to 0");
    }

    /**
     * Test constructor with invalid width.
     */
    @Test
    void testConstructorNegativeWidth()
    {
        assertThrows(() -> new TileGame(0, 0, 0, 0, 1), "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test constructor with invalid height.
     */
    @Test
    void testConstructorInvalidHeight()
    {
        assertThrows(() -> new TileGame(0, 0, 0, 1, 0), "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test the getters.
     */
    @Test
    void testGetters()
    {
        final TileGame tile = new TileGame(1, 2, 3, 4, 5);

        assertEquals(1, tile.getNumber());
        assertEquals(Integer.valueOf(1), tile.getKey());
        assertEquals(2, tile.getInTileX());
        assertEquals(3, tile.getInTileY());
        assertEquals(1, tile.getInTileWidth());
        assertEquals(1, tile.getInTileHeight());
        assertEquals(8.0, tile.getX());
        assertEquals(15.0, tile.getY());
        assertEquals(4, tile.getWidth());
        assertEquals(5, tile.getHeight());
    }

    /**
     * Test the equals.
     */
    @Test
    void testEquals()
    {
        final TileGame tile = new TileGame(1, 16, 25, 4, 5);

        assertEquals(tile, tile);
        assertEquals(tile, new TileGame(1, 16, 25, 4, 5));
        assertEquals(tile, new TileGame(1, 16, 25, 10, 5));
        assertEquals(tile, new TileGame(1, 16, 25, 4, 10));

        assertNotEquals(tile, null);
        assertNotEquals(tile, new Object());
        assertNotEquals(tile, new TileGame(0, 16, 25, 4, 5));
        assertNotEquals(tile, new TileGame(1, 0, 25, 4, 5));
        assertNotEquals(tile, new TileGame(1, 16, 0, 4, 5));
    }

    /**
     * Test the hash code.
     */
    @Test
    void testHashCode()
    {
        final TileGame tile = new TileGame(1, 16, 25, 4, 5);

        assertHashEquals(tile, tile);
        assertHashEquals(tile, new TileGame(1, 16, 25, 4, 5));
        assertHashEquals(tile, new TileGame(1, 16, 25, 10, 5));
        assertHashEquals(tile, new TileGame(1, 16, 25, 4, 10));

        assertHashNotEquals(tile, new Object());
        assertHashNotEquals(tile, new TileGame(0, 16, 25, 4, 5));
        assertHashNotEquals(tile, new TileGame(1, 0, 25, 4, 5));
        assertHashNotEquals(tile, new TileGame(1, 16, 0, 4, 5));
    }

    /**
     * Test the to string.
     */
    @Test
    void testToString()
    {
        assertEquals("number = 1 | tx = 2 | ty = 3", new TileGame(1, 2, 3, 16, 16).toString());
    }
}
