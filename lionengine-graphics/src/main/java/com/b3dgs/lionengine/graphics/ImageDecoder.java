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
package com.b3dgs.lionengine.graphics;

import java.io.IOException;
import java.io.InputStream;

/**
 * Image decoder contract: turns the bytes of an encoded image into a new {@link PixelBuffer} (ADR-0019, ADR-0026,
 * ADR-0033, REQ-SPRITE-009).
 *
 * <p>
 * {@link PngDecoder} decodes PNG identically on every platform. Other formats come from optional decoder modules
 * ({@code graphics-imageio} on desktop, {@code graphics-android}), which the application registers explicitly: decoders
 * are never discovered (ADR-0020). Every implementation passes the contract suite {@code ImageDecoderContract} of the
 * {@code graphics} test JAR (ADR-0045).
 * </p>
 *
 * <p>
 * Ownership: the caller opens the stream, for example through {@code Resources}, and closes it; the decoder reads it
 * and never closes it. The returned buffer is new and belongs to the caller; the decoder keeps no reference to it.
 * </p>
 *
 * <p>
 * Threads: implementations are thread-safe and hold no engine state, so that loading workers can decode several
 * images at once (ADR-0019).
 * </p>
 *
 * @since 11.0.0
 */
@FunctionalInterface
public interface ImageDecoder
{
    /**
     * Decodes one image.
     *
     * @param input The stream of the encoded image, positioned at its first byte; read but not closed (never
     *            {@code null}).
     * @return The pixels of the image, in a new buffer owned by the caller.
     * @throws IOException If reading the stream fails; the caller reports it as an inaccessible resource (ADR-0007).
     * @throws ImageDecodingException If the bytes are not a valid image of the decoder's format, are truncated or
     *             corrupted, or use a feature the decoder rejects.
     * @throws IllegalArgumentException If {@code input} is {@code null}.
     */
    PixelBuffer decode(InputStream input) throws IOException;
}
