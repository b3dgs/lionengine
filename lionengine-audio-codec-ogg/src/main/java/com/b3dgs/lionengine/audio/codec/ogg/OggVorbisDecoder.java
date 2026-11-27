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
import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.audio.AudioDecodingException;
import com.b3dgs.lionengine.audio.AudioStreamDecoder;
import com.b3dgs.lionengine.audio.PcmBuffer;
import com.b3dgs.lionengine.audio.PcmFormat;
import com.b3dgs.lionengine.audio.PcmSource;

/**
 * Decoder of OGG Vorbis files on desktop, entirely or by streaming, on JOrbis (ADR-0027, ADR-0033, ADR-0068,
 * ADR-0073, REQ-AUDIO-002, REQ-AUDIO-003, REQ-AUDIO-006).
 *
 * <p>
 * Registered by the game for the {@code ogg} extension of both audio families, never discovered (ADR-0020):
 * </p>
 *
 * <pre>
 * final OggVorbisDecoder ogg = new OggVorbisDecoder();
 * cache.setDecoder("ogg", ogg);
 * loader.setDecoder("ogg", ogg);
 * </pre>
 *
 * <p>
 * Accepted: an OGG stream whose first logical stream is Vorbis I, mono or stereo, at any positive sample rate. Its
 * three header packets are read and checked by {@link #open(InputStream)} before any sample. Chained logical streams
 * (files concatenated one after the other) are decoded as one sound when every link has the format of the first one.
 * Refused with an {@link AudioDecodingException}: data that is not OGG, a first logical stream that is not Vorbis
 * (Opus, FLAC, Theora...), more than two channels, a link of another format, logical streams multiplexed together, a
 * page whose checksum fails, a missing page, an audio packet that does not decode, and a stream ending before the page
 * that ends its last logical stream (truncated). By {@link #decode(InputStream)} only, a sound above the frame limit.
 * </p>
 *
 * <p>
 * Samples become 16-bit signed: each decoded value is scaled by 32,768, rounded to the nearest integer and clipped to
 * the 16-bit range, as {@code libvorbisfile} does. The last frames are trimmed to the granule position of the last
 * page, so the sound has the length the encoder gave it.
 * </p>
 *
 * <p>
 * Limit: an OGG header does not give the length of the sound, so {@link #decode(InputStream)} decodes into blocks of
 * at most {@link #BLOCK_FRAMES} frames and checks the limit before allocating each one: it never allocates samples past
 * the limit, and refuses the sound as soon as one frame more decodes. The blocks are then copied into the buffer.
 * </p>
 *
 * <p>
 * Threads: thread-safe; the decoder holds only its limit, so loading workers may decode several sounds at once
 * (ADR-0019). The stream is read and never closed. Each source it opens is independent and read by one thread at a
 * time; reading a source allocates nothing once its first packets are decoded.
 * </p>
 *
 * @since 11.0.0
 */
public final class OggVorbisDecoder implements AudioStreamDecoder
{
    /**
     * Default frame limit: 1,920,000 frames, 10 s at 192 kHz, the same limit as {@code WavDecoder}: the longest short
     * sound (ADR-0033) at the highest common rate, so 7.68 MB of samples at most in stereo (ADR-0066, ADR-0073).
     */
    public static final int DEFAULT_MAX_FRAMES = 1_920_000;
    /** Frames of a block of {@link #decode(InputStream)}: 16,384, 64 KB of stereo samples. */
    public static final int BLOCK_FRAMES = 16_384;

    /** Frame limit. */
    private final int maxFrames;

    /**
     * Creates a decoder with the {@link #DEFAULT_MAX_FRAMES} limit.
     */
    public OggVorbisDecoder()
    {
        this(DEFAULT_MAX_FRAMES);
    }

    /**
     * Creates a decoder.
     *
     * @param maxFrames The frame limit (positive): a longer sound is refused, and no samples past the limit are
     *            allocated.
     * @throws IllegalArgumentException If the limit is not positive.
     */
    public OggVorbisDecoder(int maxFrames)
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

    /**
     * {@inheritDoc}
     *
     * <p>
     * The sound is decoded in blocks of at most {@link #BLOCK_FRAMES} frames, each allocated only within the frame
     * limit, then copied into the returned buffer: at most twice the samples of the sound are held at once.
     * </p>
     */
    @Override
    public PcmBuffer decode(InputStream input) throws IOException
    {
        final OggVorbisSource source = OggVorbisSource.open(input);
        final PcmFormat format = source.getFormat();
        final int channels = format.channels();
        final List<short[]> blocks = new ArrayList<>();
        int frames = 0;
        boolean more = true;
        while (more)
        {
            final int wanted = Math.min(BLOCK_FRAMES, maxFrames - frames);
            if (wanted == 0)
            {
                checkEnded(source, channels);
                more = false;
            }
            else
            {
                final short[] block = new short[wanted * channels];
                final int read = source.read(block, 0, wanted);
                blocks.add(block);
                frames += read;
                more = read == wanted;
            }
        }

        final short[] samples = new short[frames * channels];
        int copied = 0;
        for (final short[] block : blocks)
        {
            final int length = Math.min(block.length, samples.length - copied);
            System.arraycopy(block, 0, samples, copied, length);
            copied += length;
        }
        return new PcmBuffer(format, samples);
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The frame limit does not apply: a streamed sound has no length limit. The source refuses a truncated or corrupted
     * stream with an {@link AudioDecodingException} when it reaches the faulty data, after the frames decoded before.
     * </p>
     */
    @Override
    public PcmSource open(InputStream input) throws IOException
    {
        return OggVorbisSource.open(input);
    }

    /**
     * Checks that a source has no frame left, once the frame limit is reached.
     *
     * @param source The source.
     * @param channels The channel count.
     * @throws AudioDecodingException If a frame more decodes.
     * @throws IOException If reading fails.
     */
    private void checkEnded(OggVorbisSource source, int channels) throws IOException
    {
        if (source.read(new short[channels], 0, 1) > 0)
        {
            throw new AudioDecodingException("OGG Vorbis sound of more than " + maxFrames + " frames refused, limit "
                                             + maxFrames);
        }
    }
}
