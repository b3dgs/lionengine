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

/**
 * Invalid or unsupported image content (ADR-0007, ADR-0026, ADR-0033, REQ-SPRITE-009).
 *
 * <p>
 * The decoding exception of {@code graphics}: the bytes were read, but they are not a valid image of the decoder's
 * format, they are truncated or corrupted, or they use a feature that the decoder rejects, such as an interlaced or
 * animated PNG. A missing or unreadable resource is not a decoding error: it is the resource exception of
 * {@code resources}. Immutable; may be thrown on any thread, since decoding may run on loading workers. Not meant to
 * be serialized: no {@code serialVersionUID} is declared (Sonar S4926).
 * </p>
 *
 * @since 11.0.0
 */
@SuppressWarnings("serial")
public final class ImageDecodingException extends RuntimeException
{
    /**
     * Creates the exception.
     *
     * @param message The description of the problem (never {@code null}).
     */
    public ImageDecodingException(String message)
    {
        super(message);
    }

    /**
     * Creates the exception with its cause.
     *
     * @param message The description of the problem (never {@code null}).
     * @param cause The underlying failure, for example a decompression error, {@code null} if none.
     */
    public ImageDecodingException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
