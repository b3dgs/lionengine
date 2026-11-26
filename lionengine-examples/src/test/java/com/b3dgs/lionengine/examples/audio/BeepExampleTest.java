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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.audio.FakeAudioBackend;
import com.b3dgs.lionengine.audio.PcmBuffer;

/**
 * Runs {@link BeepExample} on the fake audio backend, headless (REQ-DOC-001, REQ-AUDIO-009).
 */
final class BeepExampleTest
{
    /**
     * The beep has the requested length, starts at zero and stays within its amplitude; played on the fake backend,
     * its end reaches the listener at the inbox delivery once its frames have played.
     */
    @Test
    void testBeep()
    {
        final PcmBuffer beep = BeepExample.beep(440.0, 0.1, 8_000);
        final short[] samples = new short[beep.getFrames()];
        beep.readFrames(0, samples, 0, samples.length);
        final InboxSpy inbox = new InboxSpy();
        final FakeAudioBackend backend = new FakeAudioBackend(inbox);
        final List<Integer> ended = new ArrayList<>();

        final int voice = BeepExample.play(backend, beep, ended::add);
        backend.advance(beep.getFrames() - 1);
        inbox.deliverAll();
        assertTrue(ended.isEmpty());
        backend.advance(1);
        inbox.deliverAll();

        assertEquals(800, beep.getFrames());
        assertEquals(0.1, beep.getDuration(), 1e-12);
        assertEquals(0, samples[0]);
        for (final short sample : samples)
        {
            assertTrue(Math.abs(sample) <= Short.MAX_VALUE / 4 + 1);
        }
        assertEquals(List.of(Integer.valueOf(voice)), ended);
    }
}
