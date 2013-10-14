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
package com.b3dgs.lionengine;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test the resolution class.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
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
        catch (final IllegalArgumentException
                     | LionEngineException exception)
        {
            // Success
        }
    }

    /**
     * Test the resolution failure.
     */
    @Test
    public void testResolutionFailures()
    {
        ResolutionTest.testResolutionCreation(0, 0, -1);
        ResolutionTest.testResolutionCreation(0, 1, -1);
        ResolutionTest.testResolutionCreation(0, 0, -1);
        ResolutionTest.testResolutionCreation(0, 0, 1);
        ResolutionTest.testResolutionCreation(0, 1, 1);
        ResolutionTest.testResolutionCreation(0, 0, 1);
        ResolutionTest.testResolutionCreation(1, 0, -1);
        ResolutionTest.testResolutionCreation(1, 1, -1);
        ResolutionTest.testResolutionCreation(1, 1, -1);
    }

    /**
     * Test the resolution setters.
     */
    @Test
    public void testResolutionSetter()
    {
        final Resolution resolution = new Resolution(320, 240, 60);
        Assert.assertEquals(resolution.getRatio(), Ratio.R4_3, 0.000001);

        resolution.set(640, 360);
        Assert.assertEquals(resolution.getRatio(), Ratio.R16_9, 0.000001);

        resolution.set(640, 400, 50);
        Assert.assertEquals(resolution.getRate(), 50);
        Assert.assertEquals(resolution.getRatio(), Ratio.R16_10, 0.000001);

        resolution.setRatio(Ratio.R5_4);
        resolution.setRate(70);
        Assert.assertEquals(resolution.getWidth(), 500);
        Assert.assertEquals(resolution.getHeight(), 400);
        Assert.assertEquals(resolution.getRatio(), Ratio.R5_4, 0.000001);
        Assert.assertEquals(resolution.getRate(), 70);
        
        resolution.setRatio(Ratio.R5_4);
    }
}
