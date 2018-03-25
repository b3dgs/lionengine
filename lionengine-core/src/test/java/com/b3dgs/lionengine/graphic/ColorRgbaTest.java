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

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Medias;

/**
 * Test {@link ColorRgba}.
 */
public final class ColorRgbaTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(ColorRgbaTest.class);
        Graphics.setFactoryGraphic(new FactoryGraphicMock());
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        Medias.setLoadFromJar(null);
        Graphics.setFactoryGraphic(null);
    }

    /**
     * Test the color failure cases.
     * 
     * @param r The red value.
     * @param g The green value.
     * @param b The blue value.
     * @param a The alpha value.
     */
    private static void testColorFailure(int r, int g, int b, int a)
    {
        try
        {
            final ColorRgba color = new ColorRgba(r, g, b, a);
            Assert.assertNotNull(color);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }
    }

    /**
     * Test negative value.
     */
    @Test
    public void testNegative()
    {
        testColorFailure(-1, 0, 0, 0);
        testColorFailure(0, -1, 0, 0);
        testColorFailure(0, 0, -1, 0);
        testColorFailure(0, 0, 0, -1);
    }

    /**
     * Test out of range value.
     */
    @Test
    public void testOutOfRange()
    {
        testColorFailure(Constant.UNSIGNED_BYTE, 0, 0, 0);
        testColorFailure(0, Constant.UNSIGNED_BYTE, 0, 0);
        testColorFailure(0, 0, Constant.UNSIGNED_BYTE, 0);
        testColorFailure(0, 0, 0, Constant.UNSIGNED_BYTE);
    }

    /**
     * Test rgba value constructor with color value.
     */
    @Test
    public void testConstructorValue()
    {
        final int step = 51;
        for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE; i += step)
        {
            final ColorRgba color = new ColorRgba(i);

            Assert.assertEquals(i, color.getRgba());
            Assert.assertEquals(i >> Constant.BYTE_4 & 0xFF, color.getAlpha());
            Assert.assertEquals(i >> Constant.BYTE_3 & 0xFF, color.getRed());
            Assert.assertEquals(i >> Constant.BYTE_2 & 0xFF, color.getGreen());
            Assert.assertEquals(i >> Constant.BYTE_1 & 0xFF, color.getBlue());
        }
    }

    /**
     * Test constructor with rgb.
     */
    @Test
    public void testConstructorRgb()
    {
        for (int r = 0; r < Constant.UNSIGNED_BYTE; r++)
        {
            for (int g = 0; g < Constant.UNSIGNED_BYTE; g++)
            {
                for (int b = 0; b < Constant.UNSIGNED_BYTE; b++)
                {
                    final ColorRgba color = new ColorRgba(r, g, b);

                    Assert.assertEquals(255, color.getAlpha());
                    Assert.assertEquals(r, color.getRed());
                    Assert.assertEquals(g, color.getGreen());
                    Assert.assertEquals(b, color.getBlue());
                }
            }
        }
    }

    /**
     * Test constructor with rgb and alpha.
     */
    @Test
    public void testConstructorRgbAlpha()
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

                        Assert.assertEquals(a, color.getAlpha());
                        Assert.assertEquals(r, color.getRed());
                        Assert.assertEquals(g, color.getGreen());
                        Assert.assertEquals(b, color.getBlue());
                    }
                }
            }
        }
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        Assert.assertEquals(ColorRgba.BLACK.hashCode(), ColorRgba.BLACK.hashCode());
        Assert.assertNotEquals(ColorRgba.WHITE.hashCode(), ColorRgba.BLACK.hashCode());
        Assert.assertNotEquals(ColorRgba.WHITE.hashCode(), ColorRgba.class.hashCode());
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final int step = 654321;
        for (int i = Integer.MIN_VALUE; i < Integer.MAX_VALUE - step; i += step)
        {
            final ColorRgba color = new ColorRgba(i);
            for (int j = Integer.MIN_VALUE; j < Integer.MAX_VALUE - step; j += step)
            {
                if (i != j)
                {
                    Assert.assertNotEquals(color, new ColorRgba(j));
                }
            }
        }
        Assert.assertEquals(ColorRgba.BLACK, ColorRgba.BLACK);
        Assert.assertNotEquals(ColorRgba.WHITE, null);
        Assert.assertNotEquals(ColorRgba.WHITE, ColorRgba.BLACK);
        Assert.assertNotEquals(ColorRgba.WHITE, ColorRgba.class);
    }

    /**
     * Test to string.
     */
    @Test
    public void testToString()
    {
        Assert.assertEquals("red = 100 | green = 150 | blue = 200 | alpha = 255",
                            new ColorRgba(100, 150, 200, 255).toString());
    }
}
