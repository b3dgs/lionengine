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
 * Completion of an asynchronous load, run on the runtime thread when the inbox delivers the result of its work: turns
 * the result into the loaded value, for example by inserting decoded pixels into a cache (ADR-0018, ADR-0019,
 * ADR-0040, REQ-RT-004).
 *
 * <p>
 * It runs only for a request still {@link AsyncState#PENDING}: a result that arrives after a cancellation is
 * discarded without completion, so nothing it would acquire is acquired. A failure is thrown: the request becomes
 * {@link AsyncState#FAILED} with that exception.
 * </p>
 *
 * @param <R> The type of the result of the work.
 * @param <T> The type of the loaded value.
 * @since 11.0.0
 */
@FunctionalInterface
public interface AsyncCompletion<R, T>
{
    /**
     * Completes the load on the runtime thread.
     *
     * @param result The result of the work (never {@code null}).
     * @return The loaded value, owned by the code that made the request (never {@code null}).
     */
    T complete(R result);
}
