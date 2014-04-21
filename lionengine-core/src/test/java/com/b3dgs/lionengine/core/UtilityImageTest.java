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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.Version;

/**
 * Test the utility image class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class UtilityImageTest
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
        EngineCore.start("UtilityImage Test", Version.create(1, 0, 0), Verbose.NONE, new FactoryGraphicMock(),
                new FactoryMediaMock());
        UtilityImageTest.IMAGE = Media.create(Media.getPath("src", "test", "resources", "utilityimage", "image.png"));
        UtilityImageTest.SAVE = Media.create(Media.getPath("src", "test", "resources", "utilityimage", "save.png"));
        UtilityImageTest.RASTER = Media.create(Media.getPath("src", "test", "resources", "utilityimage", "raster.xml"));
        UtilityImageTest.RASTER_ERROR = Media.create(Media.getPath("src", "test", "resources", "utilityimage",
                "raster_error.xml"));
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        EngineCore.terminate();
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
            UtilityImage.createImageBuffer(width, height, Transparency.OPAQUE);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test utility image class.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testCheckClass() throws Exception
    {
        final Constructor<UtilityImage> constructor = UtilityImage.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try
        {
            final UtilityImage check = constructor.newInstance();
            Assert.assertNotNull(check);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }

        Assert.assertNotNull(UtilityImage.createGraphic());
    }

    /**
     * Test testUtilityImage functions.
     */
    @Test
    public void testUtilityImage()
    {
        UtilityImageTest.testCreateBufferedImageFail(0, 1);
        UtilityImageTest.testCreateBufferedImageFail(1, 0);

        final ImageBuffer bufferedImage = UtilityImage.createImageBuffer(16, 32, Transparency.OPAQUE);

        Assert.assertEquals(bufferedImage.getWidth(), 16);
        Assert.assertEquals(bufferedImage.getHeight(), 32);

        final ImageBuffer image0 = UtilityImage.getImageBuffer(UtilityImageTest.IMAGE, true);
        final ImageBuffer image1 = UtilityImage.getImageBuffer(UtilityImageTest.IMAGE, false);
        try
        {
            UtilityImage.getImageBuffer(Media.create("null"), false);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            UtilityImage.getImageBuffer(Media.create("wrong_image.png"), false);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        Assert.assertEquals(image1.getWidth(), image0.getWidth());
        Assert.assertEquals(image1.getHeight(), image0.getHeight());

        final ImageBuffer image4 = UtilityImage.applyMask(image1, ColorRgba.BLACK);
        Assert.assertEquals(image1.getWidth(), image4.getWidth());
        Assert.assertEquals(image1.getHeight(), image4.getHeight());

        UtilityImage.rotate(image1, 90);
        final ImageBuffer resized = UtilityImage.resize(image1, 1, 2);
        Assert.assertEquals(1, resized.getWidth());
        Assert.assertEquals(2, resized.getHeight());

        final ImageBuffer flipH = UtilityImage.flipHorizontal(image1);
        Assert.assertEquals(image1.getWidth(), flipH.getWidth());
        Assert.assertEquals(image1.getHeight(), flipH.getHeight());

        final ImageBuffer flipV = UtilityImage.flipVertical(image1);
        Assert.assertEquals(image1.getWidth(), flipV.getWidth());
        Assert.assertEquals(image1.getHeight(), flipV.getHeight());

        final ImageBuffer[] split = UtilityImage.splitImage(image1, 2, 2);
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

        final int filterRgb1 = UtilityImage.filterRGB(0, -1, -1, -1);
        Assert.assertTrue(filterRgb1 >= 0);
        final int filterRgb2 = UtilityImage.filterRGB(65535, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
        Assert.assertTrue(filterRgb2 >= 0);

        try
        {
            UtilityImage.applyFilter(image1, null);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        UtilityImage.applyFilter(image1, Filter.BILINEAR);

        UtilityImage.saveImage(image1, UtilityImageTest.SAVE);
        final ImageBuffer loaded = UtilityImage.getImageBuffer(UtilityImageTest.SAVE, false);
        Assert.assertEquals(image1.getWidth(), loaded.getWidth());
        Assert.assertEquals(image1.getHeight(), loaded.getHeight());
        final File file = UtilityImageTest.SAVE.getFile();
        Assert.assertTrue(file.delete());

        UtilityImage.getRasterBuffer(image1, 0, 0, 0, 255, 255, 255, 5);

        final int[][] raster = UtilityImage.loadRaster(UtilityImageTest.RASTER);
        Assert.assertNotNull(raster);
        try
        {
            UtilityImage.loadRaster(UtilityImageTest.RASTER_ERROR);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
        Assert.assertTrue(UtilityImage.getRasterColor(0, raster[0], 2) > 0);
        raster[0][5] = 1;
        Assert.assertTrue(UtilityImage.getRasterColor(0, raster[0], 2) < 0);
        Assert.assertTrue(UtilityImage.filterRGB(-16711423, 0, 0, 0) < 0);
        Assert.assertTrue(UtilityImage.filterRGB(0, 0, 0, 0) == 0);
        Assert.assertTrue(UtilityImage.filterRGB(16711935, 0, 0, 0) > 0);
        Assert.assertTrue(UtilityImage.filterRGB(5000, 0, 0, 0) > 0);
        Assert.assertTrue(UtilityImage.filterRGB(-10000, 0, -10000000, 0) < 0);
        Assert.assertTrue(UtilityImage.filterRGB(5000, -100, -100, -100) > 0);
        Assert.assertTrue(UtilityImage.filterRGB(500000, -100, -10000, -100) > 0);
    }
}
