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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;

/**
 * Test the tile sheets configuration class.
 */
public class TileSheetsConfigTest
{
    /** Test configuration. */
    private static Media media;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setResourcesDirectory(System.getProperty("java.io.tmpdir"));
        media = Medias.create("sheets.xml");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Assert.assertTrue(media.getFile().delete());
        Medias.setResourcesDirectory(Constant.EMPTY_STRING);
    }

    /**
     * Test the configuration.
     */
    @Test
    public void testConfiguration()
    {
        TileSheetsConfig.exports(media, 16, 32, Arrays.asList("sheet"));
        final TileSheetsConfig config = TileSheetsConfig.imports(media);

        Assert.assertEquals(16, config.getTileWidth());
        Assert.assertEquals(32, config.getTileHeight());
        Assert.assertArrayEquals(Arrays.asList("sheet").toArray(), config.getSheets().toArray());
    }
}
