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
package com.b3dgs.lionengine.game.map.feature.persister;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.map.MapTileGame;
import com.b3dgs.lionengine.game.map.TileSheetsConfig;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the map tile default implementation.
 */
public class MapTilePersisterModelTest
{
    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(null);
    }

    /** Temp folder. */
    @Rule public final TemporaryFolder folder = new TemporaryFolder();

    /**
     * Prepare test.
     */
    @Before
    public void prepare()
    {
        Medias.setResourcesDirectory(folder.getRoot().getAbsolutePath());
    }

    /**
     * Test the save and load map from file.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testSaveLoad() throws IOException
    {
        final File file = folder.newFile();
        final MapTile map = UtilMapTilePersister.createMap();

        Assert.assertEquals(map.getInTileWidth() * (map.getInTileHeight() - 1), map.getTilesNumber());

        final Media level = Medias.get(file);
        UtilMapTilePersister.saveMap(map, level);
        final MapTile mapLoaded = UtilMapTilePersister.loadMap(level);

        Assert.assertEquals(map.getTileWidth(), mapLoaded.getTileWidth());
        Assert.assertEquals(map.getTileHeight(), mapLoaded.getTileHeight());
        Assert.assertEquals(map.getInTileWidth(), mapLoaded.getInTileWidth());
        Assert.assertEquals(map.getInTileHeight(), mapLoaded.getInTileHeight());
        Assert.assertEquals(map.getWidth(), mapLoaded.getWidth());
        Assert.assertEquals(map.getHeight(), mapLoaded.getHeight());
        for (int x = 0; x < mapLoaded.getInTileWidth(); x++)
        {
            for (int y = 0; y < mapLoaded.getInTileHeight(); y++)
            {
                final Tile tile = mapLoaded.getTile(x, y);
                if (y == 0)
                {
                    Assert.assertNull(tile);
                }
                else
                {
                    Assert.assertNotNull(tile);
                    Assert.assertEquals(0, tile.getSheet().intValue());
                    Assert.assertEquals(x * y, tile.getNumber());
                    Assert.assertEquals(x * mapLoaded.getTileWidth(), tile.getX(), UtilTests.PRECISION);
                    Assert.assertEquals(y * mapLoaded.getTileHeight(), tile.getY(), UtilTests.PRECISION);
                }
            }
        }
        Assert.assertEquals(map.getTilesNumber(), mapLoaded.getTilesNumber());
        Assert.assertTrue(file.delete());
    }

    /**
     * Test the save and load map from file with sheet config.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testSaveLoadWithConfig() throws IOException
    {
        final File fileConfig = folder.newFile();
        Medias.setResourcesDirectory(folder.getRoot().getAbsolutePath());
        final Media config = Medias.get(fileConfig);

        TileSheetsConfig.exports(config, 16, 32, new ArrayList<String>());

        final Services services = new Services();
        final MapTile map = services.create(MapTileGame.class);
        map.addFeature(new MapTilePersisterModel());
        map.prepareFeatures(services);
        map.create(16, 32, 3, 3);
        map.loadSheets(config);

        final File levelFile = folder.newFile();
        final Media level = Medias.get(levelFile);
        UtilMapTilePersister.saveMap(map, level);
        final MapTile mapLoaded = UtilMapTilePersister.loadMap(level);

        Assert.assertEquals(config, mapLoaded.getSheetsConfig());

        Assert.assertTrue(fileConfig.delete());
        Assert.assertTrue(levelFile.delete());
    }

    /**
     * Test the save and load map from file with invalid tile sheet number.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testInvalidSheet() throws IOException
    {
        final MapTile map = UtilMapTilePersister.createMap();
        final File levelFile = folder.newFile();
        final Media level = Medias.get(levelFile);
        map.setTile(map.createTile(Integer.valueOf(Integer.MAX_VALUE), 0, 0, 0));
        UtilMapTilePersister.saveMap(map, level);

        try
        {
            final MapTile mapLoaded = UtilMapTilePersister.loadMap(level);
            Assert.assertNull(mapLoaded);
        }
        catch (final IOException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }

        Assert.assertTrue(levelFile.delete());
    }

    /**
     * Test the constructor with services with map.
     */
    @Test
    public void testConstructor()
    {
        final Services services = new Services();
        final MapTile map = services.create(MapTileGame.class);
        final MapTilePersister mapPersister = new MapTilePersisterModel();

        Assert.assertNotNull(mapPersister);
        mapPersister.prepare(map, services);
    }
}
