/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio;

import com.b3dgs.lionengine.core.Media;

/**
 * Wav factory. Allows to create wav player.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class AudioWav
{
    /**
     * Load a sound file <code>(.wav)</code>.
     * 
     * @param media The audio sound media.
     * @return The loaded sound.
     */
    public static Wav loadWav(Media media)
    {
        return new Wav(media);
    }

    /**
     * Load a sound file <code>(.wav)</code>.
     * 
     * @param media The audio sound media.
     * @param maxSimultaneous The maximum number of simultaneous sounds that can be played at the same time.
     * @return The loaded Sound.
     */
    public static Wav loadWav(Media media, int maxSimultaneous)
    {
        return new Wav(media, maxSimultaneous);
    }

    /**
     * Private constructor.
     */
    private AudioWav()
    {
        throw new RuntimeException();
    }
}
