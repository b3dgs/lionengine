/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.io.File;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.UtilityFile;
import com.b3dgs.lionengine.Version;

/**
 * Test the utility image class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class UtilityImageAwtTest
{
    /** Image. */
    private static Media IMAGE;
    /** Save image. */
    private static Media SAVE;
    /** Raster. */
    private static Media RASTER;
    /** Raster error. */
    private static Media RASTER_ERROR;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Engine.start("UtilityImage Test", Version.create(1, 0, 0),
                UtilityFile.getPath("src", "test", "resources", "utilityimage"));
        UtilityImageAwtTest.IMAGE = Core.MEDIA.create("image.png");
        UtilityImageAwtTest.SAVE = Core.MEDIA.create("save.png");
        UtilityImageAwtTest.RASTER = Core.MEDIA.create("raster.xml");
        UtilityImageAwtTest.RASTER_ERROR = Core.MEDIA.create("raster_error.xml");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Engine.terminate();
    }

    /**
     * Test creation of a wrong buffered image.
     * 
     * @param width The image width.
     * @param height The image height.
     */
    private static void testCreateBufferedImageFail(int width, int height)
    {
        try
        {
            Core.GRAPHIC.createImageBuffer(width, height, Transparency.OPAQUE);
            Assert.fail();
        }
        catch (final IllegalArgumentException exception)
        {
            // Success
        }
    }

    /**
     * Test testUtilityImage functions.
     */
    @Test
    public void testUtilityImage()
    {
        UtilityImageAwtTest.testCreateBufferedImageFail(0, 1);
        UtilityImageAwtTest.testCreateBufferedImageFail(1, 0);

        final ImageBuffer bufferedImage = Core.GRAPHIC.createImageBuffer(16, 32, Transparency.OPAQUE);

        Assert.assertEquals(bufferedImage.getWidth(), 16);
        Assert.assertEquals(bufferedImage.getHeight(), 32);

        final ImageBuffer image0 = Core.GRAPHIC.getImageBuffer(UtilityImageAwtTest.IMAGE, true);
        final ImageBuffer image1 = Core.GRAPHIC.getImageBuffer(UtilityImageAwtTest.IMAGE, false);
        try
        {
            Core.GRAPHIC.getImageBuffer(Core.MEDIA.create("null"), false);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            Core.GRAPHIC.getImageBuffer(Core.MEDIA.create("wrong_image.png"), false);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        Assert.assertEquals(image1.getWidth(), image0.getWidth());
        Assert.assertEquals(image1.getHeight(), image0.getHeight());

        final ImageBuffer image4 = Core.GRAPHIC.applyMask(image1, ColorRgba.BLACK);
        Assert.assertEquals(image1.getWidth(), image4.getWidth());
        Assert.assertEquals(image1.getHeight(), image4.getHeight());

        Core.GRAPHIC.rotate(image1, 90);
        final ImageBuffer resized = Core.GRAPHIC.resize(image1, 1, 2);
        Assert.assertEquals(1, resized.getWidth());
        Assert.assertEquals(2, resized.getHeight());

        final ImageBuffer flipH = Core.GRAPHIC.flipHorizontal(image1);
        Assert.assertEquals(image1.getWidth(), flipH.getWidth());
        Assert.assertEquals(image1.getHeight(), flipH.getHeight());

        final ImageBuffer flipV = Core.GRAPHIC.flipVertical(image1);
        Assert.assertEquals(image1.getWidth(), flipV.getWidth());
        Assert.assertEquals(image1.getHeight(), flipV.getHeight());

        final ImageBuffer[] split = Core.GRAPHIC.splitImage(image1, 2, 2);
        for (final ImageBuffer img1 : split)
        {
            for (final ImageBuffer img2 : split)
            {
                Assert.assertEquals(img1.getWidth(), img2.getWidth());
                Assert.assertEquals(img1.getHeight(), img2.getHeight());
            }
        }
        Assert.assertEquals(image1.getWidth() / 2, split[0].getWidth());
        Assert.assertEquals(image1.getHeight() / 2, split[0].getHeight());

        final int filterRgb1 = ColorRgba.filterRgb(0, -1, -1, -1);
        Assert.assertTrue(filterRgb1 >= 0);
        final int filterRgb2 = ColorRgba.filterRgb(65535, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
        Assert.assertTrue(filterRgb2 >= 0);

        try
        {
            Core.GRAPHIC.applyFilter(image1, null);
            Assert.fail();
        }
        catch (final NullPointerException exception)
        {
            // Success
        }
        Core.GRAPHIC.applyFilter(image1, Filter.BILINEAR);

        Core.GRAPHIC.saveImage(image1, UtilityImageAwtTest.SAVE);
        final ImageBuffer loaded = Core.GRAPHIC.getImageBuffer(UtilityImageAwtTest.SAVE, false);
        Assert.assertEquals(image1.getWidth(), loaded.getWidth());
        Assert.assertEquals(image1.getHeight(), loaded.getHeight());
        final File file = UtilityImageAwtTest.SAVE.getFile();
        Assert.assertTrue(file.delete());

        Core.GRAPHIC.getRasterBuffer(image1, 0, 0, 0, 255, 255, 255, 5);

        final int[][] raster = Core.GRAPHIC.loadRaster(UtilityImageAwtTest.RASTER);
        Assert.assertNotNull(raster);
        try
        {
            Core.GRAPHIC.loadRaster(UtilityImageAwtTest.RASTER_ERROR);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        Assert.assertTrue(ColorRgba.getRasterColor(0, raster[0], 2) > 0);
        raster[0][5] = 1;
        Assert.assertTrue(ColorRgba.getRasterColor(0, raster[0], 2) < 0);
        Assert.assertTrue(ColorRgba.filterRgb(-16711423, 0, 0, 0) < 0);
        Assert.assertTrue(ColorRgba.filterRgb(0, 0, 0, 0) == 0);
        Assert.assertTrue(ColorRgba.filterRgb(16711935, 0, 0, 0) > 0);
        Assert.assertTrue(ColorRgba.filterRgb(5000, 0, 0, 0) > 0);
        Assert.assertTrue(ColorRgba.filterRgb(-10000, 0, -10000000, 0) < 0);
        Assert.assertTrue(ColorRgba.filterRgb(5000, -100, -100, -100) > 0);
        Assert.assertTrue(ColorRgba.filterRgb(500000, -100, -10000, -100) > 0);
    }
}
