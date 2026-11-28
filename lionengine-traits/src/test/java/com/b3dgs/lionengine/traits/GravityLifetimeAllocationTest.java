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

import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;
import com.sun.management.ThreadMXBean;

/**
 * Allocation measure of the steady step of {@link GravitySystem} and {@link LifetimeSystem}, with an expiry and a
 * creation in every step: nothing is allocated per step (REQ-STD-003, REQ-STD-006, REQ-ECS-009, ADR-0030, ADR-0079,
 * ADR-0081).
 *
 * <p>
 * Measured with the per-thread allocation counter of the JVM, after a warm-up, as the {@code ecs} allocation tests;
 * the tolerance absorbs the counter's own noise, far below one allocation per operation.
 * </p>
 */
final class GravityLifetimeAllocationTest
{
    /** Objects pulled and counted down per step. */
    private static final int OBJECTS = 1000;
    /** Measured steps. */
    private static final int STEPS = 1000;
    /** Allocation tolerance, in bytes, over every measured step. */
    private static final long ALLOCATION_TOLERANCE = 1024L;
    /** The step, in seconds. */
    private static final double STEP = 1.0 / 60.0;
    /** Slot of the respawn, late bookkeeping after 5000 (ADR-0034). */
    private static final int RESPAWN_SLOT = 6000;

    /**
     * Steady steps of 1,000 objects accelerated and counted down, one expiring and one created per step, allocate
     * nothing.
     */
    @Test
    void testSteadyStateAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final World world = new World();
        final TraitStorage<Movable> movables = world.register(Movable.TYPE);
        final TraitStorage<Gravity> gravities = world.register(Gravity.TYPE);
        final TraitStorage<Lifetime> lifetimes = world.register(Lifetime.TYPE);
        world.addSystem(new GravitySystem(world, movables, gravities), GravitySystem.SLOT);
        world.addSystem(new LifetimeSystem(world, lifetimes), LifetimeSystem.SLOT);
        final long first = world.create();
        add(movables, gravities, lifetimes, first, Double.POSITIVE_INFINITY);
        for (int i = 1; i < OBJECTS - 1; i++)
        {
            add(movables, gravities, lifetimes, world.create(), Double.POSITIVE_INFINITY);
        }
        add(movables, gravities, lifetimes, world.create(), 0.0);
        world.addSystem(step -> add(movables, gravities, lifetimes, world.create(), 0.0), RESPAWN_SLOT);
        world.flush();
        steps(world);

        final long before = bean.getCurrentThreadAllocatedBytes();
        steps(world);
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertEquals(OBJECTS, world.getObjectCount());
        assertEquals(-3.0, movables.get(first).getVelocityY());
    }

    /**
     * Adds the three traits to an object.
     *
     * @param movables The movables.
     * @param gravities The gravities.
     * @param lifetimes The lifetimes.
     * @param id The object.
     * @param duration The lifetime, in seconds.
     */
    private static void add(TraitStorage<Movable> movables,
                            TraitStorage<Gravity> gravities,
                            TraitStorage<Lifetime> lifetimes,
                            long id,
                            double duration)
    {
        movables.add(id);
        final Gravity gravity = gravities.add(id);
        gravity.setAcceleration(-9.81);
        gravity.setTerminalVelocity(3.0);
        lifetimes.add(id).setDuration(duration);
    }

    /**
     * Runs the measured number of updates.
     *
     * @param world The world.
     */
    private static void steps(World world)
    {
        for (int step = 0; step < STEPS; step++)
        {
            world.update(STEP);
        }
    }
}
