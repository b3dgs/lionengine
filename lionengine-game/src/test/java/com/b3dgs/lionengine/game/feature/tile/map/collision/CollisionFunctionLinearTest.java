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
package com.b3dgs.lionengine.game.feature.tile.map.collision;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.UtilTests;

/**
 * Test the collision function linear class.
 */
public class CollisionFunctionLinearTest
{
    /** Function test. */
    private final CollisionFunctionLinear function = new CollisionFunctionLinear(2.0, 3.0);

    /**
     * Test the function.
     */
    @Test
    public void testFunction()
    {
        Assert.assertEquals(13.0, function.compute(5.0), UtilTests.PRECISION);
        Assert.assertEquals(-7.0, function.compute(-5.0), UtilTests.PRECISION);
        Assert.assertEquals(CollisionFunctionType.LINEAR, function.getType());
        Assert.assertEquals(2.0, function.getA(), UtilTests.PRECISION);
        Assert.assertEquals(3.0, function.getB(), UtilTests.PRECISION);
    }

    /**
     * Test the function hash code.
     */
    @Test
    public void testHashcode()
    {
        final int hash = function.hashCode();
        Assert.assertEquals(hash, new CollisionFunctionLinear(2.0, 3.0).hashCode());

        Assert.assertNotEquals(hash, new Object().hashCode());
        Assert.assertNotEquals(hash, new CollisionFunctionLinear(1.5, 3.0).hashCode());
        Assert.assertNotEquals(hash, new CollisionFunctionLinear(2.0, 3.5).hashCode());
    }

    /**
     * Test the function equality.
     */
    @Test
    public void testEquals()
    {
        Assert.assertEquals(function, function);
        Assert.assertEquals(function, new CollisionFunctionLinear(2.0, 3.0));

        Assert.assertNotEquals(function, null);
        Assert.assertNotEquals(function, new Object());
        Assert.assertNotEquals(function, new CollisionFunctionLinear(1.5, 3.0));
        Assert.assertNotEquals(function, new CollisionFunctionLinear(2.0, 3.5));
    }

    /**
     * Test the function to string.
     */
    @Test
    public void testToString()
    {
        Assert.assertEquals("CollisionFunctionLinear (a=2.0, b=3.0)", function.toString());
    }
}
