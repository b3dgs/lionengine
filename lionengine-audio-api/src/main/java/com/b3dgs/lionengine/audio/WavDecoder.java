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
import java.io.InputStream;

/**
 * Decoder of PCM WAV files, written by the project for every backend (ADR-0027, ADR-0065, ADR-0066, REQ-AUDIO-002,
 * REQ-AUDIO-003).
 *
 * <p>
 * Accepted: a RIFF {@code WAVE} file with one {@code fmt } chunk before its {@code data} chunk; integer PCM (format
 * tag {@code 1}, or {@code WAVE_FORMAT_EXTENSIBLE} with the PCM sub-format); mono or stereo; 8, 16, 24 or 32 bits
 * per sample; any positive sample rate. Other chunks ({@code LIST}, {@code fact}, {@code cue }...) are skipped, with
 * the pad byte of an odd-sized chunk, and nothing after the {@code data} chunk is read. Refused with an
 * {@link AudioDecodingException}: another encoding (floating point, ADPCM, mu-law...), more than two channels, an
 * inconsistent frame size, a {@code data} chunk whose size is not a whole number of frames, missing or truncated
 * chunks, and a sound above the frame limit, checked from the header before the samples are allocated.
 * </p>
 *
 * <p>
 * Samples become 16-bit signed: 8-bit samples (unsigned in WAV) are centered and shifted up by 8 bits; 24-bit and
 * 32-bit samples keep their 16 most significant bits.
 * </p>
 *
 * <p>
 * Threads: thread-safe; the decoder holds only its limit, so loading workers may decode several sounds at once
 * (ADR-0019). The stream is read and never closed.
 * </p>
 *
 * @since 11.0.0
 */
public final class WavDecoder implements AudioDecoder
{
    /**
     * Default frame limit: 1,920,000 frames, 10 s at 192 kHz, the longest short sound (ADR-0033) at the highest common
     * rate, so 7.68 MB of samples at most in stereo (ADR-0066).
     */
    public static final int DEFAULT_MAX_FRAMES = 1_920_000;

    /** Identifier of the RIFF container. */
    private static final String RIFF = "RIFF";
    /** Form type of a WAV file. */
    private static final String WAVE = "WAVE";
    /** Identifier of the format chunk. */
    private static final String FMT = "fmt ";
    /** Identifier of the data chunk. */
    private static final String DATA = "data";
    /** Bytes read from the stream at once. */
    private static final int READ_BYTES = 4_096;
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

    /** Frame limit. */
    private final int maxFrames;

    /**
     * Creates a decoder with the {@link #DEFAULT_MAX_FRAMES} limit.
     */
    public WavDecoder()
    {
        this(DEFAULT_MAX_FRAMES);
    }

    /**
     * Creates a decoder.
     *
     * @param maxFrames The frame limit (positive): a longer sound is refused before its samples are allocated.
     * @throws IllegalArgumentException If the limit is not positive.
     */
    public WavDecoder(int maxFrames)
    {
        super();

        if (maxFrames <= 0)
        {
            throw new IllegalArgumentException("Frame limit not positive: " + maxFrames);
        }
        this.maxFrames = maxFrames;
    }

    /**
     * Returns the frame limit.
     *
     * @return The largest number of frames decoded (positive).
     */
    public int getMaxFrames()
    {
        return maxFrames;
    }

    @Override
    public PcmBuffer decode(InputStream input) throws IOException
    {
        if (input == null)
        {
            throw new IllegalArgumentException("Input must not be null");
        }
        final WavInput wav = new WavInput(input);
        try
        {
            if (!RIFF.equals(wav.readId()))
            {
                throw new AudioDecodingException("Not a WAV file: no RIFF header");
            }
            wav.readUnsignedInt();
            if (!WAVE.equals(wav.readId()))
            {
                throw new AudioDecodingException("Not a WAV file: RIFF form is not WAVE");
            }
            return readChunks(wav);
        }
        catch (final EOFException exception)
        {
            throw new AudioDecodingException(ERROR_TRUNCATED, exception);
        }
    }

    /**
     * Reads the chunks until the data chunk, and decodes its samples.
     *
     * @param wav The reader, positioned after the RIFF header.
     * @return The samples.
     * @throws AudioDecodingException If the format is missing, unsupported or inconsistent, or the sound is too long.
     * @throws EOFException If the stream ends before the samples.
     * @throws IOException If reading fails.
     */
    private PcmBuffer readChunks(WavInput wav) throws IOException
    {
        WavFormat format = null;
        while (true)
        {
            final String id = wav.readId();
            final long size = wav.readUnsignedInt();
            if (DATA.equals(id))
            {
                if (format == null)
                {
                    throw new AudioDecodingException("WAV data chunk before its format chunk");
                }
                return readSamples(wav, format, size);
            }
            if (FMT.equals(id))
            {
                format = WavFormat.read(wav, size);
            }
            else
            {
                wav.skip(size + (size & 1L));
            }
        }
    }

    /**
     * Reads and converts the samples of the data chunk.
     *
     * @param wav The reader, positioned at the first byte of the samples.
     * @param format The format of the samples.
     * @param size The size of the data chunk in bytes.
     * @return The samples.
     * @throws AudioDecodingException If the size is not a whole number of frames, or the sound is above the limit.
     * @throws EOFException If the stream ends before the last sample.
     * @throws IOException If reading fails.
     */
    private PcmBuffer readSamples(WavInput wav, WavFormat format, long size) throws IOException
    {
        final int frameBytes = format.frameBytes();
        if (size % frameBytes != 0L)
        {
            throw new AudioDecodingException("WAV data size " + size + " not a multiple of the frame size "
                                             + frameBytes);
        }
        final long frames = size / frameBytes;
        if (frames > maxFrames)
        {
            throw new AudioDecodingException("WAV sound of " + frames + " frames refused, limit " + maxFrames);
        }
        final int bytesPerSample = format.bytesPerSample();
        final short[] samples = new short[(int) frames * format.format().channels()];
        final int samplesPerRead = READ_BYTES / bytesPerSample;
        final byte[] bytes = new byte[samplesPerRead * bytesPerSample];
        int sample = 0;
        while (sample < samples.length)
        {
            final int count = Math.min(samplesPerRead, samples.length - sample);
            wav.readFully(bytes, 0, count * bytesPerSample);
            for (int i = 0; i < count; i++)
            {
                samples[sample + i] = toShort(bytes, i * bytesPerSample, bytesPerSample);
            }
            sample += count;
        }
        return new PcmBuffer(format.format(), samples);
    }
}
