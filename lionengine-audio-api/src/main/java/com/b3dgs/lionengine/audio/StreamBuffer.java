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
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicReference;

import com.b3dgs.lionengine.resources.ResourceException;

/**
 * Bounded buffer of a streamed long sound: a ring of chunks decoded ahead from a {@link PcmSource}, pulled by the
 * backend, looping on request (ADR-0027, ADR-0033, ADR-0068, ADR-0069, REQ-AUDIO-006, REQ-AUDIO-011).
 *
 * <p>
 * {@link #fill()} decodes chunks until the ring is full, on the runtime thread when the sound is prepared. Then the
 * backend's thread reads the frames in order; each chunk read to its end is decoded again at once from the source, so
 * the ring stays full of frames ahead of the voice until the source ends. The ring is the only sample memory of the
 * stream: {@code chunks × chunkFrames} frames, whatever the length of the sound. A failure of the source (an
 * {@link IOException}, an {@link AudioDecodingException}, a {@link ResourceException}, or any other unchecked exception
 * of the decoder or of the resource, and a source returning a frame count outside what was asked) ends the stream after
 * the frames already decoded, and is kept for the runtime thread: the backend's thread never receives it (ADR-0072).
 * </p>
 *
 * <p>
 * Looping: when the source of a looping buffer ends, the resource is opened again and decoding goes on from its first
 * frame in the same chunk, so the loop has no gap and the stream never ends. The previous resource stream is closed
 * then, and a failure to close it is dropped, since nothing reads it any more. A pass of no frame ends the stream, so
 * an empty sound never spins. Each pass opens a new resource stream and source: one allocation per pass, never per
 * chunk. A failure to open again, or a new pass of another format, ends the stream as a failure of the source.
 * </p>
 *
 * <p>
 * Threads: filled on the runtime thread before the play, then read by one backend thread until the voice ends or is
 * stopped (the hand-off is ordered by the backend and the inbox); the failure and the resource stream are atomic
 * references, the looping is volatile. Reading allocates nothing, except the opening of a new pass of a loop.
 * </p>
 */
final class StreamBuffer implements PcmStream
{
    /** Opens the resource again for a new pass of a loop, {@code null} if it cannot loop. */
    private final Opener opener;
    /** The source decoded. */
    private PcmSource source;
    /** The resource stream the source reads, {@code null} if not owned; closed by {@link #close()}. */
    private final AtomicReference<InputStream> input;
    /** {@code true} to decode a new pass once the source ends. */
    private volatile boolean looping;
    /** {@code true} once the current source returned fewer frames than asked. */
    private boolean exhausted;
    /** The frames decoded from the current source. */
    private long passFrames;
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
     * Creates an empty buffer that cannot loop and owns no resource stream.
     *
     * @param source The source, opened.
     * @param chunks The number of chunks (positive).
     * @param chunkFrames The frames of a chunk (positive).
     */
    StreamBuffer(PcmSource source, int chunks, int chunkFrames)
    {
        this(null, source, chunks, chunkFrames, null);
    }

    /**
     * Creates an empty buffer.
     *
     * @param input The resource stream the source reads, closed by {@link #close()}, {@code null} if not owned.
     * @param source The source, opened.
     * @param chunks The number of chunks (positive).
     * @param chunkFrames The frames of a chunk (positive).
     * @param opener Opens the resource again for a new pass of a loop, {@code null} if it cannot loop.
     */
    StreamBuffer(InputStream input, PcmSource source, int chunks, int chunkFrames, Opener opener)
    {
        super();

        this.input = new AtomicReference<>(input);
        this.source = source;
        this.opener = opener;
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
     * Sets whether the stream loops, before it is played. A buffer whose source already ended without failure decodes
     * a new pass at once when it starts looping.
     *
     * @param loop {@code true} to loop.
     * @throws IllegalStateException If looping a buffer that cannot loop.
     */
    void setLooping(boolean loop)
    {
        if (loop && opener == null)
        {
            throw new IllegalStateException("Stream buffer without opener");
        }
        looping = loop;
        if (loop && sourceEnded && failure.get() == null && passFrames > 0)
        {
            sourceEnded = false;
            fill();
        }
    }

    /**
     * Closes the resource stream, once the buffer is no longer read.
     *
     * @return The failure to close it, {@code null} if it closed or there was none.
     */
    IOException close()
    {
        final InputStream current = input.getAndSet(null);
        IOException closeFailure = null;
        if (current != null)
        {
            closeFailure = LongSoundLoader.close(current);
        }
        return closeFailure;
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
     * Decodes one chunk from the source; the end of the source ends the stream, or starts a new pass when looping; a
     * failure is kept and ends the stream after the frames decoded.
     *
     * @param chunk The index of the chunk.
     */
    private void decode(int chunk)
    {
        final int base = chunk * chunkFrames * channels;
        int frames = 0;
        try
        {
            while (frames < chunkFrames && !sourceEnded)
            {
                if (exhausted)
                {
                    wrap();
                }
                else
                {
                    final int asked = chunkFrames - frames;
                    final int read = source.read(ring, base + frames * channels, asked);
                    if (read < 0 || read > asked)
                    {
                        throw new AudioDecodingException("Source read " + read + " frames of " + asked + " asked");
                    }
                    frames += read;
                    passFrames += read;
                    exhausted = read < asked;
                }
            }
        }
        // CHECKSTYLE IGNORE LINE: IllegalCatch
        catch (final IOException | RuntimeException exception)
        {
            // Every failure of the source stops here, on the backend's thread, which keeps mixing (ADR-0072).
            failure.set(exception);
            sourceEnded = true;
        }
        filled[chunk] = frames;
        if (frames > 0)
        {
            ready++;
        }
    }

    /**
     * Handles the end of the source: opens a new pass when looping and the pass had frames, ends the stream otherwise.
     *
     * @throws IOException If the resource cannot be read.
     * @throws AudioDecodingException If the new pass is invalid, or of another format.
     * @throws ResourceException If the resource is missing.
     */
    private void wrap() throws IOException
    {
        if (looping && passFrames > 0)
        {
            final LongSoundLoader.Opened opened = opener.open();
            final PcmFormat passFormat = opened.source().getFormat();
            if (!format.equals(passFormat))
            {
                LongSoundLoader.close(opened.input());
                throw new AudioDecodingException("Format changed at the loop: " + format + " then " + passFormat);
            }
            // The previous stream is no longer read: a failure to close it is dropped, as after a stop.
            final InputStream previous = input.getAndSet(opened.input());
            source = opened.source();
            exhausted = false;
            passFrames = 0L;
            if (previous != null)
            {
                LongSoundLoader.close(previous);
            }
        }
        else
        {
            sourceEnded = true;
        }
    }

    /**
     * Opens the resource of a looping buffer again, on the thread that reads it.
     */
    @FunctionalInterface
    interface Opener
    {
        /**
         * Opens the resource and its source from the first frame.
         *
         * @return The resource stream, to close once no longer read, and its source.
         * @throws IOException If the resource cannot be read; the stream is closed then.
         * @throws AudioDecodingException If the header is invalid; the stream is closed then.
         * @throws ResourceException If the resource is missing.
         */
        LongSoundLoader.Opened open() throws IOException;
    }
}
