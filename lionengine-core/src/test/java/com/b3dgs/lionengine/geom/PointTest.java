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
     * Test the default constructor.
     */
    @Test
    public void testConstructorDefault()
    {
        final Point point = new Point();

        Assert.assertEquals(0, point.getX());
        Assert.assertEquals(0, point.getY());
    }

    /**
     * Test the constructor with parameters.
     */
    @Test
    public void testConstructorParameters()
    {
        final Point point = new Point(1, 2);

        Assert.assertEquals(1, point.getX());
        Assert.assertEquals(2, point.getY());
    }

    /**
     * Test the translation.
     */
    @Test
    public void testTranslate()
    {
        final Point point = new Point(1, 2);
        point.translate(10, 20);

        Assert.assertEquals(11, point.getX());
        Assert.assertEquals(22, point.getY());
    }

    /**
     * Test the set.
     */
    @Test
    public void testSet()
    {
        final Point point = new Point();
        point.set(10, 20);

        Assert.assertEquals(10, point.getX());
        Assert.assertEquals(20, point.getY());
    }

    /**
     * Test the set X.
     */
    @Test
    public void testSetX()
    {
        final Point point = new Point(1, 2);
        point.setX(10);

        Assert.assertEquals(10, point.getX());
        Assert.assertEquals(2, point.getY());
    }

    /**
     * Test the set Y.
     */
    @Test
    public void testSetY()
    {
        final Point point = new Point(1, 2);
        point.setY(20);

        Assert.assertEquals(1, point.getX());
        Assert.assertEquals(20, point.getY());
    }

    /**
     * Test the equals.
     */
    @Test
    public void testEquals()
    {
        final Point point = new Point();

        Assert.assertEquals(point, point);

        Assert.assertEquals(new Point(), new Point());
        Assert.assertEquals(new Point(1, 2), new Point(1, 2));

        Assert.assertNotEquals(new Point(), null);
        Assert.assertNotEquals(new Point(), new Object());
        Assert.assertNotEquals(new Point(1, 0), new Point(1, 1));
        Assert.assertNotEquals(new Point(0, 1), new Point(1, 0));
        Assert.assertNotEquals(new Point(), new Point(1, 1));
    }

    /**
     * Test the equals.
     */
    @Test
    public void testHash()
    {
        Assert.assertEquals(new Point(1, 2).hashCode(), new Point(1, 2).hashCode());

        Assert.assertNotEquals(new Point().hashCode(), new Object().hashCode());
        Assert.assertNotEquals(new Point(1, 0).hashCode(), new Point(1, 1).hashCode());
        Assert.assertNotEquals(new Point(0, 1).hashCode(), new Point(1, 0).hashCode());
        Assert.assertNotEquals(new Point().hashCode(), new Point(1, 1).hashCode());
    }

    /**
     * Test the to string.
     */
    @Test
    public void testToString()
    {
        Assert.assertEquals("Point [x=1, y=2]", new Point(1, 2).toString());
    }
}
