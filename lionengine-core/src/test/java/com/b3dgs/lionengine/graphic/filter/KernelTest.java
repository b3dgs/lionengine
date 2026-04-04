/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.graphic.filter;

import static com.b3dgs.lionengine.UtilAssert.assertArrayEquals;
import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashEquals;
import static com.b3dgs.lionengine.UtilAssert.assertHashNotEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNotEquals;

import org.junit.jupiter.api.Test;

/**
 * Test {@link Kernel}.
 */
final class KernelTest
{
    /**
     * Test getters.
     */
    @Test
    void testGetters()
    {
        final Kernel kernel = new Kernel(320, new double[]
        {
            1.0, 2.0
        });

        assertEquals(320, kernel.getWidth());
        assertArrayEquals(new double[]
        {
            1.0, 2.0
        }, kernel.getMatrix());
    }

    /**
     * Test hash code.
     */
    @Test
    void testHashCode()
    {
        final Kernel kernel = new Kernel(320, new double[]
        {
            1.0, 2.0
        });

        assertHashEquals(kernel, kernel);
        assertHashEquals(kernel, new Kernel(320, new double[]
        {
            1.0, 2.0
        }));

        assertHashNotEquals(kernel, new Kernel(240, new double[]
        {
            1.0, 2.0
        }));
        assertHashNotEquals(kernel, new Kernel(320, new double[]
        {
            2.0, 2.0
        }));
    }

    /**
     * Test equals.
     */
    @Test
    void testEquals()
    {
        final Kernel kernel = new Kernel(320, new double[]
        {
            1.0, 2.0
        });

        assertEquals(kernel, kernel);
        assertEquals(kernel, new Kernel(320, new double[]
        {
            1.0, 2.0
        }));

        assertNotEquals(kernel, null);
        assertNotEquals(kernel, new Object());
        assertNotEquals(kernel, new Kernel(240, new double[]
        {
            1.0, 2.0
        }));
        assertNotEquals(kernel, new Kernel(320, new double[]
        {
            2.0, 2.0
        }));
    }
}
