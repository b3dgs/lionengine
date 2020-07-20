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
package com.b3dgs.lionengine.graphic;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Constant;

/**
 * Test {@link ColorRgba}.
 */
final class ColorRgbaTest
{
    /**
     * Test negative value.
     */
    @Test
    void testNegative()
    {
        assertThrows(() -> new ColorRgba(-1, 0, 0, 0), "Invalid argument: -1 is not superior or equal to 0");
        assertThrows(() -> new ColorRgba(0, -1, 0, 0), "Invalid argument: -1 is not superior or equal to 0");
        assertThrows(() -> new ColorRgba(0, 0, -1, 0), "Invalid argument: -1 is not superior or equal to 0");
        assertThrows(() -> new ColorRgba(0, 0, 0, -1), "Invalid argument: -1 is not superior or equal to 0");
    }

    /**
     * Test out of range value.
     */
    @Test
    void testOutOfRange()
    {
        assertThrows(() -> new ColorRgba(256, 0, 0, 0), "Invalid argument: 256 is not inferior or equal to 255");
        assertThrows(() -> new ColorRgba(0, 256, 0, 0), "Invalid argument: 256 is not inferior or equal to 255");
        assertThrows(() -> new ColorRgba(0, 0, 256, 0), "Invalid argument: 256 is not inferior or equal to 255");
        assertThrows(() -> new ColorRgba(0, 0, 0, 256), "Invalid argument: 256 is not inferior or equal to 255");
    }

    /**
     * Test constructor with value.
     */
    @Test
    void testConstructorValue()
    {
        final int step = 128;
        for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE + 1; i += step)
        {
            final ColorRgba color = new ColorRgba(i);

            assertEquals(i, color.getRgba());
            assertEquals(i >> Constant.BYTE_4 & 0xFF, color.getAlpha());
            assertEquals(i >> Constant.BYTE_3 & 0xFF, color.getRed());
            assertEquals(i >> Constant.BYTE_2 & 0xFF, color.getGreen());
            assertEquals(i >> Constant.BYTE_1 & 0xFF, color.getBlue());
        }
    }

    /**
     * Test constructor with rgb.
     */
    @Test
    void testConstructorRgb()
    {
        for (int r = 0; r < Constant.UNSIGNED_BYTE; r++)
        {
            for (int g = 0; g < Constant.UNSIGNED_BYTE; g++)
            {
                for (int b = 0; b < Constant.UNSIGNED_BYTE; b++)
                {
                    final ColorRgba color = new ColorRgba(r, g, b);

                    assertEquals(255, color.getAlpha());
                    assertEquals(r, color.getRed());
                    assertEquals(g, color.getGreen());
                    assertEquals(b, color.getBlue());
                }
            }
        }
    }

    /**
     * Test constructor with rgb and alpha.
     */
    @Test
    void testConstructorRgbAlpha()
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

                        assertEquals(a, color.getAlpha());
                        assertEquals(r, color.getRed());
                        assertEquals(g, color.getGreen());
                        assertEquals(b, color.getBlue());
                    }
                }
            }
        }
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final int step = 654_321;
        for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE - step; i += step)
        {
            final ColorRgba color = new ColorRgba(i);
            for (int j = Integer.MIN_VALUE; j < Integer.MAX_VALUE - step; j += step)
            {
                if (i != j)
                {
                    assertNotEquals(color, new ColorRgba(j));
                }
            }
        }

        assertEquals(ColorRgba.BLACK, ColorRgba.BLACK);

        assertNotEquals(ColorRgba.WHITE, null);
        assertNotEquals(ColorRgba.WHITE, new Object());
        assertNotEquals(ColorRgba.WHITE, ColorRgba.BLACK);
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        assertHashEquals(ColorRgba.BLACK, ColorRgba.BLACK);

        assertHashNotEquals(ColorRgba.WHITE, new Object());
        assertHashNotEquals(ColorRgba.WHITE, ColorRgba.BLACK);
    }

    /**
     * Test to string.
     */
    @Test
    void testToString()
    {
        assertEquals("ColorRgba [r=100, g=150, b=200, a=255]", new ColorRgba(100, 150, 200, 255).toString());
    }
}
