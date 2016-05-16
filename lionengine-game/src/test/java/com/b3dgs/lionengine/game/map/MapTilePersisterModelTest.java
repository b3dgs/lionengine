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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.handler.Services;
import com.b3dgs.lionengine.game.map.feature.persister.MapTilePersister;
import com.b3dgs.lionengine.game.map.feature.persister.MapTilePersisterModel;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionengine.stream.Stream;
import com.b3dgs.lionengine.test.UtilTests;
import com.b3dgs.lionengine.util.UtilStream;

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

    /**
     * Create a test map.
     * 
     * @return The created test map.
     */
    private static MapTile createMap()
    {
        final Services services = new Services();
        final MapTileGame map = new MapTileGame(services);
        map.addFeature(new MapTilePersisterModel(map));
        map.prepareFeatures(map, services);
        map.create(16, 32, 3, 3);
        map.loadSheets(new ArrayList<SpriteTiled>());

        final Integer sheet = Integer.valueOf(0);
        for (int x = 0; x < map.getInTileWidth(); x++)
        {
            for (int y = 1; y < map.getInTileHeight(); y++)
            {
                final Tile tile = map.createTile(sheet,
                                                 x * y,
                                                 x * (double) map.getTileWidth(),
                                                 y * (double) map.getTileHeight());
                map.setTile(tile);
            }
        }
        return map;
    }

    /**
     * Save map to file.
     * 
     * @param map The map to save.
     * @param level The level media.
     * @throws IOException If error.
     */
    private static void saveMap(MapTile map, Media level) throws IOException
    {
        FileWriting output = null;
        try
        {
            output = Stream.createFileWriting(level);
            map.getFeature(MapTilePersister.class).save(output);
        }
        finally
        {
            UtilStream.safeClose(output);
        }
    }

    /**
     * Load map from file.
     * 
     * @param level The level media.
     * @return The loaded map.
     * @throws IOException If error.
     */
    private static MapTileGame loadMap(Media level) throws IOException
    {
        final Services services = new Services();
        final MapTileGame map = new MapTileGame(services);
        map.addFeature(new MapTilePersisterModel(map));
        map.prepareFeatures(map, services);
        FileReading input = null;
        try
        {
            input = Stream.createFileReading(level);
            map.getFeature(MapTilePersister.class).load(input);
        }
        finally
        {
            UtilStream.safeClose(input);
        }
        return map;
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
        final MapTile map = createMap();

        Assert.assertEquals(map.getInTileWidth() * (map.getInTileHeight() - 1), map.getTilesNumber());

        final Media level = Medias.get(file);
        saveMap(map, level);
        final MapTileGame mapLoaded = loadMap(level);

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
        final MapTile map = new MapTileGame(services);
        map.addFeature(new MapTilePersisterModel(map));
        map.prepareFeatures(map, services);
        map.create(16, 32, 3, 3);
        map.loadSheets(config);

        final File levelFile = folder.newFile();
        final Media level = Medias.get(levelFile);
        saveMap(map, level);
        final MapTile mapLoaded = loadMap(level);

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
        final MapTile map = createMap();
        final File levelFile = folder.newFile();
        final Media level = Medias.get(levelFile);
        map.setTile(map.createTile(Integer.valueOf(Integer.MAX_VALUE), 0, 0, 0));
        saveMap(map, level);

        try
        {
            final MapTile mapLoaded = loadMap(level);
            Assert.assertNull(mapLoaded);
        }
        catch (final IOException exception)
        {
            // Success
        }

        Assert.assertTrue(levelFile.delete());
    }

    /**
     * Test the constructor with services without map.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructorServicesMissing()
    {
        Assert.assertNull(new MapTilePersisterModel(new Services()));
    }

    /**
     * Test the constructor with services with map.
     */
    @Test
    public void testConstructorServices()
    {
        final Services services = new Services();
        services.add(new MapTileGame(services));
        Assert.assertNotNull(new MapTilePersisterModel(services));
    }
}
