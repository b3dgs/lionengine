/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.geom;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Coord}.
 */
final class CoordTest
{
    /**
     * Test default constructor.
     */
    @Test
    void testConstructorDefault()
    {
        final Coord coord = new Coord();

        assertEquals(0.0, coord.getX());
        assertEquals(0.0, coord.getY());
    }

    /**
     * Test constructor with parameters.
     */
    @Test
    void testConstructorParameters()
    {
        final Coord coord = new Coord(1.5, 2.5);

        assertEquals(1.5, coord.getX());
        assertEquals(2.5, coord.getY());
    }

    /**
     * Test translate.
     */
    @Test
    void testTranslate()
    {
        final Coord coord = new Coord(1.5, 2.5);
        coord.translate(10.25, 20.25);

        assertEquals(11.75, coord.getX());
        assertEquals(22.75, coord.getY());
    }

    /**
     * Test set.
     */
    @Test
    void testSet()
    {
        final Coord coord = new Coord();
        coord.set(10.25, 20.25);

        assertEquals(10.25, coord.getX());
        assertEquals(20.25, coord.getY());
    }

    /**
     * Test set X.
     */
    @Test
    void testSetX()
    {
        final Coord coord = new Coord(1.5, 2.5);
        coord.setX(10.25);

        assertEquals(10.25, coord.getX());
        assertEquals(2.5, coord.getY());
    }

    /**
     * Test set Y.
     */
    @Test
    void testSetY()
    {
        final Coord coord = new Coord(1.5, 2.5);
        coord.setY(20.25);

        assertEquals(1.5, coord.getX());
        assertEquals(20.25, coord.getY());
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final Coord coord = new Coord();

        assertEquals(coord, coord);

        assertEquals(new Coord(), new Coord());
        assertEquals(new Coord(1.5, 2.5), new Coord(1.5, 2.5));

        assertNotEquals(new Coord(), null);
        assertNotEquals(new Coord(), new Object());
        assertNotEquals(new Coord(1.0, 0.0), new Coord(1.0, 1.0));
        assertNotEquals(new Coord(0.0, 1.0), new Coord(1.0, 0.0));
        assertNotEquals(new Coord(), new Coord(1.0, 1.0));
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        assertHashEquals(new Coord(), new Coord());
        assertHashEquals(new Coord(1.5, 2.5), new Coord(1.5, 2.5));

        assertHashNotEquals(new Coord(), new Object());
        assertHashNotEquals(new Coord(1.0, 0.0), new Coord(1.0, 1.0));
        assertHashNotEquals(new Coord(0.0, 1.0), new Coord(1.0, 0.0));
        assertHashNotEquals(new Coord(), new Coord(1.0, 1.0));
    }

    /**
     * Test the to string.
     */
    @Test
    void testToString()
    {
        assertEquals("Coord [x=1.5, y=2.5]", new Coord(1.5, 2.5).toString());
    }
}
