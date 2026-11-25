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
package com.b3dgs.lionengine.platform.awt;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.b3dgs.lionengine.WorkerPool;
import com.b3dgs.lionengine.WorkerTask;

/**
 * Loading worker pool of the AWT platform (ADR-0019, ADR-0031, ADR-0040, REQ-RT-004).
 *
 * <p>
 * Daemon threads named {@code lionengine-worker-1}, {@code lionengine-worker-2}, and so on, started at the first
 * submissions; tasks start in submission order. The platform closes the pool when its loop ends.
 * </p>
 */
final class AwtWorkerPool implements WorkerPool
{
    /** Prefix of the worker thread names. */
    private static final String THREAD_PREFIX = "lionengine-worker-";

    /** Executor of the tasks. */
    private final ThreadPoolExecutor executor;
    /** Number of workers. */
    private final int workers;

    /**
     * Creates the pool; no thread is started before the first submission.
     *
     * @param workers The number of workers, positive.
     */
    AwtWorkerPool(int workers)
    {
        this.workers = workers;
        final AtomicInteger count = new AtomicInteger();
        executor = new ThreadPoolExecutor(workers, workers, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), r ->
        {
            final Thread thread = new Thread(r, THREAD_PREFIX + count.incrementAndGet());
            thread.setDaemon(true);
            return thread;
        });
    }

    /**
     * Closes the pool: the tasks not started are dropped, and later submissions are rejected.
     */
    void close()
    {
        executor.shutdownNow();
    }

    @Override
    public void submit(WorkerTask task)
    {
        if (task == null)
        {
            throw new IllegalArgumentException("Task must not be null");
        }
        try
        {
            executor.execute(task::execute);
        }
        catch (final RejectedExecutionException exception)
        {
            throw new IllegalStateException("The pool is closed", exception);
        }
    }

    @Override
    public int getWorkerCount()
    {
        return workers;
    }
}
