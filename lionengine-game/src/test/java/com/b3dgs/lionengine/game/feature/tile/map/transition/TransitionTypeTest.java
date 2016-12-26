/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.transition;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test the transition type enum.
 */
public class TransitionTypeTest
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
        Assert.assertEquals(fromTable, fromBits);
        Assert.assertFalse(expected.is(!downRight, downLeft, upRight, upLeft));
        Assert.assertFalse(expected.is(downRight, !downLeft, upRight, upLeft));
        Assert.assertFalse(expected.is(downRight, downLeft, !upRight, upLeft));
        Assert.assertFalse(expected.is(downRight, downLeft, upRight, !upLeft));

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
            Assert.assertEquals(type, TransitionType.from(type.name()));
        }
    }

    /**
     * Test the enum creation from string.
     */
    @Test(expected = LionEngineException.class)
    public void testFromStringInvalid()
    {
        Assert.assertNull(TransitionType.from("null"));
    }

    /**
     * Test the enum creation from bits table.
     */
    @Test
    public void testFromTable()
    {
        Assert.assertTrue(check(TransitionType.CENTER, false, false, false, false));
        Assert.assertFalse(check(TransitionType.CENTER, true, true, true, true));
        Assert.assertTrue(check(TransitionType.UP_LEFT, false, false, false, true));
        Assert.assertTrue(check(TransitionType.UP_RIGHT, false, false, true, false));
        Assert.assertTrue(check(TransitionType.UP, false, false, true, true));
        Assert.assertTrue(check(TransitionType.DOWN, true, true, false, false));
        Assert.assertTrue(check(TransitionType.DOWN_LEFT, false, true, false, false));
        Assert.assertTrue(check(TransitionType.DOWN_RIGHT, true, false, false, false));
        Assert.assertTrue(check(TransitionType.LEFT, true, false, true, false));
        Assert.assertTrue(check(TransitionType.RIGHT, false, true, false, true));
        Assert.assertTrue(check(TransitionType.UP_LEFT_DOWN_RIGHT, true, false, false, true));
        Assert.assertTrue(check(TransitionType.UP_RIGHT_DOWN_LEFT, false, true, true, false));
        Assert.assertTrue(check(TransitionType.CORNER_UP_LEFT, true, true, true, false));
        Assert.assertTrue(check(TransitionType.CORNER_UP_RIGHT, true, true, false, true));
        Assert.assertTrue(check(TransitionType.CORNER_DOWN_LEFT, true, false, true, true));
        Assert.assertTrue(check(TransitionType.CORNER_DOWN_RIGHT, false, true, true, true));
    }

    /**
     * Test the enum symmetric equality.
     */
    @Test
    public void testSymmetric()
    {
        Assert.assertEquals(TransitionType.CENTER, TransitionType.CENTER.getSymetric());
        Assert.assertEquals(TransitionType.CORNER_UP_LEFT, TransitionType.UP_LEFT.getSymetric());
        Assert.assertEquals(TransitionType.CORNER_UP_RIGHT, TransitionType.UP_RIGHT.getSymetric());
        Assert.assertEquals(TransitionType.CORNER_DOWN_LEFT, TransitionType.DOWN_LEFT.getSymetric());
        Assert.assertEquals(TransitionType.CORNER_DOWN_RIGHT, TransitionType.DOWN_RIGHT.getSymetric());
        Assert.assertEquals(TransitionType.UP, TransitionType.DOWN.getSymetric());
        Assert.assertEquals(TransitionType.DOWN, TransitionType.UP.getSymetric());
        Assert.assertEquals(TransitionType.LEFT, TransitionType.RIGHT.getSymetric());
        Assert.assertEquals(TransitionType.RIGHT, TransitionType.LEFT.getSymetric());
        Assert.assertEquals(TransitionType.UP_LEFT_DOWN_RIGHT, TransitionType.UP_RIGHT_DOWN_LEFT.getSymetric());
        Assert.assertEquals(TransitionType.UP_RIGHT_DOWN_LEFT, TransitionType.UP_LEFT_DOWN_RIGHT.getSymetric());
        Assert.assertEquals(TransitionType.CORNER_UP_LEFT, TransitionType.UP_LEFT.getSymetric());
        Assert.assertEquals(TransitionType.CORNER_UP_RIGHT, TransitionType.UP_RIGHT.getSymetric());
        Assert.assertEquals(TransitionType.CORNER_DOWN_LEFT, TransitionType.DOWN_LEFT.getSymetric());
        Assert.assertEquals(TransitionType.CORNER_DOWN_RIGHT, TransitionType.DOWN_RIGHT.getSymetric());
    }
}
