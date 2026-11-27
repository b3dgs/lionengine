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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.audio.AudioDecodingException;
import com.b3dgs.lionengine.audio.PcmBuffer;
import com.b3dgs.lionengine.audio.PcmFormat;
import com.b3dgs.lionengine.audio.PcmSource;

/**
 * Tests of {@link OggVorbisDecoder}: formats and accuracy, frame limit, chained, multiplexed, non-Vorbis, truncated
 * and corrupted streams (ADR-0073, REQ-AUDIO-002).
 */
final class OggVorbisDecoderTest
{
    /** Serial number given to a second link. */
    private static final int OTHER_SERIAL = 0x1234_5678;
    /** Runs of the corruption test. */
    private static final int CORRUPTIONS = 200;
    /** Bytes changed per run of the corruption test. */
    private static final int CORRUPTED_BYTES = 4;

    /**
     * Decodes a sound entirely with the default decoder.
     *
     * @param sound The bytes of the sound.
     * @return The samples.
     * @throws IOException If reading fails.
     */
    private static PcmBuffer decode(byte[] sound) throws IOException
    {
        return new OggVorbisDecoder().decode(new ByteArrayInputStream(sound));
    }

    /**
     * Checks that a sound is refused, entirely and by streaming, with a message containing a text.
     *
     * @param sound The bytes of the sound.
     * @param text The text of the message.
     */
    private static void assertRefused(byte[] sound, String text)
    {
        final OggVorbisDecoder decoder = new OggVorbisDecoder();
        final ByteArrayInputStream input = new ByteArrayInputStream(sound);
        final AudioDecodingException decoding = assertThrows(AudioDecodingException.class,
                                                             () -> decoder.decode(input));
        assertTrue(decoding.getMessage().contains(text), decoding::getMessage);
        final AudioDecodingException streaming = assertThrows(AudioDecodingException.class,
                                                              () -> OggVorbisDecoderContractTest.stream(sound));
        assertTrue(streaming.getMessage().contains(text), streaming::getMessage);
    }

    /**
     * Returns a sound chained after itself, its second link with another serial number.
     *
     * @param sound The sound.
     * @return The chained stream.
     */
    private static byte[] twice(byte[] sound)
    {
        return OggPages.concat(sound, OggPages.withSerial(sound, OTHER_SERIAL));
    }

    /**
     * Mono at 11,025, 22,050 and 48,000 Hz and stereo at 44,100 Hz decode to the format and the length of the file,
     * the last frames trimmed to the granule position of the last page, and to the samples of {@code libvorbisfile}
     * within one 16-bit step; stereo is interleaved by frame.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testFormatsMatchLibvorbisfile() throws IOException
    {
        for (final String name : List.of(OggFiles.MONO_11025,
                                         OggFiles.MONO_22050,
                                         OggFiles.MONO_48000,
                                         OggFiles.STEREO_44100))
        {
            final PcmBuffer expected = OggFiles.reference(name);
            final PcmBuffer buffer = decode(OggFiles.ogg(name));

            assertEquals(expected.getFormat(), buffer.getFormat(), name);
            assertEquals(expected.getFrames(), buffer.getFrames(), name);
            final int difference = OggFiles.maxDifference(OggFiles.samples(expected), OggFiles.samples(buffer));
            assertTrue(difference <= OggFiles.TOLERANCE, () -> name + ": " + difference);
        }
        assertEquals(new PcmFormat(44_100, PcmFormat.STEREO), decode(OggFiles.ogg(OggFiles.STEREO_44100)).getFormat());
        assertEquals(0.25, decode(OggFiles.ogg(OggFiles.MONO_48000)).getDuration(), 1E-9);
    }

    /**
     * The default limit is the one of the WAV decoder; a limit not positive is refused.
     */
    @Test
    void testLimitArguments()
    {
        assertEquals(OggVorbisDecoder.DEFAULT_MAX_FRAMES, new OggVorbisDecoder().getMaxFrames());
        assertEquals(1_920_000, OggVorbisDecoder.DEFAULT_MAX_FRAMES);
        assertEquals(7, new OggVorbisDecoder(7).getMaxFrames());
        assertThrows(IllegalArgumentException.class, () -> new OggVorbisDecoder(0));
        assertThrows(IllegalArgumentException.class, () -> new OggVorbisDecoder(-1));
    }

    /**
     * A sound longer than a block of {@link OggVorbisDecoder#BLOCK_FRAMES} frames is assembled from its blocks; it
     * decodes with a limit of exactly its length, and is refused one frame below, or at the end of the first block.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testFrameLimitAcrossBlocks() throws IOException
    {
        final byte[] sound = twice(OggFiles.ogg(OggFiles.MONO_22050));
        final short[] link = OggFiles.samples(decode(OggFiles.ogg(OggFiles.MONO_22050)));
        final short[] expected = Arrays.copyOf(link, link.length * 2);
        System.arraycopy(link, 0, expected, link.length, link.length);

        final PcmBuffer buffer = new OggVorbisDecoder(expected.length).decode(new ByteArrayInputStream(sound));
        assertTrue(buffer.getFrames() > OggVorbisDecoder.BLOCK_FRAMES);
        assertArrayEquals(expected, OggFiles.samples(buffer));

        for (final int limit : new int[]
        {
            expected.length - 1, OggVorbisDecoder.BLOCK_FRAMES, 1
        })
        {
            final OggVorbisDecoder decoder = new OggVorbisDecoder(limit);
            final ByteArrayInputStream input = new ByteArrayInputStream(sound);
            final AudioDecodingException exception = assertThrows(AudioDecodingException.class,
                                                                  () -> decoder.decode(input));
            assertTrue(exception.getMessage().contains("limit " + limit), exception::getMessage);
        }
    }

    /**
     * Chained links of the format of the first one decode as one sound, entirely and by streaming.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testChainedSameFormat() throws IOException
    {
        final byte[] stereo = OggFiles.ogg(OggFiles.STEREO_44100);
        final short[] link = OggFiles.samples(decode(stereo));
        final byte[] chained = OggPages.concat(stereo,
                                               OggPages.withSerial(stereo, OTHER_SERIAL),
                                               OggPages.withSerial(stereo, OTHER_SERIAL + 1));

        final short[] samples = OggFiles.samples(decode(chained));

        assertEquals(link.length * 3, samples.length);
        for (int i = 0; i < 3; i++)
        {
            assertArrayEquals(link, Arrays.copyOfRange(samples, i * link.length, (i + 1) * link.length));
        }
        assertArrayEquals(samples, OggFiles.samples(OggVorbisDecoderContractTest.stream(chained)));
    }

    /**
     * A chained link of another format is refused when it is reached: a source gives the frames of the first link
     * before refusing it.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testChainedOtherFormat() throws IOException
    {
        final byte[] chained = OggPages.concat(OggFiles.ogg(OggFiles.MONO_22050),
                                               OggPages.withSerial(OggFiles.ogg(OggFiles.MONO_11025), OTHER_SERIAL));
        assertRefused(chained, "chained link");

        final PcmSource source = new OggVorbisDecoder().open(new ByteArrayInputStream(chained));
        final short[] target = new short[OggFiles.reference(OggFiles.MONO_22050).getFrames()];
        assertEquals(target.length, source.read(target, 0, target.length));
        assertThrows(AudioDecodingException.class, () -> source.read(target, 0, 1));
        assertEquals(0, source.read(target, 0, 1));
    }

    /**
     * More than two channels are refused at the opening.
     */
    @Test
    void testThreeChannels()
    {
        assertRefused(OggFiles.ogg(OggFiles.THREE_CHANNELS), "3 channels");
    }

    /**
     * A first logical stream that is not Vorbis is refused: an identification header without the Vorbis signature,
     * and an Opus stream.
     */
    @Test
    void testNotVorbis()
    {
        final List<byte[]> pages = OggPages.split(OggFiles.ogg(OggFiles.MONO_22050));
        final byte[] first = pages.get(0);
        first[OggPages.body(first) + 1] = 'V';
        OggPages.checksum(first);
        assertRefused(OggPages.join(pages), "not a Vorbis stream");

        final byte[] opus = Arrays.copyOf("OpusHead".getBytes(StandardCharsets.US_ASCII), 19);
        opus[8] = 1;
        opus[9] = 1;
        assertRefused(OggPages.page(OggPages.BOS | OggPages.EOS, 0L, 1, 0, opus), "not a Vorbis stream");
    }

    /**
     * A setup header without its signature is refused as corrupted.
     */
    @Test
    void testInvalidSetupHeader()
    {
        final List<byte[]> pages = OggPages.split(OggFiles.ogg(OggFiles.MONO_22050));
        final byte[] headers = pages.get(1);
        int comment = 0;
        int lacing = OggPages.body(headers) - (headers[26] & 0xFF);
        do
        {
            comment += headers[lacing] & 0xFF;
        }
        while ((headers[lacing++] & 0xFF) == 255);
        headers[OggPages.body(headers) + comment + 1] = 'V';
        OggPages.checksum(headers);

        assertRefused(OggPages.join(pages), "invalid Vorbis header");
    }

    /**
     * Empty data, text shorter or longer than a page header, a page whose checksum fails, a missing page, and pages of another logical stream inside a link
     * are refused.
     */
    @Test
    void testCorrupted()
    {
        assertRefused(new byte[0], "no OGG page");
        assertRefused("Not a sound.".getBytes(StandardCharsets.UTF_8), "no OGG page");
        assertRefused("Not a sound, only text, longer than a page header.".getBytes(StandardCharsets.UTF_8),
                      "not an OGG page");

        final byte[] flipped = OggFiles.ogg(OggFiles.LONG_MONO_22050);
        flipped[flipped.length / 2] ^= 0x10;
        assertRefused(flipped, "checksum");

        final List<byte[]> missing = OggPages.split(OggFiles.ogg(OggFiles.LONG_MONO_22050));
        missing.remove(3);
        assertRefused(OggPages.join(missing), "missing data");

        final List<byte[]> multiplexed = OggPages.split(OggFiles.ogg(OggFiles.LONG_MONO_22050));
        multiplexed.add(3, OggPages.split(OggFiles.ogg(OggFiles.MONO_11025)).get(0));
        assertRefused(OggPages.join(multiplexed), "multiplexed");
    }

    /**
     * A logical stream that ends in its headers, a stream that ends before its last page or inside a page, and data after the last page that
     * does not start a new link, are refused.
     */
    @Test
    void testTruncatedAndTrailing()
    {
        final byte[] sound = OggFiles.ogg(OggFiles.LONG_MONO_22050);
        final List<byte[]> pages = OggPages.split(sound);

        final byte[] ended = pages.get(0).clone();
        ended[5] |= OggPages.EOS;
        OggPages.checksum(ended);
        assertRefused(ended, "ended in its headers");
        assertRefused(pages.get(0), "before its last page");
        assertRefused(OggPages.join(pages.subList(0, 2)), "before its last page");
        assertRefused(OggPages.join(pages.subList(0, 4)), "before its last page");
        assertRefused(Arrays.copyOf(sound, sound.length - 1), "before its last page");
        assertRefused(OggPages.concat(sound, Arrays.copyOf(pages.get(2), 30)), "last page incomplete");
        assertRefused(OggPages.concat(sound, "trailing text, longer than a page header".getBytes(StandardCharsets.UTF_8)), "not an OGG page");
        assertRefused(OggPages.concat(sound, pages.get(2)), "outside a logical stream");
    }

    /**
     * Random bytes of every page changed, with a valid checksum, decode or are refused with a decoding error: a failure
     * of JOrbis on corrupted data never escapes as another exception. The generator is seeded, so the runs are the same
     * at every build.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testCorruptedPackets() throws IOException
    {
        final Random random = new Random(42L);
        final byte[] sound = OggFiles.ogg(OggFiles.MONO_22050);
        for (int run = 0; run < CORRUPTIONS; run++)
        {
            final List<byte[]> pages = OggPages.split(sound);
            final byte[] page = pages.get(random.nextInt(pages.size()));
            final int body = OggPages.body(page);
            for (int i = 0; i < CORRUPTED_BYTES; i++)
            {
                page[body + random.nextInt(page.length - body)] = (byte) random.nextInt();
            }
            OggPages.checksum(page);
            try
            {
                decode(OggPages.join(pages));
            }
            catch (final AudioDecodingException exception)
            {
                assertTrue(exception.getMessage().startsWith("Corrupted")
                           || exception.getMessage().startsWith("Unsupported")
                           || exception.getMessage().startsWith("Truncated"),
                           exception::getMessage);
            }
        }
        assertEquals(11_025, decode(sound).getFrames());
    }
}
