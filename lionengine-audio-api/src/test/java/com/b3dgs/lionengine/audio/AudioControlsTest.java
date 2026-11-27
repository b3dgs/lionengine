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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Tests of the controls of {@link Audio} on short sounds, on {@link FakeAudioBackend} and its clock: gain products and
 * refusals, looping without end, pause and resume of a voice and of the master, and their interaction with stop,
 * stealing and release; the control paths allocate nothing (ADR-0031, ADR-0033, ADR-0069, REQ-AUDIO-011).
 */
final class AudioControlsTest
{
    /** Format of the sounds. */
    private static final PcmFormat FORMAT = new PcmFormat(1_000, PcmFormat.MONO);
    /** Key of the sound. */
    private static final ResourceKey SHOT = new ResourceKey("sounds/shot.wav");
    /** Frames of the sound. */
    private static final int FRAMES = 100;
    /** Tolerance of the gain comparisons. */
    private static final double EPSILON = 1e-12;
    /** Rounds of the allocation test. */
    private static final int ITERATIONS = 10_000;
    /** Bytes tolerated by the allocation test, for the measurement itself. */
    private static final long ALLOCATION_TOLERANCE = 1_024L;

    /** The inbox of the backend. */
    private InboxSpy inbox;
    /** The fake backend. */
    private FakeAudioBackend backend;
    /** The cache. */
    private ShortSoundCache cache;
    /** The sound. */
    private ShortSound shot;

    /**
     * Creates the backend, the cache and the sound.
     */
    @BeforeEach
    void prepare()
    {
        final FakeResourceProvider provider = new FakeResourceProvider(false);
        provider.put(SHOT, WavWriter.write(new PcmBuffer(FORMAT, new short[FRAMES]), 16));
        inbox = new InboxSpy();
        backend = new FakeAudioBackend(inbox);
        cache = new ShortSoundCache(new Resources(provider));
        shot = cache.load(SHOT);
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
     * The effective gain of a voice is master × sound, given at the play; a change of the sound reaches every voice
     * playing it, and only them; a change of the master reaches every voice; both default to the full gain.
     */
    @Test
    void testGainProducts()
    {
        final Audio audio = new Audio(backend);
        final ShortSound other = cache.load(SHOT);
        assertEquals(Audio.MAX_GAIN, audio.getMasterGain());
        assertEquals(Audio.MAX_GAIN, shot.getGain());
        final int full = audio.play(shot);
        assertEquals(1.0, backend.getGain(full));

        audio.setMasterGain(0.5);
        shot.setGain(0.4);
        final int first = audio.play(shot);
        final int second = audio.play(other);

        assertEquals(0.2, backend.getGain(full), EPSILON);
        assertEquals(0.2, backend.getGain(first), EPSILON);
        assertEquals(0.5, backend.getGain(second), EPSILON);
        assertEquals(0.4, shot.getGain());
        assertEquals(Audio.MAX_GAIN, other.getGain());

        shot.setGain(0.0);
        assertEquals(0.0, backend.getGain(first));
        assertEquals(0.5, backend.getGain(second), EPSILON);
        audio.setMasterGain(1.0);
        assertEquals(0.0, backend.getGain(first));
        assertEquals(1.0, backend.getGain(second));

        play(FRAMES);
        shot.setGain(1.0);
        audio.setMasterGain(0.25);
        assertEquals(0, audio.getPlayingCount());
        assertEquals(0.25, backend.getGain(audio.play(shot)), EPSILON);
    }

    /**
     * Gains out of {@code [0, 1]}, infinite and NaN are refused, and nothing changes.
     */
    @Test
    void testGainRefused()
    {
        final Audio audio = new Audio(backend);
        final double[] refused =
        {
            -0.01, 1.01, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, -Double.MIN_VALUE
        };
        audio.setMasterGain(0.5);
        shot.setGain(0.5);

        for (final double gain : refused)
        {
            assertThrows(IllegalArgumentException.class, () -> audio.setMasterGain(gain));
            assertThrows(IllegalArgumentException.class, () -> shot.setGain(gain));
            assertThrows(IllegalArgumentException.class, () -> backend.setGain(1, gain));
        }

        assertEquals(0.5, audio.getMasterGain());
        assertEquals(0.5, shot.getGain());
        audio.setMasterGain(Audio.MIN_GAIN);
        assertEquals(0.0, backend.getGain(audio.play(shot)));
    }

    /**
     * A looping sound plays on a looping voice that never ends and posts no end, until it is stopped; a change of the
     * looping of the handle reaches the next plays only.
     */
    @Test
    void testLooping()
    {
        final Audio audio = new Audio(backend);
        assertFalse(shot.isLooping());
        shot.setLooping(true);
        final int voice = audio.play(shot);

        play(10 * FRAMES + 7);
        shot.setLooping(false);
        play(10 * FRAMES);

        assertTrue(backend.isLooping(voice));
        assertTrue(audio.isPlaying(voice));
        assertEquals(20L * FRAMES + 7L, backend.getPlayedFrames(voice));
        assertTrue(inbox.getPosted().isEmpty());
        final int once = audio.play(shot);
        assertFalse(backend.isLooping(once));
        audio.stop(voice);
        play(2 * FRAMES);
        assertFalse(audio.isPlaying(voice));
        assertFalse(audio.isPlaying(once));
        assertEquals(1, inbox.getPosted().size());
    }

    /**
     * A paused voice holds where it is and posts no end; resumed, it plays its remaining frames; pausing or resuming
     * twice, an ended or an unknown voice has no effect.
     */
    @Test
    void testPauseResume()
    {
        final Audio audio = new Audio(backend);
        final int voice = audio.play(shot);
        play(FRAMES / 2);

        audio.pause(voice);
        audio.pause(voice);
        play(10 * FRAMES);

        assertTrue(audio.isPaused(voice));
        assertTrue(backend.isPaused(voice));
        assertTrue(audio.isPlaying(voice));
        assertEquals(1, audio.getPlayingCount());
        audio.resume(voice);
        audio.resume(voice);
        assertFalse(audio.isPaused(voice));
        play(FRAMES / 2 - 1);
        assertTrue(audio.isPlaying(voice));
        play(1);
        assertFalse(audio.isPlaying(voice));

        audio.pause(voice);
        audio.resume(voice);
        audio.pause(Integer.MAX_VALUE);
        audio.resume(AudioBackend.NO_VOICE);
        assertFalse(audio.isPaused(voice));
        assertFalse(audio.isPaused(Integer.MAX_VALUE));
    }

    /**
     * The master pause pauses the output: nothing moves, sounds played meanwhile start at its end, and a voice paused on
     * its own stays paused when it ends; setting it twice has no effect.
     */
    @Test
    void testMasterPause()
    {
        final Audio audio = new Audio(backend);
        final int alone = audio.play(shot);
        final int other = audio.play(shot);
        audio.pause(alone);

        audio.setMasterPaused(true);
        audio.setMasterPaused(true);
        final int during = audio.play(shot);
        play(10 * FRAMES);

        assertTrue(audio.isMasterPaused());
        assertTrue(backend.isOutputPaused());
        assertEquals(0L, backend.getPlayedFrames(other));
        assertEquals(0L, backend.getPlayedFrames(during));
        assertFalse(audio.isPaused(other));
        assertTrue(audio.isPaused(alone));

        audio.setMasterPaused(false);
        audio.setMasterPaused(false);
        play(FRAMES);

        assertFalse(audio.isMasterPaused());
        assertFalse(backend.isOutputPaused());
        assertFalse(audio.isPlaying(other));
        assertFalse(audio.isPlaying(during));
        assertTrue(audio.isPlaying(alone));
        assertTrue(audio.isPaused(alone));
        audio.resume(alone);
        play(FRAMES);
        assertFalse(audio.isPlaying(alone));
    }

    /**
     * A paused voice keeps its voice: it counts in the limit, so a new sound is dropped under
     * {@link VoicePolicy#DROP_NEW}, and it may be stolen as any voice under {@link VoicePolicy#STEAL}; stopping a paused
     * voice frees it and posts no end, as {@link Audio#stopAll()} does.
     */
    @Test
    void testPausedVoiceHoldsItsVoice()
    {
        final Audio dropping = new Audio(backend, 1, VoicePolicy.DROP_NEW);
        final int held = dropping.play(shot);
        dropping.pause(held);
        assertEquals(AudioBackend.NO_VOICE, dropping.play(shot));
        dropping.stop(held);
        assertFalse(dropping.isPaused(held));
        assertFalse(backend.isPlaying(held));

        final Audio stealing = new Audio(backend, 2, VoicePolicy.STEAL);
        final int oldest = stealing.play(shot, 10);
        final int newer = stealing.play(shot, 10);
        stealing.pause(oldest);
        final int stolen = stealing.play(shot, 10);

        assertFalse(stealing.isPlaying(oldest));
        assertFalse(stealing.isPaused(oldest));
        assertFalse(backend.isPlaying(oldest));
        assertTrue(stealing.isPlaying(newer));
        assertTrue(stealing.isPlaying(stolen));
        stealing.pause(newer);
        stealing.stopAll();
        play(10 * FRAMES);
        assertFalse(stealing.isPaused(newer));
        assertEquals(0, backend.getPlayingCount());
        assertTrue(inbox.getPosted().isEmpty());
    }

    /**
     * Releasing a sound stops its looping voices, which would never end; its other voices play to their end, and a
     * paused one stays paused.
     */
    @Test
    void testReleaseStopsLooping()
    {
        final Audio audio = new Audio(backend);
        shot.setLooping(true);
        final int looping = audio.play(shot);
        shot.setLooping(false);
        final int once = audio.play(shot);
        final int paused = audio.play(shot);
        audio.pause(paused);

        shot.release();

        assertFalse(audio.isPlaying(looping));
        assertFalse(backend.isPlaying(looping));
        assertTrue(audio.isPlaying(once));
        assertTrue(audio.isPaused(paused));
        play(FRAMES);
        assertFalse(audio.isPlaying(once));
        assertTrue(audio.isPlaying(paused));
        audio.stop(paused);
        shot.release();
        assertEquals(0, backend.getPlayingCount());
    }

    /**
     * Changing the gains, pausing and resuming voices and the master, and playing looping sounds allocate nothing:
     * they run as often as the game wants.
     */
    @Test
    void testAllocatesNothing()
    {
        final SilentAudioBackend silent = new SilentAudioBackend();
        final Audio audio = new Audio(silent, 4, VoicePolicy.STEAL);
        shot.setLooping(true);
        final Runnable workload = () ->
        {
            for (int i = 0; i < ITERATIONS; i++)
            {
                final int voice = audio.play(shot, i % Audio.MAX_PRIORITY);
                audio.setMasterGain(i % 10 / 10.0);
                shot.setGain(i % 7 / 7.0);
                audio.pause(voice);
                audio.setMasterPaused(i % 2 == 0);
                audio.resume(voice);
                if (audio.isPaused(voice) || i % 3 == 0)
                {
                    audio.stop(voice);
                }
            }
            audio.stopAll();
        };

        final long allocated = SilentAudioBackend.steadyAllocation(workload);

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
    }
}
