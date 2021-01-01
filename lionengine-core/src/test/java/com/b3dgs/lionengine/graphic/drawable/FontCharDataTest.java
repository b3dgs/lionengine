/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.drawable;

import static com.b3dgs.lionengine.UtilAssert.assertEquals;
import static com.b3dgs.lionengine.UtilAssert.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test {@link FontCharData}.
 */
final class FontCharDataTest
{
    /**
     * Test constructor.
     */
    @Test
    void testConstructor()
    {
        final FontCharData data = new FontCharData(0, 1, 2);

        assertEquals(0, data.getId());
        assertEquals(1, data.getWidth());
        assertEquals(2, data.getHeight());
    }

    /**
     * Test constructor with invalid id.
     */
    @Test
    void testConstructorInvalidId()
    {
        assertThrows(() -> new FontCharData(-1, 1, 2), "Invalid argument: -1 is not superior or equal to 0");
    }

    /**
     * Test constructor with invalid with.
     */
    @Test
    void testConstructorInvalidWidth()
    {
        assertThrows(() -> new FontCharData(0, -1, 2), "Invalid argument: -1 is not superior or equal to 0");
    }

    /**
     * Test constructor with invalid height.
     */
    @Test
    void testConstructorInvalidheight()
    {
        assertThrows(() -> new FontCharData(0, 1, -1), "Invalid argument: -1 is not superior or equal to 0");
    }
}
