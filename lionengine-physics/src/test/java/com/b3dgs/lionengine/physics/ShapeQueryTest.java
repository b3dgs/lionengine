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

package com.b3dgs.lionengine.physics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Tests of the {@link ShapeQuery} contract on its reference {@link BoxListQuery}: the found shapes, the count beyond
 * the buffer and the untouched remainder (REQ-PHYS-001, ADR-0082).
 */
final class ShapeQueryTest
{
    /**
     * The overlapping shapes are written in order, touching ones are not found; the count is returned even when the
     * buffer is too short, and the boxes after the written count are left unchanged.
     */
    @Test
    void testQuery()
    {
        final ShapeQuery query = new BoxListQuery(new Box(0.5, 0.5, 1.0, 1.0),
                                                  new Box(1.5, 0.5, 1.0, 1.0),
                                                  new Box(2.5, 0.5, 1.0, 1.0),
                                                  new Box(1.5, 1.5, 1.0, 1.0));
        final Box area = new Box(1.5, 0.5, 1.5, 0.5);
        final Box[] results =
        {
            new Box(), new Box(), new Box(9.0, 9.0, 1.0, 1.0)
        };

        assertEquals(3, query.query(area, results));
        assertEquals(0.5, results[0].getX());
        assertEquals(1.5, results[1].getX());
        assertEquals(2.5, results[2].getX());

        final Box[] shortBuffer =
        {
            new Box()
        };
        assertEquals(3, query.query(area, shortBuffer));
        assertEquals(0.5, shortBuffer[0].getX());

        final Box[] untouched =
        {
            new Box(), new Box(9.0, 9.0, 1.0, 1.0)
        };
        assertEquals(1, query.query(new Box(1.5, 1.5, 0.5, 0.5), untouched));
        assertEquals(1.5, untouched[0].getY());
        assertEquals(9.0, untouched[1].getX());
        assertEquals(0, query.query(new Box(0.5, -0.5, 1.0, 1.0), untouched));
    }

    /**
     * A {@code null} argument is refused.
     */
    @Test
    void testNull()
    {
        final ShapeQuery query = new BoxListQuery();
        final Box[] results = new Box[0];
        final Box area = new Box();

        assertThrows(IllegalArgumentException.class, () -> query.query(null, results));
        assertThrows(IllegalArgumentException.class, () -> query.query(area, null));
    }
}
