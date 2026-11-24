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
package com.b3dgs.lionengine.resources.memory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.resources.ResourceException;
import com.b3dgs.lionengine.resources.ResourceKey;
import com.b3dgs.lionengine.resources.ResourceProvider;

/**
 * Writable provider that keeps its resources in memory, for temporary storage and generated content (ADR-0032,
 * REQ-RES-002, REQ-RES-006).
 *
 * <p>
 * Content is written with {@link #openWrite(ResourceKey)} and becomes readable when the stream is closed; until then,
 * readers get the previous content, if any. Stored content is never modified: a write replaces it, so streams already
 * opened keep reading the content they started with. Content lives as long as the provider.
 * </p>
 *
 * <p>
 * Threads: reading is thread-safe; writing runs on the runtime thread only, as for every provider.
 * </p>
 *
 * @since 11.0.0
 */
public final class MemoryProvider implements ResourceProvider
{
    /**
     * Checks that a key is not {@code null}.
     *
     * @param key The key.
     * @throws IllegalArgumentException If the key is {@code null}.
     */
    private static void checkKey(ResourceKey key)
    {
        if (key == null)
        {
            throw new IllegalArgumentException("Key must not be null");
        }
    }

    /** Guards the content. */
    private final Object lock = new Object();
    /** Content by key, never modified once stored. */
    private final Map<ResourceKey, byte[]> contents = new HashMap<>();

    /**
     * Creates an empty provider.
     */
    public MemoryProvider()
    {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException If {@code key} is {@code null}.
     */
    @Override
    public boolean has(ResourceKey key)
    {
        checkKey(key);
        synchronized (lock)
        {
            return contents.containsKey(key);
        }
    }

    /**
     * {@inheritDoc}
     *
     * @throws IllegalArgumentException If {@code key} is {@code null}.
     */
    @Override
    public InputStream open(ResourceKey key)
    {
        checkKey(key);
        final byte[] data;
        synchronized (lock)
        {
            data = contents.get(key);
        }
        if (data == null)
        {
            throw new ResourceException(key, ResourceException.Reason.MISSING, null);
        }
        return new ByteArrayInputStream(data);
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code true}: this provider stores what is written.
     */
    @Override
    public boolean isWritable()
    {
        return true;
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The content is stored when the stream is first closed; bytes written after that are ignored. Never throws
     * {@link ResourceException}: memory storage cannot fail to write.
     * </p>
     *
     * @throws IllegalArgumentException If {@code key} is {@code null}.
     */
    @Override
    public OutputStream openWrite(ResourceKey key)
    {
        checkKey(key);
        return new StoredOutput(key);
    }

    /**
     * Write stream that stores its content at its first close.
     */
    private final class StoredOutput extends ByteArrayOutputStream
    {
        /** Key written. */
        private final ResourceKey key;
        /** {@code true} once closed. */
        private boolean closed;

        /**
         * Creates the stream.
         *
         * @param key The key written.
         */
        StoredOutput(ResourceKey key)
        {
            super();
            this.key = key;
        }

        @Override
        public void close()
        {
            if (!closed)
            {
                closed = true;
                final byte[] data = toByteArray();
                synchronized (lock)
                {
                    contents.put(key, data);
                }
            }
        }
    }
}
