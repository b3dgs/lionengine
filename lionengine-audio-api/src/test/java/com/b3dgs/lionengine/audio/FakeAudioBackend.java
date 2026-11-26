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
 * Audio backend that plays nothing, for headless tests of the audio logic (ADR-0045, ADR-0065, REQ-AUDIO-009).
 *
 * <p>
 * Each voice counts down the frames of its sound; {@link #advance(int)} is the clock of the test: it moves every voice
 * forward and posts the end of each one that runs out to the inbox, in the order the voices started. The voice limit is
 * set at creation. What was played is recorded. Not thread-safe: the test drives it from its runtime thread.
 * </p>
 */
public final class FakeAudioBackend implements AudioBackend
{
    /** Default voice limit. */
    public static final int DEFAULT_MAX_VOICES = 32;

    /** Inbox receiving the end events. */
    private final Inbox inbox;
    /** Voice limit. */
    private final int maxVoices;
    /** Frames left of each playing voice, in start order. */
    private final Map<Integer, int[]> playing = new LinkedHashMap<>();
    /** Every buffer played, in order. */
    private final List<PcmBuffer> played = new ArrayList<>();
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
     * Moves every playing voice forward; each one that runs out ends and its end is posted to the inbox.
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
        final Iterator<Map.Entry<Integer, int[]>> iterator = playing.entrySet().iterator();
        while (iterator.hasNext())
        {
            final Map.Entry<Integer, int[]> entry = iterator.next();
            final int[] left = entry.getValue();
            left[0] -= Math.min(left[0], frames);
            if (left[0] == 0)
            {
                iterator.remove();
                final int voice = entry.getKey().intValue();
                inbox.post(() -> deliver(voice));
            }
        }
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
     * Returns the buffers played.
     *
     * @return Every buffer given to {@link #play(PcmBuffer)} that got a voice, in order, unmodifiable.
     */
    public List<PcmBuffer> getPlayed()
    {
        return Collections.unmodifiableList(played);
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
        if (closed)
        {
            throw new IllegalStateException("Fake audio backend closed");
        }
        if (playing.size() >= maxVoices)
        {
            return NO_VOICE;
        }
        final int voice = nextVoice++;
        playing.put(Integer.valueOf(voice), new int[]
        {
            buffer.getFrames()
        });
        played.add(buffer);
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
}
