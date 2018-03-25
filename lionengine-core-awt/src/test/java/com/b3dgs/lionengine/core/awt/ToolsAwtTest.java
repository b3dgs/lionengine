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
package com.b3dgs.lionengine.core.awt;

import java.awt.BufferCapabilities;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Transparency;
import com.b3dgs.lionengine.util.UtilEnum;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test {@link ToolsAwt}.
 */
public final class ToolsAwtTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void prepare()
    {
        Medias.setLoadFromJar(ToolsAwtTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
    }

    /**
     * Test constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(ToolsAwt.class);
    }

    /**
     * Test utility.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testUtility() throws IOException
    {
        final BufferedImage image = ToolsAwt.createImage(100, 100, java.awt.Transparency.OPAQUE);

        Assert.assertNotNull(image);
        Assert.assertNotNull(ToolsAwt.getRasterBuffer(image, 1, 1, 1, 1, 1, 1, 1));
        Assert.assertNotNull(ToolsAwt.flipHorizontal(image));
        Assert.assertNotNull(ToolsAwt.flipVertical(image));
        Assert.assertNotNull(ToolsAwt.resize(image, 10, 10));
        Assert.assertNotNull(ToolsAwt.rotate(image, 90));
        Assert.assertNotNull(ToolsAwt.splitImage(image, 1, 1));
        Assert.assertNotNull(ToolsAwt.applyMask(image, ColorRgba.BLACK.getRgba()));
        Assert.assertNotNull(ToolsAwt.applyMask(image, ColorRgba.WHITE.getRgba()));

        final Media media = Medias.create("image.png");

        try (InputStream input = media.getInputStream())
        {
            final BufferedImage buffer = ToolsAwt.getImage(input);
            Assert.assertNotNull(buffer);
            Assert.assertNotNull(ToolsAwt.getImageData(image));
            ToolsAwt.optimizeGraphicsQuality(buffer.createGraphics());
        }

        Assert.assertNotNull(ToolsAwt.createHiddenCursor());
    }

    /**
     * Test transparency with unknown enum.
     */
    @Test
    public void testTransparency()
    {
        try
        {
            Assert.assertNotEquals(0, ToolsAwt.getTransparency(UtilEnum.make(Transparency.class, "FAIL")));
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            Assert.assertNotNull(exception);
        }

        Assert.assertEquals(java.awt.Transparency.OPAQUE, ToolsAwt.getTransparency(Transparency.OPAQUE));
        Assert.assertEquals(java.awt.Transparency.BITMASK, ToolsAwt.getTransparency(Transparency.BITMASK));
        Assert.assertEquals(java.awt.Transparency.TRANSLUCENT, ToolsAwt.getTransparency(Transparency.TRANSLUCENT));
    }

    /**
     * Test copy.
     */
    @Test
    public void testCopy()
    {
        final BufferedImage image = ToolsAwt.createImage(100, 100, java.awt.Transparency.TRANSLUCENT);
        final BufferedImage copy = ToolsAwt.copyImage(image);
        Assert.assertEquals(image.getWidth(), copy.getWidth());
    }

    /**
     * Test save.
     * 
     * @throws IOException If error.
     */
    @Test
    public void testSave() throws IOException
    {
        final Media media = Medias.create("image.png");

        try (InputStream input = media.getInputStream())
        {
            final BufferedImage image = ToolsAwt.getImage(input);
            Assert.assertNotNull(image);

            final Media save = media;

            try (OutputStream output = save.getOutputStream())
            {
                ToolsAwt.saveImage(image, output);
            }
            Assert.assertTrue(save.getFile().getAbsolutePath(), save.getFile().exists());
        }
    }

    /**
     * Test get fail.
     * 
     * @throws IOException If error.
     */
    @Test(expected = LionEngineException.class)
    public void testGetFail() throws IOException
    {
        final Media media = Medias.create("image.xml");

        try (InputStream input = media.getInputStream())
        {
            final BufferedImage image = ToolsAwt.getImage(input);
            Assert.assertNotNull(image);
        }
    }

    /**
     * Test get fail IO.
     * 
     * @throws IOException If error.
     */
    @Test(expected = IOException.class)
    public void testGetIoFail() throws IOException
    {
        final Media media = Medias.create("raster.xml");

        try (InputStream input = media.getInputStream())
        {
            final BufferedImage image = ToolsAwt.getImage(input);
            Assert.assertNotNull(image);
        }
    }

    /**
     * Test buffer strategy creation failure.
     */
    @Test
    public void testCreateBufferStrategyFailCanvas()
    {
        final AtomicReference<Integer> result = new AtomicReference<>();

        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        ToolsAwt.createBufferStrategy(new java.awt.Canvas(null)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void createBufferStrategy(int numBuffers, BufferCapabilities caps) throws java.awt.AWTException
            {
                if (numBuffers != 1)
                {
                    throw new java.awt.AWTException(Constant.EMPTY_STRING);
                }
                result.set(Integer.valueOf(numBuffers));
            }
        }, GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
        Verbose.info("****************************************************************************************");

        Assert.assertEquals(1, result.get().intValue());
    }

    /**
     * Test buffer strategy creation failure.
     */
    @Test
    public void testCreateBufferStrategyFailWindow()
    {
        final AtomicReference<Integer> result = new AtomicReference<>();

        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        ToolsAwt.createBufferStrategy(new java.awt.Window(null)
        {
            private static final long serialVersionUID = 1L;

            @Override
            public void createBufferStrategy(int numBuffers, BufferCapabilities caps) throws java.awt.AWTException
            {
                if (numBuffers != 1)
                {
                    throw new java.awt.AWTException(Constant.EMPTY_STRING);
                }
                result.set(Integer.valueOf(numBuffers));
            }
        }, GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());
        Verbose.info("****************************************************************************************");

        Assert.assertEquals(1, result.get().intValue());
    }
}
