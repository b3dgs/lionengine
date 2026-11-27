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
 * Allocation measure of the steady step: {@link World#update(double)}, the dispatch of the systems, the flush and the
 * delivery of the events allocate nothing once the capacities fit the busiest step (REQ-ECS-007, REQ-ECS-008,
 * REQ-ECS-012, ADR-0011, ADR-0018, ADR-0034, ADR-0077; the benchmark of REQ-ECS-009 is batch 6).
 *
 * <p>
 * Measured with the per-thread allocation counter of the JVM, after a warm-up that reaches the busiest step; the
 * tolerance absorbs the counter's own noise, far below one allocation per operation.
 * </p>
 */
final class UpdateAllocationTest
{
    /** Objects per step. */
    private static final int OBJECTS = 1000;
    /** Measured steps. */
    private static final int STEPS = 1000;
    /** Allocation tolerance, in bytes, over every measured step. */
    private static final long ALLOCATION_TOLERANCE = 1024L;
    /** The step, in seconds. */
    private static final double STEP = 1.0 / 60.0;

    /** The world. */
    private final World world = new World();
    /** The first trait. */
    private final TraitStorage<Health> health = world.register(new HealthType());
    /** The trait added and removed each step. */
    private final TraitStorage<Health> stunned = world.register(new HealthType());
    /** The query of {@code health}. */
    private final Query all = world.createQuery(health);
    /** The events counted by the listeners. */
    private final long[] events = new long[4];
    /** The checksum of the systems, so that they are not optimized away. */
    private long sum;
    /** The system disabled every other step. */
    private final GameSystem blinking = step -> sum++;

    /**
     * Steady steps allocate nothing: each update applies the enabled flags, runs four systems (one switching another,
     * one iterating a query and adding or removing a trait from inside the iteration, one removing and creating 100
     * objects with a trait), flushes about 1,300 commands and delivers about 3,000 events to two object listeners and
     * two trait listeners.
     */
    @Test
    void testSteadyStateAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        listen();
        world.addSystem(step -> world.setSystemEnabled(blinking, !world.isSystemEnabled(blinking)), 0);
        world.addSystem(blinking, 1000);
        world.addSystem(step -> iterate(), 2000);
        world.addSystem(step -> respawn(), 5000);
        for (int i = 0; i < OBJECTS; i++)
        {
            health.add(world.create());
        }
        world.flush();
        steps();
        final int capacity = world.getObjectCapacity();
        final long counted = events[0] + events[1] + events[2] + events[3];

        final long before = bean.getCurrentThreadAllocatedBytes();
        steps();
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertEquals(capacity, world.getObjectCapacity());
        assertEquals(OBJECTS, all.getCount());
        final long measured = events[0] + events[1] + events[2] + events[3] - counted;
        assertTrue(measured > 2_000L * STEPS, () -> "Events: " + measured);
        assertTrue(sum > 0L);
    }

    /**
     * Registers two object listeners and two trait listeners counting the events.
     */
    private void listen()
    {
        for (int i = 0; i < 2; i++)
        {
            world.addObjectListener(new ObjectListener()
            {
                @Override
                public void onCreated(long objectId)
                {
                    events[0]++;
                }

                @Override
                public void onDestroyed(long objectId)
                {
                    events[1]++;
                }
            });
            world.addTraitListener(new TraitListener()
            {
                @Override
                public void onTraitAdded(long objectId, int traitTypeId)
                {
                    events[2]++;
                }

                @Override
                public void onTraitRemoved(long objectId, int traitTypeId)
                {
                    events[3]++;
                }
            });
        }
    }

    /**
     * Iterates the query, adding or removing the stunned trait from inside the iteration.
     */
    private void iterate()
    {
        final int count = all.getCount();
        for (int i = 0; i < count; i++)
        {
            final long id = all.getId(i);
            sum += health.get(id).points;
            if (stunned.has(id))
            {
                stunned.remove(id);
            }
            else
            {
                stunned.add(id);
            }
        }
    }

    /**
     * Removes every tenth object and creates a new one with a trait.
     */
    private void respawn()
    {
        final int count = all.getCount();
        for (int i = 0; i < count; i += 10)
        {
            world.remove(all.getId(i));
            health.add(world.create()).points = i;
        }
    }

    /**
     * Runs the measured number of updates.
     */
    private void steps()
    {
        for (int step = 0; step < STEPS; step++)
        {
            world.update(STEP);
        }
    }
}
