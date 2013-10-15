/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Test the utility math class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class UtilityMathTest
{
    /**
     * Test the utility math class.
     * 
     * @throws NoSuchMethodException If error.
     * @throws SecurityException If error.
     * @throws InstantiationException If error.
     * @throws IllegalAccessException If error.
     * @throws IllegalArgumentException If error.
     */
    @Test
    public void testUtilityMathClass() throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException
    {
        final Constructor<UtilityMath> utilityMath = UtilityMath.class.getDeclaredConstructor();
        utilityMath.setAccessible(true);
        try
        {
            final UtilityMath clazz = utilityMath.newInstance();
            Assert.assertNotNull(clazz);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }
    }

    /**
     * Test utility math.
     */
    @Test
    public void testUtilityMath()
    {
        final double precision = 0.000001;
        Assert.assertEquals(0, UtilityMath.fixBetween(-10, 0, 10));
        Assert.assertEquals(10, UtilityMath.fixBetween(50, 0, 10));
        Assert.assertEquals(10, UtilityMath.fixBetween(10, 0, 10));
        Assert.assertEquals(0, UtilityMath.fixBetween(0, 0, 10));

        Assert.assertEquals(0.0, UtilityMath.fixBetween(-10.0, 0.0, 10.0), precision);
        Assert.assertEquals(10.0, UtilityMath.fixBetween(50.0, 0.0, 10.0), precision);
        Assert.assertEquals(10.0, UtilityMath.fixBetween(10.0, 0.0, 10.0), precision);
        Assert.assertEquals(0.0, UtilityMath.fixBetween(0.0, 0.0, 10.0), precision);

        Assert.assertEquals(100, UtilityMath.getRounded(105, 100));

        Assert.assertTrue(UtilityMath.curveValue(0.0, 1.0, 0.5) > 0.0);
        Assert.assertTrue(UtilityMath.curveValue(0.0, -1.0, 0.5) < 0.0);

        Assert.assertEquals(2, UtilityMath.getDistance(4, 6, 6, 6));
        Assert.assertEquals(2.0, UtilityMath.getDistance(4.0, 6.0, 6.0, 6.0), precision);
        Assert.assertEquals(2, UtilityMath.getDistance(4, 6, 2, 2, 6, 6, 2, 2));

        Assert.assertEquals(0.0, UtilityMath.wrapDouble(360.0, 0.0, 360.0), precision);
        Assert.assertEquals(359.0, UtilityMath.wrapDouble(-1.0, 0.0, 360.0), precision);
        Assert.assertEquals(180.0, UtilityMath.wrapDouble(180.0, 0.0, 360.0), precision);

        Assert.assertEquals(-1.0, UtilityMath.cos(180), precision);
        Assert.assertEquals(0.0, UtilityMath.sin(180), precision);

        Assert.assertTrue(UtilityMath.time() > 0);
        Assert.assertTrue(UtilityMath.nano() > 0);

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
}
