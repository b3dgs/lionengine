/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.FloatControl.Type;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.audio.AudioFactory;
import com.b3dgs.lionengine.audio.PlayerAbstract;

/**
 * Wav audio implementation.
 */
final class WavImpl implements Wav
{
    /** Sound buffer size. */
    private static final int BUFFER = 4400;
    /** Minimum delay between same. */
    private static final long MIN_DELAY_NANO = 10_000_000L;
    /** Play sound error. */
    private static final String ERROR_PLAY_SOUND = "Error on playing sound: ";
    /** Logger. */
    private static final Logger LOGGER = LoggerFactory.getLogger(WavImpl.class);

    /**
     * Play a sound.
     * 
     * @param media The audio media.
     * @return The created and opened playback ready to be played.
     * @throws IOException If playback error.
     */
    private static Playback createPlayback(byte[] media) throws IOException
    {
        final AudioInputStream input = openStream(media);
        final SourceDataLine dataLine = getDataLine(input);

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
    private static AudioInputStream openStream(byte[] media) throws IOException
    {
        try (ByteArrayInputStream input = new ByteArrayInputStream(media))
        {
            return AudioSystem.getAudioInputStream(input);
        }
        catch (final UnsupportedAudioFileException exception)
        {
            throw new IOException(ERROR_PLAY_SOUND + media, exception);
        }
    }

    /**
     * Get the audio data line.
     * 
     * @param input The audio input.
     * @return The audio source data.
     * @throws IOException If no audio line available (may be already opened).
     */
    private static SourceDataLine getDataLine(AudioInputStream input) throws IOException
    {
        final AudioFormat format = input.getFormat();
        try
        {
            if (WavFormat.mixer != null)
            {
                return AudioSystem.getSourceDataLine(format, WavFormat.mixer);
            }
            return AudioSystem.getSourceDataLine(format);
        }
        catch (final LineUnavailableException | IllegalArgumentException exception)
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
     * Open audio line.
     * 
     * @param dataLine The data line.
     * @param input The input stream.
     * @throws LineUnavailableException If error.
     */
    private static void openLine(SourceDataLine dataLine, AudioInputStream input) throws LineUnavailableException
    {
        if (!dataLine.isOpen())
        {
            try
            {
                dataLine.open(input.getFormat(), BUFFER);
            }
            catch (final IllegalStateException | LineUnavailableException exception)
            {
                LOGGER.error("openLine error", exception);
            }
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
    /** Cache reference. */
    private final byte[] cache;
    /** Volume used. */
    private volatile int volume = PlayerAbstract.VOLUME_MAX;
    /** Exception flag. */
    private Exception last;
    /** Alive count. */
    private volatile int count;
    /** Last time. */
    private long time;

    /**
     * Internal constructor.
     * 
     * @param executor Tasks executor.
     * @param media The audio sound media.
     * @throws LionEngineException If media is <code>null</code>
     */
    WavImpl(ExecutorService executor, Media media)
    {
        super();

        Check.notNull(media);

        this.executor = executor;
        this.media = media;

        try (ByteArrayOutputStream output = new ByteArrayOutputStream();
             InputStream input = media.getInputStream())
        {
            final byte[] buf = new byte[Constant.MAX_PORT];
            int r;
            while ((r = input.read(buf)) != -1)
            {
                output.write(buf, 0, r);
            }
            cache = output.toByteArray();
        }
        catch (final IOException exception)
        {
            throw new LionEngineException(exception, ERROR_PLAY_SOUND);
        }
    }

    /**
     * Play sound.
     * 
     * @param media The sound media.
     * @param alignment The sound alignment.
     */
    private void play(Media media, Align alignment)
    {
        if (opened.containsKey(media))
        {
            try
            {
                opened.get(media).close();
            }
            catch (final IOException exception)
            {
                LOGGER.error(media.toString(), exception);
            }
        }

        count++;

        try (Playback playback = createPlayback(cache))
        {
            opened.put(media, playback);

            final AudioInputStream input = playback.getInput();
            final SourceDataLine dataLine = playback.getDataLine();
            openLine(dataLine, input);
            updateAlignment(dataLine, alignment);
            updateVolume(dataLine, AudioFactory.getVolume() * volume / Constant.HUNDRED);
            dataLine.start();

            try
            {
                readSound(input, dataLine);
            }
            catch (final IOException exception)
            {
                if (exception.getMessage() == null || !exception.getMessage().contains("closed"))
                {
                    throw exception;
                }
            }
            close(input, dataLine);
        }
        catch (final IOException | LineUnavailableException exception)
        {
            if (last == null || exception.getMessage() != null && !exception.getMessage().equals(last.getMessage()))
            {
                LOGGER.error(media.toString(), exception);
                last = exception;
            }
        }
        finally
        {
            count--;
        }
    }

    @Override
    public void play()
    {
        play(Align.CENTER);
    }

    @Override
    public void play(Align alignment)
    {
        if (volume > 0 && AudioFactory.getVolume() > 0 && System.nanoTime() - time > MIN_DELAY_NANO)
        {
            executor.execute(() -> play(media, alignment));
            time = System.nanoTime();
        }
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
    public void await()
    {
        try
        {
            executor.submit(() ->
            {
                while (count > 0)
                {
                    try
                    {
                        Thread.sleep(Constant.DECADE);
                    }
                    catch (@SuppressWarnings("unused") final InterruptedException exception)
                    {
                        Thread.currentThread().interrupt();
                        count = 0;
                    }
                }
            }).get(Constant.DECADE, TimeUnit.SECONDS);
        }
        catch (final InterruptedException exception)
        {
            Thread.currentThread().interrupt();
            LOGGER.error("Interrupted !", exception);
        }
        catch (final ExecutionException | TimeoutException exception)
        {
            LOGGER.error("Stopped !", exception);
        }
    }

    @Override
    public void setVolume(int volume)
    {
        Check.superiorOrEqual(volume, 0);
        Check.inferiorOrEqual(volume, 100);

        this.volume = volume;
    }

    @Override
    public long getTicks()
    {
        return 0L;
    }
}
