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
package com.b3dgs.lionengine.game.map;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the map tile default implementation.
 */
public class MapTileGameTest
{
    /** Temp folder. */
    @Rule public final TemporaryFolder folder = new TemporaryFolder();

    private final MapTileGame map = new MapTileGame();

    /**
     * Test map creation.
     */
    @Test
    public void testCreate()
    {
        Assert.assertFalse(map.isCreated());
        map.create(16, 32, 2, 3);
        Assert.assertTrue(map.isCreated());

        map.loadSheets(new ArrayList<SpriteTiled>());
        Assert.assertEquals(16, map.getTileWidth());
        Assert.assertEquals(32, map.getTileHeight());
        Assert.assertEquals(2 * 16, map.getWidth());
        Assert.assertEquals(3 * 32, map.getHeight());
        Assert.assertEquals(2, map.getInTileWidth());
        Assert.assertEquals(3, map.getInTileHeight());
        Assert.assertEquals((int) Math.ceil(StrictMath.sqrt(2.0 * 2.0 + 3.0 * 3.0)), map.getInTileRadius());

        final Tile tile = map.createTile(Integer.valueOf(1), 2, 16.0, 32.0);
        Assert.assertEquals(1, tile.getSheet().intValue());
        Assert.assertEquals(2, tile.getNumber());
        Assert.assertEquals(16.0, tile.getX(), UtilTests.PRECISION);
        Assert.assertEquals(32.0, tile.getY(), UtilTests.PRECISION);
        Assert.assertEquals(1, tile.getInTileX());
        Assert.assertEquals(1, tile.getInTileY());
        Assert.assertEquals(16, tile.getWidth());
        Assert.assertEquals(32, tile.getHeight());
        Assert.assertEquals(1, tile.getInTileWidth());
        Assert.assertEquals(1, tile.getInTileHeight());
    }

    /**
     * Test map creation with a wrong tile width.
     */
    @Test(expected = LionEngineException.class)
    public void testCreateWrongTileWidth()
    {
        map.create(0, 1, 1, 1);
    }

    /**
     * Test map creation with a wrong tile height.
     */
    @Test(expected = LionEngineException.class)
    public void testCreateWrongTileHeight()
    {
        map.create(1, 0, 1, 1);
    }

    /**
     * Test map creation with a wrong width.
     */
    @Test(expected = LionEngineException.class)
    public void testCreateWrongWidth()
    {
        map.create(1, 1, 0, 1);
    }

    /**
     * Test map creation with a wrong height.
     */
    @Test(expected = LionEngineException.class)
    public void testCreateWrongHeight()
    {
        map.create(1, 1, 1, 0);
    }

    /**
     * Test map create tile not initialized.
     */
    @Test(expected = LionEngineException.class)
    public void testCreateTileError()
    {
        Assert.assertNull(map.createTile(Integer.valueOf(0), 0, 0.0, 0.0));
    }

    /**
     * Test map set and get tile.
     */
    @Test
    public void testSetGetTile()
    {
        map.create(16, 16, 3, 3);
        map.loadSheets(new ArrayList<SpriteTiled>());

        Assert.assertEquals(0, map.getTilesNumber());
        Assert.assertNull(map.getTile(0, 0));
        Assert.assertNull(map.getTileAt(51.0, 68.0));

        map.setTile(map.createTile(Integer.valueOf(0), 0, 0.0, 0.0));

        Assert.assertEquals(1, map.getTilesNumber());
        Assert.assertNotNull(map.getTile(0, 0));
        Assert.assertNotNull(map.getTileAt(3.0, 6.0));
    }
}
