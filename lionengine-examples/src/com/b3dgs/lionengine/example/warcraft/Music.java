/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.example.warcraft;

import com.b3dgs.lionengine.audio.AudioMidi;
import com.b3dgs.lionengine.audio.Midi;
import com.b3dgs.lionengine.core.Media;

/**
 * Handle the music.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum Music
{
    /** Blank. */
    HUMANS("humans"),
    /** Blizzard. */
    ORCS("orcs"),
    /** Click. */
    MENU("menu");

    /** Audio file extension. */
    private static final String AUDIO_FILE_EXTENSION = ".mid";

    /** Sound enabled. */
    private static boolean enabled;
    /** Volume. */
    private static int volume = 80;

    /**
     * Set the enabled state.
     * 
     * @param enabled <code>true</code> if enabled, <code>false</code> else.
     */
    public static void setEnabled(boolean enabled)
    {
        Music.enabled = enabled;
    }

    /**
     * Play the music.
     * 
     * @param music The music to play.
     */
    public static void play(Music music)
    {
        music.play();
        music.setVolume(Music.volume);
    }

    /**
     * Play the music.
     * 
     * @param music The music to play.
     */
    public static void stop(Music music)
    {
        music.stop();
    }

    /**
     * Set the global music volume.
     * 
     * @param volume The music volume.
     */
    public static void setGlobalVolume(int volume)
    {
        Music.volume = volume;
    }

    /** Music module. */
    private final Midi midi;

    /**
     * Constructor.
     * 
     * @param music The music.
     */
    private Music(String music)
    {
        final Media media = Media.get(AppWarcraft.MUSICS_DIR, music + Music.AUDIO_FILE_EXTENSION);
        midi = AudioMidi.loadMidi(media);
        if (music.equals("menu"))
        {
            midi.setLoop(6300, midi.getTicks() - 3680);
        }
    }

    /**
     * Play the music.
     */
    private void play()
    {
        if (Music.enabled)
        {
            midi.play(true);
        }
    }

    /**
     * Set the music volume.
     * 
     * @param volume The music volume.
     */
    private void setVolume(int volume)
    {
        midi.setVolume(volume);
    }

    /**
     * Stop music.
     */
    private void stop()
    {
        midi.stop();
    }
}
