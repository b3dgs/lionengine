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
package com.b3dgs.lionengine;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Resolution}.
 */
public final class ResolutionTest
{
    /**
     * Test getters.
     */
    @Test
    public void testGetters()
    {
        final Resolution resolution = new Resolution(320, 240, 60);

        assertEquals(320, resolution.getWidth());
        assertEquals(240, resolution.getHeight());
        assertEquals(60, resolution.getRate());
    }

    /**
     * Test non strict positive width.
     */
    @Test
    public void testNonStrictPositiveWidth()
    {
        assertThrows(() -> new Resolution(0, 240, 0), Check.ERROR_ARGUMENT + 0 + Check.ERROR_SUPERIOR_STRICT + 0);
    }

    /**
     * Test non strict positive height.
     */
    @Test
    public void testNonStrictPositiveHeight()
    {
        assertThrows(() -> new Resolution(320, 0, 0), Check.ERROR_ARGUMENT + 0 + Check.ERROR_SUPERIOR_STRICT + 0);
    }

    /**
     * Test negative rate.
     */
    @Test
    public void testNegativeRate()
    {
        assertThrows(() -> new Resolution(320, 240, -1), Check.ERROR_ARGUMENT + -1 + Check.ERROR_SUPERIOR + 0);
    }

    /**
     * Test scale 2x function.
     */
    @Test
    public void testScale2x()
    {
        final Resolution resolution = new Resolution(320, 240, 60);

        assertEquals(new Resolution(640, 480, 60), resolution.get2x());
    }

    /**
     * Test scale 3x function.
     */
    @Test
    public void testScale3x()
    {
        final Resolution resolution = new Resolution(320, 240, 60);

        assertEquals(new Resolution(960, 720, 60), resolution.get3x());
    }

    /**
     * Test scale function with wrong factor X.
     */
    @Test
    public void testScaleWrongFactorX()
    {
        assertThrows(() -> new Resolution(320, 240, 60).getScaled(0, 1),
                     Check.ERROR_ARGUMENT + 0.0 + Check.ERROR_SUPERIOR_STRICT + 0.0);
    }

    /**
     * Test scale function with wrong factor Y.
     */
    @Test
    public void testScaleWrongFactorY()
    {
        assertThrows(() -> new Resolution(320, 240, 60).getScaled(1, 0),
                     Check.ERROR_ARGUMENT + 0.0 + Check.ERROR_SUPERIOR_STRICT + 0.0);
    }

    /**
     * Test equals.
     */
    @Test
    public void testEquals()
    {
        final Resolution resolution = new Resolution(320, 240, 60);

        assertEquals(resolution, resolution);
        assertEquals(new Resolution(320, 240, 60), resolution);

        assertNotEquals(resolution, null);
        assertNotEquals(resolution, new Object());
        assertNotEquals(resolution, new Resolution(100, 240, 60));
        assertNotEquals(resolution, new Resolution(320, 100, 60));
        assertNotEquals(resolution, new Resolution(320, 240, 30));
    }

    /**
     * Test hash code.
     */
    @Test
    public void testHashCode()
    {
        final Resolution resolution = new Resolution(320, 240, 60);

        assertHashEquals(new Resolution(320, 240, 60), resolution);

        assertHashNotEquals(resolution, new Object());
        assertHashNotEquals(resolution, new Resolution(100, 240, 60));
        assertHashNotEquals(resolution, new Resolution(320, 100, 60));
        assertHashNotEquals(resolution, new Resolution(320, 240, 30));
    }

    /**
     * Test to string.
     */
    @Test
    public void testToString()
    {
        assertEquals("Resolution [width=320, height=240, rate=60]", new Resolution(320, 240, 60).toString());
    }
}
