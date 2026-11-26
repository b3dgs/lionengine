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
 * Frames of a streamed voice, pulled by an {@link AudioBackend} as it plays them (ADR-0018, ADR-0068, REQ-AUDIO-006,
 * REQ-AUDIO-007, REQ-AUDIO-009).
 *
 * <p>
 * The engine gives a stream to {@link AudioBackend#play(PcmStream)}; the backend calls {@link #read(short[], int, int)}
 * from its own thread whenever it needs frames, until the stream returns fewer frames than asked, which is the end of
 * the sound. The stream decodes ahead into a bounded buffer of chunks, so its memory stays bounded. It never throws for
 * a failure of its data: a failure ends the stream, and the engine reports it on the runtime thread at the end of the
 * voice.
 * </p>
 *
 * <p>
 * Threads: the backend reads a stream from one thread at a time, only after {@code play} returned and until the voice
 * ends, is stopped or the backend is closed. Reading touches nothing but the stream's own buffer and decoder, never
 * simulation state, allocates nothing, and posts nothing: the end is posted by the backend.
 * </p>
 *
 * @since 11.0.0
 */
public interface PcmStream
{
    /**
     * Returns the format of the frames.
     *
     * @return The format, fixed for the life of the stream.
     */
    PcmFormat getFormat();

    /**
     * Copies the next frames into an array, without allocating.
     *
     * @param target The array receiving the interleaved samples (never {@code null}).
     * @param offset The index in {@code target} of the first sample written.
     * @param frames The number of frames wanted (not negative).
     * @return The number of frames written: {@code frames} until the end of the sound, fewer at its end, then
     *         {@code 0}.
     * @throws IllegalArgumentException If {@code target} is {@code null}, a value is negative, or the range goes past
     *             the target.
     */
    int read(short[] target, int offset, int frames);
}
