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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.sun.management.ThreadMXBean;

/**
 * Tests of the controls of long sounds and playlists on {@link FakeAudioBackend} and its clock: gain products, looping
 * of streamed and fully decoded sounds without end and with bounded memory, pause and resume of a voice and of the
 * master with streams not pulled, and the interaction with stop, release and playlists (ADR-0031, ADR-0068, ADR-0069,
 * REQ-AUDIO-006, REQ-AUDIO-008, REQ-AUDIO-011).
 */
final class LongSoundControlsTest
{
    /** Frames of the music: 2.5 s at 8 kHz, longer than the default buffer. */
    private static final int MUSIC_FRAMES = 20_000;
    /** Frames of the second track. */
    private static final int SECOND_FRAMES = 30_000;
    /** Key of the music. */
    private static final ResourceKey MUSIC = new ResourceKey("music/theme.wav");
    /** Key of the second track. */
    private static final ResourceKey SECOND = new ResourceKey("music/second.wav");
    /** Tolerance of the gain comparisons. */
    private static final double EPSILON = 1e-12;

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
        provider.put(MUSIC, LongSoundTest.wav(MUSIC_FRAMES));
        provider.put(SECOND, LongSoundTest.wav(SECOND_FRAMES));
        loader = new LongSoundLoader(new Resources(provider));
        inbox = new InboxSpy();
        backend = new FakeAudioBackend(inbox, 4);
        audio = new Audio(backend, 2, VoicePolicy.STEAL);
    }

    /**
     * Plays frames, then delivers the inbox as the loop does at its next iteration.
     *
     * @param frames The frames.
     */
    private void play(int frames)
    {
        backend.advance(frames);
        inbox.deliverAll();
    }

    /**
     * The voice of a long sound gets master × sound at its play, and every change of either at once; out of range
     * gains are refused.
     */
    @Test
    void testGain()
    {
        final LongSound music = loader.stream(MUSIC);
        assertEquals(Audio.MAX_GAIN, music.getGain());
        music.setGain(0.5);
        audio.setMasterGain(0.8);

        final int voice = audio.play(music);

        assertEquals(0.4, backend.getGain(voice), EPSILON);
        music.setGain(0.25);
        assertEquals(0.2, backend.getGain(voice), EPSILON);
        audio.setMasterGain(1.0);
        assertEquals(0.25, backend.getGain(voice), EPSILON);
        assertThrows(IllegalArgumentException.class, () -> music.setGain(Double.NaN));
        assertThrows(IllegalArgumentException.class, () -> music.setGain(1.5));
        assertEquals(0.25, music.getGain());
        music.release();
        music.setGain(0.5);
        assertEquals(0.5, music.getGain());
    }

    /**
     * A streamed sound that loops never ends and posts no end: its frames are pulled without gap across the passes,
     * each pass opens the resource again and closes the previous one, and the stop closes the last.
     */
    @Test
    void testStreamedLoop()
    {
        final LongSound music = loader.stream(MUSIC);
        assertFalse(music.isLooping());
        music.setLooping(true);

        final int voice = audio.play(music);
        play(MUSIC_FRAMES);
        play(MUSIC_FRAMES);
        play(MUSIC_FRAMES + 123);

        assertTrue(music.isPlaying());
        assertTrue(audio.isPlaying(voice));
        assertEquals(3L * MUSIC_FRAMES + 123L, backend.getPulledFrames(voice));
        assertTrue(inbox.getPosted().isEmpty());
        assertEquals(4, provider.getOpened());
        assertEquals(1, provider.getUnclosed());

        audio.stop(voice);
        assertEquals(0, provider.getUnclosed());
        assertFalse(music.isPrepared());
        music.setLooping(false);
        final int once = audio.play(music);
        play(MUSIC_FRAMES + 1);
        assertFalse(audio.isPlaying(once));
        music.release();
    }

    /**
     * A fully decoded sound that loops plays on a looping voice and never ends; its samples stay until its release.
     */
    @Test
    void testDecodedLoop()
    {
        final LongSound music = loader.decode(MUSIC);
        music.setLooping(true);

        final int voice = audio.play(music);
        play(5 * MUSIC_FRAMES);

        assertTrue(backend.isLooping(voice));
        assertTrue(audio.isPlaying(voice));
        assertTrue(inbox.getPosted().isEmpty());
        assertEquals(1, provider.getOpened());
        music.release();
        assertFalse(backend.isPlaying(voice));
        assertEquals(0, audio.getLongPlayingCount());
    }

    /**
     * A paused long voice is not pulled and posts no end; resumed, it is pulled again from where it was; the master
     * pause holds it too; releasing a paused sound stops it and closes its stream.
     */
    @Test
    void testPause()
    {
        final LongSound music = loader.stream(MUSIC);
        final int voice = audio.play(music);
        play(1_000);

        audio.pause(voice);
        play(5 * MUSIC_FRAMES);

        assertTrue(audio.isPaused(voice));
        assertEquals(1_000L, backend.getPulledFrames(voice));
        assertTrue(music.isPlaying());
        audio.resume(voice);
        play(1_000);
        assertEquals(2_000L, backend.getPulledFrames(voice));

        audio.setMasterPaused(true);
        play(5 * MUSIC_FRAMES);
        assertEquals(2_000L, backend.getPulledFrames(voice));
        assertFalse(audio.isPaused(voice));
        audio.setMasterPaused(false);
        play(MUSIC_FRAMES - 2_000);
        assertTrue(music.isPlaying());
        play(1);
        assertFalse(music.isPlaying());

        final int again = audio.play(music);
        audio.pause(again);
        music.release();
        assertFalse(audio.isPlaying(again));
        assertFalse(audio.isPaused(again));
        assertEquals(0, provider.getUnclosed());
    }

    /**
     * Pausing a playlist pauses the voice of its track, and pausing that voice pauses the playlist: it does not
     * advance while paused; it resumes where it was; a stopped playlist is not paused, and its play starts unpaused.
     */
    @Test
    void testPlaylistPause()
    {
        final Playlist playlist = new Playlist(audio, loader.stream(MUSIC), loader.stream(SECOND));
        playlist.pause();
        assertFalse(playlist.isPaused());
        playlist.play();

        playlist.pause();
        play(5 * MUSIC_FRAMES);

        assertTrue(playlist.isPaused());
        assertTrue(audio.isPaused(playlist.getVoice()));
        assertEquals(0, playlist.getTrack());
        playlist.resume();
        assertFalse(playlist.isPaused());
        play(MUSIC_FRAMES + 1);
        assertEquals(1, playlist.getTrack());

        audio.pause(playlist.getVoice());
        assertTrue(playlist.isPaused());
        play(2 * SECOND_FRAMES);
        assertEquals(1, playlist.getTrack());
        audio.resume(playlist.getVoice());
        play(SECOND_FRAMES + 1);
        assertFalse(playlist.isPlaying());

        playlist.play();
        playlist.pause();
        playlist.stop();
        assertFalse(playlist.isPaused());
        playlist.play();
        assertFalse(playlist.isPaused());
        playlist.stop();
        assertEquals(0, provider.getUnclosed());
    }

    /**
     * A playlist whose track loops stays on it until it is stopped, as an intro followed by a looping theme: the track
     * after it is not prepared ahead, and the playlist never advances.
     */
    @Test
    void testPlaylistLoopingTrack()
    {
        final LongSound intro = loader.stream(SECOND);
        final LongSound theme = loader.stream(MUSIC);
        final LongSound after = loader.stream(SECOND);
        theme.setLooping(true);
        final Playlist playlist = new Playlist(audio, intro, theme, after);
        playlist.play();
        assertTrue(theme.isPrepared());

        play(SECOND_FRAMES + 1);
        assertEquals(1, playlist.getTrack());
        assertFalse(after.isPrepared());
        play(10 * MUSIC_FRAMES);

        assertEquals(1, playlist.getTrack());
        assertTrue(theme.isPlaying());
        assertFalse(after.isPrepared());
        playlist.stop();
        assertEquals(0, provider.getUnclosed());
    }

    /**
     * A streamed loop played a long time through the fake keeps a bounded memory: each pass allocates its resource
     * stream and source once, with the read buffer of the WAV source, never per audio buffer.
     */
    @Test
    void testLoopBoundedMemory()
    {
        final LongSound music = loader.stream(MUSIC);
        music.setLooping(true);
        final int voice = audio.play(music);
        play(MUSIC_FRAMES);
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final int passes = 50;

        final long before = bean.getCurrentThreadAllocatedBytes();
        for (int i = 0; i < passes; i++)
        {
            backend.advance(MUSIC_FRAMES);
        }
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(audio.isPlaying(voice));
        assertEquals((passes + 1L) * MUSIC_FRAMES, backend.getPulledFrames(voice));
        assertTrue(allocated < passes * 16_384L, () -> "Allocated bytes: " + allocated);
        assertEquals(1, provider.getUnclosed());
        music.release();
        assertEquals(0, provider.getUnclosed());
    }
}
