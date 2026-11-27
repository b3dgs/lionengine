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
 * Short sound held by a {@link ShortSoundCache}: its immutable samples and its reference count, shared by every
 * {@link ShortSound} handle loaded on it (ADR-0019, ADR-0066).
 *
 * <p>
 * The samples are never modified; a backend may read them from its own thread. A development reload replaces them
 * with a new buffer: voices already playing keep the previous one to their end. Runtime thread only, checked by the
 * cache.
 * </p>
 */
final class ShortSoundEntry
{
    /** The cache holding the entry. */
    private final ShortSoundCache cache;
    /** The key of the entry in the cache. */
    private final ResourceKey key;
    /** The samples. */
    private PcmBuffer buffer;
    /** Number of handles not released. */
    private int references;

    /**
     * Creates the entry.
     *
     * @param cache The cache holding the entry.
     * @param key The key of the entry.
     * @param buffer The samples, owned by the entry from now on.
     */
    ShortSoundEntry(ShortSoundCache cache, ResourceKey key, PcmBuffer buffer)
    {
        super();

        this.cache = cache;
        this.key = key;
        this.buffer = buffer;
    }

    /**
     * Adds one reference, for a new handle.
     */
    void acquire()
    {
        references++;
    }

    /**
     * Releases one reference; the last one removes the entry from the cache.
     */
    void release()
    {
        references--;
        if (references == 0)
        {
            cache.remove(this);
        }
    }

    /**
     * Replaces the samples, for a development reload.
     *
     * @param newBuffer The new samples, owned by the entry from now on.
     */
    void replace(PcmBuffer newBuffer)
    {
        buffer = newBuffer;
    }

    /**
     * Returns the key.
     *
     * @return The resource key of the sound.
     */
    ResourceKey getKey()
    {
        return key;
    }

    /**
     * Returns the samples.
     *
     * @return The current samples.
     */
    PcmBuffer getBuffer()
    {
        return buffer;
    }

    /**
     * Returns the bytes held by the samples.
     *
     * @return The bytes, 2 per sample.
     */
    long getBytes()
    {
        return ShortSoundCache.bytes(buffer);
    }
}
