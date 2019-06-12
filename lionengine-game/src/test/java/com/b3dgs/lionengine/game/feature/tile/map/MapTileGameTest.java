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
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * Test {@link MapTileGame}.
 */
public final class MapTileGameTest
{
    private final MapTileGame map = new MapTileGame();

    /**
     * Test map creation.
     */
    @Test
    public void testCreate()
    {
        assertFalse(map.isCreated());

        map.create(16, 32, 2, 3);

        assertTrue(map.isCreated());

        map.loadSheets(new ArrayList<SpriteTiled>());

        assertEquals(16, map.getTileWidth());
        assertEquals(32, map.getTileHeight());
        assertEquals(2 * 16, map.getWidth());
        assertEquals(3 * 32, map.getHeight());
        assertEquals(2, map.getInTileWidth());
        assertEquals(3, map.getInTileHeight());
        assertEquals((int) Math.ceil(StrictMath.sqrt(2.0 * 2.0 + 3.0 * 3.0)), map.getInTileRadius());

        final Tile tile = map.createTile(Integer.valueOf(1), 2, 16.0, 32.0);

        assertEquals(1, tile.getSheet().intValue());
        assertEquals(2, tile.getNumber());
        assertEquals(16.0, tile.getX());
        assertEquals(32.0, tile.getY());
        assertEquals(1, tile.getInTileX());
        assertEquals(1, tile.getInTileY());
        assertEquals(16, tile.getWidth());
        assertEquals(32, tile.getHeight());
        assertEquals(1, tile.getInTileWidth());
        assertEquals(1, tile.getInTileHeight());
    }

    /**
     * Test map creation from level rip.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testCreateFromRip() throws IOException
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        try
        {
            final Path level = Files.createTempFile("level", ".png");
            try (InputStream input = MapTileGameTest.class.getResourceAsStream("level.png"))
            {
                Files.copy(input, level, StandardCopyOption.REPLACE_EXISTING);
            }

            final Path sheet = Files.createTempFile("sheet", ".png");
            try (InputStream input = MapTileGameTest.class.getResourceAsStream("sheet.png"))
            {
                Files.copy(input, sheet, StandardCopyOption.REPLACE_EXISTING);
            }

            final Media sheets = Medias.create("sheets.xml");
            TileSheetsConfig.exports(sheets, 7, 11, Arrays.asList(sheet.toFile().getName()));

            map.create(Medias.create(level.toFile().getName()), sheets);

            assertTrue(map.isCreated());
            assertEquals(7, map.getTileWidth());
            assertEquals(11, map.getTileHeight());
            assertEquals(2 * 7, map.getWidth());
            assertEquals(2 * 11, map.getHeight());

            assertTrue(sheets.getFile().delete());
            Files.delete(level);
            Files.delete(sheet);
        }
        finally
        {
            Medias.setResourcesDirectory(null);
            Graphics.setFactoryGraphic(null);
        }
    }

    /**
     * Test map creation from level rip without sheets.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testCreateFromRipWithoutSheet() throws IOException
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        try
        {
            final Path level = Files.createTempFile("level", ".png");
            try (InputStream input = MapTileGameTest.class.getResourceAsStream("level.png"))
            {
                Files.copy(input, level, StandardCopyOption.REPLACE_EXISTING);
            }

            final Path sheet = Files.createTempFile("sheet", ".png");
            try (InputStream input = MapTileGameTest.class.getResourceAsStream("sheet.png"))
            {
                Files.copy(input, sheet, StandardCopyOption.REPLACE_EXISTING);
            }

            map.create(Medias.create(level.toFile().getName()), 7, 11, 7);

            assertTrue(map.isCreated());
            assertEquals(7, map.getTileWidth());
            assertEquals(11, map.getTileHeight());
            assertEquals(2 * 7, map.getWidth());
            assertEquals(2 * 11, map.getHeight());

            Files.delete(level);
            Files.delete(sheet);
        }
        finally
        {
            Medias.setResourcesDirectory(null);
            Graphics.setFactoryGraphic(null);
        }
    }

    /**
     * Test map creation with a wrong tile width.
     */
    @Test
    public void testCreateWrongTileWidth()
    {
        assertThrows(() -> map.create(0, 1, 1, 1), "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test map creation with a wrong tile height.
     */
    @Test
    public void testCreateWrongTileHeight()
    {
        assertThrows(() -> map.create(1, 0, 1, 1), "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test map creation with a wrong width.
     */
    @Test
    public void testCreateWrongWidth()
    {
        assertThrows(() -> map.create(1, 1, 0, 1), "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test map creation with a wrong height.
     */
    @Test
    public void testCreateWrongHeight()
    {
        assertThrows(() -> map.create(1, 1, 1, 0), "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test map create tile not initialized.
     */
    @Test
    public void testCreateTileError()
    {
        assertThrows(() -> map.createTile(Integer.valueOf(0), 0, 0.0, 0.0),
                     "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test the map clearing.
     */
    @Test
    public void testClear()
    {
        map.create(16, 16, 2, 2);
        final Tile tile = map.createTile(Integer.valueOf(0), 0, 0, 0);
        map.setTile(tile);

        assertEquals(tile, map.getTile(0, 0));

        map.clear();

        assertNull(map.getTile(0, 0));
    }

    /**
     * Test unknown sheet get.
     */
    @Test
    public void testGetUnknownSheet()
    {
        assertThrows(() -> map.getSheet(Integer.valueOf(1)), MapTileGame.ERROR_SHEET_MISSING + 1);
    }

    /**
     * Test map set and get tile.
     */
    @Test
    public void testSetGetTile()
    {
        map.create(16, 16, 3, 3);
        map.loadSheets(new ArrayList<SpriteTiled>());

        assertEquals(0, map.getTilesNumber());
        assertNull(map.getTile(0, 0));
        assertNull(map.getTileAt(51.0, 68.0));

        final Tile tile = map.createTile(Integer.valueOf(0), 0, 0.0, 0.0);
        map.setTile(tile);

        assertEquals(1, map.getTilesNumber());
        assertEquals(tile, map.getTile(0, 0));
        assertEquals(tile, map.getTile(Geom.createLocalizable(0, 0), 0, 0));
        assertEquals(tile, map.getTileAt(3.0, 6.0));
        assertEquals(Arrays.asList(tile), map.getTilesHit(-1, -1, 1, 1));
    }

    /**
     * Test map tile set listener.
     */
    @Test
    public void testTileSetListener()
    {
        map.create(16, 16, 3, 3);

        final AtomicReference<Tile> set = new AtomicReference<>();
        final TileSetListener listener = tile -> set.set(tile);
        map.addListener(listener);

        final Tile tile = map.createTile(Integer.valueOf(0), 0, 0.0, 0.0);
        map.setTile(tile);

        assertEquals(tile, set.get());

        set.set(null);
        map.removeListener(listener);

        map.setTile(tile);

        assertNull(set.get());
    }
}
