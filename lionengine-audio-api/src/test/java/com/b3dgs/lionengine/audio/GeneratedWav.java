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

import java.io.InputStream;

/**
 * Stream of a 16-bit stereo WAV file generated on the fly, of any length, so that a test can stream a long sound
 * without holding its bytes (ADR-0068).
 *
 * <p>
 * Each sample is the low 16 bits of its index in the file, so a frame {@code f} holds {@code (short) (2 f)} then
 * {@code (short) (2 f + 1)}. The stream can stop early, to make a truncated file. Reading allocates nothing.
 * </p>
 */
final class GeneratedWav extends InputStream
{
    /** Bytes of the canonical header. */
    private static final int HEADER_BYTES = 44;
    /** Bytes of a stereo 16-bit frame. */
    private static final int FRAME_BYTES = 4;
    /** Mask of a byte. */
    private static final int BYTE = 0xFF;

    /**
     * Returns the sample at an index of the data, as the generated file holds it.
     *
     * @param index The index of the sample from the first one.
     * @return The sample.
     */
    static short sample(long index)
    {
        return (short) index;
    }

    /** The header bytes. */
    private final byte[] header = new byte[HEADER_BYTES];
    /** The bytes the stream gives before its end. */
    private final long length;
    /** The bytes given. */
    private long position;
    /** {@code true} once closed. */
    private boolean closed;

    /**
     * Creates a complete file.
     *
     * @param rate The sample rate in Hz.
     * @param frames The frames of the data chunk.
     */
    GeneratedWav(int rate, long frames)
    {
        this(rate, frames, frames);
    }

    /**
     * Creates a file that may be truncated.
     *
     * @param rate The sample rate in Hz.
     * @param frames The frames declared by the data chunk.
     * @param present The frames actually given before the end of the stream.
     */
    GeneratedWav(int rate, long frames, long present)
    {
        super();

        final long data = frames * FRAME_BYTES;
        final int[] words =
        {
            0x4646_4952, (int) (36 + data), 0x4556_4157, 0x2074_6D66, 16, 0x0002_0001, rate, rate * FRAME_BYTES,
            0x0010_0004, 0x6174_6164, (int) data
        };
        for (int i = 0; i < words.length; i++)
        {
            for (int b = 0; b < Integer.BYTES; b++)
            {
                header[i * Integer.BYTES + b] = (byte) (words[i] >> b * Byte.SIZE);
            }
        }
        length = HEADER_BYTES + present * FRAME_BYTES;
    }

    /**
     * Tells whether the stream is closed.
     *
     * @return {@code true} once closed.
     */
    boolean isClosed()
    {
        return closed;
    }

    @Override
    public int read()
    {
        final int value;
        if (position >= length)
        {
            value = -1;
        }
        else
        {
            value = byteAt(position) & BYTE;
            position++;
        }
        return value;
    }

    @Override
    public int read(byte[] target, int offset, int count)
    {
        final int read;
        if (count == 0)
        {
            read = 0;
        }
        else if (position >= length)
        {
            read = -1;
        }
        else
        {
            read = (int) Math.min(count, length - position);
            for (int i = 0; i < read; i++)
            {
                target[offset + i] = byteAt(position + i);
            }
            position += read;
        }
        return read;
    }

    @Override
    public void close()
    {
        closed = true;
    }

    /**
     * Returns the byte at a position of the file.
     *
     * @param at The position.
     * @return The byte.
     */
    private byte byteAt(long at)
    {
        final byte value;
        if (at < HEADER_BYTES)
        {
            value = header[(int) at];
        }
        else
        {
            final long data = at - HEADER_BYTES;
            value = (byte) (sample(data / Short.BYTES) >> (data % Short.BYTES) * Byte.SIZE);
        }
        return value;
    }
}
