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
package com.b3dgs.lionengine.game.feature.tile.map;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.graphic.ColorRgba;

/**
 * Test {@link MinimapConfig}.
 */
final class MinimapConfigTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void cleanUp()
    {
        Medias.setResourcesDirectory(null);
    }

    /**
     * Test constructor.
     */
    @Test
    void testConstructor()
    {
        assertPrivateConstructor(MinimapConfig.class);
    }

    /**
     * Test exports imports.
     */
    @Test
    void testExportsImports()
    {
        final Map<Integer, ColorRgba> tiles = new HashMap<>();
        tiles.put(Integer.valueOf(0), ColorRgba.RED);
        tiles.put(Integer.valueOf(1), ColorRgba.BLUE);
        tiles.put(Integer.valueOf(0), ColorRgba.GREEN);
        tiles.put(Integer.valueOf(1), ColorRgba.GREEN);

        final Media config = Medias.create("minimap.xml");
        MinimapConfig.exports(config, tiles);

        assertEquals(tiles, MinimapConfig.imports(config));

        assertTrue(config.getFile().delete());
    }
}
