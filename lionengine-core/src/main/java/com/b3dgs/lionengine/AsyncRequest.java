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
 * Asynchronous request created by {@link AsyncLoading}: the worker task of its work, the inbox event of its result,
 * and the handle of its value (ADR-0018, ADR-0019, ADR-0031, ADR-0040, REQ-RT-004, REQ-RT-010).
 *
 * <p>
 * The worker writes the result or the failure of the work, then the volatile flag {@code worked}, then posts the
 * event; the runtime thread reads the flag first at delivery, so it sees what the worker wrote. Every other field is
 * read and written on the runtime thread only.
 * </p>
 *
 * @param <R> The type of the result of the work.
 * @param <T> The type of the loaded value.
 */
final class AsyncRequest<R, T> implements AsyncHandle<T>, WorkerTask
{
    /** Delay between two posts refused by a full inbox, in milliseconds. */
    private static final long RETRY_MILLIS = 1L;
    /** Error message of a value read while not ready. */
    private static final String ERROR_NOT_READY = "Asynchronous load not ready: ";
    /** Error message of a result refused by a full inbox, without worker. */
    private static final String ERROR_INBOX = "Inbox full: the result of the asynchronous load was refused";
    /** Error message of a work or completion that returned {@code null}. */
    private static final String ERROR_NULL_RESULT = "Asynchronous load returned null";

    /**
     * Checks that an argument is not {@code null}.
     *
     * @param value The argument.
     * @param name The name of the argument, for the message.
     * @throws IllegalArgumentException If the argument is {@code null}.
     */
    static void checkNotNull(Object value, String name)
    {
        if (value == null)
        {
            throw new IllegalArgumentException(name + " must not be null");
        }
    }

    /**
     * Creates a handle already ready.
     *
     * @param <T> The type of the value.
     * @param value The value.
     * @return The handle.
     * @throws IllegalArgumentException If the value is {@code null}.
     */
    static <T> AsyncRequest<Object, T> ready(T value)
    {
        checkNotNull(value, "Value");
        final AsyncRequest<Object, T> request = new AsyncRequest<>();
        request.succeed(value);
        return request;
    }

    /**
     * Creates a handle already failed.
     *
     * @param <T> The type of the value.
     * @param failure The failure.
     * @return The handle.
     * @throws IllegalArgumentException If the failure is {@code null}.
     */
    static <T> AsyncRequest<Object, T> failed(RuntimeException failure)
    {
        checkNotNull(failure, "Failure");
        final AsyncRequest<Object, T> request = new AsyncRequest<>();
        request.fail(failure);
        return request;
    }

    /** The inbox of the result, {@code null} for a final handle. */
    private final Inbox inbox;
    /** The work, {@code null} for a final handle. */
    private final AsyncWork<R> work;
    /** The completion, {@code null} for a final handle. */
    private final AsyncCompletion<R, T> completion;
    /** {@code true} when the work runs on the submitting thread. */
    private final boolean synchronous;
    /** The event delivering the result, created once. */
    private final InboxEvent delivery = this::deliver;
    /** The result of the work, written by the worker before {@link #worked}. */
    private R result;
    /** The failure of the work, written by the worker before {@link #worked}. */
    private RuntimeException workFailure;
    /** Written last by the worker, read first at delivery. */
    private volatile boolean worked;
    /** The state. */
    private AsyncState state = AsyncState.PENDING;
    /** The value once ready. */
    private T value;
    /** The failure once failed. */
    private RuntimeException failure;
    /** The progress in {@code [0, 1]}. */
    private double progress;

    /**
     * Creates a pending request.
     *
     * @param inbox The inbox of the result.
     * @param work The work.
     * @param completion The completion.
     * @param synchronous {@code true} when the pool has no worker.
     * @throws IllegalArgumentException If an argument is {@code null}.
     */
    AsyncRequest(Inbox inbox, AsyncWork<R> work, AsyncCompletion<R, T> completion, boolean synchronous)
    {
        super();

        checkNotNull(inbox, "Inbox");
        checkNotNull(work, "Work");
        checkNotNull(completion, "Completion");
        this.inbox = inbox;
        this.work = work;
        this.completion = completion;
        this.synchronous = synchronous;
    }

    /**
     * Creates a request to make final at once.
     */
    private AsyncRequest()
    {
        super();

        inbox = null;
        work = null;
        completion = null;
        synchronous = true;
    }

    /**
     * Runs the work on the worker, then posts its outcome. An exception of the work never escapes (contract of
     * {@link WorkerTask}): it becomes the failure of the request.
     */
    @Override
    public void execute()
    {
        try
        {
            result = work.execute();
            if (result == null)
            {
                workFailure = new IllegalStateException(ERROR_NULL_RESULT);
            }
        }
        // CHECKSTYLE IGNORE LINE: IllegalCatch
        catch (final RuntimeException exception)
        {
            workFailure = exception;
        }
        worked = true;
        post();
    }

    @Override
    public AsyncState getState()
    {
        return state;
    }

    @Override
    public double getProgress()
    {
        return progress;
    }

    @Override
    public T get()
    {
        if (state != AsyncState.READY)
        {
            throw new IllegalStateException(ERROR_NOT_READY + state);
        }
        return value;
    }

    @Override
    public RuntimeException getFailure()
    {
        return failure;
    }

    @Override
    public boolean cancel()
    {
        final boolean cancelled = state == AsyncState.PENDING;
        if (cancelled)
        {
            state = AsyncState.CANCELLED;
        }
        return cancelled;
    }

    /**
     * Posts the result; on a worker, retries until the inbox accepts it or the worker is interrupted.
     */
    private void post()
    {
        boolean waiting = !inbox.post(delivery);
        if (waiting && synchronous)
        {
            fail(new IllegalStateException(ERROR_INBOX));
            waiting = false;
        }
        while (waiting)
        {
            try
            {
                Thread.sleep(RETRY_MILLIS);
                waiting = !inbox.post(delivery);
            }
            catch (final InterruptedException exception)
            {
                Thread.currentThread().interrupt();
                waiting = false;
            }
        }
    }

    /**
     * Delivers the result on the runtime thread: completes a pending request, discards the result of a cancelled one.
     */
    private void deliver()
    {
        if (worked && state == AsyncState.PENDING)
        {
            if (workFailure == null)
            {
                complete();
            }
            else
            {
                fail(workFailure);
            }
        }
        result = null;
        workFailure = null;
    }

    /**
     * Runs the completion; its exception fails the request.
     */
    private void complete()
    {
        try
        {
            final T completed = completion.complete(result);
            if (completed == null)
            {
                fail(new IllegalStateException(ERROR_NULL_RESULT));
            }
            else
            {
                succeed(completed);
            }
        }
        // CHECKSTYLE IGNORE LINE: IllegalCatch
        catch (final RuntimeException exception)
        {
            fail(exception);
        }
    }

    /**
     * Makes the request ready.
     *
     * @param completed The value.
     */
    private void succeed(T completed)
    {
        value = completed;
        state = AsyncState.READY;
        progress = 1.0;
    }

    /**
     * Makes the request failed.
     *
     * @param exception The failure.
     */
    private void fail(RuntimeException exception)
    {
        failure = exception;
        state = AsyncState.FAILED;
        progress = 1.0;
    }
}
