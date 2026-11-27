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
package com.b3dgs.lionengine.ecs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Test;

import com.sun.management.ThreadMXBean;

/**
 * Allocation measure of the steady paths: {@link World#create()}, {@link World#remove(long)} and {@link World#flush()}
 * allocate nothing once the capacities fit the busiest step (ADR-0011, ADR-0030; the benchmark of REQ-ECS-009 is
 * batch 6).
 *
 * <p>
 * Measured with the per-thread allocation counter of the JVM, after a warm-up that reaches the busiest step; the
 * tolerance absorbs the counter's own noise, far below one allocation per operation.
 * </p>
 */
final class WorldAllocationTest
{
    /** Objects created and removed per step. */
    private static final int OBJECTS = 1000;
    /** Measured steps. */
    private static final int STEPS = 1000;
    /** Allocation tolerance, in bytes, over every measured step. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /**
     * Steady create, remove and flush steps allocate nothing. The warm-up grows the table once: the removed objects
     * free their indices only at the flush, so a step holds 2,000 indices, above the default 1,024.
     */
    @Test
    void testSteadyStateAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final World world = new World();
        final long[] ids = new long[OBJECTS];
        steps(world, ids, STEPS);
        final int capacity = world.getObjectCapacity();

        final long before = bean.getCurrentThreadAllocatedBytes();
        steps(world, ids, STEPS);
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertEquals(capacity, world.getObjectCapacity());
    }

    /**
     * Runs steps: each removes the objects of the previous step, creates as many, and flushes.
     *
     * @param world The world.
     * @param ids The objects of the previous step, replaced.
     * @param steps The number of steps.
     */
    private static void steps(World world, long[] ids, int steps)
    {
        for (int step = 0; step < steps; step++)
        {
            for (int i = 0; i < ids.length; i++)
            {
                if (ids[i] != World.NO_OBJECT)
                {
                    world.remove(ids[i]);
                }
                ids[i] = world.create();
            }
            world.flush();
        }
    }
}
