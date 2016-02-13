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
package com.b3dgs.lionengine.audio.wav;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Handle sound fx routine. The sound is expected to be short, as it has to be played quickly. It supports the following
 * main controls:
 * <ul>
 * <li>Alignment - output location</li>
 * <li>Volume - in percent <code>[{@link #VOLUME_MIN} - {@link #VOLUME_MAX}]</code></li>
 * <li>Channel - number of sound played simultaneously</li>
 * </ul>
 */
public interface Wav
{
    /** Minimum volume value. */
    int VOLUME_MIN = 0;
    /** Maximum volume value. */
    int VOLUME_MAX = 100;

    /**
     * Play sound immediately until the end, and free resources. Sounds are played in a separated thread. If all
     * channels are used, the sound will not be played.
     * 
     * @throws LionEngineException If unable to play sound.
     */
    void play();

    /**
     * Play sound immediately until the end, and free resources. Sounds are played in a separated thread. If all
     * channels are used, the sound will not be played.
     * 
     * @param delayMilli The delay before sound is played in milliseconds.
     * @throws LionEngineException If unable to play sound.
     */
    void play(int delayMilli);

    /**
     * Play sound immediately until the end, and free resources. Sounds are played in a separated thread. If all
     * channels are used, the sound will not be played.
     * 
     * @param alignment The sound alignment.
     * @param volume The volume in percent <code>[{@link #VOLUME_MIN} - {@link #VOLUME_MAX}]</code>.
     * @throws LionEngineException If unable to play sound.
     */
    void play(Align alignment, int volume);

    /**
     * Play sound immediately until the end, and free resources. Sounds are played in a separated thread. If all
     * channels are used, the sound will not be played.
     * 
     * @param alignment The sound alignment.
     * @param volume The volume in percent <code>[{@link #VOLUME_MIN} - {@link #VOLUME_MAX}]</code>.
     * @param delayMilli The delay before sound is played in milliseconds.
     * @throws LionEngineException If unable to play sound.
     */
    void play(Align alignment, int volume, int delayMilli);

    /**
     * Stop sound.
     * 
     * @throws LionEngineException If unable to stop sound.
     */
    void stop();
}
