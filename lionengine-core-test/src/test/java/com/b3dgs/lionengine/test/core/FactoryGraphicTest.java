/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.test.core;

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
import com.b3dgs.lionengine.Hq2x;
import com.b3dgs.lionengine.Hq3x;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.TextStyle;
import com.b3dgs.lionengine.Transparency;
import com.b3dgs.lionengine.core.FactoryMediaDefault;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.core.Renderer;
import com.b3dgs.lionengine.test.Constant;
import com.b3dgs.lionengine.test.FactoryGraphicMock;

/**
 * Test the factory graphic provider class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class FactoryGraphicTest
{
    /** Config. */
    private static final Config CONFIG = new Config(Constant.RESOLUTION_320_240, 32, true);

    /** Image. */
    private static Media mediaImage;
    /** Raster. */
    private static Media mediaRaster;
    /** Raster error. */
    private static Media mediaRasterError;
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
        prepare();
        loadResources();
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        image.dispose();
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Prepare test with factories.
     */
    protected static void prepare()
    {
        Medias.setFactoryMedia(new FactoryMediaDefault());
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setLoadFromJar(FactoryGraphicTest.class);
    }

    /**
     * Load test resources.
     * 
     * @throws IOException If error.
     */
    protected static void loadResources() throws IOException
    {
        final File temp = Files.createTempFile("save", "png").toFile();
        temp.deleteOnExit();
        mediaImage = Medias.create("image.png");
        mediaRaster = Medias.create("raster.xml");
        mediaSave = Medias.create(temp.getAbsolutePath());
        mediaRasterError = Medias.create("raster_error.xml");
        image = Graphics.getImageBuffer(mediaImage);
        image.prepare();
    }

    /**
     * Test negative image width.
     */
    @Test(expected = LionEngineException.class)
    public void testNegativeImageWidth()
    {
        Assert.assertNull(Graphics.createImageBuffer(-1, 1, Transparency.OPAQUE));
    }

    /**
     * Test negative image height.
     */
    @Test(expected = LionEngineException.class)
    public void testNegativeImageHeight()
    {
        Assert.assertNull(Graphics.createImageBuffer(1, -1, Transparency.OPAQUE));
    }

    /**
     * Test create screen.
     */
    @Test
    public void testCreateScreen()
    {
        Assert.assertNotNull(Graphics.createScreen(new Renderer(CONFIG)));
    }

    /**
     * Test create text.
     */
    @Test
    public void testCreateText()
    {
        Assert.assertNotNull(Graphics.createText("test", 10, TextStyle.NORMAL));
    }

    /**
     * Test create transform.
     */
    @Test
    public void testCreateTransform()
    {
        Assert.assertNotNull(Graphics.createTransform());
    }

    /**
     * Test create image buffer.
     */
    @Test
    public void testCreateImageBuffer()
    {
        final ImageBuffer imageBuffer = Graphics.createImageBuffer(16, 32, Transparency.OPAQUE);

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
        final ImageBuffer imageBuffer = Graphics.createImageBuffer(16, 32, Transparency.TRANSLUCENT);

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
        final ImageBuffer imageBuffer = Graphics.createImageBuffer(16, 32, Transparency.OPAQUE);
        final ImageBuffer copy = Graphics.getImageBuffer(imageBuffer);

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
        final ImageBuffer imageA = Graphics.getImageBuffer(mediaImage);
        final ImageBuffer imageB = Graphics.getImageBuffer(mediaImage);

        Assert.assertNotEquals(imageA, imageB);
        Assert.assertEquals(imageB.getWidth(), imageA.getWidth());
        Assert.assertEquals(imageB.getHeight(), imageA.getHeight());

        imageA.dispose();
        imageB.dispose();
    }

    /**
     * Test get image buffer failure with not existing media.
     */
    @Test(expected = LionEngineException.class)
    public void testGetImageBufferFailureMedia()
    {
        Assert.assertNotNull(Graphics.getImageBuffer(Medias.create("null")));
    }

    /**
     * Test get image buffer failure with wrong image.
     */
    @Test(expected = LionEngineException.class)
    public void testGetImageBufferFailureWrongImage()
    {
        Assert.assertNotNull(Graphics.getImageBuffer(Medias.create("wrong_image.png")));
    }

    /**
     * Test apply mask.
     */
    @Test
    public void testApplyMask()
    {
        final ImageBuffer mask = Graphics.applyMask(image, ColorRgba.BLACK);

        Assert.assertNotEquals(image, mask);
        Assert.assertEquals(image.getWidth(), mask.getWidth());
        Assert.assertEquals(image.getHeight(), mask.getHeight());

        mask.dispose();
    }

    /**
     * Test rotate.
     */
    @Test
    public void testRotate()
    {
        final ImageBuffer rotate = Graphics.rotate(image, 90);

        Assert.assertNotEquals(image, rotate);
        Assert.assertEquals(image.getWidth(), rotate.getWidth());
        Assert.assertEquals(image.getHeight(), rotate.getHeight());

        rotate.dispose();
    }

    /**
     * Test resize.
     */
    @Test
    public void testResize()
    {
        final ImageBuffer resized = Graphics.resize(image, 1, 2);

        Assert.assertNotEquals(image, resized);
        Assert.assertEquals(1, resized.getWidth());
        Assert.assertEquals(2, resized.getHeight());

        resized.dispose();
    }

    /**
     * Test flip horizontal.
     */
    @Test
    public void testFlipHorizontal()
    {
        final ImageBuffer horizontal = Graphics.flipHorizontal(image);

        Assert.assertNotEquals(image, horizontal);
        Assert.assertEquals(image.getWidth(), horizontal.getWidth());
        Assert.assertEquals(image.getHeight(), horizontal.getHeight());

        horizontal.dispose();
    }

    /**
     * Test flip vertical.
     */
    @Test
    public void testFlipVertical()
    {
        final ImageBuffer vertical = Graphics.flipVertical(image);

        Assert.assertNotEquals(image, vertical);
        Assert.assertEquals(image.getWidth(), vertical.getWidth());
        Assert.assertEquals(image.getHeight(), vertical.getHeight());

        vertical.dispose();
    }

    /**
     * Test split image.
     */
    @Test
    public void testSplitImage()
    {
        final ImageBuffer[] split = Graphics.splitImage(image, 2, 2);

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

        for (final ImageBuffer image : split)
        {
            image.dispose();
        }
    }

    /**
     * Test apply filter.
     */
    @Test
    public void testApplyFilter()
    {
        for (final Filter filter : Filter.values())
        {
            final ImageBuffer filtered = Graphics.applyFilter(image, filter);
            final int scale;
            switch (filter)
            {
                case NONE:
                    Assert.assertEquals(image, filtered);
                    scale = 1;
                    break;
                case BILINEAR:
                    Assert.assertNotEquals(image, filtered);
                    scale = 1;
                    break;
                case HQ2X:
                    Assert.assertNotEquals(image, filtered);
                    scale = Hq2x.SCALE;
                    break;
                case HQ3X:
                    Assert.assertNotEquals(image, filtered);
                    scale = Hq3x.SCALE;
                    break;
                default:
                    scale = 0;
                    Assert.fail();
            }
            Assert.assertEquals(image.getWidth() * scale, filtered.getWidth());
            Assert.assertEquals(image.getHeight() * scale, filtered.getHeight());

            if (filter != Filter.NONE)
            {
                filtered.dispose();
            }
        }
    }

    /**
     * Test save image.
     */
    @Test
    public void testSaveImage()
    {
        Graphics.saveImage(image, mediaSave);
    }

    /**
     * Test save image.
     */
    @Test
    public void testGetRasterBuffer()
    {
        final ImageBuffer raster = Graphics.getRasterBuffer(image, 0, 0, 0, 255, 255, 255, 5);

        Assert.assertNotEquals(image, raster);
        Assert.assertEquals(image.getWidth(), raster.getWidth());
        Assert.assertEquals(image.getHeight(), raster.getHeight());

        raster.dispose();
    }

    /**
     * Test load raster.
     */
    @Test
    public void testLoadRaster()
    {
        Assert.assertNotNull(Graphics.loadRaster(mediaRaster));
    }

    /**
     * Test load raster failure.
     */
    @Test(expected = LionEngineException.class)
    public void testLoadRasterFailure()
    {
        Assert.assertNotNull(Graphics.loadRaster(mediaRasterError));
    }
}
