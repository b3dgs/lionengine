/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.tile;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.collision.tile.TileCollision;
import com.b3dgs.lionengine.game.collision.tile.TileCollisionModel;
import com.b3dgs.lionengine.game.feature.Feature;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the tile game class.
 */
public class TileGameTest
{
    /**
     * Test constructor with null sheet.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructoNullSheet()
    {
        Assert.assertNull(new TileGame(null, 0, 0, 0, 1, 1));
    }

    /**
     * Test constructor with negative sheet.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructorNegativeSheet()
    {
        Assert.assertNull(new TileGame(Integer.valueOf(-1), 0, 0, 0, 1, 1));
    }

    /**
     * Test constructor with negative number.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructorNegativeNumber()
    {
        Assert.assertNull(new TileGame(Integer.valueOf(0), -1, 0, 0, 1, 1));
    }

    /**
     * Test constructor with negative width.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructorNegativeWidth()
    {
        Assert.assertNull(new TileGame(Integer.valueOf(0), 0, 0, 0, -1, 1));
    }

    /**
     * Test constructor with negative height.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructorNegativeHeight()
    {
        Assert.assertNull(new TileGame(Integer.valueOf(0), 0, 0, 0, 1, -1));
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
        tile.prepareFeatures(new Services());

        Assert.assertEquals(Integer.valueOf(0), tile.getSheet());
        Assert.assertEquals(1, tile.getNumber());
        Assert.assertEquals(16, tile.getX(), UtilTests.PRECISION);
        Assert.assertEquals(25, tile.getY(), UtilTests.PRECISION);
        Assert.assertEquals(4, tile.getInTileX());
        Assert.assertEquals(5, tile.getInTileY());
        Assert.assertEquals(4, tile.getWidth());
        Assert.assertEquals(5, tile.getHeight());
        Assert.assertEquals(1, tile.getInTileWidth());
        Assert.assertEquals(1, tile.getInTileHeight());
        Assert.assertTrue(tile.hasFeature(TileCollision.class));
        Assert.assertFalse(tile.hasFeature(MockFeature.class));
        Assert.assertEquals(tile.getFeature(Feature.class), feature);
        Assert.assertEquals(tile.getFeatures().iterator().next(), feature);
    }

    /**
     * Test the hash code.
     */
    @Test
    public void testHashcode()
    {
        final TileGame tile = new TileGame(Integer.valueOf(0), 1, 16, 25, 4, 5);

        Assert.assertEquals(tile.hashCode(), tile.hashCode());
        Assert.assertEquals(tile.hashCode(), new TileGame(Integer.valueOf(0), 1, 16, 25, 4, 5).hashCode());

        Assert.assertNotEquals(tile.hashCode(), new Object().hashCode());
        Assert.assertNotEquals(tile.hashCode(), new TileGame(Integer.valueOf(1), 1, 16, 25, 4, 5).hashCode());
        Assert.assertNotEquals(tile.hashCode(), new TileGame(Integer.valueOf(0), 0, 16, 25, 4, 5).hashCode());
        Assert.assertNotEquals(tile.hashCode(), new TileGame(Integer.valueOf(0), 1, 0, 25, 4, 5).hashCode());
        Assert.assertNotEquals(tile.hashCode(), new TileGame(Integer.valueOf(0), 1, 16, 0, 4, 5).hashCode());
        Assert.assertNotEquals(tile.hashCode(), new TileGame(Integer.valueOf(0), 1, 16, 25, 10, 5).hashCode());
        Assert.assertNotEquals(tile.hashCode(), new TileGame(Integer.valueOf(0), 1, 16, 25, 4, 10).hashCode());
    }

    /**
     * Test the equals.
     */
    @Test
    public void testEquals()
    {
        final TileGame tile = new TileGame(Integer.valueOf(0), 1, 16, 25, 4, 5);

        Assert.assertEquals(tile, tile);
        Assert.assertEquals(tile, new TileGame(Integer.valueOf(0), 1, 16, 25, 4, 5));

        Assert.assertNotEquals(tile, new Object());
        Assert.assertNotEquals(tile, new TileGame(Integer.valueOf(1), 1, 16, 25, 4, 5));
        Assert.assertNotEquals(tile, new TileGame(Integer.valueOf(0), 0, 16, 25, 4, 5));
        Assert.assertNotEquals(tile, new TileGame(Integer.valueOf(0), 1, 0, 25, 4, 5));
        Assert.assertNotEquals(tile, new TileGame(Integer.valueOf(0), 1, 16, 0, 4, 5));
        Assert.assertNotEquals(tile, new TileGame(Integer.valueOf(0), 1, 16, 25, 10, 5));
        Assert.assertNotEquals(tile, new TileGame(Integer.valueOf(0), 1, 16, 25, 4, 10));
    }

    /**
     * Mock feature.
     */
    private interface MockFeature extends Feature
    {
        // Mock
    }
}
