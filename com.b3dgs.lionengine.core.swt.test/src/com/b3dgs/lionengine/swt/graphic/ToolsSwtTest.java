/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.swt.graphic;

import static com.b3dgs.lionengine.swt.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.swt.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.swt.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.swt.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.swt.UtilAssert.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Engine;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Version;
import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Transparency;

/**
 * Test {@link ToolsSwt}.
 */
final class ToolsSwtTest
{
    /**
     * Prepare test.
     */
    @BeforeAll
    public static void beforeTests()
    {
        EngineSwt.start(ToolsSwtTest.class.getSimpleName(), new Version(1, 0, 0), ToolsSwtTest.class);
    }

    /**
     * Clean up test.
     */
    @AfterAll
    public static void afterTests()
    {
        Engine.terminate();
    }

    /**
     * Test the transparency.
     */
    @Test
    void testTransparency()
    {
        assertEquals(SWT.TRANSPARENCY_NONE, ToolsSwt.getTransparency(Transparency.OPAQUE));
        assertEquals(SWT.TRANSPARENCY_PIXEL, ToolsSwt.getTransparency(Transparency.BITMASK));
        assertEquals(SWT.TRANSPARENCY_ALPHA, ToolsSwt.getTransparency(Transparency.TRANSLUCENT));
    }

    /**
     * Test the get raster.
     */
    @Test
    void testGetRasterColor()
    {
        final Image image = ToolsSwt.createImage(16, 16, SWT.TRANSPARENCY_PIXEL);
        final PaletteData palette = new PaletteData(new RGB(0, 0, 0),
                                                    new RGB(1, 0, 0),
                                                    new RGB(1, 1, 0),
                                                    new RGB(1, 0, 1));
        final ImageData data = new ImageData(16, 16, 8, palette);
        data.transparentPixel = 0;
        ToolsSwt.getRasterBuffer(new Image(image.getDevice(), data), 0, 0, 0);
    }

    /**
     * Test the utility.
     * 
     * @throws IOException If error.
     */
    @Test
    void testUtility() throws IOException
    {
        ScreenSwtTest.checkMultipleDisplaySupport();

        final Display display = ToolsSwt.getDisplay();
        final Image image = ToolsSwt.createImage(100, 100, SWT.TRANSPARENCY_NONE);

        assertNotNull(image);
        assertNotNull(ToolsSwt.getRasterBuffer(image, 1, 1, 1));
        assertNotNull(ToolsSwt.flipHorizontal(image));
        assertNotNull(ToolsSwt.flipVertical(image));
        assertNotNull(ToolsSwt.resize(image, 10, 10));
        assertNotNull(ToolsSwt.rotate(image, 90));
        assertNotNull(ToolsSwt.splitImage(image, 1, 1));
        assertNotNull(ToolsSwt.applyMask(image, ColorRgba.BLACK.getRgba()));
        assertNotNull(ToolsSwt.applyMask(image, ColorRgba.WHITE.getRgba()));

        image.dispose();

        final Media media = Medias.create("image.png");
        try (InputStream input = media.getInputStream())
        {
            final Image buffer = ToolsSwt.getImage(display, input);
            assertNotNull(buffer);

            try (InputStream input2 = media.getInputStream())
            {
                assertNotNull(ToolsSwt.getImageData(input2));
            }
            finally
            {
                buffer.dispose();
            }
        }

        assertNotNull(ToolsSwt.createHiddenCursor(display));
    }

    /**
     * Test the copy.
     */
    @Test
    void testCopy()
    {
        final Image image = ToolsSwt.createImage(100, 100, SWT.TRANSPARENCY_NONE);
        final Image copy = ToolsSwt.getImage(image);

        try
        {
            assertEquals(image.getImageData().width, copy.getImageData().width);
        }
        finally
        {
            image.dispose();
            copy.dispose();
        }
    }

    /**
     * Test the save.
     * 
     * @throws IOException If error.
     */
    @Test
    void testSave() throws IOException
    {
        ScreenSwtTest.checkMultipleDisplaySupport();
        final Media media = Medias.create("image.png");

        try (InputStream input = media.getInputStream())
        {
            final Image image = ToolsSwt.getImage(ToolsSwt.getDisplay(), input);
            assertNotNull(image);

            final Media save = Medias.create("test");
            try (OutputStream output = save.getOutputStream())
            {
                ToolsSwt.saveImage(image, output);
            }
            finally
            {
                image.dispose();
            }
            assertTrue(save.getFile().exists());
            assertTrue(save.getFile().delete());
            assertFalse(save.getFile().exists());
        }
    }

    /**
     * Test the get fail.
     */
    @Test
    void testGetFail()
    {
        ScreenSwtTest.checkMultipleDisplaySupport();
        final Media media = Medias.create("image.xml");

        assertThrows(() -> media.getInputStream(), "[image.xml] Cannot open the media !");
    }

    /**
     * Test the get fail IO.
     * 
     * @throws IOException If error.
     */
    @Test
    void testGetIoFail() throws IOException
    {
        ScreenSwtTest.checkMultipleDisplaySupport();
        final Media media = Medias.create("raster.xml");

        try (InputStream input = media.getInputStream())
        {
            try
            {
                ToolsSwt.getImage(ToolsSwt.getDisplay(), input);
                throw new AssertionError();
            }
            catch (final SWTException exception)
            {
                assertTrue(exception.getLocalizedMessage().contains("Unsupported or unrecognized format")
                           || exception.getLocalizedMessage().contains("Invalid image"));
            }
        }
    }
}
