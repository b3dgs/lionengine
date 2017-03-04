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
package com.b3dgs.lionengine.audio.sc68;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.audio.Audio;

/**
 * Allows to play SonicArranger musics (original Amiga player).
 */
public interface Sc68 extends Audio
{
    /**
     * Configure the audio output.
     * 
     * @param interpolation <code>true</code> to use interpolation, <code>false</code> else.
     * @param joinStereo <code>true</code> to join stereo, <code>false</code> else.
     */
    void setConfig(boolean interpolation, boolean joinStereo);

    /**
     * Play the audio.
     * <p>
     * The audio will be played from the beginning (can be set by {@link #setStart(long)}) until the end.
     * </p>
     * <p>
     * In case of a loop, audio will be played in loop between the set ticks using {@link #setLoop(long, long)}.
     * </p>
     * 
     * @param alignment The sound alignment.
     * @param loop <code>true</code> to play in loop, <code>false</code> else.
     * @throws LionEngineException If unable to play sound.
     */
    void play(Align alignment, boolean loop);

    /**
     * Set starting tick (starting audio position).
     * 
     * @param tick The starting tick <code>[0 - {@link #getTicks()}]</code>.
     * @throws LionEngineException If argument is invalid.
     */
    void setStart(long tick);

    /**
     * Set loop area in tick.
     * 
     * @param first The first tick <code>[0 - last}]</code>.
     * @param last The last tick <code>[first - {@link #getTicks()}}]</code>.
     * @throws LionEngineException If arguments are invalid.
     */
    void setLoop(long first, long last);

    /**
     * Get the total number of ticks.
     * 
     * @return The total number of ticks.
     */
    long getTicks();

    /**
     * Pause the audio (can be resumed).
     */
    void pause();

    /**
     * Resume the audio (if paused).
     */
    void resume();
}
