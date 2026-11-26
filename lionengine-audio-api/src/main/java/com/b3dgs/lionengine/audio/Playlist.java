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

import com.b3dgs.lionengine.resources.ResourceException;

/**
 * Predefined sequence of long sounds played one after the other (ADR-0004, ADR-0018, ADR-0027, ADR-0033, ADR-0068,
 * REQ-AUDIO-008).
 *
 * <p>
 * {@link #play()} starts the first track and prepares the next one ahead, so that it starts without reading its header
 * when the current one ends. The end of a track is posted to the inbox by the backend, and the advance runs at its
 * delivery, on the runtime thread, independently of the backend: the next track starts, and the one after it is
 * prepared. After the last track the playlist stops, by default, or starts again from the first one with
 * {@link #setLooping(boolean)}. {@link #stop()} stops it at once; stopping the voice of its track with
 * {@link Audio#stop(int)}, or releasing its track, stops it as well.
 * </p>
 *
 * <p>
 * Failures: a track that fails to prepare or to start, or whose stream fails while it plays, is reported to the
 * {@link PlaylistListener} with the playlist, the index of the track and the exception, then skipped: the next track
 * starts. With {@link #setAbortOnFailure(boolean)}, the playlist stops instead. When every track fails in a row, the
 * playlist stops, even when looping. The listener may stop or play the playlist: the advance then goes no further.
 * </p>
 *
 * <p>
 * Tracks: the playlist refers to the {@link LongSound} handles it is given (the array is copied) and releases none:
 * their owner releases them, normally the scene on exit, after stopping the playlist. A track playing elsewhere is
 * reported as failing when its turn comes. A track that follows itself, as in a playlist of one track that loops, is
 * prepared at its turn only, since a long sound plays on one voice at a time.
 * </p>
 *
 * <p>
 * Threads: runtime thread only (ADR-0018), not thread-safe; the listener is called on the runtime thread.
 * </p>
 *
 * @since 11.0.0
 */
public final class Playlist
{
    /** Index of the track of a stopped playlist. */
    public static final int NO_TRACK = -1;

    /** The audio system playing the tracks. */
    private final Audio audio;
    /** The tracks, in order. */
    private final LongSound[] tracks;
    /** The listener of the ends of the tracks. */
    private final TrackListener trackListener = new Tracks();
    /** The listener of the failures, {@code null} if none. */
    private PlaylistListener listener;
    /** {@code true} to start again after the last track. */
    private boolean looping;
    /** {@code true} to stop at the first failure. */
    private boolean abortOnFailure;
    /** The index of the track playing, {@link #NO_TRACK} when stopped. */
    private int track = NO_TRACK;
    /** The voice of the track playing, {@link AudioBackend#NO_VOICE} when stopped. */
    private int voice = AudioBackend.NO_VOICE;
    /** The track prepared ahead by the playlist, {@code null} if none. */
    private LongSound preparedAhead;
    /** Count of the stops, so that an advance ends when the listener stops or plays the playlist. */
    private int stops;

    /**
     * Creates a stopped playlist that stops after its last track and skips the tracks that fail.
     *
     * @param audio The audio system playing the tracks (never {@code null}).
     * @param tracks The tracks, in order; the same sound may appear several times (at least one, none {@code null}).
     * @throws IllegalArgumentException If an argument or a track is {@code null}, or there is no track.
     */
    public Playlist(Audio audio, LongSound... tracks)
    {
        super();

        ShortSoundCache.checkNotNull(audio, "Audio");
        ShortSoundCache.checkNotNull(tracks, "Tracks");
        if (tracks.length == 0)
        {
            throw new IllegalArgumentException("Playlist without track");
        }
        for (final LongSound sound : tracks)
        {
            ShortSoundCache.checkNotNull(sound, "Track");
        }
        this.audio = audio;
        this.tracks = tracks.clone();
    }

    /**
     * Sets the listener of the tracks that fail; it replaces the previous one.
     *
     * @param listener The listener, {@code null} for none: failures are then skipped silently.
     */
    public void setListener(PlaylistListener listener)
    {
        this.listener = listener;
    }

    /**
     * Sets what happens after the last track.
     *
     * @param looping {@code true} to start again from the first track, {@code false} to stop (the default).
     */
    public void setLooping(boolean looping)
    {
        this.looping = looping;
    }

    /**
     * Tells whether the playlist loops.
     *
     * @return {@code true} if it starts again after its last track.
     */
    public boolean isLooping()
    {
        return looping;
    }

    /**
     * Sets what happens when a track fails, after it is reported.
     *
     * @param abortOnFailure {@code true} to stop the playlist, {@code false} to skip the track (the default).
     */
    public void setAbortOnFailure(boolean abortOnFailure)
    {
        this.abortOnFailure = abortOnFailure;
    }

    /**
     * Tells whether the playlist stops at the first failure.
     *
     * @return {@code true} if a failing track stops it.
     */
    public boolean isAbortOnFailure()
    {
        return abortOnFailure;
    }

    /**
     * Plays the playlist from its first track; a playing playlist is stopped and starts again. A track that fails to
     * start is reported and skipped, or stops the playlist if it aborts on failure.
     *
     * @throws IllegalStateException If called on another thread than the owner of the loader of a track.
     */
    public void play()
    {
        stop();
        advance(0);
    }

    /**
     * Stops the playlist at once; no effect if it is stopped. The track prepared ahead by the playlist closes its
     * stream.
     */
    public void stop()
    {
        stops++;
        if (track != NO_TRACK)
        {
            final int current = voice;
            stopped();
            audio.stop(current);
        }
        unprepareAhead();
    }

    /**
     * Tells whether the playlist is playing.
     *
     * @return {@code true} from {@link #play()} until it stops after its last track, is stopped, or aborts.
     */
    public boolean isPlaying()
    {
        return track != NO_TRACK;
    }

    /**
     * Returns the track playing.
     *
     * @return The index of the track playing, {@link #NO_TRACK} when stopped.
     */
    public int getTrack()
    {
        return track;
    }

    /**
     * Returns the voice of the track playing.
     *
     * @return The voice, {@link AudioBackend#NO_VOICE} when stopped.
     */
    public int getVoice()
    {
        return voice;
    }

    /**
     * Returns the number of tracks.
     *
     * @return The track count, at least one.
     */
    public int size()
    {
        return tracks.length;
    }

    /**
     * Returns a track.
     *
     * @param index The index of the track, from {@code 0} to {@link #size()} excluded.
     * @return The long sound of the track.
     * @throws IndexOutOfBoundsException If the index is outside the tracks.
     */
    public LongSound getSound(int index)
    {
        return tracks[index];
    }

    /**
     * Starts the first track that does not fail from an index, wrapping after the last one when looping, then prepares
     * the track after it.
     *
     * @param from The index of the first track to try, up to {@link #size()} (the end).
     */
    private void advance(int from)
    {
        final int run = stops;
        int next = from;
        int failures = 0;
        while (track == NO_TRACK && failures < tracks.length && run == stops)
        {
            if (next == tracks.length)
            {
                if (!looping)
                {
                    break;
                }
                next = 0;
            }
            if (start(next))
            {
                prepareAhead();
            }
            else if (abortOnFailure)
            {
                break;
            }
            else
            {
                failures++;
                next++;
            }
        }
    }

    /**
     * Starts a track, reporting its failure; a track the playlist prepared and could not start closes its stream.
     *
     * @param index The index of the track.
     * @return {@code true} if it plays, {@code false} if it failed and was reported.
     */
    private boolean start(int index)
    {
        final LongSound sound = tracks[index];
        final boolean opened = sound == preparedAhead || !sound.isPrepared();
        if (sound == preparedAhead)
        {
            preparedAhead = null;
        }
        boolean started = false;
        try
        {
            final int played = audio.play(sound, trackListener);
            if (played == AudioBackend.NO_VOICE)
            {
                throw new IllegalStateException("No voice free for the track: " + sound.getKey());
            }
            track = index;
            voice = played;
            started = true;
        }
        catch (final ResourceException | AudioDecodingException | IllegalStateException exception)
        {
            if (opened && !sound.isPlaying())
            {
                sound.close();
            }
            report(index, exception);
        }
        return started;
    }

    /**
     * Prepares the track after the one playing, if it is another sound; a failure is left to its turn, where it is
     * reported.
     */
    private void prepareAhead()
    {
        final boolean last = track + 1 == tracks.length;
        final LongSound sound = tracks[last ? 0 : track + 1];
        final boolean idle = !sound.isPrepared() && !sound.isReleased() && !sound.isPlaying();
        if ((looping || !last) && sound != tracks[track] && idle)
        {
            try
            {
                sound.prepare();
                preparedAhead = sound;
            }
            catch (final ResourceException | AudioDecodingException | IllegalStateException exception)
            {
                // Reported at the turn of the track, when its preparation is tried again.
                preparedAhead = null;
            }
        }
    }

    /**
     * Closes the stream of the track prepared ahead, if it is not playing.
     */
    private void unprepareAhead()
    {
        final LongSound sound = preparedAhead;
        preparedAhead = null;
        if (sound != null && !sound.isPlaying())
        {
            sound.close();
        }
    }

    /**
     * Reports a failing track to the listener.
     *
     * @param index The index of the track.
     * @param exception The failure.
     */
    private void report(int index, RuntimeException exception)
    {
        final PlaylistListener current = listener;
        if (current != null)
        {
            current.onTrackFailed(this, index, exception);
        }
    }

    /**
     * Marks the playlist stopped.
     */
    private void stopped()
    {
        track = NO_TRACK;
        voice = AudioBackend.NO_VOICE;
    }

    /**
     * Listener of the end of the tracks, on the runtime thread.
     */
    private final class Tracks implements TrackListener
    {
        /**
         * Creates the listener.
         */
        Tracks()
        {
            super();
        }

        @Override
        public void onTrackEnded(LongSound sound, RuntimeException failure)
        {
            final int ended = track;
            final int run = stops;
            stopped();
            if (failure != null)
            {
                report(ended, failure);
            }
            if (run == stops && (failure == null || !abortOnFailure))
            {
                advance(ended + 1);
            }
            if (track == NO_TRACK)
            {
                unprepareAhead();
            }
        }

        @Override
        public void onTrackStopped(LongSound sound)
        {
            if (track != NO_TRACK)
            {
                stopped();
                unprepareAhead();
            }
        }
    }
}
