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

/**
 * Missing or inaccessible resource (ADR-0007, REQ-RES-006).
 *
 * <p>
 * The only exception of {@code resources}. Invalid content is not a resource error: it is the decoding exception of
 * the consuming module. Immutable; may be thrown on any thread (providers are read by loading workers). Not meant
 * to be serialized: no {@code serialVersionUID} is declared (Sonar S4926).
 * </p>
 *
 * @since 11.0.0
 */
@SuppressWarnings("serial")
public final class ResourceException extends RuntimeException
{
    /** Path of the resource key; kept as a string so that the exception stays serializable. */
    private final String path;
    /** Reason of the failure. */
    private final Reason reason;

    /**
     * Creates the exception.
     *
     * @param key The key of the resource (never {@code null}).
     * @param reason The reason of the failure (never {@code null}).
     * @param cause The underlying failure, for example an I/O error, {@code null} if none.
     */
    public ResourceException(ResourceKey key, Reason reason, Throwable cause)
    {
        super(reason + ": " + key, cause);

        path = key.path();
        this.reason = reason;
    }

    /**
     * Returns the key of the resource.
     *
     * @return The key of the resource (a new equal instance on each call; failures are not a hot path).
     */
    public ResourceKey getKey()
    {
        return new ResourceKey(path);
    }

    /**
     * Returns the reason of the failure.
     *
     * @return The reason of the failure.
     */
    public Reason getReason()
    {
        return reason;
    }

    /**
     * Reason of a resource failure.
     */
    public enum Reason
    {
        /** No provider has the key. */
        MISSING,
        /** A provider has the key but cannot read it, for example after an I/O or permission error. */
        INACCESSIBLE
    }
}
