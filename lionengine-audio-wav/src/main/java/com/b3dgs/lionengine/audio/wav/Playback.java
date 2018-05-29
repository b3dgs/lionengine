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
package com.b3dgs.lionengine.audio.wav;

import java.io.Closeable;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.SourceDataLine;

/**
 * Playback representation of an active sound.
 */
final class Playback implements Closeable
{
    /** Audio input. */
    private final AudioInputStream input;
    /** Audio data. */
    private final SourceDataLine dataLine;

    /**
     * Create playback.
     * 
     * @param input The audio input.
     * @param dataLine The audio data.
     */
    Playback(AudioInputStream input, SourceDataLine dataLine)
    {
        super();

        this.input = input;
        this.dataLine = dataLine;
    }

    /**
     * Get the audio data.
     * 
     * @return The audio data.
     */
    public SourceDataLine getDataLine()
    {
        return dataLine;
    }

    /*
     * Closeable
     */

    @Override
    public void close() throws IOException
    {
        dataLine.flush();
        dataLine.close();
        input.close();
    }
}
