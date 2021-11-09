/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.awt.graphic;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import java.awt.BufferCapabilities;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.EngineMock;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Transparency;

/**
 * Test {@link ToolsAwt}.
 */
final class ToolsAwtTest
{
    /**
     * Start engine.
     */
    @BeforeAll
    static void beforeAll()
    {
        Engine.start(new EngineMock(ToolsAwtTest.class.getSimpleName(), Version.DEFAULT));

        Medias.setLoadFromJar(ToolsAwtTest.class);
    }

    /**
     * Terminate engine.
     */
    @AfterAll
    static void afterAll()
    {
        Medias.setLoadFromJar(null);

        Engine.terminate();
    }

    /**
     * Test constructor.
     */
    @Test
    void testConstructor()
    {
        assertPrivateConstructor(ToolsAwt.class);
    }

    /**
     * Test utility.
     * 
     * @throws IOException If error.
     */
    @Test
    void testUtility() throws IOException
    {
        final BufferedImage image = ToolsAwt.createImage(100, 100, java.awt.Transparency.TRANSLUCENT);

        assertNotNull(image);
        assertEquals(Transparency.TRANSLUCENT, ToolsAwt.getImageBuffer(image).getTransparency());
        assertNotNull(ToolsAwt.flipHorizontal(image));
        assertNotNull(ToolsAwt.flipVertical(image));
        assertNotNull(ToolsAwt.resize(image, 10, 10));
        assertNotNull(ToolsAwt.rotate(image, 90));
        assertNotNull(ToolsAwt.splitImage(image, 1, 1));
        assertNotNull(ToolsAwt.applyMask(image, ColorRgba.BLACK.getRgba()));
        assertNotNull(ToolsAwt.applyMask(image, ColorRgba.WHITE.getRgba()));

        final Media media = Medias.create("image.png");
        try (InputStream input = media.getInputStream())
        {
            final BufferedImage buffer = ToolsAwt.getImage(input);

            assertNotNull(buffer);
            assertNotNull(ToolsAwt.getImageData(image));

            ToolsAwt.optimizeGraphicsQuality(buffer.createGraphics());
        }

        assertNotNull(ToolsAwt.createHiddenCursor());
    }

    /**
     * Test transparency with unknown enum.
     */
    @Test
    void testTransparency()
    {
        assertThrows(() -> ToolsAwt.getTransparency(Transparency.values()[3]), "Unknown enum: FAIL");
        assertEquals(java.awt.Transparency.OPAQUE, ToolsAwt.getTransparency(Transparency.OPAQUE));
        assertEquals(java.awt.Transparency.BITMASK, ToolsAwt.getTransparency(Transparency.BITMASK));
        assertEquals(java.awt.Transparency.TRANSLUCENT, ToolsAwt.getTransparency(Transparency.TRANSLUCENT));
    }

    /**
     * Test copy.
     */
    @Test
    void testCopy()
    {
        final BufferedImage image = ToolsAwt.createImage(100, 100, java.awt.Transparency.TRANSLUCENT);
        final BufferedImage copy = ToolsAwt.copyImage(image);

        assertEquals(image.getWidth(), copy.getWidth());
    }

    /**
     * Test save.
     * 
     * @throws IOException If error.
     */
    @Test
    void testSave() throws IOException
    {
        final Media media = Medias.create("image.png");
        try (InputStream input = media.getInputStream())
        {
            final BufferedImage image = ToolsAwt.getImage(input);

            assertNotNull(image);

            final Media save = media;
            try (OutputStream output = save.getOutputStream())
            {
                ToolsAwt.saveImage(image, output);
            }

            assertTrue(save.getFile().exists(), save.getFile().getAbsolutePath());
        }
    }

    /**
     * Test get fail IO.
     * 
     * @throws IOException If error.
     */
    @Test
    void testGetIoFail() throws IOException
    {
        final Media media = Medias.create("raster.xml");
        try (InputStream input = media.getInputStream())
        {
            assertThrows(IOException.class, () -> ToolsAwt.getImage(input), "Invalid image !");
        }
    }

    /**
     * Test buffer strategy creation.
     */
    @Test
    void testCreateBufferStrategyCanvas()
    {
        final AtomicReference<Integer> result = new AtomicReference<>();

        ToolsAwt.createBufferStrategy(new java.awt.Canvas(null)
        {
            @Override
            public void createBufferStrategy(int numBuffers, BufferCapabilities caps) throws java.awt.AWTException
            {
                result.set(Integer.valueOf(numBuffers));
            }
        }, GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());

        assertEquals(2, result.get().intValue());
    }

    /**
     * Test buffer strategy creation failure.
     */
    @Test
    void testCreateBufferStrategyFailCanvas()
    {
        final AtomicReference<Integer> result = new AtomicReference<>();

        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        ToolsAwt.createBufferStrategy(new java.awt.Canvas(null)
        {
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

        assertEquals(1, result.get().intValue());
    }

    /**
     * Test buffer strategy creation.
     */
    @Test
    void testCreateBufferStrategyWindow()
    {
        final AtomicReference<Integer> result = new AtomicReference<>();

        ToolsAwt.createBufferStrategy(new java.awt.Window(null)
        {
            @Override
            public void createBufferStrategy(int numBuffers, BufferCapabilities caps) throws java.awt.AWTException
            {
                result.set(Integer.valueOf(numBuffers));
            }
        }, GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration());

        assertEquals(2, result.get().intValue());
    }

    /**
     * Test buffer strategy creation failure.
     */
    @Test
    void testCreateBufferStrategyFailWindow()
    {
        final AtomicReference<Integer> result = new AtomicReference<>();

        Verbose.info("*********************************** EXPECTED VERBOSE ***********************************");
        ToolsAwt.createBufferStrategy(new java.awt.Window(null)
        {
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

        assertEquals(1, result.get().intValue());
    }
}
