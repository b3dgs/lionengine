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
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.AudioBackend;
import com.b3dgs.lionengine.audio.FakeAudioBackend;
import com.b3dgs.lionengine.audio.LongSoundLoader;
import com.b3dgs.lionengine.audio.PcmFormat;
import com.b3dgs.lionengine.audio.ShortSoundCache;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;

/**
 * Runs {@link OggVorbisExample} on OGG Vorbis files of the test resources and the fake audio backend, headless
 * (REQ-DOC-001, REQ-AUDIO-002, ADR-0073).
 */
final class OggVorbisExampleTest
{
    /** Frames of the music: 5 s at 22,050 Hz. */
    private static final int MUSIC_FRAMES = 110_250;
    /** Frames of the coin: 0.5 s at 11,025 Hz. */
    private static final int COIN_FRAMES = 5_513;

    /**
     * Copies a test file to the game data.
     *
     * @param memory The game data.
     * @param key The key.
     * @param name The name of the test file.
     * @throws IOException If copying fails.
     */
    private static void copy(MemoryProvider memory, ResourceKey key, String name) throws IOException
    {
        try (InputStream input = OggVorbisExampleTest.class.getResourceAsStream(name);
             OutputStream output = memory.openWrite(key))
        {
            input.transferTo(output);
        }
    }

    /**
     * The music streams in a loop through its bounded buffer and still plays after three passes; the coin plays as a
     * short sound, decoded once; on exit the music stops and both are released.
     *
     * @throws IOException If the sounds cannot be copied.
     */
    @Test
    void testRun() throws IOException
    {
        final MemoryProvider memory = new MemoryProvider();
        copy(memory, OggVorbisExample.MUSIC, "level.ogg");
        copy(memory, OggVorbisExample.COIN, "coin.ogg");
        final Resources resources = new Resources(memory);
        final ShortSoundCache cache = new ShortSoundCache(resources);
        final LongSoundLoader loader = new LongSoundLoader(resources);
        final InboxSpy inbox = new InboxSpy();
        final FakeAudioBackend backend = new FakeAudioBackend(inbox);
        final Audio audio = new Audio(backend);

        OggVorbisExample.register(cache, loader);
        final OggVorbisExample.Level level = OggVorbisExample.enter(cache, loader, audio);
        assertTrue(level.music().isStreamed());
        assertTrue(level.music().isLooping());
        assertEquals(new PcmFormat(22_050, PcmFormat.MONO), level.music().getFormat());
        assertEquals(0.5, level.coin().getDuration(), 1E-3);

        backend.advance(3 * MUSIC_FRAMES);
        inbox.deliverAll();
        assertTrue(audio.isPlaying(level.musicVoice()));
        assertEquals(3L * MUSIC_FRAMES, backend.getPulledFrames(level.musicVoice()));

        final int coin = OggVorbisExample.collect(audio, level);
        assertTrue(coin > AudioBackend.NO_VOICE);
        assertEquals(COIN_FRAMES, backend.getPlayed().get(0).getFrames());
        backend.advance(COIN_FRAMES + 1);
        inbox.deliverAll();
        assertFalse(audio.isPlaying(coin));

        OggVorbisExample.exit(audio, level);
        assertTrue(level.music().isReleased());
        assertTrue(level.coin().isReleased());
        assertEquals(0, backend.getPlayingCount());
    }
}
