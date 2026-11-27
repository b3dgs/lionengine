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
package com.b3dgs.lionengine.examples.audio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.FakeAudioBackend;
import com.b3dgs.lionengine.audio.LongSoundLoader;
import com.b3dgs.lionengine.audio.MixerGroup;
import com.b3dgs.lionengine.audio.ShortSoundCache;
import com.b3dgs.lionengine.audio.WavWriter;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;

/**
 * Runs {@link MixerGroupExample} on WAV files built in memory and the fake audio backend, headless (REQ-DOC-001,
 * REQ-AUDIO-012).
 */
final class MixerGroupExampleTest
{
    /** Sample rate of the sounds. */
    private static final int RATE = 8_000;
    /** Frames of the click: 0.1 s. */
    private static final int CLICK_FRAMES = 800;
    /** Frames of the music: 2 s. */
    private static final int THEME_FRAMES = 16_000;
    /** Frames of the fade-out. */
    private static final int FADE_FRAMES = (int) (MixerGroupExample.FADE_OUT_SECONDS * RATE);
    /** Tolerance of the gain comparisons. */
    private static final double EPSILON = 1e-9;

    /**
     * Writes a WAV file of a beep in memory.
     *
     * @param memory The memory provider.
     * @param key The key.
     * @param frames The frames.
     * @throws IOException If writing fails.
     */
    private static void write(MemoryProvider memory, ResourceKey key, int frames) throws IOException
    {
        try (OutputStream output = memory.openWrite(key))
        {
            output.write(WavWriter.write(BeepExample.beep(440.0, (double) frames / RATE, RATE), 16));
        }
    }

    /**
     * The slider sets the interface group, reaching a click playing and not the music; on exit the music fades out
     * linearly and, at the delivery of the end of its fade, is stopped and the sounds released.
     *
     * @throws IOException If the sounds cannot be written.
     */
    @Test
    void testRun() throws IOException
    {
        final MemoryProvider memory = new MemoryProvider();
        write(memory, MixerGroupExample.CLICK, CLICK_FRAMES);
        write(memory, MixerGroupExample.THEME, THEME_FRAMES);
        final InboxSpy inbox = new InboxSpy();
        final FakeAudioBackend backend = new FakeAudioBackend(inbox);
        final Audio audio = new Audio(backend);
        final Resources resources = new Resources(memory);

        final MixerGroupExample.Town town = MixerGroupExample.enter(new ShortSoundCache(resources),
                                                                    new LongSoundLoader(resources),
                                                                    audio);
        final int theme = town.themeVoice();
        assertEquals(MixerGroup.MUSIC, town.theme().getGroup());
        assertEquals(MixerGroup.UI, town.click().getGroup());
        final int click = MixerGroupExample.playClick(audio, town);
        MixerGroupExample.setInterfaceVolume(audio, 0.4);
        assertEquals(0.4, backend.getGain(click), EPSILON);
        assertEquals(1.0, backend.getGain(theme));
        backend.advance(CLICK_FRAMES);
        inbox.deliverAll();

        MixerGroupExample.exit(audio, town);
        backend.advance(FADE_FRAMES / 2);
        inbox.deliverAll();
        assertEquals(0.5, backend.getGain(theme), EPSILON);
        assertTrue(audio.isPlaying(theme));
        backend.advance(FADE_FRAMES / 2);
        assertTrue(audio.isPlaying(theme));
        inbox.deliverAll();

        assertFalse(audio.isPlaying(theme));
        assertTrue(town.theme().isReleased());
        assertTrue(town.click().isReleased());
        assertEquals(0, backend.getPlayingCount());
    }
}
