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

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Splits, rewrites and builds OGG pages, with their checksum, to make chained, multiplexed, corrupted and non-Vorbis
 * streams from the test files (RFC 3533).
 */
final class OggPages
{
    /** Flag of a page continuing a packet. */
    static final int CONTINUED = 0x01;
    /** Flag of the first page of a logical stream. */
    static final int BOS = 0x02;
    /** Flag of the last page of a logical stream. */
    static final int EOS = 0x04;
    /** Size of the fixed header. */
    private static final int HEADER = 27;
    /** Offset of the flags. */
    private static final int FLAGS = 5;
    /** Offset of the granule position. */
    private static final int GRANULE = 6;
    /** Offset of the serial number. */
    private static final int SERIAL = 14;
    /** Offset of the page sequence number. */
    private static final int SEQUENCE = 18;
    /** Offset of the checksum. */
    private static final int CHECKSUM = 22;
    /** Offset of the segment count. */
    private static final int SEGMENTS = 26;
    /** Largest lacing value. */
    private static final int LACING = 255;
    /** Checksum polynomial of OGG. */
    private static final int POLYNOMIAL = 0x04C1_1DB7;
    /** Bits of a byte. */
    private static final int BYTE_BITS = 8;
    /** Mask of a byte. */
    private static final int BYTE = 0xFF;
    /** Shift of the top byte of an {@code int}. */
    private static final int TOP = 24;

    /**
     * Splits a stream into its pages.
     *
     * @param ogg The stream.
     * @return The pages, each a copy.
     */
    static List<byte[]> split(byte[] ogg)
    {
        final List<byte[]> pages = new ArrayList<>();
        int offset = 0;
        while (offset < ogg.length)
        {
            final int segments = ogg[offset + SEGMENTS] & BYTE;
            int size = HEADER + segments;
            for (int i = 0; i < segments; i++)
            {
                size += ogg[offset + HEADER + i] & BYTE;
            }
            pages.add(Arrays.copyOfRange(ogg, offset, offset + size));
            offset += size;
        }
        return pages;
    }

    /**
     * Joins pages into a stream.
     *
     * @param pages The pages.
     * @return The stream.
     */
    static byte[] join(List<byte[]> pages)
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (final byte[] page : pages)
        {
            output.writeBytes(page);
        }
        return output.toByteArray();
    }

    /**
     * Concatenates streams.
     *
     * @param streams The streams.
     * @return The bytes of the streams, one after the other.
     */
    static byte[] concat(byte[]... streams)
    {
        final ByteArrayOutputStream output = new ByteArrayOutputStream();
        for (final byte[] stream : streams)
        {
            output.writeBytes(stream);
        }
        return output.toByteArray();
    }

    /**
     * Gives every page of a stream another serial number.
     *
     * @param ogg The stream.
     * @param serial The serial number.
     * @return The new stream.
     */
    static byte[] withSerial(byte[] ogg, int serial)
    {
        final List<byte[]> pages = split(ogg);
        for (final byte[] page : pages)
        {
            ByteBuffer.wrap(page).order(ByteOrder.LITTLE_ENDIAN).putInt(SERIAL, serial);
            checksum(page);
        }
        return join(pages);
    }

    /**
     * Returns the offset of the body of a page.
     *
     * @param page The page.
     * @return The index of the first byte of its first packet.
     */
    static int body(byte[] page)
    {
        return HEADER + (page[SEGMENTS] & BYTE);
    }

    /**
     * Builds a page holding one packet.
     *
     * @param flags The flags ({@link #BOS}, {@link #EOS}).
     * @param granule The granule position.
     * @param serial The serial number.
     * @param sequence The page sequence number.
     * @param packet The packet, shorter than 255 × 255 bytes.
     * @return The page, with its checksum.
     */
    static byte[] page(int flags, long granule, int serial, int sequence, byte[] packet)
    {
        final int segments = packet.length / LACING + 1;
        final byte[] page = new byte[HEADER + segments + packet.length];
        final ByteBuffer buffer = ByteBuffer.wrap(page).order(ByteOrder.LITTLE_ENDIAN);
        buffer.put(0, (byte) 'O').put(1, (byte) 'g').put(2, (byte) 'g').put(3, (byte) 'S');
        buffer.put(FLAGS, (byte) flags).putLong(GRANULE, granule).putInt(SERIAL, serial).putInt(SEQUENCE, sequence);
        buffer.put(SEGMENTS, (byte) segments);
        for (int i = 0; i < segments - 1; i++)
        {
            page[HEADER + i] = (byte) LACING;
        }
        page[HEADER + segments - 1] = (byte) (packet.length % LACING);
        System.arraycopy(packet, 0, page, HEADER + segments, packet.length);
        checksum(page);
        return page;
    }

    /**
     * Computes the checksum of a page and writes it in its header.
     *
     * @param page The page.
     */
    static void checksum(byte[] page)
    {
        ByteBuffer.wrap(page).order(ByteOrder.LITTLE_ENDIAN).putInt(CHECKSUM, 0);
        int crc = 0;
        for (final byte value : page)
        {
            crc ^= (value & BYTE) << TOP;
            for (int bit = 0; bit < BYTE_BITS; bit++)
            {
                crc = crc < 0 ? crc << 1 ^ POLYNOMIAL : crc << 1;
            }
        }
        ByteBuffer.wrap(page).order(ByteOrder.LITTLE_ENDIAN).putInt(CHECKSUM, crc);
    }

    /**
     * Private constructor.
     */
    private OggPages()
    {
        throw new AssertionError();
    }
}
