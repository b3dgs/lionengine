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

import com.b3dgs.lionengine.audio.PcmBuffer;
import com.b3dgs.lionengine.audio.PcmFormat;
import com.b3dgs.lionengine.audio.PcmStream;

/**
 * One voice of the {@link SoftwareMixer}: its sound, gain, looping, pause, and its position in the sound (ADR-0070).
 *
 * <p>
 * The voice reads its sound through a window of {@link #WINDOW_FRAMES} frames, copied from the buffer or pulled from
 * the stream, so that it allocates nothing while mixing. It converts its sound to the output format as it mixes: a
 * mono sound is played on both channels, and a sound of another sample rate is resampled by linear interpolation
 * between two frames, its position advancing by a fixed-point step of {@link #FRACTION_BITS} fractional bits per
 * output frame. A sound of the output rate is copied as it is.
 * </p>
 *
 * <p>
 * Not thread-safe: the mixer calls it under its lock.
 * </p>
 */
final class MixVoice
{
    /** Frames of the window through which the sound is read. */
    static final int WINDOW_FRAMES = 1_024;
    /** Fractional bits of the position in the sound. */
    static final int FRACTION_BITS = 16;
    /** Mask of the fractional part of a position. */
    private static final long FRACTION_MASK = (1L << FRACTION_BITS) - 1L;
    /** Factor turning a fractional part into a weight in {@code [0, 1)}. */
    private static final double FRACTION_SCALE = 1.0 / (1L << FRACTION_BITS);
    /** Frames of a stream before its end is read. */
    private static final long UNKNOWN_FRAMES = Long.MAX_VALUE;

    /** The window: frames of the sound from {@link #windowFirst}, interleaved. */
    private final short[] window = new short[WINDOW_FRAMES * PcmFormat.STEREO];
    /** The sample rate of the output, in Hz. */
    private final int outputRate;
    /** The buffer played, {@code null} for a stream or a free voice. */
    private PcmBuffer buffer;
    /** The stream played, {@code null} for a buffer or a free voice. */
    private PcmStream stream;
    /** The voice number, {@code 0} when free. */
    private int voice;
    /** The channels of the sound. */
    private int channels;
    /** The frames of the sound; for a stream, unknown until it returned fewer frames than asked. */
    private long frames;
    /** The position in the sound, in frames with {@link #FRACTION_BITS} fractional bits. */
    private long position;
    /** The step of the position per output frame. */
    private long step;
    /** The frame of the sound at the start of the window. */
    private long windowFirst;
    /** The frames in the window. */
    private int windowFrames;
    /** {@code true} when the buffer loops. */
    private boolean looping;
    /** {@code true} when the voice is paused on its own. */
    private boolean paused;
    /** {@code true} once the stream returned fewer frames than asked: it is never read again. */
    private boolean exhausted;
    /** The gain, read once per mixed buffer. */
    private double gain;
    /** The first left sample of a looping buffer, the neighbor of its last frame. */
    private short firstLeft;
    /** The first right sample of a looping buffer. */
    private short firstRight;

    /**
     * Creates a free voice.
     *
     * @param outputRate The sample rate of the output, in Hz.
     */
    MixVoice(int outputRate)
    {
        this.outputRate = outputRate;
    }

    /**
     * Starts playing a buffer.
     *
     * @param number The voice number (positive).
     * @param sound The buffer.
     * @param voiceGain The gain.
     * @param loop {@code true} to loop.
     */
    void start(int number, PcmBuffer sound, double voiceGain, boolean loop)
    {
        start(number, sound.getFormat(), voiceGain);
        buffer = sound;
        frames = sound.getFrames();
        looping = loop && frames > 0;
        if (looping)
        {
            sound.readFrames(0, window, 0, 1);
            firstLeft = window[0];
            firstRight = window[channels - 1];
        }
    }

    /**
     * Starts playing a stream.
     *
     * @param number The voice number (positive).
     * @param source The stream.
     * @param voiceGain The gain.
     */
    void start(int number, PcmStream source, double voiceGain)
    {
        start(number, source.getFormat(), voiceGain);
        stream = source;
        frames = UNKNOWN_FRAMES;
        looping = false;
    }

    /**
     * Frees the voice; its sound is no longer referenced, so a stream is never read again.
     */
    void release()
    {
        voice = 0;
        buffer = null;
        stream = null;
    }

    /**
     * Returns the voice number.
     *
     * @return The voice, {@code 0} when free.
     */
    int getVoice()
    {
        return voice;
    }

    /**
     * Sets the gain, applied from the next mixed buffer.
     *
     * @param voiceGain The gain, in {@code [0, 1]}.
     */
    void setGain(double voiceGain)
    {
        gain = voiceGain;
    }

    /**
     * Returns the gain.
     *
     * @return The gain.
     */
    double getGain()
    {
        return gain;
    }

    /**
     * Pauses or resumes the voice.
     *
     * @param pause {@code true} to pause.
     */
    void setPaused(boolean pause)
    {
        paused = pause;
    }

    /**
     * Tells whether the voice is paused on its own.
     *
     * @return {@code true} if paused.
     */
    boolean isPaused()
    {
        return paused;
    }

    /**
     * Adds frames of the voice, times its gain, to a stereo mix.
     *
     * @param mix The mix, interleaved stereo.
     * @param count The frames to mix.
     * @return {@code true} if the sound ended during these frames.
     */
    boolean mix(double[] mix, int count)
    {
        final double voiceGain = gain;
        boolean ended = false;
        int out = 0;
        while (out < count && !ended)
        {
            long index = position >>> FRACTION_BITS;
            if (looping && index >= frames)
            {
                position %= frames << FRACTION_BITS;
                index = position >>> FRACTION_BITS;
            }
            ended = !locate(index);
            if (!ended)
            {
                mixFrame(mix, out * PcmFormat.STEREO, index, voiceGain);
                position += step;
                out++;
            }
        }
        return ended;
    }

    /**
     * Starts the voice: number, gain, step, and an empty window.
     *
     * @param number The voice number.
     * @param format The format of the sound.
     * @param voiceGain The gain.
     */
    private void start(int number, PcmFormat format, double voiceGain)
    {
        voice = number;
        channels = format.channels();
        gain = voiceGain;
        step = Math.max(1L, Math.round((double) format.sampleRate() * (1L << FRACTION_BITS) / outputRate));
        position = 0L;
        windowFirst = 0L;
        windowFrames = 0;
        exhausted = false;
        paused = false;
    }

    /**
     * Makes a frame and its next one available in the window, if the sound has them.
     *
     * @param index The frame.
     * @return {@code true} if the frame is in the window, {@code false} if the sound ended before it.
     */
    private boolean locate(long index)
    {
        if (index < windowFirst || index + 1L >= windowFirst + windowFrames)
        {
            if (stream == null)
            {
                fillFromBuffer(index);
            }
            else
            {
                fillFromStream(index);
            }
        }
        return index < frames && index < windowFirst + windowFrames;
    }

    /**
     * Copies frames of the buffer to the window, from a frame.
     *
     * @param index The first frame of the window.
     */
    private void fillFromBuffer(long index)
    {
        windowFirst = index;
        windowFrames = Math.clamp(frames - index, 0, WINDOW_FRAMES);
        if (windowFrames > 0)
        {
            buffer.readFrames((int) index, window, 0, windowFrames);
        }
    }

    /**
     * Pulls the next frames of the stream to the window, keeping the frames from a frame on; does nothing once the
     * stream returned fewer frames than asked.
     *
     * @param index The frame that must stay in the window.
     */
    private void fillFromStream(long index)
    {
        if (!exhausted)
        {
            final long end = windowFirst + windowFrames;
            final long first = Math.min(index, end);
            final int kept = (int) (end - first);
            System.arraycopy(window, (int) (first - windowFirst) * channels, window, 0, kept * channels);
            windowFirst = first;
            final int asked = WINDOW_FRAMES - kept;
            final int read = stream.read(window, kept * channels, asked);
            windowFrames = kept + read;
            if (read < asked)
            {
                exhausted = true;
                frames = windowFirst + windowFrames;
            }
        }
    }

    /**
     * Adds one frame, interpolated at the position, to the mix.
     *
     * @param mix The mix.
     * @param target The index of the left sample in the mix.
     * @param index The frame at the position.
     * @param voiceGain The gain of this buffer.
     */
    private void mixFrame(double[] mix, int target, long index, double voiceGain)
    {
        final int sample = (int) (index - windowFirst) * channels;
        double left = window[sample];
        double right = window[sample + channels - 1];
        final long fraction = position & FRACTION_MASK;
        if (fraction != 0L)
        {
            final double weight = fraction * FRACTION_SCALE;
            final boolean inWindow = index + 1L < windowFirst + windowFrames;
            final int next = sample + channels;
            left += (neighbor(inWindow, next, firstLeft, left) - left) * weight;
            right += (neighbor(inWindow, next + channels - 1, firstRight, right) - right) * weight;
        }
        mix[target] += left * voiceGain;
        mix[target + 1] += right * voiceGain;
    }

    /**
     * Returns the sample after the current one: in the window, the first one of a looping buffer at its end, or the
     * current one at the end of the sound.
     *
     * @param inWindow {@code true} if the next frame is in the window.
     * @param next The index of the next sample in the window.
     * @param first The sample of the first frame of a looping buffer.
     * @param current The current sample.
     * @return The neighbor sample.
     */
    private double neighbor(boolean inWindow, int next, short first, double current)
    {
        final double value;
        if (inWindow)
        {
            value = window[next];
        }
        else if (looping)
        {
            value = first;
        }
        else
        {
            value = current;
        }
        return value;
    }
}
