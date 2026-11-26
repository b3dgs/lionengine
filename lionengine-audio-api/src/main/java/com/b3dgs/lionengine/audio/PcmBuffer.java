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

/**
 * Fully decoded PCM sound: 16-bit signed samples, interleaved by frame (ADR-0065, REQ-AUDIO-009).
 *
 * <p>
 * The buffer takes over the sample array it is created with, without copying it: the creator, usually an
 * {@link AudioDecoder}, must not keep nor modify it. The samples are read by copy with
 * {@link #readFrames(int, short[], int, int)}, which allocates nothing; the array itself is never exposed. Immutable
 * once created, so a backend may read it from its own thread.
 * </p>
 *
 * @since 11.0.0
 */
public final class PcmBuffer
{
    /** Format of the samples. */
    private final PcmFormat format;
    /** Samples, interleaved by frame. */
    private final short[] samples;
    /** Number of frames. */
    private final int frames;

    /**
     * Creates a buffer, taking over the samples.
     *
     * @param format The format of the samples (never {@code null}).
     * @param samples The samples, interleaved by frame, owned by the buffer from now on (never {@code null}); its
     *            length is a multiple of the channel count.
     * @throws IllegalArgumentException If an argument is {@code null}, or the sample count is not a multiple of the
     *             channel count.
     */
    public PcmBuffer(PcmFormat format, short[] samples)
    {
        if (format == null || samples == null || samples.length % format.channels() != 0)
        {
            throw new IllegalArgumentException("Invalid PCM buffer");
        }
        this.format = format;
        this.samples = samples;
        frames = samples.length / format.channels();
    }

    /**
     * Copies frames into an array, without allocating.
     *
     * @param fromFrame The first frame to copy (from {@code 0}).
     * @param target The array receiving the interleaved samples (never {@code null}).
     * @param offset The index in {@code target} of the first sample written.
     * @param count The number of frames to copy.
     * @throws IllegalArgumentException If {@code target} is {@code null}, a value is negative, or the range goes past
     *             the buffer or the target.
     */
    public void readFrames(int fromFrame, short[] target, int offset, int count)
    {
        final int channels = format.channels();
        final boolean negative = fromFrame < 0 || offset < 0 || count < 0;
        if (target == null || negative || fromFrame > frames - count || offset > target.length - count * channels)
        {
            throw new IllegalArgumentException("Invalid frame range: " + fromFrame + '+' + count);
        }
        System.arraycopy(samples, fromFrame * channels, target, offset, count * channels);
    }

    /**
     * Returns the format.
     *
     * @return The format of the samples.
     */
    public PcmFormat getFormat()
    {
        return format;
    }

    /**
     * Returns the number of frames.
     *
     * @return The frame count (not negative).
     */
    public int getFrames()
    {
        return frames;
    }

    /**
     * Returns the duration.
     *
     * @return The duration in seconds: the frame count divided by the sample rate.
     */
    public double getDuration()
    {
        return (double) frames / format.sampleRate();
    }
}
