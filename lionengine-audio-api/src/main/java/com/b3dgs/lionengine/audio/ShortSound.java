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

import com.b3dgs.lionengine.resources.ResourceKey;

/**
 * One load of a short sound held by a {@link ShortSoundCache}: the sound to give to {@link Audio#play(ShortSound)},
 * its gain and looping, and the release of that load (ADR-0004, ADR-0005, ADR-0007, ADR-0027, ADR-0066, ADR-0069,
 * REQ-AUDIO-003, REQ-AUDIO-004, REQ-AUDIO-011, REQ-RES-004).
 *
 * <p>
 * Every load returns a new handle, and every handle of one key shares the same decoded samples: the sound is decoded
 * once. A handle from {@link ShortSoundCache#load(ResourceKey)} is loaded at once; a handle from
 * {@link ShortSoundCache#loadLazily(ResourceKey)} is decoded at its first play, which then costs the decoding. The
 * code that loaded a handle releases it, normally the scene on exit; the sound leaves the cache with its last handle.
 * Releasing is idempotent. Voices already playing the sound play to their end: stop them first to stop the sound.
 * Its looping voices, which would never end, are stopped by the release.
 * </p>
 *
 * <p>
 * Controls (ADR-0069): the gain of the handle ({@link #setGain(double)}, {@link Audio#MAX_GAIN} by default) multiplies
 * the master gain for every voice playing it, and a change reaches them at once. A handle that loops
 * ({@link #setLooping(boolean)}, off by default) is played looping from its next play: its voices never end by
 * themselves and play until they are stopped or stolen. The gain and the looping belong to this handle, not to the
 * other handles of the same key.
 * </p>
 *
 * <p>
 * Threads: runtime thread only (ADR-0018); {@link #release()} throws an {@link IllegalStateException} on another
 * thread than the owner of the cache (ADR-0055). Changing the gain or the looping allocates nothing.
 * </p>
 *
 * @since 11.0.0
 */
public final class ShortSound
{
    /** Error message of a sound not decoded. */
    private static final String ERROR_NOT_LOADED = "Short sound not loaded: ";

    /** The cache of the sound. */
    private final ShortSoundCache cache;
    /** The resource key of the sound. */
    private final ResourceKey key;
    /** The entry of the cache, {@code null} before the first play of a lazy handle and once released. */
    private ShortSoundEntry entry;
    /** {@code true} once released. */
    private boolean released;
    /** The gain of the sound. */
    private double gain = Audio.MAX_GAIN;
    /** {@code true} to play the sound looping. */
    private boolean looping;
    /** The audio system that last played the sound, {@code null} if never played. */
    private Audio audio;

    /**
     * Creates a handle; a handle on an entry adds a reference to it.
     *
     * @param cache The cache of the sound.
     * @param key The resource key of the sound.
     * @param entry The entry, {@code null} for a lazy handle.
     */
    ShortSound(ShortSoundCache cache, ResourceKey key, ShortSoundEntry entry)
    {
        super();

        this.cache = cache;
        this.key = key;
        attach(entry);
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
     * Tells whether the sound is decoded.
     *
     * @return {@code true} from its load, or from the first play of a lazy handle, until its release.
     */
    public boolean isLoaded()
    {
        return entry != null;
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
     * Returns the format of the samples.
     *
     * @return The format.
     * @throws IllegalStateException If the sound is not loaded.
     */
    public PcmFormat getFormat()
    {
        return loaded().getBuffer().getFormat();
    }

    /**
     * Returns the duration.
     *
     * @return The duration in seconds, at most {@link ShortSoundCache#MAX_SECONDS}.
     * @throws IllegalStateException If the sound is not loaded.
     */
    public double getDuration()
    {
        return loaded().getBuffer().getDuration();
    }

    /**
     * Sets the gain of the sound; the voices playing it get their new effective gain at once.
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
            audio.updateGain(this);
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
     * Sets whether the sound is played looping, from its next play; the voices already playing keep their looping.
     *
     * @param looping {@code true} to loop: its voices never end by themselves; {@code false} to play it once (the
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
     * Releases this load; no effect if already released. The sound leaves the cache with its last handle. Its looping
     * voices are stopped; its other voices play to their end.
     *
     * @throws IllegalStateException If called on another thread than the owner of the cache; nothing changes then.
     */
    public void release()
    {
        cache.checkThread();
        if (!released)
        {
            released = true;
            if (audio != null)
            {
                audio.stopLooping(this);
            }
            final ShortSoundEntry current = entry;
            entry = null;
            if (current != null)
            {
                current.release();
            }
        }
    }

    /**
     * Returns the samples to play, decoding them first for a lazy handle (ADR-0027).
     *
     * @return The samples.
     * @throws IllegalStateException If the handle is released, if the sound would exceed the limit of the cache, or if
     *             called on another thread than the owner of the cache.
     * @throws com.b3dgs.lionengine.resources.ResourceException If the resource of a lazy handle is missing or
     *             unreadable.
     * @throws AudioDecodingException If the content of a lazy handle is invalid or too long.
     */
    PcmBuffer getBuffer()
    {
        if (released)
        {
            throw new IllegalStateException("Short sound released: " + key);
        }
        if (entry == null)
        {
            attach(cache.resolve(key));
        }
        return entry.getBuffer();
    }

    /**
     * Records the audio system that played the sound, which gets its gain changes.
     *
     * @param player The audio system.
     */
    void played(Audio player)
    {
        audio = player;
    }

    /**
     * Attaches the handle to an entry and adds a reference to it.
     *
     * @param target The entry, {@code null} for none.
     */
    private void attach(ShortSoundEntry target)
    {
        entry = target;
        if (target != null)
        {
            target.acquire();
        }
    }

    /**
     * Returns the entry of a loaded handle.
     *
     * @return The entry.
     * @throws IllegalStateException If the sound is not loaded.
     */
    private ShortSoundEntry loaded()
    {
        if (entry == null)
        {
            throw new IllegalStateException(ERROR_NOT_LOADED + key);
        }
        return entry;
    }
}
