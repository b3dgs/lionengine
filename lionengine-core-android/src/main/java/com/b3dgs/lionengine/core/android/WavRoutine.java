/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.android;

import java.util.concurrent.Semaphore;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.core.Media;

/**
 * Sound routine implementation. One sound represents one thread.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class WavRoutine
        extends Thread
{
    /** Sound buffer size. */
    static final int BUFFER = 128000;

    /** Sound monitor. */
    final Semaphore latch;
    /** Way player reference. */
    private final Wav player;
    /** Routine flags. */
    private boolean isRunning;
    /** Playing flag. */
    private boolean isPlaying;

    /**
     * Internal constructor.
     * 
     * @param player The wav player reference.
     * @param title The sound title.
     */
    WavRoutine(Wav player, String title)
    {
        super("SFX " + title);
        this.player = player;
        latch = new Semaphore(0);
        isRunning = true;
        isPlaying = false;
    }

    /**
     * Set sound alignment.
     * 
     * @param alignment sound alignment.
     */
    void setAlignement(Align alignment)
    {
        // Nothing to do
    }

    /**
     * Set sound file name.
     * 
     * @param media The audio sound media to play.
     */
    void setMedia(Media media)
    {
        // Nothing to do
    }

    /**
     * Set the sound delay.
     * 
     * @param delay The delay.
     */
    void setDelay(int delay)
    {
        // Nothing to do
    }

    /**
     * Set sound volume.
     * 
     * @param vol sound volume.
     */
    void setVolume(int vol)
    {
        // Nothing to do
    }

    /**
     * Re allow to play one again.
     */
    void restart()
    {
        // Nothing to do
    }

    /**
     * Check if sound is playing.
     * 
     * @return true if playing, false else.
     */
    boolean isPlaying()
    {
        return isPlaying;
    }

    /**
     * Stop sound.
     */
    void stopSound()
    {
        isPlaying = false;
    }

    /*
     * Thread
     */

    @Override
    public void run()
    {
        while (isRunning)
        {
            player.addBusy(this);

            isPlaying = false;
            try
            {
                player.addFree(this);
                latch.acquire();
            }
            catch (final InterruptedException exception)
            {
                Thread.currentThread().interrupt();
                isRunning = false;
                player.decreaseCount();
            }
        }
    }
}
