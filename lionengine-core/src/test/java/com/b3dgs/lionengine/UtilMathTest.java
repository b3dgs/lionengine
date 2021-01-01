/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertFalse;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.geom.Geom;

/**
 * Test {@link UtilMath}.
 */
final class UtilMathTest
{
    /**
     * Get pair in array.
     * 
     * @param a The a value.
     * @param b The b value.
     * @return The pair in array.
     */
    private static int[] pair(int a, int b)
    {
        return new int[]
        {
            a, b
        };
    }

    /**
     * Test the constructor.
     */
    @Test
    void testConstructorPrivate()
    {
        assertPrivateConstructor(UtilMath.class);
    }

    /**
     * Test the is between function.
     */
    @Test
    void testIsBetween()
    {
        assertTrue(UtilMath.isBetween(0, 0, 2));
        assertTrue(UtilMath.isBetween(1, 0, 2));
        assertTrue(UtilMath.isBetween(2, 0, 2));

        assertFalse(UtilMath.isBetween(3, 0, 2));
        assertFalse(UtilMath.isBetween(-1, 0, 2));

        assertFalse(UtilMath.isBetween(1, 2, 1));

        assertTrue(UtilMath.isBetween(0.0, 0.0, 0.2));
        assertTrue(UtilMath.isBetween(0.1, 0.0, 0.2));
        assertTrue(UtilMath.isBetween(0.2, 0.0, 0.2));

        assertFalse(UtilMath.isBetween(0.3, 0.0, 0.2));
        assertFalse(UtilMath.isBetween(-0.1, 0.0, 0.2));

        assertFalse(UtilMath.isBetween(0.1, 0.2, 0.1));
    }

    /**
     * Test the clamp function.
     */
    @Test
    void testClamp()
    {
        assertEquals(0, UtilMath.clamp(-10, 0, 10));
        assertEquals(10, UtilMath.clamp(50, 0, 10));
        assertEquals(10, UtilMath.clamp(10, 0, 10));
        assertEquals(0, UtilMath.clamp(0, 0, 10));

        assertEquals(0L, UtilMath.clamp(-10L, 0L, 10L));
        assertEquals(10L, UtilMath.clamp(50L, 0L, 10L));
        assertEquals(10L, UtilMath.clamp(10L, 0L, 10L));
        assertEquals(0L, UtilMath.clamp(0L, 0L, 10L));

        assertEquals(0.0, UtilMath.clamp(-10.0, 0.0, 10.0));
        assertEquals(10.0, UtilMath.clamp(50.0, 0.0, 10.0));
        assertEquals(10.0, UtilMath.clamp(10.0, 0.0, 10.0));
        assertEquals(0.0, UtilMath.clamp(0.0, 0.0, 10.0));
    }

    /**
     * Test the rounded function.
     */
    @Test
    void testRounded()
    {
        assertEquals(0, UtilMath.getRounded(99, 100));
        assertEquals(100, UtilMath.getRounded(149, 100));
        assertEquals(100, UtilMath.getRounded(150, 100));
        assertEquals(100, UtilMath.getRounded(151, 100));
        assertEquals(100, UtilMath.getRounded(199, 100));
    }

    /**
     * Test the rounded round function.
     */
    @Test
    void testRoundedR()
    {
        assertEquals(0, UtilMath.getRoundedR(49, 100));
        assertEquals(100, UtilMath.getRoundedR(99, 100));
        assertEquals(100, UtilMath.getRoundedR(149, 100));
        assertEquals(200, UtilMath.getRoundedR(150, 100));
        assertEquals(200, UtilMath.getRoundedR(151, 100));
        assertEquals(200, UtilMath.getRoundedR(199, 100));
    }

    /**
     * Test the rounded ceil function.
     */
    @Test
    void testRoundedC()
    {
        assertEquals(100, UtilMath.getRoundedC(49, 100));
        assertEquals(100, UtilMath.getRoundedC(99, 100));
        assertEquals(200, UtilMath.getRoundedC(149, 100));
        assertEquals(200, UtilMath.getRoundedC(150, 100));
        assertEquals(200, UtilMath.getRoundedC(151, 100));
        assertEquals(200, UtilMath.getRoundedC(199, 100));
    }

    /**
     * Test the curve value function.
     */
    @Test
    void testCurveValue()
    {
        assertTrue(UtilMath.curveValue(0.0, 1.0, 0.5) > 0.0);
        assertTrue(UtilMath.curveValue(0.0, -1.0, 0.5) < 0.0);
    }

    /**
     * Test the distance function between two localizable.
     */
    @Test
    void testDistanceLocalizable()
    {
        assertEquals(0.0, UtilMath.getDistance(Geom.createLocalizable(0.0, 0.0), Geom.createLocalizable(0.0, 0.0)));
        assertEquals(1.0, UtilMath.getDistance(Geom.createLocalizable(0.0, 0.0), Geom.createLocalizable(1.0, 0.0)));
        assertEquals(1.0, UtilMath.getDistance(Geom.createLocalizable(0.0, 0.0), Geom.createLocalizable(0.0, 1.0)));
        assertEquals(1.0, UtilMath.getDistance(Geom.createLocalizable(1.0, 0.0), Geom.createLocalizable(0.0, 0.0)));
        assertEquals(1.0, UtilMath.getDistance(Geom.createLocalizable(0.0, 1.0), Geom.createLocalizable(0.0, 0.0)));
        assertEquals(Math.sqrt(2),
                     UtilMath.getDistance(Geom.createLocalizable(1.0, 1.0), Geom.createLocalizable(2.0, 2.0)));
        assertEquals(2.0, UtilMath.getDistance(Geom.createLocalizable(4.0, 6.0), Geom.createLocalizable(6.0, 6.0)));
        assertEquals(2.0, UtilMath.getDistance(Geom.createLocalizable(-4.0, -6.0), Geom.createLocalizable(-6.0, -6.0)));
    }

    /**
     * Test the distance function between two points.
     */
    @Test
    void testDistancePointPoint()
    {
        assertEquals(0.0, UtilMath.getDistance(0.0, 0.0, 0.0, 0.0));
        assertEquals(1.0, UtilMath.getDistance(0.0, 0.0, 1.0, 0.0));
        assertEquals(1.0, UtilMath.getDistance(0.0, 0.0, 0.0, 1.0));
        assertEquals(1.0, UtilMath.getDistance(1.0, 0.0, 0.0, 0.0));
        assertEquals(1.0, UtilMath.getDistance(0.0, 1.0, 0.0, 0.0));
        assertEquals(Math.sqrt(2), UtilMath.getDistance(1.0, 1.0, 2.0, 2.0));
        assertEquals(2.0, UtilMath.getDistance(4.0, 6.0, 6.0, 6.0));
        assertEquals(2.0, UtilMath.getDistance(-4.0, -6.0, -6.0, -6.0));
    }

    /**
     * Test the distance function from point to area.
     */
    @Test
    void testDistancePointArea()
    {
        assertEquals(0.0, UtilMath.getDistance(0.0, 0.0, 0.0, 0.0, 0, 0));
        assertEquals(Math.sqrt(2), UtilMath.getDistance(1.0, 1.0, 2.0, 2.0, 1, 1));
        assertEquals(1.0, UtilMath.getDistance(1.0, 1.0, 2.0, 2.0, 0, 1));
        assertEquals(1.0, UtilMath.getDistance(1.0, 1.0, 2.0, 2.0, 1, 0));
        assertEquals(2.0, UtilMath.getDistance(4.0, 6.0, 6.0, 6.0, 2, 2));
        assertEquals(3.0, UtilMath.getDistance(-2.0, -6.0, -6.0, -6.0, 2, 2));
        assertEquals(Math.sqrt(2), UtilMath.getDistance(3.0, 3.0, 0.0, 0.0, 3, 3));
    }

    /**
     * Test the distance function from area to area.
     */
    @Test
    void testDistanceAreaArea()
    {
        assertEquals(0.0, UtilMath.getDistance(0.0, 0.0, 0, 0, 0.0, 0.0, 0, 0));
        assertEquals(0.0, UtilMath.getDistance(1.0, 1.0, 0, 0, 2.0, 2.0, 0, 0));
        assertEquals(1.0, UtilMath.getDistance(1.0, 1.0, 1, 1, 2.0, 2.0, 0, 1));
        assertEquals(1.0, UtilMath.getDistance(1.0, 1.0, 1, 1, 2.0, 2.0, 1, 0));
        assertEquals(Math.sqrt(2), UtilMath.getDistance(1.0, 1.0, 1, 1, 2.0, 2.0, 1, 1));
        assertEquals(Math.sqrt(2), UtilMath.getDistance(1.0, 1.0, 1, 0, 2.0, 2.0, 1, 1));
        assertEquals(Math.sqrt(2), UtilMath.getDistance(1.0, 1.0, 0, 1, 2.0, 2.0, 1, 1));
        assertEquals(5.0, UtilMath.getDistance(4.0, 6.0, 2, 2, 4.0, 12.0, 2, 2));
        assertEquals(Math.sqrt(2), UtilMath.getDistance(0.0, 0.0, 3, 3, 3.0, 3.0, 3, 3));
        assertEquals(Math.sqrt(2), UtilMath.getDistance(0.0, 0.0, 3, 3, -3.0, -3.0, 3, 3));
    }

    /**
     * Test the wrap function.
     */
    @Test
    void testWrap()
    {
        assertEquals(0, UtilMath.wrap(360, 0, 360));
        assertEquals(359, UtilMath.wrap(-1, 0, 360));
        assertEquals(180, UtilMath.wrap(180, 0, 360));
    }

    /**
     * Test the wrap function.
     */
    @Test
    void testWrapAngle()
    {
        assertEquals(0, UtilMath.wrapAngle(360));
        assertEquals(359, UtilMath.wrapAngle(-1));
        assertEquals(180, UtilMath.wrapAngle(180));
    }

    /**
     * Test the wrap double function.
     */
    @Test
    void testWrapDouble()
    {
        assertEquals(0.0, UtilMath.wrapDouble(360.0, 0.0, 360.0));
        assertEquals(359.0, UtilMath.wrapDouble(-1.0, 0.0, 360.0));
        assertEquals(180.0, UtilMath.wrapDouble(180.0, 0.0, 360.0));
    }

    /**
     * Test the sin & cos functions.
     */
    @Test
    void testSinCos()
    {
        assertEquals(-1.0, UtilMath.cos(180));
        assertEquals(0.0, UtilMath.sin(180));
    }

    /**
     * Test the sign function.
     */
    @Test
    void testSign()
    {
        assertEquals(-1, UtilMath.getSign(-1.0));
        assertEquals(1, UtilMath.getSign(1.0));
        assertEquals(0, UtilMath.getSign(0));
    }

    /**
     * Test the get round value.
     */
    @Test
    void testGetRound()
    {
        assertEquals(1.0, UtilMath.getRound(-1.0, 1.5));
        assertEquals(2.0, UtilMath.getRound(1.0, 1.5));
    }

    /**
     * Test the get closest square multiplier.
     */
    @Test
    void testGetClosestSquareMult()
    {
        assertEquals(pair(0, 0), UtilMath.getClosestSquareMult(0, 0));
        assertEquals(pair(1, 1), UtilMath.getClosestSquareMult(1, 1));
        assertEquals(pair(9, 9), UtilMath.getClosestSquareMult(81, 1));

        assertEquals(pair(2, 49), UtilMath.getClosestSquareMult(97, 95));
        assertEquals(pair(1, 97), UtilMath.getClosestSquareMult(97, 100));

        assertEquals(pair(10, 10), UtilMath.getClosestSquareMult(85, 0));
        assertEquals(pair(9, 10), UtilMath.getClosestSquareMult(85, 1));
        assertEquals(pair(8, 12), UtilMath.getClosestSquareMult(85, 5));
        assertEquals(pair(7, 13), UtilMath.getClosestSquareMult(85, 6));
        assertEquals(pair(6, 15), UtilMath.getClosestSquareMult(85, 9));
        assertEquals(pair(5, 17), UtilMath.getClosestSquareMult(85, 12));
        assertEquals(pair(5, 17), UtilMath.getClosestSquareMult(85, 100));
    }
}
