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
 * Result of an asynchronous load, completed on the runtime thread (ADR-0019, ADR-0031, ADR-0040, REQ-RT-004).
 *
 * <p>
 * Loading is synchronous by default; an asynchronous request returns a handle whose work runs on a
 * {@link WorkerPool}. The worker only reads and decodes; the handle changes state through an {@link InboxEvent}, so
 * {@link #getState()} and {@link #getProgress()} change only at inbox delivery, on the runtime thread.
 * </p>
 *
 * <p>
 * Thread: runtime thread only. Not thread-safe.
 * </p>
 *
 * @param <T> The loaded value type.
 * @since 11.0.0
 */
public interface AsyncHandle<T>
{
    /**
     * Returns the current state.
     *
     * @return The current state (never {@code null}).
     */
    AsyncState getState();

    /**
     * Returns the progress of the work.
     *
     * @return The progress in {@code [0, 1]}: bytes read over total when the size is known, otherwise {@code 0} until
     *         the work completes, then {@code 1}.
     */
    double getProgress();

    /**
     * Returns the loaded value.
     *
     * @return The loaded value (never {@code null}).
     * @throws IllegalStateException If the state is not {@link AsyncState#READY}.
     */
    T get();

    /**
     * Returns the failure of the work.
     *
     * @return The resource exception or the decoding exception of the consuming module when the state is
     *         {@link AsyncState#FAILED}, {@code null} otherwise.
     */
    RuntimeException getFailure();

    /**
     * Cancels the handle, best effort: a result that arrives later is discarded and its resources released.
     *
     * @return {@code true} if the handle was {@link AsyncState#PENDING} and is now {@link AsyncState#CANCELLED},
     *         {@code false} if it had already reached a final state.
     */
    boolean cancel();
}
