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

package com.b3dgs.lionengine.traits;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Test;

import com.sun.management.ThreadMXBean;

/**
 * Allocation measure of the steady step of {@link CollisionSystem} on moving objects, with pairs starting and ending
 * and their events delivered: nothing is allocated per step (REQ-STD-005, REQ-ECS-009, ADR-0030, ADR-0079,
 * ADR-0082).
 *
 * <p>
 * Measured with the per-thread allocation counter of the JVM, after a warm-up that grows the capacities to the busiest
 * step, as the {@code ecs} allocation tests; the tolerance absorbs the counter's own noise, far below one allocation
 * per operation.
 * </p>
 */
final class CollisionAllocationTest
{
    /** Objects colliding per step. */
    private static final int OBJECTS = 1000;
    /** Warm-up and measured steps. */
    private static final int STEPS = 1000;
    /** Allocation tolerance, in bytes, over every measured step. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /**
     * Steady steps of 1,000 moving objects in a bounded area, with enters and exits every step, allocate nothing.
     */
    @Test
    void testSteadyStateAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final CollisionBenchmarkWorld scene = new CollisionBenchmarkWorld(OBJECTS);
        scene.populate();
        for (int step = 0; step < STEPS; step++)
        {
            scene.getWorld().update(CollisionBenchmarkWorld.STEP);
        }
        final long events = scene.getEvents();

        final long before = bean.getCurrentThreadAllocatedBytes();
        for (int step = 0; step < STEPS; step++)
        {
            scene.getWorld().update(CollisionBenchmarkWorld.STEP);
        }
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertTrue(scene.getEvents() > events, "No event in the measured steps");
        assertTrue(scene.getCollision().getPairCount() > 0, "No pair");
        assertEquals(OBJECTS, scene.getCollision().getQuery().getCount());
    }
}
