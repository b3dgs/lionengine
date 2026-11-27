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
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.resources.FakeResourceProvider;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;

/**
 * Tests of the mixer groups of {@link Audio} on {@link FakeAudioBackend}: the default groups and the groups of the
 * game, the default group of each family, the gain product master × group × sound, a group change reaching the playing
 * voices of the group only, the group read at the play, and the refusals (ADR-0033, ADR-0071, REQ-AUDIO-012).
 */
final class AudioMixerGroupTest
{
    /** Format of the short sounds. */
    private static final PcmFormat FORMAT = new PcmFormat(1_000, PcmFormat.MONO);
    /** Key of the short sound. */
    private static final ResourceKey SHOT = new ResourceKey("sounds/shot.wav");
    /** Key of the music. */
    private static final ResourceKey MUSIC = new ResourceKey("music/theme.wav");
    /** Frames of the short sound. */
    private static final int FRAMES = 100;
    /** Frames of the music. */
    private static final int MUSIC_FRAMES = 20_000;
    /** Tolerance of the gain comparisons. */
    private static final double EPSILON = 1e-12;

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

    /**
     * Creates the backend, the cache, the loader and the audio system.
     */
    @BeforeEach
    void prepare()
    {
        final FakeResourceProvider provider = new FakeResourceProvider(false);
        provider.put(SHOT, WavWriter.write(new PcmBuffer(FORMAT, new short[FRAMES]), 16));
        provider.put(MUSIC, LongSoundTest.wav(MUSIC_FRAMES));
        inbox = new InboxSpy();
        backend = new FakeAudioBackend(inbox);
        final Resources resources = new Resources(provider);
        cache = new ShortSoundCache(resources);
        loader = new LongSoundLoader(resources);
        audio = new Audio(backend);
    }

    /**
     * The groups {@code music}, {@code effects} and {@code ui} exist at full gain; a game adds its own by id, once;
     * each is found by its id.
     */
    @Test
    void testGroups()
    {
        assertEquals("music", MixerGroup.MUSIC);
        assertEquals("effects", MixerGroup.EFFECTS);
        assertEquals("ui", MixerGroup.UI);
        for (final String id : new String[]
        {
            MixerGroup.MUSIC, MixerGroup.EFFECTS, MixerGroup.UI
        })
        {
            assertTrue(audio.hasGroup(id));
            assertEquals(id, audio.getGroup(id).getId());
            assertEquals(Audio.MAX_GAIN, audio.getGroup(id).getGain());
        }
        assertFalse(audio.hasGroup("voice"));
        assertFalse(audio.hasGroup(null));
        assertFalse(audio.hasGroup(""));

        final MixerGroup voice = audio.addGroup("voice");
        final MixerGroup ambient = audio.addGroup("ambient");
        audio.addGroup("footsteps");

        assertSame(voice, audio.getGroup("voice"));
        assertSame(ambient, audio.getGroup("ambient"));
        assertTrue(audio.hasGroup("footsteps"));
        assertEquals(Audio.MAX_GAIN, voice.getGain());
        assertNotSame(audio.getGroup(MixerGroup.MUSIC), new Audio(backend).getGroup(MixerGroup.MUSIC));
    }

    /**
     * A null, empty, duplicated or unknown id is refused, as an invalid group gain, and nothing changes.
     */
    @Test
    void testGroupRefusals()
    {
        assertThrows(IllegalArgumentException.class, () -> audio.addGroup(null));
        assertThrows(IllegalArgumentException.class, () -> audio.addGroup(""));
        assertThrows(IllegalArgumentException.class, () -> audio.addGroup(MixerGroup.UI));
        assertThrows(IllegalArgumentException.class, () -> audio.getGroup(null));
        assertThrows(IllegalArgumentException.class, () -> audio.getGroup(""));
        assertThrows(IllegalArgumentException.class, () -> audio.getGroup("unknown"));
        final MixerGroup ui = audio.getGroup(MixerGroup.UI);
        ui.setGain(0.5);
        for (final double gain : new double[]
        {
            -0.1, 1.1, Double.NaN, Double.POSITIVE_INFINITY
        })
        {
            assertThrows(IllegalArgumentException.class, () -> ui.setGain(gain));
            assertThrows(IllegalArgumentException.class, () -> ui.fade(gain, 1.0));
        }
        assertEquals(0.5, ui.getGain());
        final ShortSound shot = cache.load(SHOT);
        assertThrows(IllegalArgumentException.class, () -> shot.setGroup(null));
        assertThrows(IllegalArgumentException.class, () -> shot.setGroup(""));
        assertEquals(MixerGroup.EFFECTS, shot.getGroup());
        final LongSound music = loader.stream(MUSIC);
        assertThrows(IllegalArgumentException.class, () -> music.setGroup(null));
        assertEquals(MixerGroup.MUSIC, music.getGroup());
    }

    /**
     * Short sounds are in {@code effects} and long sounds in {@code music} by default; the effective gain of a voice is
     * master × group × sound, at its play and after every change of one of them.
     */
    @Test
    void testGainProduct()
    {
        final ShortSound shot = cache.load(SHOT);
        final LongSound music = loader.stream(MUSIC);
        final MixerGroup effects = audio.getGroup(MixerGroup.EFFECTS);
        final MixerGroup musicGroup = audio.getGroup(MixerGroup.MUSIC);
        audio.setMasterGain(0.5);
        effects.setGain(0.4);
        musicGroup.setGain(0.8);
        shot.setGain(0.5);
        music.setGain(0.25);

        final int effect = audio.play(shot);
        final int song = audio.play(music);

        assertEquals(0.5 * 0.4 * 0.5, backend.getGain(effect), EPSILON);
        assertEquals(0.5 * 0.8 * 0.25, backend.getGain(song), EPSILON);
        audio.setMasterGain(1.0);
        assertEquals(0.4 * 0.5, backend.getGain(effect), EPSILON);
        assertEquals(0.8 * 0.25, backend.getGain(song), EPSILON);
        shot.setGain(1.0);
        music.setGain(1.0);
        assertEquals(0.4, backend.getGain(effect), EPSILON);
        assertEquals(0.8, backend.getGain(song), EPSILON);
        effects.setGain(0.0);
        assertEquals(0.0, backend.getGain(effect));
        assertEquals(0.8, backend.getGain(song), EPSILON);
    }

    /**
     * A group change reaches at once every playing voice of the group, short and long, and no voice of another group.
     */
    @Test
    void testGroupChangeReachesPlayingVoices()
    {
        final MixerGroup ambient = audio.addGroup("ambient");
        final ShortSound wind = cache.load(SHOT);
        wind.setGroup("ambient");
        wind.setLooping(true);
        final LongSound rain = loader.stream(MUSIC);
        rain.setGroup("ambient");
        final ShortSound shot = cache.load(SHOT);
        final LongSound music = loader.stream(MUSIC);
        final int first = audio.play(wind);
        final int second = audio.play(wind);
        final int long1 = audio.play(rain);
        final int other = audio.play(shot);
        final int song = audio.play(music);

        ambient.setGain(0.3);

        assertEquals(0.3, backend.getGain(first), EPSILON);
        assertEquals(0.3, backend.getGain(second), EPSILON);
        assertEquals(0.3, backend.getGain(long1), EPSILON);
        assertEquals(1.0, backend.getGain(other));
        assertEquals(1.0, backend.getGain(song));
        audio.stop(second);
        ambient.setGain(0.6);
        assertEquals(0.6, backend.getGain(first), EPSILON);
        assertEquals(0.6, backend.getGain(audio.play(wind)), EPSILON);
    }

    /**
     * The group of a sound is read at its play: a voice keeps the group it started with, and the next play takes the
     * new group.
     */
    @Test
    void testGroupReadAtPlay()
    {
        final MixerGroup ui = audio.getGroup(MixerGroup.UI);
        final MixerGroup effects = audio.getGroup(MixerGroup.EFFECTS);
        ui.setGain(0.5);
        effects.setGain(0.25);
        final ShortSound click = cache.load(SHOT);
        final int asEffect = audio.play(click);

        click.setGroup(MixerGroup.UI);
        final int asUi = audio.play(click);

        assertEquals(MixerGroup.UI, click.getGroup());
        assertEquals(0.25, backend.getGain(asEffect), EPSILON);
        assertEquals(0.5, backend.getGain(asUi), EPSILON);
        effects.setGain(1.0);
        assertEquals(1.0, backend.getGain(asEffect));
        assertEquals(0.5, backend.getGain(asUi), EPSILON);
        click.setGain(0.5);
        assertEquals(0.5, backend.getGain(asEffect), EPSILON);
        assertEquals(0.25, backend.getGain(asUi), EPSILON);
    }

    /**
     * A sound of a group unknown to the audio system is refused at its play, before any voice is taken or stolen; a
     * composed set with such a layer starts no layer.
     */
    @Test
    void testUnknownGroupRefusedAtPlay()
    {
        final Audio small = new Audio(backend, 1, VoicePolicy.STEAL);
        final ShortSound shot = cache.load(SHOT);
        final ShortSound stray = cache.load(SHOT);
        stray.setGroup("missing");
        final LongSound music = loader.stream(MUSIC);
        music.setGroup("missing");
        final int playing = small.play(shot);

        assertThrows(IllegalStateException.class, () -> small.play(stray));
        assertThrows(IllegalStateException.class, () -> small.play(music));
        final VariantSet layers = VariantSet.composed(shot, stray);
        assertThrows(IllegalStateException.class, () -> small.play(layers, Audio.DEFAULT_PRIORITY, new int[2]));

        assertTrue(small.isPlaying(playing));
        assertEquals(1, small.getPlayingCount());
        assertEquals(0, small.getLongPlayingCount());
        assertFalse(music.isPlaying());
        small.addGroup("missing");
        assertTrue(small.play(stray) > 0);
        assertFalse(small.isPlaying(playing));
    }
}
