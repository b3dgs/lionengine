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
 * Converts the unfiltered rows of a PNG image to packed {@code 0xAARRGGBB} pixels with straight alpha, using its
 * palette ({@code PLTE}) and its transparency ({@code tRNS}) (ADR-0033, REQ-SPRITE-009).
 *
 * <p>
 * Samples of 1, 2 and 4 bits are scaled to 8 bits, multiplied by 255, 85 and 17, so that the largest sample becomes
 * {@code 255}; samples of 16 bits are reduced to their most significant byte. A transparent color of
 * {@code tRNS} is compared with the samples before that reduction, at the depth of the image. A suggested palette of an
 * RGB or RGBA image is ignored, and so is a {@code tRNS} chunk of an image with an alpha channel, as other decoders do.
 * One instance per decoding; not thread-safe.
 * </p>
 */
final class PngColors
{
    /** No transparent color. */
    private static final long NONE = -1L;
    /** Number of bytes of a palette entry. */
    private static final int ENTRY_BYTES = 3;
    /** Largest number of palette entries. */
    private static final int MAX_ENTRIES = 256;
    /** Number of bytes of a grayscale transparency. */
    private static final int GRAY_BYTES = 2;
    /** Number of bytes of an RGB transparency. */
    private static final int RGB_BYTES = 6;
    /** Index of the blue sample of an RGB pixel. */
    private static final int BLUE = 2;
    /** Index of the alpha sample of an RGBA pixel. */
    private static final int ALPHA = 3;
    /** Shift of the alpha in a packed pixel. */
    private static final int ALPHA_SHIFT = 24;
    /** Shift of the red in a packed pixel. */
    private static final int RED_SHIFT = 16;
    /** Bits in a byte, also the shift of the green in a packed pixel. */
    private static final int BYTE_BITS = 8;
    /** Mask of the bit index in a byte. */
    private static final int BIT_MASK = 7;
    /** Shift from a bit index to a byte index. */
    private static final int BYTE_SHIFT = 3;
    /** Number of samples of an RGB pixel. */
    private static final int RGB_SAMPLES = 3;
    /** Shift of a 16-bit sample in an RGB transparency key. */
    private static final int SAMPLE_SHIFT = 16;
    /** Mask of a byte. */
    private static final int BYTE_MASK = 0xFF;
    /** Opaque alpha in a packed pixel. */
    private static final int OPAQUE = 0xFF000000;
    /** Error message of an invalid palette. */
    private static final String ERROR_PALETTE = "PNG palette (PLTE) invalid";
    /** Error message of an invalid transparency. */
    private static final String ERROR_TRANSPARENCY = "PNG transparency (tRNS) invalid";
    /** Error message of a missing palette. */
    private static final String ERROR_NO_PALETTE = "PNG palette (PLTE) missing";
    /** Error message of a palette index out of range. */
    private static final String ERROR_INDEX = "PNG palette index out of range: ";

    /**
     * Packs a pixel.
     *
     * @param alpha The alpha, 0 to 255.
     * @param red The red, 0 to 255.
     * @param green The green, 0 to 255.
     * @param blue The blue, 0 to 255.
     * @return The packed pixel.
     */
    private static int argb(int alpha, int red, int green, int blue)
    {
        return (alpha << ALPHA_SHIFT) | (red << RED_SHIFT) | (green << BYTE_BITS) | blue;
    }

    /** Color type. */
    private final int colorType;
    /** Bits per sample. */
    private final int depth;
    /** Width in pixels. */
    private final int width;
    /** Mask of a sample of at most 8 bits. */
    private final int sampleMask;
    /** Factor scaling a sample of at most 8 bits to 8 bits. */
    private final int scale;
    /** Palette, packed pixels, empty until read. */
    private int[] palette = new int[0];
    /** Transparent sample (grayscale) or samples (RGB, 16 bits each), {@link #NONE} if none. */
    private long transparent = NONE;

    /**
     * Creates the converter.
     *
     * @param header The header of the image.
     */
    PngColors(PngHeader header)
    {
        super();

        colorType = header.colorType();
        depth = header.bitDepth();
        width = header.width();
        sampleMask = (1 << Math.min(depth, BYTE_BITS)) - 1;
        scale = BYTE_MASK / sampleMask;
    }

    /**
     * Reads the palette from the data of a {@code PLTE} chunk.
     *
     * @param chunks The chunks, at the data of the {@code PLTE} chunk.
     * @throws IOException If reading fails.
     * @throws ImageDecodingException If the palette is invalid or not allowed for the color type.
     */
    void readPalette(PngChunks chunks) throws IOException
    {
        if (colorType == PngHeader.GRAY || colorType == PngHeader.GRAY_ALPHA || palette.length > 0)
        {
            throw new ImageDecodingException(ERROR_PALETTE);
        }
        final int length = chunks.remaining();
        if (colorType == PngHeader.PALETTE)
        {
            if (length == 0 || length % ENTRY_BYTES != 0 || length > MAX_ENTRIES * ENTRY_BYTES)
            {
                throw new ImageDecodingException(ERROR_PALETTE);
            }
            final byte[] data = new byte[length];
            chunks.readFully(data, length);
            palette = new int[length / ENTRY_BYTES];
            for (int i = 0; i < palette.length; i++)
            {
                final int offset = i * ENTRY_BYTES;
                palette[i] = argb(BYTE_MASK,
                                  data[offset] & BYTE_MASK,
                                  data[offset + 1] & BYTE_MASK,
                                  data[offset + BLUE] & BYTE_MASK);
            }
        }
    }

    /**
     * Reads the transparency from the data of a {@code tRNS} chunk.
     *
     * @param chunks The chunks, at the data of the {@code tRNS} chunk.
     * @throws IOException If reading fails.
     * @throws ImageDecodingException If the transparency is invalid.
     */
    void readTransparency(PngChunks chunks) throws IOException
    {
        switch (colorType)
        {
            case PngHeader.GRAY:
                transparent = readSamples(chunks, GRAY_BYTES);
                break;
            case PngHeader.RGB:
                transparent = readSamples(chunks, RGB_BYTES);
                break;
            case PngHeader.PALETTE:
                readPaletteAlpha(chunks);
                break;
            default:
                // Ignored: the image has an alpha channel.
                break;
        }
    }

    /**
     * Checks that every chunk needed before the image data was read.
     *
     * @throws ImageDecodingException If the palette of a palette image is missing.
     */
    void checkComplete()
    {
        if (colorType == PngHeader.PALETTE && palette.length == 0)
        {
            throw new ImageDecodingException(ERROR_NO_PALETTE);
        }
    }

    /**
     * Converts one row.
     *
     * @param row The unfiltered row, its filter type byte first.
     * @param out The packed pixels of the row, {@code width} values written.
     * @throws ImageDecodingException If a palette index is out of range.
     */
    void convert(byte[] row, int[] out)
    {
        switch (colorType)
        {
            case PngHeader.GRAY:
                convertGray(row, out);
                break;
            case PngHeader.RGB:
                convertRgb(row, out);
                break;
            case PngHeader.PALETTE:
                convertPalette(row, out);
                break;
            case PngHeader.GRAY_ALPHA:
                convertGrayAlpha(row, out);
                break;
            default:
                convertRgba(row, out);
                break;
        }
    }

    /**
     * Reads 16-bit samples from the data of a chunk.
     *
     * @param chunks The chunks.
     * @param length The expected number of bytes, two per sample.
     * @return The samples, the first one in the most significant bits.
     * @throws IOException If reading fails.
     * @throws ImageDecodingException If the data length is not {@code length}.
     */
    private static long readSamples(PngChunks chunks, int length) throws IOException
    {
        if (chunks.remaining() != length)
        {
            throw new ImageDecodingException(ERROR_TRANSPARENCY);
        }
        final byte[] data = new byte[length];
        chunks.readFully(data, length);
        long samples = 0L;
        for (int i = 0; i < length; i += 2)
        {
            final int sample = ((data[i] & BYTE_MASK) << BYTE_BITS) | (data[i + 1] & BYTE_MASK);
            samples = (samples << SAMPLE_SHIFT) | sample;
        }
        return samples;
    }

    /**
     * Reads the alpha of the first palette entries.
     *
     * @param chunks The chunks.
     * @throws IOException If reading fails.
     * @throws ImageDecodingException If the palette is missing or shorter than the alpha values.
     */
    private void readPaletteAlpha(PngChunks chunks) throws IOException
    {
        final int length = chunks.remaining();
        if (palette.length == 0 || length > palette.length)
        {
            throw new ImageDecodingException(ERROR_TRANSPARENCY);
        }
        final byte[] data = new byte[length];
        chunks.readFully(data, length);
        for (int i = 0; i < length; i++)
        {
            palette[i] = (palette[i] & ~OPAQUE) | ((data[i] & BYTE_MASK) << ALPHA_SHIFT);
        }
    }

    /**
     * Returns a sample of a row, at the depth of the image.
     *
     * @param row The row, its filter type byte first.
     * @param index The index of the sample in the row.
     * @return The sample.
     */
    private int sample(byte[] row, int index)
    {
        final int value;
        if (depth == PngHeader.DEPTH_16)
        {
            final int offset = 1 + 2 * index;
            value = ((row[offset] & BYTE_MASK) << BYTE_BITS) | (row[offset + 1] & BYTE_MASK);
        }
        else
        {
            final long bit = (long) index * depth;
            final int shift = BYTE_BITS - depth - (int) (bit & BIT_MASK);
            value = (row[1 + (int) (bit >>> BYTE_SHIFT)] >>> shift) & sampleMask;
        }
        return value;
    }

    /**
     * Returns a sample of a row scaled or reduced to 8 bits.
     *
     * @param row The row, its filter type byte first.
     * @param index The index of the sample in the row.
     * @return The sample, 0 to 255.
     */
    private int sample8(byte[] row, int index)
    {
        return to8(sample(row, index));
    }

    /**
     * Scales or reduces a sample to 8 bits.
     *
     * @param sample The sample, at the depth of the image.
     * @return The sample, 0 to 255.
     */
    private int to8(int sample)
    {
        final int value;
        if (depth == PngHeader.DEPTH_16)
        {
            value = sample >>> BYTE_BITS;
        }
        else
        {
            value = sample * scale;
        }
        return value;
    }

    /**
     * Converts a grayscale row.
     *
     * @param row The row.
     * @param out The pixels.
     */
    private void convertGray(byte[] row, int[] out)
    {
        for (int x = 0; x < width; x++)
        {
            final int gray = sample(row, x);
            final int value = to8(gray);
            final int alpha = gray == transparent ? 0 : BYTE_MASK;
            out[x] = argb(alpha, value, value, value);
        }
    }

    /**
     * Converts an RGB row.
     *
     * @param row The row.
     * @param out The pixels.
     */
    private void convertRgb(byte[] row, int[] out)
    {
        for (int x = 0; x < width; x++)
        {
            final int first = x * RGB_SAMPLES;
            final int red = sample(row, first);
            final int green = sample(row, first + 1);
            final int blue = sample(row, first + BLUE);
            final long key = ((long) red << (SAMPLE_SHIFT + SAMPLE_SHIFT)) | ((long) green << SAMPLE_SHIFT) | blue;
            final int alpha = key == transparent ? 0 : BYTE_MASK;
            out[x] = argb(alpha, to8(red), to8(green), to8(blue));
        }
    }

    /**
     * Converts a palette row.
     *
     * @param row The row.
     * @param out The pixels.
     * @throws ImageDecodingException If an index is out of range.
     */
    private void convertPalette(byte[] row, int[] out)
    {
        for (int x = 0; x < width; x++)
        {
            final int index = sample(row, x);
            if (index >= palette.length)
            {
                throw new ImageDecodingException(ERROR_INDEX + index);
            }
            out[x] = palette[index];
        }
    }

    /**
     * Converts a grayscale row with alpha.
     *
     * @param row The row.
     * @param out The pixels.
     */
    private void convertGrayAlpha(byte[] row, int[] out)
    {
        for (int x = 0; x < width; x++)
        {
            final int value = sample8(row, 2 * x);
            out[x] = argb(sample8(row, 2 * x + 1), value, value, value);
        }
    }

    /**
     * Converts an RGBA row.
     *
     * @param row The row.
     * @param out The pixels.
     */
    private void convertRgba(byte[] row, int[] out)
    {
        for (int x = 0; x < width; x++)
        {
            final int first = x * (ALPHA + 1);
            out[x] = argb(sample8(row, first + ALPHA),
                          sample8(row, first),
                          sample8(row, first + 1),
                          sample8(row, first + BLUE));
        }
    }
}
