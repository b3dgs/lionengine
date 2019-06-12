/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.transition;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.UtilTests;

/**
 * Test {@link TransitionType}.
 */
public final class TransitionTypeTest
{
    /**
     * Check the bits sequence.
     * 
     * @param expected The expected type.
     * @param downRight The down right flag.
     * @param downLeft The down left flag.
     * @param upRight The up right flag.
     * @param upLeft The up left flag.
     * @return <code>true</code> if valid, <code>false</code> else.
     */
    private static boolean check(TransitionType expected,
                                 boolean downRight,
                                 boolean downLeft,
                                 boolean upRight,
                                 boolean upLeft)
    {
        final TransitionType fromTable = TransitionType.from(new boolean[]
        {
            upLeft, upRight, downLeft, downRight
        });
        final TransitionType fromBits = TransitionType.from(downRight, downLeft, upRight, upLeft);

        assertEquals(fromTable, fromBits);
        assertFalse(expected.is(!downRight, downLeft, upRight, upLeft));
        assertFalse(expected.is(downRight, !downLeft, upRight, upLeft));
        assertFalse(expected.is(downRight, downLeft, !upRight, upLeft));
        assertFalse(expected.is(downRight, downLeft, upRight, !upLeft));

        return expected.is(downRight, downLeft, upRight, upLeft);
    }

    /**
     * Test the enum.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testEnum() throws Exception
    {
        UtilTests.testEnum(TransitionType.class);
    }

    /**
     * Test the enum creation from string.
     */
    @Test
    public void testFromString()
    {
        for (final TransitionType type : TransitionType.values())
        {
            assertEquals(type, TransitionType.from(type.name()));
        }
    }

    /**
     * Test the enum creation from string.
     */
    @Test
    public void testFromStringInvalid()
    {
        assertThrows(() -> TransitionType.from("null"), "Unknown transition name: null");
    }

    /**
     * Test the enum creation from bits table.
     */
    @Test
    public void testFromTable()
    {
        assertTrue(check(TransitionType.CENTER, false, false, false, false));
        assertFalse(check(TransitionType.CENTER, true, true, true, true));
        assertTrue(check(TransitionType.UP_LEFT, false, false, false, true));
        assertTrue(check(TransitionType.UP_RIGHT, false, false, true, false));
        assertTrue(check(TransitionType.UP, false, false, true, true));
        assertTrue(check(TransitionType.DOWN, true, true, false, false));
        assertTrue(check(TransitionType.DOWN_LEFT, false, true, false, false));
        assertTrue(check(TransitionType.DOWN_RIGHT, true, false, false, false));
        assertTrue(check(TransitionType.LEFT, true, false, true, false));
        assertTrue(check(TransitionType.RIGHT, false, true, false, true));
        assertTrue(check(TransitionType.UP_LEFT_DOWN_RIGHT, true, false, false, true));
        assertTrue(check(TransitionType.UP_RIGHT_DOWN_LEFT, false, true, true, false));
        assertTrue(check(TransitionType.CORNER_UP_LEFT, true, true, true, false));
        assertTrue(check(TransitionType.CORNER_UP_RIGHT, true, true, false, true));
        assertTrue(check(TransitionType.CORNER_DOWN_LEFT, true, false, true, true));
        assertTrue(check(TransitionType.CORNER_DOWN_RIGHT, false, true, true, true));
    }

    /**
     * Test the enum symmetric equality.
     */
    @Test
    public void testSymmetric()
    {
        assertEquals(TransitionType.CENTER, TransitionType.CENTER.getSymetric());
        assertEquals(TransitionType.CORNER_UP_LEFT, TransitionType.UP_LEFT.getSymetric());
        assertEquals(TransitionType.CORNER_UP_RIGHT, TransitionType.UP_RIGHT.getSymetric());
        assertEquals(TransitionType.CORNER_DOWN_LEFT, TransitionType.DOWN_LEFT.getSymetric());
        assertEquals(TransitionType.CORNER_DOWN_RIGHT, TransitionType.DOWN_RIGHT.getSymetric());
        assertEquals(TransitionType.UP, TransitionType.DOWN.getSymetric());
        assertEquals(TransitionType.DOWN, TransitionType.UP.getSymetric());
        assertEquals(TransitionType.LEFT, TransitionType.RIGHT.getSymetric());
        assertEquals(TransitionType.RIGHT, TransitionType.LEFT.getSymetric());
        assertEquals(TransitionType.UP_LEFT_DOWN_RIGHT, TransitionType.UP_RIGHT_DOWN_LEFT.getSymetric());
        assertEquals(TransitionType.UP_RIGHT_DOWN_LEFT, TransitionType.UP_LEFT_DOWN_RIGHT.getSymetric());
        assertEquals(TransitionType.CORNER_UP_LEFT, TransitionType.UP_LEFT.getSymetric());
        assertEquals(TransitionType.CORNER_UP_RIGHT, TransitionType.UP_RIGHT.getSymetric());
        assertEquals(TransitionType.CORNER_DOWN_LEFT, TransitionType.DOWN_LEFT.getSymetric());
        assertEquals(TransitionType.CORNER_DOWN_RIGHT, TransitionType.DOWN_RIGHT.getSymetric());
    }
}
