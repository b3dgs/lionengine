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

/**
 * Supplies the content of a resource registered by the application, such as downloaded content (ADR-0032,
 * REQ-RES-002).
 *
 * <p>
 * Registered under a key in a {@link StreamProvider}. Thread: may be called from loading workers, so it must be
 * thread-safe.
 * </p>
 *
 * @since 11.0.0
 */
@FunctionalInterface
public interface StreamSupplier
{
    /**
     * Opens a new stream on the content; called once per {@link StreamProvider#open(ResourceKey)}.
     *
     * @return A new stream, owned and closed by the caller (never {@code null}).
     * @throws IOException If the content cannot be read; reported as
     *             {@link ResourceException.Reason#INACCESSIBLE}.
     */
    InputStream open() throws IOException;
}
