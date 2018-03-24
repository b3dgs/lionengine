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
 * Test {@link Line}.
 */
public final class LineTest
{
    /**
     * Test the default constructor.
     */
    @Test
    public void testConstructorDefault()
    {
        final Line line = new Line();

        Assert.assertEquals(0.0, line.getX1(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, line.getY1(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, line.getX2(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, line.getY2(), UtilTests.PRECISION);
    }

    /**
     * Test constructor with parameters.
     */
    @Test
    public void testConstructorParameters()
    {
        final Line line = new Line(1.5, 2.5, 3.5, 4.5);

        Assert.assertEquals(1.5, line.getX1(), UtilTests.PRECISION);
        Assert.assertEquals(2.5, line.getY1(), UtilTests.PRECISION);
        Assert.assertEquals(3.5, line.getX2(), UtilTests.PRECISION);
        Assert.assertEquals(4.5, line.getY2(), UtilTests.PRECISION);
    }

    /**
     * Test set.
     */
    @Test
    public void testSet()
    {
        final Line line = new Line();
        line.set(1.5, 2.5, 3.5, 4.5);

        Assert.assertEquals(1.5, line.getX1(), UtilTests.PRECISION);
        Assert.assertEquals(2.5, line.getY1(), UtilTests.PRECISION);
        Assert.assertEquals(3.5, line.getX2(), UtilTests.PRECISION);
        Assert.assertEquals(4.5, line.getY2(), UtilTests.PRECISION);
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final Line coord = new Line();

        Assert.assertEquals(coord, coord);

        Assert.assertEquals(new Line(), new Line());
        Assert.assertEquals(new Line(1.5, 2.5, 3.5, 4.5), new Line(1.5, 2.5, 3.5, 4.5));

        Assert.assertNotEquals(new Line(), null);
        Assert.assertNotEquals(new Line(), new Object());
        Assert.assertNotEquals(new Line(1.5, 2.5, 3.5, 4.5), new Line(2.5, 2.5, 3.5, 4.5));
        Assert.assertNotEquals(new Line(1.5, 2.5, 3.5, 4.5), new Line(1.5, 1.5, 3.5, 4.5));
        Assert.assertNotEquals(new Line(1.5, 2.5, 3.5, 4.5), new Line(1.5, 2.5, 2.5, 4.5));
        Assert.assertNotEquals(new Line(1.5, 2.5, 3.5, 4.5), new Line(1.5, 2.5, 3.5, 1.5));
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        Assert.assertEquals(new Line().hashCode(), new Line().hashCode());
        Assert.assertEquals(new Line(1.5, 2.5, 3.5, 4.5).hashCode(), new Line(1.5, 2.5, 3.5, 4.5).hashCode());

        Assert.assertNotEquals(new Line().hashCode(), new Object().hashCode());
        Assert.assertNotEquals(new Line(1.5, 2.5, 3.5, 4.5).hashCode(), new Line(2.5, 2.5, 3.5, 4.5).hashCode());
        Assert.assertNotEquals(new Line(1.5, 2.5, 3.5, 4.5).hashCode(), new Line(1.5, 1.5, 3.5, 4.5).hashCode());
        Assert.assertNotEquals(new Line(1.5, 2.5, 3.5, 4.5).hashCode(), new Line(1.5, 2.5, 2.5, 4.5).hashCode());
        Assert.assertNotEquals(new Line(1.5, 2.5, 3.5, 4.5).hashCode(), new Line(1.5, 2.5, 3.5, 1.5).hashCode());
    }

    /**
     * Test to string.
     */
    @Test
    public void testToString()
    {
        Assert.assertEquals("Line [x1=1.5, y1=2.5, x2=3.5, y2=4.5]", new Line(1.5, 2.5, 3.5, 4.5).toString());
    }
}
