/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
public final class UtilMathTest
{
    /**
     * Test the constructor.
     */
    @Test
    public void testConstructorPrivate()
    {
        assertPrivateConstructor(UtilMath.class);
    }

    /**
     * Test the is between function.
     */
    @Test
    public void testIsBetween()
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
    public void testClamp()
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
    public void testRounded()
    {
        assertEquals(100, UtilMath.getRounded(105, 100));
    }

    /**
     * Test the rounded ceil function.
     */
    @Test
    public void testRoundedC()
    {
        assertEquals(200, UtilMath.getRoundedC(105, 100));
    }

    /**
     * Test the curve value function.
     */
    @Test
    public void testCurveValue()
    {
        assertTrue(UtilMath.curveValue(0.0, 1.0, 0.5) > 0.0);
        assertTrue(UtilMath.curveValue(0.0, -1.0, 0.5) < 0.0);
    }

    /**
     * Test the distance function between two localizable.
     */
    @Test
    public void testDistanceLocalizable()
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
    public void testDistancePointPoint()
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
    public void testDistancePointArea()
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
    public void testDistanceAreaArea()
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
     * Test the wrap double function.
     */
    @Test
    public void testWrapDouble()
    {
        assertEquals(0.0, UtilMath.wrapDouble(360.0, 0.0, 360.0));
        assertEquals(359.0, UtilMath.wrapDouble(-1.0, 0.0, 360.0));
        assertEquals(180.0, UtilMath.wrapDouble(180.0, 0.0, 360.0));
    }

    /**
     * Test the sin & cos functions.
     */
    @Test
    public void testSinCos()
    {
        assertEquals(-1.0, UtilMath.cos(180));
        assertEquals(0.0, UtilMath.sin(180));
    }

    /**
     * Test the sign function.
     */
    @Test
    public void testSign()
    {
        assertEquals(-1, UtilMath.getSign(-1.0));
        assertEquals(1, UtilMath.getSign(1.0));
        assertEquals(0, UtilMath.getSign(0));
    }

    /**
     * Test the get round value.
     */
    @Test
    public void testGetRound()
    {
        assertEquals(1.0, UtilMath.getRound(-1.0, 1.5));
        assertEquals(2.0, UtilMath.getRound(1.0, 1.5));
    }
}
