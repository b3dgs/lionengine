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
package com.b3dgs.lionengine.util;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.test.UtilTests;

/**
 * Test the utility math class.
 */
public class UtilMathTest
{
    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(UtilMath.class);
    }

    /**
     * Test the is between function.
     */
    @Test
    public void testIsBetween()
    {
        Assert.assertTrue(UtilMath.isBetween(0, 0, 2));
        Assert.assertTrue(UtilMath.isBetween(1, 0, 2));
        Assert.assertTrue(UtilMath.isBetween(2, 0, 2));

        Assert.assertFalse(UtilMath.isBetween(3, 0, 2));
        Assert.assertFalse(UtilMath.isBetween(-1, 0, 2));

        Assert.assertFalse(UtilMath.isBetween(1, 2, 1));

        Assert.assertTrue(UtilMath.isBetween(0.0, 0.0, 0.2));
        Assert.assertTrue(UtilMath.isBetween(0.1, 0.0, 0.2));
        Assert.assertTrue(UtilMath.isBetween(0.2, 0.0, 0.2));

        Assert.assertFalse(UtilMath.isBetween(0.3, 0.0, 0.2));
        Assert.assertFalse(UtilMath.isBetween(-0.1, 0.0, 0.2));

        Assert.assertFalse(UtilMath.isBetween(0.1, 0.2, 0.1));
    }

    /**
     * Test the clamp function.
     */
    @Test
    public void testClamp()
    {
        Assert.assertEquals(0, UtilMath.clamp(-10, 0, 10));
        Assert.assertEquals(10, UtilMath.clamp(50, 0, 10));
        Assert.assertEquals(10, UtilMath.clamp(10, 0, 10));
        Assert.assertEquals(0, UtilMath.clamp(0, 0, 10));

        Assert.assertEquals(0.0, UtilMath.clamp(-10.0, 0.0, 10.0), UtilTests.PRECISION);
        Assert.assertEquals(10.0, UtilMath.clamp(50.0, 0.0, 10.0), UtilTests.PRECISION);
        Assert.assertEquals(10.0, UtilMath.clamp(10.0, 0.0, 10.0), UtilTests.PRECISION);
        Assert.assertEquals(0.0, UtilMath.clamp(0.0, 0.0, 10.0), UtilTests.PRECISION);
    }

    /**
     * Test the rounded function.
     */
    @Test
    public void testRounded()
    {
        Assert.assertEquals(100, UtilMath.getRounded(105, 100));
    }

    /**
     * Test the curve value function.
     */
    @Test
    public void testCurveValue()
    {
        Assert.assertTrue(UtilMath.curveValue(0.0, 1.0, 0.5) > 0.0);
        Assert.assertTrue(UtilMath.curveValue(0.0, -1.0, 0.5) < 0.0);
    }

    /**
     * Test the distance function between two points.
     */
    @Test
    public void testDistancePointPoint()
    {
        Assert.assertEquals(0.0, UtilMath.getDistance(0.0, 0.0, 0.0, 0.0), UtilTests.PRECISION);
        Assert.assertEquals(1.0, UtilMath.getDistance(0.0, 0.0, 1.0, 0.0), UtilTests.PRECISION);
        Assert.assertEquals(1.0, UtilMath.getDistance(0.0, 0.0, 0.0, 1.0), UtilTests.PRECISION);
        Assert.assertEquals(1.0, UtilMath.getDistance(1.0, 0.0, 0.0, 0.0), UtilTests.PRECISION);
        Assert.assertEquals(1.0, UtilMath.getDistance(0.0, 1.0, 0.0, 0.0), UtilTests.PRECISION);
        Assert.assertEquals(Math.sqrt(2), UtilMath.getDistance(1.0, 1.0, 2.0, 2.0), UtilTests.PRECISION);
        Assert.assertEquals(2.0, UtilMath.getDistance(4.0, 6.0, 6.0, 6.0), UtilTests.PRECISION);
        Assert.assertEquals(2.0, UtilMath.getDistance(-4.0, -6.0, -6.0, -6.0), UtilTests.PRECISION);
    }

    /**
     * Test the distance function from point to area.
     */
    @Test
    public void testDistancePointArea()
    {
        Assert.assertEquals(0.0, UtilMath.getDistance(0.0, 0.0, 0.0, 0.0, 0, 0), UtilTests.PRECISION);
        Assert.assertEquals(Math.sqrt(2), UtilMath.getDistance(1.0, 1.0, 2.0, 2.0, 1, 1), UtilTests.PRECISION);
        Assert.assertEquals(2.0, UtilMath.getDistance(4.0, 6.0, 6.0, 6.0, 2, 2), UtilTests.PRECISION);
        Assert.assertEquals(2.0, UtilMath.getDistance(-2.0, -6.0, -6.0, -6.0, 2, 2), UtilTests.PRECISION);
        Assert.assertEquals(0.0, UtilMath.getDistance(3.0, 3.0, 0.0, 0.0, 3, 3), UtilTests.PRECISION);
    }

    /**
     * Test the distance function from area to area.
     */
    @Test
    public void testDistanceAreaArea()
    {
        Assert.assertEquals(0.0, UtilMath.getDistance(0.0, 0.0, 0, 0, 0.0, 0.0, 0, 0), UtilTests.PRECISION);
        Assert.assertEquals(Math.sqrt(2), UtilMath.getDistance(1.0, 1.0, 0, 0, 2.0, 2.0, 0, 0), UtilTests.PRECISION);
        Assert.assertEquals(0.0, UtilMath.getDistance(1.0, 1.0, 1, 1, 2.0, 2.0, 1, 1), UtilTests.PRECISION);
        Assert.assertEquals(4.0, UtilMath.getDistance(4.0, 6.0, 2, 2, 4.0, 12.0, 2, 2), UtilTests.PRECISION);
        Assert.assertEquals(0.0, UtilMath.getDistance(0.0, 0.0, 3, 3, 3.0, 3.0, 3, 3), UtilTests.PRECISION);
        Assert.assertEquals(0.0, UtilMath.getDistance(0.0, 0.0, 3, 3, -3.0, -3.0, 3, 3), UtilTests.PRECISION);
    }

    /**
     * Test the wrap double function.
     */
    @Test
    public void testWrapDouble()
    {
        Assert.assertEquals(0.0, UtilMath.wrapDouble(360.0, 0.0, 360.0), UtilTests.PRECISION);
        Assert.assertEquals(359.0, UtilMath.wrapDouble(-1.0, 0.0, 360.0), UtilTests.PRECISION);
        Assert.assertEquals(180.0, UtilMath.wrapDouble(180.0, 0.0, 360.0), UtilTests.PRECISION);
    }

    /**
     * Test the sin & cos functions.
     */
    @Test
    public void testSinCos()
    {
        Assert.assertEquals(-1.0, UtilMath.cos(180), UtilTests.PRECISION);
        Assert.assertEquals(0.0, UtilMath.sin(180), UtilTests.PRECISION);
    }

    /**
     * Test the time functions.
     */
    @Test
    public void testTime()
    {
        Assert.assertTrue(UtilMath.time() > 0);
        Assert.assertTrue(UtilMath.nano() > 0);
    }

    /**
     * Test the sign function.
     */
    @Test
    public void testSign()
    {
        Assert.assertEquals(UtilMath.getSign(-1.0), -1);
        Assert.assertEquals(UtilMath.getSign(1.0), 1);
        Assert.assertEquals(UtilMath.getSign(0), 0);
    }

    /**
     * Test the get round value.
     */
    @Test
    public void testGetRound()
    {
        Assert.assertEquals(1.0, UtilMath.getRound(-1.0, 1.5), UtilTests.PRECISION);
        Assert.assertEquals(2.0, UtilMath.getRound(1.0, 1.5), UtilTests.PRECISION);
    }
}
