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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.DeflaterOutputStream;

/**
 * Writes PNG images in memory for tests, from raw samples, with a chosen filter type, chunk split and extra chunks
 * (ADR-0045, REQ-SPRITE-009).
 *
 * <p>
 * Published in the {@code graphics} test JAR, so that the tests of the modules that load images (sheets, tile maps,
 * examples) build their PNG content in memory instead of storing image files. It writes what it is told, including
 * invalid images, so that decoders can be tested on them: nothing is checked. Samples are given at the bit depth of
 * the image, one value per sample, row-major: one sample per pixel for grayscale and palette images (the palette
 * index), two for grayscale with alpha, three for RGB, four for RGBA.
 * </p>
 */
public final class PngWriter
{
    /** Color type of grayscale images. */
    public static final int GRAY = 0;
    /** Color type of RGB images. */
    public static final int RGB = 2;
    /** Color type of palette images. */
    public static final int PALETTE = 3;
    /** Color type of grayscale images with alpha. */
    public static final int GRAY_ALPHA = 4;
    /** Color type of RGBA images. */
    public static final int RGBA = 6;
    /** Filter type None. */
    public static final int NONE = 0;
    /** Filter type Sub. */
    public static final int SUB = 1;
    /** Filter type Up. */
    public static final int UP = 2;
    /** Filter type Average. */
    public static final int AVERAGE = 3;
    /** Filter type Paeth. */
    public static final int PAETH = 4;
    /** Filter mode where row {@code y} uses the filter type {@code y % 5}. */
    public static final int CYCLE = -1;

    /** Number of filter types. */
    private static final int FILTERS = 5;
    /** Signature of a PNG stream. */
    private static final byte[] SIGNATURE =
    {
        (byte) 0x89, 'P', 'N', 'G', '\r', '\n', 0x1A, '\n'
    };

    /**
     * Returns the number of samples of a pixel of a color type.
     *
     * @param colorType The color type.
     * @return The number of samples.
     */
    public static int samplesPerPixel(int colorType)
    {
        return switch (colorType)
        {
            case RGB -> 3;
            case GRAY_ALPHA -> 2;
            case RGBA -> 4;
            default -> 1;
        };
    }

    /**
     * Creates a writer of an 8-bit RGBA image from packed pixels.
     *
     * @param width The width in pixels.
     * @param height The height in pixels.
     * @param argb The pixels, packed {@code 0xAARRGGBB}, row-major.
     * @return The writer.
     */
    public static PngWriter rgba(int width, int height, int... argb)
    {
        final int[] samples = new int[argb.length * 4];
        for (int i = 0; i < argb.length; i++)
        {
            samples[i * 4] = argb[i] >>> 16 & 0xFF;
            samples[i * 4 + 1] = argb[i] >>> 8 & 0xFF;
            samples[i * 4 + 2] = argb[i] & 0xFF;
            samples[i * 4 + 3] = argb[i] >>> 24;
        }
        return new PngWriter(width, height, 8, RGBA).setSamples(samples);
    }

    /**
     * Compresses bytes into a zlib stream, as the image data of a PNG.
     *
     * @param data The bytes.
     * @return The zlib stream.
     */
    public static byte[] zlib(byte[] data)
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        try (DeflaterOutputStream deflater = new DeflaterOutputStream(output))
        {
            deflater.write(data);
        }
        catch (final IOException exception)
        {
            throw new UncheckedIOException(exception);
        }
        return output.toByteArray();
    }

    /**
     * Returns a whole chunk: length, type, data and CRC.
     *
     * @param type The four letters of the type.
     * @param data The data.
     * @return The bytes of the chunk.
     */
    public static byte[] chunk(String type, byte[] data)
    {
        final byte[] name = type.getBytes(StandardCharsets.ISO_8859_1);
        final CRC32 crc = new CRC32();
        crc.update(name);
        crc.update(data);
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        writeInt(output, data.length);
        output.write(name, 0, name.length);
        output.write(data, 0, data.length);
        writeInt(output, (int) crc.getValue());
        return output.toByteArray();
    }

    /**
     * Writes a big-endian integer.
     *
     * @param output The output.
     * @param value The integer.
     */
    private static void writeInt(ByteArrayOutputStream output, int value)
    {
        output.write(value >>> 24);
        output.write(value >>> 16);
        output.write(value >>> 8);
        output.write(value);
    }

    /**
     * Returns the Paeth predictor.
     *
     * @param left The byte on the left.
     * @param up The byte above.
     * @param upLeft The byte above on the left.
     * @return The predictor.
     */
    private static int paeth(int left, int up, int upLeft)
    {
        final int estimate = left + up - upLeft;
        final int toLeft = Math.abs(estimate - left);
        final int toUp = Math.abs(estimate - up);
        final int toUpLeft = Math.abs(estimate - upLeft);
        if (toLeft <= toUp && toLeft <= toUpLeft)
        {
            return left;
        }
        return toUp <= toUpLeft ? up : upLeft;
    }

    /** Width in pixels. */
    private final int width;
    /** Height in pixels. */
    private final int height;
    /** Bits per sample. */
    private final int depth;
    /** Color type. */
    private final int colorType;
    /** Chunks before the palette, as whole chunks. */
    private final List<byte[]> first = new ArrayList<>();
    /** Chunks before the image data, as whole chunks. */
    private final List<byte[]> before = new ArrayList<>();
    /** Chunks after the image data, as whole chunks. */
    private final List<byte[]> after = new ArrayList<>();
    /** Samples, row-major. */
    private int[] samples = new int[0];
    /** Palette entries, {@code 0xRRGGBB}, {@code null} for none. */
    private int[] palette;
    /** Data of the {@code tRNS} chunk, {@code null} for none. */
    private byte[] transparency;
    /** Filter type of every row, or {@link #CYCLE}. */
    private int filter = CYCLE;
    /** Largest data size of an {@code IDAT} chunk. */
    private int chunkSize = Integer.MAX_VALUE;
    /** Interlace method written in the header. */
    private int interlace;
    /** Image data written instead of the compressed rows, {@code null} to compress the rows. */
    private byte[] imageData;

    /**
     * Creates the writer of a transparent black image.
     *
     * @param width The width in pixels written in the header.
     * @param height The height in pixels written in the header.
     * @param depth The bits per sample.
     * @param colorType The color type.
     */
    public PngWriter(int width, int height, int depth, int colorType)
    {
        super();

        this.width = width;
        this.height = height;
        this.depth = depth;
        this.colorType = colorType;
    }

    /**
     * Sets the samples.
     *
     * @param samples The samples at the bit depth, row-major.
     * @return This writer.
     */
    public PngWriter setSamples(int... samples)
    {
        this.samples = samples.clone();
        return this;
    }

    /**
     * Sets the palette, written in a {@code PLTE} chunk.
     *
     * @param rgb The entries, {@code 0xRRGGBB}.
     * @return This writer.
     */
    public PngWriter setPalette(int... rgb)
    {
        palette = rgb.clone();
        return this;
    }

    /**
     * Sets the data of the {@code tRNS} chunk, written after the palette.
     *
     * @param bytes The bytes, one per value.
     * @return This writer.
     */
    public PngWriter setTransparency(int... bytes)
    {
        transparency = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++)
        {
            transparency[i] = (byte) bytes[i];
        }
        return this;
    }

    /**
     * Sets the filter type of every row.
     *
     * @param filter The filter type, from {@link #NONE} to {@link #PAETH}, or {@link #CYCLE}.
     * @return This writer.
     */
    public PngWriter setFilter(int filter)
    {
        this.filter = filter;
        return this;
    }

    /**
     * Splits the image data into {@code IDAT} chunks of at most this size.
     *
     * @param bytes The largest data size of a chunk.
     * @return This writer.
     */
    public PngWriter setChunkSize(int bytes)
    {
        chunkSize = bytes;
        return this;
    }

    /**
     * Sets the interlace method written in the header; the data stays as for a non-interlaced image.
     *
     * @param method The interlace method.
     * @return This writer.
     */
    public PngWriter setInterlace(int method)
    {
        interlace = method;
        return this;
    }

    /**
     * Replaces the compressed rows with other image data.
     *
     * @param data The data of the {@code IDAT} chunks.
     * @return This writer.
     */
    public PngWriter setImageData(byte[] data)
    {
        imageData = data.clone();
        return this;
    }

    /**
     * Adds a chunk right after the header, before the palette.
     *
     * @param type The four letters of the type.
     * @param data The data.
     * @return This writer.
     */
    public PngWriter addFirst(String type, byte[] data)
    {
        first.add(chunk(type, data));
        return this;
    }

    /**
     * Adds a chunk just before the image data.
     *
     * @param type The four letters of the type.
     * @param data The data.
     * @return This writer.
     */
    public PngWriter addBefore(String type, byte[] data)
    {
        before.add(chunk(type, data));
        return this;
    }

    /**
     * Adds a chunk just after the image data.
     *
     * @param type The four letters of the type.
     * @param data The data.
     * @return This writer.
     */
    public PngWriter addAfter(String type, byte[] data)
    {
        after.add(chunk(type, data));
        return this;
    }

    /**
     * Returns the filtered rows, each one preceded by its filter type byte, before compression.
     *
     * @return The bytes of the rows.
     */
    public byte[] filteredRows()
    {
        final int rowBytes = (width * samplesPerPixel(colorType) * depth + 7) / 8;
        final int stride = Math.max(1, samplesPerPixel(colorType) * depth / 8);
        final byte[] rows = new byte[height * (rowBytes + 1)];
        byte[] previous = new byte[rowBytes];
        for (int y = 0; y < height; y++)
        {
            final byte[] raw = pack(y, rowBytes);
            final int type = filter == CYCLE ? y % FILTERS : filter;
            final int start = y * (rowBytes + 1);
            rows[start] = (byte) type;
            for (int i = 0; i < rowBytes; i++)
            {
                final int left = i >= stride ? raw[i - stride] & 0xFF : 0;
                final int up = previous[i] & 0xFF;
                final int upLeft = i >= stride ? previous[i - stride] & 0xFF : 0;
                rows[start + 1 + i] = (byte) (raw[i] - predictor(type, left, up, upLeft));
            }
            previous = raw;
        }
        return rows;
    }

    /**
     * Writes the image.
     *
     * @return The bytes of the PNG stream.
     */
    public byte[] write()
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        output.write(SIGNATURE, 0, SIGNATURE.length);
        final byte[] header =
        {
            (byte) (width >>> 24), (byte) (width >>> 16), (byte) (width >>> 8), (byte) width, (byte) (height >>> 24),
            (byte) (height >>> 16), (byte) (height >>> 8), (byte) height, (byte) depth, (byte) colorType, 0, 0,
            (byte) interlace
        };
        writeChunk(output, chunk("IHDR", header));
        first.forEach(chunk -> writeChunk(output, chunk));
        if (palette != null)
        {
            final byte[] entries = new byte[palette.length * 3];
            for (int i = 0; i < palette.length; i++)
            {
                entries[i * 3] = (byte) (palette[i] >>> 16);
                entries[i * 3 + 1] = (byte) (palette[i] >>> 8);
                entries[i * 3 + 2] = (byte) palette[i];
            }
            writeChunk(output, chunk("PLTE", entries));
        }
        if (transparency != null)
        {
            writeChunk(output, chunk("tRNS", transparency));
        }
        before.forEach(chunk -> writeChunk(output, chunk));
        final byte[] data = imageData != null ? imageData : zlib(filteredRows());
        int offset = 0;
        do
        {
            final int length = Math.min(chunkSize, data.length - offset);
            final byte[] part = new byte[length];
            System.arraycopy(data, offset, part, 0, length);
            writeChunk(output, chunk("IDAT", part));
            offset += length;
        }
        while (offset < data.length);
        after.forEach(chunk -> writeChunk(output, chunk));
        writeChunk(output, chunk("IEND", new byte[0]));
        return output.toByteArray();
    }

    /**
     * Packs the samples of a row at the bit depth, most significant bits first.
     *
     * @param y The row.
     * @param rowBytes The number of bytes of the row.
     * @return The bytes of the row.
     */
    private byte[] pack(int y, int rowBytes)
    {
        final byte[] raw = new byte[rowBytes];
        final int count = width * samplesPerPixel(colorType);
        for (int i = 0; i < count; i++)
        {
            final int index = y * count + i;
            final int sample = index < samples.length ? samples[index] : 0;
            if (depth == 16)
            {
                raw[2 * i] = (byte) (sample >>> 8);
                raw[2 * i + 1] = (byte) sample;
            }
            else
            {
                final int bit = i * depth;
                raw[bit / 8] |= (byte) (sample << 8 - depth - bit % 8);
            }
        }
        return raw;
    }

    /**
     * Returns the predictor of a filter type.
     *
     * @param type The filter type.
     * @param left The byte on the left.
     * @param up The byte above.
     * @param upLeft The byte above on the left.
     * @return The predictor, {@code 0} for an invalid filter type.
     */
    private static int predictor(int type, int left, int up, int upLeft)
    {
        return switch (type)
        {
            case SUB -> left;
            case UP -> up;
            case AVERAGE -> (left + up) / 2;
            case PAETH -> paeth(left, up, upLeft);
            default -> 0;
        };
    }

    /**
     * Writes a whole chunk.
     *
     * @param output The output.
     * @param chunk The bytes of the chunk.
     */
    private static void writeChunk(ByteArrayOutputStream output, byte[] chunk)
    {
        output.write(chunk, 0, chunk.length);
    }
}
