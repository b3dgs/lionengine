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
package com.b3dgs.lionengine.core;

import com.b3dgs.lionengine.audio.Midi;
import com.b3dgs.lionengine.audio.Wav;

/**
 * Represents the audio factory.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
interface FactoryAudio
{
    /**
     * Create midi.
     * 
     * @param media The music to play.
     * @return The created midi.
     */
    Midi createAudioMidi(Media media);

    /**
     * Create wav.
     * 
     * @param media The sound to play.
     * @return The created wav.
     */
    Wav createAudioWav(Media media);

    /**
     * Create wav.
     * 
     * @param media The sound to play.
     * @param maxSimultaneous The maximum simultaneous sounds.
     * @return The created wav.
     */
    Wav createAudioWav(Media media, int maxSimultaneous);
}
