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
package com.b3dgs.lionengine.geom;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the rectangle class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class RectangleTest
{
    /**
     * Test the rectangle class.
     */
    @Test
    public void testRectangle()
    {
        final Rectangle rectangle1 = Geom.createRectangle();
        rectangle1.set(0.0, 0.0, 10.0, 10.0);
        final Rectangle rectangle2 = Geom.createRectangle(1.0, 1.0, 5.0, 5.0);
        final Rectangle rectangle3 = Geom.createRectangle(4.0, 1.0, 5.0, 5.0);
        final Rectangle rectangle4 = Geom.createRectangle(1.0, 4.0, 5.0, 5.0);
        final Rectangle rectangle5 = Geom.createRectangle(6.0, 6.0, 5.0, 5.0);

        Assert.assertTrue(rectangle1.contains(rectangle2));

        Assert.assertTrue(rectangle2.intersects(rectangle1));
        Assert.assertTrue(rectangle2.intersects(rectangle3));
        Assert.assertTrue(rectangle2.intersects(rectangle4));
        Assert.assertFalse(rectangle2.intersects(rectangle5));

        Assert.assertFalse(rectangle2.contains(rectangle1));
        Assert.assertFalse(rectangle2.contains(rectangle3));
        Assert.assertFalse(rectangle2.contains(rectangle4));
        Assert.assertFalse(rectangle2.contains(rectangle5));

        Assert.assertEquals(1.0, rectangle2.getX(), 0.000000001);
        Assert.assertEquals(1.0, rectangle2.getY(), 0.000000001);

        Assert.assertEquals(1.0, rectangle2.getMinX(), 0.000000001);
        Assert.assertEquals(1.0, rectangle2.getMinY(), 0.000000001);
        Assert.assertEquals(6.0, rectangle2.getMaxX(), 0.000000001);
        Assert.assertEquals(6.0, rectangle2.getMaxY(), 0.000000001);
        Assert.assertEquals(5.0, rectangle2.getWidth(), 0.000000001);
        Assert.assertEquals(5.0, rectangle2.getHeight(), 0.000000001);

        Assert.assertTrue(rectangle1.contains(2, 3));
    }
}
