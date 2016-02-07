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
        }
    }

    /**
     * Test the invalid ratio.
     */
    @Test(expected = LionEngineException.class)
    public void testInvalidRatio()
    {
        final Resolution resolution = new Resolution(320, 240, 60);
        resolution.setRatio(0);
        Assert.fail();
    }

    /**
     * Test the negative rate.
     */
    @Test(expected = LionEngineException.class)
    public void testNegativeRate()
    {
        final Resolution resolution = new Resolution(320, 240, 60);
        resolution.setRate(-1);
        Assert.fail();
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

    /**
     * Test the resolution setters.
     */
    @Test
    public void testSetters()
    {
        final Resolution resolution = new Resolution(320, 240, 60);
        Assert.assertEquals(Ratio.R4_3, resolution.getRatio(), 0.000001);

        resolution.setSize(640, 360);
        Assert.assertEquals(Ratio.R16_9, resolution.getRatio(), 0.000001);

        resolution.setSize(640, 400);
        resolution.setRate(50);
        Assert.assertEquals(50, resolution.getRate());
        Assert.assertEquals(Ratio.R16_10, resolution.getRatio(), 0.000001);

        resolution.setRatio(Ratio.R5_4);
        resolution.setRate(70);
        Assert.assertEquals(500, resolution.getWidth());
        Assert.assertEquals(400, resolution.getHeight());
        Assert.assertEquals(Ratio.R5_4, resolution.getRatio(), 0.000001);
        Assert.assertEquals(70, resolution.getRate());

        resolution.setRatio(Ratio.R5_4);
    }
}
