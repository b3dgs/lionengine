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
 * Test {@link Line}.
 */
final class LineTest
{
    /**
     * Test the default constructor.
     */
    @Test
    void testConstructorDefault()
    {
        final Line line = new Line();

        assertEquals(0.0, line.getX1());
        assertEquals(0.0, line.getY1());
        assertEquals(0.0, line.getX2());
        assertEquals(0.0, line.getY2());
    }

    /**
     * Test constructor with parameters.
     */
    @Test
    void testConstructorParameters()
    {
        final Line line = new Line(1.5, 2.5, 3.5, 4.5);

        assertEquals(1.5, line.getX1());
        assertEquals(2.5, line.getY1());
        assertEquals(3.5, line.getX2());
        assertEquals(4.5, line.getY2());
    }

    /**
     * Test set.
     */
    @Test
    void testSet()
    {
        final Line line = new Line();
        line.set(1.5, 2.5, 3.5, 4.5);

        assertEquals(1.5, line.getX1());
        assertEquals(2.5, line.getY1());
        assertEquals(3.5, line.getX2());
        assertEquals(4.5, line.getY2());
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final Line coord = new Line();

        assertEquals(coord, coord);

        assertEquals(new Line(), new Line());
        assertEquals(new Line(1.5, 2.5, 3.5, 4.5), new Line(1.5, 2.5, 3.5, 4.5));

        assertNotEquals(new Line(), null);
        assertNotEquals(new Line(), new Object());
        assertNotEquals(new Line(1.5, 2.5, 3.5, 4.5), new Line(2.5, 2.5, 3.5, 4.5));
        assertNotEquals(new Line(1.5, 2.5, 3.5, 4.5), new Line(1.5, 1.5, 3.5, 4.5));
        assertNotEquals(new Line(1.5, 2.5, 3.5, 4.5), new Line(1.5, 2.5, 2.5, 4.5));
        assertNotEquals(new Line(1.5, 2.5, 3.5, 4.5), new Line(1.5, 2.5, 3.5, 1.5));
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        assertHashEquals(new Line(), new Line());
        assertHashEquals(new Line(1.5, 2.5, 3.5, 4.5), new Line(1.5, 2.5, 3.5, 4.5));

        assertHashNotEquals(new Line(), new Object());
        assertHashNotEquals(new Line(1.5, 2.5, 3.5, 4.5), new Line(2.5, 2.5, 3.5, 4.5));
        assertHashNotEquals(new Line(1.5, 2.5, 3.5, 4.5), new Line(1.5, 1.5, 3.5, 4.5));
        assertHashNotEquals(new Line(1.5, 2.5, 3.5, 4.5), new Line(1.5, 2.5, 2.5, 4.5));
        assertHashNotEquals(new Line(1.5, 2.5, 3.5, 4.5), new Line(1.5, 2.5, 3.5, 1.5));
    }

    /**
     * Test to string.
     */
    @Test
    void testToString()
    {
        assertEquals("Line [x1=1.5, y1=2.5, x2=3.5, y2=4.5]", new Line(1.5, 2.5, 3.5, 4.5).toString());
    }
}
