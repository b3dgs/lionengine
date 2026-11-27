/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.render;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test {@link Viewport}.
 */
final class ViewportTest
{
    /**
     * A viewport keeps its rectangle, which may start outside the frame.
     */
    @Test
    void testValid()
    {
        final Viewport viewport = new Viewport(-8, 16, 160, 90);

        assertEquals(-8, viewport.x());
        assertEquals(16, viewport.y());
        assertEquals(160, viewport.width());
        assertEquals(90, viewport.height());
    }

    /**
     * Sizes that are not positive are rejected.
     *
     * @param width The width.
     * @param height The height.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "0, 1", "1, 0", "-1, 1", "1, -1"
    })
    void testInvalidSize(int width, int height)
    {
        assertThrows(IllegalArgumentException.class, () -> new Viewport(0, 0, width, height));
    }
}
