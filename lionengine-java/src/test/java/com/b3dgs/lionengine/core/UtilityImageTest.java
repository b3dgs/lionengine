/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Engine.start("UtilityImageTest", Version.create(1, 0, 0), Media.getPath("src", "test", "resources"));
    }

    /**
     * Clean test.
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
            UtilityImage.createImageBuffer(width, height, Transparency.OPAQUE);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test the utility image class.
     * 
     * @throws SecurityException If error.
     * @throws NoSuchMethodException If error.
     * @throws IllegalArgumentException If error.
     * @throws IllegalAccessException If error.
     * @throws InstantiationException If error.
     */
    @Test
    public void testUtilityImageClass() throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException
    {
        final Constructor<UtilityImage> utilityImage = UtilityImage.class.getDeclaredConstructor();
        utilityImage.setAccessible(true);
        try
        {
            final UtilityImage clazz = utilityImage.newInstance();
            Assert.assertNotNull(clazz);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
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
        UtilityImageTest.testCreateBufferedImageFail(0, 1);
        UtilityImageTest.testCreateBufferedImageFail(1, 0);

        final ImageBuffer bufferedImage = UtilityImage.createImageBuffer(16, 32, Transparency.OPAQUE);

        Assert.assertEquals(bufferedImage.getWidth(), 16);
        Assert.assertEquals(bufferedImage.getHeight(), 32);

        final ImageBuffer image0 = UtilityImage.getImageBuffer(Media.get("dot.png"), true);
        final ImageBuffer image1 = UtilityImage.getImageBuffer(Media.get("dot.png"), false);
        try
        {
            UtilityImage.getImageBuffer(Media.get("null"), false);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        try
        {
            UtilityImage.getImageBuffer(Media.get("wrong_image.png"), false);
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
        try
        {
            UtilityImage.applyFilter(image1, Filter.HQ3X);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }

        UtilityImage.saveImage(image1, Media.get("testImage.png"));
        final ImageBuffer loaded = UtilityImage.getImageBuffer(Media.get("testImage.png"), false);
        Assert.assertEquals(image1.getWidth(), loaded.getWidth());
        Assert.assertEquals(image1.getHeight(), loaded.getHeight());
        final File file = new File(Media.get("testImage.png").getPath());
        Assert.assertTrue(file.delete());

        UtilityImage.getRasterBuffer(image1, 0, 0, 0, 255, 255, 255, 5);

        Assert.assertNotNull(UtilityImage.loadRaster(Media.get("raster.xml")));
        try
        {
            UtilityImage.loadRaster(Media.get("rasterError.xml"));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }
}
