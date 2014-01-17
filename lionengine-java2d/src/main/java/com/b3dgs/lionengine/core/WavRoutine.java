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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.concurrent.Semaphore;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.LionEngineException;

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
    /** Audio output. */
    private SourceDataLine sourceDataLine;
    /** Audio stream. */
    private AudioInputStream audioInputStream;

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
     * Stop sound, close stream and flush output.
     */
    synchronized void stopSound()
    {
        if (sourceDataLine != null)
        {
            try
            {
                sourceDataLine.flush();
                sourceDataLine.stop();
                audioInputStream.close();
            }
            catch (final IOException exception)
            {
                Verbose.exception(WavRoutine.class, "stopSound", exception);
            }
            finally
            {
                restart = false;
                close = true;
            }
        }
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
     * Read the stream.
     * 
     * @param tempBuffer The buffer.
     * @return The read byte.
     * @throws IOException If error.
     */
    synchronized int readStream(byte[] tempBuffer) throws IOException
    {
        if (!close)
        {
            return audioInputStream.read(tempBuffer, 0, tempBuffer.length);
        }
        return -1;
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
            if (media != null)
            {
                final String filename = media.getPath();
                try
                {
                    isPlaying = true;
                    restart = false;

                    // Open stream
                    audioInputStream = AudioSystem.getAudioInputStream(new BufferedInputStream(media.getStream()));
                    final AudioFormat audioFormat = audioInputStream.getFormat();
                    final DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);

                    // Prepare output
                    sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
                    sourceDataLine.open(audioFormat);

                    // Set alignment
                    if (sourceDataLine.isControlSupported(FloatControl.Type.PAN))
                    {
                        final FloatControl pan = (FloatControl) sourceDataLine.getControl(FloatControl.Type.PAN);
                        switch (alignment)
                        {
                            case CENTER:
                                pan.setValue(0.0f);
                                break;
                            case RIGHT:
                                pan.setValue(1.0f);
                                break;
                            case LEFT:
                                pan.setValue(-1.0f);
                                break;
                            default:
                                throw new LionEngineException("Unsupported alignment !");
                        }
                    }

                    // Set volume
                    if (sourceDataLine.isControlSupported(FloatControl.Type.MASTER_GAIN))
                    {
                        final FloatControl gainControl = (FloatControl) sourceDataLine
                                .getControl(FloatControl.Type.MASTER_GAIN);
                        final double gain = UtilityMath.fixBetween(volume / 100.0, 0.0, 100.0);
                        final double dB = Math.log(gain) / Math.log(10.0) * 20.0;
                        gainControl.setValue((float) dB);
                    }

                    if (delay > 0)
                    {
                        Thread.sleep(delay);
                    }

                    // Start playing by filling buffer till the end
                    sourceDataLine.start();
                    int cnt;
                    final byte[] tempBuffer = new byte[WavRoutine.BUFFER];

                    while ((cnt = readStream(tempBuffer)) != -1)
                    {
                        if (cnt > 0)
                        {
                            sourceDataLine.write(tempBuffer, 0, cnt);
                        }
                    }

                    // Flush and close stream
                    sourceDataLine.drain();
                    sourceDataLine.flush();
                    sourceDataLine.stop();
                    sourceDataLine.close();
                    audioInputStream.close();
                }
                catch (final InterruptedException exception)
                {
                    Thread.currentThread().interrupt();
                }
                catch (final UnsupportedAudioFileException
                             | IllegalArgumentException exception)
                {
                    Verbose.critical(WavRoutine.class, "run", "Unsupported audio format: \"", filename, "\"");
                }
                catch (final LineUnavailableException exception)
                {
                    Verbose.critical(WavRoutine.class, "run", "Unavailable audio line: \"", filename, "\"");
                }
                catch (final IOException exception)
                {
                    Verbose.exception(WavRoutine.class, "run", exception);
                }
            }
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
