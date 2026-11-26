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
package com.b3dgs.lionengine.audio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.b3dgs.lionengine.Inbox;

/**
 * Audio backend that plays nothing, for headless tests of the audio logic (ADR-0045, ADR-0065, ADR-0068,
 * REQ-AUDIO-009).
 *
 * <p>
 * Each buffer voice counts down the frames of its sound; each streamed voice pulls the frames of its stream, in pieces
 * of at most {@link #PULL_FRAMES} frames, as a real backend fills its output. {@link #advance(int)} is the clock of the
 * test: it moves every voice forward and posts the end of each one that runs out to the inbox, in the order the voices
 * started. The voice limit is set at creation. What was played is recorded. Not thread-safe: the test drives it from
 * its runtime thread, which also pulls the streams.
 * </p>
 */
public final class FakeAudioBackend implements AudioBackend
{
    /** Default voice limit. */
    public static final int DEFAULT_MAX_VOICES = 32;
    /** Largest number of frames pulled from a stream at once, as an output buffer of a real backend. */
    public static final int PULL_FRAMES = 1_024;

    /** Inbox receiving the end events. */
    private final Inbox inbox;
    /** Voice limit. */
    private final int maxVoices;
    /** Playing voices, in start order. */
    private final Map<Integer, Voice> playing = new LinkedHashMap<>();
    /** Every buffer played, in order. */
    private final List<PcmBuffer> played = new ArrayList<>();
    /** Every stream played, in order. */
    private final List<PcmStream> streams = new ArrayList<>();
    /** The frames pulled from the streams, stereo at most. */
    private final short[] scratch = new short[PULL_FRAMES * PcmFormat.STEREO];
    /** Listener of the ended voices, {@code null} if none. */
    private VoiceListener listener;
    /** Next voice. */
    private int nextVoice = 1;
    /** {@code true} once closed. */
    private boolean closed;

    /**
     * Creates a backend of {@link #DEFAULT_MAX_VOICES} voices.
     *
     * @param inbox The inbox receiving the end events (never {@code null}).
     * @throws IllegalArgumentException If {@code inbox} is {@code null}.
     */
    public FakeAudioBackend(Inbox inbox)
    {
        this(inbox, DEFAULT_MAX_VOICES);
    }

    /**
     * Creates a backend.
     *
     * @param inbox The inbox receiving the end events (never {@code null}).
     * @param maxVoices The voice limit (positive).
     * @throws IllegalArgumentException If {@code inbox} is {@code null} or the limit is not positive.
     */
    public FakeAudioBackend(Inbox inbox, int maxVoices)
    {
        if (inbox == null || maxVoices <= 0)
        {
            throw new IllegalArgumentException("Invalid fake audio backend: " + maxVoices + " voices");
        }
        this.inbox = inbox;
        this.maxVoices = maxVoices;
    }

    /**
     * Moves every playing voice forward; each one that runs out ends and its end is posted to the inbox. A streamed
     * voice pulls up to {@code frames} frames from its stream, and runs out when the stream returns fewer frames than
     * asked: a stream that gave exactly its last frame runs out at the next advance, as on a real backend.
     *
     * @param frames The number of frames played (not negative).
     * @throws IllegalArgumentException If {@code frames} is negative.
     */
    public void advance(int frames)
    {
        if (frames < 0)
        {
            throw new IllegalArgumentException("Frames must not be negative: " + frames);
        }
        final Iterator<Map.Entry<Integer, Voice>> iterator = playing.entrySet().iterator();
        while (iterator.hasNext())
        {
            final Map.Entry<Integer, Voice> entry = iterator.next();
            final Voice voice = entry.getValue();
            if (voice.stream == null ? countDown(voice, frames) : pull(voice, frames))
            {
                iterator.remove();
                final int ended = entry.getKey().intValue();
                inbox.post(() -> deliver(ended));
            }
        }
    }

    /**
     * Returns the buffers played.
     *
     * @return Every buffer given to {@link #play(PcmBuffer)} that got a voice, in order, unmodifiable.
     */
    public List<PcmBuffer> getPlayed()
    {
        return Collections.unmodifiableList(played);
    }

    /**
     * Returns the streams played.
     *
     * @return Every stream given to {@link #play(PcmStream)} that got a voice, in order, unmodifiable.
     */
    public List<PcmStream> getStreams()
    {
        return Collections.unmodifiableList(streams);
    }

    /**
     * Returns the frames pulled from the stream of a voice.
     *
     * @param voice The voice.
     * @return The frames pulled so far from its stream, {@code 0} if it is not a playing streamed voice.
     */
    public long getPulledFrames(int voice)
    {
        final Voice playingVoice = playing.get(Integer.valueOf(voice));
        return playingVoice == null ? 0L : playingVoice.pulled;
    }

    /**
     * Returns the number of voices playing.
     *
     * @return The number of voices that neither ended nor were stopped.
     */
    public int getPlayingCount()
    {
        return playing.size();
    }

    /**
     * Tells whether the backend is closed.
     *
     * @return {@code true} once {@link #close()} was called.
     */
    public boolean isClosed()
    {
        return closed;
    }

    @Override
    public int getMaxVoices()
    {
        return maxVoices;
    }

    @Override
    public void setVoiceListener(VoiceListener listener)
    {
        this.listener = listener;
    }

    @Override
    public int play(PcmBuffer buffer)
    {
        if (buffer == null)
        {
            throw new IllegalArgumentException("Buffer must not be null");
        }
        final int voice = start(new Voice(buffer.getFrames(), null));
        if (voice != NO_VOICE)
        {
            played.add(buffer);
        }
        return voice;
    }

    @Override
    public int play(PcmStream stream)
    {
        if (stream == null)
        {
            throw new IllegalArgumentException("Stream must not be null");
        }
        final int voice = start(new Voice(0, stream));
        if (voice != NO_VOICE)
        {
            streams.add(stream);
        }
        return voice;
    }

    @Override
    public void stop(int voice)
    {
        playing.remove(Integer.valueOf(voice));
    }

    @Override
    public boolean isPlaying(int voice)
    {
        return playing.containsKey(Integer.valueOf(voice));
    }

    @Override
    public void close()
    {
        closed = true;
        playing.clear();
    }

    /**
     * Starts a voice if one is free.
     *
     * @param voice The voice state.
     * @return The voice, or {@link #NO_VOICE}.
     * @throws IllegalStateException If closed.
     */
    private int start(Voice voice)
    {
        if (closed)
        {
            throw new IllegalStateException("Fake audio backend closed");
        }
        if (playing.size() >= maxVoices)
        {
            return NO_VOICE;
        }
        final int started = nextVoice++;
        playing.put(Integer.valueOf(started), voice);
        return started;
    }

    /**
     * Moves a buffer voice forward.
     *
     * @param voice The voice.
     * @param frames The frames played.
     * @return {@code true} if it ran out.
     */
    private static boolean countDown(Voice voice, int frames)
    {
        voice.left -= Math.min(voice.left, frames);
        return voice.left == 0;
    }

    /**
     * Pulls frames from a streamed voice, in pieces of at most {@link #PULL_FRAMES} frames.
     *
     * @param voice The voice.
     * @param frames The frames played.
     * @return {@code true} if the stream returned fewer frames than asked.
     */
    private boolean pull(Voice voice, int frames)
    {
        int left = frames;
        boolean ended = false;
        while (left > 0 && !ended)
        {
            final int asked = Math.min(left, PULL_FRAMES);
            final int read = voice.stream.read(scratch, 0, asked);
            voice.pulled += read;
            left -= read;
            ended = read < asked;
        }
        return ended;
    }

    /**
     * Delivers the end of a voice to the listener, on the runtime thread.
     *
     * @param voice The voice.
     */
    private void deliver(int voice)
    {
        final VoiceListener current = listener;
        if (current != null)
        {
            current.onVoiceEnded(voice);
        }
    }

    /**
     * State of a playing voice.
     */
    private static final class Voice
    {
        /** The stream of a streamed voice, {@code null} for a buffer voice. */
        private final PcmStream stream;
        /** The frames left of a buffer voice. */
        private int left;
        /** The frames pulled from the stream. */
        private long pulled;

        /**
         * Creates the state.
         *
         * @param left The frames of a buffer voice.
         * @param stream The stream of a streamed voice, {@code null} for a buffer voice.
         */
        Voice(int left, PcmStream stream)
        {
            this.left = left;
            this.stream = stream;
        }
    }
}
