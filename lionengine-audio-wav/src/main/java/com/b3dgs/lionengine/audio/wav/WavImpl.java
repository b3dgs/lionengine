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

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Timing;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.Verbose;

/**
 * Wav audio implementation.
 */
final class WavImpl implements Wav
{
    /** Sound buffer size. */
    private static final int BUFFER = 128000;
    /** Play sound error. */
    private static final String ERROR_PLAY_SOUND = "Error on playing sound: ";

    /**
     * Play a sound.
     * 
     * @param media The audio media.
     * @param alignment The alignment type.
     * @param volume The audio volume value.
     * @return The created and opened playback ready to be played.
     * @throws IOException If playback error.
     */
    private static Playback createPlayback(Media media, Align alignment, int volume) throws IOException
    {
        final AudioInputStream audioInputStream = openStream(media);
        final SourceDataLine sourceDataLine = getDataLine(audioInputStream);
        updateAlignment(sourceDataLine, alignment);
        updateVolume(sourceDataLine, volume);

        return new Playback(audioInputStream, sourceDataLine);
    }

    /**
     * Open the audio stream and prepare it.
     * 
     * @param media The audio media.
     * @return The audio source data.
     * @throws IOException If error when reading the audio file.
     * @throws LionEngineException If error when getting the stream.
     */
    private static AudioInputStream openStream(Media media) throws IOException
    {
        try
        {
            return AudioSystem.getAudioInputStream(new BufferedInputStream(media.getInputStream()));
        }
        catch (final UnsupportedAudioFileException exception)
        {
            throw new IOException(ERROR_PLAY_SOUND + media.getPath(), exception);
        }
    }

    /**
     * Get the audio data line.
     * 
     * @param audioInputStream The audio input.
     * @return The audio source data.
     * @throws IOException The no audio line available (may be already opened).
     */
    private static SourceDataLine getDataLine(AudioInputStream audioInputStream) throws IOException
    {
        final AudioFormat audioFormat = audioInputStream.getFormat();
        final DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat);
        try
        {
            final SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
            sourceDataLine.open(audioFormat);

            return sourceDataLine;
        }
        catch (final LineUnavailableException exception)
        {
            throw new IOException(exception);
        }
    }

    /**
     * Update the sound alignment.
     * 
     * @param sourceDataLine Audio source data.
     * @param alignment Alignment value.
     */
    private static void updateAlignment(SourceDataLine sourceDataLine, Align alignment)
    {
        if (sourceDataLine.isControlSupported(Type.PAN))
        {
            final FloatControl pan = (FloatControl) sourceDataLine.getControl(Type.PAN);
            switch (alignment)
            {
                case CENTER:
                    pan.setValue(0.0F);
                    break;
                case RIGHT:
                    pan.setValue(1.0F);
                    break;
                case LEFT:
                    pan.setValue(-1.0F);
                    break;
                default:
                    throw new LionEngineException(alignment);
            }
        }
    }

    /**
     * Update the sound volume.
     * 
     * @param sourceDataLine Audio source data.
     * @param volume The audio playback volume value.
     */
    private static void updateVolume(SourceDataLine sourceDataLine, int volume)
    {
        if (sourceDataLine.isControlSupported(Type.MASTER_GAIN))
        {
            final FloatControl gainControl = (FloatControl) sourceDataLine.getControl(Type.MASTER_GAIN);
            final double gain = UtilMath.clamp(volume / 100.0, 0.0, 100.0);
            final double dB = Math.log(gain) / Math.log(10.0) * 20.0;
            gainControl.setValue((float) dB);
        }
    }

    /**
     * Read the full sound and play it by buffer.
     * 
     * @param audioInputStream The audio input.
     * @param sourceDataLine Audio source data.
     * @throws IOException If error when reading the sound.
     */
    private static void readSound(AudioInputStream audioInputStream, SourceDataLine sourceDataLine) throws IOException
    {
        int read;
        final byte[] buffer = new byte[BUFFER];
        while ((read = audioInputStream.read(buffer, 0, buffer.length)) > 0)
        {
            sourceDataLine.write(buffer, 0, read);
        }
    }

    /**
     * Flush and close audio data and stream.
     * 
     * @param audioInputStream The audio input.
     * @param sourceDataLine Audio source data.
     * @throws IOException If error on closing.
     */
    private static void close(AudioInputStream audioInputStream, SourceDataLine sourceDataLine) throws IOException
    {
        sourceDataLine.drain();
        sourceDataLine.flush();
        sourceDataLine.stop();
        sourceDataLine.close();
        audioInputStream.close();
    }

    /** Opened playback. */
    private final Collection<Playback> opened = new ConcurrentLinkedQueue<Playback>();
    /** Tasks executor. */
    private final ExecutorService executor;
    /** Sound file reference. */
    private final Media media;

    /**
     * Internal constructor.
     * 
     * @param executor Tasks executor.
     * @param media The audio sound media.
     * @throws LionEngineException If media is <code>null</code>
     */
    WavImpl(ExecutorService executor, Media media)
    {
        Check.notNull(media);

        this.executor = executor;
        this.media = media;
    }

    /**
     * Play sound.
     * 
     * @param media The sound media.
     * @param alignment The sound alignment.
     * @param volume The volume in percent.
     * @param delayMilli The delay before sound is played in milliseconds.
     */
    private void play(Media media, Align alignment, int volume, int delayMilli)
    {
        try
        {
            final Timing timing = new Timing();
            timing.start();
            final Playback playback = createPlayback(media, alignment, volume);
            opened.add(playback);
            final long toWait = delayMilli - timing.elapsed();
            if (toWait > 0)
            {
                Thread.sleep(toWait);
            }

            final AudioInputStream audioInputStream = playback.getAudioInputStream();
            final SourceDataLine sourceDataLine = playback.getSourceDataLine();
            sourceDataLine.start();
            readSound(audioInputStream, sourceDataLine);
            close(audioInputStream, sourceDataLine);
        }
        catch (final IOException exception)
        {
            Verbose.exception(exception);
        }
        catch (final InterruptedException exception)
        {
            Thread.currentThread().interrupt();
            Verbose.exception(exception);
        }
    }

    /*
     * Wav
     */

    @Override
    public void play()
    {
        play(Align.CENTER, VOLUME_MAX, 0);
    }

    @Override
    public void play(int delayMilli)
    {
        play(Align.CENTER, VOLUME_MAX, delayMilli);
    }

    @Override
    public void play(Align alignment, int volume)
    {
        play(alignment, volume, 0);
    }

    @Override
    public void play(final Align alignment, final int volume, final int delayMilli)
    {
        Check.superiorOrEqual(volume, 0);
        Check.inferiorOrEqual(volume, 100);

        executor.execute(new Runnable()
        {
            @Override
            public void run()
            {
                play(media, alignment, volume, delayMilli);
            }
        });
    }

    @Override
    public void stop()
    {
        for (final Playback playback : opened)
        {
            try
            {
                playback.close();
            }
            catch (final IOException exception)
            {
                throw new LionEngineException(exception, media);
            }
        }
        opened.clear();
    }
}
