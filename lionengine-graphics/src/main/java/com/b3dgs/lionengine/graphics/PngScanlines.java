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
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

/**
 * Inflates the zlib stream of the consecutive {@code IDAT} chunks of a PNG image and removes the filter of each row
 * (filter types None, Sub, Up, Average and Paeth) (ADR-0026, REQ-SPRITE-009).
 *
 * <p>
 * Only two rows are held at a time, whatever the size of the image. The caller owns the {@link Inflater} and ends it.
 * One instance per decoding; not thread-safe.
 * </p>
 */
final class PngScanlines
{
    /** Filter type None. */
    private static final int NONE = 0;
    /** Filter type Sub. */
    private static final int SUB = 1;
    /** Filter type Up. */
    private static final int UP = 2;
    /** Filter type Average. */
    private static final int AVERAGE = 3;
    /** Filter type Paeth. */
    private static final int PAETH = 4;
    /** Size of the buffer of compressed bytes given to the inflater. */
    private static final int INPUT_BYTES = 8192;
    /** Mask of a byte. */
    private static final int BYTE_MASK = 0xFF;
    /** Error message of an invalid filter type. */
    private static final String ERROR_FILTER = "PNG filter type invalid: ";
    /** Error message of invalid compressed data. */
    private static final String ERROR_COMPRESSION = "PNG image data corrupted";
    /** Error message of compressed data ending before the last row. */
    private static final String ERROR_SHORT = "PNG image data shorter than the image";

    /**
     * Returns the Paeth predictor of a byte.
     *
     * @param left The byte on the left, unsigned.
     * @param up The byte above, unsigned.
     * @param upLeft The byte above on the left, unsigned.
     * @return The predictor: the closest of the three to {@code left + up - upLeft}, in that order on ties.
     */
    private static int paeth(int left, int up, int upLeft)
    {
        final int estimate = left + up - upLeft;
        final int toLeft = Math.abs(estimate - left);
        final int toUp = Math.abs(estimate - up);
        final int toUpLeft = Math.abs(estimate - upLeft);
        final int predictor;
        if (toLeft <= toUp && toLeft <= toUpLeft)
        {
            predictor = left;
        }
        else if (toUp <= toUpLeft)
        {
            predictor = up;
        }
        else
        {
            predictor = upLeft;
        }
        return predictor;
    }

    /** The chunks, at the data of the first {@code IDAT} chunk. */
    private final PngChunks chunks;
    /** The inflater, owned by the caller. */
    private final Inflater inflater;
    /** Distance in bytes to the matching byte of the pixel on the left. */
    private final int stride;
    /** Compressed bytes given to the inflater. */
    private final byte[] input = new byte[INPUT_BYTES];
    /** Row being read, its filter type byte first. */
    private byte[] current;
    /** Previous row, unfiltered, zero before the first row. */
    private byte[] previous;

    /**
     * Creates the reader.
     *
     * @param chunks The chunks, at the data of the first {@code IDAT} chunk.
     * @param inflater The inflater, owned by the caller.
     * @param header The header of the image.
     */
    PngScanlines(PngChunks chunks, Inflater inflater, PngHeader header)
    {
        super();

        this.chunks = chunks;
        this.inflater = inflater;
        stride = header.filterStride();
        current = new byte[header.rowBytes() + 1];
        previous = new byte[current.length];
    }

    /**
     * Reads the next row and removes its filter.
     *
     * @return The unfiltered row, its filter type byte first; valid until the next call.
     * @throws IOException If reading fails.
     * @throws ImageDecodingException If the data is corrupted, ends before the row, or has an invalid filter type.
     */
    byte[] next() throws IOException
    {
        final byte[] swap = previous;
        previous = current;
        current = swap;
        int offset = 0;
        while (offset < current.length)
        {
            final int count = inflate(offset);
            if (count == 0)
            {
                feed();
            }
            offset += count;
        }
        unfilter();
        return current;
    }

    /**
     * Inflates bytes into the current row.
     *
     * @param offset The first index written.
     * @return The number of bytes written, {@code 0} if the inflater needs input or has finished.
     * @throws ImageDecodingException If the compressed data is invalid.
     */
    private int inflate(int offset)
    {
        try
        {
            return inflater.inflate(current, offset, current.length - offset);
        }
        catch (final DataFormatException exception)
        {
            throw new ImageDecodingException(ERROR_COMPRESSION, exception);
        }
    }

    /**
     * Gives the next compressed bytes to the inflater, from the following {@code IDAT} chunks when needed.
     *
     * @throws IOException If reading fails.
     * @throws ImageDecodingException If the compressed data ends or asks for a preset dictionary, or the next chunk is
     *             not an {@code IDAT} chunk.
     */
    private void feed() throws IOException
    {
        if (inflater.needsDictionary())
        {
            throw new ImageDecodingException(ERROR_COMPRESSION);
        }
        if (inflater.finished())
        {
            throw new ImageDecodingException(ERROR_SHORT);
        }
        while (chunks.remaining() == 0)
        {
            chunks.end();
            if (chunks.next() != PngChunks.IDAT)
            {
                throw new ImageDecodingException(ERROR_SHORT);
            }
        }
        final int count = chunks.read(input, 0, input.length);
        inflater.setInput(input, 0, count);
    }

    /**
     * Removes the filter of the current row.
     *
     * @throws ImageDecodingException If the filter type is invalid.
     */
    private void unfilter()
    {
        final int filter = current[0] & BYTE_MASK;
        switch (filter)
        {
            case NONE:
                break;
            case SUB:
                unfilterSub();
                break;
            case UP:
                unfilterUp();
                break;
            case AVERAGE:
                unfilterAverage();
                break;
            case PAETH:
                unfilterPaeth();
                break;
            default:
                throw new ImageDecodingException(ERROR_FILTER + filter);
        }
    }

    /**
     * Removes the Sub filter: adds the byte on the left.
     */
    private void unfilterSub()
    {
        for (int i = 1 + stride; i < current.length; i++)
        {
            current[i] = (byte) (current[i] + current[i - stride]);
        }
    }

    /**
     * Removes the Up filter: adds the byte above.
     */
    private void unfilterUp()
    {
        for (int i = 1; i < current.length; i++)
        {
            current[i] = (byte) (current[i] + previous[i]);
        }
    }

    /**
     * Removes the Average filter: adds the floor of the mean of the byte on the left and the byte above.
     */
    private void unfilterAverage()
    {
        for (int i = 1; i < current.length; i++)
        {
            final int left = left(current, i);
            current[i] = (byte) (current[i] + ((left + (previous[i] & BYTE_MASK)) >>> 1));
        }
    }

    /**
     * Removes the Paeth filter: adds the Paeth predictor of the bytes on the left, above, and above on the left.
     */
    private void unfilterPaeth()
    {
        for (int i = 1; i < current.length; i++)
        {
            current[i] = (byte) (current[i] + paeth(left(current, i), previous[i] & BYTE_MASK, left(previous, i)));
        }
    }

    /**
     * Returns the byte of the pixel on the left in a row, {@code 0} on the first pixel.
     *
     * @param row The row, its filter type byte first.
     * @param index The index of the byte.
     * @return The byte on the left, unsigned.
     */
    private int left(byte[] row, int index)
    {
        final int left;
        if (index > stride)
        {
            left = row[index - stride] & BYTE_MASK;
        }
        else
        {
            left = 0;
        }
        return left;
    }
}
