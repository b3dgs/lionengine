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
import java.io.IOException;
import java.nio.file.Files;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Filter;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.mock.MediaMock;

/**
 * Test the factory graphic provider class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryGraphicProviderTest
{
    /** Config. */
    private static final Config CONFIG = new Config(new Resolution(320, 240, 60), 32, true);

    /** Image. */
    private static final Media MEDIA_IMAGE = new MediaMock("image.png");
    /** Raster. */
    private static final Media MEDIA_RASTER = new MediaMock("raster.xml");
    /** Raster error. */
    private static final Media RASTER_ERROR = new MediaMock("raster_error.xml");
    /** Save image. */
    private static Media mediaSave;
    /** Image. */
    private static ImageBuffer image;

    /**
     * Prepare test.
     * 
     * @throws IOException If error.
     */
    @BeforeClass
    public static void setUp() throws IOException
    {
        FactoryGraphicProvider.setFactoryGraphic(new FactoryGraphicMock());

        final File temp = Files.createTempFile("save", "png").toFile();
        temp.deleteOnExit();
        mediaSave = new MediaMock(temp.getAbsolutePath(), true);
        image = Core.GRAPHIC.getImageBuffer(MEDIA_IMAGE, true);
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(null);
    }

    /**
     * Test negative image width.
     */
    @Test(expected = NegativeArraySizeException.class)
    public void testNegativeImageWidth()
    {
        Core.GRAPHIC.createImageBuffer(-1, 1, Transparency.OPAQUE);
    }

    /**
     * Test negative image height.
     */
    @Test(expected = NegativeArraySizeException.class)
    public void testNegativeImageHeight()
    {
        Core.GRAPHIC.createImageBuffer(1, -1, Transparency.OPAQUE);
    }

    /**
     * Test create renderer.
     */
    @Test
    public void testCreateRenderer()
    {
        Assert.assertNotNull(Core.GRAPHIC.createRenderer(CONFIG));
    }

    /**
     * Test create screen.
     */
    @Test
    public void testCreateScreen()
    {
        final Renderer renderer = Core.GRAPHIC.createRenderer(CONFIG);
        Assert.assertNotNull(Core.GRAPHIC.createScreen(renderer));
    }

    /**
     * Test create text.
     */
    @Test
    public void testCreateText()
    {
        Assert.assertNotNull(Core.GRAPHIC.createText("test", 10, TextStyle.NORMAL));
    }

    /**
     * Test create transform.
     */
    @Test
    public void testCreateTransform()
    {
        Assert.assertNotNull(Core.GRAPHIC.createTransform());
    }

    /**
     * Test create image buffer.
     */
    @Test
    public void testCreateImageBuffer()
    {
        final ImageBuffer imageBuffer = Core.GRAPHIC.createImageBuffer(16, 32, Transparency.OPAQUE);

        Assert.assertEquals(imageBuffer.getWidth(), 16);
        Assert.assertEquals(imageBuffer.getHeight(), 32);

        imageBuffer.dispose();
    }

    /**
     * Test create image buffer translucent.
     */
    @Test
    public void testCreateImageBufferTranslucent()
    {
        final ImageBuffer imageBuffer = Core.GRAPHIC.createImageBuffer(16, 32, Transparency.TRANSLUCENT);

        Assert.assertEquals(imageBuffer.getWidth(), 16);
        Assert.assertEquals(imageBuffer.getHeight(), 32);
        Assert.assertEquals(imageBuffer.getTransparency(), Transparency.TRANSLUCENT);

        imageBuffer.dispose();
    }

    /**
     * Test get image buffer from image.
     */
    @Test
    public void testGetImageBufferFromImage()
    {
        final ImageBuffer imageBuffer = Core.GRAPHIC.createImageBuffer(16, 32, Transparency.OPAQUE);
        final ImageBuffer copy = Core.GRAPHIC.getImageBuffer(imageBuffer);

        Assert.assertEquals(imageBuffer.getWidth(), copy.getWidth());
        Assert.assertEquals(imageBuffer.getHeight(), copy.getHeight());

        imageBuffer.dispose();
        copy.dispose();
    }

    /**
     * Test get image buffer from media.
     */
    @Test
    public void testGetImageBufferFromMedia()
    {
        final ImageBuffer imageA = Core.GRAPHIC.getImageBuffer(MEDIA_IMAGE, true);
        final ImageBuffer imageB = Core.GRAPHIC.getImageBuffer(MEDIA_IMAGE, false);

        Assert.assertNotEquals(imageA, imageB);
        Assert.assertEquals(imageB.getWidth(), imageA.getWidth());
        Assert.assertEquals(imageB.getHeight(), imageA.getHeight());
    }

    /**
     * Test get image buffer failure with not existing media.
     */
    @Test(expected = LionEngineException.class)
    public void testGetImageBufferFailureMedia()
    {
        Assert.assertNotNull(Core.GRAPHIC.getImageBuffer(new MediaMock("null", true), false));
    }

    /**
     * Test get image buffer failure with wrong image.
     */
    @Test(expected = LionEngineException.class)
    public void testGetImageBufferFailureWrongImage()
    {
        Assert.assertNotNull(Core.GRAPHIC.getImageBuffer(new MediaMock("wrong_image.png", true), false));
    }

    /**
     * Test apply mask.
     */
    @Test
    public void testApplyMask()
    {
        final ImageBuffer mask = Core.GRAPHIC.applyMask(image, ColorRgba.BLACK);
        Assert.assertNotEquals(image, mask);
        Assert.assertEquals(image.getWidth(), mask.getWidth());
        Assert.assertEquals(image.getHeight(), mask.getHeight());
    }

    /**
     * Test rotate.
     */
    @Test
    public void testRotate()
    {
        final ImageBuffer rotate = Core.GRAPHIC.rotate(image, 90);
        Assert.assertNotEquals(image, rotate);
        Assert.assertEquals(image.getWidth(), rotate.getWidth());
        Assert.assertEquals(image.getHeight(), rotate.getHeight());
    }

    /**
     * Test resize.
     */
    @Test
    public void testResize()
    {
        final ImageBuffer resized = Core.GRAPHIC.resize(image, 1, 2);
        Assert.assertNotEquals(image, resized);
        Assert.assertEquals(1, resized.getWidth());
        Assert.assertEquals(2, resized.getHeight());
    }

    /**
     * Test flip horizontal.
     */
    @Test
    public void testFlipHorizontal()
    {
        final ImageBuffer horizontal = Core.GRAPHIC.flipHorizontal(image);
        Assert.assertNotEquals(image, horizontal);
        Assert.assertEquals(image.getWidth(), horizontal.getWidth());
        Assert.assertEquals(image.getHeight(), horizontal.getHeight());
    }

    /**
     * Test flip vertical.
     */
    @Test
    public void testFlipVertical()
    {
        final ImageBuffer vertical = Core.GRAPHIC.flipVertical(image);
        Assert.assertNotEquals(image, vertical);
        Assert.assertEquals(image.getWidth(), vertical.getWidth());
        Assert.assertEquals(image.getHeight(), vertical.getHeight());
    }

    /**
     * Test split image.
     */
    @Test
    public void testSplitImage()
    {
        final ImageBuffer[] split = Core.GRAPHIC.splitImage(image, 2, 2);
        for (final ImageBuffer img1 : split)
        {
            for (final ImageBuffer img2 : split)
            {
                Assert.assertEquals(img1.getWidth(), img2.getWidth());
                Assert.assertEquals(img1.getHeight(), img2.getHeight());
            }
        }
        Assert.assertEquals(image.getWidth() / 2, split[0].getWidth());
        Assert.assertEquals(image.getHeight() / 2, split[0].getHeight());
    }

    /**
     * Test apply filter.
     */
    @Test
    public void testApplyFilter()
    {
        final ImageBuffer filtered = Core.GRAPHIC.applyFilter(image, Filter.BILINEAR);
        Assert.assertNotEquals(image, filtered);
        Assert.assertEquals(image.getWidth(), filtered.getWidth());
        Assert.assertEquals(image.getHeight(), filtered.getHeight());
    }

    /**
     * Test save image.
     */
    @Test
    public void testSaveImage()
    {
        Core.GRAPHIC.saveImage(image, mediaSave);
    }

    /**
     * Test save image.
     */
    @Test
    public void testGetRasterBuffer()
    {
        final ImageBuffer raster = Core.GRAPHIC.getRasterBuffer(image, 0, 0, 0, 255, 255, 255, 5);
        Assert.assertNotEquals(image, raster);
        Assert.assertEquals(image.getWidth(), raster.getWidth());
        Assert.assertEquals(image.getHeight(), raster.getHeight());
    }

    /**
     * Test load raster.
     */
    @Test
    public void testLoadRaster()
    {
        Assert.assertNotNull(Core.GRAPHIC.loadRaster(MEDIA_RASTER));
    }

    /**
     * Test load raster failure.
     */
    @Test(expected = LionEngineException.class)
    public void testLoadRasterFailure()
    {
        Assert.assertNotNull(Core.GRAPHIC.loadRaster(RASTER_ERROR));
    }
}
