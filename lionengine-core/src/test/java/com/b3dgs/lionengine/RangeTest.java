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
package com.b3dgs.lionengine;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test {@link Range}.
 */
public final class RangeTest
{
    /**
     * Test constructor with invalid argument.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructorWrongArguments()
    {
        Assert.assertNotNull(new Range(1, 0));
    }

    /**
     * Test default constructor.
     */
    @Test
    public void testConstructorDefault()
    {
        final Range range = new Range();

        Assert.assertEquals(0, range.getMin());
        Assert.assertEquals(0, range.getMax());
    }

    /**
     * Test constructor with parameters.
     */
    @Test
    public void testConstructorParameters()
    {
        final Range range = new Range(1, 2);

        Assert.assertEquals(1, range.getMin());
        Assert.assertEquals(2, range.getMax());
    }

    /**
     * Test inclusion.
     */
    @Test
    public void testIncludes()
    {
        final Range range = new Range(-1, 1);

        Assert.assertTrue(range.includes(0));
        Assert.assertTrue(range.includes(0.0));
        Assert.assertTrue(range.includes(Double.MIN_NORMAL));

        Assert.assertTrue(range.includes(range.getMin()));
        Assert.assertTrue(range.includes(range.getMax()));
        Assert.assertTrue(range.includes(range.getMin() + 1));
        Assert.assertTrue(range.includes(range.getMax() - 1));

        Assert.assertFalse(range.includes(range.getMax() + 1));
        Assert.assertFalse(range.includes(range.getMin() - 1));

        Assert.assertTrue(range.includes((double) range.getMin()));
        Assert.assertTrue(range.includes((double) range.getMax()));
        Assert.assertTrue(range.includes(range.getMin() + 0.000000000000001));
        Assert.assertTrue(range.includes(range.getMax() - 0.000000000000001));

        Assert.assertFalse(range.includes(range.getMax() + 0.000000000000001));
        Assert.assertFalse(range.includes(range.getMin() - 0.000000000000001));
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final Range range = new Range(1, 2);

        Assert.assertEquals(range, range);
        Assert.assertEquals(range, new Range(1, 2));

        Assert.assertNotEquals(range, null);
        Assert.assertNotEquals(range, new Object());
        Assert.assertNotEquals(range, new Range(2, 2));
        Assert.assertNotEquals(range, new Range(1, 1));
    }

    /**
     * Test equals.
     */
    @Test
    public void testHashCode()
    {
        final int range = new Range(1, 2).hashCode();

        Assert.assertEquals(range, new Range(1, 2).hashCode());

        Assert.assertNotEquals(range, new Object().hashCode());
        Assert.assertNotEquals(range, new Range(2, 2).hashCode());
        Assert.assertNotEquals(range, new Range(1, 1).hashCode());
    }

    /**
     * Test to string.
     */
    @Test
    public void testToString()
    {
        Assert.assertEquals("Range [min=1, max=2]", new Range(1, 2).toString());
    }
}
