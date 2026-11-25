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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test {@link PixelBuffer} (REQ-SPRITE-009, REQ-SPR-001, ADR-0026, ADR-0033).
 */
final class PixelBufferTest
{
    /** Width of the buffers. */
    private static final int WIDTH = 3;
    /** Height of the buffers. */
    private static final int HEIGHT = 2;

    /**
     * A new buffer has its size and is transparent black.
     */
    @Test
    void testCreate()
    {
        final PixelBuffer buffer = new PixelBuffer(WIDTH, HEIGHT);

        assertEquals(WIDTH, buffer.getWidth());
        assertEquals(HEIGHT, buffer.getHeight());
        assertArrayEquals(new int[WIDTH * HEIGHT], buffer.copyPixels());
    }

    /**
     * Sizes that are not positive, or that make more pixels than one array holds, are rejected.
     *
     * @param width The width.
     * @param height The height.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "0, 1", "1, 0", "-1, 1", "1, -1", "65536, 32768", "2147483647, 2"
    })
    void testInvalidSize(int width, int height)
    {
        assertThrows(IllegalArgumentException.class, () -> new PixelBuffer(width, height));
    }

    /**
     * Pixels are read and written by column and row, stored row-major from the top-left pixel, with their alpha.
     */
    @Test
    void testPixels()
    {
        final PixelBuffer buffer = new PixelBuffer(WIDTH, HEIGHT);
        buffer.setPixel(0, 0, 0x80112233);
        buffer.setPixel(2, 0, 0xFF445566);
        buffer.setPixel(1, 1, 0x00778899);

        assertEquals(0x80112233, buffer.getPixel(0, 0));
        assertEquals(0xFF445566, buffer.getPixel(2, 0));
        assertEquals(0x00778899, buffer.getPixel(1, 1));
        assertArrayEquals(new int[]
        {
            0x80112233, 0, 0xFF445566, 0, 0x00778899, 0
        }, buffer.copyPixels());
    }

    /**
     * Pixels outside the buffer are rejected, for reading and writing.
     *
     * @param x The column.
     * @param y The row.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "-1, 0", "0, -1", "3, 0", "0, 2", "3, 2"
    })
    void testOutside(int x, int y)
    {
        final PixelBuffer buffer = new PixelBuffer(WIDTH, HEIGHT);

        assertThrows(IndexOutOfBoundsException.class, () -> buffer.getPixel(x, y));
        assertThrows(IndexOutOfBoundsException.class, () -> buffer.setPixel(x, y, 1));
    }

    /**
     * The arrays given and returned are copies: the buffer never shares its pixels.
     */
    @Test
    void testCopies()
    {
        final PixelBuffer buffer = new PixelBuffer(WIDTH, HEIGHT);
        final int[] pixels =
        {
            1, 2, 3, 4, 5, 6
        };
        buffer.setPixels(pixels);
        pixels[0] = 7;
        final int[] copy = buffer.copyPixels();
        copy[1] = 8;

        assertArrayEquals(new int[]
        {
            1, 2, 3, 4, 5, 6
        }, buffer.copyPixels());
        assertEquals(6, buffer.getPixel(2, 1));
    }

    /**
     * Replacing the pixels needs exactly one value per pixel.
     */
    @Test
    void testSetPixelsInvalid()
    {
        final PixelBuffer buffer = new PixelBuffer(WIDTH, HEIGHT);
        final int[] tooShort = new int[WIDTH * HEIGHT - 1];
        final int[] tooLong = new int[WIDTH * HEIGHT + 1];

        assertThrows(IllegalArgumentException.class, () -> buffer.setPixels(null));
        assertThrows(IllegalArgumentException.class, () -> buffer.setPixels(tooShort));
        assertThrows(IllegalArgumentException.class, () -> buffer.setPixels(tooLong));
    }

    /**
     * A row written by a decoder replaces one row only.
     */
    @Test
    void testSetRow()
    {
        final PixelBuffer buffer = new PixelBuffer(WIDTH, HEIGHT);
        buffer.setRow(1, new int[]
        {
            1, 2, 3, 4
        });

        assertArrayEquals(new int[]
        {
            0, 0, 0, 1, 2, 3
        }, buffer.copyPixels());
    }
}
