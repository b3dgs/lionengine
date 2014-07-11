/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.FactoryGraphicProvider;
import com.b3dgs.lionengine.core.FactoryMediaProvider;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.mock.FactoryGraphicMock;
import com.b3dgs.lionengine.mock.FactoryMediaMock;

/**
 * Test the color class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ColorRgbaTest
{
    /** Raster. */
    private static Media RASTER;

    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(new FactoryGraphicMock());
        FactoryMediaProvider.setFactoryMedia(new FactoryMediaMock());
        ColorRgbaTest.RASTER = Core.MEDIA.create("src", "test", "resources", "graphic", "raster.xml");
    }

    /**
     * Clean up test.
     */
    @AfterClass
    public static void cleanUp()
    {
        FactoryGraphicProvider.setFactoryGraphic(null);
        FactoryMediaProvider.setFactoryMedia(null);
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
        ColorRgbaTest.testColorFailure(-1, 0, 0, 0);
        ColorRgbaTest.testColorFailure(0, -1, 0, 0);
        ColorRgbaTest.testColorFailure(0, 0, -1, 0);
        ColorRgbaTest.testColorFailure(0, 0, 0, -1);
    }

    /**
     * Test the color out of range value.
     */
    @Test
    public void testColorRgbaFailures()
    {
        ColorRgbaTest.testColorFailure(256, 0, 0, 0);
        ColorRgbaTest.testColorFailure(0, 256, 0, 0);
        ColorRgbaTest.testColorFailure(0, 0, 256, 0);
        ColorRgbaTest.testColorFailure(0, 0, 0, 256);
    }

    /**
     * Test the color rgba value constructor.
     */
    @Test
    public void testColorRgbaValueConstructor()
    {
        final ColorRgba color1 = new ColorRgba(100);
        Assert.assertEquals(100, color1.getRgba());
        Assert.assertEquals(100, color1.getBlue());

        final ColorRgba color2 = new ColorRgba(255, 0, 0);
        Assert.assertEquals(ColorRgba.RED.getRgba(), color2.getRgba());

        final ColorRgba color3 = new ColorRgba(0, 255, 0, 255);
        Assert.assertEquals(ColorRgba.GREEN.getRgba(), color3.getRgba());
    }

    /**
     * Test the color rgb value equality.
     */
    @Test
    public void testColorRgbValueEquality()
    {
        final ColorRgba color = new ColorRgba(255, 0, 0);
        Assert.assertTrue(color.inc(1, 1, 1) != color.getRgba());
        Assert.assertEquals(255, color.getRed());
        Assert.assertEquals(0, color.getGreen());
        Assert.assertEquals(0, color.getBlue());
        Assert.assertEquals(255, color.getAlpha());
    }

    /**
     * Test the color with alpha value equality.
     */
    @Test
    public void testColorWithAlphaValueEquality()
    {
        final int r = 100;
        final int g = 75;
        final int b = 50;
        final int a = 0;

        final ColorRgba color = new ColorRgba(r, g, b, a);
        Assert.assertEquals(r, color.getRed());
        Assert.assertEquals(g, color.getGreen());
        Assert.assertEquals(b, color.getBlue());
        Assert.assertEquals(a, color.getAlpha());

        Assert.assertTrue(color.inc(1, 1, 1) != color.getRgba());
        Assert.assertEquals(r, color.getRed());
        Assert.assertEquals(g, color.getGreen());
        Assert.assertEquals(b, color.getBlue());
        Assert.assertEquals(a, color.getAlpha());
    }

    /**
     * Test the color utility.
     */
    @Test
    public void testColorUtility()
    {
        final int[][] raster = Core.GRAPHIC.loadRaster(ColorRgbaTest.RASTER);
        Assert.assertTrue(ColorRgba.getRasterColor(0, raster[0], 2) > 0);
        raster[0][5] = 1;
        Assert.assertTrue(ColorRgba.getRasterColor(0, raster[0], 2) < 0);
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
