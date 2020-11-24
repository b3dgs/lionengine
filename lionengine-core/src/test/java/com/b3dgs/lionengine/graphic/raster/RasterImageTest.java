/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.raster;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.FactoryMediaDefault;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFolder;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBufferMock;
import com.b3dgs.lionengine.graphic.ImageFormat;

/**
 * Test {@link RasterImage}.
 */
final class RasterImageTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setFactoryMedia(new FactoryMediaDefault());
        Medias.setLoadFromJar(RasterImageTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test default.
     */
    @Test
    void testDefaultXml()
    {
        final Media mediaRaster = Medias.create("raster.xml");
        final RasterImage raster = new RasterImage(new ImageBufferMock(100, 200), mediaRaster, 100);
        raster.loadRasters();

        assertEquals(27, raster.getRasters().size());
        assertEquals(mediaRaster, raster.getFile());
        assertEquals(100, raster.getHeight());
        assertEquals(100, raster.getRaster(0).getWidth());
        assertEquals(200, raster.getRaster(0).getHeight());
    }

    /**
     * Test save.
     */
    @Test
    void testSaveXml()
    {
        final Media mediaRaster = Medias.create("raster.xml");
        final RasterImage raster = new RasterImage(new ImageBufferMock(100, 200), mediaRaster, 100);
        raster.loadRasters(true, "prefix");

        assertEquals(27, raster.getRasters().size());
        assertEquals(mediaRaster, raster.getFile());
        assertEquals(100, raster.getHeight());
        assertEquals(100, raster.getRaster(0).getWidth());
        assertEquals(200, raster.getRaster(0).getHeight());

        final Media folder = Medias.create("prefix_raster");

        assertTrue(folder.exists(), folder.getFile().getAbsolutePath());

        try
        {
            for (int i = 0; i < 27; i++)
            {
                final Media file = Medias.create("prefix_raster", i + Constant.DOT + ImageFormat.PNG);

                assertTrue(file.exists(), file.getFile().getAbsolutePath());
            }
        }
        finally
        {
            UtilFolder.deleteDirectory(folder.getFile());
        }
    }

    /**
     * Test cache with palette.
     */
    @Test
    void testCacheXml()
    {
        final Media mediaRaster = Medias.create("raster.xml");
        final RasterImage raster = new RasterImage(Medias.create("image.png"), mediaRaster, 100);
        raster.loadRasters(true, "cache");

        assertEquals(27, raster.getRasters().size());
        assertEquals(mediaRaster, raster.getFile());
        assertEquals(100, raster.getHeight());
        assertEquals(64, raster.getRaster(0).getWidth());
        assertEquals(32, raster.getRaster(0).getHeight());

        final Media folder = Medias.create("cache_raster");

        assertTrue(folder.exists(), folder.getFile().getAbsolutePath());

        try
        {
            for (int i = 0; i < 27; i++)
            {
                final Media file = Medias.create("cache_raster", i + Constant.DOT + ImageFormat.PNG);

                assertTrue(file.exists(), file.getFile().getAbsolutePath());
            }

            final RasterImage cache = new RasterImage(new ImageBufferMock(100, 200), mediaRaster, 100);
            cache.loadRasters(false, "cache");

            assertEquals(27, cache.getRasters().size());
            assertEquals(mediaRaster, cache.getFile());
            assertEquals(100, cache.getHeight());
            assertEquals(64, cache.getRaster(0).getWidth());
            assertEquals(32, cache.getRaster(0).getHeight());
        }
        finally
        {
            UtilFolder.deleteDirectory(folder.getFile());
        }
    }

    /**
     * Test default with palette.
     */
    @Test
    void testDefaultPalette()
    {
        final Media mediaRaster = Medias.create("raster.png");
        final RasterImage raster = new RasterImage(new ImageBufferMock(100, 200), mediaRaster, 100);
        raster.loadRasters();

        assertEquals(1, raster.getRasters().size());
        assertEquals(mediaRaster, raster.getFile());
        assertEquals(100, raster.getHeight());
        assertEquals(100, raster.getRaster(0).getWidth());
        assertEquals(200, raster.getRaster(0).getHeight());
    }

    /**
     * Test save.
     */
    @Test
    void testSavePalette()
    {
        final Media mediaRaster = Medias.create("raster.png");
        final RasterImage raster = new RasterImage(new ImageBufferMock(100, 200), mediaRaster, 100);
        raster.loadRasters(true, "prefix");

        assertEquals(1, raster.getRasters().size());
        assertEquals(mediaRaster, raster.getFile());
        assertEquals(100, raster.getHeight());
        assertEquals(100, raster.getRaster(0).getWidth());
        assertEquals(200, raster.getRaster(0).getHeight());

        final Media folder = Medias.create("prefix_raster");

        assertTrue(folder.exists(), folder.getFile().getAbsolutePath());

        try
        {
            for (int i = 0; i < 1; i++)
            {
                final Media file = Medias.create("prefix_raster", i + Constant.DOT + ImageFormat.PNG);

                assertTrue(file.exists(), file.getFile().getAbsolutePath());
            }
        }
        finally
        {
            UtilFolder.deleteDirectory(folder.getFile());
        }
    }

    /**
     * Test cache with palette.
     */
    @Test
    void testCachePalette()
    {
        final Media mediaRaster = Medias.create("raster.png");
        final RasterImage raster = new RasterImage(Medias.create("image.png"), mediaRaster, 100);
        raster.loadRasters(true, "cache");

        assertEquals(1, raster.getRasters().size());
        assertEquals(mediaRaster, raster.getFile());
        assertEquals(100, raster.getHeight());
        assertEquals(64, raster.getRaster(0).getWidth());
        assertEquals(32, raster.getRaster(0).getHeight());

        final Media folder = Medias.create("cache_raster");

        assertTrue(folder.exists(), folder.getFile().getAbsolutePath());

        try
        {
            for (int i = 0; i < 1; i++)
            {
                final Media file = Medias.create("cache_raster", i + Constant.DOT + ImageFormat.PNG);

                assertTrue(file.exists(), file.getFile().getAbsolutePath());
            }

            final RasterImage cache = new RasterImage(new ImageBufferMock(100, 200), mediaRaster, 100);
            cache.loadRasters(false, "cache");

            assertEquals(1, cache.getRasters().size());
            assertEquals(mediaRaster, cache.getFile());
            assertEquals(100, cache.getHeight());
            assertEquals(64, cache.getRaster(0).getWidth());
            assertEquals(32, cache.getRaster(0).getHeight());
        }
        finally
        {
            UtilFolder.deleteDirectory(folder.getFile());
        }
    }
}
