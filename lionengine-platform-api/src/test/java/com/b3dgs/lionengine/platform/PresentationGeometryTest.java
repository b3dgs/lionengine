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
package com.b3dgs.lionengine.platform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test {@link PresentationGeometry} (ADR-0046, REQ-IN-004).
 */
final class PresentationGeometryTest
{
    /** Tolerance of floating point comparisons. */
    private static final double EPSILON = 1.0E-9;

    /**
     * A 320 x 180 frame presented with {@code FIT} at scale 4 in a 1400 x 720 surface: centered horizontally.
     */
    @Test
    void testFitConversion()
    {
        final PresentationGeometry geometry = new PresentationGeometry(60, 0, 1280, 720, 4.0, 4.0);

        assertEquals(0.0, geometry.toLogicalX(60.0), EPSILON);
        assertEquals(319.75, geometry.toLogicalX(1339.0), EPSILON);
        assertEquals(45.0, geometry.toLogicalY(180.0), EPSILON);
        assertTrue(geometry.contains(60.0, 0.0));
        assertTrue(geometry.contains(1339.9, 719.9));
        assertFalse(geometry.contains(59.9, 10.0));
        assertFalse(geometry.contains(1340.0, 10.0));
        assertFalse(geometry.contains(100.0, 720.0));
        assertEquals(-15.0, geometry.toLogicalX(0.0), EPSILON);
    }

    /**
     * {@code STRETCH} uses different scales per axis.
     */
    @Test
    void testStretchConversion()
    {
        final PresentationGeometry geometry = new PresentationGeometry(0, 0, 800, 600, 2.5, 10.0 / 3.0);

        assertEquals(160.0, geometry.toLogicalX(400.0), EPSILON);
        assertEquals(90.0, geometry.toLogicalY(300.0), EPSILON);
    }

    /**
     * Scales that are not positive and finite are rejected.
     *
     * @param scale The scale.
     */
    @ParameterizedTest
    @ValueSource(doubles =
    {
        0.0, -1.0, Double.NaN, Double.POSITIVE_INFINITY
    })
    void testInvalidScale(double scale)
    {
        assertThrows(IllegalArgumentException.class, () -> new PresentationGeometry(0, 0, 10, 10, scale, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new PresentationGeometry(0, 0, 10, 10, 1.0, scale));
    }

    /**
     * Negative sizes are rejected; an empty geometry (minimized surface) is allowed.
     */
    @Test
    void testSizes()
    {
        assertEquals(0, new PresentationGeometry(0, 0, 0, 0, 1.0, 1.0).width());
        assertThrows(IllegalArgumentException.class, () -> new PresentationGeometry(0, 0, -1, 10, 1.0, 1.0));
        assertThrows(IllegalArgumentException.class, () -> new PresentationGeometry(0, 0, 10, -1, 1.0, 1.0));
    }
}
