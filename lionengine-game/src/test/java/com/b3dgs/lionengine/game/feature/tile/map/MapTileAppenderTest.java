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
package com.b3dgs.lionengine.game.feature.tile.map;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.feature.tile.TileGame;

/**
 * Test {@link MapTileAppenderModel}.
 */
public final class MapTileAppenderTest
{
    private final Services services = new Services();
    private final MapTileGame map = services.add(new MapTileGame());
    private final MapTileAppender appender = map.addFeatureAndGet(new MapTileAppenderModel(services));

    /**
     * Test the map append when map is not created.
     */
    @Test
    public void testAppendNotCreated()
    {
        final MapTile map1 = new MapTileGame();
        map1.create(1, 1, 1, 1);
        map1.setTile(new TileGame(Integer.valueOf(0), 0, 0, 0, 1, 1));

        Assert.assertEquals(0, map.getInTileWidth());
        Assert.assertEquals(0, map.getInTileHeight());
        Assert.assertEquals(0, map.getTilesNumber());

        appender.append(map1, 0, 0);

        Assert.assertEquals(1, map.getInTileWidth());
        Assert.assertEquals(1, map.getInTileHeight());
        Assert.assertEquals(1, map.getTilesNumber());
    }

    /**
     * Test the map append when map is created with same tile size.
     */
    @Test
    public void testAppendCreatedSameTileSize()
    {
        map.create(1, 1, 1, 1);

        final MapTile map1 = new MapTileGame();
        map1.create(1, 1, 1, 1);
        map1.setTile(new TileGame(Integer.valueOf(0), 0, 0, 0, 1, 1));

        Assert.assertEquals(1, map.getInTileWidth());
        Assert.assertEquals(1, map.getInTileHeight());
        Assert.assertEquals(0, map.getTilesNumber());

        appender.append(map1, 0, 0);

        Assert.assertEquals(1, map.getInTileWidth());
        Assert.assertEquals(1, map.getInTileHeight());
        Assert.assertEquals(1, map.getTilesNumber());
    }

    /**
     * Test the map append when map is created with different tile width.
     */
    @Test(expected = LionEngineException.class)
    public void testAppendCreatedDifferentTileWidth()
    {
        map.create(2, 1, 1, 1);

        final MapTile map1 = new MapTileGame();
        map1.create(1, 1, 1, 1);
        map1.setTile(new TileGame(Integer.valueOf(0), 0, 0, 0, 1, 1));

        appender.append(map1, 0, 0);
    }

    /**
     * Test the map append when map is created with different tile height.
     */
    @Test(expected = LionEngineException.class)
    public void testAppendCreatedDifferentTileHeight()
    {
        map.create(1, 2, 1, 1);

        final MapTile map1 = new MapTileGame();
        map1.create(1, 1, 1, 1);
        map1.setTile(new TileGame(Integer.valueOf(0), 0, 0, 0, 1, 1));

        appender.append(map1, 0, 0);
    }

    /**
     * Test the map collection append when map is not created.
     */
    @Test
    public void testAppendCollectionNotCreated()
    {
        final MapTile map1 = new MapTileGame();
        map1.create(1, 1, 1, 1);
        map1.setTile(new TileGame(Integer.valueOf(0), 0, 0, 0, 1, 1));

        Assert.assertEquals(0, map.getInTileWidth());
        Assert.assertEquals(0, map.getInTileHeight());
        Assert.assertEquals(0, map.getTilesNumber());

        appender.append(Arrays.asList(map1, map1), 0, 0, 0, 0);

        Assert.assertEquals(2, map.getInTileWidth());
        Assert.assertEquals(2, map.getInTileHeight());
        Assert.assertEquals(1, map.getTilesNumber());
    }

    /**
     * Test the map collection append when map is created.
     */
    @Test
    public void testAppendCollectionCreated()
    {
        map.create(1, 1, 1, 1);

        final MapTile map1 = new MapTileGame();
        map1.create(1, 1, 2, 2);
        map1.setTile(new TileGame(Integer.valueOf(0), 0, 0, 0, 1, 1));

        Assert.assertEquals(1, map.getInTileWidth());
        Assert.assertEquals(1, map.getInTileHeight());
        Assert.assertEquals(0, map.getTilesNumber());

        appender.append(Arrays.asList(map1, map1), 0, 0, 0, 0);

        Assert.assertEquals(5, map.getInTileWidth());
        Assert.assertEquals(5, map.getInTileHeight());
        Assert.assertEquals(1, map.getTilesNumber());
    }

    /**
     * Test the map collection append when map is created with different tile width.
     */
    @Test(expected = LionEngineException.class)
    public void testAppendCollectionCreatedDifferentTileWidth()
    {
        map.create(1, 1, 1, 1);

        final MapTile map1 = new MapTileGame();
        map1.create(1, 1, 1, 1);
        map1.setTile(new TileGame(Integer.valueOf(0), 0, 0, 0, 1, 1));

        final MapTile map2 = new MapTileGame();
        map2.create(2, 1, 1, 1);
        appender.append(Arrays.asList(map1, map2), 0, 0, 0, 0);
    }

    /**
     * Test the map collection append when map is created with different tile height.
     */
    @Test(expected = LionEngineException.class)
    public void testAppendCollectionCreatedDifferentTileHeight()
    {
        map.create(1, 1, 1, 1);

        final MapTile map1 = new MapTileGame();
        map1.create(1, 1, 1, 1);
        map1.setTile(new TileGame(Integer.valueOf(0), 0, 0, 0, 1, 1));

        final MapTile map2 = new MapTileGame();
        map2.create(1, 2, 1, 1);
        appender.append(Arrays.asList(map1, map2), 0, 0, 0, 0);
    }
}
