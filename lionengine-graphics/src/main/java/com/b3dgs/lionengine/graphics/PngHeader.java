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

import java.io.IOException;

/**
 * Header of a PNG image, read from its {@code IHDR} chunk and checked (ADR-0033, REQ-SPRITE-009).
 *
 * <p>
 * Every color type is accepted with the bit depths of the PNG specification: grayscale 1, 2, 4, 8 and 16; RGB 8 and
 * 16; palette 1, 2, 4 and 8; grayscale with alpha 8 and 16; RGBA 8 and 16. Adam7 interlacing is rejected (ADR-0033).
 * </p>
 *
 * @param width The width in pixels (positive).
 * @param height The height in pixels (positive).
 * @param bitDepth The bits per sample.
 * @param colorType The PNG color type: {@link #GRAY}, {@link #RGB}, {@link #PALETTE}, {@link #GRAY_ALPHA} or
 *            {@link #RGBA}.
 */
record PngHeader(int width, int height, int bitDepth, int colorType)
{
    /** Color type of grayscale images. */
    static final int GRAY = 0;
    /** Color type of RGB images. */
    static final int RGB = 2;
    /** Color type of palette images. */
    static final int PALETTE = 3;
    /** Color type of grayscale images with alpha. */
    static final int GRAY_ALPHA = 4;
    /** Color type of RGBA images. */
    static final int RGBA = 6;
    /** Bit depth of the samples reduced to 8 bits. */
    static final int DEPTH_16 = 16;

    /** Number of bytes of the header data. */
    private static final int LENGTH = 13;
    /** Offset of the height in the header data. */
    private static final int OFFSET_HEIGHT = 4;
    /** Offset of the bit depth in the header data. */
    private static final int OFFSET_DEPTH = 8;
    /** Offset of the color type in the header data. */
    private static final int OFFSET_COLOR = 9;
    /** Offset of the compression method in the header data. */
    private static final int OFFSET_COMPRESSION = 10;
    /** Offset of the filter method in the header data. */
    private static final int OFFSET_FILTER = 11;
    /** Offset of the interlace method in the header data. */
    private static final int OFFSET_INTERLACE = 12;
    /** Interlace method Adam7. */
    private static final int ADAM7 = 1;
    /** Bits in a byte. */
    private static final int BYTE_BITS = 8;
    /** Mask of a byte. */
    private static final int BYTE_MASK = 0xFF;
    /** Bit depths of grayscale images, one bit per depth: 1, 2, 4, 8 and 16. */
    private static final int DEPTHS_GRAY = 0x10116;
    /** Bit depths of palette images, one bit per depth: 1, 2, 4 and 8. */
    private static final int DEPTHS_PALETTE = 0x116;
    /** Bit depths of the other color types, one bit per depth: 8 and 16. */
    private static final int DEPTHS_TRUE = 0x10100;
    /** Number of samples of an RGBA pixel. */
    private static final int RGBA_SAMPLES = 4;
    /** Error message of a missing header. */
    private static final String ERROR_FIRST = "PNG header (IHDR) not first";
    /** Error message of an invalid size. */
    private static final String ERROR_SIZE = "PNG size invalid: ";
    /** Error message of an invalid color type or bit depth. */
    private static final String ERROR_FORMAT = "PNG color type and bit depth invalid: ";
    /** Error message of an unknown method. */
    private static final String ERROR_METHOD = "PNG compression, filter or interlace method unknown";
    /** Error message of an interlaced image. */
    private static final String ERROR_INTERLACED = "Interlaced (Adam7) PNG not supported";
    /** Separator of the two values of a message. */
    private static final String SEPARATOR = " x ";

    /**
     * Reads the header from the first chunk.
     *
     * @param chunks The chunks, just after the signature.
     * @return The header.
     * @throws IOException If reading fails.
     * @throws ImageDecodingException If the first chunk is not a valid header, or the image is interlaced.
     */
    static PngHeader read(PngChunks chunks) throws IOException
    {
        if (chunks.next() != PngChunks.IHDR)
        {
            throw new ImageDecodingException(ERROR_FIRST);
        }
        final byte[] data = new byte[LENGTH];
        chunks.readFully(data, LENGTH);
        chunks.end();

        final PngHeader header = new PngHeader(toInt(data, 0),
                                               toInt(data, OFFSET_HEIGHT),
                                               data[OFFSET_DEPTH] & BYTE_MASK,
                                               data[OFFSET_COLOR] & BYTE_MASK);
        header.check();
        final int interlace = data[OFFSET_INTERLACE] & BYTE_MASK;
        if (data[OFFSET_COMPRESSION] != 0 || data[OFFSET_FILTER] != 0 || interlace > ADAM7)
        {
            throw new ImageDecodingException(ERROR_METHOD);
        }
        if (interlace == ADAM7)
        {
            throw new ImageDecodingException(ERROR_INTERLACED);
        }
        return header;
    }

    /**
     * Reads a big-endian integer.
     *
     * @param data The bytes.
     * @param offset The index of the first byte.
     * @return The integer.
     */
    private static int toInt(byte[] data, int offset)
    {
        int value = 0;
        for (int i = 0; i < Integer.BYTES; i++)
        {
            value = (value << BYTE_BITS) | (data[offset + i] & BYTE_MASK);
        }
        return value;
    }

    /**
     * Returns the number of samples of a pixel.
     *
     * @return The number of samples: 1 for grayscale and palette, 2 for grayscale with alpha, 3 for RGB, 4 for RGBA.
     */
    int samples()
    {
        return switch (colorType)
        {
            case RGB -> RGBA_SAMPLES - 1;
            case GRAY_ALPHA -> 2;
            case RGBA -> RGBA_SAMPLES;
            default -> 1;
        };
    }

    /**
     * Returns the number of bytes of a filtered row, without its filter type byte.
     *
     * @return The number of bytes.
     */
    int rowBytes()
    {
        return (int) rowBytesLong();
    }

    /**
     * Returns the distance in bytes between a byte and the matching byte of the pixel on its left, for the filters.
     *
     * @return The number of bytes of a pixel, at least 1.
     */
    int filterStride()
    {
        return Math.max(1, samples() * bitDepth / BYTE_BITS);
    }

    /**
     * Returns the number of bytes of a filtered row, without its filter type byte, without overflow.
     *
     * @return The number of bytes.
     */
    private long rowBytesLong()
    {
        return ((long) width * samples() * bitDepth + BYTE_BITS - 1) / BYTE_BITS;
    }

    /**
     * Checks the size, the color type and the bit depth.
     *
     * @throws ImageDecodingException If one is invalid.
     */
    private void check()
    {
        if (width <= 0 || height <= 0 || (long) width * height > PixelBuffer.MAX_PIXELS)
        {
            throw new ImageDecodingException(ERROR_SIZE + Integer.toUnsignedString(width)
                                             + SEPARATOR
                                             + Integer.toUnsignedString(height));
        }
        final int depths = switch (colorType)
        {
            case GRAY -> DEPTHS_GRAY;
            case PALETTE -> DEPTHS_PALETTE;
            case RGB, GRAY_ALPHA, RGBA -> DEPTHS_TRUE;
            default -> 0;
        };
        if (bitDepth > DEPTH_16 || ((depths >>> bitDepth) & 1) == 0)
        {
            throw new ImageDecodingException(ERROR_FORMAT + colorType + SEPARATOR + bitDepth);
        }
        if (rowBytesLong() >= Integer.MAX_VALUE)
        {
            throw new ImageDecodingException(ERROR_SIZE + width + SEPARATOR + height);
        }
    }
}
