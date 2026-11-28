/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

package com.b3dgs.lionengine.physics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests of {@link Box}: bounds, validation and the strict overlap test (REQ-PHYS-001, ADR-0082).
 */
final class BoxTest
{
    /**
     * A new box is at the origin with a size of zero.
     */
    @Test
    void testDefault()
    {
        final Box box = new Box();

        assertEquals(0.0, box.getX());
        assertEquals(0.0, box.getY());
        assertEquals(0.0, box.getWidth());
        assertEquals(0.0, box.getHeight());
    }

    /**
     * The bounds are the center minus and plus half of the size.
     */
    @Test
    void testBounds()
    {
        final Box box = new Box(1.0, -2.0, 3.0, 0.5);

        assertEquals(1.0, box.getX());
        assertEquals(-2.0, box.getY());
        assertEquals(3.0, box.getWidth());
        assertEquals(0.5, box.getHeight());
        assertEquals(-0.5, box.getMinX());
        assertEquals(2.5, box.getMaxX());
        assertEquals(-2.25, box.getMinY());
        assertEquals(-1.75, box.getMaxY());
        assertEquals("Box[x=1.0, y=-2.0, width=3.0, height=0.5]", box.toString());
    }

    /**
     * Setting copies the values, and copying another box takes its values.
     */
    @Test
    void testSet()
    {
        final Box box = new Box();
        box.set(4.0, 5.0, 6.0, 7.0);
        final Box copy = new Box();
        copy.set(box);

        assertEquals(4.0, copy.getX());
        assertEquals(5.0, copy.getY());
        assertEquals(6.0, copy.getWidth());
        assertEquals(7.0, copy.getHeight());
        assertThrows(IllegalArgumentException.class, () -> copy.set(null));
    }

    /**
     * A center that is not finite, or a size negative or not finite, is refused and leaves the box unchanged.
     *
     * @param x The center X.
     * @param y The center Y.
     * @param width The width.
     * @param height The height.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "NaN, 0, 1, 1", "0, Infinity, 1, 1", "-Infinity, 0, 1, 1", "0, 0, -0.5, 1", "0, 0, 1, -1", "0, 0, NaN, 1",
        "0, 0, 1, Infinity"
    })
    void testInvalid(double x, double y, double width, double height)
    {
        final Box box = new Box(1.0, 2.0, 3.0, 4.0);

        assertThrows(IllegalArgumentException.class, () -> box.set(x, y, width, height));
        assertThrows(IllegalArgumentException.class, () -> new Box(x, y, width, height));
        assertEquals(1.0, box.getX());
        assertEquals(4.0, box.getHeight());
    }

    /**
     * Boxes overlap when their interiors share an area, in both directions; boxes touching by an edge or a corner, or
     * apart, do not.
     *
     * @param x The center X of the second box, the first being the unit box at the origin.
     * @param y The center Y of the second box.
     * @param width The width of the second box.
     * @param height The height of the second box.
     * @param expected The expected overlap.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "0, 0, 1, 1, true", "0.5, 0.5, 1, 1, true", "0.99, 0, 1, 1, true", "0, 0, 0.1, 0.1, true",
        "0, 0, 4, 4, true", "1, 0, 1, 1, false", "-1, 0, 1, 1, false", "0, 1, 1, 1, false", "0, -1, 1, 1, false",
        "1, 1, 1, 1, false", "2, 0, 1, 1, false", "0, 0.7, 1, 0.5, true", "0, 0, 0, 0, true", "0.5, 0, 0, 1, false"
    })
    void testOverlaps(double x, double y, double width, double height, boolean expected)
    {
        final Box unit = new Box(0.0, 0.0, 1.0, 1.0);
        final Box other = new Box(x, y, width, height);

        assertEquals(expected, unit.overlaps(other));
        assertEquals(expected, other.overlaps(unit));
    }

    /**
     * The interval test is strict at the bounds.
     */
    @Test
    void testIntervals()
    {
        assertTrue(Box.overlaps(0.0, 1.0, 0.5, 2.0));
        assertTrue(Box.overlaps(0.5, 2.0, 0.0, 1.0));
        assertFalse(Box.overlaps(0.0, 1.0, 1.0, 2.0));
        assertFalse(Box.overlaps(1.0, 2.0, 0.0, 1.0));
        assertFalse(Box.overlaps(0.0, 1.0, 2.0, 3.0));
    }

    /**
     * A {@code null} box is refused.
     */
    @Test
    void testNull()
    {
        final Box box = new Box();

        assertThrows(IllegalArgumentException.class, () -> box.overlaps(null));
    }
}
