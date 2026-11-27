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

import com.b3dgs.lionengine.Inbox;
import com.b3dgs.lionengine.audio.AudioBackend;
import com.b3dgs.lionengine.audio.PcmBuffer;
import com.b3dgs.lionengine.audio.PcmFormat;
import com.b3dgs.lionengine.audio.PcmStream;
import com.b3dgs.lionengine.audio.VoiceListener;

/**
 * Desktop audio backend on Java Sound: mixes its voices in software into one {@code SourceDataLine}, on its own thread
 * (ADR-0018, ADR-0027, ADR-0069, ADR-0070, REQ-AUDIO-002).
 *
 * <p>
 * Output: {@link #FORMAT}, 44,100 Hz stereo, 16-bit signed little-endian. The mixing thread mixes
 * {@link #BUFFER_FRAMES} frames at a time and writes them to a line holding {@link #LINE_BUFFERS} buffers, which
 * blocks it at the pace of the device. A sound played now is mixed into the next buffer, so its first frame reaches
 * the device after at most the buffers queued in the line and the one being mixed: 4 × 512 frames, 46.4 ms, within
 * the 50 ms desktop target (ADR-0027), plus the latency of the device and its driver, measured by the platform tests.
 * </p>
 *
 * <p>
 * Voices: {@link #DEFAULT_MAX_VOICES} by default, twice the 16 short voices of {@code Audio}, so that the long sounds
 * and the playlists have their own voices (OQ-175, answered by ADR-0070). Every voice is converted to the output as it
 * is mixed: mono is played on both channels, another sample rate is resampled by linear interpolation. The gain of a
 * voice is read once per buffer; a buffer loops in the mixer without gap; a stream is pulled from the mixing thread,
 * only while its voice plays and neither it nor the output is paused. While the output is paused the line keeps
 * playing silence. A voice that ends posts its end to the inbox from the mixing thread; the mixing allocates nothing
 * and never touches simulation state.
 * </p>
 *
 * <p>
 * Threads: every method is called on the runtime thread. A control waits at most for the mixing of one buffer, the
 * pulls of the streams included, so that once {@link #stop(int)}, {@link #pause(int)}, {@link #pauseOutput()} or
 * {@link #close()} returns, the streams concerned are no longer read. {@link #close()} also stops the mixing thread and
 * releases the line.
 * </p>
 *
 * @since 11.0.0
 */
public final class JavaSoundBackend implements AudioBackend
{
    /** Sample rate of the output, in Hz. */
    public static final int SAMPLE_RATE = 44_100;
    /** Format of the output: {@link #SAMPLE_RATE}, stereo, 16 bits. */
    public static final PcmFormat FORMAT = new PcmFormat(SAMPLE_RATE, PcmFormat.STEREO);
    /** Frames mixed at a time: 512, 11.6 ms; the gain of a voice is read once per buffer. */
    public static final int BUFFER_FRAMES = 512;
    /** Buffers held by the line: 3, 1,536 frames, 34.8 ms. */
    public static final int LINE_BUFFERS = 3;
    /** Default voice limit: 32, the 16 short voices of {@code Audio} and 16 voices for the long sounds. */
    public static final int DEFAULT_MAX_VOICES = 32;

    /** Longest wait for the mixing thread at the close, in milliseconds. */
    private static final long CLOSE_TIMEOUT_MILLIS = 1_000L;
    /** Name of the mixing thread. */
    private static final String THREAD_NAME = "LionEngine Java Sound mixer";

    /**
     * Opens the line after checking the arguments, so that nothing is opened for invalid arguments.
     *
     * @param inbox The inbox.
     * @param maxVoices The voice limit.
     * @return The line output.
     * @throws IllegalArgumentException If the inbox is {@code null} or the voice limit is not positive.
     * @throws IllegalStateException If no line is available.
     */
    private static MixOutput openLine(Inbox inbox, int maxVoices)
    {
        if (inbox == null || maxVoices <= 0)
        {
            throw new IllegalArgumentException("Invalid Java Sound backend: " + maxVoices + " voices");
        }
        return LineOutput.open(FORMAT, BUFFER_FRAMES * LINE_BUFFERS);
    }

    /** The mixer. */
    private final SoftwareMixer mixer;
    /** The output. */
    private final MixOutput output;
    /** The mixing thread. */
    private final Thread thread;
    /** {@code true} once closed, runtime thread only. */
    private boolean closed;

    /**
     * Creates a backend with {@link #DEFAULT_MAX_VOICES} voices, opens the default line and starts the mixing thread.
     *
     * @param inbox The inbox of the loop, receiving the ends of the voices (never {@code null}).
     * @throws IllegalArgumentException If {@code inbox} is {@code null}.
     * @throws IllegalStateException If no Java Sound line is available for {@link #FORMAT}.
     */
    public JavaSoundBackend(Inbox inbox)
    {
        this(inbox, DEFAULT_MAX_VOICES);
    }

    /**
     * Creates a backend, opens the default line and starts the mixing thread.
     *
     * @param inbox The inbox of the loop, receiving the ends of the voices (never {@code null}).
     * @param maxVoices The voice limit (positive); keep it above the short-voice limit of {@code Audio}, so that the
     *            long sounds have voices.
     * @throws IllegalArgumentException If {@code inbox} is {@code null} or {@code maxVoices} is not positive.
     * @throws IllegalStateException If no Java Sound line is available for {@link #FORMAT}.
     */
    public JavaSoundBackend(Inbox inbox, int maxVoices)
    {
        this(inbox, maxVoices, openLine(inbox, maxVoices));
    }

    /**
     * Creates a backend on an output and starts the mixing thread.
     *
     * @param inbox The inbox.
     * @param maxVoices The voice limit.
     * @param output The output, owned by the backend from now on.
     */
    JavaSoundBackend(Inbox inbox, int maxVoices, MixOutput output)
    {
        mixer = new SoftwareMixer(inbox, maxVoices, SAMPLE_RATE, BUFFER_FRAMES);
        this.output = output;
        thread = new Thread(new MixLoop(mixer, output), THREAD_NAME);
        thread.setDaemon(true);
        thread.setPriority(Thread.MAX_PRIORITY);
        thread.start();
    }

    /**
     * Tells whether the mixing thread runs, for the tests.
     *
     * @return {@code true} until the close stopped it.
     */
    boolean isMixing()
    {
        return thread.isAlive();
    }

    /**
     * Returns the frames mixed so far, for the latency measurement.
     *
     * @return The output frame at which a voice played now starts.
     */
    long getMixedFrames()
    {
        return mixer.getMixedFrames();
    }

    /**
     * Returns the frames played by the output, for the latency measurement.
     *
     * @return The frames the output played.
     */
    long getPlayedFrames()
    {
        return output.getPlayedFrames();
    }

    @Override
    public int getMaxVoices()
    {
        return mixer.getMaxVoices();
    }

    @Override
    public void setVoiceListener(VoiceListener listener)
    {
        mixer.setVoiceListener(listener);
    }

    @Override
    public int play(PcmBuffer buffer, double gain, boolean looping)
    {
        return mixer.play(buffer, gain, looping);
    }

    @Override
    public int play(PcmStream stream, double gain)
    {
        return mixer.play(stream, gain);
    }

    @Override
    public void setGain(int voice, double gain)
    {
        mixer.setGain(voice, gain);
    }

    @Override
    public void pause(int voice)
    {
        mixer.pause(voice);
    }

    @Override
    public void resume(int voice)
    {
        mixer.resume(voice);
    }

    @Override
    public void pauseOutput()
    {
        mixer.pauseOutput();
    }

    @Override
    public void resumeOutput()
    {
        mixer.resumeOutput();
    }

    @Override
    public void stop(int voice)
    {
        mixer.stop(voice);
    }

    @Override
    public boolean isPlaying(int voice)
    {
        return mixer.isPlaying(voice);
    }

    @Override
    public void close()
    {
        if (!closed)
        {
            closed = true;
            mixer.close();
            output.close();
            try
            {
                thread.join(CLOSE_TIMEOUT_MILLIS);
            }
            catch (final InterruptedException exception)
            {
                Thread.currentThread().interrupt();
            }
        }
    }
}
