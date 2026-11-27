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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Test;

import com.sun.management.ThreadMXBean;

/**
 * Tests of {@link StreamBuffer}: frames read in order across the chunks, the end, the failures, the bounded memory of
 * a long stream and the per-chunk path without allocation (ADR-0033, ADR-0068, REQ-AUDIO-006).
 */
final class StreamBufferTest
{
    /** Sample rate of the sounds. */
    private static final int RATE = 44_100;
    /** Frames of a read of the tests, not a divisor of the chunk size. */
    private static final int PIECE = 1_000;
    /** Tolerance of the allocation test, in bytes, for the JVM's own accounting. */
    private static final long ALLOCATION_TOLERANCE = 1_024L;

    /**
     * Opens a generated WAV file for streaming.
     *
     * @param input The file.
     * @return The source.
     * @throws IOException If reading fails.
     */
    private static PcmSource open(InputStream input) throws IOException
    {
        return new WavDecoder().open(input);
    }

    /**
     * Reads a stream to its end in pieces of {@link #PIECE} frames, checking every sample.
     *
     * @param stream The stream.
     * @param from The index of the first sample expected.
     * @return The frames read.
     */
    private static long readChecked(StreamBuffer stream, long from)
    {
        final short[] target = new short[PIECE * PcmFormat.STEREO];
        long frames = 0L;
        int read;
        do
        {
            read = stream.read(target, 0, PIECE);
            for (int i = 0; i < read * PcmFormat.STEREO; i++)
            {
                assertEquals(GeneratedWav.sample(from + frames * PcmFormat.STEREO + i), target[i]);
            }
            frames += read;
        }
        while (read == PIECE);
        return frames;
    }

    /**
     * The buffer is filled ahead to its capacity, then gives every frame in order across the chunks, fewer at the end,
     * then none.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testReadsInOrder() throws IOException
    {
        final StreamBuffer stream = new StreamBuffer(open(new GeneratedWav(RATE, 10_001)), 4, 256);

        assertEquals(1_024, stream.getCapacity());
        assertEquals(0, stream.getBufferedFrames());
        stream.fill();
        assertEquals(1_024, stream.getBufferedFrames());
        assertEquals(new PcmFormat(RATE, PcmFormat.STEREO), stream.getFormat());

        assertEquals(10_001L, readChecked(stream, 0L));
        assertEquals(0, stream.read(new short[2], 0, 1));
        assertEquals(0, stream.read(new short[0], 0, 0));
        assertNull(stream.getFailure());
    }

    /**
     * A sound shorter than the buffer is decoded entirely by the fill; an empty one ends at once.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testShortSound() throws IOException
    {
        final StreamBuffer stream = new StreamBuffer(open(new GeneratedWav(RATE, 300)), 4, 256);
        final StreamBuffer empty = new StreamBuffer(open(new GeneratedWav(RATE, 0)), 4, 256);

        stream.fill();
        empty.fill();

        assertEquals(300, stream.getBufferedFrames());
        assertEquals(300L, readChecked(stream, 0L));
        assertEquals(0, empty.read(new short[2], 0, 1));
    }

    /**
     * A truncated file ends the stream after its last decoded frames and keeps the decoding failure.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testTruncated() throws IOException
    {
        final StreamBuffer stream = new StreamBuffer(open(new GeneratedWav(RATE, 5_000, 2_000)), 4, 256);
        stream.fill();

        final long frames = readChecked(stream, 0L);

        assertTrue(frames <= 2_000L, () -> "Frames: " + frames);
        assertInstanceOf(AudioDecodingException.class, stream.getFailure());
    }

    /**
     * A read failure of the resource ends the stream and keeps the exception.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testReadFailure() throws IOException
    {
        final GeneratedWav wav = new GeneratedWav(RATE, 5_000);
        final InputStream failing = new InputStream()
        {
            private int left = 44 + 4 * 600;

            @Override
            public int read() throws IOException
            {
                if (left == 0)
                {
                    throw new IOException("Disk removed");
                }
                left--;
                return wav.read();
            }
        };
        final StreamBuffer stream = new StreamBuffer(open(failing), 4, 256);
        stream.fill();

        assertEquals(512L, readChecked(stream, 0L));
        assertInstanceOf(IOException.class, stream.getFailure());
    }

    /**
     * Invalid ranges are refused.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testArguments() throws IOException
    {
        final StreamBuffer stream = new StreamBuffer(open(new GeneratedWav(RATE, 100)), 4, 256);
        final short[] target = new short[4];

        assertThrows(IllegalArgumentException.class, () -> stream.read(null, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> stream.read(target, -1, 1));
        assertThrows(IllegalArgumentException.class, () -> stream.read(target, 0, -1));
        assertThrows(IllegalArgumentException.class, () -> stream.read(target, 1, 2));
    }

    /**
     * Ten minutes of stereo at 44.1 kHz, 105.8 MB of samples once decoded, stream through the default buffer of 4
     * chunks of 4,096 frames: the buffer holds 16,384 frames, and streaming the whole sound allocates almost nothing.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testBoundedMemory() throws IOException
    {
        final long frames = 10L * 60L * RATE;
        final StreamBuffer stream = new StreamBuffer(open(new GeneratedWav(RATE, frames)),
                                                     LongSoundLoader.DEFAULT_CHUNKS,
                                                     LongSoundLoader.DEFAULT_CHUNK_FRAMES);
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final short[] target = new short[PIECE * PcmFormat.STEREO];
        stream.fill();

        final long before = bean.getCurrentThreadAllocatedBytes();
        long read = 0L;
        int count;
        do
        {
            count = stream.read(target, 0, PIECE);
            read += count;
        }
        while (count == PIECE);
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertEquals(frames, read);
        assertEquals(16_384, stream.getCapacity());
        assertTrue(allocated < 64L * 1_024L, () -> "Allocated bytes: " + allocated);
        assertNull(stream.getFailure());
    }

    /**
     * Reading a stream, which decodes a chunk each time one is read to its end, allocates nothing: it runs for every
     * audio buffer of the backend.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testAllocatesNothing() throws IOException
    {
        final StreamBuffer stream = new StreamBuffer(open(new GeneratedWav(RATE, Integer.MAX_VALUE / 4)),
                                                     LongSoundLoader.DEFAULT_CHUNKS,
                                                     LongSoundLoader.DEFAULT_CHUNK_FRAMES);
        final short[] target = new short[PIECE * PcmFormat.STEREO];
        stream.fill();
        final Runnable workload = () ->
        {
            for (int i = 0; i < 200; i++)
            {
                stream.read(target, 0, PIECE);
            }
        };

        final long allocated = SilentAudioBackend.steadyAllocation(workload);

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertNull(stream.getFailure());
    }
}
