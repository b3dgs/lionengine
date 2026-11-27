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
 * Mixing loop of the mixing thread (ADR-0070): mixes a buffer, then writes it to the output, until the mixer is
 * closed.
 *
 * <p>
 * The output blocks the loop at the pace of the line, so that the line buffer stays full and the latency bounded. The
 * mixing holds the lock of the mixer, the write does not, so that a control waits at most for the mixing of one buffer.
 * </p>
 */
final class MixLoop implements Runnable
{
    /** The mixer. */
    private final SoftwareMixer mixer;
    /** The output. */
    private final MixOutput output;
    /** The mixed bytes, reused for every buffer. */
    private final byte[] bytes;

    /**
     * Creates the loop.
     *
     * @param mixer The mixer.
     * @param output The output.
     */
    MixLoop(SoftwareMixer mixer, MixOutput output)
    {
        this.mixer = mixer;
        this.output = output;
        bytes = new byte[mixer.getBufferBytes()];
    }

    @Override
    public void run()
    {
        while (mixer.mix(bytes))
        {
            output.write(bytes, bytes.length);
        }
    }
}
