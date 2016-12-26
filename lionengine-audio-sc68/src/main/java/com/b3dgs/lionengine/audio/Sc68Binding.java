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

import com.sun.jna.Library;

/**
 * Sc68 binding interface.
 */
interface Sc68Binding extends Library
{
    /**
     * Play sc68.
     * 
     * @param name The track name.
     */
    void Sc68Play(String name);

    /**
     * Set volume.
     * 
     * @param volume The value.
     */
    void Sc68SetVolume(int volume);

    /**
     * Configure the audio output.
     * 
     * @param interpolation <code>1</code> to use interpolation, <code>0</code> else.
     * @param stereoJoin <code>1</code> to join stereo, <code>0</code> else.
     */
    void Sc68Config(int interpolation, int stereoJoin);

    /**
     * Pause track.
     */
    void Sc68Pause();

    /**
     * Resume track.
     */
    void Sc68Resume();

    /**
     * Stop track.
     */
    void Sc68Stop();

    /**
     * Get play index.
     * 
     * @return The play index.
     */
    int Sc68Seek();
}
