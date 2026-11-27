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
package com.b3dgs.lionengine.audio.codec.ogg;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Arrays;

import com.b3dgs.lionengine.audio.AudioDecoder;
import com.b3dgs.lionengine.audio.AudioDecoderContract;
import com.b3dgs.lionengine.audio.PcmBuffer;
import com.b3dgs.lionengine.audio.PcmSource;

/**
 * Binding of the {@link AudioDecoderContract} on {@link OggVorbisDecoder} (ADR-0045, ADR-0073, REQ-AUDIO-002,
 * REQ-AUDIO-009).
 *
 * <p>
 * The sound is the mono file at 22,050 Hz. The expected samples are those of its streaming source, read in pieces of
 * 1,000 frames, so the suite also checks that both paths decode the same samples; {@link OggVorbisDecoderTest}
 * compares them with the decoding of {@code libvorbisfile}.
 * </p>
 */
final class OggVorbisDecoderContractTest extends AudioDecoderContract
{
    /** Frames read at once to build the expected samples. */
    private static final int PIECE = 1_000;
    /** The sound. */
    private static final byte[] SOUND = OggFiles.ogg(OggFiles.MONO_22050);
    /** The expected samples. */
    private static final PcmBuffer EXPECTED = stream(SOUND);

    /**
     * Decodes a sound through its streaming source.
     *
     * @param sound The sound.
     * @return Its samples.
     */
    static PcmBuffer stream(byte[] sound)
    {
        try
        {
            final PcmSource source = new OggVorbisDecoder().open(new ByteArrayInputStream(sound));
            final int channels = source.getFormat().channels();
            short[] samples = new short[PIECE * channels];
            int frames = 0;
            int read;
            do
            {
                if (samples.length < (frames + PIECE) * channels)
                {
                    samples = Arrays.copyOf(samples, samples.length * 2);
                }
                read = source.read(samples, frames * channels, PIECE);
                frames += read;
            }
            while (read == PIECE);
            return new PcmBuffer(source.getFormat(), Arrays.copyOf(samples, frames * channels));
        }
        catch (final IOException exception)
        {
            throw new UncheckedIOException(exception);
        }
    }

    @Override
    protected AudioDecoder createDecoder()
    {
        return new OggVorbisDecoder();
    }

    @Override
    protected AudioDecoder createDecoder(int maxFrames)
    {
        return new OggVorbisDecoder(maxFrames);
    }

    @Override
    protected byte[] getSound()
    {
        return SOUND.clone();
    }

    @Override
    protected PcmBuffer getExpected()
    {
        return EXPECTED;
    }
}
