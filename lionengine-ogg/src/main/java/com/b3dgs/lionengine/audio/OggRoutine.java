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
package com.b3dgs.lionengine.audio;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityMath;
import com.b3dgs.lionengine.core.Verbose;

/**
 * Ogg music routine.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class OggRoutine
        extends Thread
{
    /** Buffer size. */
    private static final int DEFAULT_EXTERNAL_BUFFER_SIZE = 128000;

    /**
     * Get the audio stream from a file.
     * 
     * @param mus The input file.
     * @return The audio stream.
     * @throws UnsupportedAudioFileException If error.
     * @throws IOException If error on reading audio.
     */
    private static AudioInputStream getAudioStream(File mus) throws UnsupportedAudioFileException, IOException
    {
        final boolean bBigEndian = false;
        final int nSampleSizeInBits = 16;
        final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(mus);
        final AudioFormat audioFormat = audioInputStream.getFormat();
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
        final boolean bIsSupportedDirectly = AudioSystem.isLineSupported(info);

        if (!bIsSupportedDirectly)
        {
            final AudioFormat targetFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                    audioFormat.getSampleRate(), nSampleSizeInBits, audioFormat.getChannels(),
                    audioFormat.getChannels() * (nSampleSizeInBits / 8), audioFormat.getSampleRate(), bBigEndian);

            return AudioSystem.getAudioInputStream(targetFormat, audioInputStream);
        }
        return audioInputStream;
    }

    /**
     * Get data line.
     * 
     * @param audioFormat The input audio format.
     * @param nBufferSize The buffer size.
     * @return The source data line.
     * @throws LineUnavailableException If error.
     */
    private static SourceDataLine getSourceDataLine(AudioFormat audioFormat, int nBufferSize)
            throws LineUnavailableException
    {
        final DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat, nBufferSize);
        final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        line.open(audioFormat, nBufferSize);
        return line;
    }

    /** Music filename. */
    private final Media media;
    /** Repeat flag. */
    private final boolean repeat;
    /** Sound volume. */
    private int volume;
    /** Stop flag. */
    private boolean stop;

    /**
     * Constructor.
     * 
     * @param media The media music.
     * @param repeat repeat flag.
     */
    OggRoutine(Media media, boolean repeat)
    {
        super("Ogg Player");
        this.media = media;
        this.repeat = repeat;
        stop = false;
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
     * Terminate the routine.
     */
    void terminate()
    {
        stop = true;
    }

    /*
     * Thread
     */

    @Override
    public void run()
    {
        while (!stop)
        {
            final File mus = Media.getTempFile(media, true, true);
            final int bufferInternal = AudioSystem.NOT_SPECIFIED;
            try (AudioInputStream audioInputStream = OggRoutine.getAudioStream(mus);
                 SourceDataLine line = OggRoutine.getSourceDataLine(audioInputStream.getFormat(), bufferInternal);)
            {
                // Set volume
                if (line.isControlSupported(FloatControl.Type.MASTER_GAIN))
                {
                    final FloatControl gainControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                    final double gain = UtilityMath.fixBetween(volume / 100.0, 0.0, 100.0);
                    final double dB = Math.log(gain) / Math.log(10.0) * 20.0;
                    gainControl.setValue((float) dB);
                }

                line.start();
                int nBytesRead = 0;
                final byte[] abData = new byte[OggRoutine.DEFAULT_EXTERNAL_BUFFER_SIZE];

                while (nBytesRead != -1 && !stop)
                {
                    nBytesRead = audioInputStream.read(abData, 0, abData.length);
                    if (nBytesRead >= 0)
                    {
                        line.write(abData, 0, nBytesRead);
                    }
                }
                line.drain();
                line.close();
                audioInputStream.close();
            }
            catch (final UnsupportedAudioFileException exception)
            {
                Verbose.critical(OggPlayer.class, "run", "Unsupported audio file !");
            }
            catch (final LineUnavailableException exception)
            {
                Verbose.critical(OggPlayer.class, "run", "Unavailable audio line !");
            }
            catch (final IOException exception)
            {
                Verbose.exception(OggPlayer.class, "run", exception);
            }
            stop = !repeat;
        }
    }
}
