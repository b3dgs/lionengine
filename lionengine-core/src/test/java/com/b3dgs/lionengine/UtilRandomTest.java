/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import static com.b3dgs.lionengine.UtilAssert.assertNotNull;
import static com.b3dgs.lionengine.UtilAssert.assertPrivateConstructor;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;
import static com.b3dgs.lionengine.UtilAssert.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test {@link UtilRandom}.
 */
public final class UtilRandomTest
{
    /**
     * Test the constructor.
     */
    @Test
    public void testConstructorPrivate()
    {
        assertPrivateConstructor(UtilRandom.class);
    }

    /**
     * Test random.
     */
    @Test
    public void testRandom()
    {
        UtilRandom.setSeed(4_894_516L);

        assertNotNull(Boolean.valueOf(UtilRandom.getRandomBoolean()));
        assertNotNull(Integer.valueOf(UtilRandom.getRandomInteger()));
        assertNotNull(Double.valueOf(UtilRandom.getRandomDouble()));
        assertTrue(UtilRandom.getRandomInteger(100) <= 100);
        assertTrue(UtilRandom.getRandomInteger(-100, 100) <= 100);
        assertTrue(UtilRandom.getRandomInteger(Range.INT_POSITIVE_STRICT) >= 0);
    }

    /**
     * Test get random integer with <code>null</code> argument.
     */
    @Test
    public void testGetRandomIntegerNullRange()
    {
        assertThrows(() -> UtilRandom.getRandomInteger(null), Check.ERROR_NULL);
    }
}
