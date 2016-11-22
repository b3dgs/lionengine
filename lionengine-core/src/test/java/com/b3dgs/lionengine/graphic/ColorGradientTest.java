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

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the gradient color class.
 */
public class ColorGradientTest
{
    /**
     * Test the gradient color.
     */
    @Test
    public void testColorGradient()
    {
        final ColorRgba color1 = ColorRgba.BLACK;
        final ColorRgba color2 = ColorRgba.WHITE;

        final int x1 = 1;
        final int y1 = 2;
        final int x2 = 3;
        final int y2 = 4;
        final ColorGradient color = new ColorGradient(x1, y1, color1, x2, y2, color2);

        Assert.assertEquals(x1, color.getX1());
        Assert.assertEquals(y1, color.getY1());
        Assert.assertEquals(color1, color.getColor1());

        Assert.assertEquals(x2, color.getX2());
        Assert.assertEquals(y2, color.getY2());
        Assert.assertEquals(color2, color.getColor2());
    }

    /**
     * Test the color gradient equals.
     */
    @Test
    public void testColorHashcode()
    {
        final int color = new ColorGradient(0, 1, ColorRgba.BLACK, 2, 3, ColorRgba.WHITE).hashCode();

        Assert.assertEquals(color, new ColorGradient(0, 1, ColorRgba.BLACK, 2, 3, ColorRgba.WHITE).hashCode());

        Assert.assertNotEquals(color, new Object().hashCode());
        Assert.assertNotEquals(color, new ColorGradient(1, 1, ColorRgba.BLACK, 2, 3, ColorRgba.WHITE).hashCode());
        Assert.assertNotEquals(color, new ColorGradient(0, 0, ColorRgba.BLACK, 2, 3, ColorRgba.WHITE).hashCode());
        Assert.assertNotEquals(color, new ColorGradient(0, 1, ColorRgba.WHITE, 2, 3, ColorRgba.WHITE).hashCode());
        Assert.assertNotEquals(color, new ColorGradient(0, 1, ColorRgba.BLACK, 0, 3, ColorRgba.WHITE).hashCode());
        Assert.assertNotEquals(color, new ColorGradient(0, 1, ColorRgba.BLACK, 2, 0, ColorRgba.WHITE).hashCode());
        Assert.assertNotEquals(color, new ColorGradient(0, 1, ColorRgba.BLACK, 2, 3, ColorRgba.BLACK).hashCode());
    }

    /**
     * Test the color gradient equals.
     */
    @Test
    public void testColorEquals()
    {
        final ColorGradient color = new ColorGradient(0, 1, ColorRgba.BLACK, 2, 3, ColorRgba.WHITE);

        Assert.assertEquals(color, color);
        Assert.assertEquals(color, new ColorGradient(0, 1, ColorRgba.BLACK, 2, 3, ColorRgba.WHITE));

        Assert.assertNotEquals(color, null);
        Assert.assertNotEquals(color, new Object());
        Assert.assertNotEquals(color, new ColorGradient(1, 1, ColorRgba.BLACK, 2, 3, ColorRgba.WHITE));
        Assert.assertNotEquals(color, new ColorGradient(0, 0, ColorRgba.BLACK, 2, 3, ColorRgba.WHITE));
        Assert.assertNotEquals(color, new ColorGradient(0, 1, ColorRgba.WHITE, 2, 3, ColorRgba.WHITE));
        Assert.assertNotEquals(color, new ColorGradient(0, 1, ColorRgba.BLACK, 0, 3, ColorRgba.WHITE));
        Assert.assertNotEquals(color, new ColorGradient(0, 1, ColorRgba.BLACK, 2, 0, ColorRgba.WHITE));
        Assert.assertNotEquals(color, new ColorGradient(0, 1, ColorRgba.BLACK, 2, 3, ColorRgba.BLACK));
    }
}
