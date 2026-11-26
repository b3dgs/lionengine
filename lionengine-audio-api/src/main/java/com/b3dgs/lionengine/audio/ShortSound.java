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
 * and the release of that load (ADR-0004, ADR-0005, ADR-0007, ADR-0027, ADR-0066, REQ-AUDIO-003, REQ-AUDIO-004,
 * REQ-RES-004).
 *
 * <p>
 * Every load returns a new handle, and every handle of one key shares the same decoded samples: the sound is decoded
 * once. A handle from {@link ShortSoundCache#load(ResourceKey)} is loaded at once; a handle from
 * {@link ShortSoundCache#loadLazily(ResourceKey)} is decoded at its first play, which then costs the decoding. The
 * code that loaded a handle releases it, normally the scene on exit; the sound leaves the cache with its last handle.
 * Releasing is idempotent. Voices already playing the sound play to their end: stop them first to stop the sound.
 * </p>
 *
 * <p>
 * Threads: runtime thread only (ADR-0018); {@link #release()} throws an {@link IllegalStateException} on another
 * thread than the owner of the cache (ADR-0055).
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
     * Releases this load; no effect if already released. The sound leaves the cache with its last handle.
     *
     * @throws IllegalStateException If called on another thread than the owner of the cache; nothing changes then.
     */
    public void release()
    {
        cache.checkThread();
        if (!released)
        {
            released = true;
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
