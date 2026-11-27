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

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.audio.AudioDecodingException;

/**
 * Tests of {@link VorbisHeaders} on headers written bit by bit: the bounds that keep the memory of JOrbis bounded
 * (ADR-0054, ADR-0073).
 */
final class VorbisHeadersTest
{
    /** Long block size of the tests. */
    private static final int LONG_BLOCK = 2_048;

    /**
     * Writer of the bits of a packet, least significant bit first.
     */
    private static final class Bits
    {
        /** The bytes written. */
        private final ByteArrayOutputStream output = new ByteArrayOutputStream();
        /** The byte being written. */
        private int current;
        /** The bits of the byte being written. */
        private int count;

        /**
         * Writes a value.
         *
         * @param bits The number of bits.
         * @param value The value.
         * @return This writer.
         */
        Bits write(int bits, long value)
        {
            for (int i = 0; i < bits; i++)
            {
                current |= (int) (value >>> i & 1L) << count;
                count++;
                if (count == Byte.SIZE)
                {
                    output.write(current);
                    current = 0;
                    count = 0;
                }
            }
            return this;
        }

        /**
         * Writes the type and the signature of a header.
         *
         * @param type The type.
         * @return This writer.
         */
        Bits header(int type)
        {
            write(Byte.SIZE, type);
            for (final char letter : "vorbis".toCharArray())
            {
                write(Byte.SIZE, letter);
            }
            return this;
        }

        /**
         * Returns the packet.
         *
         * @return The bytes, the last one padded with zeros.
         */
        byte[] toBytes()
        {
            if (count > 0)
            {
                output.write(current);
            }
            return output.toByteArray();
        }
    }

    /**
     * Writes a setup header: one codebook of 4 entries of 2 dimensions with sparse lengths and a lookup of type 1, a
     * floor 0 and a floor 1, a residue of type 2; a field is replaced when its name is in the overrides.
     *
     * @param overrides The values replacing the defaults, by field name.
     * @return The packet.
     */
    private static byte[] setup(Map<String, Long> overrides)
    {
        final Map<String, Long> fields = new HashMap<>(overrides);
        final Bits bits = new Bits().header(5);
        bits.write(8, fields.getOrDefault("books", 0L));
        bits.write(24, fields.getOrDefault("sync", 0x56_4342L));
        bits.write(16, fields.getOrDefault("dimensions", 2L));
        final long entries = fields.getOrDefault("entries", 4L);
        bits.write(24, entries);
        bits.write(1, 0L).write(1, 1L);
        for (int i = 0; i < 4; i++)
        {
            bits.write(1, 1L).write(5, 1L);
        }
        bits.write(4, fields.getOrDefault("lookup", 1L)).write(32, 0L).write(32, 0L).write(4, 3L).write(1, 0L);
        bits.write(4, 1L).write(4, 2L);
        bits.write(6, 0L).write(16, fields.getOrDefault("time", 0L));
        bits.write(6, 1L);
        bits.write(16, fields.getOrDefault("floor", 0L));
        bits.write(8, fields.getOrDefault("order", 1L)).write(16, 8_000L).write(16, 256L).write(6, 0L).write(8, 0L);
        bits.write(4, 0L).write(8, fields.getOrDefault("floorBook", 0L));
        bits.write(16, 1L).write(5, 1L).write(4, 0L);
        bits.write(3, fields.getOrDefault("classDimensions", 1L)).write(2, 1L).write(8, 0L).write(8, 0L).write(8, 1L);
        bits.write(2, 1L).write(4, 4L).write(4, 1L).write(4, 2L);
        bits.write(6, 0L).write(16, fields.getOrDefault("residue", 2L)).write(24, 0L);
        bits.write(24, fields.getOrDefault("end", 1_024L)).write(24, 31L);
        bits.write(6, fields.getOrDefault("classifications", 1L)).write(8, fields.getOrDefault("classBook", 0L));
        bits.write(3, 1L).write(1, 0L).write(3, 1L).write(1, 1L).write(5, 0L);
        bits.write(8, 0L).write(8, fields.getOrDefault("stageBook", 0L));
        return bits.toBytes();
    }

    /**
     * Writes a setup header with one field replaced.
     *
     * @param name The field name.
     * @param value The value.
     * @return The packet.
     */
    private static byte[] setup(String name, long value)
    {
        return setup(Map.of(name, Long.valueOf(value)));
    }

    /**
     * Checks a setup header of stereo.
     *
     * @param packet The packet.
     */
    private static void check(byte[] packet)
    {
        VorbisHeaders.checkSetup(packet, 0, packet.length, LONG_BLOCK, 2);
    }

    /**
     * Checks that a setup header is refused with a message containing a text.
     *
     * @param packet The packet.
     * @param text The text.
     */
    private static void assertRefused(byte[] packet, String text)
    {
        final AudioDecodingException exception = assertThrows(AudioDecodingException.class, () -> check(packet));
        assertTrue(exception.getMessage().contains(text), exception::getMessage);
    }

    /**
     * The identification header gives its long block; block sizes outside 64 to 8,192, or a short block longer than
     * the long one, are refused; another packet is left to JOrbis.
     */
    @Test
    void testIdentification()
    {
        for (final long[] blocks : new long[][]
        {
            {
                8, 11
            },
            {
                5, 11
            },
            {
                8, 14
            },
            {
                11, 8
            }
        })
        {
            final byte[] packet = new Bits().header(1)
                                            .write(32, 0L)
                                            .write(8, 2L)
                                            .write(32, 44_100L)
                                            .write(96, 0L)
                                            .write(4, blocks[0])
                                            .write(4, blocks[1])
                                            .write(1, 1L)
                                            .toBytes();
            if (blocks[0] == 8 && blocks[1] == 11)
            {
                assertEquals(LONG_BLOCK, VorbisHeaders.checkIdentification(packet, 0, packet.length));
            }
            else
            {
                assertThrows(AudioDecodingException.class,
                             () -> VorbisHeaders.checkIdentification(packet, 0, packet.length));
            }
        }
        final byte[] other = new Bits().header(3).toBytes();
        assertEquals(0, VorbisHeaders.checkIdentification(other, 0, other.length));
        assertEquals(0, VorbisHeaders.checkIdentification(other, 0, 3));
    }

    /**
     * A comment header whose vendor, comment count or comment length goes past the packet is refused.
     */
    @Test
    void testComment()
    {
        final byte[] valid = new Bits().header(3)
                                       .write(32, 2L)
                                       .write(16, 0L)
                                       .write(32, 1L)
                                       .write(32, 1L)
                                       .write(8, 0L)
                                       .write(1, 1L)
                                       .toBytes();
        assertDoesNotThrow(() -> VorbisHeaders.checkComment(valid, 0, valid.length));

        for (final Bits bits : new Bits[]
        {
            new Bits().header(3).write(32, 0x7FFF_FFFFL), new Bits().header(3).write(32, 0L).write(32, 0xFFFF_FFFFL),
            new Bits().header(3).write(32, 0L).write(32, 1L).write(32, 1_000L)
        })
        {
            final byte[] packet = bits.toBytes();
            assertThrows(AudioDecodingException.class, () -> VorbisHeaders.checkComment(packet, 0, packet.length));
        }
        final byte[] other = new Bits().header(1).toBytes();
        assertDoesNotThrow(() -> VorbisHeaders.checkComment(other, 0, other.length));
    }

    /**
     * A valid setup header is accepted; a packet of another type is left to JOrbis.
     */
    @Test
    void testValidSetup()
    {
        assertDoesNotThrow(() -> check(setup(Map.of())));
        final byte[] other = new Bits().header(3).toBytes();
        assertDoesNotThrow(() -> check(other));
    }

    /**
     * Codebooks without their pattern, of no entry or dimension, needing more than 24 bits, with an unknown lookup, or
     * whose values go past the packet are refused.
     */
    @Test
    void testCodebooks()
    {
        assertRefused(setup("sync", 0x12_3456L), "pattern");
        assertRefused(setup("dimensions", 0L), "dimensions");
        assertRefused(setup("entries", 0L), "entries");
        assertRefused(setup(Map.of("entries", Long.valueOf(1L << 20), "dimensions", Long.valueOf(32L))), "dimensions");
        assertRefused(setup("lookup", 3L), "lookup type");
        assertRefused(setup("lookup", 2L), "OGG Vorbis");
        assertRefused(setup("books", 1L), "codebook 1");
    }

    /**
     * Codebooks above the budget of entries or values are refused; ordered lengths past the entries are refused.
     */
    @Test
    void testCodebookBudget()
    {
        final Bits bits = new Bits().header(5).write(8, 1L);
        for (int book = 0; book < 2; book++)
        {
            bits.write(24, 0x56_4342L).write(16, 1L).write(24, VorbisHeaders.MAX_ENTRIES);
            bits.write(1, 1L).write(5, 0L).write(21, VorbisHeaders.MAX_ENTRIES).write(4, 0L);
        }
        assertRefused(bits.toBytes(), "limit " + VorbisHeaders.MAX_ENTRIES);

        final Bits values = new Bits().header(5).write(8, 0L);
        values.write(24, 0x56_4342L).write(16, 16L).write(24, VorbisHeaders.MAX_VALUES / 8);
        values.write(1, 1L).write(5, 0L).write(18, VorbisHeaders.MAX_VALUES / 8).write(4, 0L);
        assertRefused(values.toBytes(), "values");

        final Bits ordered = new Bits().header(5).write(8, 0L);
        ordered.write(24, 0x56_4342L).write(16, 1L).write(24, 4L).write(1, 1L).write(5, 0L).write(3, 5L);
        assertRefused(ordered.toBytes(), "lengths");

        final Bits deep = new Bits().header(5).write(8, 0L);
        deep.write(24, 0x56_4342L).write(16, 1L).write(24, 100L).write(1, 1L).write(5, 31L).write(7, 0L).write(7, 0L);
        assertRefused(deep.toBytes(), "lengths");
    }

    /**
     * Time transforms not zero, unknown floor and residue types, floor 0 parameters at zero, floors and residues naming
     * a missing codebook, floors of too many points, residues past their vector and classifications that do not fit
     * their codebook are refused. A residue of type 2 spans the vectors of every channel.
     */
    @Test
    void testFloorsAndResidues()
    {
        assertRefused(setup("time", 1L), "time");
        assertRefused(setup("floor", 2L), "floor type");
        assertRefused(setup("order", 0L), "floor 0");
        assertRefused(setup("floorBook", 1L), "missing codebook");
        assertRefused(setup("classDimensions", 7L), "OGG Vorbis");
        assertRefused(setup("residue", 3L), "residue 3");
        assertRefused(setup("end", 2_049L), "vector of 2048");
        assertRefused(setup("classifications", 2L), "classifications");
        assertRefused(setup("classBook", 2L), "missing codebook");
        assertRefused(setup("stageBook", 1L), "missing codebook");
        final byte[] both = setup("end", 2_048L);
        assertDoesNotThrow(() -> check(both));
        assertThrows(AudioDecodingException.class, () -> VorbisHeaders.checkSetup(both, 0, both.length, LONG_BLOCK, 1));
    }
}
