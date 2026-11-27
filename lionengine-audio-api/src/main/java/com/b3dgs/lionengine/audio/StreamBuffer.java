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

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Bounded buffer of a streamed long sound: a ring of chunks decoded ahead from a {@link PcmSource}, pulled by the
 * backend (ADR-0027, ADR-0033, ADR-0068, REQ-AUDIO-006).
 *
 * <p>
 * {@link #fill()} decodes chunks until the ring is full, on the runtime thread when the sound is prepared. Then the
 * backend's thread reads the frames in order; each chunk read to its end is decoded again at once from the source, so
 * the ring stays full of frames ahead of the voice until the source ends. The ring is the only sample memory of the
 * stream: {@code chunks × chunkFrames} frames, whatever the length of the sound. A failure of the source (an
 * {@link IOException} or an {@link AudioDecodingException}) ends the stream after the frames already decoded, and is
 * kept for the runtime thread.
 * </p>
 *
 * <p>
 * Threads: filled on the runtime thread before the play, then read by one backend thread until the voice ends or is
 * stopped (the hand-off is ordered by the backend and the inbox); the failure is an atomic reference. Reading
 * allocates nothing.
 * </p>
 */
final class StreamBuffer implements PcmStream
{
    /** The source decoded. */
    private final PcmSource source;
    /** The format of the frames. */
    private final PcmFormat format;
    /** The channel count. */
    private final int channels;
    /** The number of chunks of the ring. */
    private final int chunks;
    /** The frames of a chunk. */
    private final int chunkFrames;
    /** The samples of the chunks, interleaved by frame. */
    private final short[] ring;
    /** The frames decoded in each chunk. */
    private final int[] filled;
    /** The chunk read next. */
    private int head;
    /** The frames already read from the head chunk. */
    private int position;
    /** The number of chunks decoded and not read to their end. */
    private int ready;
    /** {@code true} once the source ended or failed: nothing more is decoded. */
    private boolean sourceEnded;
    /** The failure of the source, {@code null} if none; set by the backend's thread, read on the runtime thread. */
    private final AtomicReference<Exception> failure = new AtomicReference<>();

    /**
     * Creates an empty buffer.
     *
     * @param source The source, opened.
     * @param chunks The number of chunks (positive).
     * @param chunkFrames The frames of a chunk (positive).
     */
    StreamBuffer(PcmSource source, int chunks, int chunkFrames)
    {
        super();

        this.source = source;
        this.chunks = chunks;
        this.chunkFrames = chunkFrames;
        format = source.getFormat();
        channels = format.channels();
        ring = new short[chunks * chunkFrames * channels];
        filled = new int[chunks];
    }

    /**
     * Decodes chunks until the ring is full or the source ends.
     */
    void fill()
    {
        while (ready < chunks && !sourceEnded)
        {
            decode((head + ready) % chunks);
        }
    }

    /**
     * Returns the failure of the source.
     *
     * @return The exception the source raised, {@code null} if none.
     */
    Exception getFailure()
    {
        return failure.get();
    }

    /**
     * Returns the capacity of the ring.
     *
     * @return The number of frames the ring holds at most.
     */
    int getCapacity()
    {
        return chunks * chunkFrames;
    }

    /**
     * Returns the frames decoded ahead.
     *
     * @return The frames decoded and not read yet.
     */
    int getBufferedFrames()
    {
        int frames = -position;
        for (int i = 0; i < ready; i++)
        {
            frames += filled[(head + i) % chunks];
        }
        return frames;
    }

    @Override
    public PcmFormat getFormat()
    {
        return format;
    }

    @Override
    public int read(short[] target, int offset, int frames)
    {
        final boolean negative = offset < 0 || frames < 0;
        if (target == null || negative || offset > target.length - frames * channels)
        {
            throw new IllegalArgumentException("Invalid frame range: " + offset + '+' + frames);
        }
        int copied = 0;
        while (copied < frames && ready > 0)
        {
            final int count = Math.min(filled[head] - position, frames - copied);
            System.arraycopy(ring,
                             (head * chunkFrames + position) * channels,
                             target,
                             offset + copied * channels,
                             count * channels);
            position += count;
            copied += count;
            if (position == filled[head])
            {
                position = 0;
                head = (head + 1) % chunks;
                ready--;
                fill();
            }
        }
        return copied;
    }

    /**
     * Decodes one chunk from the source; a short chunk ends the source, a failure is kept and ends it.
     *
     * @param chunk The index of the chunk.
     */
    private void decode(int chunk)
    {
        int frames;
        try
        {
            frames = source.read(ring, chunk * chunkFrames * channels, chunkFrames);
        }
        catch (final IOException | AudioDecodingException exception)
        {
            failure.set(exception);
            frames = 0;
        }
        if (frames < chunkFrames)
        {
            sourceEnded = true;
        }
        filled[chunk] = frames;
        if (frames > 0)
        {
            ready++;
        }
    }
}
