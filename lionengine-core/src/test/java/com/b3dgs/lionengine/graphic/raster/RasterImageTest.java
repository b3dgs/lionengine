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
package com.b3dgs.lionengine.graphic.raster;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.io.File;
import java.util.Collections;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.EngineMock;
import com.b3dgs.lionengine.FactoryMediaDefault;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.UtilFolder;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBufferMock;
import com.b3dgs.lionengine.graphic.ImageFormat;
import com.b3dgs.lionengine.graphic.drawable.ImageInfo;

/**
 * Test {@link RasterImage}.
 */
final class RasterImageTest
{
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(RasterImageTest.class);

    /**
     * Start engine.
     */
    @BeforeAll
    static void beforeAll()
    {
        Engine.start(new EngineMock(RasterImageTest.class.getSimpleName(), new Version(1, 0, 0)));

        Medias.setFactoryMedia(new FactoryMediaDefault());
        Medias.setLoadFromJar(RasterImageTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Terminate engine.
     */
    @AfterAll
    static void afterAll()
    {
        try
        {
            UtilFile.deleteFile(new File(System.getProperty("java.io.tmpdir"), RasterImageTest.class.getSimpleName()));
        }
        catch (final LionEngineException exception)
        {
            LOGGER.error("afterAll", exception);
        }

        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);

        Engine.terminate();
    }

    /**
     * Test default with palette.
     */
    @Test
    void testDefaultPalette()
    {
        final Media mediaRaster = Medias.create("raster.png");
        final int rastersNumber = ImageInfo.get(mediaRaster).getHeight() - 1;
        final RasterImage raster = new RasterImage(new ImageBufferMock(128, 64), mediaRaster, 16);
        raster.loadRasters(false, Constant.EMPTY_STRING);

        assertEquals(rastersNumber, raster.getRasters().size());
        assertEquals(mediaRaster, raster.getFile());
        assertEquals(16, raster.getHeight());
        assertEquals(128, raster.getRaster(0).getWidth());
        assertEquals(64, raster.getRaster(0).getHeight());
    }

    /**
     * Test specific with palette.
     */
    @Test
    void testSpecificPalette()
    {
        final Media mediaRaster = Medias.create("raster.png");
        final int rastersNumber = ImageInfo.get(mediaRaster).getHeight() - 1;
        final RasterImage raster = new RasterImage(new ImageBufferMock(128, 64), mediaRaster, 16);
        raster.loadRasters(false, mediaRaster, Constant.EMPTY_STRING, Collections.emptyList());

        assertEquals(rastersNumber, raster.getRasters().size());
        assertEquals(mediaRaster, raster.getFile());
        assertEquals(16, raster.getHeight());
        assertEquals(128, raster.getRaster(0).getWidth());
        assertEquals(64, raster.getRaster(0).getHeight());
    }

    /**
     * Test save.
     */
    @Test
    void testSavePalette()
    {
        final Media mediaRaster = Medias.create("raster.png");
        final int rastersNumber = ImageInfo.get(mediaRaster).getHeight() - 1;
        final RasterImage raster = new RasterImage(new ImageBufferMock(128, 64), mediaRaster, 16);
        raster.loadRasters(true, "save");

        assertEquals(rastersNumber, raster.getRasters().size());
        assertEquals(mediaRaster, raster.getFile());
        assertEquals(16, raster.getHeight());
        assertEquals(128, raster.getRaster(0).getWidth());
        assertEquals(64, raster.getRaster(0).getHeight());

        final Media folder = Medias.create("raster_save");

        assertTrue(folder.exists(), folder.getFile().getAbsolutePath());

        try
        {
            for (int i = 0; i < rastersNumber; i++)
            {
                final Media file = Medias.create("raster_save", i + Constant.DOT + ImageFormat.PNG);

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
        final int rastersNumber = ImageInfo.get(mediaRaster).getHeight() - 1;
        final RasterImage raster = new RasterImage(Medias.create("image.png"), mediaRaster, 16);
        raster.loadRasters(true, "cache");

        assertEquals(rastersNumber, raster.getRasters().size());
        assertEquals(mediaRaster, raster.getFile());
        assertEquals(16, raster.getHeight());
        assertEquals(64, raster.getRaster(0).getWidth());
        assertEquals(32, raster.getRaster(0).getHeight());

        final Media folder = Medias.create("raster_cache");

        assertTrue(folder.exists(), folder.getFile().getAbsolutePath());

        try
        {
            for (int i = 0; i < rastersNumber; i++)
            {
                final Media file = Medias.create("raster_cache", i + Constant.DOT + ImageFormat.PNG);

                assertTrue(file.exists(), file.getFile().getAbsolutePath());
            }

            final RasterImage cache = new RasterImage(new ImageBufferMock(100, 200), mediaRaster, 100);
            cache.loadRasters(false, "cache");

            assertEquals(rastersNumber, cache.getRasters().size());
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
