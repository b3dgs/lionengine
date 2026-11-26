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
 * Format of PCM audio data: sample rate and channel count; samples are always 16-bit signed, interleaved by frame
 * (ADR-0065, REQ-AUDIO-009).
 *
 * <p>
 * A frame holds one sample per channel: left then right in stereo. Immutable and thread-safe.
 * </p>
 *
 * @param sampleRate The sample rate in Hz (positive).
 * @param channels The number of channels: {@link #MONO} or {@link #STEREO}.
 * @since 11.0.0
 */
public record PcmFormat(int sampleRate, int channels)
{
    /** One channel. */
    public static final int MONO = 1;
    /** Two channels, left then right. */
    public static final int STEREO = 2;

    /**
     * Creates a format.
     *
     * @param sampleRate The sample rate in Hz (positive).
     * @param channels The number of channels: {@link #MONO} or {@link #STEREO}.
     * @throws IllegalArgumentException If the sample rate is not positive or the channel count is not 1 or 2.
     */
    public PcmFormat
    {
        if (sampleRate <= 0 || channels < MONO || channels > STEREO)
        {
            throw new IllegalArgumentException("Invalid PCM format: " + sampleRate + " Hz, " + channels + " channels");
        }
    }
}
