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
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Tests {@link Audio} with variant sets on the fake backend: a random set plays its chosen variant, a composed set
 * starts every layer in one call, under each overflow policy, and the play path allocates nothing (REQ-AUDIO-005,
 * REQ-AUDIO-010, ADR-0067).
 */
final class AudioVariantSetTest
{
    /** Seed of the random sets. */
    private static final long SEED = 7L;
    /** Format of the sounds. */
    private static final PcmFormat FORMAT = new PcmFormat(1_000, PcmFormat.MONO);
    /** Frames of each layer, the shortest first. */
    private static final int[] FRAMES =
    {
        100, 200, 300
    };
    /** Low priority of the voices already playing. */
    private static final int LOW = 10;
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
    /** The sounds, of {@link #FRAMES} frames. */
    private ShortSound[] sounds;

    /**
     * Creates the backend, the cache and the sounds.
     */
    @BeforeEach
    void prepare()
    {
        final FakeResourceProvider provider = new FakeResourceProvider(false);
        final ResourceKey[] keys = new ResourceKey[FRAMES.length];
        for (int i = 0; i < FRAMES.length; i++)
        {
            keys[i] = new ResourceKey("sounds/layer" + i + ".wav");
            provider.put(keys[i], WavWriter.write(new PcmBuffer(FORMAT, new short[FRAMES[i]]), 16));
        }
        inbox = new InboxSpy();
        backend = new FakeAudioBackend(inbox);
        cache = new ShortSoundCache(new Resources(provider));
        sounds = new ShortSound[FRAMES.length];
        for (int i = 0; i < FRAMES.length; i++)
        {
            sounds[i] = cache.load(keys[i]);
        }
    }

    /**
     * Fills an audio system with voices at a low priority.
     *
     * @param audio The audio system.
     * @param voices The number of voices to play.
     * @return The voices, the oldest first.
     */
    private int[] fill(Audio audio, int voices)
    {
        final int[] played = new int[voices];
        for (int i = 0; i < voices; i++)
        {
            played[i] = audio.play(sounds[2], LOW);
        }
        return played;
    }

    /**
     * A random set plays the variant it chose, at the given priority, on a new voice.
     */
    @Test
    void testPlayRandom()
    {
        final Audio audio = new Audio(backend, 4, VoicePolicy.STEAL);
        final VariantSet set = VariantSet.random(SEED, sounds);

        final int first = audio.play(set);
        final int second = audio.play(set, Audio.MAX_PRIORITY);

        assertTrue(first > 0 && second > first);
        final List<PcmBuffer> played = backend.getPlayed();
        assertEquals(2, played.size());
        assertSame(sounds[set.getLastIndex()].getBuffer(), played.get(1));
        assertFalse(played.get(0) == played.get(1));
        assertEquals(2, audio.getPlayingCount());
    }

    /**
     * A random set played with a voices array writes its voice to the first entry only.
     */
    @Test
    void testPlayRandomArray()
    {
        final Audio audio = new Audio(backend, 4, VoicePolicy.STEAL);
        final VariantSet set = VariantSet.random(SEED, sounds);
        final int[] voices =
        {
            -5, -5
        };

        assertEquals(1, audio.play(set, Audio.DEFAULT_PRIORITY, voices));

        assertTrue(audio.isPlaying(voices[0]));
        assertEquals(-5, voices[1]);
    }

    /**
     * Every layer of a composed set starts in the same call, on the same clock, and each ends after its own length.
     */
    @Test
    void testComposedTogether()
    {
        final Audio audio = new Audio(backend, 4, VoicePolicy.STEAL);
        final VariantSet set = VariantSet.composed(sounds);
        final int[] voices = new int[set.getVoicesPerPlay()];

        assertEquals(FRAMES.length, audio.play(set, Audio.DEFAULT_PRIORITY, voices));

        final List<PcmBuffer> played = backend.getPlayed();
        for (int i = 0; i < FRAMES.length; i++)
        {
            assertSame(sounds[i].getBuffer(), played.get(i));
            assertTrue(audio.isPlaying(voices[i]));
        }
        backend.advance(FRAMES[0]);
        inbox.deliverAll();
        assertFalse(audio.isPlaying(voices[0]));
        assertTrue(audio.isPlaying(voices[1]));
        backend.advance(FRAMES[2] - FRAMES[0]);
        inbox.deliverAll();
        assertEquals(0, audio.getPlayingCount());
        assertThrows(IllegalArgumentException.class, () -> audio.play(set));
        assertThrows(IllegalArgumentException.class, () -> audio.play(set, Audio.DEFAULT_PRIORITY));
    }

    /**
     * Under the stealing policy, the first layer takes the free voice and each other layer the oldest voice of the
     * lowest priority.
     */
    @Test
    void testComposedSteal()
    {
        final Audio audio = new Audio(backend, 3, VoicePolicy.STEAL);
        final int[] old = fill(audio, 2);
        final VariantSet set = VariantSet.composed(sounds);
        final int[] voices = new int[set.getVoicesPerPlay()];

        assertEquals(FRAMES.length, audio.play(set, Audio.DEFAULT_PRIORITY, voices));

        assertFalse(audio.isPlaying(old[0]));
        assertFalse(audio.isPlaying(old[1]));
        for (final int voice : voices)
        {
            assertTrue(audio.isPlaying(voice));
        }
        assertEquals(3, audio.getPlayingCount());
    }

    /**
     * At a priority below the voices playing, the first layer takes the free voice and the others find none: they
     * may neither steal a more important voice nor the first layer.
     */
    @Test
    void testComposedStealLowerPriority()
    {
        final Audio audio = new Audio(backend, 3, VoicePolicy.STEAL);
        final int[] old = fill(audio, 2);
        final VariantSet set = VariantSet.composed(sounds);
        final int[] voices = new int[set.getVoicesPerPlay()];

        assertEquals(1, audio.play(set, LOW - 1, voices));

        assertTrue(audio.isPlaying(old[0]));
        assertTrue(audio.isPlaying(old[1]));
        assertTrue(audio.isPlaying(voices[0]));
        assertEquals(AudioBackend.NO_VOICE, voices[1]);
        assertEquals(AudioBackend.NO_VOICE, voices[2]);
    }

    /**
     * A composed set with more layers than voices starts as many layers as voices, without one stealing another.
     */
    @Test
    void testComposedNoSelfSteal()
    {
        final Audio audio = new Audio(backend, 2, VoicePolicy.STEAL);
        final VariantSet set = VariantSet.composed(sounds);
        final int[] voices = new int[set.getVoicesPerPlay()];

        assertEquals(2, audio.play(set, Audio.DEFAULT_PRIORITY, voices));

        assertTrue(audio.isPlaying(voices[0]));
        assertTrue(audio.isPlaying(voices[1]));
        assertEquals(AudioBackend.NO_VOICE, voices[2]);
    }

    /**
     * Under drop-new, the layers that find no free voice are not played.
     */
    @Test
    void testComposedDropNew()
    {
        final Audio audio = new Audio(backend, 3, VoicePolicy.DROP_NEW);
        final int[] old = fill(audio, 2);
        final VariantSet set = VariantSet.composed(sounds);
        final int[] voices = new int[set.getVoicesPerPlay()];

        assertEquals(1, audio.play(set, Audio.MAX_PRIORITY, voices));

        assertTrue(audio.isPlaying(old[0]));
        assertTrue(audio.isPlaying(voices[0]));
        assertEquals(AudioBackend.NO_VOICE, voices[1]);
        assertEquals(AudioBackend.NO_VOICE, voices[2]);
    }

    /**
     * Under refuse, a composed set without a free voice per layer is refused before any layer starts.
     */
    @Test
    void testComposedRefuse()
    {
        final Audio audio = new Audio(backend, 3, VoicePolicy.REFUSE);
        fill(audio, 1);
        final VariantSet set = VariantSet.composed(sounds);
        final int[] voices = new int[set.getVoicesPerPlay()];

        assertThrows(IllegalStateException.class, () -> audio.play(set, Audio.DEFAULT_PRIORITY, voices));

        assertEquals(1, audio.getPlayingCount());
        assertEquals(1, backend.getPlayed().size());
        audio.stopAll();
        assertEquals(FRAMES.length, audio.play(set, Audio.DEFAULT_PRIORITY, voices));
    }

    /**
     * A set of which a sound is released is refused, and no layer of a composed set starts.
     */
    @Test
    void testReleased()
    {
        final Audio audio = new Audio(backend, 4, VoicePolicy.STEAL);
        final VariantSet composed = VariantSet.composed(sounds);
        final VariantSet random = VariantSet.random(SEED, sounds[2]);
        final int[] voices = new int[composed.getVoicesPerPlay()];
        sounds[2].release();

        assertThrows(IllegalStateException.class, () -> audio.play(composed, Audio.DEFAULT_PRIORITY, voices));
        assertThrows(IllegalStateException.class, () -> audio.play(random));

        assertEquals(0, backend.getPlayed().size());
    }

    /**
     * A lazy layer is decoded at the first play of its set.
     */
    @Test
    void testLazyLayer()
    {
        final Audio audio = new Audio(backend, 4, VoicePolicy.STEAL);
        final ShortSound lazy = cache.loadLazily(new ResourceKey("sounds/layer0.wav"));
        final VariantSet set = VariantSet.composed(lazy, sounds[1]);
        final int[] voices = new int[set.getVoicesPerPlay()];
        sounds[0].release();

        assertEquals(2, audio.play(set, Audio.DEFAULT_PRIORITY, voices));

        assertTrue(lazy.isLoaded());
    }

    /**
     * Invalid arguments are refused before anything starts.
     */
    @Test
    void testArguments()
    {
        final Audio audio = new Audio(backend, 4, VoicePolicy.STEAL);
        final VariantSet set = VariantSet.composed(sounds);
        final int[] voices = new int[set.getVoicesPerPlay()];
        final int[] tooShort = new int[set.getVoicesPerPlay() - 1];

        assertThrows(IllegalArgumentException.class, () -> audio.play((VariantSet) null));
        assertThrows(IllegalArgumentException.class, () -> audio.play(null, Audio.DEFAULT_PRIORITY, voices));
        assertThrows(IllegalArgumentException.class, () -> audio.play(set, Audio.DEFAULT_PRIORITY, null));
        assertThrows(IllegalArgumentException.class, () -> audio.play(set, Audio.DEFAULT_PRIORITY, tooShort));
        assertThrows(IllegalArgumentException.class, () -> audio.play(set, Audio.MAX_PRIORITY + 1, voices));
        assertThrows(IllegalArgumentException.class, () -> audio.play(set, Audio.MIN_PRIORITY - 1, voices));

        assertEquals(0, backend.getPlayed().size());
    }

    /**
     * Playing random and composed sets, stealing, stopping and ending their voices allocate nothing in steady state
     * (ADR-0019, REQ-RT-006).
     */
    @Test
    void testAllocatesNothing()
    {
        final SilentAudioBackend silent = new SilentAudioBackend();
        final Audio audio = new Audio(silent, 4, VoicePolicy.STEAL);
        final VariantSet random = VariantSet.random(SEED, sounds, new double[]
        {
            1.0, 2.0, 0.5
        });
        final VariantSet composed = VariantSet.composed(sounds);
        final int[] voices = new int[composed.getVoicesPerPlay()];
        final Runnable workload = () -> run(audio, silent, random, composed, voices);

        final long allocated = SilentAudioBackend.steadyAllocation(workload);

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
    }

    /**
     * Plays many sets, stops and ends some voices.
     *
     * @param audio The audio system.
     * @param silent The backend.
     * @param random The random set.
     * @param composed The composed set.
     * @param voices The voices array.
     */
    private static void run(Audio audio,
                            SilentAudioBackend silent,
                            VariantSet random,
                            VariantSet composed,
                            int[] voices)
    {
        for (int i = 0; i < ITERATIONS; i++)
        {
            final int priority = i % Audio.MAX_PRIORITY;
            final int voice = audio.play(random, priority);
            audio.play(composed, priority, voices);
            if (i % 2 == 0)
            {
                audio.stop(voice);
            }
            else
            {
                silent.end(voices[0]);
            }
        }
        audio.stopAll();
    }
}
