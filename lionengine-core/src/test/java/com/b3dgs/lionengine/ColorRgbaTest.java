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
package com.b3dgs.lionengine;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the color class.
 */
public class ColorRgbaTest
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
        }
    }

    /**
     * Test the color negative value.
     */
    @Test
    public void testColorRgbaNegative()
    {
        testColorFailure(-1, 0, 0, 0);
        testColorFailure(0, -1, 0, 0);
        testColorFailure(0, 0, -1, 0);
        testColorFailure(0, 0, 0, -1);
    }

    /**
     * Test the color out of range value.
     */
    @Test
    public void testColorRgbaOutOfRange()
    {
        testColorFailure(Constant.UNSIGNED_BYTE, 0, 0, 0);
        testColorFailure(0, Constant.UNSIGNED_BYTE, 0, 0);
        testColorFailure(0, 0, Constant.UNSIGNED_BYTE, 0);
        testColorFailure(0, 0, 0, Constant.UNSIGNED_BYTE);
    }

    /**
     * Test the color rgba value constructor with color value.
     */
    @Test
    public void testColorRgbaValueConstructorValue()
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
     * Test the color rgba value constructor with rgb.
     */
    @Test
    public void testColorRgbaValueConstructorRgb()
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
     * Test the color rgba value constructor with rgb and alpha.
     */
    @Test
    public void testColorRgbaValueConstructorRgbAlpha()
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
     * Test the color rgb increment.
     */
    @Test
    public void testColorRgbInc()
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
                        final ColorRgba colorInc = new ColorRgba(ColorRgba.inc(color.getRgba(), r, g, b));

                        if (r != 0 && g != 0 && b != 0 && r != 255 && g != 255 && b != 255)
                        {
                            Assert.assertNotEquals(color.getRgba(), colorInc.getRgba());
                        }
                        if (!(a == 0 && (r > 0 || g > 0 || b > 0)))
                        {
                            Assert.assertEquals(color.getAlpha(), colorInc.getAlpha());
                            Assert.assertEquals(UtilMath.fixBetween(color.getRed() + r, 0, 255), colorInc.getRed());
                            Assert.assertEquals(UtilMath.fixBetween(color.getGreen() + g, 0, 255), colorInc.getGreen());
                            Assert.assertEquals(UtilMath.fixBetween(color.getBlue() + b, 0, 255), colorInc.getBlue());
                        }
                    }
                }
            }
        }
    }

    /**
     * Test the delta between two colors.
     */
    @Test
    public void testColorDelta()
    {
        Assert.assertEquals(Math.sqrt(255 * 255 + 255 * 255 + 255 * 255),
                            ColorRgba.getDelta(ColorRgba.BLACK, ColorRgba.WHITE),
                            UtilTests.PRECISION);
        Assert.assertEquals(0.0, ColorRgba.getDelta(ColorRgba.GRAY, ColorRgba.GRAY), UtilTests.PRECISION);
    }

    /**
     * Test the color opaque and transparent exclusive.
     */
    @Test
    public void testColorOpaqueTransparentExclusive()
    {
        Assert.assertFalse(ColorRgba.isOpaqueTransparentExclusive(ColorRgba.BLACK, ColorRgba.WHITE));
        Assert.assertFalse(ColorRgba.isOpaqueTransparentExclusive(ColorRgba.BLUE.getRgba(), ColorRgba.RED.getRgba()));

        Assert.assertTrue(ColorRgba.isOpaqueTransparentExclusive(ColorRgba.TRANSPARENT, ColorRgba.BLACK));
        Assert.assertTrue(ColorRgba.isOpaqueTransparentExclusive(ColorRgba.TRANSPARENT, ColorRgba.OPAQUE));
        Assert.assertTrue(ColorRgba.isOpaqueTransparentExclusive(ColorRgba.OPAQUE, ColorRgba.TRANSPARENT));

        Assert.assertFalse(ColorRgba.isOpaqueTransparentExclusive(ColorRgba.OPAQUE, ColorRgba.BLACK));
        Assert.assertFalse(ColorRgba.isOpaqueTransparentExclusive(ColorRgba.TRANSPARENT, ColorRgba.TRANSPARENT));
        Assert.assertFalse(ColorRgba.isOpaqueTransparentExclusive(ColorRgba.OPAQUE, ColorRgba.OPAQUE));
    }

    /**
     * Test the color equality.
     */
    @Test
    public void testColorHashCode()
    {
        Assert.assertEquals(ColorRgba.BLACK.hashCode(), ColorRgba.BLACK.hashCode());
        Assert.assertNotEquals(ColorRgba.WHITE.hashCode(), ColorRgba.BLACK.hashCode());
        Assert.assertNotEquals(ColorRgba.WHITE.hashCode(), ColorRgba.class.hashCode());
    }

    /**
     * Test the color equality.
     */
    @Test
    public void testColorEquals()
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
        Assert.assertNotEquals(ColorRgba.WHITE, ColorRgba.BLACK);
        Assert.assertNotEquals(ColorRgba.WHITE, ColorRgba.class);
    }

    /**
     * Test the color raster.
     */
    @Test
    public void testColorRaster()
    {
        final Media media = Medias.create("raster.xml");
        final int[][] raster = Graphics.loadRaster(media);
        Assert.assertTrue(ColorRgba.getRasterColor(0, raster[0], 2) > 0);
        raster[0][5] = 1;
        Assert.assertTrue(ColorRgba.getRasterColor(0, raster[0], 2) < 0);
    }

    /**
     * Test the color filter rgb.
     */
    @Test
    public void testColorFilterRgb()
    {
        Assert.assertTrue(ColorRgba.filterRgb(-16711423, 0, 0, 0) < 0);
        Assert.assertTrue(ColorRgba.filterRgb(0, 0, 0, 0) == 0);
        Assert.assertTrue(ColorRgba.filterRgb(16711935, 0, 0, 0) > 0);
        Assert.assertTrue(ColorRgba.filterRgb(5000, 0, 0, 0) > 0);
        Assert.assertFalse(ColorRgba.filterRgb(0, 5000, 0, 0) > 0);
        Assert.assertFalse(ColorRgba.filterRgb(0, 0, 5000, 0) > 0);
        Assert.assertFalse(ColorRgba.filterRgb(0, 0, 0, 5000) > 0);
        Assert.assertTrue(ColorRgba.filterRgb(-10000, 0, -10000000, 0) < 0);
        Assert.assertTrue(ColorRgba.filterRgb(5000, -100, -100, -100) > 0);
        Assert.assertTrue(ColorRgba.filterRgb(500000, -100, -10000, -100) > 0);

        final int filterRgb1 = ColorRgba.filterRgb(0, -1, -1, -1);
        Assert.assertTrue(filterRgb1 >= 0);

        final int filterRgb2 = ColorRgba.filterRgb(65535, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
        Assert.assertTrue(filterRgb2 >= 0);
    }
}
