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
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.mock.FactoryMediaMock;

/**
 * Test the factory graphic provider class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryGraphicProviderTest
{
    /** Resources path. */
    protected static final String PATH = UtilFile.getPath("resources", "graphic");
    /** Config. */
    protected static final Config CONFIG = new Config(new Resolution(320, 240, 60), 32, true);

    /** Image. */
    protected static Media MEDIA_IMAGE;
    /** Save image. */
    protected static Media MEDIA_SAVE;
    /** Raster. */
    protected static Media MEDIA_RASTER;
    /** Raster error. */
    protected static Media RASTER_ERROR;
    /** Image. */
    protected static ImageBuffer IMAGE;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(new FactoryGraphicMock());
        FactoryMediaProvider.setFactoryMedia(new FactoryMediaMock());

        FactoryGraphicProviderTest.MEDIA_IMAGE = Core.MEDIA.create(FactoryGraphicProviderTest.PATH, "image.png");
        FactoryGraphicProviderTest.MEDIA_SAVE = Core.MEDIA.create(FactoryGraphicProviderTest.PATH, "save.png");
        FactoryGraphicProviderTest.MEDIA_RASTER = Core.MEDIA.create(FactoryGraphicProviderTest.PATH, "raster.xml");
        FactoryGraphicProviderTest.RASTER_ERROR = Core.MEDIA
                .create(FactoryGraphicProviderTest.PATH, "raster_error.xml");

        FactoryGraphicProviderTest.IMAGE = Core.GRAPHIC.getImageBuffer(FactoryGraphicProviderTest.MEDIA_IMAGE, true);
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
        Assert.assertNotNull(Core.GRAPHIC.createRenderer(FactoryGraphicProviderTest.CONFIG));
    }

    /**
     * Test create screen.
     */
    @Test
    public void testCreateScreen()
    {
        final Renderer renderer = Core.GRAPHIC.createRenderer(FactoryGraphicProviderTest.CONFIG);
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
        final ImageBuffer imageA = Core.GRAPHIC.getImageBuffer(FactoryGraphicProviderTest.MEDIA_IMAGE, true);
        final ImageBuffer imageB = Core.GRAPHIC.getImageBuffer(FactoryGraphicProviderTest.MEDIA_IMAGE, false);

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
        Assert.assertNotNull(Core.GRAPHIC.getImageBuffer(Core.MEDIA.create("null"), false));
    }

    /**
     * Test get image buffer failure with wrong image.
     */
    @Test(expected = LionEngineException.class)
    public void testGetImageBufferFailureWrongImage()
    {
        Assert.assertNotNull(Core.GRAPHIC.getImageBuffer(Core.MEDIA.create("wrong_image.png"), false));
    }

    /**
     * Test apply mask.
     */
    @Test
    public void testApplyMask()
    {
        final ImageBuffer mask = Core.GRAPHIC.applyMask(FactoryGraphicProviderTest.IMAGE, ColorRgba.BLACK);
        Assert.assertNotEquals(FactoryGraphicProviderTest.IMAGE, mask);
        Assert.assertEquals(FactoryGraphicProviderTest.IMAGE.getWidth(), mask.getWidth());
        Assert.assertEquals(FactoryGraphicProviderTest.IMAGE.getHeight(), mask.getHeight());
    }

    /**
     * Test rotate.
     */
    @Test
    public void testRotate()
    {
        final ImageBuffer rotate = Core.GRAPHIC.rotate(FactoryGraphicProviderTest.IMAGE, 90);
        Assert.assertNotEquals(FactoryGraphicProviderTest.IMAGE, rotate);
        Assert.assertEquals(FactoryGraphicProviderTest.IMAGE.getWidth(), rotate.getWidth());
        Assert.assertEquals(FactoryGraphicProviderTest.IMAGE.getHeight(), rotate.getHeight());
    }

    /**
     * Test resize.
     */
    @Test
    public void testResize()
    {
        final ImageBuffer resized = Core.GRAPHIC.resize(FactoryGraphicProviderTest.IMAGE, 1, 2);
        Assert.assertNotEquals(FactoryGraphicProviderTest.IMAGE, resized);
        Assert.assertEquals(1, resized.getWidth());
        Assert.assertEquals(2, resized.getHeight());
    }

    /**
     * Test flip horizontal.
     */
    @Test
    public void testFlipHorizontal()
    {
        final ImageBuffer horizontal = Core.GRAPHIC.flipHorizontal(FactoryGraphicProviderTest.IMAGE);
        Assert.assertNotEquals(FactoryGraphicProviderTest.IMAGE, horizontal);
        Assert.assertEquals(FactoryGraphicProviderTest.IMAGE.getWidth(), horizontal.getWidth());
        Assert.assertEquals(FactoryGraphicProviderTest.IMAGE.getHeight(), horizontal.getHeight());
    }

    /**
     * Test flip vertical.
     */
    @Test
    public void testFlipVertical()
    {
        final ImageBuffer vertical = Core.GRAPHIC.flipVertical(FactoryGraphicProviderTest.IMAGE);
        Assert.assertNotEquals(FactoryGraphicProviderTest.IMAGE, vertical);
        Assert.assertEquals(FactoryGraphicProviderTest.IMAGE.getWidth(), vertical.getWidth());
        Assert.assertEquals(FactoryGraphicProviderTest.IMAGE.getHeight(), vertical.getHeight());
    }

    /**
     * Test split image.
     */
    @Test
    public void testSplitImage()
    {
        final ImageBuffer[] split = Core.GRAPHIC.splitImage(FactoryGraphicProviderTest.IMAGE, 2, 2);
        for (final ImageBuffer img1 : split)
        {
            for (final ImageBuffer img2 : split)
            {
                Assert.assertEquals(img1.getWidth(), img2.getWidth());
                Assert.assertEquals(img1.getHeight(), img2.getHeight());
            }
        }
        Assert.assertEquals(FactoryGraphicProviderTest.IMAGE.getWidth() / 2, split[0].getWidth());
        Assert.assertEquals(FactoryGraphicProviderTest.IMAGE.getHeight() / 2, split[0].getHeight());
    }

    /**
     * Test apply filter.
     */
    @Test
    public void testApplyFilter()
    {
        final ImageBuffer filtered = Core.GRAPHIC.applyFilter(FactoryGraphicProviderTest.IMAGE, Filter.BILINEAR);
        Assert.assertNotEquals(FactoryGraphicProviderTest.IMAGE, filtered);
        Assert.assertEquals(FactoryGraphicProviderTest.IMAGE.getWidth(), filtered.getWidth());
        Assert.assertEquals(FactoryGraphicProviderTest.IMAGE.getHeight(), filtered.getHeight());
    }

    /**
     * Test save image.
     */
    @Test
    public void testSaveImage()
    {
        Core.GRAPHIC.saveImage(FactoryGraphicProviderTest.IMAGE, FactoryGraphicProviderTest.MEDIA_SAVE);
        Assert.assertTrue(FactoryGraphicProviderTest.MEDIA_SAVE.getFile().delete());
    }

    /**
     * Test save image.
     */
    @Test
    public void testGetRasterBuffer()
    {
        final ImageBuffer raster = Core.GRAPHIC.getRasterBuffer(FactoryGraphicProviderTest.IMAGE, 0, 0, 0, 255, 255,
                255, 5);
        Assert.assertNotEquals(FactoryGraphicProviderTest.IMAGE, raster);
        Assert.assertEquals(FactoryGraphicProviderTest.IMAGE.getWidth(), raster.getWidth());
        Assert.assertEquals(FactoryGraphicProviderTest.IMAGE.getHeight(), raster.getHeight());
    }

    /**
     * Test load raster.
     */
    @Test
    public void testLoadRaster()
    {
        Assert.assertNotNull(Core.GRAPHIC.loadRaster(FactoryGraphicProviderTest.MEDIA_RASTER));
    }

    /**
     * Test load raster failure.
     */
    @Test(expected = LionEngineException.class)
    public void testLoadRasterFailure()
    {
        Assert.assertNotNull(Core.GRAPHIC.loadRaster(FactoryGraphicProviderTest.RASTER_ERROR));
    }
}
