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

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Decoder of a raw test format, binding the {@link AudioDecoderContract} until the WAV decoder (ADR-0045, ADR-0065).
 *
 * <p>
 * Format, big-endian: the four ASCII bytes {@code RAWP}, the sample rate ({@code int}), the channel count
 * ({@code byte}), the frame count ({@code int}), then the 16-bit samples ({@code short}), interleaved by frame.
 * Thread-safe.
 * </p>
 */
public final class RawAudioDecoder implements AudioDecoder
{
    /** Magic bytes. */
    private static final byte[] MAGIC = "RAWP".getBytes(StandardCharsets.US_ASCII);

    /**
     * Encodes a buffer in the raw format.
     *
     * @param buffer The buffer.
     * @return The bytes.
     */
    public static byte[] encode(PcmBuffer buffer)
    {
        final PcmFormat format = buffer.getFormat();
        final short[] samples = new short[buffer.getFrames() * format.channels()];
        buffer.readFrames(0, samples, 0, buffer.getFrames());
        final byte[] bytes = new byte[MAGIC.length + Integer.BYTES + 1 + Integer.BYTES + samples.length * Short.BYTES];
        System.arraycopy(MAGIC, 0, bytes, 0, MAGIC.length);
        int index = MAGIC.length;
        index = putInt(bytes, index, format.sampleRate());
        bytes[index++] = (byte) format.channels();
        index = putInt(bytes, index, buffer.getFrames());
        for (final short sample : samples)
        {
            bytes[index++] = (byte) (sample >> Byte.SIZE);
            bytes[index++] = (byte) sample;
        }
        return bytes;
    }

    /**
     * Writes a big-endian {@code int}.
     *
     * @param bytes The bytes.
     * @param index The first index.
     * @param value The value.
     * @return The index after the value.
     */
    private static int putInt(byte[] bytes, int index, int value)
    {
        for (int i = Integer.BYTES - 1; i >= 0; i--)
        {
            bytes[index + Integer.BYTES - 1 - i] = (byte) (value >> i * Byte.SIZE);
        }
        return index + Integer.BYTES;
    }

    /** Frame limit. */
    private final int maxFrames;

    /**
     * Creates a decoder.
     *
     * @param maxFrames The frame limit (positive).
     * @throws IllegalArgumentException If the limit is not positive.
     */
    public RawAudioDecoder(int maxFrames)
    {
        if (maxFrames <= 0)
        {
            throw new IllegalArgumentException("Invalid frame limit: " + maxFrames);
        }
        this.maxFrames = maxFrames;
    }

    @Override
    public PcmBuffer decode(InputStream input) throws IOException
    {
        if (input == null)
        {
            throw new IllegalArgumentException("Input must not be null");
        }
        final DataInputStream data = new DataInputStream(input);
        try
        {
            final byte[] magic = new byte[MAGIC.length];
            data.readFully(magic);
            if (!Arrays.equals(MAGIC, magic))
            {
                throw new AudioDecodingException("Not a raw test sound");
            }
            final PcmFormat format = format(data.readInt(), data.readByte());
            final int frames = data.readInt();
            if (frames < 0 || frames > maxFrames)
            {
                throw new AudioDecodingException("Frame count refused: " + frames + ", limit " + maxFrames);
            }
            final short[] samples = new short[frames * format.channels()];
            for (int i = 0; i < samples.length; i++)
            {
                samples[i] = data.readShort();
            }
            return new PcmBuffer(format, samples);
        }
        catch (final EOFException exception)
        {
            throw new AudioDecodingException("Truncated raw test sound", exception);
        }
    }

    /**
     * Creates the format of the header.
     *
     * @param sampleRate The sample rate read.
     * @param channels The channel count read.
     * @return The format.
     * @throws AudioDecodingException If the values are invalid.
     */
    private static PcmFormat format(int sampleRate, int channels)
    {
        try
        {
            return new PcmFormat(sampleRate, channels);
        }
        catch (final IllegalArgumentException exception)
        {
            throw new AudioDecodingException("Invalid raw test format", exception);
        }
    }
}
