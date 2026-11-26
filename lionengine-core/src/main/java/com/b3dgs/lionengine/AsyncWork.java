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
package com.b3dgs.lionengine;

/**
 * Work of an asynchronous load, run on a loading worker: reads and decodes, and returns what the runtime thread
 * completes into the loaded value (ADR-0018, ADR-0019, ADR-0040, REQ-RT-004).
 *
 * <p>
 * It touches no engine state, no cache and no GPU object: only thread-safe reads (resource providers, decoders) and
 * objects it creates. A failure is thrown: the request becomes {@link AsyncState#FAILED} with that exception, the
 * resource exception or the decoding exception of the module (ADR-0007).
 * </p>
 *
 * @param <R> The type of the result handed to the runtime thread, for example the decoded pixels.
 * @since 11.0.0
 */
@FunctionalInterface
public interface AsyncWork<R>
{
    /**
     * Executes the work on a worker thread, or on the submitting thread when the pool has no worker.
     *
     * @return The result for the completion (never {@code null}).
     */
    R execute();
}
