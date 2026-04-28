/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio.wav;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.Control.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * Null source data line fallback.
 */
public final class NullSourceDataLine implements SourceDataLine
{
    /**
     * Create line.
     */
    public NullSourceDataLine()
    {
        super();
    }

    @Override
    public void drain()
    {
        // Mock
    }

    @Override
    public void flush()
    {
        // Mock
    }

    @Override
    public void start()
    {
        // Mock
    }

    @Override
    public void stop()
    {
        // Mock
    }

    @Override
    public boolean isRunning()
    {
        return false;
    }

    @Override
    public boolean isActive()
    {
        return false;
    }

    @Override
    public AudioFormat getFormat()
    {
        return null;
    }

    @Override
    public int getBufferSize()
    {
        return 0;
    }

    @Override
    public int available()
    {
        return 0;
    }

    @Override
    public int getFramePosition()
    {
        return 0;
    }

    @Override
    public long getLongFramePosition()
    {
        return 0;
    }

    @Override
    public long getMicrosecondPosition()
    {
        return 0;
    }

    @Override
    public float getLevel()
    {
        return 0;
    }

    @Override
    public javax.sound.sampled.Line.Info getLineInfo()
    {
        return null;
    }

    @Override
    public void open() throws LineUnavailableException
    {
        // Mock
    }

    @Override
    public void close()
    {
        // Mock
    }

    @Override
    public boolean isOpen()
    {
        return true;
    }

    @Override
    public Control[] getControls()
    {
        return null;
    }

    @Override
    public boolean isControlSupported(Type control)
    {
        return false;
    }

    @Override
    public Control getControl(Type control)
    {
        return null;
    }

    @Override
    public void addLineListener(LineListener listener)
    {
        // Mock
    }

    @Override
    public void removeLineListener(LineListener listener)
    {
        // Mock
    }

    @Override
    public void open(AudioFormat format, int bufferSize) throws LineUnavailableException
    {
        // Mock
    }

    @Override
    public void open(AudioFormat format) throws LineUnavailableException
    {
        // Mock
    }

    @Override
    public int write(byte[] b, int off, int len)
    {
        return len;
    }
}
