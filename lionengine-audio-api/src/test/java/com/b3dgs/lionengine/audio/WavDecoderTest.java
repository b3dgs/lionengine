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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test {@link WavDecoder}: depths, layouts, chunks and refusals (ADR-0027, ADR-0066, REQ-AUDIO-002, REQ-AUDIO-003).
 */
final class WavDecoderTest
{
    /** Mono format at 8 kHz. */
    private static final PcmFormat MONO = new PcmFormat(8_000, PcmFormat.MONO);
    /** Samples of the tests, covering the extremes. */
    private static final short[] SAMPLES =
    {
        0, 1, -1, 0x1234, -0x1234, Short.MAX_VALUE, Short.MIN_VALUE, 255, -256
    };

    /**
     * Returns all the samples of a buffer.
     *
     * @param buffer The buffer.
     * @return The interleaved samples.
     */
    private static short[] samples(PcmBuffer buffer)
    {
        final short[] samples = new short[buffer.getFrames() * buffer.getFormat().channels()];
        buffer.readFrames(0, samples, 0, buffer.getFrames());
        return samples;
    }

    /**
     * Decodes bytes with the default decoder.
     *
     * @param bytes The bytes.
     * @return The buffer.
     * @throws IOException If reading fails.
     */
    private static PcmBuffer decode(byte[] bytes) throws IOException
    {
        return new WavDecoder().decode(new ByteArrayInputStream(bytes));
    }

    /**
     * Returns the sound of the tests.
     *
     * @return A new buffer of {@link #SAMPLES}.
     */
    private static PcmBuffer sound()
    {
        return new PcmBuffer(MONO, SAMPLES.clone());
    }

    /**
     * Checks that decoding bytes is refused.
     *
     * @param bytes The bytes.
     * @return The message of the refusal.
     */
    private static String refused(byte[] bytes)
    {
        final WavDecoder decoder = new WavDecoder();
        final InputStream input = new ByteArrayInputStream(bytes);
        return assertThrows(AudioDecodingException.class, () -> decoder.decode(input)).getMessage();
    }

    /**
     * 16, 24 and 32-bit samples keep their 16 most significant bits; 8-bit samples keep their high byte.
     *
     * @param bits The depth in the file.
     * @throws IOException If reading fails.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        8, 16, 24, 32
    })
    void testDepths(int bits) throws IOException
    {
        final short[] expected = SAMPLES.clone();
        if (bits == 8)
        {
            for (int i = 0; i < expected.length; i++)
            {
                expected[i] = (short) (expected[i] & 0xFF00);
            }
        }

        final PcmBuffer buffer = decode(WavWriter.write(sound(), bits));

        assertEquals(MONO, buffer.getFormat());
        assertArrayEquals(expected, samples(buffer));
    }

    /**
     * A stereo file keeps its frames interleaved, left then right.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testStereo() throws IOException
    {
        final PcmFormat stereo = new PcmFormat(44_100, PcmFormat.STEREO);
        final PcmBuffer sound = new PcmBuffer(stereo, new short[]
        {
            1, -1, 2, -2
        });

        final PcmBuffer buffer = decode(WavWriter.write(sound, 24));

        assertEquals(stereo, buffer.getFormat());
        assertEquals(2, buffer.getFrames());
        assertArrayEquals(new short[]
        {
            1, -1, 2, -2
        }, samples(buffer));
    }

    /**
     * Other chunks are skipped, odd-sized ones with their pad byte, and bytes after the data are not read.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testOtherChunks() throws IOException
    {
        final byte[] odd = new WavWriter(sound(), 16).setExtraChunk("LIST", 5_001).setTrailingBytes(3).toBytes();
        final byte[] even = new WavWriter(sound(), 16).setExtraChunk("fact", 4).toBytes();

        assertArrayEquals(SAMPLES, samples(decode(odd)));
        assertArrayEquals(SAMPLES, samples(decode(even)));
    }

    /**
     * The extensible format is accepted with the PCM sub-format, refused with another one.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testExtensible() throws IOException
    {
        final byte[] pcm = new WavWriter(sound(), 16).setTag(WavWriter.TAG_EXTENSIBLE, WavWriter.TAG_PCM).toBytes();
        final byte[] floating = new WavWriter(sound(), 32).setTag(WavWriter.TAG_EXTENSIBLE, WavWriter.TAG_FLOAT)
                                                           .toBytes();
        final byte[] wrongGuid = pcm.clone();
        wrongGuid[20 + 26] ^= 1;

        assertArrayEquals(SAMPLES, samples(decode(pcm)));
        assertTrue(refused(floating).contains("sub-format 3"));
        refused(wrongGuid);
    }

    /**
     * Other encodings, more than two channels, other depths and inconsistent frame sizes are refused.
     */
    @Test
    void testUnsupported()
    {
        final byte[] floating = new WavWriter(sound(), 32).setTag(WavWriter.TAG_FLOAT, 0).toBytes();
        final byte[] channels = WavWriter.write(sound(), 16);
        channels[22] = 3;
        final byte[] depth = WavWriter.write(sound(), 16);
        depth[34] = 12;
        final byte[] align = WavWriter.write(sound(), 16);
        align[32] = 4;
        final byte[] rate = WavWriter.write(sound(), 16);
        Arrays.fill(rate, 24, 28, (byte) 0);
        final byte[] noChannel = WavWriter.write(sound(), 16);
        noChannel[22] = 0;

        assertTrue(refused(floating).contains("format tag: 3"));
        assertTrue(refused(channels).contains("3 channels"));
        assertTrue(refused(depth).contains("12"));
        assertTrue(refused(align).contains("frame size"));
        assertTrue(refused(rate).contains("0 Hz"));
        refused(noChannel);
    }

    /**
     * A missing or reordered format chunk, a too small one, a wrong header and a partial frame are refused.
     */
    @Test
    void testInvalidStructure()
    {
        final byte[] dataFirst = new WavWriter(sound(), 16).setDataFirst().toBytes();
        final byte[] small = WavWriter.write(sound(), 16);
        small[16] = 14;
        final byte[] small40 = new WavWriter(sound(), 16).setTag(WavWriter.TAG_EXTENSIBLE, WavWriter.TAG_PCM)
                                                         .toBytes();
        small40[16] = 18;
        final byte[] notWave = WavWriter.write(sound(), 16);
        notWave[8] = 'X';
        final byte[] partial = WavWriter.write(sound(), 16);
        partial[40] = 3;

        assertTrue(refused(dataFirst).contains("before its format"));
        assertTrue(refused(small).contains("too small"));
        assertTrue(refused(small40).contains("too small"));
        assertTrue(refused(notWave).contains("WAVE"));
        assertTrue(refused(partial).contains("multiple"));
    }

    /**
     * The frame limit is checked from the header, before the samples are read; it must be positive.
     */
    @Test
    void testLimit()
    {
        final byte[] header = Arrays.copyOf(WavWriter.write(sound(), 16), 44);
        header[40] = 0;
        header[41] = 0;
        header[42] = 0x10;
        final WavDecoder decoder = new WavDecoder(SAMPLES.length - 1);
        final InputStream input = new ByteArrayInputStream(WavWriter.write(sound(), 16));
        final WavDecoder small = new WavDecoder(0x10_0000 / 2 - 1);
        final InputStream huge = new ByteArrayInputStream(header);

        assertTrue(assertThrows(AudioDecodingException.class, () -> decoder.decode(input)).getMessage()
                                                                                        .contains("limit"));
        assertTrue(assertThrows(AudioDecodingException.class, () -> small.decode(huge)).getMessage()
                                                                                       .contains("limit"));
        assertEquals(WavDecoder.DEFAULT_MAX_FRAMES, new WavDecoder().getMaxFrames());
        assertThrows(IllegalArgumentException.class, () -> new WavDecoder(0));
    }
}
