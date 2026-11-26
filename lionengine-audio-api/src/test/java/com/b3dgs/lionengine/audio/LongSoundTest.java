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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.StreamProvider;
import com.sun.management.ThreadMXBean;

/**
 * Tests of {@link LongSound} and {@link LongSoundLoader} played by {@link Audio} on {@link FakeAudioBackend}:
 * preparation, use, release and double release, streamed and fully decoded, errors, and the bounded memory of a long
 * stream (ADR-0005, ADR-0007, ADR-0033, ADR-0068, REQ-AUDIO-006, REQ-AUDIO-007, REQ-RES-004).
 */
final class LongSoundTest
{
    /** Sample rate of the sounds. */
    private static final int RATE = 8_000;
    /** Format of the sounds. */
    private static final PcmFormat FORMAT = new PcmFormat(RATE, PcmFormat.MONO);
    /** Frames of the music: 3 s, longer than the default buffer. */
    private static final int MUSIC_FRAMES = 3 * RATE;
    /** Key of the music. */
    private static final ResourceKey MUSIC = new ResourceKey("music/theme.wav");
    /** Key of a sound that is not a WAV file. */
    private static final ResourceKey INVALID = new ResourceKey("music/invalid.wav");
    /** Key of a sound that does not exist. */
    private static final ResourceKey MISSING = new ResourceKey("music/missing.wav");

    /**
     * Writes a mono WAV file of a ramp.
     *
     * @param frames The frames.
     * @return The bytes of the file.
     */
    static byte[] wav(int frames)
    {
        final short[] samples = new short[frames];
        for (int i = 0; i < frames; i++)
        {
            samples[i] = (short) i;
        }
        return WavWriter.write(new PcmBuffer(FORMAT, samples), 16);
    }

    /** The resources. */
    private FakeResourceProvider provider;
    /** The loader. */
    private LongSoundLoader loader;
    /** The inbox. */
    private InboxSpy inbox;
    /** The backend. */
    private FakeAudioBackend backend;
    /** The audio system. */
    private Audio audio;

    /**
     * Creates the resources and the audio system.
     */
    @BeforeEach
    void prepare()
    {
        provider = new FakeResourceProvider(false);
        provider.put(MUSIC, wav(MUSIC_FRAMES));
        provider.put(INVALID, new byte[]
        {
            1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12
        });
        loader = new LongSoundLoader(new Resources(provider));
        inbox = new InboxSpy();
        backend = new FakeAudioBackend(inbox, 4);
        audio = new Audio(backend, 2, VoicePolicy.STEAL);
    }

    /**
     * A streamed sound reads nothing until prepared; preparing opens it and fills its buffer once; it plays to its
     * end through the inbox, which closes its stream; it is prepared again by its next play; releasing twice is
     * idempotent and a released sound is refused.
     */
    @Test
    void testStreamedLifecycle()
    {
        final LongSound music = loader.stream(MUSIC);

        assertSame(MUSIC, music.getKey());
        assertTrue(music.isStreamed());
        assertEquals(LongSoundLoader.DEFAULT_CHUNKS, music.getChunks());
        assertEquals(LongSoundLoader.DEFAULT_CHUNK_FRAMES, music.getChunkFrames());
        assertFalse(music.isPrepared());
        assertEquals(0, provider.getOpened());
        assertThrows(IllegalStateException.class, music::getFormat);

        music.prepare();
        final StreamBuffer stream = music.getStream();
        music.prepare();

        assertTrue(music.isPrepared());
        assertSame(stream, music.getStream());
        assertEquals(1, provider.getOpened());
        assertEquals(FORMAT, music.getFormat());
        assertEquals(LongSoundLoader.DEFAULT_CHUNKS * LongSoundLoader.DEFAULT_CHUNK_FRAMES,
                     stream.getBufferedFrames());

        final int voice = audio.play(music);
        assertTrue(voice > 0);
        assertTrue(music.isPlaying());
        assertTrue(audio.isPlaying(voice));
        assertEquals(1, audio.getLongPlayingCount());
        assertEquals(0, audio.getPlayingCount());
        assertEquals(List.of(stream), backend.getStreams());
        backend.advance(MUSIC_FRAMES / 2);
        assertEquals(MUSIC_FRAMES / 2, backend.getPulledFrames(voice));
        backend.advance(MUSIC_FRAMES);
        assertTrue(music.isPlaying());
        inbox.deliverAll();

        assertFalse(music.isPlaying());
        assertFalse(audio.isPlaying(voice));
        assertFalse(music.isPrepared());
        assertEquals(0, provider.getUnclosed());

        final int again = audio.play(music);
        assertTrue(again > voice);
        assertEquals(2, provider.getOpened());

        music.release();
        music.release();

        assertTrue(music.isReleased());
        assertFalse(music.isPlaying());
        assertFalse(music.isPrepared());
        assertEquals(0, provider.getUnclosed());
        assertEquals(0, backend.getPlayingCount());
        assertThrows(IllegalStateException.class, music::prepare);
        assertThrows(IllegalStateException.class, () -> audio.play(music));
    }

    /**
     * A fully decoded sound decodes everything at its preparation, plays its buffer, stays prepared after its end, and
     * frees its samples at its release.
     */
    @Test
    void testDecodedLifecycle()
    {
        final LongSound loop = loader.decode(MUSIC);

        assertFalse(loop.isStreamed());
        assertEquals(0, loop.getChunks());
        assertEquals(0, loop.getChunkFrames());
        loop.prepare();
        assertEquals(0, provider.getUnclosed());

        final int voice = audio.play(loop);
        assertEquals(MUSIC_FRAMES, backend.getPlayed().get(0).getFrames());
        backend.advance(MUSIC_FRAMES);
        inbox.deliverAll();

        assertFalse(audio.isPlaying(voice));
        assertTrue(loop.isPrepared());
        assertEquals(FORMAT, loop.getFormat());
        assertTrue(audio.play(loop) > voice);
        assertEquals(1, provider.getOpened());

        loop.release();
        loop.release();

        assertFalse(loop.isPrepared());
        assertEquals(0, backend.getPlayingCount());
    }

    /**
     * A stopped sound is no longer pulled, posts no end and closes its stream; stopping again has no effect.
     */
    @Test
    void testStop()
    {
        final LongSound music = loader.stream(MUSIC);
        final int voice = audio.play(music);
        backend.advance(1_000);

        audio.stop(voice);
        audio.stop(voice);
        backend.advance(MUSIC_FRAMES);
        inbox.deliverAll();

        assertFalse(music.isPlaying());
        assertFalse(music.isPrepared());
        assertEquals(0, audio.getLongPlayingCount());
        assertEquals(0, backend.getPlayingCount());
        assertEquals(0, provider.getUnclosed());
    }

    /**
     * Releasing a playing sound stops it first; a sound plays on one voice at a time; with no voice free in the
     * backend, nothing is played and the sound stays prepared.
     */
    @Test
    void testPlayingAndVoices()
    {
        final LongSound music = loader.stream(MUSIC);
        final LongSound other = loader.stream(MUSIC);
        final int voice = audio.play(music);
        assertThrows(IllegalStateException.class, () -> audio.play(music));
        music.release();
        assertFalse(backend.isPlaying(voice));

        final FakeAudioBackend single = new FakeAudioBackend(inbox, 1);
        final Audio busy = new Audio(single, 1, VoicePolicy.STEAL);
        busy.play(other);
        final LongSound third = loader.stream(MUSIC);

        assertEquals(AudioBackend.NO_VOICE, busy.play(third));
        assertTrue(third.isPrepared());
        assertFalse(third.isPlaying());
        third.release();
        other.release();
        assertEquals(0, provider.getUnclosed());
    }

    /**
     * Several long sounds play at once, outside the short-sound limit, and each end reaches its own sound.
     */
    @Test
    void testSeveralLongSounds()
    {
        final LongSound[] sounds = new LongSound[3];
        final int[] voices = new int[sounds.length];
        for (int i = 0; i < sounds.length; i++)
        {
            sounds[i] = loader.stream(MUSIC);
            voices[i] = audio.play(sounds[i]);
        }

        assertEquals(3, audio.getLongPlayingCount());
        audio.stop(voices[1]);
        backend.advance(MUSIC_FRAMES + 1);
        inbox.deliverAll();

        for (final LongSound sound : sounds)
        {
            assertFalse(sound.isPlaying());
        }
        assertEquals(0, audio.getLongPlayingCount());
        assertEquals(0, provider.getUnclosed());
    }

    /**
     * Errors: a missing or unreadable resource and an invalid content are raised at the preparation, with the key; a
     * key without decoder at the creation; a fully decoded sound above the frame limit of its decoder is refused.
     */
    @Test
    void testErrors()
    {
        final ResourceException missing = assertThrows(ResourceException.class,
                                                       () -> loader.stream(MISSING).prepare());
        assertEquals(ResourceException.Reason.MISSING, missing.getReason());

        final AudioDecodingException invalid = assertThrows(AudioDecodingException.class,
                                                            () -> audio.play(loader.stream(INVALID)));
        assertTrue(invalid.getMessage().startsWith(INVALID.toString()), invalid::getMessage);
        assertThrows(AudioDecodingException.class, () -> loader.decode(INVALID).prepare());

        final ResourceKey failing = new ResourceKey("music/failing.wav");
        provider.put(failing, wav(10));
        provider.failOn(failing);
        final ResourceException unreadable = assertThrows(ResourceException.class,
                                                          () -> loader.stream(failing).prepare());
        assertEquals(ResourceException.Reason.INACCESSIBLE, unreadable.getReason());

        assertThrows(AudioDecodingException.class, () -> loader.stream(new ResourceKey("music/theme.ogg")));
        loader.setDecoder("wav", new WavDecoder(MUSIC_FRAMES - 1));
        final AudioDecodingException limit = assertThrows(AudioDecodingException.class,
                                                          () -> loader.decode(MUSIC).prepare());
        assertTrue(limit.getMessage().contains("limit"), limit::getMessage);
        final LongSound streamed = loader.stream(MUSIC);
        streamed.prepare();
        streamed.release();
        assertEquals(0, provider.getUnclosed());
    }

    /**
     * A file truncated within the buffer is refused at the preparation, and its stream is closed.
     */
    @Test
    void testTruncatedAtPreparation()
    {
        final ResourceKey truncated = new ResourceKey("music/truncated.wav");
        final byte[] bytes = wav(1_000);
        provider.put(truncated, Arrays.copyOf(bytes, bytes.length - 100));

        final AudioDecodingException exception = assertThrows(AudioDecodingException.class,
                                                              () -> loader.stream(truncated).prepare());

        assertTrue(exception.getMessage().startsWith(truncated.toString()), exception::getMessage);
        assertEquals(0, provider.getUnclosed());
    }

    /**
     * A custom buffer and a custom decoder are used; invalid arguments are refused.
     */
    @Test
    void testArguments()
    {
        final LongSound custom = loader.stream(MUSIC, 2, 512);
        custom.prepare();
        assertEquals(1_024, custom.getStream().getCapacity());
        custom.release();
        loader.setDecoder("raw", new WavDecoder());
        assertNotNull(loader.stream(new ResourceKey("music/theme.raw")));

        assertThrows(IllegalArgumentException.class, () -> new LongSoundLoader(null));
        assertThrows(IllegalArgumentException.class, () -> loader.stream(null));
        assertThrows(IllegalArgumentException.class, () -> loader.decode(null));
        assertThrows(IllegalArgumentException.class, () -> loader.stream(MUSIC, 0, 512));
        assertThrows(IllegalArgumentException.class, () -> loader.stream(MUSIC, 2, 0));
        assertThrows(IllegalArgumentException.class, () -> loader.stream(MUSIC, Integer.MAX_VALUE, 512));
        assertThrows(IllegalArgumentException.class, () -> loader.setDecoder(null, new WavDecoder()));
        assertThrows(IllegalArgumentException.class, () -> loader.setDecoder("", new WavDecoder()));
        assertThrows(IllegalArgumentException.class, () -> loader.setDecoder("a.b", new WavDecoder()));
        assertThrows(IllegalArgumentException.class, () -> loader.setDecoder("wav", null));
        assertThrows(IllegalArgumentException.class, () -> audio.play((LongSound) null));
    }

    /**
     * The loader and its sounds belong to the thread of its first operation.
     *
     * @throws InterruptedException If interrupted.
     */
    @Test
    void testThread() throws InterruptedException
    {
        final LongSound music = loader.stream(MUSIC);
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        final Thread other = new Thread(() ->
        {
            try
            {
                music.prepare();
            }
            catch (final IllegalStateException exception)
            {
                failure.set(exception);
            }
        });

        other.start();
        other.join();

        assertTrue(failure.get() instanceof IllegalStateException);
        assertFalse(music.isPrepared());
    }

    /**
     * Ten minutes of stereo at 44.1 kHz, 105.8 MB of samples once decoded, play to their end on the fake backend
     * through the default buffer of 16,384 frames: the sound is not limited to the 10 s of the short sounds, and the
     * whole play allocates far less than its samples.
     */
    @Test
    void testStreamingBoundedMemory()
    {
        final int rate = 44_100;
        final long frames = 10L * 60L * rate;
        final ResourceKey key = new ResourceKey("music/long.wav");
        final GeneratedWav file = new GeneratedWav(rate, frames);
        final StreamProvider streams = new StreamProvider();
        streams.register(key, () -> file);
        final LongSoundLoader longLoader = new LongSoundLoader(new Resources(streams));
        final LongSound music = longLoader.stream(key);
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();

        final long before = bean.getCurrentThreadAllocatedBytes();
        final int voice = audio.play(music);
        final int capacity = music.getStream().getCapacity();
        long played = 0L;
        while (backend.isPlaying(voice))
        {
            backend.advance(rate);
            played += rate;
        }
        inbox.deliverAll();
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertEquals(16_384, capacity);
        assertTrue(played >= frames);
        assertFalse(music.isPlaying());
        assertTrue(file.isClosed());
        assertTrue(allocated < 1_024L * 1_024L, () -> "Allocated bytes: " + allocated);
    }
}
