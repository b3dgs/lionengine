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

import java.io.IOException;
import java.util.Arrays;

/**
 * Content of the {@code fmt } chunk of a WAV file accepted by {@link WavDecoder}: integer PCM, mono or stereo, 8, 16,
 * 24 or 32 bits per sample (ADR-0066).
 *
 * @param format The PCM format of the decoded samples.
 * @param bytesPerSample The bytes of one sample of one channel in the file: 1, 2, 3 or 4.
 */
record WavFormat(PcmFormat format, int bytesPerSample)
{
    /** Format tag of integer PCM. */
    private static final int TAG_PCM = 1;
    /** Format tag of {@code WAVE_FORMAT_EXTENSIBLE}, whose sub-format names the encoding. */
    private static final int TAG_EXTENSIBLE = 0xFFFE;
    /** Bytes of the basic {@code fmt } chunk. */
    private static final int BASIC_BYTES = 16;
    /** Bytes of the extensible {@code fmt } chunk. */
    private static final int EXTENSIBLE_BYTES = 40;
    /** Bytes of the extension read before the sub-format: size, valid bits, channel mask. */
    private static final int EXTENSION_BYTES = 8;
    /** Bytes of the sub-format GUID after its format tag. */
    private static final int GUID_TAIL_BYTES = 14;
    /** The GUID tail shared by the standard sub-formats ({@code KSDATAFORMAT_SUBTYPE_*}). */
    private static final byte[] GUID_TAIL =
    {
        0x00, 0x00, 0x00, 0x00, 0x10, 0x00, (byte) 0x80, 0x00, 0x00, (byte) 0xAA, 0x00, 0x38, (byte) 0x9B, 0x71
    };
    /** Largest supported bytes per sample. */
    private static final int MAX_BYTES_PER_SAMPLE = 4;
    /** Largest sample rate representable by the format. */
    private static final long MAX_RATE = Integer.MAX_VALUE;
    /** Unit of a size in messages. */
    private static final String BYTES = " bytes";
    /** Message of an unsupported encoding. */
    private static final String ERROR_ENCODING = "Unsupported WAV encoding (integer PCM only), format tag: ";

    /**
     * Reads the {@code fmt } chunk, after its size, and skips its unread bytes and its pad byte.
     *
     * @param input The reader, positioned after the size of the chunk.
     * @param size The size of the chunk in bytes, without its pad byte.
     * @return The format.
     * @throws AudioDecodingException If the chunk is too small or describes an unsupported or inconsistent format.
     * @throws java.io.EOFException If the stream ends before the end of the chunk.
     * @throws IOException If reading fails.
     */
    static WavFormat read(WavInput input, long size) throws IOException
    {
        if (size < BASIC_BYTES)
        {
            throw new AudioDecodingException("WAV format chunk too small: " + size + BYTES);
        }
        final int tag = input.readUnsignedShort();
        final int channels = input.readUnsignedShort();
        final long rate = input.readUnsignedInt();
        input.readUnsignedInt();
        final int blockAlign = input.readUnsignedShort();
        final int bits = input.readUnsignedShort();
        long read = BASIC_BYTES;
        if (tag == TAG_EXTENSIBLE)
        {
            readSubFormat(input, size);
            read = EXTENSIBLE_BYTES;
        }
        else if (tag != TAG_PCM)
        {
            throw new AudioDecodingException(ERROR_ENCODING + tag);
        }
        input.skip(size - read + (size & 1L));
        return create(channels, rate, blockAlign, bits);
    }

    /**
     * Reads the extension of a {@code WAVE_FORMAT_EXTENSIBLE} chunk and checks that its sub-format is integer PCM.
     *
     * @param input The reader, positioned after the basic fields.
     * @param size The size of the chunk in bytes.
     * @throws AudioDecodingException If the extension is missing or the sub-format is not integer PCM.
     * @throws IOException If reading fails.
     */
    private static void readSubFormat(WavInput input, long size) throws IOException
    {
        if (size < EXTENSIBLE_BYTES)
        {
            throw new AudioDecodingException("WAV extensible format chunk too small: " + size + BYTES);
        }
        input.skip(EXTENSION_BYTES);
        final int subFormat = input.readUnsignedShort();
        final byte[] tail = new byte[GUID_TAIL_BYTES];
        input.readFully(tail, 0, GUID_TAIL_BYTES);
        if (subFormat != TAG_PCM || !Arrays.equals(GUID_TAIL, tail))
        {
            throw new AudioDecodingException(ERROR_ENCODING + TAG_EXTENSIBLE + ", sub-format " + subFormat);
        }
    }

    /**
     * Creates the format from the fields read.
     *
     * @param channels The channel count.
     * @param rate The sample rate in Hz.
     * @param blockAlign The bytes of one frame.
     * @param bits The bits of one sample.
     * @return The format.
     * @throws AudioDecodingException If a field is unsupported or the frame size is inconsistent.
     */
    private static WavFormat create(int channels, long rate, int blockAlign, int bits)
    {
        final int bytesPerSample = bits / Byte.SIZE;
        final boolean depth = bits % Byte.SIZE == 0 && bytesPerSample >= 1 && bytesPerSample <= MAX_BYTES_PER_SAMPLE;
        if (!depth)
        {
            throw new AudioDecodingException("Unsupported WAV sample depth (8, 16, 24 or 32 bits): " + bits);
        }
        if (channels < PcmFormat.MONO || channels > PcmFormat.STEREO || rate <= 0L || rate > MAX_RATE)
        {
            throw new AudioDecodingException("Unsupported WAV layout: " + channels + " channels, " + rate + " Hz");
        }
        if (blockAlign != channels * bytesPerSample)
        {
            throw new AudioDecodingException("Inconsistent WAV frame size: " + blockAlign + BYTES);
        }
        return new WavFormat(new PcmFormat((int) rate, channels), bytesPerSample);
    }

    /**
     * Returns the bytes of one frame in the file.
     *
     * @return The bytes of one sample of every channel.
     */
    int frameBytes()
    {
        return bytesPerSample * format.channels();
    }
}
