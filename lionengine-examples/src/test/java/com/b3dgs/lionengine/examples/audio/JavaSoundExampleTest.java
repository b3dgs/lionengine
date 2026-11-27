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

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.audio.AudioBackend;
import com.b3dgs.lionengine.audio.FakeAudioBackend;
import com.b3dgs.lionengine.audio.PcmBuffer;

/**
 * Runs the headless part of {@link JavaSoundExample} on the fake audio backend (REQ-DOC-001, REQ-AUDIO-002): the
 * desktop part, {@link JavaSoundExample#main()}, is run on a sound device by {@code JavaSoundExampleDeviceTest}.
 */
final class JavaSoundExampleTest
{
    /** Frames of a note. */
    private static final int NOTE_FRAMES = (int) Math.round(JavaSoundExample.NOTE_SECONDS * JavaSoundExample.NOTE_RATE);

    /**
     * The notes play one after the other, each started at the delivery of the end of the previous one, mono at
     * 22,050 Hz, each softer than the previous; the jingle is done after the last one.
     */
    @Test
    void testJingle()
    {
        final InboxSpy inbox = new InboxSpy();
        final FakeAudioBackend backend = new FakeAudioBackend(inbox);

        final Jingle jingle = JavaSoundExample.start(backend);

        double gain = Double.MAX_VALUE;
        for (int i = 0; i < JavaSoundExample.NOTES; i++)
        {
            assertFalse(jingle.isDone());
            assertEquals(i + 1, jingle.getPlayed());
            assertTrue(backend.getGain(jingle.getVoice()) < gain);
            gain = backend.getGain(jingle.getVoice());
            backend.advance(NOTE_FRAMES / 2);
            inbox.deliverAll();
            assertEquals(i + 1, backend.getPlayed().size());
            backend.advance(NOTE_FRAMES);
            inbox.deliverAll();
        }

        assertTrue(jingle.isDone());
        assertEquals(AudioBackend.NO_VOICE, jingle.getVoice());
        assertEquals(JavaSoundExample.NOTES, backend.getPlayed().size());
        final PcmBuffer note = backend.getPlayed().get(0);
        assertEquals(JavaSoundExample.NOTE_RATE, note.getFormat().sampleRate());
        assertEquals(1, note.getFormat().channels());
        assertEquals(NOTE_FRAMES, note.getFrames());
        assertEquals(0, backend.getPlayingCount());
    }
}
