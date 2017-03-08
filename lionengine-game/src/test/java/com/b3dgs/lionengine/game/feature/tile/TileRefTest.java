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

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Test the tile ref class.
 */
public class TileRefTest
{
    /**
     * Test the constructor with null tile argument.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructorTileNull()
    {
        Assert.assertNotNull(new TileRef(null));
    }

    /**
     * Test the constructor with null sheet argument.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructorSheetNull()
    {
        Assert.assertNotNull(new TileRef(null, 1));
    }

    /**
     * Test the constructor with negative sheet argument.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructorNegativeSheet()
    {
        Assert.assertNotNull(new TileRef(Integer.valueOf(-1), 1));
    }

    /**
     * Test the constructor with negative sheet argument.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructorNegativeNumber()
    {
        Assert.assertNotNull(new TileRef(Integer.valueOf(0), -1));
    }

    /**
     * Test the getters.
     */
    @Test
    public void testGetters()
    {
        final TileRef tile = new TileRef(0, 1);

        Assert.assertEquals(Integer.valueOf(0), tile.getSheet());
        Assert.assertEquals(1, tile.getNumber());
    }

    /**
     * Test the hash code.
     */
    @Test
    public void testHashcode()
    {
        final TileRef tile = new TileRef(0, 1);

        Assert.assertEquals(tile.hashCode(), tile.hashCode());
        Assert.assertEquals(tile.hashCode(), new TileRef(0, 1).hashCode());
        Assert.assertEquals(tile.hashCode(), new TileRef(Integer.valueOf(0), 1).hashCode());
        Assert.assertEquals(tile.hashCode(), new TileRef(new TileGame(Integer.valueOf(0), 1, 1, 1, 1, 1)).hashCode());

        Assert.assertNotEquals(tile.hashCode(), new Object().hashCode());
        Assert.assertNotEquals(tile.hashCode(), new TileRef(0, 0).hashCode());
        Assert.assertNotEquals(tile.hashCode(), new TileRef(1, 1).hashCode());
        Assert.assertNotEquals(tile.hashCode(), new TileRef(1, 0).hashCode());
    }

    /**
     * Test the equals.
     */
    @Test
    public void testEquals()
    {
        final TileRef tile = new TileRef(0, 1);

        Assert.assertEquals(tile, tile);
        Assert.assertEquals(tile, new TileRef(0, 1));
        Assert.assertEquals(tile, new TileRef(Integer.valueOf(0), 1));
        Assert.assertEquals(tile, new TileRef(new TileGame(Integer.valueOf(0), 1, 1, 1, 1, 1)));

        Assert.assertNotEquals(tile, new Object());
        Assert.assertNotEquals(tile, new TileRef(0, 0));
        Assert.assertNotEquals(tile, new TileRef(1, 1));
        Assert.assertNotEquals(tile, new TileRef(1, 0));
    }

    /**
     * Test the to string.
     */
    @Test
    public void testToString()
    {
        final TileRef tile = new TileRef(0, 1);

        Assert.assertEquals("tileRef(sheet=0 | number=1)", tile.toString());
    }
}
