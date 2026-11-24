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
package com.b3dgs.lionengine.resources;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Provider held in memory that counts its streams, for the tests of {@link Resources} and of the modules that read
 * resources (ADR-0045, REQ-RES-003, REQ-RES-006).
 *
 * <p>
 * Follows the {@link ResourceProvider} contract. A key can be made unreadable to test the
 * {@link ResourceException.Reason#INACCESSIBLE} path, and {@link #getUnclosed()} tells whether the code under test
 * closed every stream it opened. Published in the {@code resources} test JAR. Thread-safe.
 * </p>
 */
public final class FakeResourceProvider implements ResourceProvider
{
    /** Guards the state. */
    private final Object lock = new Object();
    /** Content by key. */
    private final Map<ResourceKey, byte[]> contents = new HashMap<>();
    /** Keys present but unreadable. */
    private final Set<ResourceKey> failures = new HashSet<>();
    /** {@code true} if writing is accepted. */
    private final boolean writable;
    /** Number of read streams opened. */
    private int opened;
    /** Number of read streams opened and not closed yet. */
    private int unclosed;

    /**
     * Creates an empty provider.
     *
     * @param writable {@code true} to accept {@link #openWrite(ResourceKey)}.
     */
    public FakeResourceProvider(boolean writable)
    {
        this.writable = writable;
    }

    /**
     * Stores the content of a key, whether the provider is writable or not.
     *
     * @param key The resource key.
     * @param data The content, copied.
     */
    public void put(ResourceKey key, byte[] data)
    {
        synchronized (lock)
        {
            contents.put(key, data.clone());
        }
    }

    /**
     * Makes a key present but unreadable: {@link #has(ResourceKey)} returns {@code true} and
     * {@link #open(ResourceKey)} throws with reason {@link ResourceException.Reason#INACCESSIBLE}.
     *
     * @param key The resource key.
     */
    public void failOn(ResourceKey key)
    {
        synchronized (lock)
        {
            failures.add(key);
        }
    }

    /**
     * Returns the number of read streams opened.
     *
     * @return The number of read streams opened.
     */
    public int getOpened()
    {
        synchronized (lock)
        {
            return opened;
        }
    }

    /**
     * Returns the number of read streams opened and not closed yet.
     *
     * @return The number of read streams not closed.
     */
    public int getUnclosed()
    {
        synchronized (lock)
        {
            return unclosed;
        }
    }

    @Override
    public boolean has(ResourceKey key)
    {
        synchronized (lock)
        {
            return contents.containsKey(key) || failures.contains(key);
        }
    }

    @Override
    public InputStream open(ResourceKey key)
    {
        synchronized (lock)
        {
            if (failures.contains(key))
            {
                throw new ResourceException(key,
                                            ResourceException.Reason.INACCESSIBLE,
                                            new IOException("Unreadable: " + key.path()));
            }
            final byte[] data = contents.get(key);
            if (data == null)
            {
                throw new ResourceException(key, ResourceException.Reason.MISSING, null);
            }
            opened++;
            unclosed++;
            return new CountedInput(data);
        }
    }

    @Override
    public boolean isWritable()
    {
        return writable;
    }

    @Override
    public OutputStream openWrite(ResourceKey key)
    {
        if (!writable)
        {
            throw new IllegalStateException("Read-only provider");
        }
        return new CommittedOutput(key);
    }

    /**
     * Read stream counted until its first close.
     */
    private final class CountedInput extends ByteArrayInputStream
    {
        /** {@code true} once closed. */
        private boolean closed;

        /**
         * Creates the stream.
         *
         * @param data The content, never modified.
         */
        CountedInput(byte[] data)
        {
            super(data);
        }

        @Override
        public void close()
        {
            synchronized (lock)
            {
                if (!closed)
                {
                    closed = true;
                    unclosed--;
                }
            }
        }
    }

    /**
     * Write stream that stores its content at its first close.
     */
    private final class CommittedOutput extends ByteArrayOutputStream
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
        CommittedOutput(ResourceKey key)
        {
            super();
            this.key = key;
        }

        @Override
        public void close()
        {
            synchronized (lock)
            {
                if (!closed)
                {
                    closed = true;
                    contents.put(key, toByteArray());
                }
            }
        }
    }
}
