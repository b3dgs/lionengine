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

import java.util.Collection;
import java.util.List;

import com.b3dgs.lionengine.audio.Audio;
import com.b3dgs.lionengine.audio.LongSound;
import com.b3dgs.lionengine.audio.LongSoundLoader;
import com.b3dgs.lionengine.audio.Playlist;
import com.b3dgs.lionengine.resources.ResourceKey;

/**
 * Streams the music of a menu, and plays the music of the levels as a two-track playlist (REQ-AUDIO-006,
 * REQ-AUDIO-007, REQ-AUDIO-008, ADR-0068).
 *
 * <p>
 * The game creates one {@link LongSoundLoader} with its resources. A long sound is streamed by default: it holds only
 * a buffer of 4 chunks of 4,096 frames, whatever its length, so music is not limited to the 10 s of the short sounds.
 * The menu prepares its theme on entry, which opens the file and decodes the buffer ahead, so that
 * {@link Audio#play(LongSound)} starts it at once; {@link Audio#stop(int)} stops it. A {@link Playlist} plays its
 * tracks one after the other: the next track is prepared while the current one plays, and starts when the end of the
 * current one is delivered through the inbox, on the runtime thread. After its last track the playlist stops, unless it
 * loops. A track that fails, for example a missing file, is reported to the listener with its index, then skipped. On
 * exit, the scene stops the playlist and releases every long sound it created.
 * </p>
 *
 * @since 11.0.0
 */
public final class LongSoundExample
{
    /** Key of the menu theme, in the game data. */
    public static final ResourceKey THEME = new ResourceKey("music/theme.wav");
    /** Keys of the level tracks, in the game data. */
    public static final List<ResourceKey> LEVELS = List.of(new ResourceKey("music/level-1.wav"),
                                                           new ResourceKey("music/level-2.wav"));

    /**
     * Private constructor.
     */
    private LongSoundExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Prepares the menu theme, streamed, and plays it at once.
     *
     * @param loader The long-sound loader over the game data holding {@link #THEME}.
     * @param audio The audio system.
     * @return The theme, playing; stop its voice with {@link Audio#stop(int)} and release it on exit.
     */
    public static Menu playTheme(LongSoundLoader loader, Audio audio)
    {
        final LongSound theme = loader.stream(THEME);
        theme.prepare();
        final int voice = audio.play(theme);
        return new Menu(theme, voice);
    }

    /**
     * Plays the two level tracks as a playlist that stops after the last one; a failing track is reported to a list,
     * then skipped.
     *
     * @param loader The long-sound loader over the game data holding {@link #LEVELS}.
     * @param audio The audio system.
     * @param failures The collection receiving one line per failing track: its index and its error.
     * @return The playlist, playing its first track.
     */
    public static Playlist playLevels(LongSoundLoader loader, Audio audio, Collection<String> failures)
    {
        final LongSound[] tracks = new LongSound[LEVELS.size()];
        for (int i = 0; i < tracks.length; i++)
        {
            tracks[i] = loader.stream(LEVELS.get(i));
        }
        final Playlist levels = new Playlist(audio, tracks);
        levels.setListener((playlist, track, exception) -> failures.add(track + ": " + exception.getMessage()));
        levels.play();
        return levels;
    }

    /**
     * Stops the music and releases every long sound, as a scene does on exit.
     *
     * @param menu The menu theme.
     * @param levels The playlist of the levels.
     */
    public static void exit(Menu menu, Playlist levels)
    {
        levels.stop();
        for (int i = 0; i < levels.size(); i++)
        {
            levels.getSound(i).release();
        }
        menu.theme().release();
    }

    /**
     * The menu theme and its voice.
     *
     * @param theme The theme.
     * @param voice The voice playing it.
     * @since 11.0.0
     */
    public record Menu(LongSound theme, int voice)
    {
        // Value type of the example.
    }
}
