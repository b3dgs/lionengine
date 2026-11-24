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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Provider of the streams registered by the application, such as downloaded content (ADR-0032, REQ-RES-002).
 *
 * <p>
 * The application registers a {@link StreamSupplier} under a key, and places this provider in the list given to
 * {@link Resources} where its content must win: first to override the game data, last to complete it. Read-only.
 * Thread-safe: registration and reading may happen on any thread.
 * </p>
 *
 * @since 11.0.0
 */
public final class StreamProvider implements ResourceProvider
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

    /** Guards the suppliers. */
    private final Object lock = new Object();
    /** Suppliers by key. */
    private final Map<ResourceKey, StreamSupplier> suppliers = new HashMap<>();

    /**
     * Creates a provider without content.
     */
    public StreamProvider()
    {
        super();
    }

    /**
     * Registers the content of a key, replacing the supplier registered before under that key, if any.
     *
     * @param key The resource key.
     * @param supplier The supplier of the content, thread-safe.
     * @throws IllegalArgumentException If {@code key} or {@code supplier} is {@code null}.
     */
    public void register(ResourceKey key, StreamSupplier supplier)
    {
        checkKey(key);
        if (supplier == null)
        {
            throw new IllegalArgumentException("Supplier must not be null");
        }
        synchronized (lock)
        {
            suppliers.put(key, supplier);
        }
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
            return suppliers.containsKey(key);
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * Calls the supplier registered under the key; an {@link IOException} or a {@code null} stream is reported as
     * {@link ResourceException.Reason#INACCESSIBLE}.
     * </p>
     *
     * @throws IllegalArgumentException If {@code key} is {@code null}.
     */
    @Override
    public InputStream open(ResourceKey key)
    {
        checkKey(key);
        final StreamSupplier supplier;
        synchronized (lock)
        {
            supplier = suppliers.get(key);
        }
        if (supplier == null)
        {
            throw new ResourceException(key, ResourceException.Reason.MISSING, null);
        }
        final InputStream stream;
        try
        {
            stream = supplier.open();
        }
        catch (final IOException exception)
        {
            throw new ResourceException(key, ResourceException.Reason.INACCESSIBLE, exception);
        }
        if (stream == null)
        {
            throw new ResourceException(key, ResourceException.Reason.INACCESSIBLE, null);
        }
        return stream;
    }

    /**
     * {@inheritDoc}
     *
     * @return {@code false}: the application registers suppliers instead.
     */
    @Override
    public boolean isWritable()
    {
        return false;
    }

    /**
     * Refuses writing: the provider is read-only.
     *
     * @param key The resource key.
     * @return Never returns.
     * @throws IllegalStateException Always.
     */
    @Override
    public OutputStream openWrite(ResourceKey key)
    {
        throw new IllegalStateException("Read-only provider of application streams");
    }
}
