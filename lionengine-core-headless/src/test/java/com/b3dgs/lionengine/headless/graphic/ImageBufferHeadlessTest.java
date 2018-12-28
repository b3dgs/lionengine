/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.headless.graphic;

import static com.b3dgs.lionengine.UtilAssert.assertArrayEquals;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;

import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.graphic.ColorRgba;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Transparency;

/**
 * Test {@link ImageBufferHeadless}.
 */
public final class ImageBufferHeadlessTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Graphics.setFactoryGraphic(new FactoryGraphicHeadless());
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
        final ImageBuffer image = new ImageBufferHeadless(100, 100, Transparency.OPAQUE);

        assertNotNull(image.createGraphic());

        image.prepare();

        assertEquals(ColorRgba.TRANSPARENT.getRgba(), image.getRgb(0, 0));
        assertNotNull(image.getRgb(0, 0, 1, 1, new int[1], 0, 0));
        assertEquals(Transparency.OPAQUE, image.getTransparency());
        assertEquals(Transparency.OPAQUE, new ImageBufferHeadless(100, 100, Transparency.OPAQUE).getTransparency());
        assertEquals(100, image.getWidth());
        assertEquals(100, image.getHeight());

        image.setRgb(0, 0, ColorRgba.BLUE.getRgba());

        assertEquals(ColorRgba.BLUE.getRgba(), image.getRgb(0, 0));

        image.setRgb(0, 0, 0, 0, new int[1], 0, 0);

        image.dispose();
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor()
    {
        final ImageBufferHeadless image = new ImageBufferHeadless(100, 200, new int[100 * 200]);

        assertEquals(100, image.getWidth());
        assertEquals(200, image.getHeight());
    }

    /**
     * Test set rgb.
     */
    @Test
    public void testSetRgb()
    {
        final ImageBufferHeadless image = new ImageBufferHeadless(100, 200, Transparency.BITMASK);
        final int[] array = new int[3 * 3];
        Arrays.fill(array, ColorRgba.BLUE.getRgba());

        assertEquals(ColorRgba.TRANSPARENT.getRgba(), image.getRgb(0, 0));
        assertEquals(ColorRgba.TRANSPARENT.getRgba(), image.getRgb(1, 1));
        assertEquals(ColorRgba.TRANSPARENT.getRgba(), image.getRgb(2, 2));

        image.setRgb(1, 1, 1, 1, array, 0, 1);

        assertEquals(ColorRgba.TRANSPARENT.getRgba(), image.getRgb(0, 0));
        assertEquals(ColorRgba.BLUE.getRgba(), image.getRgb(1, 1));
        assertEquals(ColorRgba.TRANSPARENT.getRgba(), image.getRgb(2, 2));

        Arrays.fill(array, ColorRgba.TRANSPARENT.getRgba());
        final int[] expected = new int[3 * 3];
        expected[0] = ColorRgba.BLUE.getRgba();
        image.getRgb(1, 1, 1, 1, array, 0, 9);

        assertArrayEquals(expected, array);
        assertArrayEquals(expected, image.getRgb(1, 1, 1, 1, null, 0, 9));
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
    }
}
