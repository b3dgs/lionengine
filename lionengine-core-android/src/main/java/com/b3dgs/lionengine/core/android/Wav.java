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
package com.b3dgs.lionengine.core.android;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.core.Media;

/**
 * Handle sound fx routine. The sound is expected to be short, as it has to be played quickly. It supports the following
 * main controls:
 * <ul>
 * <li>Alignment</li>
 * <li>Volume</li>
 * <li>Channel</li>
 * </ul>
 * <p>
 * Example:
 * </p>
 * 
 * <pre>
 * final Wav sound = AudioWav.loadWav(Media.get(&quot;sound.wav&quot;));
 * sound.setVolume(100);
 * 
 * sound.setAlignment(Align.LEFT);
 * sound.play();
 * Thread.sleep(200);
 * 
 * sound.setAlignment(Align.CENTER);
 * sound.play();
 * Thread.sleep(200);
 * 
 * sound.setAlignment(Align.RIGHT);
 * sound.play();
 * Thread.sleep(200);
 * 
 * sound.stop();
 * </pre>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Wav
{
    /** Minimum volume value. */
    public static final int VOLUME_MIN = 0;
    /** Maximum volume value. */
    public static final int VOLUME_MAX = 100;

    /** Maximum number of sounds played at the same time. */
    final int maxSimultaneous;
    /** Sound ready threads. */
    final Queue<WavRoutine> freeSounds;
    /** Sound busy threads. */
    final Queue<WavRoutine> busySounds;
    /** Sound monitor. */
    final Semaphore latch;
    /** Terminated. */
    final Object monitor = new Object();
    /** Created thread counter. */
    volatile Integer count;
    /** Terminated. */
    volatile Boolean terminated;
    /** Count monitor. */
    private final Object monitorCount = new Object();
    /** Sound file reference. */
    private final Media media;
    /** Sound alignment. */
    private Align alignment;
    /** Volume value. */
    private int volume;

    /**
     * Constructor.
     * 
     * @param media The audio sound media.
     */
    Wav(Media media)
    {
        this(media, 1);
    }

    /**
     * Constructor.
     * 
     * @param media The audio sound media.
     * @param maxSimultaneous The maximum number of simultaneous sounds that can be played at the same time.
     */
    Wav(Media media, int maxSimultaneous)
    {
        Check.notNull(media);

        this.media = media;
        this.maxSimultaneous = maxSimultaneous;
        count = Integer.valueOf(0);
        latch = new Semaphore(0);
        freeSounds = new LinkedList<WavRoutine>();
        busySounds = new LinkedList<WavRoutine>();
        alignment = Align.CENTER;
        volume = 100;
        terminated = Boolean.FALSE;
    }

    /**
     * Decrease the routine counter value.
     */
    void decreaseCount()
    {
        synchronized (monitorCount)
        {
            count = Integer.valueOf(count.intValue() - 1);
        }
    }

    /**
     * Add a sound routine to the free list and remove it from the busy list.
     * 
     * @param routine Sound routine.
     */
    void addFree(WavRoutine routine)
    {
        busySounds.remove(routine);
        freeSounds.add(routine);
    }

    /**
     * Add a sound routine to the busy list.
     * 
     * @param routine Sound routine.
     */
    void addBusy(WavRoutine routine)
    {
        busySounds.add(routine);
    }

    /*
     * Wav
     */

    /**
     * Play sound immediately until the end, and free resources. Sounds are played in a separated thread. If all
     * channels are used, the sound will not be played.
     */
    public void play()
    {
        play(0);
    }

    /**
     * Play sound immediately until the end, and free resources. Sounds are played in a separated thread. If all
     * channels are used, the sound will not be played.
     * 
     * @param delay The delay in millisecond before being played.
     */
    public void play(int delay)
    {
        synchronized (monitor)
        {
            if (!terminated.booleanValue())
            {
                final WavRoutine routine;
                if (!freeSounds.isEmpty() || count.intValue() >= maxSimultaneous)
                {
                    if (freeSounds.isEmpty())
                    {
                        routine = busySounds.poll();
                    }
                    else
                    {
                        routine = freeSounds.poll();
                    }

                    if (routine != null)
                    {
                        routine.stopSound();
                        routine.setAlignement(alignment);
                        routine.setMedia(media);
                        routine.setVolume(volume);
                        routine.setDelay(delay);
                        routine.restart();
                        routine.latch.release();
                    }
                }
                else
                {
                    routine = new WavRoutine(this, media.getPath());
                    routine.setAlignement(alignment);
                    routine.setMedia(media);
                    routine.setVolume(volume);
                    routine.setDelay(delay);
                    routine.start();
                    synchronized (monitorCount)
                    {
                        count = Integer.valueOf(count.intValue() + 1);
                    }
                }
            }
        }
    }

    /**
     * Set sound alignment.
     * 
     * @param align sound alignment.
     */
    public void setAlignment(Align align)
    {
        alignment = align;
    }

    /**
     * Set the sound volume.
     * 
     * @param volume The volume in percent <code>[{@link #VOLUME_MIN} - {@link #VOLUME_MAX}]</code>.
     */
    public void setVolume(int volume)
    {
        Check.superiorOrEqual(volume, Wav.VOLUME_MIN);
        Check.inferiorOrEqual(volume, Wav.VOLUME_MAX);

        this.volume = volume;
    }

    /**
     * Stop sound. The sound will be stopped, but not deleted.
     */
    public void stop()
    {
        final List<WavRoutine> toStop = new ArrayList<WavRoutine>(busySounds);
        for (final WavRoutine routine : toStop)
        {
            if (routine != null)
            {
                routine.stopSound();
            }
        }
        toStop.clear();
    }

    /**
     * Close sound. Release resources.
     */
    public void terminate()
    {
        terminated = Boolean.TRUE;
        new Thread("WavPlayer cleanup")
        {
            @Override
            public void run()
            {
                while (!busySounds.isEmpty())
                {
                    final List<WavRoutine> toStop = new ArrayList<WavRoutine>(busySounds);
                    for (final WavRoutine routine : toStop)
                    {
                        if (routine != null)
                        {
                            routine.stopSound();
                            routine.interrupt();
                        }
                    }
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (final InterruptedException exception)
                    {
                        Thread.currentThread().interrupt();
                    }
                    toStop.clear();
                    busySounds.clear();
                }
                while (count.intValue() != 0)
                {
                    synchronized (monitor)
                    {
                        final List<WavRoutine> toStop = new ArrayList<WavRoutine>(freeSounds);
                        for (final WavRoutine routine : toStop)
                        {
                            if (routine != null)
                            {
                                routine.interrupt();
                            }
                        }

                        try
                        {
                            monitor.wait(100);
                        }
                        catch (final InterruptedException exception)
                        {
                            Thread.currentThread().interrupt();
                        }
                        toStop.clear();
                        freeSounds.clear();
                    }
                }
                terminated = Boolean.FALSE;
            }
        }.start();
    }
}
