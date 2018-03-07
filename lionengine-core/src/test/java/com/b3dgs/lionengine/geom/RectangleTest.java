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

import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the rectangle class.
 */
public class RectangleTest
{
    /**
     * Test the default constructor.
     */
    @Test
    public void testConstructorDefault()
    {
        final Rectangle rectangle = new Rectangle();

        Assert.assertEquals(0.0, rectangle.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, rectangle.getY(), UtilTests.PRECISION);
        Assert.assertEquals(0, rectangle.getWidth());
        Assert.assertEquals(0, rectangle.getHeight());
        Assert.assertEquals(0.0, rectangle.getWidthReal(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, rectangle.getHeightReal(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, rectangle.getMinX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, rectangle.getMinY(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, rectangle.getMaxX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, rectangle.getMaxY(), UtilTests.PRECISION);
    }

    /**
     * Test the constructor with parameters.
     */
    @Test
    public void testConstructorParameters()
    {
        final Rectangle rectangle = new Rectangle(1.0, 2.0, 3.6, 4.6);

        Assert.assertEquals(1.0, rectangle.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, rectangle.getY(), UtilTests.PRECISION);
        Assert.assertEquals(3, rectangle.getWidth());
        Assert.assertEquals(4, rectangle.getHeight());
        Assert.assertEquals(3.6, rectangle.getWidthReal(), UtilTests.PRECISION);
        Assert.assertEquals(4.6, rectangle.getHeightReal(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, rectangle.getMinX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, rectangle.getMinY(), UtilTests.PRECISION);
        Assert.assertEquals(4.6, rectangle.getMaxX(), UtilTests.PRECISION);
        Assert.assertEquals(6.6, rectangle.getMaxY(), UtilTests.PRECISION);
    }

    /**
     * Test the set.
     */
    @Test
    public void testSet()
    {
        final Rectangle rectangle = new Rectangle();
        rectangle.set(1.0, 2.0, 3.6, 4.6);

        Assert.assertEquals(1.0, rectangle.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, rectangle.getY(), UtilTests.PRECISION);
        Assert.assertEquals(3, rectangle.getWidth());
        Assert.assertEquals(4, rectangle.getHeight());
        Assert.assertEquals(3.6, rectangle.getWidthReal(), UtilTests.PRECISION);
        Assert.assertEquals(4.6, rectangle.getHeightReal(), UtilTests.PRECISION);
        Assert.assertEquals(1.0, rectangle.getMinX(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, rectangle.getMinY(), UtilTests.PRECISION);
        Assert.assertEquals(4.6, rectangle.getMaxX(), UtilTests.PRECISION);
        Assert.assertEquals(6.6, rectangle.getMaxY(), UtilTests.PRECISION);
    }

    /**
     * Test the rectangle intersects.
     */
    @Test
    public void testIntersects()
    {
        final Rectangle rectangle1 = new Rectangle(0.0, 0.0, 10.0, 10.0);
        final Rectangle rectangle2 = new Rectangle(1.0, 1.0, 5.0, 5.0);
        final Rectangle rectangle3 = new Rectangle(4.0, 1.0, 5.0, 5.0);
        final Rectangle rectangle4 = new Rectangle(1.0, 4.0, 5.0, 5.0);
        final Rectangle rectangle5 = new Rectangle(6.0, 6.0, 5.0, 5.0);

        Assert.assertFalse(rectangle1.intersects(null));

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
    }

    /**
     * Test the rectangle contains.
     */
    @Test
    public void testContains()
    {
        final Rectangle rectangle1 = new Rectangle(0.0, 0.0, 10.0, 10.0);
        final Rectangle rectangle2 = new Rectangle(1.0, 1.0, 5.0, 5.0);
        final Rectangle rectangle3 = new Rectangle(4.0, 1.0, 5.0, 5.0);
        final Rectangle rectangle4 = new Rectangle(1.0, 4.0, 5.0, 5.0);
        final Rectangle rectangle5 = new Rectangle(6.0, 6.0, 5.0, 5.0);

        Assert.assertFalse(rectangle1.contains(null));

        Assert.assertTrue(rectangle1.contains(rectangle2));

        Assert.assertFalse(rectangle2.contains(rectangle1));
        Assert.assertFalse(rectangle2.contains(rectangle3));
        Assert.assertFalse(rectangle2.contains(rectangle4));
        Assert.assertFalse(rectangle2.contains(rectangle5));

        Assert.assertFalse(rectangle4.contains(rectangle3));
        Assert.assertFalse(rectangle4.contains(rectangle2));
        Assert.assertTrue(rectangle4.contains(rectangle4));
        Assert.assertFalse(rectangle4.contains(rectangle5));

        Assert.assertTrue(rectangle1.contains(2, 3));

        Assert.assertFalse(rectangle1.contains(-2, -3));
        Assert.assertFalse(rectangle1.contains(-1, 11));
        Assert.assertFalse(rectangle1.contains(0, 11));
        Assert.assertFalse(rectangle1.contains(-1, 10));
        Assert.assertFalse(rectangle1.contains(11, 12));
        Assert.assertFalse(rectangle1.contains(11, -3));
    }

    /**
     * Test the rectangle rotation.
     */
    @Test
    public void testRotate()
    {
        final Rectangle rectangle = new Rectangle(1.0, 2.0, 5.0, 10.0);

        Assert.assertEquals(rectangle, rectangle.rotate(0));
        Assert.assertEquals(new Rectangle(-1.5, 4.5, 10.0, 5.0), rectangle.rotate(90));
        Assert.assertEquals(rectangle, rectangle.rotate(360));
    }

    /**
     * Test the rectangle translate.
     */
    @Test
    public void testTranslate()
    {
        final Rectangle rectangle = new Rectangle(1.0, 1.0, 5.0, 5.0);
        rectangle.translate(1.0, 2.0);

        Assert.assertEquals(2.0, rectangle.getX(), UtilTests.PRECISION);
        Assert.assertEquals(3.0, rectangle.getY(), UtilTests.PRECISION);
        Assert.assertEquals(2.0, rectangle.getMinX(), UtilTests.PRECISION);
        Assert.assertEquals(3.0, rectangle.getMinY(), UtilTests.PRECISION);
        Assert.assertEquals(7.0, rectangle.getMaxX(), UtilTests.PRECISION);
        Assert.assertEquals(8.0, rectangle.getMaxY(), UtilTests.PRECISION);
        Assert.assertEquals(5.0, rectangle.getWidthReal(), UtilTests.PRECISION);
        Assert.assertEquals(5.0, rectangle.getHeightReal(), UtilTests.PRECISION);
    }

    /**
     * Test the rectangle hash code.
     */
    @Test
    public void testHashcode()
    {
        final int rectangle = new Rectangle(0, 1, 2, 3).hashCode();

        Assert.assertEquals(rectangle, new Rectangle(0, 1, 2, 3).hashCode());

        Assert.assertNotEquals(rectangle, new Object().hashCode());
        Assert.assertNotEquals(rectangle, new Rectangle(0, 1, 2, 0).hashCode());
        Assert.assertNotEquals(rectangle, new Rectangle(0, 1, 0, 3).hashCode());
        Assert.assertNotEquals(rectangle, new Rectangle(0, 1, 0, 0).hashCode());
        Assert.assertNotEquals(rectangle, new Rectangle(0, 0, 2, 3).hashCode());
        Assert.assertNotEquals(rectangle, new Rectangle(0, 0, 2, 0).hashCode());
        Assert.assertNotEquals(rectangle, new Rectangle(0, 0, 0, 0).hashCode());
        Assert.assertNotEquals(rectangle, new Rectangle(0, 0, 0, 0).hashCode());
        Assert.assertNotEquals(rectangle, new Rectangle(1, 1, 2, 3).hashCode());
        Assert.assertNotEquals(rectangle, new Rectangle(0, 1, 2, 0).hashCode());
        Assert.assertNotEquals(rectangle, new Rectangle(0, 1, 0, 3).hashCode());
        Assert.assertNotEquals(rectangle, new Rectangle(0, 1, 0, 0).hashCode());
        Assert.assertNotEquals(rectangle, new Rectangle(0, 0, 2, 3).hashCode());
        Assert.assertNotEquals(rectangle, new Rectangle(0, 0, 2, 0).hashCode());
        Assert.assertNotEquals(rectangle, new Rectangle(0, 0, 0, 3).hashCode());
        Assert.assertNotEquals(rectangle, new Rectangle(1, 0, 0, 0).hashCode());
    }

    /**
     * Test the rectangle equals.
     */
    @Test
    public void testEquals()
    {
        final Rectangle rectangle = new Rectangle(0, 1, 2, 3);

        Assert.assertEquals(rectangle, rectangle);
        Assert.assertEquals(rectangle, new Rectangle(0, 1, 2, 3));

        Assert.assertNotEquals(rectangle, null);
        Assert.assertNotEquals(rectangle, new Object());
        Assert.assertNotEquals(rectangle, new Rectangle(0, 1, 2, 0));
        Assert.assertNotEquals(rectangle, new Rectangle(0, 1, 0, 3));
        Assert.assertNotEquals(rectangle, new Rectangle(0, 1, 0, 0));
        Assert.assertNotEquals(rectangle, new Rectangle(0, 0, 2, 3));
        Assert.assertNotEquals(rectangle, new Rectangle(0, 0, 2, 0));
        Assert.assertNotEquals(rectangle, new Rectangle(0, 0, 0, 0));
        Assert.assertNotEquals(rectangle, new Rectangle(0, 0, 0, 0));
        Assert.assertNotEquals(rectangle, new Rectangle(1, 1, 2, 3));
        Assert.assertNotEquals(rectangle, new Rectangle(0, 1, 2, 0));
        Assert.assertNotEquals(rectangle, new Rectangle(0, 1, 0, 3));
        Assert.assertNotEquals(rectangle, new Rectangle(0, 1, 0, 0));
        Assert.assertNotEquals(rectangle, new Rectangle(0, 0, 2, 3));
        Assert.assertNotEquals(rectangle, new Rectangle(0, 0, 2, 0));
        Assert.assertNotEquals(rectangle, new Rectangle(0, 0, 0, 3));
        Assert.assertNotEquals(rectangle, new Rectangle(1, 0, 0, 0));
    }

    /**
     * Test the to string.
     */
    @Test
    public void testToString()
    {
        Assert.assertEquals("Rectangle [x=0.0, y=0.0, width=0.0, height=0.0]", new Rectangle().toString());
        Assert.assertEquals("Rectangle [x=0.0, y=1.0, width=2.0, height=3.0]", new Rectangle(0, 1, 2, 3).toString());
    }
}
