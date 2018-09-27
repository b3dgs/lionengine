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
package com.b3dgs.lionengine.graphic;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.FactoryMediaDefault;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.MediaMock;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.UtilTests;
import com.b3dgs.lionengine.Verbose;

/**
 * Test {@link FactoryGraphic}.
 */
public class FactoryGraphicTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setFactoryMedia(new FactoryMediaDefault());
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
        Medias.setLoadFromJar(FactoryGraphicTest.class);
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
     * Test invalid image width.
     */
    @Test
    public void testInvalidImageWidth()
    {
        assertThrows(() -> Graphics.createImageBuffer(0, 1), "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test invalid image height.
     */
    @Test
    public void testInvalidImageHeight()
    {
        assertThrows(() -> Graphics.createImageBuffer(1, 0), "Invalid argument: 0 is not strictly superior to 0");
    }

    /**
     * Test create screen windowed.
     */
    @Test
    public void testCreateScreenWindowed()
    {
        assertNotNull(Graphics.createScreen(new Config(UtilTests.RESOLUTION_320_240, 32, true)));
    }

    /**
     * Test create screen full.
     */
    @Test
    public void testCreateScreenFull()
    {
        assertNotNull(Graphics.createScreen(new Config(UtilTests.RESOLUTION_320_240, 32, false)));
    }

    /**
     * Test create screen with <code>null</code> argument.
     */
    @Test
    public void testCreateScreenNullConfig()
    {
        assertThrows(() -> Graphics.createScreen(null), "Unexpected null argument !");
    }

    /**
     * Test create text.
     */
    @Test
    public void testCreateText()
    {
        assertNotNull(Graphics.createText("test", 10, TextStyle.NORMAL));
    }

    /**
     * Test create transform.
     */
    @Test
    public void testCreateTransform()
    {
        assertNotNull(Graphics.createTransform());
    }

    /**
     * Test create image buffer.
     */
    @Test
    public void testCreateImageBuffer()
    {
        final ImageBuffer imageBuffer = Graphics.createImageBuffer(16, 32);

        assertEquals(16, imageBuffer.getWidth());
        assertEquals(32, imageBuffer.getHeight());

        imageBuffer.dispose();
    }

    /**
     * Test create image buffer.
     */
    @Test
    public void testCreateImageBufferTransparentColor()
    {
        final ImageBuffer imageBuffer = Graphics.createImageBuffer(16, 32, ColorRgba.TRANSPARENT);

        assertEquals(16, imageBuffer.getWidth());
        assertEquals(32, imageBuffer.getHeight());

        final String info = imageBuffer.getTransparency() + " " + imageBuffer.getTransparentColor();

        assertTrue(imageBuffer.getTransparency() == Transparency.BITMASK
                   && imageBuffer.getTransparentColor().getRed() == 0
                   && imageBuffer.getTransparentColor().getGreen() == 0
                   && imageBuffer.getTransparentColor().getBlue() == 0
                   || imageBuffer.getTransparency() == Transparency.TRANSLUCENT
                      && imageBuffer.getTransparentColor() == null,
                   info);

        imageBuffer.dispose();
    }

    /**
     * Test get image buffer from image.
     */
    @Test
    public void testGetImageBufferFromImage()
    {
        final ImageBuffer imageBuffer = Graphics.createImageBuffer(16, 32);
        final ImageBuffer copy = Graphics.getImageBuffer(imageBuffer);

        assertEquals(imageBuffer.getWidth(), copy.getWidth());
        assertEquals(imageBuffer.getHeight(), copy.getHeight());

        imageBuffer.dispose();
        copy.dispose();
    }

    /**
     * Test get image buffer from media.
     */
    @Test
    public void testGetImageBufferFromMedia()
    {
        final Media media = Medias.create("image.png");

        final ImageBuffer imageA = Graphics.getImageBuffer(media);
        final ImageBuffer imageB = Graphics.getImageBuffer(media);

        assertNotEquals(imageA, imageB);
        assertEquals(imageB.getWidth(), imageA.getWidth());
        assertEquals(imageB.getHeight(), imageA.getHeight());

        imageA.dispose();
        imageB.dispose();
    }

    /**
     * Test get image buffer failure with not existing media.
     */
    @Test
    public void testGetImageBufferFailureMedia()
    {
        assertThrows(() -> Graphics.getImageBuffer(Medias.create("null")), "[null] Error on reading image !");
    }

    /**
     * Test get image with error.
     */
    @Test
    public void testGetImageError()
    {
        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        assertThrows(() -> Graphics.getImageBuffer(new MediaMock()), "[null] Error on reading image !");
        Verbose.info("****************************************************************************************");
    }

    /**
     * Test get image buffer failure with wrong image.
     */
    @Test
    public void testGetImageBufferFailureWrongImage()
    {
        assertThrows(() -> Graphics.getImageBuffer(Medias.create("wrong_image.png")),
                     "[wrong_image.png] Error on reading image !");
    }

    /**
     * Test apply mask.
     */
    @Test
    public void testApplyMask()
    {
        final ImageBuffer image = Graphics.getImageBuffer(Medias.create("image.png"));
        image.prepare();
        final ImageBuffer mask = Graphics.applyMask(image, ColorRgba.BLACK);

        assertNotEquals(image, mask);
        assertEquals(image.getWidth(), mask.getWidth());
        assertEquals(image.getHeight(), mask.getHeight());

        mask.dispose();
        image.dispose();
    }

    /**
     * Test rotate.
     */
    @Test
    public void testRotate()
    {
        final ImageBuffer image = Graphics.getImageBuffer(Medias.create("image.png"));
        final ImageBuffer rotate = Graphics.rotate(image, 90);

        assertNotEquals(image, rotate);
        assertEquals(image.getWidth(), rotate.getWidth());
        assertEquals(image.getHeight(), rotate.getHeight());

        rotate.dispose();
        image.dispose();
    }

    /**
     * Test resize.
     */
    @Test
    public void testResize()
    {
        final ImageBuffer image = Graphics.getImageBuffer(Medias.create("image.png"));
        image.prepare();
        final ImageBuffer resized = Graphics.resize(image, 1, 2);

        assertNotEquals(image, resized);
        assertEquals(1, resized.getWidth());
        assertEquals(2, resized.getHeight());

        resized.dispose();
        image.dispose();
    }

    /**
     * Test flip horizontal.
     */
    @Test
    public void testFlipHorizontal()
    {
        final ImageBuffer image = Graphics.getImageBuffer(Medias.create("image.png"));
        image.prepare();
        final ImageBuffer horizontal = Graphics.flipHorizontal(image);

        assertNotEquals(image, horizontal);
        assertEquals(image.getWidth(), horizontal.getWidth());
        assertEquals(image.getHeight(), horizontal.getHeight());

        horizontal.dispose();
        image.dispose();
    }

    /**
     * Test flip vertical.
     */
    @Test
    public void testFlipVertical()
    {
        final ImageBuffer image = Graphics.getImageBuffer(Medias.create("image.png"));
        image.prepare();
        final ImageBuffer vertical = Graphics.flipVertical(image);

        assertNotEquals(image, vertical);
        assertEquals(image.getWidth(), vertical.getWidth());
        assertEquals(image.getHeight(), vertical.getHeight());

        vertical.dispose();
        image.dispose();
    }

    /**
     * Test split image.
     */
    @Test
    public void testSplitImage()
    {
        final ImageBuffer image = Graphics.getImageBuffer(Medias.create("image.png"));
        image.prepare();
        final ImageBuffer[] split = Graphics.splitImage(image, 2, 2);

        for (final ImageBuffer img1 : split)
        {
            for (final ImageBuffer img2 : split)
            {
                assertEquals(img1.getWidth(), img2.getWidth());
                assertEquals(img1.getHeight(), img2.getHeight());
            }
        }

        assertEquals(image.getWidth() / 2, split[0].getWidth());
        assertEquals(image.getHeight() / 2, split[0].getHeight());

        for (final ImageBuffer current : split)
        {
            current.dispose();
        }
        image.dispose();
    }

    /**
     * Test save image.
     */
    @Test
    public void testSaveImage()
    {
        final ImageBuffer image = Graphics.getImageBuffer(Medias.create("image.png"));
        image.prepare();
        final Media mediaSave = Medias.create("image_save.png");

        Graphics.saveImage(image, mediaSave);

        assertTrue(mediaSave.exists());

        UtilFile.deleteFile(mediaSave.getFile());
        image.dispose();
    }

    /**
     * Test save image with error.
     */
    @Test
    public void testSaveImageError()
    {
        final ImageBuffer image = Graphics.getImageBuffer(Medias.create("image.png"));

        assertThrows(() -> Graphics.saveImage(image, new MediaMock()), "[null] Unable to save image: ");

        image.dispose();
    }

    /**
     * Test get raster buffer.
     */
    @Test
    public void testGetRasterBuffer()
    {
        final ImageBuffer image = Graphics.getImageBuffer(Medias.create("image.png"));
        image.prepare();
        final ImageBuffer raster = Graphics.getRasterBuffer(image, 0, 0, 0);

        assertNotEquals(image, raster);
        assertEquals(image.getWidth(), raster.getWidth());
        assertEquals(image.getHeight(), raster.getHeight());

        raster.dispose();
        image.dispose();
    }
}
