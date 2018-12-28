/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Range}.
 */
public final class RangeTest
{
    /**
     * Test constructor with invalid argument.
     */
    @Test
    public void testConstructorWrongArguments()
    {
        assertThrows(() -> new Range(1, 0), Check.ERROR_ARGUMENT + 1 + Check.ERROR_INFERIOR + 0);
    }

    /**
     * Test default constructor.
     */
    @Test
    public void testConstructorDefault()
    {
        final Range range = new Range();

        assertEquals(0, range.getMin());
        assertEquals(0, range.getMax());
    }

    /**
     * Test constructor with parameters.
     */
    @Test
    public void testConstructorParameters()
    {
        final Range range = new Range(1, 2);

        assertEquals(1, range.getMin());
        assertEquals(2, range.getMax());
    }

    /**
     * Test inclusion.
     */
    @Test
    public void testIncludes()
    {
        final Range range = new Range(-1, 1);

        assertTrue(range.includes(0));
        assertTrue(range.includes(0.0));
        assertTrue(range.includes(Double.MIN_NORMAL));

        assertTrue(range.includes(range.getMin()));
        assertTrue(range.includes(range.getMax()));
        assertTrue(range.includes(range.getMin() + 1));
        assertTrue(range.includes(range.getMax() - 1));

        assertFalse(range.includes(range.getMax() + 1));
        assertFalse(range.includes(range.getMin() - 1));

        assertTrue(range.includes((double) range.getMin()));
        assertTrue(range.includes((double) range.getMax()));
        assertTrue(range.includes(range.getMin() + 0.000000000000001));
        assertTrue(range.includes(range.getMax() - 0.000000000000001));

        assertFalse(range.includes(range.getMax() + 0.000000000000001));
        assertFalse(range.includes(range.getMin() - 0.000000000000001));
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final Range range = new Range(1, 2);

        assertEquals(range, range);
        assertEquals(range, new Range(1, 2));

        assertNotEquals(range, null);
        assertNotEquals(range, new Object());
        assertNotEquals(range, new Range(2, 2));
        assertNotEquals(range, new Range(1, 1));
    }

    /**
     * Test equals.
     */
    @Test
    public void testHashCode()
    {
        final Range range = new Range(1, 2);

        assertHashEquals(range, new Range(1, 2));

        assertHashNotEquals(range, new Object());
        assertHashNotEquals(range, new Range(2, 2));
        assertHashNotEquals(range, new Range(1, 1));
    }

    /**
     * Test to string.
     */
    @Test
    public void testToString()
    {
        assertEquals("Range [min=1, max=2]", new Range(1, 2).toString());
    }
}
