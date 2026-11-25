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
import java.io.InputStream;
import java.util.zip.CRC32;

/**
 * Reads the chunks of a PNG stream: signature, then length, type, data and CRC of each chunk, checking the CRC of
 * every chunk (REQ-SPRITE-009).
 *
 * <p>
 * The data of the current chunk is read with {@link #read(byte[], int, int)} and {@link #readFully(byte[], int)};
 * {@link #end()} skips what is left of it and checks its CRC. The end of the stream before the end of the image is a
 * truncation. One instance per decoding; not thread-safe.
 * </p>
 */
final class PngChunks
{
    /** Chunk type of the image header. */
    static final int IHDR = 0x49484452;
    /** Chunk type of the palette. */
    static final int PLTE = 0x504C5445;
    /** Chunk type of the image data. */
    static final int IDAT = 0x49444154;
    /** Chunk type of the image end. */
    static final int IEND = 0x49454E44;
    /** Chunk type of the transparency. */
    static final int TRNS = 0x74524E53;
    /** Chunk type of the animation control of an animated PNG. */
    static final int ACTL = 0x6163544C;
    /** Chunk type of the frame control of an animated PNG. */
    static final int FCTL = 0x6663544C;
    /** Chunk type of the frame data of an animated PNG. */
    static final int FDAT = 0x66644154;

    /** The eight bytes of the PNG signature. */
    private static final long SIGNATURE = 0x89504E470D0A1A0AL;
    /** Number of bytes of the signature. */
    private static final int SIGNATURE_BYTES = 8;
    /** Number of bytes of a chunk length, type or CRC. */
    private static final int INT_BYTES = 4;
    /** Bits in a byte. */
    private static final int BYTE_BITS = 8;
    /** Mask of a byte. */
    private static final int BYTE_MASK = 0xFF;
    /** Mask of the unsigned 32-bit CRC. */
    private static final long CRC_MASK = 0xFFFFFFFFL;
    /** Bit of the type that marks an ancillary chunk (lower case first letter). */
    private static final int ANCILLARY = 0x20000000;
    /** Bit that makes an ASCII letter lower case. */
    private static final int LOWER_CASE = 0x20;
    /** First ASCII letter, after folding to lower case. */
    private static final int LETTER_FIRST = 'a';
    /** Last ASCII letter, after folding to lower case. */
    private static final int LETTER_LAST = 'z';
    /** Size of the buffer used to skip data. */
    private static final int SKIP_BYTES = 512;
    /** Error message of an invalid signature. */
    private static final String ERROR_SIGNATURE = "Not a PNG image: invalid signature";
    /** Error message of an invalid chunk length. */
    private static final String ERROR_LENGTH = "PNG chunk length invalid: ";
    /** Error message of an invalid chunk type. */
    private static final String ERROR_TYPE = "PNG chunk type invalid: ";
    /** Error message of a CRC mismatch. */
    private static final String ERROR_CRC = "PNG chunk corrupted (CRC mismatch): ";
    /** Error message of a truncated stream. */
    private static final String ERROR_TRUNCATED = "PNG truncated";

    /**
     * Returns the name of a chunk type.
     *
     * @param type The chunk type.
     * @return The four characters of the type.
     */
    static String name(int type)
    {
        final char[] name = new char[INT_BYTES];
        for (int i = 0; i < INT_BYTES; i++)
        {
            name[i] = (char) ((type >>> ((INT_BYTES - 1 - i) * BYTE_BITS)) & BYTE_MASK);
        }
        return new String(name);
    }

    /**
     * Tells whether a chunk type is ancillary, so that a decoder that does not know it may skip it.
     *
     * @param type The chunk type.
     * @return {@code true} if ancillary, {@code false} if critical.
     */
    static boolean isAncillary(int type)
    {
        return (type & ANCILLARY) != 0;
    }

    /**
     * Tells whether each byte of a chunk type is an ASCII letter.
     *
     * @param type The chunk type.
     * @return {@code true} if valid.
     */
    private static boolean isValidType(int type)
    {
        boolean valid = true;
        for (int i = 0; i < INT_BYTES; i++)
        {
            final int letter = ((type >>> (i * BYTE_BITS)) & BYTE_MASK) | LOWER_CASE;
            valid &= letter >= LETTER_FIRST && letter <= LETTER_LAST;
        }
        return valid;
    }

    /** The stream, not closed here. */
    private final InputStream input;
    /** CRC of the current chunk, type and data. */
    private final CRC32 crc = new CRC32();
    /** Buffer of the fixed-size fields. */
    private final byte[] field = new byte[SIGNATURE_BYTES];
    /** Buffer used to skip data. */
    private final byte[] skip = new byte[SKIP_BYTES];
    /** Type of the current chunk. */
    private int type;
    /** Bytes of data left in the current chunk. */
    private int remaining;

    /**
     * Creates the reader.
     *
     * @param input The stream, not closed by the reader.
     */
    PngChunks(InputStream input)
    {
        super();

        this.input = input;
    }

    /**
     * Reads and checks the signature.
     *
     * @throws IOException If reading fails.
     * @throws ImageDecodingException If the signature is not the PNG one, or the stream is shorter.
     */
    void readSignature() throws IOException
    {
        if (!fill(field, SIGNATURE_BYTES) || toLong(field) != SIGNATURE)
        {
            throw new ImageDecodingException(ERROR_SIGNATURE);
        }
    }

    /**
     * Reads the length and the type of the next chunk; its data comes next.
     *
     * @return The type of the chunk.
     * @throws IOException If reading fails.
     * @throws ImageDecodingException If the stream ends, or the length or the type is invalid.
     */
    int next() throws IOException
    {
        final int length = readInt();
        if (length < 0)
        {
            throw new ImageDecodingException(ERROR_LENGTH + Integer.toUnsignedString(length));
        }
        readField(INT_BYTES);
        crc.reset();
        crc.update(field, 0, INT_BYTES);
        type = toInt(field, 0);
        if (!isValidType(type))
        {
            throw new ImageDecodingException(ERROR_TYPE + Integer.toHexString(type));
        }
        remaining = length;
        return type;
    }

    /**
     * Returns the number of data bytes left in the current chunk.
     *
     * @return The number of bytes.
     */
    int remaining()
    {
        return remaining;
    }

    /**
     * Reads data of the current chunk, as many bytes as asked or as left in the chunk.
     *
     * @param buffer The destination.
     * @param offset The first index written.
     * @param length The number of bytes asked.
     * @return The number of bytes read: {@code length}, or fewer at the end of the chunk.
     * @throws IOException If reading fails.
     * @throws ImageDecodingException If the stream ends before the chunk.
     */
    int read(byte[] buffer, int offset, int length) throws IOException
    {
        final int count = Math.min(length, remaining);
        int done = 0;
        while (done < count)
        {
            final int read = input.read(buffer, offset + done, count - done);
            if (read < 0)
            {
                throw new ImageDecodingException(ERROR_TRUNCATED);
            }
            done += read;
        }
        crc.update(buffer, offset, count);
        remaining -= count;
        return count;
    }

    /**
     * Reads the whole data of the current chunk.
     *
     * @param buffer The destination, at least as long as the data.
     * @param length The expected length of the data.
     * @throws IOException If reading fails.
     * @throws ImageDecodingException If the length of the data is not {@code length}, or the stream ends.
     */
    void readFully(byte[] buffer, int length) throws IOException
    {
        if (remaining != length)
        {
            throw new ImageDecodingException(ERROR_LENGTH + name(type) + ' ' + remaining);
        }
        read(buffer, 0, length);
    }

    /**
     * Skips the data left in the current chunk, then reads and checks its CRC.
     *
     * @throws IOException If reading fails.
     * @throws ImageDecodingException If the stream ends or the CRC does not match.
     */
    void end() throws IOException
    {
        while (remaining > 0)
        {
            read(skip, 0, skip.length);
        }
        final long expected = readInt() & CRC_MASK;
        if (expected != crc.getValue())
        {
            throw new ImageDecodingException(ERROR_CRC + name(type));
        }
    }

    /**
     * Reads a 32-bit big-endian integer outside the chunk data.
     *
     * @return The integer.
     * @throws IOException If reading fails.
     * @throws ImageDecodingException If the stream ends.
     */
    private int readInt() throws IOException
    {
        readField(INT_BYTES);
        return toInt(field, 0);
    }

    /**
     * Reads bytes outside the chunk data into {@link #field}.
     *
     * @param length The number of bytes.
     * @throws IOException If reading fails.
     * @throws ImageDecodingException If the stream ends.
     */
    private void readField(int length) throws IOException
    {
        if (!fill(field, length))
        {
            throw new ImageDecodingException(ERROR_TRUNCATED);
        }
    }

    /**
     * Reads bytes until the buffer is filled or the stream ends.
     *
     * @param buffer The destination.
     * @param length The number of bytes.
     * @return {@code true} if filled, {@code false} if the stream ended before.
     * @throws IOException If reading fails.
     */
    private boolean fill(byte[] buffer, int length) throws IOException
    {
        int done = 0;
        int read = 0;
        while (done < length && read >= 0)
        {
            read = input.read(buffer, done, length - done);
            done += Math.max(read, 0);
        }
        return done == length;
    }

    /**
     * Reads a big-endian long from the first eight bytes of a buffer.
     *
     * @param buffer The buffer.
     * @return The long.
     */
    private static long toLong(byte[] buffer)
    {
        return ((long) toInt(buffer, 0) << Integer.SIZE) | (toInt(buffer, INT_BYTES) & CRC_MASK);
    }

    /**
     * Reads a big-endian integer from four bytes of a buffer.
     *
     * @param buffer The buffer.
     * @param offset The index of the first byte.
     * @return The integer.
     */
    private static int toInt(byte[] buffer, int offset)
    {
        int value = 0;
        for (int i = 0; i < INT_BYTES; i++)
        {
            value = (value << BYTE_BITS) | (buffer[offset + i] & BYTE_MASK);
        }
        return value;
    }
}
