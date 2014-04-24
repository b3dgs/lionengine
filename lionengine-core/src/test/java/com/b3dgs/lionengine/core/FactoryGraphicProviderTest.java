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
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.mock.FactoryMediaMock;

/**
 * Test the utility image class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryGraphicProviderTest
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
        FactoryGraphicProvider.setFactoryGraphic(new FactoryGraphicMock());
        FactoryMediaProvider.setFactoryMedia(new FactoryMediaMock());
        FactoryGraphicProviderTest.IMAGE = Core.MEDIA.create("src", "test", "resources", "utilityimage", "image.png");
        FactoryGraphicProviderTest.SAVE = Core.MEDIA.create("src", "test", "resources", "utilityimage", "save.png");
        FactoryGraphicProviderTest.RASTER = Core.MEDIA.create("src", "test", "resources", "utilityimage", "raster.xml");
        FactoryGraphicProviderTest.RASTER_ERROR = Core.MEDIA.create("src", "test", "resources", "utilityimage",
                "raster_error.xml");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(null);
        FactoryMediaProvider.setFactoryMedia(null);
    }

    /**
     * Test creation of a wrong image buffer.
     * 
     * @param width The image width.
     * @param height The image height.
     */
    private static void testCreateImageBufferFail(int width, int height)
    {
        try
        {
            Core.GRAPHIC.createImageBuffer(width, height, Transparency.OPAQUE);
            Assert.fail();
        }
        catch (final NegativeArraySizeException exception)
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
        FactoryGraphicProviderTest.testCreateImageBufferFail(-1, 1);
        FactoryGraphicProviderTest.testCreateImageBufferFail(1, -1);

        final Config config = new Config(new Resolution(320, 240, 60), 32, true);
        final Renderer renderer = Core.GRAPHIC.createRenderer(config);
        Assert.assertNotNull(renderer);
        Assert.assertNotNull(Core.GRAPHIC.createScreen(renderer, config));
        Assert.assertNotNull(Core.GRAPHIC.createTransform());

        final ImageBuffer imageBuffer = Core.GRAPHIC.createImageBuffer(16, 32, Transparency.OPAQUE);

        Assert.assertEquals(imageBuffer.getWidth(), 16);
        Assert.assertEquals(imageBuffer.getHeight(), 32);

        final ImageBuffer clone = Core.GRAPHIC.getImageBuffer(imageBuffer);
        Assert.assertEquals(imageBuffer.getWidth(), clone.getWidth());
        Assert.assertEquals(imageBuffer.getHeight(), clone.getHeight());

        final ImageBuffer image0 = Core.GRAPHIC.getImageBuffer(FactoryGraphicProviderTest.IMAGE, true);
        final ImageBuffer image1 = Core.GRAPHIC.getImageBuffer(FactoryGraphicProviderTest.IMAGE, false);

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

        Core.GRAPHIC.applyFilter(image1, Filter.BILINEAR);

        Core.GRAPHIC.saveImage(image1, FactoryGraphicProviderTest.SAVE);
        final ImageBuffer loaded = Core.GRAPHIC.getImageBuffer(FactoryGraphicProviderTest.IMAGE, false);
        Assert.assertEquals(image1.getWidth(), loaded.getWidth());
        Assert.assertEquals(image1.getHeight(), loaded.getHeight());
        final File file = FactoryGraphicProviderTest.SAVE.getFile();
        Assert.assertTrue(file.delete());

        Assert.assertNotNull(Core.GRAPHIC.getRasterBuffer(image1, 0, 0, 0, 255, 255, 255, 5));

        Assert.assertNotNull(Core.GRAPHIC.createText("test", 10, TextStyle.NORMAL));
    }

    /**
     * Test testUtilityImage failure.
     */
    @Test
    public void testUtilityImageFailure()
    {
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

        final ImageBuffer image = Core.GRAPHIC.getImageBuffer(FactoryGraphicProviderTest.IMAGE, false);
        try
        {
            Core.GRAPHIC.applyFilter(image, null);
            Assert.fail();
        }
        catch (final NullPointerException exception)
        {
            // Success
        }
    }

    /**
     * Test testUtilityImage raster functions.
     */
    @Test
    public void testUtilityImageRaster()
    {
        final int[][] raster = Core.GRAPHIC.loadRaster(FactoryGraphicProviderTest.RASTER);
        Assert.assertNotNull(raster);
        try
        {
            Core.GRAPHIC.loadRaster(FactoryGraphicProviderTest.RASTER_ERROR);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }
}
