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

import java.io.IOException;

import com.b3dgs.lionengine.resources.ResourceKey;

/**
 * Handle of a long sound, music or ambient, created by a {@link LongSoundLoader}: streamed by default, or fully
 * decoded on request, with its gain, fades, mixer group and looping (ADR-0004, ADR-0005, ADR-0007, ADR-0018, ADR-0027,
 * ADR-0033, ADR-0055, ADR-0068, ADR-0069, ADR-0071, REQ-AUDIO-006, REQ-AUDIO-007, REQ-AUDIO-011, REQ-AUDIO-012,
 * REQ-RES-004).
 *
 * <p>
 * Lifecycle: a new handle reads nothing. {@link #prepare()} makes it ready for direct use: a streamed sound opens its
 * resource, reads its header and decodes its buffer ahead; a fully decoded sound decodes all its samples. Preparing
 * raises the errors of the resource, so a scene prepares its music on entry and the play starts at once.
 * {@link Audio#play(LongSound)} plays it, preparing it first if needed, and {@link Audio#stop(int)} stops it; a long
 * sound plays on one voice at a time. A streamed sound reads its resource while it plays and closes it once it ends
 * or is stopped, so it is prepared again for its next play; a fully decoded sound keeps its samples until its release.
 * {@link #release()} stops it if it plays and frees what it holds; releasing again has no effect (ADR-0007).
 * </p>
 *
 * <p>
 * Memory: a streamed sound holds at most {@link #getChunks()} × {@link #getChunkFrames()} decoded frames, whatever its
 * length, so it is not limited to the 10 s of the short sounds. A fully decoded sound holds all its frames.
 * </p>
 *
 * <p>
 * Controls (ADR-0069): the gain of the handle ({@link #setGain(double)}, {@link Audio#MAX_GAIN} by default) multiplies
 * the gain of its mixer group and the master gain, and a change reaches its voice at once; {@link #fade(double,
 * double)} ramps it to a target gain over a duration instead (ADR-0071). The group is {@link MixerGroup#MUSIC} by
 * default ({@link #setGroup(String)}), read at the play. A sound that loops ({@link #setLooping(boolean)}, off by
 * default) is played looping from its next play and never ends by itself: a fully decoded sound loops on its voice, a
 * streamed sound opens its resource again when its source ends and goes on decoding in the same chunk, so the loop has
 * no gap and its memory stays the same buffer. A streamed loop opens a new resource stream for each pass, on the
 * backend's thread.
 * </p>
 *
 * <p>
 * Threads: runtime thread only (ADR-0018); {@link #prepare()} and {@link #release()} throw an
 * {@link IllegalStateException} on another thread than the owner of the loader (ADR-0055). While it plays, the
 * backend's thread pulls its buffer, which reads and decodes the resource and touches nothing else; a failure of the
 * stream, or to close it at the end, ends the voice and is reported on the runtime thread, to the {@link Playlist}
 * that plays it.
 * </p>
 *
 * @since 11.0.0
 */
public final class LongSound
{
    /** Chunk count and size of a fully decoded sound. */
    static final int NOT_STREAMED = 0;

    /** The loader of the sound. */
    private final LongSoundLoader loader;
    /** The resource key. */
    private final ResourceKey key;
    /** The decoder of the key. */
    private final AudioStreamDecoder decoder;
    /** The number of chunks of the stream buffer, {@link #NOT_STREAMED} if fully decoded. */
    private final int chunks;
    /** The frames of a chunk, {@link #NOT_STREAMED} if fully decoded. */
    private final int chunkFrames;
    /** The samples of a fully decoded sound once prepared, {@code null} otherwise. */
    private PcmBuffer buffer;
    /** The buffer of a streamed sound once prepared, {@code null} otherwise. */
    private StreamBuffer stream;
    /** The audio system playing it, {@code null} when not playing. */
    private Audio audio;
    /** The voice playing it, {@link AudioBackend#NO_VOICE} when not playing. */
    private int voice = AudioBackend.NO_VOICE;
    /** The listener of the playlist playing it, {@code null} if none. */
    private TrackListener listener;
    /** {@code true} once released. */
    private boolean released;
    /** The gain of the sound. */
    private double gain = Audio.MAX_GAIN;
    /** The id of the mixer group of the sound. */
    private String group = MixerGroup.MUSIC;
    /** {@code true} to play the sound looping. */
    private boolean looping;

    /**
     * Creates a handle, not prepared.
     *
     * @param loader The loader.
     * @param key The resource key.
     * @param decoder The decoder of the key.
     * @param chunks The number of chunks of the stream buffer, {@link #NOT_STREAMED} if fully decoded.
     * @param chunkFrames The frames of a chunk, {@link #NOT_STREAMED} if fully decoded.
     */
    LongSound(LongSoundLoader loader, ResourceKey key, AudioStreamDecoder decoder, int chunks, int chunkFrames)
    {
        super();

        this.loader = loader;
        this.key = key;
        this.decoder = decoder;
        this.chunks = chunks;
        this.chunkFrames = chunkFrames;
    }

    /**
     * Returns the resource key.
     *
     * @return The key of the sound, also once released.
     */
    public ResourceKey getKey()
    {
        return key;
    }

    /**
     * Tells whether the sound is streamed.
     *
     * @return {@code true} if streamed through a bounded buffer, {@code false} if fully decoded.
     */
    public boolean isStreamed()
    {
        return chunks != NOT_STREAMED;
    }

    /**
     * Returns the number of chunks of the stream buffer.
     *
     * @return The chunk count of a streamed sound, {@code 0} for a fully decoded one.
     */
    public int getChunks()
    {
        return chunks;
    }

    /**
     * Returns the frames of a chunk of the stream buffer.
     *
     * @return The chunk size of a streamed sound, {@code 0} for a fully decoded one.
     */
    public int getChunkFrames()
    {
        return chunkFrames;
    }

    /**
     * Tells whether the sound is prepared: its next play starts at once.
     *
     * @return {@code true} from {@link #prepare()}, or the start of a play, until a streamed sound ends or is
     *         stopped, or until the release.
     */
    public boolean isPrepared()
    {
        return buffer != null || stream != null;
    }

    /**
     * Tells whether the sound is playing.
     *
     * @return {@code true} from its play until it is stopped, released or its end is delivered.
     */
    public boolean isPlaying()
    {
        return voice != AudioBackend.NO_VOICE;
    }

    /**
     * Tells whether the handle is released.
     *
     * @return {@code true} once {@link #release()} was called.
     */
    public boolean isReleased()
    {
        return released;
    }

    /**
     * Sets the gain of the sound; its voice, if it plays, gets its new effective gain at once; if it is fading, it
     * keeps fading, toward its new effective gain, in its remaining time.
     *
     * @param gain The gain, from {@link Audio#MIN_GAIN} to {@link Audio#MAX_GAIN}.
     * @throws IllegalArgumentException If the gain is NaN or outside its range; nothing changes then.
     */
    public void setGain(double gain)
    {
        Audio.checkGain(gain);
        this.gain = gain;
        if (audio != null)
        {
            audio.updateGain(this, voice);
        }
    }

    /**
     * Returns the gain of the sound.
     *
     * @return The gain, {@link Audio#MAX_GAIN} by default.
     */
    public double getGain()
    {
        return gain;
    }

    /**
     * Fades the sound: its gain becomes the target at once, so that {@link #getGain()} returns it, and its voice, if it
     * plays, ramps linearly from the gain it has to its new effective gain over the duration, in the backend. Its voice
     * reports the end of the ramp to the {@link FadeListener} of the {@link Audio} through the inbox; a sound that does
     * not play only takes the gain and reports nothing. A fade to {@code 0} stops nothing.
     *
     * @param gain The target gain, from {@link Audio#MIN_GAIN} to {@link Audio#MAX_GAIN}.
     * @param seconds The duration, in seconds, finite and not negative; {@code 0} reaches the target at the next audio
     *            buffer.
     * @throws IllegalArgumentException If the gain or the duration is invalid; nothing changes then.
     */
    public void fade(double gain, double seconds)
    {
        Audio.checkGain(gain);
        Audio.checkSeconds(seconds);
        this.gain = gain;
        if (audio != null)
        {
            audio.fade(this, voice, seconds);
        }
    }

    /**
     * Sets the mixer group of the sound, from its next play; a playing voice keeps the group it started with.
     *
     * @param group The id of the group, a group of the {@link Audio} that plays the sound (never {@code null} nor
     *            empty); an id unknown to that audio system is refused at the play.
     * @throws IllegalArgumentException If the id is {@code null} or empty.
     */
    public void setGroup(String group)
    {
        this.group = MixerGroup.checkId(group);
    }

    /**
     * Returns the mixer group of the sound.
     *
     * @return The id of the group, {@link MixerGroup#MUSIC} by default.
     */
    public String getGroup()
    {
        return group;
    }

    /**
     * Sets whether the sound is played looping, from its next play; a playing voice keeps its looping.
     *
     * @param looping {@code true} to loop: its voice never ends by itself; {@code false} to play it once (the
     *            default).
     */
    public void setLooping(boolean looping)
    {
        this.looping = looping;
    }

    /**
     * Tells whether the sound is played looping.
     *
     * @return {@code true} if its next plays loop.
     */
    public boolean isLooping()
    {
        return looping;
    }

    /**
     * Returns the format of the samples.
     *
     * @return The format.
     * @throws IllegalStateException If the sound is not prepared.
     */
    public PcmFormat getFormat()
    {
        final PcmFormat format;
        if (buffer != null)
        {
            format = buffer.getFormat();
        }
        else if (stream != null)
        {
            format = stream.getFormat();
        }
        else
        {
            throw new IllegalStateException("Long sound not prepared: " + key);
        }
        return format;
    }

    /**
     * Prepares the sound for direct use; no effect if it is prepared. A streamed sound opens its resource and decodes
     * its buffer ahead, a fully decoded sound decodes all its samples.
     *
     * @throws IllegalStateException If the handle is released, or if called on another thread than the owner of the
     *             loader.
     * @throws com.b3dgs.lionengine.resources.ResourceException If the resource is missing ({@code MISSING}) or cannot
     *             be read ({@code INACCESSIBLE}).
     * @throws AudioDecodingException If the content is not a valid sound, or a fully decoded sound is above the frame
     *             limit of its decoder; the message starts with the key.
     */
    public void prepare()
    {
        loader.checkThread();
        if (released)
        {
            throw new IllegalStateException("Long sound released: " + key);
        }
        if (isPrepared())
        {
            return;
        }
        if (isStreamed())
        {
            final LongSoundLoader.Opened opened = loader.open(key, decoder);
            final StreamBuffer prepared = new StreamBuffer(opened.input(),
                                                           opened.source(),
                                                           chunks,
                                                           chunkFrames,
                                                           () -> loader.reopen(key, decoder));
            prepared.fill();
            final Exception failure = prepared.getFailure();
            if (failure != null)
            {
                throw LongSoundLoader.closing(opened.input(), LongSoundLoader.report(key, failure));
            }
            stream = prepared;
        }
        else
        {
            buffer = loader.read(key, decoder);
        }
    }

    /**
     * Releases the sound; no effect if already released. A playing sound is stopped first, then the stream of a
     * streamed sound is closed and the samples of a fully decoded one are freed.
     *
     * @throws IllegalStateException If called on another thread than the owner of the loader; nothing changes then.
     */
    public void release()
    {
        loader.checkThread();
        if (!released)
        {
            released = true;
            if (audio != null)
            {
                audio.stop(voice);
            }
            close();
            buffer = null;
        }
    }

    /**
     * Prepares the sound and starts it on a voice of a backend.
     *
     * @param backend The backend.
     * @param effectiveGain The gain of the voice.
     * @param loop {@code true} to loop the voice.
     * @return The voice, or {@link AudioBackend#NO_VOICE}; the sound stays prepared then.
     * @throws IllegalStateException If the sound is released, or for the errors of {@link #prepare()}.
     */
    int start(AudioBackend backend, double effectiveGain, boolean loop)
    {
        prepare();
        final int started;
        if (stream != null)
        {
            stream.setLooping(loop);
            started = backend.play(stream, effectiveGain);
        }
        else
        {
            started = backend.play(buffer, effectiveGain, loop);
        }
        return started;
    }

    /**
     * Records the voice that plays the sound.
     *
     * @param owner The audio system playing it.
     * @param started The voice.
     * @param trackListener The listener of the playlist playing it, {@code null} if none.
     */
    void started(Audio owner, int started, TrackListener trackListener)
    {
        audio = owner;
        voice = started;
        listener = trackListener;
    }

    /**
     * Notifies that the voice was stopped: a streamed sound closes its stream.
     */
    void stopped()
    {
        // A failure to close is dropped: the voice was stopped, so nothing reads the stream any more.
        finish();
        final TrackListener current = takeListener();
        if (current != null)
        {
            current.onTrackStopped(this);
        }
    }

    /**
     * Notifies that the voice ended by itself, at the inbox delivery of its end: a streamed sound closes its stream
     * and reports the failure of its stream, if any.
     */
    void ended()
    {
        Exception failure = null;
        if (stream != null)
        {
            failure = stream.getFailure();
        }
        final IOException closeFailure = finish();
        if (failure == null)
        {
            failure = closeFailure;
        }
        final TrackListener current = takeListener();
        if (current != null)
        {
            final RuntimeException reported;
            if (failure != null)
            {
                reported = LongSoundLoader.report(key, failure);
            }
            else
            {
                reported = null;
            }
            current.onTrackEnded(this, reported);
        }
    }

    /**
     * Closes the stream of a streamed sound, which is no longer prepared; a fully decoded sound keeps its samples.
     *
     * @return The failure to close the stream, {@code null} if it closed or there was none. The callers that stop a
     *         sound or release it drop it: nothing reads the stream any more.
     */
    IOException close()
    {
        IOException failure = null;
        if (stream != null)
        {
            failure = stream.close();
            stream = null;
        }
        return failure;
    }

    /**
     * Returns the buffer of a streamed sound, for the tests.
     *
     * @return The buffer, {@code null} if not prepared or fully decoded.
     */
    StreamBuffer getStream()
    {
        return stream;
    }

    /**
     * Forgets the voice once it no longer plays, and closes the stream.
     *
     * @return The failure to close the stream, {@code null} if none.
     */
    private IOException finish()
    {
        audio = null;
        voice = AudioBackend.NO_VOICE;
        return close();
    }

    /**
     * Takes the listener of the playlist that played the sound.
     *
     * @return The listener, {@code null} if none.
     */
    private TrackListener takeListener()
    {
        final TrackListener current = listener;
        listener = null;
        return current;
    }
}
