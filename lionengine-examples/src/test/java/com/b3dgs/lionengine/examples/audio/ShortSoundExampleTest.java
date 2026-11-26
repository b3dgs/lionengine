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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioBackend;
import com.b3dgs.lionengine.audio.AudioDecodingException;
import com.b3dgs.lionengine.audio.FakeAudioBackend;
import com.b3dgs.lionengine.audio.ShortSoundCache;
import com.b3dgs.lionengine.audio.WavWriter;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;

/**
 * Runs {@link ShortSoundExample} on WAV files built in memory and the fake audio backend, headless (REQ-DOC-001,
 * REQ-AUDIO-003, REQ-AUDIO-010).
 */
final class ShortSoundExampleTest
{
    /**
     * Writes a WAV file of a beep in memory.
     *
     * @param memory The memory provider.
     * @param key The key.
     * @param seconds The duration in seconds.
     * @throws IOException If writing fails.
     */
    private static void write(MemoryProvider memory, ResourceKey key, double seconds) throws IOException
    {
        try (OutputStream output = memory.openWrite(key))
        {
            output.write(WavWriter.write(BeepExample.beep(440.0, seconds, 8_000), 16));
        }
    }

    /**
     * The explosion steals the oldest footstep; the distant footstep is not played; the sounds are cached while in use
     * and leave the cache once released; the voices end through the inbox.
     *
     * @throws IOException If the sounds cannot be written.
     */
    @Test
    void testRun() throws IOException
    {
        final MemoryProvider memory = new MemoryProvider();
        write(memory, ShortSoundExample.STEP, 0.2);
        write(memory, ShortSoundExample.EXPLOSION, 1.5);
        final InboxSpy inbox = new InboxSpy();
        final FakeAudioBackend backend = new FakeAudioBackend(inbox);
        final Audio audio = ShortSoundExample.createAudio(backend);

        final ShortSoundExample.Result result = ShortSoundExample.run(new ShortSoundCache(new Resources(memory)),
                                                                      audio);

        assertTrue(result.blast() > 0);
        assertFalse(result.firstStepPlaying());
        assertTrue(audio.isPlaying(result.secondStep()));
        assertEquals(AudioBackend.NO_VOICE, result.distantStep());
        assertEquals(2, result.cachedInUse());
        assertEquals(0, result.cachedAtEnd());
        backend.advance(12_000);
        inbox.deliverAll();
        assertEquals(0, audio.getPlayingCount());
    }

    /**
     * A file longer than 10 s is refused by the short-sound cache.
     *
     * @throws IOException If the sound cannot be written.
     */
    @Test
    void testTooLong() throws IOException
    {
        final MemoryProvider memory = new MemoryProvider();
        write(memory, ShortSoundExample.STEP, 10.5);
        final ShortSoundCache cache = new ShortSoundCache(new Resources(memory));

        final AudioDecodingException exception = assertThrows(AudioDecodingException.class,
                                                              () -> cache.load(ShortSoundExample.STEP));

        assertTrue(exception.getMessage().contains("long sound"));
    }
}
