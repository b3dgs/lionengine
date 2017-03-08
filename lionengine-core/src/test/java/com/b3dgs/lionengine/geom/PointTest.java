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
package com.b3dgs.lionengine.geom;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the point class.
 */
public class PointTest
{
    /**
     * Test the coordinate.
     */
    @Test
    public void testPoint()
    {
        final Point point = new Point();
        Assert.assertEquals(point.getX(), 0);
        Assert.assertEquals(point.getY(), 0);

        point.setX(4);
        point.setY(5);
        Assert.assertEquals(point.getX(), 4);
        Assert.assertEquals(point.getY(), 5);

        final Point point2 = new Point(1, 2);
        Assert.assertEquals(point2.getX(), 1);
        Assert.assertEquals(point2.getY(), 2);

        point2.translate(-2, -1);
        Assert.assertEquals(point2.getX(), -1);
        Assert.assertEquals(point2.getY(), 1);

        point2.set(0, 0);
        Assert.assertEquals(point2.getX(), 0);
        Assert.assertEquals(point2.getY(), 0);

        Assert.assertNotEquals(point, point2);
        Assert.assertNotEquals(point.hashCode(), point2.hashCode());
    }

    /**
     * Test the coordinate.
     */
    @Test
    public void testHashEquals()
    {
        final Point point1 = new Point(1, 2);
        final Point point2 = new Point(1, 2);

        Assert.assertEquals(point1, point1);
        Assert.assertEquals(point1, point2);
        Assert.assertEquals(point1.hashCode(), point2.hashCode());
    }

    /**
     * Test the not equals.
     */
    @Test
    public void testNotEquals()
    {
        final Point point1 = new Point(1, 2);
        final Point point2 = new Point(2, 1);
        final Point point3 = new Point(3, 3);
        final Point point4 = new Point(1, 3);

        Assert.assertNotEquals(point1, null);
        Assert.assertNotEquals(point1, new Object());
        Assert.assertNotEquals(point1, point2);
        Assert.assertNotEquals(point1, point3);
        Assert.assertNotEquals(point2, point3);
        Assert.assertNotEquals(point1, point4);
    }
}
