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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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

/**
 * Contract suite of {@link AudioDecoder} (ADR-0045, ADR-0065, REQ-AUDIO-009).
 *
 * <p>
 * Published in the {@code audio-api} test JAR: every decoder binds it with a valid sound of its format and the samples
 * it decodes to.
 * </p>
 */
public abstract class AudioDecoderContract
{
    /** Threads of the concurrent test. */
    private static final int THREADS = 2;
    /** Decodings per thread. */
    private static final int DECODINGS = 20;
    /** Timeout of the concurrent test. */
    private static final long TIMEOUT_SECONDS = 30L;

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
     * Creates the decoder with its default frame limit.
     *
     * @return The decoder.
     */
    protected abstract AudioDecoder createDecoder();

    /**
     * Creates the decoder with a frame limit.
     *
     * @param maxFrames The frame limit.
     * @return The decoder.
     */
    protected abstract AudioDecoder createDecoder(int maxFrames);

    /**
     * Returns a valid sound of the decoder's format, of at least two frames.
     *
     * @return The encoded bytes.
     */
    protected abstract byte[] getSound();

    /**
     * Returns the samples {@link #getSound()} decodes to.
     *
     * @return The expected buffer.
     */
    protected abstract PcmBuffer getExpected();

    /**
     * Checks that a buffer holds the expected samples.
     *
     * @param buffer The buffer.
     */
    private void assertExpected(PcmBuffer buffer)
    {
        final PcmBuffer expected = getExpected();
        assertEquals(expected.getFormat(), buffer.getFormat());
        assertEquals(expected.getFrames(), buffer.getFrames());
        assertArrayEquals(samples(expected), samples(buffer));
    }

    /**
     * The sound decodes to the expected samples.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testDecode() throws IOException
    {
        assertExpected(createDecoder().decode(new ByteArrayInputStream(getSound())));
    }

    /**
     * A decoder decodes a sound of exactly its frame limit, and refuses it with one frame less.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testFrameLimit() throws IOException
    {
        final int frames = getExpected().getFrames();
        final AudioDecoder below = createDecoder(frames - 1);
        final InputStream input = new ByteArrayInputStream(getSound());

        assertExpected(createDecoder(frames).decode(new ByteArrayInputStream(getSound())));
        assertThrows(AudioDecodingException.class, () -> below.decode(input));
    }

    /**
     * Each call returns a new buffer.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testNewBuffer() throws IOException
    {
        final AudioDecoder decoder = createDecoder();

        assertNotSame(decoder.decode(new ByteArrayInputStream(getSound())),
                      decoder.decode(new ByteArrayInputStream(getSound())));
    }

    /**
     * The decoder reads the stream but does not close it: the caller does.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testStreamLeftOpen() throws IOException
    {
        final boolean[] closed = new boolean[1];
        final InputStream input = new ByteArrayInputStream(getSound())
        {
            @Override
            public void close() throws IOException
            {
                closed[0] = true;
                super.close();
            }
        };
        createDecoder().decode(input);

        assertFalse(closed[0]);
    }

    /**
     * A failure of the stream is an {@link IOException}, not a decoding error (ADR-0007).
     */
    @Test
    void testReadFailure()
    {
        final AudioDecoder decoder = createDecoder();
        final InputStream input = new InputStream()
        {
            @Override
            public int read() throws IOException
            {
                throw new IOException("Read failure");
            }
        };

        assertThrows(IOException.class, () -> decoder.decode(input));
    }

    /**
     * An empty stream, content that is not a sound, and a truncated sound are decoding errors.
     */
    @Test
    void testInvalid()
    {
        final AudioDecoder decoder = createDecoder();
        final byte[] sound = getSound();
        final InputStream empty = new ByteArrayInputStream(new byte[0]);
        final InputStream text = new ByteArrayInputStream("Not a sound, only text.".getBytes(StandardCharsets.UTF_8));
        final InputStream truncated = new ByteArrayInputStream(Arrays.copyOf(sound, sound.length - 1));

        assertThrows(AudioDecodingException.class, () -> decoder.decode(empty));
        assertThrows(AudioDecodingException.class, () -> decoder.decode(text));
        assertThrows(AudioDecodingException.class, () -> decoder.decode(truncated));
    }

    /**
     * A {@code null} stream is rejected.
     */
    @Test
    void testNull()
    {
        final AudioDecoder decoder = createDecoder();

        assertThrows(IllegalArgumentException.class, () -> decoder.decode(null));
    }

    /**
     * One decoder decodes on two threads at once, as loading workers do (ADR-0019).
     *
     * @throws InterruptedException If interrupted.
     * @throws ExecutionException If a decoding fails.
     * @throws TimeoutException If the decodings take too long.
     */
    @Test
    void testConcurrent() throws InterruptedException, ExecutionException, TimeoutException
    {
        final AudioDecoder decoder = createDecoder();
        final byte[] sound = getSound();
        final Callable<List<PcmBuffer>> task = () ->
        {
            final List<PcmBuffer> results = new ArrayList<>();
            for (int i = 0; i < DECODINGS; i++)
            {
                results.add(decoder.decode(new ByteArrayInputStream(sound)));
            }
            return results;
        };
        final ExecutorService executor = Executors.newFixedThreadPool(THREADS);
        try
        {
            final List<Future<List<PcmBuffer>>> futures = new ArrayList<>();
            for (int i = 0; i < THREADS; i++)
            {
                futures.add(executor.submit(task));
            }
            for (final Future<List<PcmBuffer>> future : futures)
            {
                for (final PcmBuffer buffer : future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS))
                {
                    assertExpected(buffer);
                }
            }
        }
        finally
        {
            executor.shutdownNow();
        }
    }
}
