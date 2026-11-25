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
 * Modifiable image pixels, owned by the code that holds the buffer (ADR-0026, ADR-0033, REQ-SPRITE-009,
 * REQ-SPR-001).
 *
 * <p>
 * Layout: packed {@code 0xAARRGGBB} values, row-major, origin at the top-left, stride equal to the width, straight
 * (non-premultiplied) alpha; the pixel {@code (x, y)} is the value {@code y * width + x}. This is the layout of the
 * {@code render-api} textures (ADR-0047), so {@link #copyPixels()} can be given to a texture store as it is. A new
 * buffer is transparent black ({@code 0x00000000}).
 * </p>
 *
 * <p>
 * Ownership: a decoder returns a new buffer that belongs to its caller, and the buffer never shares its pixels: the
 * arrays given to {@link #setPixels(int[])} and returned by {@link #copyPixels()} are copies. The cached images of
 * {@code graphics} are never pixel buffers: modifying a buffer never changes a cached image, and turning a buffer into
 * a cached image copies it (ADR-0026).
 * </p>
 *
 * <p>
 * Threads: not thread-safe. A buffer is used by one thread at a time, for example filled by a decoder on a loading
 * worker, then handed over to the runtime thread through the inbox.
 * </p>
 *
 * @since 11.0.0
 */
public final class PixelBuffer
{
    /** Largest number of pixels, the largest size of one {@code int[]}. */
    static final long MAX_PIXELS = Integer.MAX_VALUE;

    /** Separator of a width and a height in messages. */
    private static final String TIMES = " x ";

    /**
     * Checks that a size is positive.
     *
     * @param value The size.
     * @param name The name of the size, for the message.
     * @throws IllegalArgumentException If the size is not positive.
     */
    private static void checkPositive(int value, String name)
    {
        if (value <= 0)
        {
            throw new IllegalArgumentException(name + " not positive: " + value);
        }
    }

    /** Width in pixels. */
    private final int width;
    /** Height in pixels. */
    private final int height;
    /** Pixels, packed {@code 0xAARRGGBB}, row-major. */
    private final int[] argb;

    /**
     * Creates a transparent black buffer.
     *
     * @param width The width in pixels (positive).
     * @param height The height in pixels (positive).
     * @throws IllegalArgumentException If a size is not positive, or if {@code width * height} exceeds
     *             {@link Integer#MAX_VALUE}, the largest number of pixels of one buffer.
     */
    public PixelBuffer(int width, int height)
    {
        super();

        checkPositive(width, "Width");
        checkPositive(height, "Height");
        if ((long) width * height > MAX_PIXELS)
        {
            throw new IllegalArgumentException("Too many pixels: " + width + TIMES + height);
        }
        this.width = width;
        this.height = height;
        argb = new int[width * height];
    }

    /**
     * Returns the width.
     *
     * @return The width in pixels.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * Returns the height.
     *
     * @return The height in pixels.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * Returns a pixel.
     *
     * @param x The column, from {@code 0} at the left.
     * @param y The row, from {@code 0} at the top.
     * @return The pixel, packed {@code 0xAARRGGBB} with straight alpha.
     * @throws IndexOutOfBoundsException If the pixel is outside the buffer.
     */
    public int getPixel(int x, int y)
    {
        return argb[index(x, y)];
    }

    /**
     * Changes a pixel.
     *
     * @param x The column, from {@code 0} at the left.
     * @param y The row, from {@code 0} at the top.
     * @param argb The pixel, packed {@code 0xAARRGGBB} with straight alpha.
     * @throws IndexOutOfBoundsException If the pixel is outside the buffer.
     */
    public void setPixel(int x, int y, int argb)
    {
        this.argb[index(x, y)] = argb;
    }

    /**
     * Returns a copy of every pixel.
     *
     * @return A new array of {@code width * height} pixels, packed {@code 0xAARRGGBB}, row-major from the top-left
     *         pixel, owned by the caller.
     */
    public int[] copyPixels()
    {
        return Arrays.copyOf(argb, argb.length);
    }

    /**
     * Replaces every pixel with a copy of the given ones.
     *
     * @param argb The pixels, exactly {@code width * height} values, packed {@code 0xAARRGGBB}, row-major from the
     *            top-left pixel; copied, so the caller keeps the array (never {@code null}).
     * @throws IllegalArgumentException If {@code argb} is {@code null} or its length is not {@code width * height}.
     */
    public void setPixels(int[] argb)
    {
        if (argb == null || argb.length != this.argb.length)
        {
            throw new IllegalArgumentException("Pixels not " + width + TIMES + height);
        }
        System.arraycopy(argb, 0, this.argb, 0, argb.length);
    }

    /**
     * Replaces the pixels of one row, for the decoders of {@code graphics}.
     *
     * @param y The row, from {@code 0} at the top (in range).
     * @param row The pixels of the row, at least {@code width} values, copied.
     */
    void setRow(int y, int[] row)
    {
        System.arraycopy(row, 0, argb, y * width, width);
    }

    /**
     * Returns the index of a pixel in the array.
     *
     * @param x The column.
     * @param y The row.
     * @return The index.
     * @throws IndexOutOfBoundsException If the pixel is outside the buffer.
     */
    private int index(int x, int y)
    {
        if (x < 0 || x >= width || y < 0 || y >= height)
        {
            throw new IndexOutOfBoundsException("Pixel (" + x + ", " + y + ") outside " + width + TIMES + height);
        }
        return y * width + x;
    }
}
