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
package com.b3dgs.lionengine.test.geom;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Rectangle;

/**
 * Test the rectangle class.
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

        Assert.assertFalse(rectangle1.contains(null));
        Assert.assertFalse(rectangle1.intersects(null));

        Assert.assertTrue(rectangle1.contains(rectangle2));

        Assert.assertTrue(rectangle2.intersects(rectangle1));
        Assert.assertTrue(rectangle2.intersects(rectangle3));
        Assert.assertTrue(rectangle2.intersects(rectangle4));
        Assert.assertFalse(rectangle2.intersects(rectangle5));

        Assert.assertTrue(rectangle3.intersects(rectangle1));
        Assert.assertTrue(rectangle3.intersects(rectangle2));
        Assert.assertTrue(rectangle3.intersects(rectangle4));
        Assert.assertFalse(rectangle3.intersects(rectangle5));

        Assert.assertTrue(rectangle5.intersects(rectangle1));
        Assert.assertFalse(rectangle5.intersects(rectangle2));
        Assert.assertFalse(rectangle5.intersects(rectangle3));
        Assert.assertFalse(rectangle5.intersects(rectangle4));

        Assert.assertFalse(rectangle2.contains(rectangle1));
        Assert.assertFalse(rectangle2.contains(rectangle3));
        Assert.assertFalse(rectangle2.contains(rectangle4));
        Assert.assertFalse(rectangle2.contains(rectangle5));

        Assert.assertFalse(rectangle4.contains(rectangle3));
        Assert.assertFalse(rectangle4.contains(rectangle2));
        Assert.assertTrue(rectangle4.contains(rectangle4));
        Assert.assertFalse(rectangle4.contains(rectangle5));

        final double precision = 0.000000001;
        Assert.assertEquals(1.0, rectangle2.getX(), precision);
        Assert.assertEquals(1.0, rectangle2.getY(), precision);

        Assert.assertEquals(1.0, rectangle2.getMinX(), precision);
        Assert.assertEquals(1.0, rectangle2.getMinY(), precision);
        Assert.assertEquals(6.0, rectangle2.getMaxX(), precision);
        Assert.assertEquals(6.0, rectangle2.getMaxY(), precision);
        Assert.assertEquals(5.0, rectangle2.getWidth(), precision);
        Assert.assertEquals(5.0, rectangle2.getHeight(), precision);

        Assert.assertTrue(rectangle1.contains(2, 3));

        Assert.assertFalse(rectangle1.contains(-2, -3));
        Assert.assertFalse(rectangle1.contains(-1, 11));
        Assert.assertFalse(rectangle1.contains(0, 11));
        Assert.assertFalse(rectangle1.contains(-1, 10));
        Assert.assertFalse(rectangle1.contains(11, 12));
        Assert.assertFalse(rectangle1.contains(11, -3));
    }
}
