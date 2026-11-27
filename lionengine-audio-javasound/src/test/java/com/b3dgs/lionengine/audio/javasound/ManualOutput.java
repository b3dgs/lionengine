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

import java.util.concurrent.TimeUnit;

/**
 * Output of the tests, driven by the test as the clock of the mixing thread (ADR-0070).
 *
 * <p>
 * Each {@link #write(byte[], int)} waits until the test grants a buffer with {@link #advance(int)}, which returns once
 * the granted buffers are written and the mixing thread mixed the next one and waits again, so that every end posted
 * by then is in the inbox. In free-running mode the writes return at once. Thread-safe.
 * </p>
 */
final class ManualOutput implements MixOutput
{
    /** Longest wait of an advance, in nanoseconds. */
    private static final long TIMEOUT_NANOS = TimeUnit.SECONDS.toNanos(10L);
    /** Wait between two checks, in milliseconds. */
    private static final long WAIT_MILLIS = 10L;

    /** The buffers granted. */
    private long granted;
    /** The buffers written. */
    private long written;
    /** The writes entered. */
    private long entered;
    /** The frames of one buffer. */
    private final int bufferFrames;
    /** {@code true} when writes return at once. */
    private boolean freeRunning;
    /** {@code true} once closed. */
    private boolean closed;
    /** The last buffer written. */
    private byte[] last = new byte[0];

    /**
     * Creates the output.
     *
     * @param bufferFrames The frames of one buffer.
     */
    ManualOutput(int bufferFrames)
    {
        this.bufferFrames = bufferFrames;
    }

    /**
     * Lets frames be played, then waits until they are written and the next buffer is mixed; returns at once when
     * closed.
     *
     * @param frames The frames.
     */
    synchronized void advanceFrames(int frames)
    {
        advance((frames + bufferFrames - 1) / bufferFrames);
    }

    /**
     * Lets buffers be played, then waits until they are written and the next buffer is mixed; returns at once when
     * closed.
     *
     * @param buffers The buffers.
     */
    synchronized void advance(int buffers)
    {
        final long target = written + buffers;
        granted = target;
        notifyAll();
        final long deadline = System.nanoTime() + TIMEOUT_NANOS;
        while (!closed && (written < target || entered <= target))
        {
            if (System.nanoTime() > deadline)
            {
                throw new IllegalStateException("Mixing thread stalled at " + written + " buffers");
            }
            await();
        }
    }

    /**
     * Makes the writes return at once, or wait for the grants again.
     *
     * @param free {@code true} for free running.
     */
    synchronized void setFreeRunning(boolean free)
    {
        freeRunning = free;
        granted = written;
        notifyAll();
    }

    /**
     * Returns the buffers written.
     *
     * @return The writes completed.
     */
    synchronized long getWritten()
    {
        return written;
    }

    /**
     * Returns a copy of the last buffer written.
     *
     * @return The bytes.
     */
    synchronized byte[] getLast()
    {
        return last.clone();
    }

    /**
     * Tells whether the output was closed.
     *
     * @return {@code true} once closed.
     */
    synchronized boolean isClosed()
    {
        return closed;
    }

    @Override
    public synchronized void write(byte[] data, int length)
    {
        entered++;
        notifyAll();
        while (!closed && !freeRunning && written >= granted)
        {
            await();
        }
        if (!closed)
        {
            if (last.length != length)
            {
                last = new byte[length];
            }
            System.arraycopy(data, 0, last, 0, length);
            written++;
            granted = Math.max(granted, written);
        }
        notifyAll();
    }

    @Override
    public synchronized long getPlayedFrames()
    {
        return written * bufferFrames;
    }

    @Override
    public synchronized void close()
    {
        closed = true;
        notifyAll();
    }

    /**
     * Waits for a notification or a short time.
     */
    private void await()
    {
        try
        {
            wait(WAIT_MILLIS);
        }
        catch (final InterruptedException exception)
        {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(exception);
        }
    }
}
