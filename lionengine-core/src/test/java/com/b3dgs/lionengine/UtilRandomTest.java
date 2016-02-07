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
package com.b3dgs.lionengine;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.util.UtilTests;

/**
 * Test utility random class.
 */
public class UtilRandomTest
{
    /**
     * Test the constructor.
     * 
     * @throws Exception If error.
     */
    @Test(expected = LionEngineException.class)
    public void testConstructor() throws Exception
    {
        UtilTests.testPrivateConstructor(UtilRandom.class);
    }

    /**
     * Test utility random.
     */
    @Test
    public void testRandom()
    {
        UtilRandom.setSeed(4894516L);

        Assert.assertNotNull(Boolean.valueOf(UtilRandom.getRandomBoolean()));
        Assert.assertNotNull(Integer.valueOf(UtilRandom.getRandomInteger()));
        Assert.assertNotNull(Double.valueOf(UtilRandom.getRandomDouble()));
        Assert.assertTrue(UtilRandom.getRandomInteger(100) <= 100);
        Assert.assertTrue(UtilRandom.getRandomInteger(-100, 100) <= 100);
        Assert.assertTrue(UtilRandom.getRandomInteger(Range.INT_POSITIVE_STRICT) >= 0);
    }
}
