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

/**
 * Test the ratio.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class RatioTest
{
    /**
     * Test the ratio constructor.
     * 
     * @throws NoSuchMethodException If error.
     * @throws SecurityException If error.
     * @throws InstantiationException If error.
     * @throws IllegalAccessException If error.
     * @throws IllegalArgumentException If error.
     */
    @Test
    public void testRatioClass() throws NoSuchMethodException, SecurityException, InstantiationException,
            IllegalAccessException, IllegalArgumentException
    {
        final Constructor<Ratio> constructor = Ratio.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        try
        {
            final Ratio ratio = constructor.newInstance();
            Assert.assertNotNull(ratio);
            Assert.fail();
        }
        catch (final InvocationTargetException exception)
        {
            // Success
        }

        Assert.assertFalse(Ratio.equals(Ratio.R16_10, Ratio.R16_9));
        Assert.assertTrue(Ratio.equals(Ratio.R4_3, Ratio.R4_3));
        Assert.assertFalse(Ratio.equals(Ratio.R16_9, Ratio.R4_3));
    }
}
