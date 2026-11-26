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

import java.io.EOFException;
import java.io.IOException;

/**
 * Frames of the data chunk of a WAV file, decoded on demand to 16 bits (ADR-0066, ADR-0068).
 *
 * <p>
 * Created by {@link WavDecoder} once the header is read, positioned at the first sample. Reads through a scratch array
 * of {@link #READ_BYTES} bytes, created with the source, so reading allocates nothing. Not thread-safe.
 * </p>
 */
final class WavSource implements PcmSource
{
    /** Bytes read from the stream at once. */
    static final int READ_BYTES = 4_096;
    /** Offset that centers an unsigned 8-bit sample. */
    private static final int UNSIGNED_OFFSET = 128;
    /** Mask of an unsigned byte. */
    private static final int BYTE = 0xFF;
    /** Error message of truncated data. */
    private static final String ERROR_TRUNCATED = "Truncated WAV data";

    /**
     * Converts one sample of the file to 16 bits.
     *
     * @param bytes The bytes of the file.
     * @param index The index of the first byte of the sample.
     * @param bytesPerSample The bytes of the sample: 1, 2, 3 or 4.
     * @return The 16-bit sample.
     */
    private static short toShort(byte[] bytes, int index, int bytesPerSample)
    {
        final short sample;
        if (bytesPerSample == 1)
        {
            sample = (short) (((bytes[index] & BYTE) - UNSIGNED_OFFSET) << Byte.SIZE);
        }
        else
        {
            final int high = index + bytesPerSample - 1;
            sample = (short) (bytes[high] << Byte.SIZE | bytes[high - 1] & BYTE);
        }
        return sample;
    }

    /** The reader of the file. */
    private final WavInput wav;
    /** The format of the file. */
    private final WavFormat format;
    /** The channel count. */
    private final int channels;
    /** Scratch bytes of the samples. */
    private final byte[] bytes;
    /** The number of samples read from the scratch bytes at once. */
    private final int samplesPerRead;
    /** The frames of the data chunk. */
    private final long frames;
    /** The frames not read yet. */
    private long left;

    /**
     * Creates the source.
     *
     * @param wav The reader, positioned at the first sample.
     * @param format The format of the samples.
     * @param frames The frames of the data chunk.
     */
    WavSource(WavInput wav, WavFormat format, long frames)
    {
        super();

        this.wav = wav;
        this.format = format;
        this.frames = frames;
        channels = format.format().channels();
        samplesPerRead = READ_BYTES / format.bytesPerSample();
        bytes = new byte[samplesPerRead * format.bytesPerSample()];
        left = frames;
    }

    /**
     * Returns the frames of the data chunk.
     *
     * @return The frame count given by the header.
     */
    long getFrames()
    {
        return frames;
    }

    @Override
    public PcmFormat getFormat()
    {
        return format.format();
    }

    @Override
    public int read(short[] target, int offset, int count) throws IOException
    {
        final boolean negative = offset < 0 || count < 0;
        if (target == null || negative || offset > target.length - count * channels)
        {
            throw new IllegalArgumentException("Invalid frame range: " + offset + '+' + count);
        }
        final int read = (int) Math.min(count, left);
        final int samples = read * channels;
        final int bytesPerSample = format.bytesPerSample();
        int sample = 0;
        try
        {
            while (sample < samples)
            {
                final int length = Math.min(samplesPerRead, samples - sample);
                wav.readFully(bytes, 0, length * bytesPerSample);
                final int base = offset + sample;
                for (int i = 0; i < length; i++)
                {
                    target[base + i] = toShort(bytes, i * bytesPerSample, bytesPerSample);
                }
                sample += length;
            }
        }
        catch (final EOFException exception)
        {
            left = 0L;
            throw new AudioDecodingException(ERROR_TRUNCATED, exception);
        }
        left -= read;
        return read;
    }
}
