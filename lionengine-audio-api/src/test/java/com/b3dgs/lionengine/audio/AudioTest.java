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
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.sun.management.ThreadMXBean;

/**
 * Test {@link Audio}: playing and stopping short sounds, the voice limit, priorities and each overflow policy, on the
 * fake backend (ADR-0027, ADR-0033, ADR-0066, REQ-AUDIO-003, REQ-AUDIO-004, REQ-AUDIO-010).
 */
final class AudioTest
{
    /** Format of the sounds. */
    private static final PcmFormat FORMAT = new PcmFormat(1_000, PcmFormat.MONO);
    /** Key of the short sound. */
    private static final ResourceKey SHOT = new ResourceKey("sounds/shot.wav");
    /** Key of the long sound. */
    private static final ResourceKey ALARM = new ResourceKey("sounds/alarm.wav");
    /** Frames of the short sound. */
    private static final int SHOT_FRAMES = 100;
    /** Frames of the long sound. */
    private static final int ALARM_FRAMES = 1_000;
    /** Plays of the allocation test. */
    private static final int ITERATIONS = 10_000;
    /** Bytes tolerated by the allocation test, for the measurement itself. */
    private static final long ALLOCATION_TOLERANCE = 1_024L;

    /** The inbox of the backend. */
    private InboxSpy inbox;
    /** The fake backend. */
    private FakeAudioBackend backend;
    /** The cache. */
    private ShortSoundCache cache;
    /** The short sound. */
    private ShortSound shot;
    /** The long sound. */
    private ShortSound alarm;

    /**
     * Creates the backend, the cache and the sounds.
     */
    @BeforeEach
    void prepare()
    {
        final FakeResourceProvider provider = new FakeResourceProvider(false);
        provider.put(SHOT, WavWriter.write(new PcmBuffer(FORMAT, new short[SHOT_FRAMES]), 16));
        provider.put(ALARM, WavWriter.write(new PcmBuffer(FORMAT, new short[ALARM_FRAMES]), 16));
        inbox = new InboxSpy();
        backend = new FakeAudioBackend(inbox);
        cache = new ShortSoundCache(new Resources(provider));
        shot = cache.load(SHOT);
        alarm = cache.load(ALARM);
    }

    /**
     * Creates an audio system of three voices on the backend.
     *
     * @param policy The overflow policy.
     * @return The audio system.
     */
    private Audio audio(VoicePolicy policy)
    {
        return new Audio(backend, 3, policy);
    }

    /**
     * A sound plays on a new voice until its end is delivered; stopping it then, or again, has no effect.
     */
    @Test
    void testPlayToEnd()
    {
        final Audio audio = new Audio(backend);

        final int voice = audio.play(shot);
        assertTrue(audio.isPlaying(voice));
        assertEquals(1, audio.getPlayingCount());
        backend.advance(SHOT_FRAMES);
        assertTrue(audio.isPlaying(voice));
        inbox.deliverAll();

        assertFalse(audio.isPlaying(voice));
        assertEquals(0, audio.getPlayingCount());
        audio.stop(voice);
        audio.stop(voice);
        assertEquals(List.of(shot.getBuffer()), backend.getPlayed());
        assertEquals(Audio.DEFAULT_MAX_VOICES, audio.getMaxVoices());
        assertEquals(VoicePolicy.STEAL, audio.getPolicy());
    }

    /**
     * A stopped voice stops at once in the backend and frees its slot; stopping every voice frees them all.
     */
    @Test
    void testStop()
    {
        final Audio audio = new Audio(backend);
        final int first = audio.play(alarm);
        final int second = audio.play(alarm);
        final int third = audio.play(alarm);

        audio.stop(second);
        assertFalse(backend.isPlaying(second));
        assertFalse(audio.isPlaying(second));
        assertTrue(audio.isPlaying(first));
        assertEquals(2, audio.getPlayingCount());

        audio.stopAll();
        assertEquals(0, audio.getPlayingCount());
        assertEquals(0, backend.getPlayingCount());
        assertFalse(audio.isPlaying(third));
        audio.stop(Integer.MAX_VALUE);
    }

    /**
     * Stealing stops the oldest voice of the lowest priority, never a voice of higher priority than the new sound.
     */
    @Test
    void testSteal()
    {
        final Audio audio = audio(VoicePolicy.STEAL);
        final int high = audio.play(alarm, 90);
        final int lowOld = audio.play(alarm, 10);
        final int lowNew = audio.play(alarm, 10);

        final int stealer = audio.play(shot, 10);
        assertTrue(stealer > 0);
        assertFalse(audio.isPlaying(lowOld));
        assertFalse(backend.isPlaying(lowOld));
        assertTrue(audio.isPlaying(lowNew));

        final int second = audio.play(shot);
        assertTrue(second > 0);
        assertFalse(audio.isPlaying(lowNew));
        assertTrue(audio.isPlaying(high));
        assertTrue(audio.isPlaying(stealer));

        assertEquals(AudioBackend.NO_VOICE, audio.play(shot, 0));
        assertEquals(3, audio.getPlayingCount());
        assertTrue(audio.isPlaying(high));
    }

    /**
     * Stealing among equal priorities takes the oldest voice.
     */
    @Test
    void testStealOldest()
    {
        final Audio audio = audio(VoicePolicy.STEAL);
        final int first = audio.play(alarm);
        final int second = audio.play(alarm);
        final int third = audio.play(alarm);

        audio.play(shot);

        assertFalse(audio.isPlaying(first));
        assertTrue(audio.isPlaying(second));
        assertTrue(audio.isPlaying(third));
    }

    /**
     * Drop-new keeps the playing voices and plays nothing, whatever the priority; a voice ended frees a slot.
     */
    @Test
    void testDropNew()
    {
        final Audio audio = audio(VoicePolicy.DROP_NEW);
        final int first = audio.play(shot, 0);
        audio.play(alarm, 0);
        audio.play(alarm, 0);

        assertEquals(AudioBackend.NO_VOICE, audio.play(shot, Audio.MAX_PRIORITY));
        assertTrue(audio.isPlaying(first));
        assertEquals(3, backend.getPlayingCount());

        backend.advance(SHOT_FRAMES);
        inbox.deliverAll();
        assertTrue(audio.play(shot) > 0);
    }

    /**
     * Refuse throws and keeps the playing voices.
     */
    @Test
    void testRefuse()
    {
        final Audio audio = audio(VoicePolicy.REFUSE);
        audio.play(alarm);
        audio.play(alarm);
        audio.play(alarm);

        assertThrows(IllegalStateException.class, () -> audio.play(shot));
        assertEquals(3, audio.getPlayingCount());
        assertEquals(3, backend.getPlayingCount());
    }

    /**
     * A backend without free voice plays nothing; the slot is not taken.
     */
    @Test
    void testBackendBusy()
    {
        final FakeAudioBackend small = new FakeAudioBackend(inbox, 2);
        final Audio audio = new Audio(small);
        final int other = small.play(alarm.getBuffer());

        audio.play(alarm);
        assertEquals(AudioBackend.NO_VOICE, audio.play(alarm));
        assertEquals(1, audio.getPlayingCount());
        assertEquals(2, audio.getMaxVoices());
        assertTrue(small.isPlaying(other));
    }

    /**
     * A lazy sound decodes at its first play; a released sound and invalid arguments are refused.
     */
    @Test
    void testArguments()
    {
        final Audio audio = new Audio(backend);
        final ShortSound lazy = cache.loadLazily(new ResourceKey("sounds/lazy.wav"));
        final ShortSound released = cache.load(SHOT);
        released.release();

        assertThrows(ResourceException.class, () -> audio.play(lazy));
        assertThrows(IllegalStateException.class, () -> audio.play(released));
        assertThrows(IllegalArgumentException.class, () -> audio.play((ShortSound) null));
        assertThrows(IllegalArgumentException.class, () -> audio.play(shot, -1));
        assertThrows(IllegalArgumentException.class, () -> audio.play(shot, 101));
        assertThrows(IllegalArgumentException.class, () -> new Audio(null));
        assertThrows(IllegalArgumentException.class, () -> new Audio(backend, 0, VoicePolicy.STEAL));
        assertThrows(IllegalArgumentException.class, () -> new Audio(backend, 33, VoicePolicy.STEAL));
        assertThrows(IllegalArgumentException.class, () -> new Audio(backend, 1, null));
        assertEquals(0, audio.getPlayingCount());
    }

    /**
     * Playing, stealing, stopping and ending voices allocate nothing in steady state (ADR-0019, REQ-RT-006); the
     * backend used allocates nothing either.
     */
    @Test
    void testAllocatesNothing()
    {
        final SilentAudioBackend silent = new SilentAudioBackend();
        final Audio audio = new Audio(silent, 4, VoicePolicy.STEAL);
        final PcmBuffer buffer = shot.getBuffer();
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        run(audio, silent);

        final long before = bean.getCurrentThreadAllocatedBytes();
        run(audio, silent);
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertEquals(buffer, shot.getBuffer());
    }

    /**
     * Plays many sounds, steals and stops voices, and ends some.
     *
     * @param audio The audio system.
     * @param silent The backend.
     */
    private void run(Audio audio, SilentAudioBackend silent)
    {
        for (int i = 0; i < ITERATIONS; i++)
        {
            final int voice = audio.play(shot, i % Audio.MAX_PRIORITY);
            if (i % 3 == 0)
            {
                audio.stop(voice);
            }
            else if (i % 3 == 1)
            {
                silent.end(voice);
            }
        }
        audio.stopAll();
    }
}
