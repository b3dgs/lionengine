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
 * Bounded pool of loading workers, provided by the platform (ADR-0019, ADR-0031, ADR-0040, REQ-RT-004).
 *
 * <p>
 * The engine starts no thread by itself; the platform owns the pool and its threads (2 workers on desktop, 1 on
 * Android by default). With {@code 0} workers, {@link #submit(WorkerTask)} runs the task synchronously on the caller
 * and starts no thread.
 * </p>
 *
 * <p>
 * Thread: {@link #submit(WorkerTask)} is called on the runtime thread; the task runs on a worker.
 * </p>
 *
 * @since 11.0.0
 */
public interface WorkerPool
{
    /**
     * Submits a task. Tasks start in submission order; with several workers they may complete in any order.
     *
     * @param task The task to run (never {@code null}).
     * @throws IllegalArgumentException If {@code task} is {@code null}.
     * @throws IllegalStateException If the pool is closed.
     */
    void submit(WorkerTask task);

    /**
     * Returns the number of worker threads.
     *
     * @return The number of workers, {@code 0} when tasks run synchronously on the caller.
     */
    int getWorkerCount();
}
