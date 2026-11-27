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
 * Allocation measure of the steady trait paths: {@link TraitStorage#add(long)}, {@link TraitStorage#remove(long)},
 * replacement, object removal with its traits and {@link World#flush()} allocate nothing once the capacities fit the
 * busiest step (ADR-0011, ADR-0030, ADR-0075; the benchmark of REQ-ECS-009 is batch 6).
 *
 * <p>
 * Measured with the per-thread allocation counter of the JVM, after a warm-up that reaches the busiest step; the
 * tolerance absorbs the counter's own noise, far below one allocation per operation.
 * </p>
 */
final class TraitAllocationTest
{
    /** Objects per step. */
    private static final int OBJECTS = 1000;
    /** Measured steps. */
    private static final int STEPS = 1000;
    /** Allocation tolerance, in bytes, over every measured step. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /**
     * Steady steps allocate nothing: each one removes the objects of the previous step with their two traits, creates
     * as many with two traits, replaces a trait of each, adds and removes a third, and flushes. The warm-up grows the
     * object table and the trait storages once.
     */
    @Test
    void testSteadyStateAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final World world = new World();
        final TraitStorage<Health> health = world.register(new HealthType());
        final TraitStorage<Health> armor = world.register(new HealthType());
        final TraitStorage<Health> shield = world.register(new HealthType());
        final long[] ids = new long[OBJECTS];
        steps(world, health, armor, shield, ids, STEPS);
        final int capacity = health.getCapacity();

        final long before = bean.getCurrentThreadAllocatedBytes();
        steps(world, health, armor, shield, ids, STEPS);
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertEquals(capacity, health.getCapacity());
        assertEquals(OBJECTS, health.getCount());
        assertEquals(OBJECTS, armor.getCount());
        assertEquals(0, shield.getCount());
    }

    /**
     * Runs steps.
     *
     * @param world The world.
     * @param health The first trait.
     * @param armor The second trait.
     * @param shield The third trait, added and removed in each step.
     * @param ids The objects of the previous step, replaced.
     * @param steps The number of steps.
     */
    private static void steps(World world,
                              TraitStorage<Health> health,
                              TraitStorage<Health> armor,
                              TraitStorage<Health> shield,
                              long[] ids,
                              int steps)
    {
        for (int step = 0; step < steps; step++)
        {
            for (int i = 0; i < ids.length; i++)
            {
                if (ids[i] != World.NO_OBJECT)
                {
                    health.add(ids[i]).points = step;
                    world.remove(ids[i]);
                }
                ids[i] = world.create();
                health.add(ids[i]).points = i;
                armor.add(ids[i]);
                shield.add(ids[i]);
                shield.remove(ids[i]);
            }
            world.flush();
        }
    }
}
