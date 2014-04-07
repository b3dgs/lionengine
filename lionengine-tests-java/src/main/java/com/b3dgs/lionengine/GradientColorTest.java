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
 * Test the gradient color class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class GradientColorTest
{
    /**
     * Test the gradient color.
     */
    @Test
    public void testGradientColor()
    {
        final GradientColor gradientColor = new GradientColor(1, 2, ColorRgba.BLACK, 3, 4, ColorRgba.WHITE);
        Assert.assertEquals(1, gradientColor.getX1());
        Assert.assertEquals(2, gradientColor.getY1());
        Assert.assertEquals(ColorRgba.BLACK, gradientColor.getColor1());

        Assert.assertEquals(3, gradientColor.getX2());
        Assert.assertEquals(4, gradientColor.getY2());
        Assert.assertEquals(ColorRgba.WHITE, gradientColor.getColor2());
    }
}
