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
 * Binding of the {@link AudioDecoderContract} on {@link WavDecoder} (ADR-0045, ADR-0066, REQ-AUDIO-009).
 */
final class WavDecoderContractTest extends AudioDecoderContract
{
    /** Expected sound: three stereo frames. */
    private static final PcmBuffer EXPECTED = new PcmBuffer(new PcmFormat(22_050, PcmFormat.STEREO), new short[]
    {
        0, 1, Short.MAX_VALUE, Short.MIN_VALUE, -2, 300
    });

    @Override
    protected AudioDecoder createDecoder()
    {
        return new WavDecoder();
    }

    @Override
    protected AudioDecoder createDecoder(int maxFrames)
    {
        return new WavDecoder(maxFrames);
    }

    @Override
    protected byte[] getSound()
    {
        return WavWriter.write(EXPECTED, 16);
    }

    @Override
    protected PcmBuffer getExpected()
    {
        return EXPECTED;
    }
}
