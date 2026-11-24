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
 * One origin of resources: filesystem, archive, memory, Android assets or an application stream (ADR-0005, ADR-0007,
 * ADR-0032, REQ-RES-001, REQ-RES-002).
 *
 * <p>
 * The application builds an ordered list of providers; the first provider that has a key wins. A provider is
 * read-only unless {@link #isWritable()} says otherwise.
 * </p>
 *
 * <p>
 * Threads: reading ({@link #has(ResourceKey)}, {@link #open(ResourceKey)}) is thread-safe, because loading workers
 * read through providers; writing ({@link #openWrite(ResourceKey)}) runs on the runtime thread only.
 * </p>
 *
 * @since 11.0.0
 */
public interface ResourceProvider
{
    /**
     * Tells whether this provider has the resource. Never throws for a missing key.
     *
     * @param key The resource key (never {@code null}).
     * @return {@code true} if {@link #open(ResourceKey)} would find the resource.
     */
    boolean has(ResourceKey key);

    /**
     * Opens the resource for reading.
     *
     * @param key The resource key (never {@code null}).
     * @return A new stream, owned and closed by the caller.
     * @throws ResourceException With reason {@link ResourceException.Reason#MISSING} if this provider does not have the
     *             key, or {@link ResourceException.Reason#INACCESSIBLE} if it cannot read it.
     */
    InputStream open(ResourceKey key);

    /**
     * Tells whether this provider accepts {@link #openWrite(ResourceKey)}.
     *
     * @return {@code true} if the provider is writable.
     */
    boolean isWritable();

    /**
     * Opens the resource for writing, creating or replacing it. Runtime thread only.
     *
     * @param key The resource key (never {@code null}).
     * @return A new stream, owned and closed by the caller.
     * @throws IllegalStateException If the provider is not writable.
     * @throws ResourceException With reason {@link ResourceException.Reason#INACCESSIBLE} if it cannot be written.
     */
    OutputStream openWrite(ResourceKey key);
}
