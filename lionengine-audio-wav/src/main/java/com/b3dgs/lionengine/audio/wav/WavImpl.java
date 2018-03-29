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

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.core.PlayerAbstract;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Wav audio implementation.
 */
final class WavImpl implements Wav
{
    /** Sound buffer size. */
    private static final int BUFFER = 128_000;
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
        final AudioInputStream input = openStream(media);
        final SourceDataLine dataLine = getDataLine(input);
        dataLine.start();
        updateAlignment(dataLine, alignment);
        updateVolume(dataLine, volume);

        return new Playback(input, dataLine);
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
            return AudioSystem.getAudioInputStream(media.getInputStream());
        }
        catch (final UnsupportedAudioFileException exception)
        {
            throw new IOException(ERROR_PLAY_SOUND + media.getPath(), exception);
        }
    }

    /**
     * Get the audio data line.
     * 
     * @param input The audio input.
     * @return The audio source data.
     * @throws IOException If no audio line available (may be already opened).
     */
    @SuppressWarnings("resource")
    private static SourceDataLine getDataLine(AudioInputStream input) throws IOException
    {
        final AudioFormat format = input.getFormat();
        try
        {
            final SourceDataLine dataLine;
            if (WavFormat.mixer != null)
            {
                dataLine = AudioSystem.getSourceDataLine(format, WavFormat.mixer);
            }
            else
            {
                dataLine = AudioSystem.getSourceDataLine(format);
            }
            dataLine.open(format);

            return dataLine;
        }
        catch (final LineUnavailableException exception)
        {
            throw new IOException(exception);
        }
    }

    /**
     * Update the sound alignment.
     * 
     * @param dataLine Audio source data.
     * @param alignment Alignment value.
     */
    private static void updateAlignment(DataLine dataLine, Align alignment)
    {
        if (dataLine.isControlSupported(Type.PAN))
        {
            final FloatControl pan = (FloatControl) dataLine.getControl(Type.PAN);
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
     * @param dataLine Audio source data.
     * @param volume The audio playback volume value.
     */
    private static void updateVolume(DataLine dataLine, int volume)
    {
        if (dataLine.isControlSupported(Type.MASTER_GAIN))
        {
            final FloatControl gainControl = (FloatControl) dataLine.getControl(Type.MASTER_GAIN);
            final double gain = UtilMath.clamp(volume / 100.0, 0.0, 100.0);
            final double dB = Math.log(gain) / Math.log(10.0) * 20.0;
            gainControl.setValue((float) dB);
        }
    }

    /**
     * Read the full sound and play it by buffer.
     * 
     * @param input The audio input.
     * @param dataLine Audio source data.
     * @throws IOException If error when reading the sound.
     */
    private static void readSound(AudioInputStream input, SourceDataLine dataLine) throws IOException
    {
        int read;
        final byte[] buffer = new byte[BUFFER];
        while ((read = input.read(buffer, 0, buffer.length)) > 0)
        {
            dataLine.write(buffer, 0, read);
        }
    }

    /**
     * Flush and close audio data and stream.
     * 
     * @param input The audio input.
     * @param dataLine Audio source data.
     * @throws IOException If error on closing.
     */
    private static void close(AudioInputStream input, DataLine dataLine) throws IOException
    {
        dataLine.drain();
        dataLine.flush();
        dataLine.stop();
        dataLine.close();
        input.close();
    }

    /** Opened playback. */
    private final Map<Media, Playback> opened = new ConcurrentHashMap<>();
    /** Tasks executor. */
    private final ExecutorService executor;
    /** Sound file reference. */
    private final Media media;
    /** Volume used. */
    private int volume = PlayerAbstract.VOLUME_MAX;
    /** Exception flag. */
    private IOException last;

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
     */
    private void play(Media media, Align alignment)
    {
        try (Playback playback = createPlayback(media, alignment, volume))
        {
            if (opened.containsKey(media))
            {
                opened.get(media).close();
            }
            opened.put(media, playback);

            final AudioInputStream input = openStream(media);
            final SourceDataLine dataLine = playback.getDataLine();
            dataLine.start();
            readSound(input, dataLine);
            close(input, dataLine);
        }
        catch (final IOException exception)
        {
            if (last == null || !exception.getMessage().equals(last.getMessage()))
            {
                Verbose.exception(exception, media.toString());
                last = exception;
            }
        }
    }

    /*
     * Wav
     */

    @Override
    public void play()
    {
        play(Align.CENTER);
    }

    @Override
    public void play(final Align alignment)
    {
        executor.execute(() -> play(media, alignment));
    }

    @SuppressWarnings("resource")
    @Override
    public void stop()
    {
        for (final Playback playback : opened.values())
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

    @Override
    public void setVolume(int volume)
    {
        Check.superiorOrEqual(volume, 0);
        Check.inferiorOrEqual(volume, 100);

        this.volume = volume;
    }
}
