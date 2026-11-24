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

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Access to the game data by resource key, through an ordered list of providers (ADR-0005, ADR-0007, ADR-0032,
 * REQ-RES-001, REQ-RES-003, REQ-RES-006).
 *
 * <p>
 * The application builds it once with its providers, in lookup order: a key is looked up in each provider in turn and
 * the first one that has it wins, so a provider placed first (a mod folder, downloaded content) overrides the ones
 * after it (the base data). Content is never merged. When the provider that has a key cannot read it, the error is
 * reported: the lookup does not fall back to the next provider.
 * </p>
 *
 * <p>
 * Every opened stream belongs to the caller, who closes it. Threads: {@link #exists(ResourceKey)} and
 * {@link #open(ResourceKey)} are thread-safe, because loading workers read through them;
 * {@link #openWrite(ResourceKey)} runs on the runtime thread only.
 * </p>
 *
 * @since 11.0.0
 */
public final class Resources
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

    /** Providers, in lookup order. */
    private final ResourceProvider[] providers;

    /**
     * Creates the access to the game data.
     *
     * @param providers The providers, in lookup order: the first one that has a key wins. The array is copied.
     * @throws IllegalArgumentException If {@code providers} or one of them is {@code null}.
     */
    public Resources(ResourceProvider... providers)
    {
        if (providers == null)
        {
            throw new IllegalArgumentException("Providers must not be null");
        }
        this.providers = providers.clone();
        for (final ResourceProvider provider : this.providers)
        {
            if (provider == null)
            {
                throw new IllegalArgumentException("Provider must not be null");
            }
        }
    }

    /**
     * Tells whether a provider has the resource. Never throws for a missing key.
     *
     * @param key The resource key.
     * @return {@code true} if {@link #open(ResourceKey)} would find the resource.
     * @throws IllegalArgumentException If {@code key} is {@code null}.
     */
    public boolean exists(ResourceKey key)
    {
        checkKey(key);
        for (final ResourceProvider provider : providers)
        {
            if (provider.has(key))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Opens a resource for reading from the first provider that has it.
     *
     * @param key The resource key.
     * @return A new stream, owned and closed by the caller.
     * @throws IllegalArgumentException If {@code key} is {@code null}.
     * @throws ResourceException With reason {@link ResourceException.Reason#MISSING} if no provider has the key, or
     *             {@link ResourceException.Reason#INACCESSIBLE} if the provider that has it cannot read it.
     */
    public InputStream open(ResourceKey key)
    {
        checkKey(key);
        for (final ResourceProvider provider : providers)
        {
            if (provider.has(key))
            {
                return provider.open(key);
            }
        }
        throw new ResourceException(key, ResourceException.Reason.MISSING, null);
    }

    /**
     * Tells whether a provider accepts writing.
     *
     * @return {@code true} if at least one provider is writable.
     */
    public boolean isWritable()
    {
        for (final ResourceProvider provider : providers)
        {
            if (provider.isWritable())
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Opens a resource for writing in the first writable provider, creating or replacing it. Runtime thread only.
     *
     * <p>
     * The written content is visible to readers of that provider once the stream is closed. A provider placed before
     * the writable one and having the same key still wins for reading.
     * </p>
     *
     * @param key The resource key.
     * @return A new stream, owned and closed by the caller.
     * @throws IllegalArgumentException If {@code key} is {@code null}.
     * @throws IllegalStateException If no provider is writable.
     * @throws ResourceException With reason {@link ResourceException.Reason#INACCESSIBLE} if the writable provider
     *             cannot write the resource.
     */
    public OutputStream openWrite(ResourceKey key)
    {
        checkKey(key);
        for (final ResourceProvider provider : providers)
        {
            if (provider.isWritable())
            {
                return provider.openWrite(key);
            }
        }
        throw new IllegalStateException("No writable provider for: " + key.path());
    }
}
