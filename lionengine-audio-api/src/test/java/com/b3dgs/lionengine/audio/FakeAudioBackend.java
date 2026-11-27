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
 * Audio backend that plays nothing, for headless tests of the audio logic (ADR-0045, ADR-0065, ADR-0068, ADR-0069,
 * ADR-0071, REQ-AUDIO-009, REQ-AUDIO-011, REQ-AUDIO-012).
 *
 * <p>
 * Each buffer voice counts down the frames of its sound; each streamed voice pulls the frames of its stream, in pieces
 * of at most {@link #PULL_FRAMES} frames, as a real backend fills its output. {@link #advance(int)} is the clock of the
 * test: it moves every voice forward and posts the end of each one that runs out to the inbox, in the order the voices
 * started. The voice limit is set at creation. What was played is recorded, and the gain, the looping and the pause of
 * each voice playing: a looping buffer voice wraps to its first frame and never ends, a paused voice does not move and
 * its stream is not pulled, and nothing moves while the output is paused. The fake does not mix, so it applies no gain
 * to samples. A fade ramps the gain of its voice linearly over the frames the voice moves, its audio buffer being the
 * frame: a fade of {@code s} seconds lasts {@code ceil(s × rate)} frames of the voice (at least one), the rate being
 * the sample rate of its sound; the voice is at the target gain once they are played, and the completion is then
 * posted to the inbox (ADR-0071). Not thread-safe: the test drives it from its runtime thread, which also pulls the
 * streams.
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
    /** Listener of the ended fades, {@code null} if none. */
    private FadeListener fadeListener;
    /** Next voice. */
    private int nextVoice = 1;
    /** {@code true} once closed. */
    private boolean closed;
    /** {@code true} while the output is paused. */
    private boolean outputPaused;

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
     * asked: a stream that gave exactly its last frame runs out at the next advance, as on a real backend. A looping
     * buffer voice wraps and never runs out; a paused voice does not move; nothing moves while the output is paused.
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
        if (outputPaused)
        {
            return;
        }
        final Iterator<Map.Entry<Integer, Voice>> iterator = playing.entrySet().iterator();
        while (iterator.hasNext())
        {
            final Map.Entry<Integer, Voice> entry = iterator.next();
            final Voice voice = entry.getValue();
            if (!voice.paused)
            {
                final long before = voice.played;
                final int number = entry.getKey().intValue();
                if (voice.stream == null ? countDown(voice, frames) : pull(voice, frames))
                {
                    iterator.remove();
                    inbox.post(() -> deliver(number));
                }
                else if (voice.ramp(voice.played - before))
                {
                    inbox.post(() -> deliverFade(number));
                }
            }
        }
    }

    /**
     * Returns the buffers played.
     *
     * @return Every buffer given to {@link #play(PcmBuffer, double, boolean)} that got a voice, in order, unmodifiable.
     */
    public List<PcmBuffer> getPlayed()
    {
        return Collections.unmodifiableList(played);
    }

    /**
     * Returns the streams played.
     *
     * @return Every stream given to {@link #play(PcmStream, double)} that got a voice, in order, unmodifiable.
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
     * Returns the gain of a voice.
     *
     * @param voice The voice.
     * @return The gain last given to the voice, NaN if it is not playing.
     */
    public double getGain(int voice)
    {
        final Voice playingVoice = playing.get(Integer.valueOf(voice));
        return playingVoice == null ? Double.NaN : playingVoice.gain;
    }

    /**
     * Tells whether a voice is fading.
     *
     * @param voice The voice.
     * @return {@code true} if it is a playing voice whose fade did not reach its target yet.
     */
    public boolean isFading(int voice)
    {
        final Voice playingVoice = playing.get(Integer.valueOf(voice));
        return playingVoice != null && playingVoice.fadeLeft > 0L;
    }

    /**
     * Returns the target gain of the fade of a voice.
     *
     * @param voice The voice.
     * @return The gain its fade reaches, NaN if it is not a playing voice that is fading.
     */
    public double getFadeTarget(int voice)
    {
        final Voice playingVoice = playing.get(Integer.valueOf(voice));
        return playingVoice == null || playingVoice.fadeLeft == 0L ? Double.NaN : playingVoice.fadeTarget;
    }

    /**
     * Tells whether a voice loops.
     *
     * @param voice The voice.
     * @return {@code true} if it is a playing buffer voice played looping.
     */
    public boolean isLooping(int voice)
    {
        final Voice playingVoice = playing.get(Integer.valueOf(voice));
        return playingVoice != null && playingVoice.looping;
    }

    /**
     * Tells whether a voice is paused on its own.
     *
     * @param voice The voice.
     * @return {@code true} if it is a playing voice paused by {@link #pause(int)}.
     */
    public boolean isPaused(int voice)
    {
        final Voice playingVoice = playing.get(Integer.valueOf(voice));
        return playingVoice != null && playingVoice.paused;
    }

    /**
     * Returns the frames a voice played.
     *
     * @param voice The voice.
     * @return The frames it moved forward, across its loops, {@code 0} if it is not playing.
     */
    public long getPlayedFrames(int voice)
    {
        final Voice playingVoice = playing.get(Integer.valueOf(voice));
        return playingVoice == null ? 0L : playingVoice.played;
    }

    /**
     * Tells whether the output is paused.
     *
     * @return {@code true} from {@link #pauseOutput()} until {@link #resumeOutput()} or {@link #close()}.
     */
    public boolean isOutputPaused()
    {
        return outputPaused;
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
    public void setFadeListener(FadeListener listener)
    {
        fadeListener = listener;
    }

    @Override
    public int play(PcmBuffer buffer, double gain, boolean looping)
    {
        if (buffer == null)
        {
            throw new IllegalArgumentException("Buffer must not be null");
        }
        checkGain(gain);
        final int voice = start(new Voice(buffer.getFrames(), null, buffer.getFormat().sampleRate(), gain, looping));
        if (voice != NO_VOICE)
        {
            played.add(buffer);
        }
        return voice;
    }

    @Override
    public int play(PcmStream stream, double gain)
    {
        if (stream == null)
        {
            throw new IllegalArgumentException("Stream must not be null");
        }
        checkGain(gain);
        final int voice = start(new Voice(0, stream, stream.getFormat().sampleRate(), gain, false));
        if (voice != NO_VOICE)
        {
            streams.add(stream);
        }
        return voice;
    }

    @Override
    public void setGain(int voice, double gain)
    {
        checkGain(gain);
        final Voice playingVoice = playing.get(Integer.valueOf(voice));
        if (playingVoice != null)
        {
            if (playingVoice.fadeLeft > 0L)
            {
                playingVoice.fadeTarget = gain;
            }
            else
            {
                playingVoice.gain = gain;
            }
        }
    }

    @Override
    public void fade(int voice, double gain, double seconds)
    {
        checkGain(gain);
        if (!(seconds >= 0.0 && seconds < Double.POSITIVE_INFINITY))
        {
            throw new IllegalArgumentException("Fade duration not finite and not negative: " + seconds);
        }
        final Voice playingVoice = playing.get(Integer.valueOf(voice));
        if (playingVoice != null)
        {
            playingVoice.fadeTarget = gain;
            playingVoice.fadeLeft = Math.max(1L, (long) Math.ceil(seconds * playingVoice.rate));
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
        outputPaused = !closed;
    }

    @Override
    public void resumeOutput()
    {
        outputPaused = false;
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
        outputPaused = false;
        playing.clear();
    }

    /**
     * Checks a gain.
     *
     * @param gain The gain.
     * @throws IllegalArgumentException If it is NaN or outside {@code [0, 1]}.
     */
    private static void checkGain(double gain)
    {
        if (!(gain >= 0.0 && gain <= 1.0))
        {
            throw new IllegalArgumentException("Gain outside 0 to 1: " + gain);
        }
    }

    /**
     * Pauses or resumes a voice if it plays.
     *
     * @param voice The voice.
     * @param paused {@code true} to pause it.
     */
    private void setPaused(int voice, boolean paused)
    {
        final Voice playingVoice = playing.get(Integer.valueOf(voice));
        if (playingVoice != null)
        {
            playingVoice.paused = paused;
        }
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
        final boolean ended;
        if (voice.looping && voice.frames > 0)
        {
            // Wraps to the first frame: the frames left stay in 1 to the frames of the sound.
            final long left = voice.left - (long) frames % voice.frames;
            voice.left = (int) (left > 0 ? left : left + voice.frames);
            voice.played += frames;
            ended = false;
        }
        else
        {
            final int moved = Math.min(voice.left, frames);
            voice.left -= moved;
            voice.played += moved;
            ended = voice.left == 0;
        }
        return ended;
    }

    /**
     * Pulls frames from a streamed voice, in pieces of at most {@link #PULL_FRAMES} frames.
     *
     * @param voice The voice.
     * @param frames The frames played.
     * @return {@code true} if the stream returned fewer frames than asked, or threw an unchecked exception.
     */
    private boolean pull(Voice voice, int frames)
    {
        int left = frames;
        boolean ended = false;
        while (left > 0 && !ended)
        {
            final int asked = Math.min(left, PULL_FRAMES);
            int read;
            try
            {
                read = voice.stream.read(scratch, 0, asked);
            }
            // CHECKSTYLE IGNORE LINE: IllegalCatch
            catch (final RuntimeException exception)
            {
                // A failing stream ends its voice, as on a real backend (ADR-0072).
                read = -1;
            }
            if (read >= 0)
            {
                voice.pulled += read;
                voice.played += read;
                left -= read;
            }
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
     * Delivers the end of the fade of a voice to the listener, on the runtime thread.
     *
     * @param voice The voice.
     */
    private void deliverFade(int voice)
    {
        final FadeListener current = fadeListener;
        if (current != null)
        {
            current.onFadeEnded(voice);
        }
    }

    /**
     * State of a playing voice.
     */
    private static final class Voice
    {
        /** The stream of a streamed voice, {@code null} for a buffer voice. */
        private final PcmStream stream;
        /** The frames of the sound of a buffer voice. */
        private final int frames;
        /** {@code true} for a looping buffer voice. */
        private final boolean looping;
        /** The sample rate of its sound, in Hz. */
        private final int rate;
        /** The frames left of a buffer voice, before its end or its next wrap. */
        private int left;
        /** The frames pulled from the stream. */
        private long pulled;
        /** The frames played, across the loops. */
        private long played;
        /** The gain. */
        private double gain;
        /** {@code true} while paused on its own. */
        private boolean paused;
        /** The target gain of the fade. */
        private double fadeTarget;
        /** The frames left of the fade, {@code 0} when not fading. */
        private long fadeLeft;

        /**
         * Creates the state.
         *
         * @param frames The frames of a buffer voice.
         * @param stream The stream of a streamed voice, {@code null} for a buffer voice.
         * @param rate The sample rate of its sound.
         * @param gain The gain.
         * @param looping {@code true} for a looping buffer voice.
         */
        Voice(int frames, PcmStream stream, int rate, double gain, boolean looping)
        {
            this.frames = frames;
            left = frames;
            this.stream = stream;
            this.rate = rate;
            this.gain = gain;
            this.looping = looping;
        }

        /**
         * Moves the fade forward by the frames the voice played.
         *
         * @param moved The frames played.
         * @return {@code true} if the fade reached its target now.
         */
        boolean ramp(long moved)
        {
            boolean reached = false;
            if (fadeLeft > 0L && moved > 0L)
            {
                if (moved >= fadeLeft)
                {
                    gain = fadeTarget;
                    fadeLeft = 0L;
                    reached = true;
                }
                else
                {
                    gain += (fadeTarget - gain) * moved / fadeLeft;
                    fadeLeft -= moved;
                }
            }
            return reached;
        }
    }
}
