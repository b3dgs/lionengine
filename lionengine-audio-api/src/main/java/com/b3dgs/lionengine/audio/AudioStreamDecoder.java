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
 * Streaming audio decoder contract: decodes a sound chunk by chunk instead of entirely, for the long sounds
 * (ADR-0027, ADR-0033, ADR-0065, ADR-0068, REQ-AUDIO-006, REQ-AUDIO-009).
 *
 * <p>
 * {@link #open(InputStream)} reads the header of the sound and returns a {@link PcmSource} that decodes the frames on
 * demand, so a long sound never needs its whole samples in memory and has no length limit. A streaming decoder also
 * decodes a sound entirely with {@link #decode(InputStream)}, within the frame limit of the {@link AudioDecoder}
 * contract, for a long sound fully decoded on request. {@link WavDecoder} implements both; the OGG Vorbis decoder of
 * {@code audio-codec-ogg} comes later and registers the same way. Decoders are registered explicitly, never discovered
 * (ADR-0020).
 * </p>
 *
 * <p>
 * Ownership: the caller opens the stream and closes it once the source is no longer read; the decoder and its sources
 * never close it. Threads: the decoder is thread-safe and holds no engine state; each source is independent and read
 * by one thread at a time, which may be a backend thread.
 * </p>
 *
 * @since 11.0.0
 */
public interface AudioStreamDecoder extends AudioDecoder
{
    /**
     * Opens a sound for streaming: reads and checks its header, and no sample yet.
     *
     * @param input The stream of the encoded sound, positioned at its first byte; read by the source and never closed
     *            (never {@code null}).
     * @return A new source positioned at the first frame.
     * @throws IOException If reading the stream fails; the caller reports it as an inaccessible resource (ADR-0007).
     * @throws AudioDecodingException If the header is not a valid sound of the decoder's format, is truncated, or uses
     *             a feature the decoder rejects.
     * @throws IllegalArgumentException If {@code input} is {@code null}.
     */
    PcmSource open(InputStream input) throws IOException;
}
