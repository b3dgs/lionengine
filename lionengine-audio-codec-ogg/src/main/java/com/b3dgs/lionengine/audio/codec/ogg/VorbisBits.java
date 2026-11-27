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
package com.b3dgs.lionengine.audio.codec.ogg;

import com.b3dgs.lionengine.audio.AudioDecodingException;

/**
 * Reader of the bits of a Vorbis packet, least significant bit first, as the Vorbis I specification packs them
 * (ADR-0073).
 *
 * <p>
 * Reading past the end of the packet is refused with an {@link AudioDecodingException}: a header that declares more
 * data than it holds is corrupted. Not thread-safe.
 * </p>
 */
final class VorbisBits
{
    /** Bits of a byte. */
    private static final int BYTE_BITS = 8;
    /** Mask of a byte. */
    private static final int BYTE = 0xFF;
    /** Largest number of bits read at once. */
    private static final int MAX_BITS = 32;

    /** The bytes of the packet. */
    private final byte[] data;
    /** The index of the first byte of the packet. */
    private final int offset;
    /** The bits of the packet. */
    private final long length;
    /** The index of the next bit, from the first bit of the packet. */
    private long position;

    /**
     * Creates the reader.
     *
     * @param data The bytes holding the packet.
     * @param offset The index of its first byte.
     * @param bytes The bytes of the packet.
     */
    VorbisBits(byte[] data, int offset, int bytes)
    {
        super();

        this.data = data;
        this.offset = offset;
        length = (long) bytes * BYTE_BITS;
    }

    /**
     * Reads an unsigned value.
     *
     * @param bits The number of bits, from {@code 0} to {@code 32}.
     * @return The value.
     * @throws AudioDecodingException If the packet ends first.
     */
    long read(int bits)
    {
        if (bits < 0 || bits > MAX_BITS)
        {
            throw new IllegalArgumentException("Bits out of range: " + bits);
        }
        require(bits);
        long value = 0L;
        for (int i = 0; i < bits; i++)
        {
            final int bit = data[offset + (int) (position / BYTE_BITS)] >> (int) (position % BYTE_BITS) & 1;
            value |= (long) bit << i;
            position++;
        }
        return value;
    }

    /**
     * Skips bits.
     *
     * @param bits The number of bits (not negative).
     * @throws AudioDecodingException If the packet ends first.
     */
    void skip(long bits)
    {
        require(bits);
        position += bits;
    }

    /**
     * Skips whole bytes, from a byte boundary.
     *
     * @param bytes The number of bytes (not negative).
     * @throws AudioDecodingException If the packet ends first.
     */
    void skipBytes(long bytes)
    {
        skip(bytes * BYTE_BITS);
    }

    /**
     * Reads a byte, from a byte boundary.
     *
     * @return The byte, from {@code 0} to {@code 255}.
     * @throws AudioDecodingException If the packet ends first.
     */
    int readByte()
    {
        return (int) read(BYTE_BITS) & BYTE;
    }

    /**
     * Checks that bits remain.
     *
     * @param bits The number of bits needed.
     * @throws AudioDecodingException If the packet ends first.
     */
    private void require(long bits)
    {
        if (bits > length - position)
        {
            throw new AudioDecodingException(OggPackets.ERROR_CORRUPT + "Vorbis header shorter than its content");
        }
    }
}
