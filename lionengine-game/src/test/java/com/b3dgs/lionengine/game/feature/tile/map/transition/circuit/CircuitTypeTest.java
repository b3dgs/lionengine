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
package com.b3dgs.lionengine.game.feature.tile.map.transition.circuit;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.UtilTests;

/**
 * Test {@link CircuitType}.
 */
final class CircuitTypeTest
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

        assertEquals(fromTable, fromBits);
        assertFalse(expected.is(!top, left, bottom, right));
        assertFalse(expected.is(top, !left, bottom, right));
        assertFalse(expected.is(top, left, !bottom, right));
        assertFalse(expected.is(top, left, bottom, !right));

        return expected.is(top, left, bottom, right);
    }

    /**
     * Test the enum.
     * 
     * @throws Exception If error.
     */
    @Test
    void testEnum() throws Exception
    {
        UtilTests.testEnum(CircuitType.class);
    }

    /**
     * Test the enum creation from string.
     */
    @Test
    void testFromString()
    {
        for (final CircuitType type : CircuitType.values())
        {
            assertEquals(type, CircuitType.from(type.name()));
        }
    }

    /**
     * Test the enum creation from string.
     */
    @Test
    void testFromStringInvalid()
    {
        assertThrows(() -> CircuitType.from("null"), "Unknown circuit part name: null");
    }

    /**
     * Test the enum creation from bits table.
     */
    @Test
    void testFromTable()
    {
        assertTrue(check(CircuitType.BLOCK, false, false, false, false));
        assertTrue(check(CircuitType.MIDDLE, true, true, true, true));
        assertTrue(check(CircuitType.TOP, false, false, true, false));
        assertTrue(check(CircuitType.LEFT, false, false, false, true));
        assertTrue(check(CircuitType.BOTTOM, true, false, false, false));
        assertTrue(check(CircuitType.RIGHT, false, true, false, false));
        assertTrue(check(CircuitType.HORIZONTAL, false, true, false, true));
        assertTrue(check(CircuitType.VERTICAL, true, false, true, false));
        assertTrue(check(CircuitType.ANGLE_TOP_LEFT, false, false, true, true));
        assertTrue(check(CircuitType.ANGLE_TOP_RIGHT, false, true, true, false));
        assertTrue(check(CircuitType.ANGLE_BOTTOM_LEFT, true, false, false, true));
        assertTrue(check(CircuitType.ANGLE_BOTTOM_RIGHT, true, true, false, false));
        assertTrue(check(CircuitType.T3J_TOP, false, true, true, true));
        assertTrue(check(CircuitType.T3J_LEFT, true, false, true, true));
        assertTrue(check(CircuitType.T3J_BOTTOM, true, true, false, true));
        assertTrue(check(CircuitType.T3J_RIGHT, true, true, true, false));

        for (int i = 0; i < CircuitType.BITS * CircuitType.BITS; i++)
        {
            assertNotNull(CircuitType.from(UtilConversion.toBinary(i, CircuitType.BITS)));
        }
    }
}
