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
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.util.UtilConversion;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the circuit type enum.
 */
public class CircuitTypeTest
{
    /**
     * Check the bits sequence.
     * 
     * @param expected The expected type.
     * @param top The top flag.
     * @param left The left flag.
     * @param bottom The bottom flag.
     * @param right The right flag.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    private static boolean check(CircuitType expected, boolean top, boolean left, boolean bottom, boolean right)
    {
        final CircuitType fromTable = CircuitType.from(new boolean[]
        {
            right, bottom, left, top
        });
        final CircuitType fromBits = CircuitType.from(top, left, bottom, right);
        Assert.assertEquals(fromTable, fromBits);
        Assert.assertFalse(expected.is(!top, left, bottom, right));
        Assert.assertFalse(expected.is(top, !left, bottom, right));
        Assert.assertFalse(expected.is(top, left, !bottom, right));
        Assert.assertFalse(expected.is(top, left, bottom, !right));

        return expected.is(top, left, bottom, right);
    }

    /**
     * Test the enum.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testEnum() throws Exception
    {
        UtilTests.testEnum(CircuitType.class);
    }

    /**
     * Test the enum creation from string.
     */
    @Test
    public void testFromString()
    {
        for (final CircuitType type : CircuitType.values())
        {
            Assert.assertEquals(type, CircuitType.from(type.name()));
        }
    }

    /**
     * Test the enum creation from string.
     */
    @Test(expected = LionEngineException.class)
    public void testFromStringInvalid()
    {
        Assert.assertNull(CircuitType.from("null"));
    }

    /**
     * Test the enum creation from bits table.
     */
    @Test
    public void testFromTable()
    {
        Assert.assertTrue(check(CircuitType.BLOCK, false, false, false, false));
        Assert.assertTrue(check(CircuitType.MIDDLE, true, true, true, true));
        Assert.assertTrue(check(CircuitType.TOP, false, false, true, false));
        Assert.assertTrue(check(CircuitType.LEFT, false, false, false, true));
        Assert.assertTrue(check(CircuitType.BOTTOM, true, false, false, false));
        Assert.assertTrue(check(CircuitType.RIGHT, false, true, false, false));
        Assert.assertTrue(check(CircuitType.HORIZONTAL, false, true, false, true));
        Assert.assertTrue(check(CircuitType.VERTICAL, true, false, true, false));
        Assert.assertTrue(check(CircuitType.ANGLE_TOP_LEFT, false, false, true, true));
        Assert.assertTrue(check(CircuitType.ANGLE_TOP_RIGHT, false, true, true, false));
        Assert.assertTrue(check(CircuitType.ANGLE_BOTTOM_LEFT, true, false, false, true));
        Assert.assertTrue(check(CircuitType.ANGLE_BOTTOM_RIGHT, true, true, false, false));
        Assert.assertTrue(check(CircuitType.T3J_TOP, false, true, true, true));
        Assert.assertTrue(check(CircuitType.T3J_LEFT, true, false, true, true));
        Assert.assertTrue(check(CircuitType.T3J_BOTTOM, true, true, false, true));
        Assert.assertTrue(check(CircuitType.T3J_RIGHT, true, true, true, false));

        for (int i = 0; i < CircuitType.BITS * CircuitType.BITS; i++)
        {
            Assert.assertNotNull(CircuitType.from(UtilConversion.toBinary(i, CircuitType.BITS)));
        }
    }
}
