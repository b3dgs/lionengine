/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio;

/**
 * Void audio implementation.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class AudioVoid implements Audio
{
    /** Single audio instance. */
    public static final AudioVoid INSTANCE = new AudioVoid();

    /**
     * Create void audio.
     */
    private AudioVoid()
    {
        super();
    }

    /*
     * Audio
     */

    @Override
    public void play()
    {
        // Nothing to do
    }

    @Override
    public void setVolume(int volume)
    {
        // Nothing to do
    }

    @Override
    public void await()
    {
        // Nothing to do
    }

    @Override
    public void stop()
    {
        // Nothing to do
    }

    @Override
    public long getTicks()
    {
        return 0L;
    }
}
