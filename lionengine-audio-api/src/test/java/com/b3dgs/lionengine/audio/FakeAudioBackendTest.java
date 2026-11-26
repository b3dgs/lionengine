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

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;

/**
 * Binding of the {@link AudioBackendContract} on {@link FakeAudioBackend}, and the fake's own recording (ADR-0045,
 * REQ-AUDIO-009).
 */
final class FakeAudioBackendTest extends AudioBackendContract
{
    @Override
    protected AudioBackend createBackend(InboxSpy inbox)
    {
        return new FakeAudioBackend(inbox, 4);
    }

    @Override
    protected void advance(AudioBackend backend, int frames)
    {
        ((FakeAudioBackend) backend).advance(frames);
    }

    /**
     * The fake records what it played, counts the playing voices, ends voices in start order, and a sound of no frame
     * ends at the next advance.
     */
    @Test
    void testRecording()
    {
        final InboxSpy inbox = new InboxSpy();
        final FakeAudioBackend backend = new FakeAudioBackend(inbox);
        final List<Integer> ended = new ArrayList<>();
        backend.setVoiceListener(ended::add);
        final PcmBuffer longer = sound(2 * FRAMES);
        final PcmBuffer empty = sound(0);

        final int first = backend.play(longer);
        final int second = backend.play(empty);
        backend.advance(0);
        inbox.deliverAll();
        backend.advance(FRAMES);
        backend.advance(FRAMES);
        inbox.deliverAll();

        assertEquals(FakeAudioBackend.DEFAULT_MAX_VOICES, backend.getMaxVoices());
        assertEquals(List.of(longer, empty), backend.getPlayed());
        assertEquals(List.of(Integer.valueOf(second), Integer.valueOf(first)), ended);
        assertEquals(0, backend.getPlayingCount());
        assertFalse(backend.isClosed());
        backend.close();
        assertTrue(backend.isClosed());
        assertThrows(IllegalArgumentException.class, () -> backend.advance(-1));
        assertThrows(IllegalArgumentException.class, () -> new FakeAudioBackend(null));
        assertThrows(IllegalArgumentException.class, () -> new FakeAudioBackend(inbox, 0));
    }
}
