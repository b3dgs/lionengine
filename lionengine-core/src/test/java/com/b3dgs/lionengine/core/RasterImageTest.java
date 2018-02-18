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
package com.b3dgs.lionengine.core;

import java.io.IOException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.graphic.FactoryGraphicMock;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBufferMock;
import com.b3dgs.lionengine.graphic.ImageFormat;
import com.b3dgs.lionengine.util.UtilFolder;

/**
 * Test the raster
 */
public class RasterImageTest
{
    /** Raster. */
    private static Media mediaRaster;

    /**
     * Prepare test.
     * 
     * @throws IOException If error.
     */
    @BeforeClass
    public static void setUp() throws IOException
    {
        Medias.setLoadFromJar(RasterImageTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        mediaRaster = Medias.create("raster.xml");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test raster no smooth default.
     */
    @Test
    public void testLoadRastersNoSmoothDefault()
    {
        final RasterImage raster = new RasterImage(new ImageBufferMock(100, 200), mediaRaster, 100, false);
        raster.loadRasters(50);

        Assert.assertEquals(15, raster.getRasters().size());
        Assert.assertEquals(mediaRaster, raster.getFile());
        Assert.assertEquals(100, raster.getHeight());
        Assert.assertFalse(raster.hasSmooth());
        Assert.assertEquals(100, raster.getRaster(0).getWidth());
        Assert.assertEquals(200, raster.getRaster(0).getHeight());
    }

    /**
     * Test raster smooth default.
     */
    @Test
    public void testLoadRastersSmoothDefault()
    {
        final RasterImage raster = new RasterImage(new ImageBufferMock(100, 200), mediaRaster, 100, true);
        raster.loadRasters(50);

        Assert.assertEquals(30, raster.getRasters().size());
        Assert.assertEquals(mediaRaster, raster.getFile());
        Assert.assertEquals(100, raster.getHeight());
        Assert.assertTrue(raster.hasSmooth());
        Assert.assertEquals(100, raster.getRaster(0).getWidth());
        Assert.assertEquals(200, raster.getRaster(0).getHeight());
    }

    /**
     * Test raster no smooth save.
     */
    @Test
    public void testLoadRastersNoSmoothSave()
    {
        final RasterImage raster = new RasterImage(new ImageBufferMock(100, 200), mediaRaster, 100, false);
        raster.loadRasters(50, true, "prefix");

        Assert.assertEquals(15, raster.getRasters().size());
        Assert.assertEquals(mediaRaster, raster.getFile());
        Assert.assertEquals(100, raster.getHeight());
        Assert.assertFalse(raster.hasSmooth());
        Assert.assertEquals(100, raster.getRaster(0).getWidth());
        Assert.assertEquals(200, raster.getRaster(0).getHeight());

        final Media folder = Medias.create("prefix_raster");
        Assert.assertTrue(folder.getFile().getAbsolutePath(), folder.exists());
        try
        {
            for (int i = 1; i <= 15; i++)
            {
                final Media file = Medias.create("prefix_raster", i + Constant.DOT + ImageFormat.PNG);
                Assert.assertTrue(file.getFile().getAbsolutePath(), file.exists());
            }
        }
        finally
        {
            UtilFolder.deleteDirectory(folder.getFile());
        }
    }

    /**
     * Test raster no smooth cache.
     */
    @Test
    public void testLoadRastersNoSmoothCache()
    {
        final RasterImage raster = new RasterImage(Medias.create("image.png"), mediaRaster, 100, false);
        raster.loadRasters(50, true, "cache");

        Assert.assertEquals(15, raster.getRasters().size());
        Assert.assertEquals(mediaRaster, raster.getFile());
        Assert.assertEquals(100, raster.getHeight());
        Assert.assertFalse(raster.hasSmooth());
        Assert.assertEquals(64, raster.getRaster(0).getWidth());
        Assert.assertEquals(32, raster.getRaster(0).getHeight());

        final Media folder = Medias.create("cache_raster");
        Assert.assertTrue(folder.getFile().getAbsolutePath(), folder.exists());
        try
        {
            for (int i = 1; i <= 15; i++)
            {
                final Media file = Medias.create("cache_raster", i + Constant.DOT + ImageFormat.PNG);
                Assert.assertTrue(file.getFile().getAbsolutePath(), file.exists());
            }

            final RasterImage cache = new RasterImage(new ImageBufferMock(100, 200), mediaRaster, 100, false);
            cache.loadRasters(50, false, "cache");

            Assert.assertEquals(15, cache.getRasters().size());
            Assert.assertEquals(mediaRaster, cache.getFile());
            Assert.assertEquals(100, cache.getHeight());
            Assert.assertFalse(cache.hasSmooth());
            Assert.assertEquals(64, cache.getRaster(0).getWidth());
            Assert.assertEquals(32, cache.getRaster(0).getHeight());
        }
        finally
        {
            UtilFolder.deleteDirectory(folder.getFile());
        }
    }
}
