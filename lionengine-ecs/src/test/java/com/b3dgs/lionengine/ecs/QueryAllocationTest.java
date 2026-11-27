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
 * Allocation measure of the steady query paths: iteration, the incremental update at {@link World#flush()} and
 * {@link Query#copySortedIds(long[])} allocate nothing once the capacities fit the busiest step (REQ-ECS-005,
 * REQ-ECS-013, ADR-0011, ADR-0076; the benchmark of REQ-ECS-009 is batch 6).
 *
 * <p>
 * Measured with the per-thread allocation counter of the JVM, after a warm-up that reaches the busiest step; the
 * tolerance absorbs the counter's own noise, far below one allocation per operation.
 * </p>
 */
final class QueryAllocationTest
{
    /** Objects per step. */
    private static final int OBJECTS = 1000;
    /** Measured steps. */
    private static final int STEPS = 1000;
    /** Allocation tolerance, in bytes, over every measured step. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /** The world. */
    private final World world = new World();
    /** The included trait. */
    private final TraitStorage<Health> health = world.register(new HealthType());
    /** The second included trait. */
    private final TraitStorage<Health> armor = world.register(new HealthType());
    /** The excluded trait. */
    private final TraitStorage<Health> stunned = world.register(new HealthType());
    /** The query of {@code health}. */
    private final Query all = world.createQuery(health);
    /** The query of {@code health} and {@code armor}. */
    private final Query armored = world.createQuery(health, armor);
    /** The query of {@code health} without {@code stunned}. */
    private final Query active = world.createQuery(new TraitStorage<?>[] {health}, new TraitStorage<?>[] {stunned});
    /** The sorted copy buffer. */
    private final long[] buffer = new long[OBJECTS];
    /** The checksum of the iterations, so that they are not optimized away. */
    private long sum;

    /**
     * Steady steps allocate nothing: each one iterates three queries, removes and creates objects from inside an
     * iteration, adds and removes an excluded trait, replaces an included one, flushes, then copies the sorted IDs.
     */
    @Test
    void testSteadyStateAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        for (int i = 0; i < OBJECTS; i++)
        {
            final long id = world.create();
            health.add(id);
            armor.add(id);
        }
        world.flush();
        steps();
        final int capacity = world.getObjectCapacity();
        final int activeCount = active.getCount();

        final long before = bean.getCurrentThreadAllocatedBytes();
        steps();
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertEquals(capacity, world.getObjectCapacity());
        assertEquals(OBJECTS, all.getCount());
        assertEquals(OBJECTS, armored.getCount());
        assertEquals(activeCount, active.getCount());
        assertTrue(activeCount > 0 && activeCount < OBJECTS, () -> "Active: " + activeCount);
        assertTrue(sum > 0L);
    }

    /**
     * Runs the measured number of steps.
     */
    private void steps()
    {
        for (int step = 0; step < STEPS; step++)
        {
            for (int i = 0; i < active.getCount(); i++)
            {
                sum += health.get(active.getId(i)).points;
            }
            for (int i = 0; i < armored.getCount(); i++)
            {
                final long id = armored.getId(i);
                if (i % 2 == 0)
                {
                    stunned.add(id);
                }
                else
                {
                    stunned.remove(id);
                }
                health.add(id).points = step;
            }
            final int count = all.getCount();
            for (int i = 0; i < count; i += 10)
            {
                world.remove(all.getId(i));
                final long id = world.create();
                health.add(id).points = i;
                armor.add(id);
                stunned.add(id);
            }
            world.flush();
            sum += all.copySortedIds(buffer);
        }
    }
}
