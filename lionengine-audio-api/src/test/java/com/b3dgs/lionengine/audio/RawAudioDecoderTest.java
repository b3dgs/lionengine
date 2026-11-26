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

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.junit.jupiter.api.Test;

/**
 * Binding of the {@link AudioDecoderContract} on {@link RawAudioDecoder} (ADR-0045, REQ-AUDIO-009).
 */
final class RawAudioDecoderTest extends AudioDecoderContract
{
    /** Expected sound: three stereo frames. */
    private static final PcmBuffer EXPECTED = new PcmBuffer(new PcmFormat(22_050, PcmFormat.STEREO), new short[]
    {
        0, 1, Short.MAX_VALUE, Short.MIN_VALUE, -2, 300
    });

    @Override
    protected AudioDecoder createDecoder()
    {
        return new RawAudioDecoder(1_000);
    }

    @Override
    protected AudioDecoder createDecoder(int maxFrames)
    {
        return new RawAudioDecoder(maxFrames);
    }

    @Override
    protected byte[] getSound()
    {
        return RawAudioDecoder.encode(EXPECTED);
    }

    @Override
    protected PcmBuffer getExpected()
    {
        return EXPECTED;
    }

    /**
     * An invalid format in the header is a decoding error; a limit must be positive.
     */
    @Test
    void testInvalidHeader()
    {
        final byte[] sound = getSound();
        sound[8] = 3;
        final InputStream input = new ByteArrayInputStream(sound);
        final AudioDecoder decoder = createDecoder();

        assertThrows(AudioDecodingException.class, () -> decoder.decode(input));
        assertThrows(IllegalArgumentException.class, () -> new RawAudioDecoder(0));
    }
}
