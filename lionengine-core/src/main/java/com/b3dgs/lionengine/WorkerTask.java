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
 * Unit of work run by a {@link WorkerPool} (ADR-0019, ADR-0040).
 *
 * <p>
 * Runs on a worker thread, or on the submitting thread when the pool has no worker. It only reads and decodes: no
 * engine state, no GPU call, no cache access. It hands its result back to the runtime thread by posting an
 * {@link InboxEvent}.
 * </p>
 *
 * @since 11.0.0
 */
@FunctionalInterface
public interface WorkerTask
{
    /**
     * Executes the work.
     *
     * <p>
     * An exception must not escape: the task catches its failure and reports it through the inbox, so that the handle
     * becomes {@link AsyncState#FAILED} on the runtime thread.
     * </p>
     */
    void execute();
}
