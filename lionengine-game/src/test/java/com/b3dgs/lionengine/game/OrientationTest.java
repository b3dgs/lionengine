/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertNull;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.UtilTests;

/**
 * Test {@link Orientation}.
 */
public final class OrientationTest
{
    /**
     * Test the enum.
     * 
     * @throws Exception If error.
     */
    @Test
    public void testEnum() throws Exception
    {
        UtilTests.testEnum(Orientation.class);
    }

    /**
     * Test the get.
     */
    @Test
    public void testGet()
    {
        assertNull(Orientation.get(0, 0));
        assertEquals(Orientation.NORTH, Orientation.get(0, 1));
        assertEquals(Orientation.SOUTH, Orientation.get(0, -1));
        assertEquals(Orientation.EAST, Orientation.get(1, 0));
        assertEquals(Orientation.WEST, Orientation.get(-1, 0));
        assertEquals(Orientation.NORTH_EAST, Orientation.get(1, 1));
        assertEquals(Orientation.SOUTH_EAST, Orientation.get(1, -1));
        assertEquals(Orientation.NORTH_WEST, Orientation.get(-1, 1));
        assertEquals(Orientation.SOUTH_WEST, Orientation.get(-1, -1));
    }

    /**
     * Test the next.
     */
    @Test
    public void testNext()
    {
        assertEquals(Orientation.NORTH_EAST, Orientation.next(Orientation.NORTH, 1));
        assertEquals(Orientation.NORTH, Orientation.next(Orientation.NORTH, 8));
    }
}
