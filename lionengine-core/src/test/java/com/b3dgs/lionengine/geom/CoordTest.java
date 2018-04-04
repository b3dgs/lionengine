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

import com.b3dgs.lionengine.UtilTests;

/**
 * Test {@link Coord}.
 */
public final class CoordTest
{
    /**
     * Test default constructor.
     */
    @Test
    public void testConstructorDefault()
    {
        final Coord coord = new Coord();

        Assert.assertEquals(0.0, coord.getX(), UtilTests.PRECISION);
        Assert.assertEquals(0.0, coord.getY(), UtilTests.PRECISION);
    }

    /**
     * Test constructor with parameters.
     */
    @Test
    public void testConstructorParameters()
    {
        final Coord coord = new Coord(1.5, 2.5);

        Assert.assertEquals(1.5, coord.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.5, coord.getY(), UtilTests.PRECISION);
    }

    /**
     * Test translate.
     */
    @Test
    public void testTranslate()
    {
        final Coord coord = new Coord(1.5, 2.5);
        coord.translate(10.25, 20.25);

        Assert.assertEquals(11.75, coord.getX(), UtilTests.PRECISION);
        Assert.assertEquals(22.75, coord.getY(), UtilTests.PRECISION);
    }

    /**
     * Test set.
     */
    @Test
    public void testSet()
    {
        final Coord coord = new Coord();
        coord.set(10.25, 20.25);

        Assert.assertEquals(10.25, coord.getX(), UtilTests.PRECISION);
        Assert.assertEquals(20.25, coord.getY(), UtilTests.PRECISION);
    }

    /**
     * Test set X.
     */
    @Test
    public void testSetX()
    {
        final Coord coord = new Coord(1.5, 2.5);
        coord.setX(10.25);

        Assert.assertEquals(10.25, coord.getX(), UtilTests.PRECISION);
        Assert.assertEquals(2.5, coord.getY(), UtilTests.PRECISION);
    }

    /**
     * Test set Y.
     */
    @Test
    public void testSetY()
    {
        final Coord coord = new Coord(1.5, 2.5);
        coord.setY(20.25);

        Assert.assertEquals(1.5, coord.getX(), UtilTests.PRECISION);
        Assert.assertEquals(20.25, coord.getY(), UtilTests.PRECISION);
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final Coord coord = new Coord();

        Assert.assertEquals(coord, coord);

        Assert.assertEquals(new Coord(), new Coord());
        Assert.assertEquals(new Coord(1.5, 2.5), new Coord(1.5, 2.5));

        Assert.assertNotEquals(new Coord(), null);
        Assert.assertNotEquals(new Coord(), new Object());
        Assert.assertNotEquals(new Coord(1.0, 0.0), new Coord(1.0, 1.0));
        Assert.assertNotEquals(new Coord(0.0, 1.0), new Coord(1.0, 0.0));
        Assert.assertNotEquals(new Coord(), new Coord(1.0, 1.0));
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        Assert.assertEquals(new Coord().hashCode(), new Coord().hashCode());
        Assert.assertEquals(new Coord(1.5, 2.5).hashCode(), new Coord(1.5, 2.5).hashCode());

        Assert.assertNotEquals(new Coord().hashCode(), new Object().hashCode());
        Assert.assertNotEquals(new Coord(1.0, 0.0).hashCode(), new Coord(1.0, 1.0).hashCode());
        Assert.assertNotEquals(new Coord(0.0, 1.0).hashCode(), new Coord(1.0, 0.0).hashCode());
        Assert.assertNotEquals(new Coord().hashCode(), new Coord(1.0, 1.0).hashCode());
    }

    /**
     * Test the to string.
     */
    @Test
    public void testToString()
    {
        Assert.assertEquals("Coord [x=1.5, y=2.5]", new Coord(1.5, 2.5).toString());
    }
}
