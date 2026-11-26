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

/**
 * Audio decoder contract: turns the bytes of an encoded sound into a new {@link PcmBuffer} (ADR-0019, ADR-0027,
 * ADR-0065, REQ-AUDIO-009).
 *
 * <p>
 * PCM WAV is decoded by project code on every backend; OGG Vorbis by {@code audio-codec-ogg} on desktop and by the
 * platform on Android. Decoders are registered explicitly, never discovered (ADR-0020). Every implementation passes the
 * contract suite {@code AudioDecoderContract} of the {@code audio-api} test JAR (ADR-0045).
 * </p>
 *
 * <p>
 * Limit: every decoder refuses, with an {@link AudioDecodingException} and before allocating the samples, a sound
 * above the frame limit given when it is created, so a small file cannot make it allocate an unbounded amount of
 * memory. Other sample depths are converted to 16 bits.
 * </p>
 *
 * <p>
 * Ownership: the caller opens the stream, for example through {@code Resources}, and closes it; the decoder reads it
 * and never closes it. The returned buffer is new and belongs to the caller. Threads: implementations are thread-safe
 * and hold no engine state, so that loading workers can decode several sounds at once (ADR-0019).
 * </p>
 *
 * @since 11.0.0
 */
@FunctionalInterface
public interface AudioDecoder
{
    /**
     * Decodes one sound entirely.
     *
     * @param input The stream of the encoded sound, positioned at its first byte; read but not closed (never
     *            {@code null}).
     * @return The samples of the sound, in a new buffer owned by the caller.
     * @throws IOException If reading the stream fails; the caller reports it as an inaccessible resource (ADR-0007).
     * @throws AudioDecodingException If the bytes are not a valid sound of the decoder's format, are truncated or
     *             corrupted, use a feature the decoder rejects, or the sound is above its frame limit.
     * @throws IllegalArgumentException If {@code input} is {@code null}.
     */
    PcmBuffer decode(InputStream input) throws IOException;
}
