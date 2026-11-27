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

import java.util.Arrays;

import com.b3dgs.lionengine.Inbox;
import com.b3dgs.lionengine.audio.AudioBackend;
import com.b3dgs.lionengine.audio.PcmBuffer;
import com.b3dgs.lionengine.audio.PcmFormat;
import com.b3dgs.lionengine.audio.PcmStream;
import com.b3dgs.lionengine.audio.VoiceListener;

/**
 * Software mixer of the Java Sound backend: the voices, their controls, and the mixing of one audio buffer at a time
 * into 16-bit signed little-endian stereo (ADR-0018, ADR-0069, ADR-0070).
 *
 * <p>
 * It implements the backend SPI without thread or line, so that its mixing is tested headless: {@link #mix(byte[])}
 * is called by the mixing thread of {@link JavaSoundBackend}, or by a test as its clock. Every voice is mixed into a
 * double accumulator, times its gain read once per buffer, then the sum is clipped to 16 bits. A voice that ends during
 * a buffer is freed at once and its end is posted to the inbox from the mixing thread, with an event taken from a
 * pool, so that mixing allocates nothing. The pool grows, if ever, on the runtime thread at a play.
 * </p>
 *
 * <p>
 * Threads: the controls are called on the runtime thread, {@link #mix(byte[])} on the mixing thread; both hold one
 * lock, so that once a control returns, the mixing sees it, and a stream is read only under the lock. The listener is
 * set and called on the runtime thread only.
 * </p>
 */
final class SoftwareMixer implements AudioBackend
{
    /** Bits of a byte, to split a sample into its two bytes. */
    private static final int BYTE_BITS = Byte.SIZE;
    /** Bytes of a stereo frame. */
    private static final int FRAME_BYTES = PcmFormat.STEREO * Short.BYTES;
    /** Message of a closed mixer. */
    private static final String MESSAGE_CLOSED = "Java Sound backend closed";

    /**
     * Checks a gain.
     *
     * @param gain The gain.
     * @throws IllegalArgumentException If the gain is NaN or outside {@code [0, 1]}.
     */
    private static void checkGain(double gain)
    {
        if (!(gain >= 0.0 && gain <= 1.0))
        {
            throw new IllegalArgumentException("Gain outside 0 to 1: " + gain);
        }
    }

    /** The lock of the voices, the pool and the states. */
    private final Object lock = new Object();
    /** The inbox receiving the ends. */
    private final Inbox inbox;
    /** The voices, free or playing. */
    private final MixVoice[] voices;
    /** The accumulator of one buffer, interleaved stereo. */
    private final double[] accumulator;
    /** The frames of one buffer. */
    private final int bufferFrames;
    /** The pool of end events: the free ones are in {@code [0, freeEnds)}. */
    private VoiceEnd[] ends;
    /** The free end events. */
    private int freeEnds;
    /** The listener, runtime thread only. */
    private VoiceListener listener;
    /** The next voice number. */
    private int nextVoice = 1;
    /** The voices playing. */
    private int playing;
    /** The frames mixed since the creation. */
    private long mixedFrames;
    /** {@code true} while the output is paused. */
    private boolean outputPaused;
    /** {@code true} once closed. */
    private boolean closed;

    /**
     * Creates a mixer.
     *
     * @param inbox The inbox receiving the ends.
     * @param maxVoices The voice limit (positive).
     * @param outputRate The sample rate of the output, in Hz (positive).
     * @param bufferFrames The frames of one buffer (positive).
     * @throws IllegalArgumentException If the inbox is {@code null} or a value is not positive.
     */
    SoftwareMixer(Inbox inbox, int maxVoices, int outputRate, int bufferFrames)
    {
        if (inbox == null || maxVoices <= 0 || outputRate <= 0 || bufferFrames <= 0)
        {
            throw new IllegalArgumentException("Invalid mixer: " + maxVoices + " voices, " + bufferFrames + " frames");
        }
        this.inbox = inbox;
        this.bufferFrames = bufferFrames;
        voices = new MixVoice[maxVoices];
        for (int i = 0; i < maxVoices; i++)
        {
            voices[i] = new MixVoice(outputRate);
        }
        accumulator = new double[bufferFrames * PcmFormat.STEREO];
        ends = new VoiceEnd[maxVoices];
        for (int i = 0; i < maxVoices; i++)
        {
            ends[i] = new VoiceEnd(this);
        }
        freeEnds = maxVoices;
    }

    /**
     * Returns the size of a mixed buffer.
     *
     * @return The bytes {@link #mix(byte[])} writes.
     */
    int getBufferBytes()
    {
        return bufferFrames * FRAME_BYTES;
    }

    /**
     * Returns the frames mixed so far: the output frame at which a voice played now starts.
     *
     * @return The frames mixed since the creation.
     */
    long getMixedFrames()
    {
        synchronized (lock)
        {
            return mixedFrames;
        }
    }

    /**
     * Mixes the next buffer: every playing voice that is not paused, nothing while the output is paused; posts the
     * ends of the voices that end during it. Allocates nothing.
     *
     * @param target The bytes of the buffer, at least {@link #getBufferBytes()} long.
     * @return {@code true} if mixed, {@code false} once closed, and {@code target} is left as it is.
     */
    boolean mix(byte[] target)
    {
        synchronized (lock)
        {
            if (!closed)
            {
                Arrays.fill(accumulator, 0.0);
                if (!outputPaused)
                {
                    mixVoices();
                }
                write(target);
                mixedFrames += bufferFrames;
            }
            return !closed;
        }
    }

    /**
     * Gives back an end event and calls the listener, on the runtime thread.
     *
     * @param end The event delivered.
     */
    void deliver(VoiceEnd end)
    {
        final int voice;
        synchronized (lock)
        {
            voice = end.getVoice();
            ends[freeEnds] = end;
            freeEnds++;
        }
        final VoiceListener current = listener;
        if (current != null)
        {
            current.onVoiceEnded(voice);
        }
    }

    /**
     * Returns the gain of a voice, for the tests.
     *
     * @param voice The voice.
     * @return The gain, NaN if the voice does not play.
     */
    double getGain(int voice)
    {
        synchronized (lock)
        {
            final MixVoice found = find(voice);
            return found == null ? Double.NaN : found.getGain();
        }
    }

    @Override
    public int getMaxVoices()
    {
        return voices.length;
    }

    @Override
    public void setVoiceListener(VoiceListener listener)
    {
        this.listener = listener;
    }

    @Override
    public int play(PcmBuffer buffer, double gain, boolean looping)
    {
        if (buffer == null)
        {
            throw new IllegalArgumentException("Buffer must not be null");
        }
        checkGain(gain);
        synchronized (lock)
        {
            final MixVoice voice = take();
            int number = NO_VOICE;
            if (voice != null)
            {
                number = nextVoice;
                nextVoice++;
                voice.start(number, buffer, gain, looping);
            }
            return number;
        }
    }

    @Override
    public int play(PcmStream stream, double gain)
    {
        if (stream == null)
        {
            throw new IllegalArgumentException("Stream must not be null");
        }
        checkGain(gain);
        synchronized (lock)
        {
            final MixVoice voice = take();
            int number = NO_VOICE;
            if (voice != null)
            {
                number = nextVoice;
                nextVoice++;
                voice.start(number, stream, gain);
            }
            return number;
        }
    }

    @Override
    public void setGain(int voice, double gain)
    {
        checkGain(gain);
        synchronized (lock)
        {
            final MixVoice found = find(voice);
            if (found != null)
            {
                found.setGain(gain);
            }
        }
    }

    @Override
    public void pause(int voice)
    {
        setPaused(voice, true);
    }

    @Override
    public void resume(int voice)
    {
        setPaused(voice, false);
    }

    @Override
    public void pauseOutput()
    {
        synchronized (lock)
        {
            outputPaused = !closed;
        }
    }

    @Override
    public void resumeOutput()
    {
        synchronized (lock)
        {
            outputPaused = false;
        }
    }

    @Override
    public void stop(int voice)
    {
        synchronized (lock)
        {
            final MixVoice found = find(voice);
            if (found != null)
            {
                free(found);
            }
        }
    }

    @Override
    public boolean isPlaying(int voice)
    {
        synchronized (lock)
        {
            return find(voice) != null;
        }
    }

    @Override
    public void close()
    {
        synchronized (lock)
        {
            closed = true;
            outputPaused = false;
            for (final MixVoice voice : voices)
            {
                if (voice.getVoice() != NO_VOICE)
                {
                    free(voice);
                }
            }
        }
    }

    /**
     * Pauses or resumes a playing voice.
     *
     * @param voice The voice.
     * @param paused {@code true} to pause.
     */
    private void setPaused(int voice, boolean paused)
    {
        synchronized (lock)
        {
            final MixVoice found = find(voice);
            if (found != null)
            {
                found.setPaused(paused);
            }
        }
    }

    /**
     * Takes a free voice, under the lock, and makes sure the pool has an end event for every playing voice.
     *
     * @return The voice, counted as playing, {@code null} if every voice is busy.
     * @throws IllegalStateException If closed.
     */
    private MixVoice take()
    {
        if (closed)
        {
            throw new IllegalStateException(MESSAGE_CLOSED);
        }
        MixVoice free = null;
        for (int i = 0; i < voices.length && free == null; i++)
        {
            if (voices[i].getVoice() == NO_VOICE)
            {
                free = voices[i];
            }
        }
        if (free != null)
        {
            playing++;
            growEnds();
        }
        return free;
    }

    /**
     * Grows the pool of end events when it has fewer free events than playing voices, which happens only when ends
     * are not delivered while voices keep being played.
     */
    private void growEnds()
    {
        if (freeEnds < playing)
        {
            final VoiceEnd[] grown = Arrays.copyOf(ends, ends.length * 2);
            final int added = grown.length - ends.length;
            System.arraycopy(grown, 0, grown, added, freeEnds);
            for (int i = 0; i < added; i++)
            {
                grown[i] = new VoiceEnd(this);
            }
            ends = grown;
            freeEnds += added;
        }
    }

    /**
     * Finds a playing voice, under the lock.
     *
     * @param voice The voice number.
     * @return The voice, {@code null} if it does not play.
     */
    private MixVoice find(int voice)
    {
        MixVoice found = null;
        for (int i = 0; i < voices.length && found == null && voice != NO_VOICE; i++)
        {
            if (voices[i].getVoice() == voice)
            {
                found = voices[i];
            }
        }
        return found;
    }

    /**
     * Frees a playing voice, under the lock, without end.
     *
     * @param voice The voice.
     */
    private void free(MixVoice voice)
    {
        voice.release();
        playing--;
    }

    /**
     * Mixes every playing voice that is not paused, and posts the ends, under the lock.
     */
    private void mixVoices()
    {
        for (final MixVoice voice : voices)
        {
            final int number = voice.getVoice();
            if (number != NO_VOICE && !voice.isPaused() && voice.mix(accumulator, bufferFrames))
            {
                free(voice);
                postEnd(number);
            }
        }
    }

    /**
     * Posts the end of a voice with an event of the pool, under the lock; a rejected event goes back to the pool and
     * the end is lost, as the inbox counts it.
     *
     * @param voice The voice that ended.
     */
    private void postEnd(int voice)
    {
        freeEnds--;
        final VoiceEnd end = ends[freeEnds];
        end.setVoice(voice);
        if (!inbox.post(end))
        {
            freeEnds++;
        }
    }

    /**
     * Writes the accumulator to bytes, clipped to 16 bits, little-endian.
     *
     * @param target The bytes.
     */
    private void write(byte[] target)
    {
        for (int i = 0; i < accumulator.length; i++)
        {
            final int sample = (int) Math.clamp(accumulator[i], Short.MIN_VALUE, Short.MAX_VALUE);
            target[i * 2] = (byte) sample;
            target[i * 2 + 1] = (byte) (sample >> BYTE_BITS);
        }
    }
}
