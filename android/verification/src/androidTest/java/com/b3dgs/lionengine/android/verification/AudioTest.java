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
package com.b3dgs.lionengine.android.verification;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

import java.io.ByteArrayInputStream;

import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.b3dgs.lionengine.audio.AudioDecoder;
import com.b3dgs.lionengine.audio.AudioDecodingException;
import com.b3dgs.lionengine.audio.PcmBuffer;
import com.b3dgs.lionengine.audio.PcmFormat;

/**
 * Runs {@code audio-api} on Android API 21 (ADR-0044, ADR-0065): the PCM format and buffer, and a decoder throwing
 * the decoding exception. Expected values are the ones of the JVM tests (PcmBufferTest).
 */
@RunWith(AndroidJUnit4.class)
public final class AudioTest
{
    /** Stereo format at 10 Hz, for round durations. */
    private static final PcmFormat STEREO = new PcmFormat(10, PcmFormat.STEREO);

    /**
     * Creates the test.
     */
    public AudioTest()
    {
        super();
    }

    /**
     * Frames, duration and the copy of a range of frames; invalid formats and ranges are refused.
     */
    @Test
    public void testPcm()
    {
        final PcmBuffer buffer = new PcmBuffer(STEREO, new short[]
        {
            1, 2, 3, 4, 5, 6, 7, 8
        });
        final short[] target = new short[6];

        buffer.readFrames(1, target, 2, 2);

        assertEquals(4, buffer.getFrames());
        assertEquals(0.4, buffer.getDuration(), 1e-12);
        assertArrayEquals(new short[]
        {
            0, 0, 3, 4, 5, 6
        }, target);
        assertThrows(IllegalArgumentException.class, () -> new PcmFormat(44_100, 3));
        assertThrows(IllegalArgumentException.class, () -> buffer.readFrames(1, target, 0, 4));
    }

    /**
     * A decoder is a functional interface; its decoding exception keeps its message.
     */
    @Test
    public void testDecoder()
    {
        final AudioDecoder decoder = input ->
        {
            throw new AudioDecodingException("Not a sound");
        };
        final ByteArrayInputStream input = new ByteArrayInputStream(new byte[0]);

        final AudioDecodingException exception = assertThrows(AudioDecodingException.class,
                                                              () -> decoder.decode(input));

        assertEquals("Not a sound", exception.getMessage());
    }
}
