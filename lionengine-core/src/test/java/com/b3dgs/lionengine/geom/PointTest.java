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

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Point}.
 */
public final class PointTest
{
    /**
     * Test default constructor.
     */
    @Test
    public void testConstructorDefault()
    {
        final Point point = new Point();

        assertEquals(0, point.getX());
        assertEquals(0, point.getY());
    }

    /**
     * Test constructor with parameters.
     */
    @Test
    public void testConstructorParameters()
    {
        final Point point = new Point(1, 2);

        assertEquals(1, point.getX());
        assertEquals(2, point.getY());
    }

    /**
     * Test translate.
     */
    @Test
    public void testTranslate()
    {
        final Point point = new Point(1, 2);
        point.translate(10, 20);

        assertEquals(11, point.getX());
        assertEquals(22, point.getY());
    }

    /**
     * Test set.
     */
    @Test
    public void testSet()
    {
        final Point point = new Point();
        point.set(10, 20);

        assertEquals(10, point.getX());
        assertEquals(20, point.getY());
    }

    /**
     * Test set X.
     */
    @Test
    public void testSetX()
    {
        final Point point = new Point(1, 2);
        point.setX(10);

        assertEquals(10, point.getX());
        assertEquals(2, point.getY());
    }

    /**
     * Test set Y.
     */
    @Test
    public void testSetY()
    {
        final Point point = new Point(1, 2);
        point.setY(20);

        assertEquals(1, point.getX());
        assertEquals(20, point.getY());
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final Point point = new Point();

        assertEquals(point, point);

        assertEquals(new Point(), new Point());
        assertEquals(new Point(1, 2), new Point(1, 2));

        assertNotEquals(new Point(), null);
        assertNotEquals(new Point(), new Object());
        assertNotEquals(new Point(1, 0), new Point(1, 1));
        assertNotEquals(new Point(0, 1), new Point(1, 0));
        assertNotEquals(new Point(), new Point(1, 1));
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        assertEquals(new Point(1, 2), new Point(1, 2));

        assertHashNotEquals(new Point(), new Object());
        assertHashNotEquals(new Point(1, 0), new Point(1, 1));
        assertHashNotEquals(new Point(0, 1), new Point(1, 0));
        assertHashNotEquals(new Point(), new Point(1, 1));
    }

    /**
     * Test to string.
     */
    @Test
    public void testToString()
    {
        assertEquals("Point [x=1, y=2]", new Point(1, 2).toString());
    }
}
