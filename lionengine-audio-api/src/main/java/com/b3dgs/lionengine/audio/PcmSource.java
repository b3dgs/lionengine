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

/**
 * Decoded frames of one sound, read in order from the start by a streaming decoder (ADR-0027, ADR-0068,
 * REQ-AUDIO-006, REQ-AUDIO-009).
 *
 * <p>
 * Returned by {@link AudioStreamDecoder#open(java.io.InputStream)}, positioned at the first frame. Each
 * {@link #read(short[], int, int)} decodes the next frames into an array of the caller, as 16-bit signed samples
 * interleaved by frame, so that memory stays bounded whatever the length of the sound. The source reads the stream it
 * was opened on and never closes it: the owner of the stream closes it once the source is no longer read.
 * </p>
 *
 * <p>
 * Threads: not thread-safe; one thread reads it at a time, the backend's while a long sound plays. A source touches
 * nothing but its stream and its own scratch memory, and reading allocates nothing.
 * </p>
 *
 * @since 11.0.0
 */
public interface PcmSource
{
    /**
     * Returns the format of the frames.
     *
     * @return The format, known from the opening.
     */
    PcmFormat getFormat();

    /**
     * Decodes the next frames into an array, without allocating.
     *
     * @param target The array receiving the interleaved samples (never {@code null}).
     * @param offset The index in {@code target} of the first sample written.
     * @param frames The number of frames wanted (not negative).
     * @return The number of frames written: {@code frames} unless the sound ends first, then fewer, and {@code 0} once
     *         it has ended.
     * @throws IOException If reading the stream fails.
     * @throws AudioDecodingException If the data is truncated or corrupted.
     * @throws IllegalArgumentException If {@code target} is {@code null}, a value is negative, or the range goes past
     *             the target.
     */
    int read(short[] target, int offset, int frames) throws IOException;
}
