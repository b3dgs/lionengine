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

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Tests of the fades of {@link Audio} on {@link FakeAudioBackend} and its clock: fades of a short sound, of a long
 * sound and of a group, their linear progression, their completion through the inbox to the fade listener, and their
 * interaction with gain changes, pause, the master pause, stop, stealing and a new fade; the group and fade paths
 * allocate nothing (ADR-0033, ADR-0071, REQ-AUDIO-012).
 */
final class AudioFadeTest
{
    /** Sample rate of the sounds: one frame per millisecond. */
    private static final int RATE = 1_000;
    /** Format of the short sounds. */
    private static final PcmFormat FORMAT = new PcmFormat(RATE, PcmFormat.MONO);
    /** Key of the short sound. */
    private static final ResourceKey HUM = new ResourceKey("sounds/hum.wav");
    /** Key of the music. */
    private static final ResourceKey MUSIC = new ResourceKey("music/theme.wav");
    /** Frames of the short sound: 2 s. */
    private static final int FRAMES = 2_000;
    /** Sample rate of the music, as written by {@link LongSoundTest#wav(int)}. */
    private static final int MUSIC_RATE = 8_000;
    /** Frames of the music: 2.5 s. */
    private static final int MUSIC_FRAMES = 20_000;
    /** Duration of the fades: 0.5 s. */
    private static final double SECONDS = 0.5;
    /** Frames of a fade of a short sound. */
    private static final int FADE_FRAMES = 500;
    /** Tolerance of the gain comparisons. */
    private static final double EPSILON = 1e-9;
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
    /** The long sound loader. */
    private LongSoundLoader loader;
    /** The audio system. */
    private Audio audio;
    /** The short sound. */
    private ShortSound hum;
    /** The voices whose fade ended, in order. */
    private final List<Integer> faded = new ArrayList<>();

    /**
     * Creates the backend, the sounds and the audio system, listening to the fades.
     */
    @BeforeEach
    void prepare()
    {
        final FakeResourceProvider provider = new FakeResourceProvider(false);
        provider.put(HUM, WavWriter.write(new PcmBuffer(FORMAT, new short[FRAMES]), 16));
        provider.put(MUSIC, LongSoundTest.wav(MUSIC_FRAMES));
        inbox = new InboxSpy();
        backend = new FakeAudioBackend(inbox);
        final Resources resources = new Resources(provider);
        cache = new ShortSoundCache(resources);
        loader = new LongSoundLoader(resources);
        audio = new Audio(backend);
        audio.setFadeListener(faded::add);
        hum = cache.load(HUM);
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
     * A sound fade takes the target gain at once on the handle and ramps the voice linearly in the backend; its end
     * reaches the listener once, through the inbox, and the voice plays on at the target, also at {@code 0}.
     */
    @Test
    void testShortSoundFade()
    {
        final int voice = audio.play(hum);

        hum.fade(0.0, SECONDS);

        assertEquals(0.0, hum.getGain());
        assertTrue(backend.isFading(voice));
        assertEquals(0.0, backend.getFadeTarget(voice));
        play(FADE_FRAMES / 4);
        assertEquals(0.75, backend.getGain(voice), EPSILON);
        play(FADE_FRAMES / 4);
        assertEquals(0.5, backend.getGain(voice), EPSILON);
        assertTrue(faded.isEmpty());
        backend.advance(FADE_FRAMES / 2);
        assertTrue(faded.isEmpty());
        inbox.deliverAll();
        assertEquals(List.of(Integer.valueOf(voice)), faded);
        assertEquals(0.0, backend.getGain(voice));
        assertTrue(audio.isPlaying(voice));
        assertFalse(backend.isFading(voice));
        play(FADE_FRAMES);
        assertEquals(1, faded.size());
    }

    /**
     * Every voice of a fading short sound ramps to its own effective gain and reports its end; a voice played during
     * the fade starts at the target; a sound playing on no voice only takes the gain.
     */
    @Test
    void testShortSoundFadeEveryVoice()
    {
        final MixerGroup ui = audio.getGroup(MixerGroup.UI);
        ui.setGain(0.5);
        final int effect = audio.play(hum);
        hum.setGroup(MixerGroup.UI);
        final int interfaceVoice = audio.play(hum);

        hum.fade(0.5, SECONDS);
        final int during = audio.play(hum);

        assertEquals(0.5, backend.getFadeTarget(effect), EPSILON);
        assertEquals(0.25, backend.getFadeTarget(interfaceVoice), EPSILON);
        assertEquals(0.25, backend.getGain(during), EPSILON);
        assertFalse(backend.isFading(during));
        play(FADE_FRAMES);
        assertEquals(List.of(Integer.valueOf(effect), Integer.valueOf(interfaceVoice)), faded);
        final ShortSound idle = cache.load(HUM);
        idle.fade(0.2, SECONDS);
        assertEquals(0.2, idle.getGain());
        play(FADE_FRAMES);
        assertEquals(2, faded.size());
    }

    /**
     * A long sound fades on its voice; a music fade-out stopped at its completion frees the voice; a long sound that
     * does not play only takes the gain.
     */
    @Test
    void testLongSoundFadeOut()
    {
        final LongSound music = loader.stream(MUSIC);
        final int voice = audio.play(music);
        audio.setFadeListener(ended -> audio.stop(ended));

        music.fade(0.0, 1.0);

        assertEquals(0.0, backend.getFadeTarget(voice));
        play(MUSIC_RATE / 2);
        assertEquals(0.5, backend.getGain(voice), EPSILON);
        assertTrue(music.isPlaying());
        play(MUSIC_RATE / 2);
        assertFalse(music.isPlaying());
        assertFalse(audio.isPlaying(voice));
        music.fade(1.0, 1.0);
        assertEquals(1.0, music.getGain());
        assertEquals(0, backend.getPlayingCount());
    }

    /**
     * A group fade ramps every voice of the group, short and long, to master × target × sound, and none of another
     * group; each voice reports its end; a sound of the group played during the fade starts at the target.
     */
    @Test
    void testGroupFade()
    {
        final MixerGroup musicGroup = audio.getGroup(MixerGroup.MUSIC);
        final LongSound music = loader.stream(MUSIC);
        final ShortSound sting = cache.load(HUM);
        sting.setGroup(MixerGroup.MUSIC);
        sting.setGain(0.5);
        audio.setMasterGain(0.8);
        final int song = audio.play(music);
        final int stingVoice = audio.play(sting);
        final int effect = audio.play(hum);

        musicGroup.fade(0.25, SECONDS);

        assertEquals(0.25, musicGroup.getGain());
        assertEquals(0.8 * 0.25, backend.getFadeTarget(song), EPSILON);
        assertEquals(0.8 * 0.25 * 0.5, backend.getFadeTarget(stingVoice), EPSILON);
        assertFalse(backend.isFading(effect));
        assertEquals(0.8 * 0.25, backend.getGain(audio.play(loader.stream(MUSIC))), EPSILON);
        play(FADE_FRAMES);
        assertEquals(List.of(Integer.valueOf(stingVoice)), faded);
        play(MUSIC_RATE);
        assertEquals(List.of(Integer.valueOf(stingVoice), Integer.valueOf(song)), faded);
        assertEquals(0.8 * 0.25, backend.getGain(song), EPSILON);
    }

    /**
     * A gain change of the master, of the group or of the sound during a fade does not cut it: the ramp goes on to the
     * new effective gain and completes at its time.
     */
    @Test
    void testGainChangeDuringFade()
    {
        final int voice = audio.play(hum);
        hum.fade(0.0, SECONDS);
        play(FADE_FRAMES / 2);

        audio.setMasterGain(0.5);
        hum.setGain(0.5);
        audio.getGroup(MixerGroup.EFFECTS).setGain(0.5);

        assertTrue(backend.isFading(voice));
        assertEquals(0.125, backend.getFadeTarget(voice), EPSILON);
        assertEquals(0.5, backend.getGain(voice), EPSILON);
        play(FADE_FRAMES / 4);
        assertEquals((0.5 + 0.125) / 2, backend.getGain(voice), EPSILON);
        assertTrue(faded.isEmpty());
        play(FADE_FRAMES / 4);
        assertEquals(List.of(Integer.valueOf(voice)), faded);
        assertEquals(0.125, backend.getGain(voice), EPSILON);
    }

    /**
     * A new fade replaces the ramp from the gain reached: the replaced one reports nothing.
     */
    @Test
    void testNewFadeReplaces()
    {
        final int voice = audio.play(hum);
        hum.fade(0.0, SECONDS);
        play(FADE_FRAMES / 2);

        hum.fade(1.0, SECONDS);
        play(FADE_FRAMES / 2);

        assertTrue(faded.isEmpty());
        assertEquals(0.75, backend.getGain(voice), EPSILON);
        play(FADE_FRAMES / 2);
        assertEquals(List.of(Integer.valueOf(voice)), faded);
        assertEquals(1.0, backend.getGain(voice), EPSILON);
    }

    /**
     * A fade holds while its voice is paused and during the master pause, and completes once resumed.
     */
    @Test
    void testFadePaused()
    {
        final int voice = audio.play(hum);
        hum.fade(0.0, SECONDS);
        play(FADE_FRAMES / 2);

        audio.pause(voice);
        play(FADE_FRAMES);
        assertEquals(0.5, backend.getGain(voice), EPSILON);
        audio.resume(voice);
        audio.setMasterPaused(true);
        play(FADE_FRAMES);
        assertEquals(0.5, backend.getGain(voice), EPSILON);
        assertTrue(faded.isEmpty());
        audio.setMasterPaused(false);
        play(FADE_FRAMES / 2);

        assertEquals(List.of(Integer.valueOf(voice)), faded);
    }

    /**
     * A fade is dropped without completion when its voice is stopped, stolen or ends; a completion posted before the
     * voice is stopped is not reported; without listener nothing is reported.
     */
    @Test
    void testFadeDropped()
    {
        final Audio small = new Audio(backend, 2, VoicePolicy.STEAL);
        small.setFadeListener(faded::add);
        final ShortSound brief = cache.load(HUM);
        final int stopped = small.play(hum);
        final int stolen = small.play(brief);
        hum.fade(0.0, SECONDS);
        brief.fade(0.0, SECONDS);

        small.stop(stopped);
        small.play(cache.load(HUM));
        small.play(cache.load(HUM));
        play(FADE_FRAMES);
        assertTrue(faded.isEmpty());
        assertFalse(small.isPlaying(stolen));

        final ShortSound late = cache.load(HUM);
        final int voice = small.play(late);
        late.fade(0.5, SECONDS);
        backend.advance(FADE_FRAMES);
        small.stop(voice);
        inbox.deliverAll();
        assertTrue(faded.isEmpty());

        final ShortSound once = cache.load(HUM);
        final int ending = small.play(once);
        once.fade(0.5, 10.0);
        play(FRAMES);
        assertFalse(small.isPlaying(ending));
        small.setFadeListener(null);
        final int unheard = small.play(once);
        once.fade(0.2, 0.0);
        play(1);
        assertTrue(small.isPlaying(unheard));
        assertTrue(faded.isEmpty());
    }

    /**
     * Invalid gains and durations are refused and nothing changes.
     */
    @Test
    void testFadeRefused()
    {
        final LongSound music = loader.stream(MUSIC);
        final MixerGroup ui = audio.getGroup(MixerGroup.UI);
        hum.setGain(0.5);
        final int voice = audio.play(hum);
        for (final double seconds : new double[]
        {
            -1.0, Double.NaN, Double.POSITIVE_INFINITY
        })
        {
            assertThrows(IllegalArgumentException.class, () -> hum.fade(0.0, seconds));
            assertThrows(IllegalArgumentException.class, () -> music.fade(0.0, seconds));
            assertThrows(IllegalArgumentException.class, () -> ui.fade(0.0, seconds));
        }
        assertThrows(IllegalArgumentException.class, () -> hum.fade(2.0, SECONDS));
        assertThrows(IllegalArgumentException.class, () -> music.fade(Double.NaN, SECONDS));

        assertEquals(0.5, hum.getGain());
        assertEquals(1.0, music.getGain());
        assertEquals(1.0, ui.getGain());
        assertFalse(backend.isFading(voice));
    }

    /**
     * Changing a group gain, fading sounds and groups, and the completions reaching the listener allocate nothing.
     */
    @Test
    void testAllocatesNothing()
    {
        final SilentAudioBackend silent = new SilentAudioBackend();
        final Audio quiet = new Audio(silent, 4, VoicePolicy.STEAL);
        final int[] heard = new int[1];
        quiet.setFadeListener(voice -> heard[0]++);
        final MixerGroup effects = quiet.getGroup(MixerGroup.EFFECTS);
        final MixerGroup ambient = quiet.addGroup("ambient");
        final ShortSound wind = cache.load(HUM);
        wind.setGroup("ambient");
        wind.setLooping(true);
        hum.setLooping(true);
        final int windVoice = quiet.play(wind);
        final Runnable workload = () ->
        {
            for (int i = 0; i < ITERATIONS; i++)
            {
                final int voice = quiet.play(hum, i % Audio.MAX_PRIORITY);
                effects.setGain(i % 10 / 10.0);
                ambient.fade(i % 7 / 7.0, i % 3);
                hum.fade(i % 5 / 5.0, SECONDS);
                quiet.getGroup(MixerGroup.UI).fade(1.0, 0.0);
                silent.faded(voice);
                silent.faded(windVoice);
                if (i % 3 == 0)
                {
                    quiet.stop(voice);
                }
            }
        };

        final long allocated = SilentAudioBackend.steadyAllocation(workload);

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertTrue(heard[0] > 0);
    }
}
