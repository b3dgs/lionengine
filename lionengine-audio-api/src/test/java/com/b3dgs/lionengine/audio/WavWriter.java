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

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Writer of WAV files for tests, published in the {@code audio-api} test JAR (ADR-0045, ADR-0066).
 *
 * <p>
 * Writes a RIFF {@code WAVE} file of integer PCM with the depth asked: 16-bit samples as given, 8-bit samples from
 * their high byte, 24-bit and 32-bit samples with the given value in their 16 most significant bits and a filler in
 * the bits below, so that {@link WavDecoder} must drop it. A file can carry an extra chunk before its format, use the
 * extensible format tag, or end with bytes after its data.
 * </p>
 */
public final class WavWriter
{
    /** Format tag of integer PCM. */
    public static final int TAG_PCM = 1;
    /** Format tag of IEEE floating point samples. */
    public static final int TAG_FLOAT = 3;
    /** Format tag of the extensible format. */
    public static final int TAG_EXTENSIBLE = 0xFFFE;
    /** Bytes of the basic format chunk. */
    private static final int FMT_BYTES = 16;
    /** Bytes of the extensible format chunk. */
    private static final int EXTENSIBLE_BYTES = 40;
    /** Bytes of the extension of the extensible format chunk. */
    private static final int EXTENSION_BYTES = 22;
    /** The GUID tail of the standard sub-formats. */
    private static final byte[] GUID_TAIL =
    {
        0x00, 0x00, 0x00, 0x00, 0x10, 0x00, (byte) 0x80, 0x00, 0x00, (byte) 0xAA, 0x00, 0x38, (byte) 0x9B, 0x71
    };
    /** Filler of the low byte of 24-bit and 32-bit samples. */
    private static final int FILLER = 0x5A;

    /**
     * Writes a WAV file of the samples of a buffer.
     *
     * @param buffer The samples.
     * @param bits The depth in the file: 8, 16, 24 or 32.
     * @return The bytes of the file.
     */
    public static byte[] write(PcmBuffer buffer, int bits)
    {
        return new WavWriter(buffer, bits).toBytes();
    }

    /** The samples. */
    private final short[] samples;
    /** The format. */
    private final PcmFormat format;
    /** The depth in bits. */
    private final int bits;
    /** The format tag. */
    private int tag = TAG_PCM;
    /** The sub-format tag of the extensible format. */
    private int subFormat = TAG_PCM;
    /** Name of the chunk written before the format, {@code null} for none. */
    private String extraChunk;
    /** Bytes of the extra chunk. */
    private int extraBytes;
    /** Bytes written after the data chunk. */
    private int trailingBytes;
    /** {@code true} to write the data chunk before the format chunk. */
    private boolean dataFirst;

    /**
     * Creates a writer.
     *
     * @param buffer The samples.
     * @param bits The depth in the file: 8, 16, 24 or 32.
     */
    public WavWriter(PcmBuffer buffer, int bits)
    {
        format = buffer.getFormat();
        samples = new short[buffer.getFrames() * format.channels()];
        buffer.readFrames(0, samples, 0, buffer.getFrames());
        this.bits = bits;
    }

    /**
     * Sets the format tag; {@link #TAG_EXTENSIBLE} writes the extensible chunk with a sub-format.
     *
     * @param formatTag The format tag.
     * @param subFormatTag The sub-format tag of the extensible format.
     * @return This writer.
     */
    public WavWriter setTag(int formatTag, int subFormatTag)
    {
        tag = formatTag;
        subFormat = subFormatTag;
        return this;
    }

    /**
     * Writes an extra chunk of zeros before the format chunk.
     *
     * @param name The four ASCII characters of the chunk.
     * @param bytes The size of the chunk, odd sizes get their pad byte.
     * @return This writer.
     */
    public WavWriter setExtraChunk(String name, int bytes)
    {
        extraChunk = name;
        extraBytes = bytes;
        return this;
    }

    /**
     * Writes bytes after the data chunk.
     *
     * @param bytes The number of bytes.
     * @return This writer.
     */
    public WavWriter setTrailingBytes(int bytes)
    {
        trailingBytes = bytes;
        return this;
    }

    /**
     * Writes the data chunk before the format chunk.
     *
     * @return This writer.
     */
    public WavWriter setDataFirst()
    {
        dataFirst = true;
        return this;
    }

    /**
     * Writes the file.
     *
     * @return The bytes of the file.
     */
    public byte[] toBytes()
    {
        final ByteArrayOutputStream body = new ByteArrayOutputStream();
        body.writeBytes(ascii("WAVE"));
        if (extraChunk != null)
        {
            chunk(body, extraChunk, new byte[extraBytes]);
        }
        if (dataFirst)
        {
            chunk(body, "data", data());
            chunk(body, "fmt ", fmt());
        }
        else
        {
            chunk(body, "fmt ", fmt());
            chunk(body, "data", data());
        }
        body.writeBytes(new byte[trailingBytes]);
        final ByteArrayOutputStream file = new ByteArrayOutputStream();
        chunk(file, "RIFF", body.toByteArray());
        return file.toByteArray();
    }

    /**
     * Returns the content of the format chunk.
     *
     * @return The bytes.
     */
    private byte[] fmt()
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final int blockAlign = format.channels() * bits / Byte.SIZE;
        putShort(out, tag);
        putShort(out, format.channels());
        putInt(out, format.sampleRate());
        putInt(out, format.sampleRate() * blockAlign);
        putShort(out, blockAlign);
        putShort(out, bits);
        if (tag == TAG_EXTENSIBLE)
        {
            putShort(out, EXTENSION_BYTES);
            putShort(out, bits);
            putInt(out, format.channels() == 1 ? 0x4 : 0x3);
            putShort(out, subFormat);
            out.writeBytes(GUID_TAIL);
        }
        final byte[] bytes = out.toByteArray();
        if (bytes.length != (tag == TAG_EXTENSIBLE ? EXTENSIBLE_BYTES : FMT_BYTES))
        {
            throw new IllegalStateException("Format chunk of " + bytes.length + " bytes");
        }
        return bytes;
    }

    /**
     * Returns the content of the data chunk.
     *
     * @return The bytes.
     */
    private byte[] data()
    {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        for (final short sample : samples)
        {
            switch (bits)
            {
                case 8:
                    out.write((sample >> Byte.SIZE) + 128);
                    break;
                case 16:
                    putShort(out, sample);
                    break;
                case 24:
                    out.write(FILLER);
                    putShort(out, sample);
                    break;
                case 32:
                    out.write(FILLER);
                    out.write(FILLER);
                    putShort(out, sample);
                    break;
                default:
                    throw new IllegalArgumentException("Depth: " + bits);
            }
        }
        return out.toByteArray();
    }

    /**
     * Writes a chunk and its pad byte.
     *
     * @param out The output.
     * @param name The four ASCII characters of the chunk.
     * @param content The content.
     */
    private static void chunk(ByteArrayOutputStream out, String name, byte[] content)
    {
        out.writeBytes(ascii(name));
        putInt(out, content.length);
        out.writeBytes(content);
        if ((content.length & 1) != 0)
        {
            out.write(0);
        }
    }

    /**
     * Returns the ASCII bytes of a name.
     *
     * @param name The name.
     * @return The bytes.
     */
    private static byte[] ascii(String name)
    {
        return name.getBytes(StandardCharsets.US_ASCII);
    }

    /**
     * Writes a little-endian 16-bit value.
     *
     * @param out The output.
     * @param value The value.
     */
    private static void putShort(ByteArrayOutputStream out, int value)
    {
        out.write(value);
        out.write(value >> Byte.SIZE);
    }

    /**
     * Writes a little-endian 32-bit value.
     *
     * @param out The output.
     * @param value The value.
     */
    private static void putInt(ByteArrayOutputStream out, int value)
    {
        putShort(out, value);
        putShort(out, value >> Short.SIZE);
    }
}
