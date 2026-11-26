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
 * Failure to decode audio data: invalid, truncated or unsupported bytes, or a sound above the limit of the decoder
 * (ADR-0007, ADR-0037, ADR-0065).
 *
 * @since 11.0.0
 */
public final class AudioDecodingException extends RuntimeException
{
    /** Serial version. */
    private static final long serialVersionUID = 1L;

    /**
     * Creates the exception.
     *
     * @param message The description of the failure, naming what was refused.
     */
    public AudioDecodingException(String message)
    {
        super(message);
    }

    /**
     * Creates the exception with its cause.
     *
     * @param message The description of the failure, naming what was refused.
     * @param cause The cause.
     */
    public AudioDecodingException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
