/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.tutorials.mario.d;

import com.b3dgs.lionengine.audio.wav.AudioWav;
import com.b3dgs.lionengine.audio.wav.Wav;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Medias;

/**
 * Handle the SFX.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
enum Sfx
{
    /** Jump. */
    JUMP("jump"),
    /** Die. */
    CRUSH("crush");

    /** Audio file extension. */
    private static final String AUDIO_FILE_EXTENSION = ".wav";

    /**
     * Terminate all sounds.
     */
    public static void terminateAll()
    {
        for (final Sfx sfx : Sfx.values())
        {
            sfx.terminate();
        }
    }

    /** Sounds list composing the effect. */
    private final Wav sound;

    /**
     * Constructor.
     * 
     * @param sound The sound.
     */
    private Sfx(String sound)
    {
        this(sound, 1);
    }

    /**
     * Constructor.
     * 
     * @param sound The sound.
     * @param count The total number of sounds.
     */
    private Sfx(String sound, int count)
    {
        final Media media = Medias.create("sfx", sound + Sfx.AUDIO_FILE_EXTENSION);
        this.sound = AudioWav.loadWav(media, count);
        this.sound.setVolume(20);
    }

    /**
     * Play the sound effect.
     */
    public void play()
    {
        sound.play();
    }

    /**
     * Terminate all channels.
     */
    private void terminate()
    {
        sound.terminate();
    }
}
