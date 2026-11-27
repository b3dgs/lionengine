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

import java.util.Arrays;

/**
 * Color grading: a 4 by 5 matrix over the red, green, blue and alpha channels of a pixel plus an offset, as Android
 * {@code ColorMatrix} and SVG {@code feColorMatrix} (ADR-0026, ADR-0056, REQ-SPRITE-004).
 *
 * <p>
 * The 20 values are row-major: the row of red gives {@code R' = m0·R + m1·G + m2·B + m3·A + m4}, then the rows of
 * green, blue and alpha. Channels are in {@code [0, 255]}, offsets in channel units; each result is rounded half up
 * and clamped to {@code [0, 255]}, on straight (non-premultiplied) alpha. {@link #IDENTITY} changes nothing;
 * {@link #saturation(double)} gives the grayscale and saturation matrices. A matrix is a static color transform: it is
 * baked on the CPU, never applied per frame.
 * </p>
 *
 * <p>
 * Immutable and thread-safe; equal matrices have equal values ({@code -0.0} is stored as {@code 0.0}).
 * </p>
 *
 * @since 11.0.0
 */
public final class ColorMatrix
{
    /** Number of values of a matrix: 4 rows of 5 columns. */
    public static final int SIZE = 20;
    /** The identity matrix, which changes nothing. */
    public static final ColorMatrix IDENTITY = new ColorMatrix(1.0, 0.0, 0.0, 0.0, 0.0,
                                                               0.0, 1.0, 0.0, 0.0, 0.0,
                                                               0.0, 0.0, 1.0, 0.0, 0.0,
                                                               0.0, 0.0, 0.0, 1.0, 0.0);

    /** Number of rows: red, green, blue, alpha. */
    private static final int ROWS = 4;
    /** Number of columns: red, green, blue, alpha, offset. */
    private static final int COLUMNS = 5;
    /** Weight of red in the luminance, as Android {@code ColorMatrix.setSaturation}. */
    private static final double RED_WEIGHT = 0.213;
    /** Weight of green in the luminance, as Android {@code ColorMatrix.setSaturation}. */
    private static final double GREEN_WEIGHT = 0.715;
    /** Weight of blue in the luminance, as Android {@code ColorMatrix.setSaturation}. */
    private static final double BLUE_WEIGHT = 0.072;
    /** Maximum value of a channel. */
    private static final int CHANNEL_MAX = 255;
    /** Half a unit, for the rounding. */
    private static final double HALF = 0.5;
    /** Shift of the alpha channel. */
    private static final int ALPHA_SHIFT = 24;
    /** Shift of the red channel. */
    private static final int RED_SHIFT = 16;
    /** Shift of the green channel. */
    private static final int GREEN_SHIFT = 8;
    /** Column of the offset. */
    private static final int OFFSET = 4;
    /** Separator of the values in the text. */
    private static final char SEPARATOR = ',';

    /**
     * Returns the saturation matrix of a saturation, as Android {@code ColorMatrix.setSaturation}.
     *
     * @param saturation The saturation: {@code 0} for gray (luminance weights {@code 0.213}, {@code 0.715},
     *            {@code 0.072}), {@code 1} for no change, above {@code 1} to saturate (finite).
     * @return The matrix; alpha is unchanged.
     * @throws IllegalArgumentException If the saturation is not finite.
     */
    public static ColorMatrix saturation(double saturation)
    {
        if (!Double.isFinite(saturation))
        {
            throw new IllegalArgumentException("Saturation not finite: " + saturation);
        }
        final double gray = 1.0 - saturation;
        final double red = RED_WEIGHT * gray;
        final double green = GREEN_WEIGHT * gray;
        final double blue = BLUE_WEIGHT * gray;
        return new ColorMatrix(red + saturation, green, blue, 0.0, 0.0,
                               red, green + saturation, blue, 0.0, 0.0,
                               red, green, blue + saturation, 0.0, 0.0,
                               0.0, 0.0, 0.0, 1.0, 0.0);
    }

    /**
     * Rounds a channel half up and clamps it.
     *
     * @param value The channel value.
     * @return The channel in {@code [0, 255]}.
     */
    static int channel(double value)
    {
        final int result;
        if (value <= 0.0)
        {
            result = 0;
        }
        else if (value >= CHANNEL_MAX)
        {
            result = CHANNEL_MAX;
        }
        else
        {
            result = (int) (value + HALF);
        }
        return result;
    }

    /**
     * Tells whether values are the identity.
     *
     * @param values The values.
     * @return {@code true} if the diagonal is {@code 1} and every other value {@code 0}.
     */
    private static boolean computeIdentity(double[] values)
    {
        boolean result = true;
        for (int i = 0; result && i < SIZE; i++)
        {
            final double expected;
            if (i % COLUMNS == i / COLUMNS)
            {
                expected = 1.0;
            }
            else
            {
                expected = 0.0;
            }
            result = Double.compare(values[i], expected) == 0;
        }
        return result;
    }

    /** The values, row-major. */
    private final double[] values;
    /** {@code true} if the matrix changes nothing: the identity. */
    private final boolean neutral;

    /**
     * Creates a matrix.
     *
     * @param values The 20 values, row-major: the rows of red, green, blue and alpha, each with the factors of red,
     *            green, blue and alpha and the offset in channel units (all finite); copied.
     * @throws IllegalArgumentException If the values are {@code null}, not 20, or not all finite.
     */
    public ColorMatrix(double... values)
    {
        super();

        if (values == null || values.length != SIZE)
        {
            throw new IllegalArgumentException("Color matrix needs " + SIZE + " values");
        }
        this.values = new double[SIZE];
        for (int i = 0; i < SIZE; i++)
        {
            if (!Double.isFinite(values[i]))
            {
                throw new IllegalArgumentException("Color matrix value not finite at " + i + ": " + values[i]);
            }
            this.values[i] = values[i] + 0.0;
        }
        neutral = computeIdentity(this.values);
    }

    /**
     * Returns a value.
     *
     * @param row The row: {@code 0} red, {@code 1} green, {@code 2} blue, {@code 3} alpha.
     * @param column The column: {@code 0} red, {@code 1} green, {@code 2} blue, {@code 3} alpha, {@code 4} offset.
     * @return The value.
     * @throws IndexOutOfBoundsException If the row or the column is outside the matrix.
     */
    public double get(int row, int column)
    {
        if (row < 0 || row >= ROWS || column < 0 || column >= COLUMNS)
        {
            throw new IndexOutOfBoundsException("Color matrix cell outside 4 by 5: " + row + ", " + column);
        }
        return values[row * COLUMNS + column];
    }

    /**
     * Tells whether the matrix changes nothing.
     *
     * @return {@code true} for the identity matrix.
     */
    public boolean isIdentity()
    {
        return neutral;
    }

    /**
     * Grades a pixel.
     *
     * @param argb The pixel, packed {@code 0xAARRGGBB}, straight alpha.
     * @return The graded pixel, each channel rounded half up and clamped.
     */
    int apply(int argb)
    {
        final int alpha = argb >>> ALPHA_SHIFT;
        final int red = (argb >> RED_SHIFT) & CHANNEL_MAX;
        final int green = (argb >> GREEN_SHIFT) & CHANNEL_MAX;
        final int blue = argb & CHANNEL_MAX;
        return channel(row(0, red, green, blue, alpha)) << RED_SHIFT
               | channel(row(1, red, green, blue, alpha)) << GREEN_SHIFT
               | channel(row(2, red, green, blue, alpha))
               | channel(row(ROWS - 1, red, green, blue, alpha)) << ALPHA_SHIFT;
    }

    /**
     * Computes one row.
     *
     * @param row The row.
     * @param red The red channel.
     * @param green The green channel.
     * @param blue The blue channel.
     * @param alpha The alpha channel.
     * @return The value of the row, not rounded.
     */
    private double row(int row, int red, int green, int blue, int alpha)
    {
        final int base = row * COLUMNS;
        return values[base] * red
               + values[base + 1] * green
               + values[base + 2] * blue
               + values[base + ROWS - 1] * alpha
               + values[base + OFFSET];
    }

    @Override
    public boolean equals(Object object)
    {
        return object instanceof final ColorMatrix other && Arrays.equals(values, other.values);
    }

    @Override
    public int hashCode()
    {
        return Arrays.hashCode(values);
    }

    /**
     * Returns the canonical text of the matrix, part of the cache discriminator of a baked image.
     *
     * @return {@code grading(} and the 20 values separated by commas, then {@code )}.
     */
    @Override
    public String toString()
    {
        final StringBuilder text = new StringBuilder("grading(");
        for (int i = 0; i < SIZE; i++)
        {
            if (i > 0)
            {
                text.append(SEPARATOR);
            }
            text.append(values[i]);
        }
        return text.append(')').toString();
    }
}
