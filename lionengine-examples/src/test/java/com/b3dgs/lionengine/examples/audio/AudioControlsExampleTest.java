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
import com.b3dgs.lionengine.audio.ShortSoundCache;
import com.b3dgs.lionengine.audio.WavWriter;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;

/**
 * Runs {@link AudioControlsExample} on WAV files built in memory and the fake audio backend, headless (REQ-DOC-001,
 * REQ-AUDIO-011).
 */
final class AudioControlsExampleTest
{
    /** Sample rate of the sounds. */
    private static final int RATE = 8_000;
    /** Frames of the footstep: 0.2 s. */
    private static final int STEP_FRAMES = 1_600;
    /** Frames of the wind: 3 s. */
    private static final int WIND_FRAMES = 24_000;
    /** Tolerance of the gain comparisons. */
    private static final double EPSILON = 1e-12;

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
            output.write(WavWriter.write(BeepExample.beep(330.0, (double) frames / RATE, RATE), 16));
        }
    }

    /**
     * The voices get master × sound; the wind loops past its end without an end event; the background holds every
     * voice and the foreground resumes them where they were; on exit nothing plays.
     *
     * @throws IOException If the sounds cannot be written.
     */
    @Test
    void testRun() throws IOException
    {
        final MemoryProvider memory = new MemoryProvider();
        write(memory, AudioControlsExample.STEP, STEP_FRAMES);
        write(memory, AudioControlsExample.WIND, WIND_FRAMES);
        final InboxSpy inbox = new InboxSpy();
        final FakeAudioBackend backend = new FakeAudioBackend(inbox);
        final Audio audio = new Audio(backend);
        final Resources resources = new Resources(memory);

        final AudioControlsExample.Forest forest = AudioControlsExample.enter(new ShortSoundCache(resources),
                                                                              new LongSoundLoader(resources),
                                                                              audio);
        final int wind = forest.windVoice();
        assertEquals(0.8 * 0.25, backend.getGain(wind), EPSILON);
        backend.advance(3 * WIND_FRAMES);
        inbox.deliverAll();
        assertTrue(audio.isPlaying(wind));
        assertTrue(inbox.getPosted().isEmpty());

        final int step = AudioControlsExample.step(audio, forest);
        assertEquals(0.8 * 0.5, backend.getGain(step), EPSILON);
        backend.advance(STEP_FRAMES / 2);
        AudioControlsExample.background(audio);
        final long pulled = backend.getPulledFrames(wind);
        backend.advance(10 * STEP_FRAMES);
        inbox.deliverAll();
        assertEquals(pulled, backend.getPulledFrames(wind));
        assertTrue(audio.isPlaying(step));

        AudioControlsExample.foreground(audio);
        backend.advance(STEP_FRAMES / 2);
        inbox.deliverAll();
        assertFalse(audio.isPlaying(step));
        assertEquals(pulled + STEP_FRAMES / 2, backend.getPulledFrames(wind));

        AudioControlsExample.exit(audio, forest);
        assertTrue(forest.wind().isReleased());
        assertTrue(forest.step().isReleased());
        assertEquals(0, backend.getPlayingCount());
    }
}
