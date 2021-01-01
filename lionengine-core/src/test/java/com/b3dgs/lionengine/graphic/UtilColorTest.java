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
package com.b3dgs.lionengine.graphic;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilMath;

/**
 * Test {@link UtilColor}.
 */
final class UtilColorTest
{
    /**
     * Prepare tests.
     */
    @BeforeAll
    public static void beforeTests()
    {
        Medias.setLoadFromJar(UtilColorTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
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
     * Test constructor.
     */
    @Test
    void testConstructorPrivate()
    {
        assertPrivateConstructor(UtilColor.class);
    }

    /**
     * Test delta between two colors.
     */
    @Test
    void testDelta()
    {
        assertEquals(Math.sqrt(255 * 255 + 255 * 255 + 255 * 255),
                     UtilColor.getDelta(ColorRgba.BLACK, ColorRgba.WHITE));

        assertEquals(0.0, UtilColor.getDelta(ColorRgba.GRAY, ColorRgba.GRAY));
    }

    /**
     * Test rgb increment.
     */
    @Test
    void testRgbInc()
    {
        final int step = 5;
        for (int r = 0; r < Constant.UNSIGNED_BYTE; r += step)
        {
            for (int g = 0; g < Constant.UNSIGNED_BYTE; g += step)
            {
                for (int b = 0; b < Constant.UNSIGNED_BYTE; b += step)
                {
                    for (int a = 0; a < Constant.UNSIGNED_BYTE; a += step)
                    {
                        final ColorRgba color = new ColorRgba(r, g, b, a);
                        final ColorRgba colorInc = new ColorRgba(UtilColor.inc(color.getRgba(), r, g, b));

                        if (r != 0 && g != 0 && b != 0 && r != 255 && g != 255 && b != 255)
                        {
                            assertNotEquals(color.getRgba(), colorInc.getRgba());
                        }
                        if (!(a == 0 && (r > 0 || g > 0 || b > 0)))
                        {
                            assertEquals(color.getAlpha(), colorInc.getAlpha());
                            assertEquals(UtilMath.clamp(color.getRed() + r, 0, 255), colorInc.getRed());
                            assertEquals(UtilMath.clamp(color.getGreen() + g, 0, 255), colorInc.getGreen());
                            assertEquals(UtilMath.clamp(color.getBlue() + b, 0, 255), colorInc.getBlue());
                        }
                    }
                }
            }
        }
    }

    /**
     * Test opaque and transparent exclusive.
     */
    @Test
    void testOpaqueTransparentExclusive()
    {
        assertFalse(UtilColor.isOpaqueTransparentExclusive(ColorRgba.BLACK, ColorRgba.WHITE));
        assertFalse(UtilColor.isOpaqueTransparentExclusive(ColorRgba.BLUE.getRgba(), ColorRgba.RED.getRgba()));

        assertTrue(UtilColor.isOpaqueTransparentExclusive(ColorRgba.TRANSPARENT, ColorRgba.BLACK));
        assertTrue(UtilColor.isOpaqueTransparentExclusive(ColorRgba.TRANSPARENT, ColorRgba.OPAQUE));
        assertTrue(UtilColor.isOpaqueTransparentExclusive(ColorRgba.OPAQUE, ColorRgba.TRANSPARENT));

        assertFalse(UtilColor.isOpaqueTransparentExclusive(ColorRgba.OPAQUE, ColorRgba.BLACK));
        assertFalse(UtilColor.isOpaqueTransparentExclusive(ColorRgba.TRANSPARENT, ColorRgba.TRANSPARENT));
        assertFalse(UtilColor.isOpaqueTransparentExclusive(ColorRgba.OPAQUE, ColorRgba.OPAQUE));
    }

    /**
     * Test filter rgb.
     */
    @Test
    void testFilterRgb()
    {
        assertEquals(ColorRgba.BLACK.getRgba(), UtilColor.multiplyRgb(ColorRgba.WHITE.getRgba(), 0.0, 0.0, 0.0));
        assertEquals(ColorRgba.BLUE.getRgba(), UtilColor.multiplyRgb(ColorRgba.CYAN.getRgba(), 1.0, 0.0, 1.0));
        assertEquals(ColorRgba.GREEN.getRgba(), UtilColor.multiplyRgb(ColorRgba.YELLOW.getRgba(), 0.0, 1.0, 1.0));
        assertEquals(ColorRgba.RED.getRgba(), UtilColor.multiplyRgb(ColorRgba.PURPLE.getRgba(), 1.0, 1.0, 0.0));

        assertTrue(UtilColor.multiplyRgb(0, -1, -1, -1) >= 0);

        assertTrue(UtilColor.multiplyRgb(65_535, 0xFF_FF_FF, 0xFF_FF_FF, 0xFF_FF_FF) >= 0);
    }

    /**
     * Test weighted color of a surface.
     */
    @Test
    void testWeighted()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(2, 2, ColorRgba.TRANSPARENT);

        assertEquals(ColorRgba.TRANSPARENT,
                     UtilColor.getWeightedColor(surface, 0, 0, surface.getWidth(), surface.getHeight()));

        surface.setRgb(0, 0, ColorRgba.RED.getRgba());
        surface.setRgb(0, 1, ColorRgba.BLUE.getRgba());
        surface.setRgb(1, 0, ColorRgba.GREEN.getRgba());
        surface.setRgb(1, 1, ColorRgba.WHITE.getRgba());

        assertEquals(new ColorRgba(127, 127, 127),
                     UtilColor.getWeightedColor(surface, 0, 0, surface.getWidth(), surface.getHeight()));
    }
}
