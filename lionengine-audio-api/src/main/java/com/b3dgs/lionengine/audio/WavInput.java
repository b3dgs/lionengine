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
package com.b3dgs.lionengine.audio;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Little-endian reader of the bytes of a WAV file, for {@link WavDecoder} (ADR-0066).
 *
 * <p>
 * Reads the stream it is given without closing it; the end of the stream before a value is an {@link EOFException},
 * which the decoder reports as truncated data. One reader per decoding, so it is confined to the decoding thread.
 * </p>
 */
final class WavInput
{
    /** Bytes of a chunk identifier. */
    static final int ID_BYTES = 4;
    /** Size of the scratch buffer used to skip bytes. */
    private static final int SKIP_BYTES = 4_096;
    /** Mask of an unsigned byte. */
    private static final int BYTE = 0xFF;
    /** Mask of an unsigned 32-bit value. */
    private static final long UNSIGNED_INT = 0xFFFF_FFFFL;

    /** The stream read. */
    private final InputStream input;
    /** Scratch bytes of the values. */
    private final byte[] bytes = new byte[ID_BYTES];
    /** Scratch bytes skipped, created at the first skip. */
    private byte[] skipped;

    /**
     * Creates the reader.
     *
     * @param input The stream, read but not closed.
     */
    WavInput(InputStream input)
    {
        super();

        this.input = input;
    }

    /**
     * Reads bytes until the array range is full.
     *
     * @param target The array receiving the bytes.
     * @param offset The first index written.
     * @param length The number of bytes to read.
     * @throws EOFException If the stream ends before.
     * @throws IOException If reading fails.
     */
    void readFully(byte[] target, int offset, int length) throws IOException
    {
        int done = 0;
        while (done < length)
        {
            final int read = input.read(target, offset + done, length - done);
            if (read < 0)
            {
                throw new EOFException("WAV data ended after " + done + " of " + length + " bytes");
            }
            done += read;
        }
    }

    /**
     * Reads a chunk identifier: four ASCII bytes.
     *
     * @return The identifier.
     * @throws EOFException If the stream ends before.
     * @throws IOException If reading fails.
     */
    String readId() throws IOException
    {
        readFully(bytes, 0, ID_BYTES);
        final char[] id = new char[ID_BYTES];
        for (int i = 0; i < ID_BYTES; i++)
        {
            id[i] = (char) (bytes[i] & BYTE);
        }
        return new String(id);
    }

    /**
     * Reads an unsigned little-endian 16-bit value.
     *
     * @return The value, from {@code 0} to {@code 65535}.
     * @throws EOFException If the stream ends before.
     * @throws IOException If reading fails.
     */
    int readUnsignedShort() throws IOException
    {
        readFully(bytes, 0, Short.BYTES);
        return bytes[0] & BYTE | (bytes[1] & BYTE) << Byte.SIZE;
    }

    /**
     * Reads an unsigned little-endian 32-bit value.
     *
     * @return The value, from {@code 0} to {@code 2^32 - 1}.
     * @throws EOFException If the stream ends before.
     * @throws IOException If reading fails.
     */
    long readUnsignedInt() throws IOException
    {
        readFully(bytes, 0, Integer.BYTES);
        int value = 0;
        for (int i = Integer.BYTES - 1; i >= 0; i--)
        {
            value = value << Byte.SIZE | bytes[i] & BYTE;
        }
        return value & UNSIGNED_INT;
    }

    /**
     * Skips bytes by reading them.
     *
     * @param count The number of bytes to skip (not negative).
     * @throws EOFException If the stream ends before.
     * @throws IOException If reading fails.
     */
    void skip(long count) throws IOException
    {
        if (skipped == null)
        {
            skipped = new byte[SKIP_BYTES];
        }
        long left = count;
        while (left > 0L)
        {
            final int length = (int) Math.min(left, SKIP_BYTES);
            readFully(skipped, 0, length);
            left -= length;
        }
    }
}
