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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.Identifiable;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollision;
import com.b3dgs.lionengine.game.feature.tile.map.collision.TileCollisionModel;

/**
 * Test {@link TileGame}.
 */
public final class TileGameTest
{
    /**
     * Test constructor with null sheet.
     */
    @Test
    public void testConstructoNullSheet()
    {
        assertThrows(() -> new TileGame(null, 0, 0, 0, 1, 1), "Unexpected null argument !");
    }

    /**
     * Test constructor with negative sheet.
     */
    @Test
    public void testConstructorNegativeSheet()
    {
        assertThrows(() -> new TileGame(Integer.valueOf(-1), 0, 0, 0, 1, 1),
                     "Invalid argument: -1 is not superior or equal to 0");
    }

    /**
     * Test constructor with negative number.
     */
    @Test
    public void testConstructorNegativeNumber()
    {
        assertThrows(() -> new TileGame(Integer.valueOf(0), -1, 0, 0, 1, 1),
                     "Invalid argument: -1 is not superior or equal to 0");
    }

    /**
     * Test constructor with invalid width.
     */
    @Test
    public void testConstructorNegativeWidth()
    {
        assertThrows(() -> new TileGame(Integer.valueOf(0), 0, 0, 0, 0, 1),
                     "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test constructor with invalid height.
     */
    @Test
    public void testConstructorInvalidHeight()
    {
        assertThrows(() -> new TileGame(Integer.valueOf(0), 0, 0, 0, 1, 0),
                     "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test the getters.
     */
    @Test
    public void testGetters()
    {
        final TileGame tile = new TileGame(Integer.valueOf(0), 1, 16, 25, 4, 5);
        final TileCollision feature = new TileCollisionModel(tile);
        tile.addFeature(feature);

        assertEquals(Integer.valueOf(0), tile.getSheet());
        assertEquals(1, tile.getNumber());
        assertEquals(16, tile.getX());
        assertEquals(25, tile.getY());
        assertEquals(4, tile.getInTileX());
        assertEquals(5, tile.getInTileY());
        assertEquals(4, tile.getWidth());
        assertEquals(5, tile.getHeight());
        assertEquals(1, tile.getInTileWidth());
        assertEquals(1, tile.getInTileHeight());
        assertTrue(tile.hasFeature(TileCollision.class));
        assertFalse(tile.hasFeature(MockFeature.class));

        final Class<?> next = tile.getFeature(Feature.class).getClass();

        assertTrue(next.equals(feature.getClass()) || Identifiable.class.isAssignableFrom(next), next.getName());
    }

    /**
     * Test the equals.
     */
    @Test
    public void testEquals()
    {
        final TileGame tile = new TileGame(Integer.valueOf(0), 1, 16, 25, 4, 5);

        assertEquals(tile, tile);
        assertEquals(tile, new TileGame(Integer.valueOf(0), 1, 16, 25, 4, 5));

        assertNotEquals(tile, new Object());
        assertNotEquals(tile, new TileGame(Integer.valueOf(1), 1, 16, 25, 4, 5));
        assertNotEquals(tile, new TileGame(Integer.valueOf(0), 0, 16, 25, 4, 5));
        assertNotEquals(tile, new TileGame(Integer.valueOf(0), 1, 0, 25, 4, 5));
        assertNotEquals(tile, new TileGame(Integer.valueOf(0), 1, 16, 0, 4, 5));
        assertNotEquals(tile, new TileGame(Integer.valueOf(0), 1, 16, 25, 10, 5));
        assertNotEquals(tile, new TileGame(Integer.valueOf(0), 1, 16, 25, 4, 10));
    }

    /**
     * Test the hash code.
     */
    @Test
    public void testHashCode()
    {
        final TileGame tile = new TileGame(Integer.valueOf(0), 1, 16, 25, 4, 5);

        assertHashEquals(tile, tile);
        assertHashEquals(tile, new TileGame(Integer.valueOf(0), 1, 16, 25, 4, 5));

        assertHashNotEquals(tile, new Object());
        assertHashNotEquals(tile, new TileGame(Integer.valueOf(1), 1, 16, 25, 4, 5));
        assertHashNotEquals(tile, new TileGame(Integer.valueOf(0), 0, 16, 25, 4, 5));
        assertHashNotEquals(tile, new TileGame(Integer.valueOf(0), 1, 0, 25, 4, 5));
        assertHashNotEquals(tile, new TileGame(Integer.valueOf(0), 1, 16, 0, 4, 5));
        assertHashNotEquals(tile, new TileGame(Integer.valueOf(0), 1, 16, 25, 10, 5));
        assertHashNotEquals(tile, new TileGame(Integer.valueOf(0), 1, 16, 25, 4, 10));
    }

    /**
     * Test the to string.
     */
    @Test
    public void testToString()
    {
        assertEquals("sheet = 0 | number = 1 | tx = 2 | ty = 3",
                     new TileGame(Integer.valueOf(0), 1, 32, 48, 16, 16).toString());
    }

    /**
     * Mock feature.
     */
    private interface MockFeature extends Feature
    {
        // Mock
    }
}
