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

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the color class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class ColorRgbaTest
{
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
     * Test the color.
     */
    @Test
    public void testColorRgba()
    {
        final ColorRgba color = new ColorRgba(0);
        Assert.assertEquals(color.getRgba(), 0);

        final ColorRgba color2 = new ColorRgba(255, 0, 0);
        Assert.assertEquals(ColorRgba.RED.getRgba(), color2.getRgba());

        final ColorRgba color3 = new ColorRgba(0, 255, 0, 255);
        Assert.assertEquals(ColorRgba.GREEN.getRgba(), color3.getRgba());

        final ColorRgba color4 = new ColorRgba(100, 75, 50, 25);
        Assert.assertEquals(100, color4.getRed());
        Assert.assertEquals(75, color4.getGreen());
        Assert.assertEquals(50, color4.getBlue());
        Assert.assertEquals(25, color4.getAlpha());
    }

    /**
     * Test the color failures.
     */
    @Test
    public void testColorRgbaFailures()
    {
        ColorRgbaTest.testColorFailure(-1, 0, 0, 0);
        ColorRgbaTest.testColorFailure(0, -1, 0, 0);
        ColorRgbaTest.testColorFailure(0, 0, -1, 0);

        ColorRgbaTest.testColorFailure(256, 0, 0, 0);
        ColorRgbaTest.testColorFailure(0, 256, 0, 0);
        ColorRgbaTest.testColorFailure(0, 0, 256, 0);

        ColorRgbaTest.testColorFailure(0, 0, 0, -1);
        ColorRgbaTest.testColorFailure(0, 0, 0, 256);
    }
}
