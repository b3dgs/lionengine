/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;

/**
 * Describe audio format.
 */
public interface AudioFormat
{
    /**
     * Load an audio file and prepare it to be played.
     * 
     * @param media The audio media (must not be <code>null</code>).
     * @return The loaded audio.
     * @throws LionEngineException If media is <code>null</code> or invalid audio or no audio player is available.
     */
    Audio loadAudio(Media media);

    /**
     * Get the music audio formats as read only.
     * 
     * @return The audio music formats.
     */
    Collection<String> getFormats();

    /**
     * Close the handler.
     */
    void close();
}
