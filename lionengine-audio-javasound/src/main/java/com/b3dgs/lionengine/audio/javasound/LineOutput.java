/*
 * Copyright (C) 2013-2026 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.audio.javasound;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.b3dgs.lionengine.audio.PcmFormat;

/**
 * Output on a Java Sound {@link SourceDataLine} (ADR-0070): 16-bit signed little-endian PCM, opened and started at its
 * creation with a line buffer of a given number of frames.
 */
final class LineOutput implements MixOutput
{
    /** Bytes per sample. */
    private static final int SAMPLE_BYTES = Short.BYTES;

    /**
     * Creates the Java Sound format of a PCM format.
     *
     * @param format The PCM format.
     * @return The 16-bit signed little-endian format.
     */
    static AudioFormat toAudioFormat(PcmFormat format)
    {
        return new AudioFormat(format.sampleRate(), Short.SIZE, format.channels(), true, false);
    }

    /**
     * Tells whether the default mixer offers a line for a format.
     *
     * @param format The PCM format.
     * @return {@code true} if a line can be asked for it; it may still be busy when opened.
     */
    static boolean isAvailable(PcmFormat format)
    {
        boolean available;
        try
        {
            AudioSystem.getSourceDataLine(toAudioFormat(format));
            available = true;
        }
        catch (final LineUnavailableException | IllegalArgumentException | SecurityException exception)
        {
            available = false;
        }
        return available;
    }

    /**
     * Opens and starts a line.
     *
     * @param format The PCM format of the line.
     * @param frames The size of the line buffer, in frames.
     * @return The output, started.
     * @throws IllegalStateException If no line is available for the format.
     */
    static LineOutput open(PcmFormat format, int frames)
    {
        final AudioFormat audio = toAudioFormat(format);
        try
        {
            final SourceDataLine line = AudioSystem.getSourceDataLine(audio);
            line.open(audio, frames * format.channels() * SAMPLE_BYTES);
            line.start();
            return new LineOutput(line);
        }
        catch (final LineUnavailableException | IllegalArgumentException | SecurityException exception)
        {
            throw new IllegalStateException("No Java Sound output line for " + format, exception);
        }
    }

    /** The line, open and started. */
    private final SourceDataLine line;

    /**
     * Creates the output.
     *
     * @param line The line, open and started.
     */
    private LineOutput(SourceDataLine line)
    {
        this.line = line;
    }

    @Override
    public void write(byte[] data, int length)
    {
        line.write(data, 0, length);
    }

    @Override
    public long getPlayedFrames()
    {
        return line.getLongFramePosition();
    }

    @Override
    public void close()
    {
        line.stop();
        line.flush();
        line.close();
    }
}
