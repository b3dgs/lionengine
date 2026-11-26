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
package com.b3dgs.lionengine.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test {@link ColorMatrix}: the 4 by 5 grading matrix of Android {@code ColorMatrix}, its rounding and clamping, the
 * saturation matrices and the value rules (REQ-SPRITE-004, ADR-0056).
 */
final class ColorMatrixTest
{
    /**
     * Returns the values of a matrix that only offsets the channels.
     *
     * @param red The red offset.
     * @param green The green offset.
     * @param blue The blue offset.
     * @param alpha The alpha offset.
     * @return The 20 values.
     */
    private static double[] offsets(double red, double green, double blue, double alpha)
    {
        return new double[]
        {
            1.0, 0.0, 0.0, 0.0, red,
            0.0, 1.0, 0.0, 0.0, green,
            0.0, 0.0, 1.0, 0.0, blue,
            0.0, 0.0, 0.0, 1.0, alpha
        };
    }

    /**
     * The identity changes no pixel and knows it.
     */
    @Test
    void testIdentity()
    {
        assertTrue(ColorMatrix.IDENTITY.isIdentity());
        assertEquals(0x80C08040, ColorMatrix.IDENTITY.apply(0x80C08040));
        assertEquals(0x00000000, ColorMatrix.IDENTITY.apply(0x00000000));
        assertEquals(0xFFFFFFFF, ColorMatrix.IDENTITY.apply(0xFFFFFFFF));
        assertEquals(1.0, ColorMatrix.IDENTITY.get(3, 3));
        assertEquals(0.0, ColorMatrix.IDENTITY.get(0, 4));
    }

    /**
     * Each row mixes the four channels and adds its offset in channel units: here the channels are swapped and the
     * alpha halved.
     */
    @Test
    void testRows()
    {
        final ColorMatrix swap = new ColorMatrix(0.0, 0.0, 1.0, 0.0, 0.0,
                                                 0.0, 1.0, 0.0, 0.0, 10.0,
                                                 1.0, 0.0, 0.0, 0.0, 0.0,
                                                 0.0, 0.0, 0.0, 0.5, 0.0);

        assertFalse(swap.isIdentity());
        assertEquals(0x40408AC0, swap.apply(0x80C08040));
        assertEquals(10.0, swap.get(1, 4));
    }

    /**
     * Results are rounded half up, then clamped to {@code [0, 255]}.
     */
    @Test
    void testRoundingAndClamping()
    {
        assertEquals(0xFF000000, new ColorMatrix(offsets(-300.0, -0.6, -0.4, 0.0)).apply(0xFF000100));
        assertEquals(0xFFFF0201, new ColorMatrix(offsets(300.0, 0.5, 0.49, 0.0)).apply(0xFF000101));
        assertEquals(0x00000000, new ColorMatrix(offsets(0.0, 0.0, 0.0, -256.0)).apply(0xFF000000));
    }

    /**
     * Saturation {@code 0} gives gray with the luminance weights of Android ({@code 0.213}, {@code 0.715},
     * {@code 0.072}); saturation {@code 1} is the identity; alpha never changes.
     */
    @Test
    void testSaturation()
    {
        final ColorMatrix gray = ColorMatrix.saturation(0.0);

        assertEquals(0x80363636, gray.apply(0x80FF0000));
        assertEquals(0x80B6B6B6, gray.apply(0x8000FF00));
        assertEquals(0x80121212, gray.apply(0x800000FF));
        assertEquals(0xFFFFFFFF, gray.apply(0xFFFFFFFF));
        assertEquals(ColorMatrix.IDENTITY, ColorMatrix.saturation(1.0));
        assertTrue(ColorMatrix.saturation(1.0).isIdentity());
        assertEquals(0xFFFF0000, ColorMatrix.saturation(2.0).apply(0xFFFF0000));
    }

    /**
     * Equal values make equal matrices, {@code -0.0} included; the canonical text lists the values in order.
     */
    @Test
    void testEquality()
    {
        final double[] values = offsets(-0.0, 0.0, 0.0, 0.0);

        assertEquals(ColorMatrix.IDENTITY, new ColorMatrix(values));
        assertEquals(ColorMatrix.IDENTITY.hashCode(), new ColorMatrix(values).hashCode());
        assertTrue(new ColorMatrix(values).isIdentity());
        assertNotEquals(ColorMatrix.IDENTITY, new ColorMatrix(offsets(1.0, 0.0, 0.0, 0.0)));
        assertEquals("grading(1.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0,0.0,0.0,0.0,0.0,1.0,0.0)",
                     ColorMatrix.IDENTITY.toString());
    }

    /**
     * The values are copied: changing the array afterwards changes nothing.
     */
    @Test
    void testCopied()
    {
        final double[] values = offsets(0.0, 0.0, 0.0, 0.0);
        final ColorMatrix matrix = new ColorMatrix(values);

        values[0] = 2.0;

        assertEquals(1.0, matrix.get(0, 0));
        assertTrue(matrix.isIdentity());
    }

    /**
     * A value that is not finite is refused, as are a missing array and a wrong number of values.
     *
     * @param value The value that is not finite.
     */
    @ParameterizedTest
    @ValueSource(doubles =
    {
        Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY
    })
    void testInvalid(double value)
    {
        final double[] invalid = offsets(value, 0.0, 0.0, 0.0);
        final double[] tooMany = new double[ColorMatrix.SIZE + 1];

        assertThrows(IllegalArgumentException.class, () -> new ColorMatrix(invalid));
        assertThrows(IllegalArgumentException.class, () -> ColorMatrix.saturation(value));
        assertThrows(IllegalArgumentException.class, () -> new ColorMatrix((double[]) null));
        assertThrows(IllegalArgumentException.class, () -> new ColorMatrix(1.0, 0.0));
        assertThrows(IllegalArgumentException.class, () -> new ColorMatrix(tooMany));
    }

    /**
     * A cell outside the 4 by 5 matrix is refused.
     */
    @Test
    void testCellOutside()
    {
        assertThrows(IndexOutOfBoundsException.class, () -> ColorMatrix.IDENTITY.get(4, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> ColorMatrix.IDENTITY.get(0, 5));
        assertThrows(IndexOutOfBoundsException.class, () -> ColorMatrix.IDENTITY.get(-1, 0));
        assertThrows(IndexOutOfBoundsException.class, () -> ColorMatrix.IDENTITY.get(0, -1));
    }
}
