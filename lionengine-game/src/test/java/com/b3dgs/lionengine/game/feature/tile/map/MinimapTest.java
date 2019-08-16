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

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.ViewerMock;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBufferMock;
import com.b3dgs.lionengine.graphic.drawable.Drawable;

/**
 * Test {@link Minimap}.
 */
public final class MinimapTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setResourcesDirectory(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test the minimap.
     */
    @Test
    public void testMinimap()
    {
        final MapTileGame map = new MapTileGame();
        map.loadSheets(Arrays.asList(Drawable.loadSpriteTiled(new ImageBufferMock(80, 80), 40, 40)));
        map.create(40, 40, 3, 3);
        map.setTile(0, 0, 0);

        final Minimap minimap = new Minimap(map);
        final Media config = Medias.create("minimap.xml");
        final Map<Integer, ColorRgba> tiles = new HashMap<>();
        tiles.put(Integer.valueOf(0), ColorRgba.RED);
        tiles.put(Integer.valueOf(1), ColorRgba.BLUE);
        tiles.put(Integer.valueOf(0), ColorRgba.GREEN);

        minimap.automaticColor(config);
        minimap.loadPixelConfig(config);
        minimap.setOrigin(Origin.BOTTOM_LEFT);

        assertEquals(0.0, minimap.getX());
        assertEquals(0.0, minimap.getY());
        assertNull(minimap.getSurface());
        assertFalse(minimap.isLoaded());

        minimap.load();
        minimap.prepare();
        minimap.setLocation(1.0, 2.0);
        minimap.load();

        assertTrue(minimap.isLoaded());
        assertEquals(1.0, minimap.getX());
        assertEquals(-1.0, minimap.getY());
        assertEquals(3, minimap.getWidth());
        assertEquals(3, minimap.getHeight());

        minimap.setLocation(new ViewerMock(), Geom.createLocalizable(2.0, 3.0));

        assertEquals(2.0, minimap.getX());
        assertEquals(234.0, minimap.getY());

        minimap.dispose();
    }

    /**
     * Test the minimap with wrong prepare.
     */
    @Test
    public void testNullPrepare()
    {
        final Minimap minimap = new Minimap(new MapTileGame());

        assertThrows(() -> minimap.prepare(), Minimap.ERROR_SURFACE);
    }
}
