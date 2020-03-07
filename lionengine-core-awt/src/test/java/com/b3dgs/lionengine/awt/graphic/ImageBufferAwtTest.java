/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Transparency;

/**
 * Test {@link ImageBufferAwt}.
 */
public final class ImageBufferAwtTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicAwt());
    }

    /**
     * Clean tests.
     */
    @AfterAll
    public static void afterTests()
    {
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test image.
     */
    @Test
    public void testImage()
    {
        final BufferedImage buffer = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        final ImageBuffer image = ToolsAwt.getImageBuffer(buffer);

        assertNotNull(image.createGraphic());

        image.prepare();

        assertEquals(buffer, image.getSurface());
        assertEquals(ColorRgba.BLACK.getRgba(), image.getRgb(0, 0));
        assertNotNull(image.getRgb(0, 0, 1, 1, new int[1], 0, 0));
        assertEquals(Transparency.OPAQUE, image.getTransparency());
        assertEquals(Transparency.OPAQUE, ToolsAwt.getImageBuffer(ToolsAwt.copyImage(buffer)).getTransparency());
        assertEquals(buffer.getWidth(), image.getWidth());
        assertEquals(buffer.getHeight(), image.getHeight());

        image.setRgb(0, 0, ColorRgba.BLUE.getRgba());

        assertEquals(ColorRgba.BLUE.getRgba(), image.getRgb(0, 0));

        image.setRgb(0, 0, 0, 0, new int[1], 0, 0);
        image.dispose();
    }

    /**
     * Test image transparency
     */
    @Test
    public void testImageTransparency()
    {
        final ImageBuffer image = Graphics.createImageBuffer(100, 100, ColorRgba.RED);

        assertEquals(Transparency.BITMASK, image.getTransparency());
        assertEquals(ColorRgba.TRANSPARENT, image.getTransparentColor());

        image.setRgb(0, 0, ColorRgba.TRANSPARENT.getRgba());

        assertEquals(ColorRgba.TRANSPARENT.getRgba(), image.getRgb(0, 0));
    }
}
