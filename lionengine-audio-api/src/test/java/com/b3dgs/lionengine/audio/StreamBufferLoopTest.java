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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.sun.management.ThreadMXBean;

/**
 * Tests of the loop of {@link StreamBuffer}: a new pass opened when the source ends, without gap, for sounds longer
 * and shorter than the buffer, the end of an empty sound, the failures of a new pass, and the bounded memory of a
 * long loop (ADR-0068, ADR-0069, REQ-AUDIO-006, REQ-AUDIO-011).
 */
final class StreamBufferLoopTest
{
    /** Sample rate of the sounds. */
    private static final int RATE = 44_100;
    /** Frames of a read of the tests, not a divisor of the chunk size. */
    private static final int PIECE = 1_000;
    /** Chunks of the buffers of the tests. */
    private static final int CHUNKS = 4;
    /** Frames of a chunk of the buffers of the tests. */
    private static final int CHUNK_FRAMES = 256;

    /**
     * Opener of generated WAV files of a length, recording the streams it opened.
     */
    private static final class Files implements StreamBuffer.Opener
    {
        /** The frames of each file. */
        private final long frames;
        /** The streams opened, in order. */
        private final List<GeneratedWav> opened = new ArrayList<>();

        /**
         * Creates the opener.
         *
         * @param frames The frames of each file.
         */
        Files(long frames)
        {
            this.frames = frames;
        }

        /**
         * Creates a buffer on the first file, filled as a long sound is at its preparation.
         *
         * @return The buffer, filled and not looping.
         * @throws IOException If reading fails.
         */
        StreamBuffer buffer() throws IOException
        {
            final LongSoundLoader.Opened first = open();
            final StreamBuffer stream = new StreamBuffer(first.input(), first.source(), CHUNKS, CHUNK_FRAMES, this);
            stream.fill();
            return stream;
        }

        @Override
        public LongSoundLoader.Opened open() throws IOException
        {
            final GeneratedWav wav = new GeneratedWav(RATE, frames);
            opened.add(wav);
            return new LongSoundLoader.Opened(wav, new WavDecoder().open(wav));
        }
    }

    /**
     * Reads frames in pieces of {@link #PIECE}, checking that each one is the frame of its place in the loop.
     *
     * @param stream The stream.
     * @param frames The frames to read.
     * @param soundFrames The frames of one pass.
     */
    private static void readLooping(StreamBuffer stream, long frames, long soundFrames)
    {
        final short[] target = new short[PIECE * PcmFormat.STEREO];
        long done = 0L;
        while (done < frames)
        {
            final int asked = (int) Math.min(PIECE, frames - done);
            assertEquals(asked, stream.read(target, 0, asked));
            for (int i = 0; i < asked * PcmFormat.STEREO; i++)
            {
                final long frame = (done + i / PcmFormat.STEREO) % soundFrames;
                assertEquals(GeneratedWav.sample(frame * PcmFormat.STEREO + i % PcmFormat.STEREO), target[i]);
            }
            done += asked;
        }
    }

    /**
     * A sound longer than the buffer loops without gap: the first frame of a pass follows the last frame of the
     * previous one in the same read, and the stream never returns fewer frames; each pass closes the previous file.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testLoopWithoutGap() throws IOException
    {
        final Files files = new Files(1_500L);
        final StreamBuffer stream = files.buffer();

        stream.setLooping(true);
        readLooping(stream, 5L * 1_500L + 300L, 1_500L);

        assertNull(stream.getFailure());
        final int opened = files.opened.size();
        assertTrue(opened >= 6, () -> "Opened: " + opened);
        for (int i = 0; i < opened - 1; i++)
        {
            assertTrue(files.opened.get(i).isClosed());
        }
        assertFalse(files.opened.get(opened - 1).isClosed());
        assertNull(stream.close());
        assertTrue(files.opened.get(opened - 1).isClosed());
    }

    /**
     * A sound shorter than the buffer, whose source ended at the fill, decodes a new pass as soon as it loops.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testLoopShortSound() throws IOException
    {
        final Files files = new Files(300L);
        final StreamBuffer stream = files.buffer();
        assertEquals(300, stream.getBufferedFrames());

        stream.setLooping(true);

        // The last chunk of the fill keeps its 44 frames; the free chunks are decoded from the next passes.
        assertEquals(44 + 3 * CHUNK_FRAMES, stream.getBufferedFrames());
        readLooping(stream, 10_000L, 300L);
        assertNull(stream.getFailure());
        stream.close();
    }

    /**
     * An empty sound never loops: its stream ends at once, without opening a new pass.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testLoopEmpty() throws IOException
    {
        final Files files = new Files(0L);
        final StreamBuffer stream = files.buffer();

        stream.setLooping(true);

        assertEquals(0, stream.read(new short[2], 0, 1));
        assertEquals(1, files.opened.size());
        assertNull(stream.getFailure());
    }

    /**
     * A buffer that may loop but is not set looping plays once and opens nothing more; a buffer without opener cannot
     * loop.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testNotLooping() throws IOException
    {
        final Files files = new Files(1_500L);
        final StreamBuffer stream = files.buffer();
        stream.setLooping(false);
        final short[] target = new short[PIECE * PcmFormat.STEREO];

        assertEquals(PIECE, stream.read(target, 0, PIECE));
        assertEquals(500, stream.read(target, 0, PIECE));
        assertEquals(1, files.opened.size());

        final StreamBuffer single = new StreamBuffer(new WavDecoder().open(new GeneratedWav(RATE, 10L)), 1, 16);
        assertThrows(IllegalStateException.class, () -> single.setLooping(true));
        assertNull(single.close());
    }

    /**
     * A new pass that fails to open, or of another format, ends the stream after the frames decoded, and keeps the
     * failure for the runtime thread.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testPassFailures() throws IOException
    {
        final Files files = new Files(1_500L);
        final LongSoundLoader.Opened first = files.open();
        final StreamBuffer missing = new StreamBuffer(first.input(), first.source(), CHUNKS, CHUNK_FRAMES, () ->
        {
            throw new ResourceException(new ResourceKey("music/gone.wav"), ResourceException.Reason.MISSING, null);
        });
        missing.fill();
        missing.setLooping(true);

        assertEquals(1_500L, read(missing));
        assertInstanceOf(ResourceException.class, missing.getFailure());

        final LongSoundLoader.Opened second = files.open();
        final StreamBuffer changed = new StreamBuffer(second.input(), second.source(), CHUNKS, CHUNK_FRAMES, () ->
        {
            final GeneratedWav other = new GeneratedWav(RATE / 2, 1_500L);
            return new LongSoundLoader.Opened(other, new WavDecoder().open(other));
        });
        changed.fill();
        changed.setLooping(true);

        assertEquals(1_500L, read(changed));
        assertInstanceOf(AudioDecodingException.class, changed.getFailure());
        assertTrue(changed.getFailure().getMessage().startsWith("Format changed at the loop"));
        changed.close();
    }

    /**
     * A looping stream played for a long time keeps the same buffer: one pass opens one file and one source, with the
     * read buffer of the WAV source (8 KB), so the memory stays bounded however long it loops.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testBoundedMemory() throws IOException
    {
        final long soundFrames = 100_000L;
        final int passes = 50;
        final Files files = new Files(soundFrames);
        final StreamBuffer stream = files.buffer();
        stream.setLooping(true);
        final short[] target = new short[PIECE * PcmFormat.STEREO];
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();

        final long before = bean.getCurrentThreadAllocatedBytes();
        for (long done = 0L; done < passes * soundFrames; done += PIECE)
        {
            assertEquals(PIECE, stream.read(target, 0, PIECE));
        }
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertEquals(CHUNKS * CHUNK_FRAMES, stream.getCapacity());
        assertTrue(allocated < passes * 16_384L, () -> "Allocated bytes: " + allocated);
        assertNull(stream.getFailure());
        stream.close();
    }

    /**
     * The reads of a looping stream between two passes, which decode a chunk each time one is read to its end, allocate
     * nothing: they run for every audio buffer of the backend.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testAllocatesNothing() throws IOException
    {
        final Files files = new Files(Integer.MAX_VALUE / 4);
        final StreamBuffer stream = files.buffer();
        stream.setLooping(true);
        final short[] target = new short[PIECE * PcmFormat.STEREO];
        final Runnable workload = () ->
        {
            for (int i = 0; i < 200; i++)
            {
                stream.read(target, 0, PIECE);
            }
        };

        final long allocated = SilentAudioBackend.steadyAllocation(workload);

        assertTrue(allocated < 1_024L, () -> "Allocated bytes: " + allocated);
        assertEquals(1, files.opened.size());
        stream.close();
    }

    /**
     * Reads a stream to its end.
     *
     * @param stream The stream.
     * @return The frames read.
     */
    private static long read(StreamBuffer stream)
    {
        final short[] target = new short[PIECE * PcmFormat.STEREO];
        long frames = 0L;
        int count;
        do
        {
            count = stream.read(target, 0, PIECE);
            frames += count;
        }
        while (count == PIECE);
        return frames;
    }
}
