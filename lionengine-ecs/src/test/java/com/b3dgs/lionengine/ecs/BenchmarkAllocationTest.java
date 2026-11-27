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
 * Allocation measure of the benchmark step of REQ-ECS-009: with 1,000 objects and 4 traits, the full step (three
 * queries iterated by the stand-in systems, removals and creations, one flush) allocates nothing in steady state
 * (ADR-0030, decision 3; ADR-0079).
 *
 * <p>
 * Measured with the per-thread allocation counter of the JVM, after a warm-up, with the 1 KiB tolerance of the other
 * allocation tests; this is the CI-asserted part of the benchmark, the wall-clock time is {@link StepBenchmarkTest}.
 * </p>
 */
final class BenchmarkAllocationTest
{
    /** Objects. */
    private static final int OBJECTS = 1000;
    /** Warm-up steps, more than one full lifetime cycle. */
    private static final int WARMUP = 1000;
    /** Measured steps. */
    private static final int STEPS = 1000;
    /** Allocation tolerance, in bytes, over every measured step. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /**
     * The steady benchmark step allocates nothing: no instance is created by the pools and no capacity grows.
     */
    @Test
    void testSteadyStepAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final BenchmarkWorld scene = new BenchmarkWorld(OBJECTS);
        scene.populate();
        scene.run(WARMUP);
        final World world = scene.getWorld();
        final int capacity = world.getObjectCapacity();
        final long created = created(scene);
        final long spawned = scene.getSpawned();

        final long before = bean.getCurrentThreadAllocatedBytes();
        scene.run(STEPS);
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertEquals(capacity, world.getObjectCapacity());
        assertEquals(created, created(scene));
        assertEquals(OBJECTS, world.getObjectCount());
        for (final Query query : scene.getQueries())
        {
            assertEquals(OBJECTS, query.getCount());
        }
        final long respawned = scene.getSpawned() - spawned;
        assertTrue(respawned > (long) STEPS, () -> "Respawned: " + respawned);
        assertTrue(Double.isFinite(scene.getChecksum()));
    }

    /**
     * Returns the instances created by the pools of the scene.
     *
     * @param scene The scene.
     * @return The sum of the creations of its four types.
     */
    private static long created(BenchmarkWorld scene)
    {
        long sum = 0L;
        for (final CountingType<?> type : scene.getTypes())
        {
            sum += type.getCreated();
        }
        return sum;
    }
}
