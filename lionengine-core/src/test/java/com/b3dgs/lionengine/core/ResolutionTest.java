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
package com.b3dgs.lionengine.core;

import org.junit.Assert;
import org.junit.Test;

import com.b3dgs.lionengine.LionEngineException;

/**
 * Test the resolution class.
 */
public class ResolutionTest
{
    /**
     * Test the display creation function.
     * 
     * @param width The width.
     * @param height The height.
     * @param rate The rate.
     */
    private static void testResolutionCreation(int width, int height, int rate)
    {
        try
        {
            final Resolution resolution = new Resolution(width, height, rate);
            Assert.assertNotNull(resolution);
            Assert.fail();
        }
        catch (final LionEngineException exception)
        {
            // Success
            Assert.assertNotNull(exception);
        }
    }

    /**
     * Test the negative rate.
     */
    @Test(expected = LionEngineException.class)
    public void testNegativeRate()
    {
        Assert.assertNull(new Resolution(320, 240, -1));
    }

    /**
     * Test the resolution failure.
     */
    @Test
    public void testFailures()
    {
        testResolutionCreation(0, 0, -1);
        testResolutionCreation(0, 1, -1);
        testResolutionCreation(0, 0, -1);
        testResolutionCreation(0, 0, 1);
        testResolutionCreation(0, 1, 1);
        testResolutionCreation(0, 0, 1);
        testResolutionCreation(1, 0, -1);
        testResolutionCreation(1, 1, -1);
        testResolutionCreation(1, 1, -1);
    }
}
