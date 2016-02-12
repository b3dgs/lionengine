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
    private final AudioInputStream audioInputStream;

    /** Audio data. */
    private final SourceDataLine sourceDataLine;

    /**
     * Create playback.
     * 
     * @param audioInputStream The audio input.
     * @param sourceDataLine The audio data.
     */
    Playback(AudioInputStream audioInputStream, SourceDataLine sourceDataLine)
    {
        this.audioInputStream = audioInputStream;
        this.sourceDataLine = sourceDataLine;
    }

    /**
     * Get the audio stream.
     * 
     * @return The audio stream.
     */
    public AudioInputStream getAudioInputStream()
    {
        return audioInputStream;
    }

    /**
     * Get the audio data.
     * 
     * @return The audio data.
     */
    public SourceDataLine getSourceDataLine()
    {
        return sourceDataLine;
    }

    /*
     * Closeable
     */

    @Override
    public void close() throws IOException
    {
        sourceDataLine.flush();
        sourceDataLine.stop();
        audioInputStream.close();
    }
}
