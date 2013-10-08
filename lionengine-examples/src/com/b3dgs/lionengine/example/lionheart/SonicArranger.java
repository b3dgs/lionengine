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
package com.b3dgs.lionengine.example.lionheart;

import com.b3dgs.lionengine.audio.AudioSc68;
import com.b3dgs.lionengine.audio.Sc68;
import com.b3dgs.lionengine.core.Media;

/**
 * Sc68 module binding.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class SonicArranger
{
    /** Player instance. */
    private static Sc68 sc68;
    /** Music enabled. */
    private static boolean enabled;

    /**
     * Set the enabled state.
     * 
     * @param enabled <code>true</code> if enabled, <code>false</code> else.
     */
    public static void setEnabled(boolean enabled)
    {
        SonicArranger.enabled = enabled;
        if (enabled)
        {
            SonicArranger.sc68 = AudioSc68.createSc68Player();
            SonicArranger.sc68.setVolume(40);
        }
    }

    /**
     * Stop player.
     */
    public static void stop()
    {
        if (SonicArranger.enabled)
        {
            SonicArranger.sc68.stop();
        }
    }

    /**
     * Pause player.
     */
    public static void pause()
    {
        if (SonicArranger.enabled)
        {
            SonicArranger.sc68.pause();
        }
    }

    /**
     * Resume player.
     */
    public static void resume()
    {
        if (SonicArranger.enabled)
        {
            SonicArranger.sc68.resume();
        }
    }

    /**
     * Play a music.
     * 
     * @param music The music to play.
     */
    public static void play(Music music)
    {
        if (SonicArranger.enabled)
        {
            SonicArranger.sc68.play(Media.get(AppLionheart.MUSICS_DIR, music.getFilename()));
        }
    }

    /**
     * Terminate player.
     */
    public static void terminate()
    {
        if (SonicArranger.enabled)
        {
            SonicArranger.sc68.free();
        }
    }

    /**
     * Get playing index.
     * 
     * @return The playing index.
     */
    public static int seek()
    {
        if (SonicArranger.enabled)
        {
            return SonicArranger.sc68.seek();
        }
        return -1;
    }

    /**
     * Private constructor.
     */
    private SonicArranger()
    {
        throw new RuntimeException();
    }
}
