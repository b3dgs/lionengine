package com.b3dgs.lionengine.audio;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Verbose;

/**
 * Default sound player implementation.
 */
final class WavPlayer
        implements Wav
{
    /** Maximum number of sounds played at the same time. */
    final int maxSimultaneous;
    /** Sound ready threads. */
    final BlockingQueue<WavRoutine> freeSounds;
    /** Sound busy threads. */
    final BlockingQueue<WavRoutine> busySounds;
    /** Sound monitor. */
    final Object monitor;
    /** Created thread counter. */
    volatile Integer count;
    /** Sound file reference. */
    private final Media media;
    /** Sound alignment. */
    private Align alignment;
    /** Volume value. */
    private int volume;

    /**
     * Create a sound player.
     * 
     * @param media The audio sound media.
     */
    WavPlayer(Media media)
    {
        this(media, 1);
    }

    /**
     * Create a sound player.
     * 
     * @param media The audio sound media.
     * @param maxSimultaneous The maximum number of simultaneous sounds that can be played at the same time.
     */
    WavPlayer(Media media, int maxSimultaneous)
    {
        Check.notNull(media, "Sound file must exists !");
        this.media = media;
        this.maxSimultaneous = maxSimultaneous;
        count = Integer.valueOf(0);
        monitor = new Object();
        freeSounds = new LinkedBlockingQueue<>(maxSimultaneous);
        busySounds = new LinkedBlockingQueue<>(maxSimultaneous);
        alignment = Align.CENTER;
        volume = 100;
    }

    /**
     * Decrease the routine counter value.
     */
    void decreaseCount()
    {
        synchronized (count)
        {
            count = Integer.valueOf(count.intValue() - 1);
        }
    }

    /**
     * Add a sound routine to the free list and remove it from the busy list.
     * 
     * @param routine Sound routine.
     * @throws InterruptedException If errors.
     */
    void addFree(WavRoutine routine) throws InterruptedException
    {
        busySounds.remove(routine);
        freeSounds.put(routine);
    }

    /**
     * Add a sound routine to the busy list.
     * 
     * @param routine Sound routine.
     */
    void addBusy(WavRoutine routine)
    {
        try
        {
            busySounds.put(routine);
        }
        catch (final InterruptedException exception)
        {
            Verbose.exception(WavPlayer.class, "play", exception);
        }
    }

    /*
     * Wav
     */

    @Override
    public void play()
    {
        play(0);
    }

    @Override
    public void play(int delay)
    {
        final WavRoutine routine;
        if (!freeSounds.isEmpty() || count.intValue() >= maxSimultaneous)
        {
            routine = freeSounds.poll();
            if (routine != null)
            {
                routine.setAlignement(alignment);
                routine.setMedia(media);
                routine.setVolume(volume);
                routine.setDelay(delay);
                synchronized (monitor)
                {
                    monitor.notify();
                }
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
            synchronized (count)
            {
                count = Integer.valueOf(count.intValue() + 1);
            }
        }
    }

    @Override
    public void setAlignment(Align align)
    {
        alignment = align;
    }

    @Override
    public void setVolume(int vol)
    {
        Check.argument(vol >= Wav.VOLUME_MIN && vol <= Wav.VOLUME_MAX, "Wrong volume value: ", String.valueOf(vol),
                " [" + Wav.VOLUME_MIN + "-" + Wav.VOLUME_MAX + "]");
        volume = vol;
    }

    @Override
    public void stop()
    {
        for (WavRoutine routine : busySounds)
        {
            routine.stopSound();
        }
    }

    @Override
    public void terminate()
    {
        new Thread("WavPlayer cleanup")
        {
            @Override
            public void run()
            {
                while (count.intValue() > 0)
                {
                    WavPlayer.this.stop();
                    for (WavRoutine routine : busySounds)
                    {
                        routine.interrupt();
                    }
                    for (WavRoutine routine : freeSounds)
                    {
                        routine.interrupt();
                    }
                    try
                    {
                        Thread.sleep(100);
                    }
                    catch (InterruptedException exception)
                    {
                        Verbose.exception(WavPlayer.class, "terminate", exception);
                    }
                }
            }
        }.start();
    }
}
