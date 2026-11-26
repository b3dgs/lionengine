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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test {@link PcmFormat} and {@link PcmBuffer} (ADR-0065, REQ-AUDIO-009).
 */
final class PcmBufferTest
{
    /** Stereo format at 10 Hz, for round durations. */
    private static final PcmFormat STEREO = new PcmFormat(10, PcmFormat.STEREO);

    /**
     * A format has a positive rate and one or two channels.
     */
    @Test
    void testFormat()
    {
        assertEquals(44_100, new PcmFormat(44_100, PcmFormat.MONO).sampleRate());
        assertThrows(IllegalArgumentException.class, () -> new PcmFormat(0, PcmFormat.MONO));
        assertThrows(IllegalArgumentException.class, () -> new PcmFormat(44_100, 0));
        assertThrows(IllegalArgumentException.class, () -> new PcmFormat(44_100, 3));
    }

    /**
     * Frames, duration and the copy of a range of frames.
     */
    @Test
    void testFrames()
    {
        final PcmBuffer buffer = new PcmBuffer(STEREO, new short[]
        {
            1, 2, 3, 4, 5, 6, 7, 8
        });
        final short[] target = new short[6];

        buffer.readFrames(1, target, 2, 2);

        assertEquals(4, buffer.getFrames());
        assertEquals(0.4, buffer.getDuration(), 1e-12);
        assertEquals(STEREO, buffer.getFormat());
        assertArrayEquals(new short[]
        {
            0, 0, 3, 4, 5, 6
        }, target);
    }

    /**
     * Invalid buffers and ranges are refused.
     */
    @Test
    void testInvalid()
    {
        final PcmBuffer buffer = new PcmBuffer(STEREO, new short[4]);
        final short[] target = new short[4];
        final short[] odd = new short[3];

        assertThrows(IllegalArgumentException.class, () -> new PcmBuffer(null, target));
        assertThrows(IllegalArgumentException.class, () -> new PcmBuffer(STEREO, null));
        assertThrows(IllegalArgumentException.class, () -> new PcmBuffer(STEREO, odd));
        assertThrows(IllegalArgumentException.class, () -> buffer.readFrames(0, null, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> buffer.readFrames(-1, target, 0, 1));
        assertThrows(IllegalArgumentException.class, () -> buffer.readFrames(0, target, -1, 1));
        assertThrows(IllegalArgumentException.class, () -> buffer.readFrames(0, target, 0, -1));
        assertThrows(IllegalArgumentException.class, () -> buffer.readFrames(1, target, 0, 2));
        assertThrows(IllegalArgumentException.class, () -> buffer.readFrames(0, target, 2, 2));
    }
}
