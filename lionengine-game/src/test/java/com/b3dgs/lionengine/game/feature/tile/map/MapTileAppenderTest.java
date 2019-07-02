/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

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
     * Test constructor with null services.
     */
    @Test
    public void testConstructorNullServices()
    {
        assertThrows(() -> new MapTileAppenderModel(null), "Unexpected null argument !");
    }

    /**
     * Test the map append when map is not created.
     */
    @Test
    public void testAppendNotCreated()
    {
        final MapTile map1 = new MapTileGame();
        map1.create(1, 1, 1, 1);
        map1.setTile(new TileGame(Integer.valueOf(0), 0, 0, 0, 1, 1));

        assertEquals(0, map.getInTileWidth());
        assertEquals(0, map.getInTileHeight());
        assertEquals(0, map.getTilesNumber());

        appender.append(map1, 0, 0);

        assertEquals(1, map.getInTileWidth());
        assertEquals(1, map.getInTileHeight());
        assertEquals(1, map.getTilesNumber());
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

        assertEquals(1, map.getInTileWidth());
        assertEquals(1, map.getInTileHeight());
        assertEquals(0, map.getTilesNumber());

        appender.append(map1, 0, 0);

        assertEquals(1, map.getInTileWidth());
        assertEquals(1, map.getInTileHeight());
        assertEquals(1, map.getTilesNumber());
    }

    /**
     * Test the map append when map is created with different tile width.
     */
    @Test
    public void testAppendCreatedDifferentTileWidth()
    {
        map.create(2, 1, 1, 1);

        final MapTile map1 = new MapTileGame();
        map1.create(1, 1, 1, 1);
        map1.setTile(new TileGame(Integer.valueOf(0), 0, 0, 0, 1, 1));

        assertThrows(() -> appender.append(map1, 0, 0), MapTileAppenderModel.ERROR_APPEND_MAP_TILE_SIZE + "1 1");
    }

    /**
     * Test the map append when map is created with different tile height.
     */
    @Test
    public void testAppendCreatedDifferentTileHeight()
    {
        map.create(1, 2, 1, 1);

        final MapTile map1 = new MapTileGame();
        map1.create(1, 1, 1, 1);
        map1.setTile(new TileGame(Integer.valueOf(0), 0, 0, 0, 1, 1));

        assertThrows(() -> appender.append(map1, 0, 0), MapTileAppenderModel.ERROR_APPEND_MAP_TILE_SIZE + "1 2");
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

        assertEquals(0, map.getInTileWidth());
        assertEquals(0, map.getInTileHeight());
        assertEquals(0, map.getTilesNumber());

        appender.append(Arrays.asList(map1, map1), 0, 0, 0, 0);

        assertEquals(2, map.getInTileWidth());
        assertEquals(2, map.getInTileHeight());
        assertEquals(1, map.getTilesNumber());
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

        assertEquals(1, map.getInTileWidth());
        assertEquals(1, map.getInTileHeight());
        assertEquals(0, map.getTilesNumber());

        appender.append(Arrays.asList(map1, map1), 0, 0, 0, 0);

        assertEquals(5, map.getInTileWidth());
        assertEquals(5, map.getInTileHeight());
        assertEquals(1, map.getTilesNumber());
    }

    /**
     * Test the map collection append when map is created with different tile width.
     */
    @Test
    public void testAppendCollectionCreatedDifferentTileWidth()
    {
        map.create(1, 1, 1, 1);

        final MapTile map1 = new MapTileGame();
        map1.create(1, 1, 1, 1);
        map1.setTile(new TileGame(Integer.valueOf(0), 0, 0, 0, 1, 1));

        final MapTile map2 = new MapTileGame();
        map2.create(2, 1, 1, 1);

        assertThrows(() -> appender.append(Arrays.asList(map1, map2), 0, 0, 0, 0),
                     MapTileAppenderModel.ERROR_APPEND_MAP_TILE_SIZE + "2 1");
    }

    /**
     * Test the map collection append when map is created with different tile height.
     */
    @Test
    public void testAppendCollectionCreatedDifferentTileHeight()
    {
        map.create(1, 1, 1, 1);

        final MapTile map1 = new MapTileGame();
        map1.create(1, 1, 1, 1);
        map1.setTile(new TileGame(Integer.valueOf(0), 0, 0, 0, 1, 1));

        final MapTile map2 = new MapTileGame();
        map2.create(1, 2, 1, 1);

        assertThrows(() -> appender.append(Arrays.asList(map1, map2), 0, 0, 0, 0),
                     MapTileAppenderModel.ERROR_APPEND_MAP_TILE_SIZE + "1 2");
    }
}
