/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.persister;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileGame;
import com.b3dgs.lionengine.game.feature.tile.map.TileSheetsConfig;
import com.b3dgs.lionengine.io.FileReading;

/**
 * Test {@link MapTilePersisterModel}.
 */
final class MapTilePersisterModelTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setResourcesDirectory(null);
    }

    /**
     * Test the save and load map from file.
     * 
     * @throws IOException If error.
     */
    @Test
    void testSaveLoad() throws IOException
    {
        final MapTile map = UtilMapTilePersister.createMap();

        assertEquals(map.getInTileWidth() * (map.getInTileHeight() - 1), map.getTilesNumber());

        final Media level = Medias.create("level");
        UtilMapTilePersister.saveMap(map, level);
        final MapTile mapLoaded = UtilMapTilePersister.loadMap(level);

        assertEquals(map.getTileWidth(), mapLoaded.getTileWidth());
        assertEquals(map.getTileHeight(), mapLoaded.getTileHeight());
        assertEquals(map.getInTileWidth(), mapLoaded.getInTileWidth());
        assertEquals(map.getInTileHeight(), mapLoaded.getInTileHeight());
        assertEquals(map.getWidth(), mapLoaded.getWidth());
        assertEquals(map.getHeight(), mapLoaded.getHeight());
        for (int x = 0; x < mapLoaded.getInTileWidth(); x++)
        {
            for (int y = 0; y < mapLoaded.getInTileHeight(); y++)
            {
                final Tile tile = mapLoaded.getTile(x, y);
                if (y == 0)
                {
                    assertNull(tile);
                }
                else
                {
                    assertNotNull(tile);
                    assertEquals(x * y, tile.getNumber());
                    assertEquals(x * mapLoaded.getTileWidth(), tile.getX());
                    assertEquals(y * mapLoaded.getTileHeight(), tile.getY());
                }
            }
        }
        assertEquals(map.getTilesNumber(), mapLoaded.getTilesNumber());
        assertTrue(level.getFile().delete());
    }

    /**
     * Test the save and load map from file with sheet config.
     * 
     * @throws IOException If error.
     */
    @Test
    void testSaveLoadWithConfig() throws IOException
    {
        final Media config = Medias.create("config.xml");

        TileSheetsConfig.exports(config, 16, 32, new ArrayList<String>());

        final MapTileGame map = new MapTileGame();
        map.addFeature(new MapTilePersisterModel());
        map.create(16, 32, 3, 3);
        map.loadSheets(config);

        final Media level = Medias.create("level");
        UtilMapTilePersister.saveMap(map, level);
        final MapTileGame mapLoaded = UtilMapTilePersister.loadMap(level);
        mapLoaded.loadSheets(config);

        assertEquals(config, mapLoaded.getMedia());
        assertTrue(config.getFile().delete());
        assertTrue(level.getFile().delete());
    }

    /**
     * Test load listener.
     * 
     * @throws IOException If error.
     */
    @Test
    void testListener() throws IOException
    {
        final MapTileGame map = new MapTileGame();
        final MapTilePersister mapPersister = map.addFeature(new MapTilePersisterModel());
        final Media level = Medias.create("level");
        map.create(16, 32, 3, 3);
        UtilMapTilePersister.saveMap(map, level);

        final AtomicBoolean load = new AtomicBoolean();
        final MapTilePersisterListener listener = new MapTilePersisterListener()
        {
            @Override
            public void notifyMapLoadStart()
            {
                load.set(false);
            }

            @Override
            public void notifyMapLoaded()
            {
                load.set(true);
            }
        };
        mapPersister.addListener(listener);

        try (FileReading input = new FileReading(level))
        {
            mapPersister.load(input);
        }

        assertTrue(load.get());

        load.set(false);
        mapPersister.removeListener(listener);

        try (FileReading input = new FileReading(level))
        {
            mapPersister.load(input);
        }

        assertFalse(load.get());
        assertTrue(level.getFile().delete());
    }

    /**
     * Test the constructor with services with map.
     */
    @Test
    void testConstructor()
    {
        final MapTile map = new MapTileGame();
        final MapTilePersister mapPersister = new MapTilePersisterModel();

        assertNotNull(mapPersister);

        mapPersister.prepare(map);
    }
}
