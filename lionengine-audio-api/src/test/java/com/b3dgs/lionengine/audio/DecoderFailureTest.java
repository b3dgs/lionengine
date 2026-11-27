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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Regression tests of the unchecked failures of a decoder (ADR-0072): a decoder that throws an unchecked exception, or
 * a source that returns a frame count outside what was asked, never reaches the backend's thread. While a sound plays,
 * the stream ends and the failure is reported on the runtime thread as an {@link AudioDecodingException} starting with
 * the key, its cause kept; at the preparation and at a full decoding, it is raised as such, the resource stream closed.
 */
final class DecoderFailureTest
{
    /** Extension of the failing decoder. */
    private static final String EXTENSION = "bug";
    /** Key of a sound whose source throws while it plays. */
    private static final ResourceKey THROWING = new ResourceKey("music/throwing.bug");
    /** Frames of the sounds. */
    private static final int FRAMES = 20_000;
    /** Frames read before the source of {@link #THROWING} throws: more than the buffer prepared ahead. */
    private static final int FAIL_AFTER = 18_000;

    /** The resources. */
    private FakeResourceProvider provider;
    /** The loader. */
    private LongSoundLoader loader;
    /** The decoder of the extension {@link #EXTENSION}. */
    private FailingDecoder decoder;

    /**
     * Creates the resources, holding the same WAV content under every key, and the loader.
     */
    @BeforeEach
    void prepare()
    {
        provider = new FakeResourceProvider(false);
        provider.put(THROWING, LongSoundTest.wav(FRAMES));
        loader = new LongSoundLoader(new Resources(provider));
        decoder = new FailingDecoder();
        loader.setDecoder(EXTENSION, decoder);
    }

    /**
     * A source that throws while its playlist track plays ends the track, which is reported on the runtime thread with
     * its key and the original cause, and the next track plays.
     */
    @Test
    void testThrowingWhilePlaying()
    {
        final InboxSpy inbox = new InboxSpy();
        final FakeAudioBackend backend = new FakeAudioBackend(inbox);
        final Audio audio = new Audio(backend);
        final List<RuntimeException> failures = new ArrayList<>();
        decoder.failAfter = FAIL_AFTER;
        final Playlist playlist = new Playlist(audio, loader.stream(THROWING), loader.decode(THROWING));
        playlist.setListener((list, track, exception) -> failures.add(exception));
        playlist.play();

        backend.advance(FRAMES);
        assertTrue(failures.isEmpty());
        inbox.deliverAll();

        assertEquals(1, failures.size());
        final AudioDecodingException failure = assertInstanceOf(AudioDecodingException.class, failures.get(0));
        assertTrue(failure.getMessage().startsWith(THROWING.toString()), failure::getMessage);
        assertInstanceOf(IllegalStateException.class, failure.getCause());
        assertEquals(1, playlist.getTrack());
        playlist.stop();
        assertEquals(0, provider.getUnclosed());
    }

    /**
     * A source returning more frames than asked, or a negative count, ends the stream as a decoding failure.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testFrameCountOutOfRange() throws IOException
    {
        for (final int wrong : new int[]
        {
            -1, 1
        })
        {
            final InputStream input = new GeneratedWav(8_000, FRAMES);
            final PcmSource source = new WavDecoder().open(input);
            final PcmSource lying = new PcmSource()
            {
                @Override
                public PcmFormat getFormat()
                {
                    return source.getFormat();
                }

                @Override
                public int read(short[] target, int offset, int frames) throws IOException
                {
                    return wrong < 0 ? wrong : source.read(target, offset, frames) + wrong;
                }
            };
            final StreamBuffer stream = new StreamBuffer(lying, 4, 256);

            stream.fill();

            assertInstanceOf(AudioDecodingException.class, stream.getFailure());
            assertEquals(0, stream.read(new short[256 * stream.getFormat().channels()], 0, 256));
        }
    }

    /**
     * A decoder that throws an unchecked exception at the opening, at the first chunks or at a full decoding is raised
     * on the runtime thread as an {@link AudioDecodingException} starting with the key, its cause kept, and the
     * resource stream is closed.
     */
    @Test
    void testThrowingAtPreparation()
    {
        decoder.failOpen = true;
        final LongSound opening = loader.stream(THROWING);
        final AudioDecodingException atOpen = assertThrows(AudioDecodingException.class, opening::prepare);
        assertTrue(atOpen.getMessage().startsWith(THROWING.toString()), atOpen::getMessage);
        assertInstanceOf(IllegalArgumentException.class, atOpen.getCause());
        final LongSound decoding = loader.decode(THROWING);
        final AudioDecodingException atDecode = assertThrows(AudioDecodingException.class, decoding::prepare);
        assertInstanceOf(IllegalArgumentException.class, atDecode.getCause());

        decoder.failOpen = false;
        decoder.failAfter = 0;
        final LongSound reading = loader.stream(THROWING);
        final AudioDecodingException atFill = assertThrows(AudioDecodingException.class, reading::prepare);
        assertInstanceOf(IllegalStateException.class, atFill.getCause());

        assertFalse(opening.isPrepared());
        assertFalse(reading.isPrepared());
        assertEquals(0, provider.getUnclosed());
    }

    /**
     * Decoder of the WAV format with injected unchecked failures, as a decoder bug.
     */
    private static final class FailingDecoder implements AudioStreamDecoder
    {
        /** The WAV decoder. */
        private final WavDecoder wav = new WavDecoder();
        /** {@code true} to throw at the opening and at a full decoding. */
        private boolean failOpen;
        /** The frames a read may not go beyond without throwing, {@code -1} for never. */
        private int failAfter = -1;

        /**
         * Creates the decoder.
         */
        FailingDecoder()
        {
            super();
        }

        @Override
        public PcmBuffer decode(InputStream input) throws IOException
        {
            if (failOpen)
            {
                throw new IllegalArgumentException("Decoder bug at the decoding");
            }
            return wav.decode(input);
        }

        @Override
        public PcmSource open(InputStream input) throws IOException
        {
            if (failOpen)
            {
                throw new IllegalArgumentException("Decoder bug at the opening");
            }
            final PcmSource source = wav.open(input);
            final int limit = failAfter;
            return new PcmSource()
            {
                /** The frames read. */
                private int read;

                @Override
                public PcmFormat getFormat()
                {
                    return source.getFormat();
                }

                @Override
                public int read(short[] target, int offset, int frames) throws IOException
                {
                    if (limit >= 0 && read + frames > limit)
                    {
                        throw new IllegalStateException("Decoder bug at frame " + read);
                    }
                    final int count = source.read(target, offset, frames);
                    read += count;
                    return count;
                }
            };
        }
    }
}
