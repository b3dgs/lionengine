/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.LionEngineException;

/**
 * Describe a audio interface, dedicated to long audio stream, playable in loop with a specified volume.
 */
public interface Audio
{
    /** Minimum volume value. */
    int VOLUME_MIN = 0;
    /** Maximum volume value. */
    int VOLUME_MAX = 100;

    /**
     * Play the audio.
     * <p>
     * The audio will be played from the beginning until the end.
     * </p>
     * 
     * @throws LionEngineException If unable to play sound.
     */
    void play();

    /**
     * Set the midi volume.
     * 
     * @param volume The volume in percent <code>[{@link #VOLUME_MIN} - {@link #VOLUME_MAX}]</code>.
     * @throws LionEngineException If argument is invalid or midi not available.
     */
    void setVolume(int volume);

    /**
     * Stop the audio.
     */
    void stop();
}
