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
package com.b3dgs.lionengine.core;

import java.util.concurrent.Semaphore;

import com.b3dgs.lionengine.Align;

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
    private final WavPlayer player;
    /** Sound file reference. */
    private Media media;
    /** Sound alignment. */
    private Align alignment;
    /** Sound volume. */
    private int volume;
    /** Sound delay. */
    private int delay;
    /** Routine flags. */
    private boolean isRunning;
    /** Playing flag. */
    private boolean isPlaying;
    /** Restart flag. */
    private boolean restart;
    /** Close flag. */
    private boolean close;

    /**
     * Constructor.
     * 
     * @param player The wav player reference.
     * @param title The sound title.
     */
    WavRoutine(WavPlayer player, String title)
    {
        super("SFX " + title);
        this.player = player;
        latch = new Semaphore(0);
        isRunning = true;
        media = null;
        isPlaying = false;
        restart = false;
    }

    /**
     * Set sound alignment.
     * 
     * @param alignment sound alignment.
     */
    void setAlignement(Align alignment)
    {
        this.alignment = alignment;
    }

    /**
     * Set sound file name.
     * 
     * @param media The audio sound media to play.
     */
    void setMedia(Media media)
    {
        this.media = media;
    }

    /**
     * Set the sound delay.
     * 
     * @param delay The delay.
     */
    void setDelay(int delay)
    {
        this.delay = delay;
    }

    /**
     * Set sound volume.
     * 
     * @param vol sound volume.
     */
    void setVolume(int vol)
    {
        volume = vol;
    }

    /**
     * Re allow to play one again.
     */
    void restart()
    {
        restart = true;
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
            close = false;
            player.addBusy(this);

            isPlaying = false;
            if (!restart)
            {
                media = null;
            }
            try
            {
                player.addFree(this);
                latch.acquire();
            }
            catch (final InterruptedException exception)
            {
                Thread.currentThread().interrupt();
                media = null;
                isRunning = false;
                player.decreaseCount();
            }
        }
    }
}
