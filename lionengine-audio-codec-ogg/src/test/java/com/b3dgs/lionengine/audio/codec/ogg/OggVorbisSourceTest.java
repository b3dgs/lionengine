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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.audio.AudioDecodingException;
import com.b3dgs.lionengine.audio.PcmFormat;
import com.b3dgs.lionengine.audio.PcmSource;
import com.sun.management.ThreadMXBean;

/**
 * Tests of the streaming source of {@link OggVorbisDecoder}: reads in pieces, headers read at the opening, failures
 * while streaming, arguments, threads, and the allocation of the steady path (ADR-0068, ADR-0073, REQ-AUDIO-006).
 */
final class OggVorbisSourceTest
{
    /** Frames of a read of the backend, as the window of the Java Sound mixer. */
    private static final int PIECE = 1_024;
    /** Frames read before the allocation is measured: the first pages and packets size the buffers. */
    private static final int WARM_UP = 22_050;
    /** Measures of the allocation test, the lowest is kept. */
    private static final int MEASURES = 5;
    /** Threads of the concurrent test. */
    private static final int THREADS = 4;
    /** Timeout of the concurrent test. */
    private static final long TIMEOUT_SECONDS = 30L;

    /**
     * Opens a sound with the default decoder.
     *
     * @param sound The bytes of the sound.
     * @return The source.
     * @throws IOException If reading fails.
     */
    private static PcmSource open(byte[] sound) throws IOException
    {
        return new OggVorbisDecoder().open(new ByteArrayInputStream(sound));
    }

    /**
     * Reads every frame of a source in pieces.
     *
     * @param source The source.
     * @param piece The frames of a read.
     * @return The samples.
     * @throws IOException If reading fails.
     */
    private static short[] readAll(PcmSource source, int piece) throws IOException
    {
        final int channels = source.getFormat().channels();
        final short[] target = new short[piece * channels];
        short[] samples = new short[piece * channels];
        int length = 0;
        int read;
        do
        {
            read = source.read(target, 0, piece);
            if (length + read * channels > samples.length)
            {
                samples = Arrays.copyOf(samples, Math.max(samples.length * 2, length + read * channels));
            }
            System.arraycopy(target, 0, samples, length, read * channels);
            length += read * channels;
        }
        while (read == piece);
        assertEquals(0, source.read(target, 0, piece));
        return Arrays.copyOf(samples, length);
    }

    /**
     * Reading in pieces of any size gives the samples of the full decoding, mono and stereo, and {@code 0} once ended.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testPieces() throws IOException
    {
        for (final String name : List.of(OggFiles.LONG_MONO_22050, OggFiles.STEREO_44100))
        {
            final byte[] sound = OggFiles.ogg(name);
            final short[] expected = OggFiles.samples(new OggVorbisDecoder().decode(new ByteArrayInputStream(sound)));
            for (final int piece : new int[]
            {
                1, 7, 333, PIECE, 100_000
            })
            {
                assertArrayEquals(expected, readAll(open(sound), piece), name + " " + piece);
            }
        }
        assertEquals(110_250, readAll(open(OggFiles.ogg(OggFiles.LONG_MONO_22050)), PIECE).length);
    }

    /**
     * The opening reads the headers and no sample: a stream of the header pages alone opens with its format, and its
     * first read is refused as truncated, then the source has ended.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testHeadersBeforeSamples() throws IOException
    {
        final List<byte[]> pages = OggPages.split(OggFiles.ogg(OggFiles.STEREO_44100));
        final PcmSource source = open(OggPages.join(pages.subList(0, 2)));
        final short[] target = new short[PIECE * PcmFormat.STEREO];

        assertEquals(new PcmFormat(44_100, PcmFormat.STEREO), source.getFormat());
        final AudioDecodingException exception = assertThrows(AudioDecodingException.class,
                                                              () -> source.read(target, 0, PIECE));
        assertTrue(exception.getMessage().startsWith("Truncated"), exception::getMessage);
        assertEquals(0, source.read(target, 0, PIECE));
    }

    /**
     * A stream cut inside its pages gives the frames decoded before the cut, then is refused as truncated, then has
     * ended.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testTruncatedWhileStreaming() throws IOException
    {
        final byte[] sound = OggFiles.ogg(OggFiles.LONG_MONO_22050);
        final PcmSource source = open(Arrays.copyOf(sound, sound.length * 2 / 3));
        final short[] target = new short[PIECE];
        final long[] frames = new long[1];
        final AudioDecodingException exception = assertThrows(AudioDecodingException.class, () ->
        {
            while (true)
            {
                frames[0] += source.read(target, 0, PIECE);
            }
        });

        assertTrue(exception.getMessage().startsWith("Truncated"), exception::getMessage);
        assertTrue(frames[0] > WARM_UP, () -> "Frames: " + frames[0]);
        assertTrue(frames[0] < 110_250L, () -> "Frames: " + frames[0]);
        assertEquals(0, source.read(target, 0, PIECE));
    }

    /**
     * A failure of the stream while streaming is an {@link IOException}, then the source has ended; the stream is
     * never closed by the source.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testReadFailureAndStreamLeftOpen() throws IOException
    {
        final byte[] sound = OggFiles.ogg(OggFiles.LONG_MONO_22050);
        final boolean[] closed = new boolean[1];
        final int[] position = new int[1];
        final InputStream input = new InputStream()
        {
            @Override
            public int read() throws IOException
            {
                if (position[0] >= sound.length / 2)
                {
                    throw new IOException("Read failure");
                }
                return sound[position[0]++] & 0xFF;
            }

            @Override
            public void close()
            {
                closed[0] = true;
            }
        };
        final PcmSource source = new OggVorbisDecoder().open(input);
        final short[] target = new short[PIECE];

        assertThrows(IOException.class, () -> readAll(source, PIECE));
        assertEquals(0, source.read(target, 0, PIECE));
        assertFalse(closed[0]);
    }

    /**
     * A read with a {@code null} array, a negative offset or count, or a range past the array is refused; a read of no
     * frame returns {@code 0}; a {@code null} stream is refused at the opening.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testArguments() throws IOException
    {
        final PcmSource source = open(OggFiles.ogg(OggFiles.STEREO_44100));
        final short[] target = new short[2 * PcmFormat.STEREO];
        final OggVorbisDecoder decoder = new OggVorbisDecoder();

        assertThrows(IllegalArgumentException.class, () -> source.read(null, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> source.read(target, -1, 1));
        assertThrows(IllegalArgumentException.class, () -> source.read(target, 0, -1));
        assertThrows(IllegalArgumentException.class, () -> source.read(target, 1, 2));
        assertThrows(IllegalArgumentException.class, () -> source.read(target, 0, Integer.MAX_VALUE));
        assertEquals(0, source.read(target, 0, 0));
        assertEquals(2, source.read(target, 0, 2));
        assertThrows(IllegalArgumentException.class, () -> decoder.open(null));
    }

    /**
     * Sources of one decoder are read on several threads at once and each gives the samples of the sound: JOrbis keeps
     * its shared tables under its own locks.
     *
     * @throws IOException If reading fails.
     * @throws InterruptedException If interrupted.
     * @throws ExecutionException If a decoding fails.
     * @throws TimeoutException If the decodings take too long.
     */
    @Test
    void testConcurrentSources() throws IOException, InterruptedException, ExecutionException, TimeoutException
    {
        final OggVorbisDecoder decoder = new OggVorbisDecoder();
        final byte[] mono = OggFiles.ogg(OggFiles.LONG_MONO_22050);
        final byte[] stereo = OggFiles.ogg(OggFiles.STEREO_44100);
        final short[] expectedMono = readAll(open(mono), PIECE);
        final short[] expectedStereo = readAll(open(stereo), PIECE);
        try (ExecutorService executor = Executors.newFixedThreadPool(THREADS))
        {
            final List<Future<short[]>> futures = new ArrayList<>();
            for (int i = 0; i < THREADS; i++)
            {
                final byte[] sound = i % 2 == 0 ? mono : stereo;
                final Callable<short[]> task = () -> readAll(decoder.open(new ByteArrayInputStream(sound)), 100);
                futures.add(executor.submit(task));
            }
            for (int i = 0; i < THREADS; i++)
            {
                assertArrayEquals(i % 2 == 0 ? expectedMono : expectedStereo,
                                  futures.get(i).get(TIMEOUT_SECONDS, TimeUnit.SECONDS));
            }
        }
    }

    /**
     * Once the first packets are decoded, reading a source allocates nothing: it runs on the backend's thread for each
     * audio buffer. Measured over four seconds of mono, the lowest of {@link #MEASURES} sources.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testSteadyAllocation() throws IOException
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final byte[] sound = OggFiles.ogg(OggFiles.LONG_MONO_22050);
        final short[] target = new short[PIECE];
        long lowest = Long.MAX_VALUE;
        for (int measure = 0; measure < MEASURES; measure++)
        {
            final PcmSource source = open(sound);
            int frames = 0;
            while (frames < WARM_UP)
            {
                frames += source.read(target, 0, PIECE);
            }
            final long before = bean.getCurrentThreadAllocatedBytes();
            int read;
            do
            {
                read = source.read(target, 0, PIECE);
                frames += read;
            }
            while (read == PIECE);
            lowest = Math.min(lowest, bean.getCurrentThreadAllocatedBytes() - before);
            assertEquals(110_250, frames);
        }
        final long allocated = lowest;

        assertEquals(0L, allocated, () -> "Allocated bytes: " + allocated);
    }
}
