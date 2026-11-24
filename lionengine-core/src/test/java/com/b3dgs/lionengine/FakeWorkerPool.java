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

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Worker pool whose tasks run when the test says so, for the tests of loaders (ADR-0040, ADR-0045, REQ-RT-010).
 *
 * <p>
 * With {@code 0} workers a task runs synchronously in {@link #submit(WorkerTask)}, like a real pool. With workers, a
 * task waits until {@link #runNext()} or {@link #runAll()} runs it on the calling thread, so the order of completions
 * is chosen by the test. Published in the {@code core} test JAR. Not thread-safe.
 * </p>
 */
public final class FakeWorkerPool implements WorkerPool
{
    /** Number of workers reported. */
    private final int workers;
    /** Submitted tasks not run yet. */
    private final Deque<WorkerTask> tasks = new ArrayDeque<>();
    /** Number of tasks run. */
    private int completed;
    /** Whether the pool is closed. */
    private boolean closed;

    /**
     * Creates a pool.
     *
     * @param workers The number of workers it reports, {@code 0} to run each task when submitted.
     * @throws IllegalArgumentException If {@code workers} is negative.
     */
    public FakeWorkerPool(int workers)
    {
        if (workers < 0)
        {
            throw new IllegalArgumentException("Negative worker count: " + workers);
        }
        this.workers = workers;
    }

    @Override
    public void submit(WorkerTask task)
    {
        if (task == null)
        {
            throw new IllegalArgumentException("Task must not be null");
        }
        if (closed)
        {
            throw new IllegalStateException("The pool is closed");
        }
        if (workers == 0)
        {
            run(task);
        }
        else
        {
            tasks.addLast(task);
        }
    }

    @Override
    public int getWorkerCount()
    {
        return workers;
    }

    /**
     * Runs the oldest waiting task on the calling thread.
     *
     * @return {@code true} if a task ran, {@code false} if none was waiting.
     */
    public boolean runNext()
    {
        final WorkerTask task = tasks.pollFirst();
        if (task != null)
        {
            run(task);
        }
        return task != null;
    }

    /**
     * Runs every waiting task, oldest first, including tasks submitted while they run.
     */
    public void runAll()
    {
        while (runNext())
        {
            // Each call runs one task.
        }
    }

    /**
     * Returns the number of tasks waiting.
     *
     * @return The number of submitted tasks not run yet.
     */
    public int getPendingCount()
    {
        return tasks.size();
    }

    /**
     * Returns the number of tasks run.
     *
     * @return The number of completed tasks.
     */
    public int getCompletedCount()
    {
        return completed;
    }

    /**
     * Closes the pool: later submissions fail; waiting tasks are kept.
     */
    public void close()
    {
        closed = true;
    }

    /**
     * Runs a task.
     *
     * @param task The task.
     */
    private void run(WorkerTask task)
    {
        task.execute();
        completed++;
    }
}
