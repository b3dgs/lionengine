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

/**
 * Destination of the mixed audio buffers: the Java Sound line, or a test double (ADR-0070).
 *
 * <p>
 * Called by the mixing thread only, except {@link #close()}, called on the runtime thread while the mixing thread may
 * be blocked in {@link #write(byte[], int)}.
 * </p>
 */
interface MixOutput
{
    /**
     * Writes a mixed buffer, blocking until the output accepted it.
     *
     * @param data The samples: 16-bit signed little-endian, interleaved stereo.
     * @param length The number of bytes to write from the start of {@code data}.
     */
    void write(byte[] data, int length);

    /**
     * Returns the number of frames played by the output since it was opened.
     *
     * @return The frames played, as far as the output knows.
     */
    long getPlayedFrames();

    /**
     * Releases the output; a blocked {@link #write(byte[], int)} returns.
     */
    void close();
}
