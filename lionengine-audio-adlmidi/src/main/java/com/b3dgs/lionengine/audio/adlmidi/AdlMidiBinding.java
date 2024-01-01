/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio.adlmidi;

import com.sun.jna.Library;

/**
 * AdlMidi binding interface.
 */
interface AdlMidiBinding extends Library
{
    /**
     * Play track.
     * 
     * @param name The track name.
     */
    void adlPlay(String name);

    /**
     * Set volume.
     * 
     * @param volume The value.
     */
    void adlSetVolume(int volume);

    /**
     * Configure the audio output.
     * 
     * @param bank The bank id.
     */
    void adlSetBank(int bank);

    /**
     * Pause track.
     */
    void adlPause();

    /**
     * Resume track.
     */
    void adlResume();

    /**
     * Stop track.
     */
    void adlStop();

    /**
     * Get play index.
     * 
     * @return The play index.
     */
    int adlSeek();
}
