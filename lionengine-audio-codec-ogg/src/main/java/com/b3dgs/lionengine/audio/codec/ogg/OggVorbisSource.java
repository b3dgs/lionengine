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

import java.io.IOException;
import java.io.InputStream;

import com.b3dgs.lionengine.audio.AudioDecodingException;
import com.b3dgs.lionengine.audio.PcmFormat;
import com.b3dgs.lionengine.audio.PcmSource;
import com.jcraft.jogg.Packet;
import com.jcraft.jorbis.Block;
import com.jcraft.jorbis.Comment;
import com.jcraft.jorbis.DspState;
import com.jcraft.jorbis.Info;

/**
 * Frames of an OGG Vorbis stream, decoded on demand to 16 bits with JOrbis (ADR-0068, ADR-0073).
 *
 * <p>
 * Created by {@link #open(InputStream)}, which reads the three header packets of the first logical stream, once
 * {@link VorbisHeaders} checked them, positioned at the first frame. {@link OggPackets} gives the packets; each audio
 * packet is decoded into the windowed buffer of the Vorbis synthesis, and each read converts the frames it returns.
 * Once the first packets are decoded, the buffers of JOrbis have their size and reading allocates nothing; a new link
 * of a chained stream allocates its Vorbis state once. A failure sets the source as ended, so a later read returns
 * {@code 0}. Not thread-safe.
 * </p>
 */
final class OggVorbisSource implements PcmSource
{
    /** Header packets of a Vorbis stream: identification, comment, setup. */
    private static final int HEADERS = 3;
    /** No granule position known for the end of the current link. */
    private static final long NO_END = -1L;
    /** Scale of a decoded value to 16 bits, as {@code libvorbisfile}. */
    private static final double SCALE = 32_768.0;

    /**
     * Opens a stream: reads and checks the headers of its first logical stream.
     *
     * @param input The stream, positioned at its first byte.
     * @return The source, positioned at the first frame.
     * @throws AudioDecodingException If the stream is not OGG Vorbis, is truncated, corrupted or unsupported.
     * @throws IllegalArgumentException If {@code input} is {@code null}.
     * @throws IOException If reading fails.
     */
    static OggVorbisSource open(InputStream input) throws IOException
    {
        if (input == null)
        {
            throw new IllegalArgumentException("Input must not be null");
        }
        final OggVorbisSource source = new OggVorbisSource(input);
        try
        {
            source.packets.open();
            source.readHeaders();
        }
        // CHECKSTYLE IGNORE LINE: IllegalCatch
        catch (final RuntimeException exception)
        {
            throw failure(exception);
        }
        return source;
    }

    /**
     * Returns the decoding exception of a failure of JOrbis or of the source.
     *
     * @param exception The failure.
     * @return The exception itself if it is a decoding exception, else a decoding exception caused by it.
     */
    private static AudioDecodingException failure(RuntimeException exception)
    {
        if (exception instanceof final AudioDecodingException decoding)
        {
            return decoding;
        }
        return new AudioDecodingException(OggPackets.ERROR_CORRUPT + "rejected by the Vorbis decoder", exception);
    }

    /**
     * Converts a decoded value to 16 bits: scaled, rounded to the nearest integer and clipped.
     *
     * @param value The decoded value, nominally in {@code [-1, 1]}.
     * @return The 16-bit sample.
     */
    private static short toShort(float value)
    {
        return (short) Math.clamp(Math.round(value * SCALE), Short.MIN_VALUE, Short.MAX_VALUE);
    }

    /** The packets of the stream. */
    private final OggPackets packets;
    /** The decoded values given by the synthesis: {@code [0][channel][index]}. */
    private final float[][][] pcm = new float[1][][];
    /** The index of the first value of each channel given by the synthesis. */
    private final int[] index = new int[PcmFormat.STEREO];
    /** The Vorbis synthesis of the current link. */
    private DspState dsp;
    /** The block of the current link. */
    private Block block;
    /** The long block size of the current link, from its identification header. */
    private int longBlock;
    /** The format, known from the headers of the first link. */
    private PcmFormat format;
    /** The frames of the current link given so far. */
    private long linkFrames;
    /** The granule position of the last packet of the current link, its frame count, {@link #NO_END} until known. */
    private long linkEnd = NO_END;
    /** {@code true} once the stream ended or failed. */
    private boolean ended;

    /**
     * Creates the source.
     *
     * @param input The stream, positioned at its first byte.
     */
    private OggVorbisSource(InputStream input)
    {
        super();

        packets = new OggPackets(input);
    }

    @Override
    public PcmFormat getFormat()
    {
        return format;
    }

    @Override
    public int read(short[] target, int offset, int frames) throws IOException
    {
        final boolean negative = offset < 0 || frames < 0;
        if (target == null || negative || offset > target.length - (long) frames * format.channels())
        {
            throw new IllegalArgumentException("Invalid frame range: " + offset + '+' + frames);
        }
        try
        {
            return decode(target, offset, frames);
        }
        catch (final IOException exception)
        {
            ended = true;
            throw exception;
        }
        // CHECKSTYLE IGNORE LINE: IllegalCatch
        catch (final RuntimeException exception)
        {
            ended = true;
            throw failure(exception);
        }
    }

    /**
     * Decodes frames until the count is reached or the stream ends.
     *
     * @param target The array receiving the interleaved samples.
     * @param offset The index of the first sample written.
     * @param frames The number of frames wanted.
     * @return The number of frames written.
     * @throws AudioDecodingException If the data is truncated, corrupted or unsupported.
     * @throws IOException If reading fails.
     */
    private int decode(short[] target, int offset, int frames) throws IOException
    {
        final int channels = format.channels();
        int written = 0;
        while (written < frames && !ended)
        {
            final int available = available();
            if (available > 0)
            {
                final int count = Math.min(available, frames - written);
                convert(target, offset + written * channels, count, channels);
                dsp.synthesis_read(count);
                linkFrames += count;
                written += count;
            }
            else
            {
                decodePacket();
            }
        }
        return written;
    }

    /**
     * Converts frames ready in the synthesis to 16 bits, interleaved by frame.
     *
     * @param target The array receiving the samples.
     * @param first The index of the first sample written.
     * @param count The number of frames.
     * @param channels The channel count.
     */
    private void convert(short[] target, int first, int count, int channels)
    {
        final float[][] values = pcm[0];
        int sample = first;
        for (int i = 0; i < count; i++)
        {
            for (int channel = 0; channel < channels; channel++)
            {
                target[sample] = toShort(values[channel][index[channel] + i]);
                sample++;
            }
        }
    }

    /**
     * Returns the frames ready in the synthesis, trimmed to the end of the link: the frames past the granule position
     * of its last packet are padding of the encoder, dropped once the frames before them are given, as
     * {@code libvorbisfile} does.
     *
     * @return The frames ready to be given.
     */
    private int available()
    {
        int available = dsp.synthesis_pcmout(pcm, index);
        if (linkEnd != NO_END && available > linkEnd - linkFrames)
        {
            if (linkFrames < linkEnd)
            {
                available = (int) (linkEnd - linkFrames);
            }
            else
            {
                dsp.synthesis_read(available);
                available = 0;
            }
        }
        return available;
    }

    /**
     * Decodes the next audio packet into the synthesis, opens the next link once a link ended, or ends the source.
     *
     * @throws AudioDecodingException If the data is truncated, corrupted or unsupported.
     * @throws IOException If reading fails.
     */
    private void decodePacket() throws IOException
    {
        final Packet packet = packets.next();
        if (packet != null)
        {
            if (block.synthesis(packet) != 0)
            {
                throw new AudioDecodingException(OggPackets.ERROR_CORRUPT + "audio packet not decoded");
            }
            dsp.synthesis_blockin(block);
            if (packet.e_o_s != 0)
            {
                linkEnd = packet.granulepos;
            }
        }
        else if (packets.nextLink())
        {
            readHeaders();
        }
        else
        {
            ended = true;
        }
    }

    /**
     * Reads the three headers of the current link, checks its format and starts its synthesis.
     *
     * @throws AudioDecodingException If the link is not Vorbis, is truncated, unsupported or of another format.
     * @throws IOException If reading fails.
     */
    private void readHeaders() throws IOException
    {
        final Info info = new Info();
        final Comment comment = new Comment();
        info.init();
        comment.init();
        for (int headers = 0; headers < HEADERS; headers++)
        {
            final Packet packet = packets.next();
            if (packet == null)
            {
                throw new AudioDecodingException(OggPackets.ERROR_TRUNCATED + "logical stream ended in its headers");
            }
            checkHeader(headers, packet, info);
            if (info.synthesis_headerin(comment, packet) < 0)
            {
                throw new AudioDecodingException(headers == 0 ? OggPackets.ERROR_UNSUPPORTED + "not a Vorbis stream"
                                                              : OggPackets.ERROR_CORRUPT + "invalid Vorbis header");
            }
        }
        checkFormat(info);
        dsp = new DspState();
        dsp.synthesis_init(info);
        block = new Block(dsp);
        linkFrames = 0L;
        linkEnd = NO_END;
    }

    /**
     * Checks a header packet before JOrbis reads it, so that its declared sizes stay bounded.
     *
     * @param header The index of the header: identification, comment, setup.
     * @param packet The packet.
     * @param info The headers read before, by JOrbis.
     * @throws AudioDecodingException If the header is corrupted or above the bounds of {@link VorbisHeaders}.
     */
    private void checkHeader(int header, Packet packet, Info info)
    {
        switch (header)
        {
            case 0 -> longBlock = VorbisHeaders.checkIdentification(packet.packet_base, packet.packet, packet.bytes);
            case 1 -> VorbisHeaders.checkComment(packet.packet_base, packet.packet, packet.bytes);
            default -> VorbisHeaders.checkSetup(packet.packet_base,
                                                packet.packet,
                                                packet.bytes,
                                                longBlock,
                                                info.channels);
        }
    }

    /**
     * Checks the format of a link.
     *
     * @param info The headers of the link.
     * @throws AudioDecodingException If the format is unsupported or differs from the first link.
     */
    private void checkFormat(Info info)
    {
        if (info.channels < PcmFormat.MONO || info.channels > PcmFormat.STEREO)
        {
            throw new AudioDecodingException(OggPackets.ERROR_UNSUPPORTED + info.channels
                                             + " channels, mono or stereo only");
        }
        final PcmFormat link = new PcmFormat(info.rate, info.channels);
        if (format == null)
        {
            format = link;
        }
        else if (!format.equals(link))
        {
            throw new AudioDecodingException(OggPackets.ERROR_UNSUPPORTED + "chained link of " + link + " after "
                                             + format);
        }
    }
}
