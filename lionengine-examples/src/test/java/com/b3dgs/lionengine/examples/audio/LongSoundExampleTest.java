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
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.FakeAudioBackend;
import com.b3dgs.lionengine.audio.LongSoundLoader;
import com.b3dgs.lionengine.audio.Playlist;
import com.b3dgs.lionengine.audio.WavWriter;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.Resources;
import com.b3dgs.lionengine.resources.memory.MemoryProvider;

/**
 * Runs {@link LongSoundExample} on WAV files built in memory and the fake audio backend, headless (REQ-DOC-001,
 * REQ-AUDIO-006, REQ-AUDIO-007, REQ-AUDIO-008).
 */
final class LongSoundExampleTest
{
    /** Sample rate of the sounds. */
    private static final int RATE = 8_000;
    /** Duration of the theme in seconds: longer than a short sound may be. */
    private static final double THEME_SECONDS = 12.0;
    /** Duration of a level track in seconds. */
    private static final double LEVEL_SECONDS = 2.0;

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
            output.write(WavWriter.write(BeepExample.beep(220.0, seconds, RATE), 16));
        }
    }

    /** The game data. */
    private MemoryProvider memory;
    /** The inbox of the loop. */
    private InboxSpy inbox;
    /** The backend. */
    private FakeAudioBackend backend;
    /** The audio system. */
    private Audio audio;
    /** The loader. */
    private LongSoundLoader loader;

    /**
     * Writes the music and creates the audio system.
     *
     * @throws IOException If the sounds cannot be written.
     */
    @BeforeEach
    void prepare() throws IOException
    {
        memory = new MemoryProvider();
        write(memory, LongSoundExample.THEME, THEME_SECONDS);
        inbox = new InboxSpy();
        backend = new FakeAudioBackend(inbox);
        audio = new Audio(backend);
        loader = new LongSoundLoader(new Resources(memory));
    }

    /**
     * Plays seconds of audio, then delivers the inbox as the loop does at its next iteration.
     *
     * @param seconds The duration in seconds.
     */
    private void play(double seconds)
    {
        backend.advance((int) (seconds * RATE) + 1);
        inbox.deliverAll();
    }

    /**
     * The theme, longer than 10 s, streams through a buffer of 16,384 frames and is stopped by its voice; the two
     * level tracks play one after the other, then the playlist stops; on exit everything is released.
     *
     * @throws IOException If the sounds cannot be written.
     */
    @Test
    void testRun() throws IOException
    {
        for (final ResourceKey level : LongSoundExample.LEVELS)
        {
            write(memory, level, LEVEL_SECONDS);
        }
        final List<String> failures = new ArrayList<>();

        final LongSoundExample.Menu menu = LongSoundExample.playTheme(loader, audio);
        assertTrue(menu.theme().isPlaying());
        assertTrue(menu.theme().isStreamed());
        assertEquals(16_384, menu.theme().getChunks() * menu.theme().getChunkFrames());
        play(THEME_SECONDS / 2);
        assertTrue(audio.isPlaying(menu.voice()));
        audio.stop(menu.voice());
        assertFalse(menu.theme().isPlaying());

        final Playlist levels = LongSoundExample.playLevels(loader, audio, failures);
        assertEquals(0, levels.getTrack());
        assertTrue(levels.getSound(1).isPrepared());
        play(LEVEL_SECONDS);
        assertEquals(1, levels.getTrack());
        play(LEVEL_SECONDS);
        assertFalse(levels.isPlaying());
        assertTrue(failures.isEmpty());

        LongSoundExample.exit(menu, levels);
        assertTrue(menu.theme().isReleased());
        assertTrue(levels.getSound(0).isReleased());
        assertEquals(0, audio.getLongPlayingCount());
        assertEquals(0, backend.getPlayingCount());
    }

    /**
     * A missing level track is reported with its index and skipped: the next track plays.
     *
     * @throws IOException If the sounds cannot be written.
     */
    @Test
    void testMissingTrack() throws IOException
    {
        write(memory, LongSoundExample.LEVELS.get(1), LEVEL_SECONDS);
        final List<String> failures = new ArrayList<>();

        final Playlist levels = LongSoundExample.playLevels(loader, audio, failures);

        assertEquals(1, failures.size());
        assertTrue(failures.get(0).startsWith("0: "), failures::toString);
        assertEquals(1, levels.getTrack());
        LongSoundExample.exit(LongSoundExample.playTheme(loader, audio), levels);
        assertEquals(0, backend.getPlayingCount());
    }
}
