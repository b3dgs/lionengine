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

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioBackend;
import com.b3dgs.lionengine.audio.AudioDecodingException;
import com.b3dgs.lionengine.audio.FakeAudioBackend;
import com.b3dgs.lionengine.audio.LongSound;
import com.b3dgs.lionengine.audio.LongSoundLoader;
import com.b3dgs.lionengine.audio.PcmStream;
import com.b3dgs.lionengine.audio.Playlist;
import com.b3dgs.lionengine.audio.ShortSound;
import com.b3dgs.lionengine.audio.ShortSoundCache;
import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.sun.management.ThreadMXBean;

/**
 * Integration of {@link OggVorbisDecoder} with {@code audio-api} on the fake backend: short sounds of the cache, long
 * sounds streamed through their bounded buffer, looping, fully decoded, and playlists (ADR-0068, ADR-0072, ADR-0073,
 * REQ-AUDIO-002, REQ-AUDIO-003, REQ-AUDIO-006, REQ-AUDIO-008).
 */
final class OggVorbisAudioTest
{
    /** Key of an effect. */
    private static final ResourceKey EFFECT = new ResourceKey("sfx/coin.ogg");
    /** Key of a sound too long for a short sound. */
    private static final ResourceKey TOO_LONG = new ResourceKey("sfx/long.ogg");
    /** Key of the music. */
    private static final ResourceKey MUSIC = new ResourceKey("music/theme.ogg");
    /** Key of a track cut in its middle. */
    private static final ResourceKey TRUNCATED = new ResourceKey("music/truncated.ogg");
    /** Key of a second track. */
    private static final ResourceKey SECOND = new ResourceKey("music/second.ogg");
    /** Frames of the music. */
    private static final int MUSIC_FRAMES = 110_250;
    /** Frames of a read of the backend. */
    private static final int PIECE = 512;

    /** The bytes of each sound of the resources. */
    private final Map<ResourceKey, byte[]> sounds = new HashMap<>();
    /** The resources. */
    private FakeResourceProvider provider;
    /** The decoder. */
    private OggVorbisDecoder decoder;
    /** The inbox. */
    private InboxSpy inbox;
    /** The backend. */
    private FakeAudioBackend backend;
    /** The audio system. */
    private Audio audio;
    /** The loader of the long sounds. */
    private LongSoundLoader loader;

    /**
     * Puts the sounds in the resources and registers the decoder on the loader.
     */
    @BeforeEach
    void prepare()
    {
        final byte[] music = OggFiles.ogg(OggFiles.LONG_MONO_22050);
        sounds.put(EFFECT, OggFiles.ogg(OggFiles.MONO_11025));
        sounds.put(TOO_LONG, OggPages.concat(music, OggPages.withSerial(music, 1), OggPages.withSerial(music, 2)));
        sounds.put(MUSIC, music);
        sounds.put(TRUNCATED, Arrays.copyOf(music, music.length * 2 / 3));
        sounds.put(SECOND, OggFiles.ogg(OggFiles.MONO_22050));
        provider = new FakeResourceProvider(false);
        sounds.forEach(provider::put);
        decoder = new OggVorbisDecoder();
        inbox = new InboxSpy();
        backend = new FakeAudioBackend(inbox, 4);
        audio = new Audio(backend);
        loader = new LongSoundLoader(new Resources(provider));
        loader.setDecoder("ogg", decoder);
    }

    /**
     * Decodes a sound of the resources entirely.
     *
     * @param key The key.
     * @return The samples.
     * @throws IOException If reading fails.
     */
    private short[] decode(ResourceKey key) throws IOException
    {
        return OggFiles.samples(decoder.decode(new ByteArrayInputStream(sounds.get(key))));
    }

    /**
     * An OGG effect registered on the short-sound cache is decoded at its load and played on a voice; a sound longer
     * than 10 s is refused with its key and the advice to play it as a long sound.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testShortSound() throws IOException
    {
        final ShortSoundCache cache = new ShortSoundCache(new Resources(provider));
        cache.setDecoder("ogg", decoder);

        final ShortSound effect = cache.load(EFFECT);
        assertEquals(0.5, effect.getDuration(), 1E-3);
        assertTrue(audio.play(effect) > AudioBackend.NO_VOICE);
        assertEquals(1, backend.getPlayed().size());
        assertArrayEquals(decode(EFFECT), OggFiles.samples(backend.getPlayed().get(0)));

        final AudioDecodingException exception = assertThrows(AudioDecodingException.class,
                                                              () -> cache.load(TOO_LONG));
        assertTrue(exception.getMessage().startsWith(TOO_LONG.toString()), exception::getMessage);
        assertTrue(exception.getMessage().contains("long sound"), exception::getMessage);
        effect.release();
    }

    /**
     * Music streams through the default buffer of 4 chunks of 4,096 frames: the backend pulls every frame of the file,
     * then the voice ends at the delivery of its end.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testStreamed() throws IOException
    {
        final LongSound music = loader.stream(MUSIC);
        final int voice = audio.play(music);
        assertTrue(music.isStreamed());
        assertEquals(16_384, music.getChunks() * music.getChunkFrames());

        final PcmStream stream = backend.getStreams().get(0);
        final short[] samples = new short[decode(MUSIC).length];
        int frames = 0;
        int read;
        do
        {
            read = stream.read(samples, frames, Math.min(PIECE, samples.length - frames));
            frames += read;
        }
        while (read > 0);

        assertEquals(MUSIC_FRAMES, frames);
        assertArrayEquals(decode(MUSIC), samples);
        backend.advance(1);
        inbox.deliverAll();
        assertFalse(audio.isPlaying(voice));
        music.release();
    }

    /**
     * A looping music plays its passes one after the other without gap, reopening the file at each pass, and never
     * ends by itself.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testLooping() throws IOException
    {
        final LongSound music = loader.stream(MUSIC);
        music.setLooping(true);
        final int voice = audio.play(music);
        final short[] pass = decode(MUSIC);
        final PcmStream stream = backend.getStreams().get(0);
        final int passes = 3;
        final short[] samples = new short[pass.length * passes + PIECE];
        int frames = 0;
        while (frames < pass.length * passes)
        {
            assertEquals(PIECE, stream.read(samples, frames, PIECE));
            frames += PIECE;
        }

        for (int i = 0; i < passes; i++)
        {
            assertArrayEquals(pass, Arrays.copyOfRange(samples, i * pass.length, (i + 1) * pass.length), "pass " + i);
        }
        inbox.deliverAll();
        assertTrue(audio.isPlaying(voice));
        music.release();
    }

    /**
     * A long sound fully decoded on request holds the samples of the file and plays them on a buffer voice.
     *
     * @throws IOException If reading fails.
     */
    @Test
    void testFullyDecoded() throws IOException
    {
        final LongSound music = loader.decode(MUSIC);
        final int voice = audio.play(music);

        assertFalse(music.isStreamed());
        assertArrayEquals(decode(MUSIC), OggFiles.samples(backend.getPlayed().get(0)));
        backend.advance(MUSIC_FRAMES + 1);
        inbox.deliverAll();
        assertFalse(audio.isPlaying(voice));
        music.release();
    }

    /**
     * A playlist track whose OGG stream is cut in its middle plays its decoded frames, then is reported at the delivery
     * of its end with an {@link AudioDecodingException} starting with its key, and the next track plays.
     */
    @Test
    void testTrackFailingWhileStreaming()
    {
        final List<RuntimeException> failures = new ArrayList<>();
        final List<Integer> tracks = new ArrayList<>();
        final Playlist playlist = new Playlist(audio, loader.stream(TRUNCATED), loader.stream(SECOND));
        playlist.setListener((list, track, exception) ->
        {
            tracks.add(Integer.valueOf(track));
            failures.add(exception);
        });
        playlist.play();
        final int first = playlist.getVoice();

        backend.advance(MUSIC_FRAMES / 4);
        inbox.deliverAll();
        assertTrue(failures.isEmpty());
        assertEquals(MUSIC_FRAMES / 4, backend.getPulledFrames(first));
        backend.advance(MUSIC_FRAMES);
        assertTrue(failures.isEmpty());
        inbox.deliverAll();

        assertEquals(List.of(Integer.valueOf(0)), tracks);
        final AudioDecodingException exception = assertInstanceOf(AudioDecodingException.class, failures.get(0));
        assertTrue(exception.getMessage().startsWith(TRUNCATED.toString()), exception::getMessage);
        assertTrue(exception.getMessage().contains("Truncated"), exception::getMessage);
        assertEquals(1, playlist.getTrack());
        assertTrue(playlist.isPlaying());
        playlist.stop();
    }

    /**
     * Streaming the music through its bounded buffer allocates nothing once the buffer is filled: the reads of the
     * backend and the chunks decoded again from the OGG source, on the backend's thread.
     */
    @Test
    void testSteadyStreamingAllocation()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final short[] target = new short[PIECE];
        long lowest = Long.MAX_VALUE;
        for (int measure = 0; measure < 3; measure++)
        {
            final LongSound music = loader.stream(MUSIC);
            final int voice = audio.play(music);
            final PcmStream stream = backend.getStreams().get(measure);
            for (int i = 0; i < 40; i++)
            {
                stream.read(target, 0, PIECE);
            }
            final long before = bean.getCurrentThreadAllocatedBytes();
            for (int i = 0; i < 150; i++)
            {
                assertEquals(PIECE, stream.read(target, 0, PIECE));
            }
            lowest = Math.min(lowest, bean.getCurrentThreadAllocatedBytes() - before);
            audio.stop(voice);
            music.release();
        }
        final long allocated = lowest;

        assertEquals(0L, allocated, () -> "Allocated bytes: " + allocated);
    }
}
