/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test {@link TileRef}.
 */
public final class TileRefTest
{
    /**
     * Test the constructor with null tile argument.
     */
    @Test
    public void testConstructorTileNull()
    {
        assertThrows(() -> new TileRef(null), "Unexpected null argument !");
    }

    /**
     * Test the constructor with null sheet argument.
     */
    @Test
    public void testConstructorSheetNull()
    {
        assertThrows(() -> new TileRef(null, 1), "Unexpected null argument !");
    }

    /**
     * Test the constructor with negative sheet argument.
     */
    @Test
    public void testConstructorNegativeSheet()
    {
        assertThrows(() -> new TileRef(Integer.valueOf(-1), 1), "Invalid argument: -1 is not superior or equal to 0");
    }

    /**
     * Test the constructor with negative sheet argument.
     */
    @Test
    public void testConstructorNegativeNumber()
    {
        assertThrows(() -> new TileRef(Integer.valueOf(0), -1), "Invalid argument: -1 is not superior or equal to 0");
    }

    /**
     * Test the getters.
     */
    @Test
    public void testGetters()
    {
        final TileRef tile = new TileRef(0, 1);

        assertEquals(Integer.valueOf(0), tile.getSheet());
        assertEquals(1, tile.getNumber());
    }

    /**
     * Test the equals.
     */
    @Test
    public void testEquals()
    {
        final TileRef tile = new TileRef(0, 1);

        assertEquals(tile, tile);
        assertEquals(tile, new TileRef(0, 1));
        assertEquals(tile, new TileRef(Integer.valueOf(0), 1));
        assertEquals(tile, new TileRef(new TileGame(Integer.valueOf(0), 1, 1, 1, 1, 1)));

        assertNotEquals(tile, null);
        assertNotEquals(tile, new Object());
        assertNotEquals(tile, new TileRef(0, 0));
        assertNotEquals(tile, new TileRef(1, 1));
        assertNotEquals(tile, new TileRef(1, 0));
    }

    /**
     * Test the hash code.
     */
    @Test
    public void testHashCode()
    {
        final TileRef tile = new TileRef(0, 1);

        assertHashEquals(tile, tile);
        assertHashEquals(tile, new TileRef(0, 1));
        assertHashEquals(tile, new TileRef(Integer.valueOf(0), 1));
        assertHashEquals(tile, new TileRef(new TileGame(Integer.valueOf(0), 1, 1, 1, 1, 1)));

        assertHashNotEquals(tile, new Object());
        assertHashNotEquals(tile, new TileRef(0, 0));
        assertHashNotEquals(tile, new TileRef(1, 1));
        assertHashNotEquals(tile, new TileRef(1, 0));
    }

    /**
     * Test the to string.
     */
    @Test
    public void testToString()
    {
        final TileRef tile = new TileRef(0, 1);

        assertEquals("tileRef(sheet=0 | number=1)", tile.toString());
    }
}
