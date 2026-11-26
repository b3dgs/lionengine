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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.FakeAudioBackend;
import com.b3dgs.lionengine.audio.ShortSoundCache;
import com.b3dgs.lionengine.audio.VoicePolicy;
import com.b3dgs.lionengine.audio.WavWriter;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;

/**
 * Runs {@link VariantSetExample} on WAV files built in memory and the fake audio backend, headless (REQ-DOC-001,
 * REQ-AUDIO-005).
 */
final class VariantSetExampleTest
{
    /** Voices of the audio system: every sound of the example at once. */
    private static final int VOICES = VariantSetExample.FOOTSTEPS + 2;

    /** The game data. */
    private MemoryProvider memory;

    /**
     * Writes a WAV file of a beep in memory.
     *
     * @param key The key.
     * @param frequency The frequency in Hz.
     * @throws IOException If writing fails.
     */
    private void write(ResourceKey key, double frequency) throws IOException
    {
        try (OutputStream output = memory.openWrite(key))
        {
            output.write(WavWriter.write(BeepExample.beep(frequency, 0.1, 8_000), 16));
        }
    }

    /**
     * Writes the sounds of the example.
     *
     * @throws IOException If writing fails.
     */
    @BeforeEach
    void prepare() throws IOException
    {
        memory = new MemoryProvider();
        for (int i = 0; i < VariantSetExample.STEPS.length; i++)
        {
            write(VariantSetExample.STEPS[i], 200.0 + i * 100.0);
        }
        write(VariantSetExample.BLAST, 60.0);
        write(VariantSetExample.DEBRIS, 900.0);
    }

    /**
     * Runs the example on a new backend.
     *
     * @param backend The backend.
     * @return The result.
     */
    private VariantSetExample.Result run(FakeAudioBackend backend)
    {
        final Audio audio = new Audio(backend, VOICES, VoicePolicy.STEAL);
        return VariantSetExample.run(new ShortSoundCache(new Resources(memory)), audio);
    }

    /**
     * The footsteps never repeat at once, both layers of the explosion start in the same play, and every sound is
     * played.
     */
    @Test
    void testRun()
    {
        final InboxSpy inbox = new InboxSpy();
        final FakeAudioBackend backend = new FakeAudioBackend(inbox);

        final VariantSetExample.Result result = run(backend);

        final List<Integer> footsteps = result.footsteps();
        assertEquals(VariantSetExample.FOOTSTEPS, footsteps.size());
        for (int i = 1; i < footsteps.size(); i++)
        {
            assertNotEquals(footsteps.get(i - 1), footsteps.get(i));
        }
        assertEquals(2, result.started());
        assertTrue(result.blast() > 0);
        assertEquals(result.blast() + 1, result.debris());
        assertEquals(VOICES, backend.getPlayed().size());
        backend.advance(800);
        inbox.deliverAll();
        assertEquals(0, backend.getPlayingCount());
    }

    /**
     * The same seed gives the same footsteps on another run.
     */
    @Test
    void testDeterministic()
    {
        final List<Integer> first = run(new FakeAudioBackend(new InboxSpy())).footsteps();
        final List<Integer> second = run(new FakeAudioBackend(new InboxSpy())).footsteps();

        assertEquals(first, second);
    }
}
