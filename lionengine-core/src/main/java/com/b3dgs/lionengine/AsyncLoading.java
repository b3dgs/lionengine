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
 * Creates the {@link AsyncHandle}s of the loaders: a request whose work runs on a {@link WorkerPool} and whose
 * completion runs on the runtime thread at inbox delivery, or a handle already final (ADR-0018, ADR-0019, ADR-0031,
 * ADR-0040, REQ-RT-004, REQ-RT-010).
 *
 * <p>
 * A submitted request is {@link AsyncState#PENDING}. Its work runs on a worker (on the submitting thread when the
 * pool has no worker) and posts the outcome to the {@link Inbox}; at the delivery, on the runtime thread, the request
 * becomes {@link AsyncState#READY} with the value of its completion, or {@link AsyncState#FAILED} with the exception
 * of its work or of its completion. A request cancelled before then becomes {@link AsyncState#CANCELLED} and its
 * result is discarded without completion. So the state and the progress change only at inbox delivery, even with a
 * pool without worker.
 * </p>
 *
 * <p>
 * Progress: resources do not tell their size, so the progress is {@code 0} until the request ends, then {@code 1} once
 * it is ready or failed; a cancelled request keeps its progress.
 * </p>
 *
 * <p>
 * A full inbox never loses a completion: a worker retries the post every millisecond until the inbox accepts it, and
 * gives up only when interrupted (the pool is closed, the request stays pending). Without worker the post happens on
 * the submitting thread, which delivers the inbox itself, so a rejected post fails the request at once, before
 * {@link #submit(WorkerPool, Inbox, AsyncWork, AsyncCompletion)} returns, with an {@link IllegalStateException}.
 * </p>
 *
 * <p>
 * Threads: every method runs on the runtime thread, like the handles it returns (ADR-0018).
 * </p>
 *
 * @since 11.0.0
 */
public final class AsyncLoading
{
    /**
     * Submits a request.
     *
     * @param <R> The type of the result of the work.
     * @param <T> The type of the loaded value.
     * @param workers The pool running the work (never {@code null}).
     * @param inbox The inbox delivering the result on the runtime thread (never {@code null}).
     * @param work The work, run on a worker: reads and decodes only (never {@code null}).
     * @param completion The completion, run on the runtime thread at delivery (never {@code null}).
     * @return The pending handle; {@link AsyncState#FAILED} at once when the pool has no worker and the inbox refused
     *         the result.
     * @throws IllegalArgumentException If an argument is {@code null}.
     * @throws IllegalStateException If the pool is closed.
     */
    public static <R, T> AsyncHandle<T> submit(WorkerPool workers,
                                               Inbox inbox,
                                               AsyncWork<R> work,
                                               AsyncCompletion<R, T> completion)
    {
        AsyncRequest.checkNotNull(workers, "Workers");
        final AsyncRequest<R, T> request = new AsyncRequest<>(inbox, work, completion, workers.getWorkerCount() == 0);
        workers.submit(request);
        return request;
    }

    /**
     * Returns a handle already {@link AsyncState#READY}, for a value available without loading, for example an image
     * already cached.
     *
     * @param <T> The type of the value.
     * @param value The value, owned by the code that made the request (never {@code null}).
     * @return The ready handle, with a progress of {@code 1}.
     * @throws IllegalArgumentException If the value is {@code null}.
     */
    public static <T> AsyncHandle<T> ready(T value)
    {
        return AsyncRequest.ready(value);
    }

    /**
     * Returns a handle already {@link AsyncState#FAILED}, for a request refused before any work, for example a key
     * without decoder.
     *
     * @param <T> The type of the value.
     * @param failure The failure (never {@code null}).
     * @return The failed handle, with a progress of {@code 1}.
     * @throws IllegalArgumentException If the failure is {@code null}.
     */
    public static <T> AsyncHandle<T> failed(RuntimeException failure)
    {
        return AsyncRequest.failed(failure);
    }

    /**
     * Private constructor.
     */
    private AsyncLoading()
    {
        throw new UnsupportedOperationException();
    }
}
