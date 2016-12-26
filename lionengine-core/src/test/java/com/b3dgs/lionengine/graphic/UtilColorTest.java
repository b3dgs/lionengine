/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.util.UtilMath;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the color class.
 */
public class UtilColorTest
{
    /**
     * Prepare test.
     */
    @BeforeClass
    public static void setUp()
    {
        Medias.setLoadFromJar(UtilColorTest.class);
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
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(UtilColor.class);
    }

    /**
     * Test the delta between two colors.
     */
    @Test
    public void testColorDelta()
    {
        Assert.assertEquals(Math.sqrt(255 * 255 + 255 * 255 + 255 * 255),
                            UtilColor.getDelta(ColorRgba.BLACK, ColorRgba.WHITE),
                            UtilTests.PRECISION);
        Assert.assertEquals(0.0, UtilColor.getDelta(ColorRgba.GRAY, ColorRgba.GRAY), UtilTests.PRECISION);
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
                        final ColorRgba colorInc = new ColorRgba(UtilColor.inc(color.getRgba(), r, g, b));

                        if (r != 0 && g != 0 && b != 0 && r != 255 && g != 255 && b != 255)
                        {
                            Assert.assertNotEquals(color.getRgba(), colorInc.getRgba());
                        }
                        if (!(a == 0 && (r > 0 || g > 0 || b > 0)))
                        {
                            Assert.assertEquals(color.getAlpha(), colorInc.getAlpha());
                            Assert.assertEquals(UtilMath.clamp(color.getRed() + r, 0, 255), colorInc.getRed());
                            Assert.assertEquals(UtilMath.clamp(color.getGreen() + g, 0, 255), colorInc.getGreen());
                            Assert.assertEquals(UtilMath.clamp(color.getBlue() + b, 0, 255), colorInc.getBlue());
                        }
                    }
                }
            }
        }
    }

    /**
     * Test the color raster.
     */
    @Test
    public void testColorRaster()
    {
        final Media media = Medias.create("raster.xml");
        final Raster raster = Raster.load(media);
        final RasterData rasterData = raster.getRed();
        Assert.assertTrue(UtilColor.getRasterColor(0, rasterData, 2) > 0);

        final RasterData data = new RasterData(rasterData.getStart(),
                                               rasterData.getStep(),
                                               rasterData.getForce(),
                                               rasterData.getAmplitude(),
                                               rasterData.getOffset(),
                                               1);
        Assert.assertTrue(UtilColor.getRasterColor(0, data, 2) < 0);
    }

    /**
     * Test the color opaque and transparent exclusive.
     */
    @Test
    public void testColorOpaqueTransparentExclusive()
    {
        Assert.assertFalse(UtilColor.isOpaqueTransparentExclusive(ColorRgba.BLACK, ColorRgba.WHITE));
        Assert.assertFalse(UtilColor.isOpaqueTransparentExclusive(ColorRgba.BLUE.getRgba(), ColorRgba.RED.getRgba()));

        Assert.assertTrue(UtilColor.isOpaqueTransparentExclusive(ColorRgba.TRANSPARENT, ColorRgba.BLACK));
        Assert.assertTrue(UtilColor.isOpaqueTransparentExclusive(ColorRgba.TRANSPARENT, ColorRgba.OPAQUE));
        Assert.assertTrue(UtilColor.isOpaqueTransparentExclusive(ColorRgba.OPAQUE, ColorRgba.TRANSPARENT));

        Assert.assertFalse(UtilColor.isOpaqueTransparentExclusive(ColorRgba.OPAQUE, ColorRgba.BLACK));
        Assert.assertFalse(UtilColor.isOpaqueTransparentExclusive(ColorRgba.TRANSPARENT, ColorRgba.TRANSPARENT));
        Assert.assertFalse(UtilColor.isOpaqueTransparentExclusive(ColorRgba.OPAQUE, ColorRgba.OPAQUE));
    }

    /**
     * Test the color filter rgb.
     */
    @Test
    public void testColorFilterRgb()
    {
        Assert.assertTrue(UtilColor.filterRgb(-16711423, 0, 0, 0) < 0);
        Assert.assertTrue(UtilColor.filterRgb(0, 0, 0, 0) == 0);
        Assert.assertTrue(UtilColor.filterRgb(16711935, 0, 0, 0) > 0);
        Assert.assertTrue(UtilColor.filterRgb(5000, 0, 0, 0) > 0);
        Assert.assertFalse(UtilColor.filterRgb(0, 5000, 0, 0) > 0);
        Assert.assertFalse(UtilColor.filterRgb(0, 0, 5000, 0) > 0);
        Assert.assertFalse(UtilColor.filterRgb(0, 0, 0, 5000) > 0);
        Assert.assertTrue(UtilColor.filterRgb(-10000, 0, -10000000, 0) < 0);
        Assert.assertTrue(UtilColor.filterRgb(5000, -100, -100, -100) > 0);
        Assert.assertTrue(UtilColor.filterRgb(500000, -100, -10000, -100) > 0);

        final int filterRgb1 = UtilColor.filterRgb(0, -1, -1, -1);
        Assert.assertTrue(filterRgb1 >= 0);

        final int filterRgb2 = UtilColor.filterRgb(65535, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF);
        Assert.assertTrue(filterRgb2 >= 0);
    }

    /**
     * Test the weighted color of a surface.
     */
    @Test
    public void testColorWeighted()
    {
        final ImageBuffer surface = Graphics.createImageBuffer(2, 2, Transparency.TRANSLUCENT);

        Assert.assertEquals(ColorRgba.TRANSPARENT,
                            UtilColor.getWeightedColor(surface, 0, 0, surface.getWidth(), surface.getHeight()));

        surface.setRgb(0, 0, ColorRgba.RED.getRgba());
        surface.setRgb(0, 1, ColorRgba.BLUE.getRgba());
        surface.setRgb(1, 0, ColorRgba.GREEN.getRgba());
        surface.setRgb(1, 1, ColorRgba.WHITE.getRgba());

        Assert.assertEquals(new ColorRgba(127, 127, 127),
                            UtilColor.getWeightedColor(surface, 0, 0, surface.getWidth(), surface.getHeight()));
    }
}
