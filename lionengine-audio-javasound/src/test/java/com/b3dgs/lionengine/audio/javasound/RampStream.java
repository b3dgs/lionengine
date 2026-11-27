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
package com.b3dgs.lionengine.audio.javasound;

import com.b3dgs.lionengine.audio.PcmFormat;
import com.b3dgs.lionengine.audio.PcmStream;

/**
 * Stream of the tests: a ramp of samples (frame {@code i} has the value {@code i} on every channel, wrapping at
 * {@code 2^15}), of a given length or endless, counting its reads and checking that none happens once forbidden.
 * Thread-safe.
 */
final class RampStream implements PcmStream
{
    /** Modulo of the ramp. */
    private static final int MODULO = 1 << 15;

    /** The format. */
    private final PcmFormat format;
    /** The frames of the sound, negative for an endless one. */
    private final long frames;
    /** The frames read. */
    private long read;
    /** The calls. */
    private int calls;
    /** {@code true} once a read is forbidden. */
    private boolean forbidden;
    /** {@code true} if a read happened while forbidden. */
    private boolean violated;

    /**
     * Creates the stream.
     *
     * @param format The format.
     * @param frames The frames, negative for an endless stream.
     */
    RampStream(PcmFormat format, long frames)
    {
        this.format = format;
        this.frames = frames;
    }

    /**
     * Returns the frames read.
     *
     * @return The frames given.
     */
    synchronized long getRead()
    {
        return read;
    }

    /**
     * Returns the calls.
     *
     * @return The calls of {@link #read(short[], int, int)}.
     */
    synchronized int getCalls()
    {
        return calls;
    }

    /**
     * Forbids any further read; a read after it is recorded as a violation.
     */
    synchronized void forbid()
    {
        forbidden = true;
    }

    /**
     * Tells whether a read happened after {@link #forbid()}.
     *
     * @return {@code true} if violated.
     */
    synchronized boolean isViolated()
    {
        return violated;
    }

    @Override
    public PcmFormat getFormat()
    {
        return format;
    }

    @Override
    public synchronized int read(short[] target, int offset, int count)
    {
        violated |= forbidden;
        calls++;
        final int copied = (int) (frames < 0 ? count : Math.min(count, frames - read));
        final int channels = format.channels();
        for (int i = 0; i < copied; i++)
        {
            for (int c = 0; c < channels; c++)
            {
                target[offset + i * channels + c] = (short) ((read + i) % MODULO);
            }
        }
        read += copied;
        return copied;
    }
}
