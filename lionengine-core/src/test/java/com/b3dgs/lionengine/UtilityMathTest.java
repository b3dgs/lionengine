/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.geom.Coord;
import com.b3dgs.lionengine.geom.Geom;
import com.b3dgs.lionengine.geom.Line;

/**
 * Test the utility math class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class UtilityMathTest
{
    /** Precision value. */
    private static final double PRECISION = 0.000001;

    /**
     * Test the core class.
     * 
     * @throws NoSuchMethodException If error.
     * @throws IllegalAccessException If error.
     * @throws InstantiationException If error.
     * @throws InvocationTargetException If success.
     */
    @Test(expected = InvocationTargetException.class)
    public void testUtilityMathClass() throws NoSuchMethodException, InstantiationException, IllegalAccessException,
            InvocationTargetException
    {
        final Constructor<UtilityMath> utilityMath = UtilityMath.class.getDeclaredConstructor();
        utilityMath.setAccessible(true);
        final UtilityMath clazz = utilityMath.newInstance();
        Assert.assertNotNull(clazz);
    }

    /**
     * Test the fix between function.
     */
    @Test
    public void testFixBetween()
    {
        Assert.assertEquals(0, UtilityMath.fixBetween(-10, 0, 10));
        Assert.assertEquals(10, UtilityMath.fixBetween(50, 0, 10));
        Assert.assertEquals(10, UtilityMath.fixBetween(10, 0, 10));
        Assert.assertEquals(0, UtilityMath.fixBetween(0, 0, 10));

        Assert.assertEquals(0.0, UtilityMath.fixBetween(-10.0, 0.0, 10.0), UtilityMathTest.PRECISION);
        Assert.assertEquals(10.0, UtilityMath.fixBetween(50.0, 0.0, 10.0), UtilityMathTest.PRECISION);
        Assert.assertEquals(10.0, UtilityMath.fixBetween(10.0, 0.0, 10.0), UtilityMathTest.PRECISION);
        Assert.assertEquals(0.0, UtilityMath.fixBetween(0.0, 0.0, 10.0), UtilityMathTest.PRECISION);
    }

    /**
     * Test the rounded function.
     */
    @Test
    public void testRounded()
    {
        Assert.assertEquals(100, UtilityMath.getRounded(105, 100));
    }

    /**
     * Test the curve value function.
     */
    @Test
    public void testCurveValue()
    {
        Assert.assertTrue(UtilityMath.curveValue(0.0, 1.0, 0.5) > 0.0);
        Assert.assertTrue(UtilityMath.curveValue(0.0, -1.0, 0.5) < 0.0);
    }

    /**
     * Test the distance function.
     */
    @Test
    public void testDistance()
    {
        Assert.assertEquals(2, UtilityMath.getDistance(4, 6, 6, 6));
        Assert.assertEquals(2.0, UtilityMath.getDistance(4.0, 6.0, 6.0, 6.0), UtilityMathTest.PRECISION);
        Assert.assertEquals(2, UtilityMath.getDistance(4, 6, 2, 2, 6, 6, 2, 2));
    }

    /**
     * Test the wrap double function.
     */
    @Test
    public void testWrapDouble()
    {
        Assert.assertEquals(0.0, UtilityMath.wrapDouble(360.0, 0.0, 360.0), UtilityMathTest.PRECISION);
        Assert.assertEquals(359.0, UtilityMath.wrapDouble(-1.0, 0.0, 360.0), UtilityMathTest.PRECISION);
        Assert.assertEquals(180.0, UtilityMath.wrapDouble(180.0, 0.0, 360.0), UtilityMathTest.PRECISION);
    }

    /**
     * Test the sin & cos functions.
     */
    @Test
    public void testSinCos()
    {
        Assert.assertEquals(-1.0, UtilityMath.cos(180), UtilityMathTest.PRECISION);
        Assert.assertEquals(0.0, UtilityMath.sin(180), UtilityMathTest.PRECISION);
    }

    /**
     * Test the time functions.
     */
    @Test
    public void testTime()
    {
        Assert.assertTrue(UtilityMath.time() > 0);
        Assert.assertTrue(UtilityMath.nano() > 0);
    }

    /**
     * Test the sign function.
     */
    @Test
    public void testSign()
    {
        Assert.assertTrue(UtilityMath.getSign(-1) < 0);
        Assert.assertTrue(UtilityMath.getSign(1) > 0);
        try
        {
            Assert.assertFalse(UtilityMath.getSign(0) == 0);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test the coord intersection function.
     */
    @Test
    public void testCoordIntersection()
    {
        try
        {
            final Coord coord = UtilityMath.intersection(Geom.createLine(), Geom.createLine());
            Assert.assertNotNull(coord);
        }
        catch (final IllegalStateException exception)
        {
            // Success
        }

        final Line line1 = Geom.createLine(1, 2, 3, 4);
        final Line line2 = Geom.createLine(-1, 2, -3, 4);
        final Coord coord = UtilityMath.intersection(line1, line2);
        Assert.assertNotNull(coord);
    }
}
