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
package com.b3dgs.lionengine.audio;

import com.b3dgs.lionengine.Align;

/**
 * Handle sound fx routine. The sound is expected to be short, as it has to be played quickly. It supports the following
 * main controls:
 * <ul>
 * <li>Alignment</li>
 * <li>Volume</li>
 * <li>Channel</li>
 * </ul>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Wav sound = AudioWav.loadWav(Media.get(&quot;sound.wav&quot;));
 * sound.setVolume(100);
 * 
 * sound.setAlignment(Align.LEFT);
 * sound.play();
 * Thread.sleep(200);
 * 
 * sound.setAlignment(Align.CENTER);
 * sound.play();
 * Thread.sleep(200);
 * 
 * sound.setAlignment(Align.RIGHT);
 * sound.play();
 * Thread.sleep(200);
 * 
 * sound.stop();
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Wav
{
    /** Minimum volume value. */
    int VOLUME_MIN = 0;
    /** Maximum volume value. */
    int VOLUME_MAX = 100;

    /**
     * Set sound alignment.
     * 
     * @param align sound alignment.
     */
    void setAlignment(Align align);

    /**
     * Set the sound volume.
     * 
     * @param volume The volume in percent <code>[{@link #VOLUME_MIN} - {@link #VOLUME_MAX}]</code>.
     */
    void setVolume(int volume);

    /**
     * Play sound immediately until the end, and free resources. Sounds are played in a separated thread. If all
     * channels are used, the sound will not be played.
     */
    void play();

    /**
     * Play sound immediately until the end, and free resources. Sounds are played in a separated thread. If all
     * channels are used, the sound will not be played.
     * 
     * @param delay The delay in millisecond before being played.
     */
    void play(int delay);

    /**
     * Stop sound. The sound will be stopped, but not deleted.
     */
    void stop();

    /**
     * Close sound. Release resources.
     */
    void terminate();
}
