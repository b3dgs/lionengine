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
 * Allocation measure of the steady step of {@link TransformCaptureSystem} and {@link MovementSystem}: nothing is
 * allocated per step (REQ-STD-002, REQ-ECS-009, ADR-0030, ADR-0079, ADR-0080).
 *
 * <p>
 * Measured with the per-thread allocation counter of the JVM, after a warm-up, as the {@code ecs} allocation tests;
 * the tolerance absorbs the counter's own noise, far below one allocation per operation.
 * </p>
 */
final class MovementAllocationTest
{
    /** Objects moved per step. */
    private static final int OBJECTS = 1000;
    /** Measured steps. */
    private static final int STEPS = 1000;
    /** Allocation tolerance, in bytes, over every measured step. */
    private static final long ALLOCATION_TOLERANCE = 1024L;
    /** The step, in seconds. */
    private static final double STEP = 1.0 / 60.0;

    /**
     * Steady steps of 1,000 objects, captured, clamped and moved, allocate nothing.
     */
    @Test
    void testSteadyStateAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final World world = new World();
        final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
        final TraitStorage<Movable> movables = world.register(Movable.TYPE);
        world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);
        world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
        final long first = world.create();
        for (int i = 0; i < OBJECTS; i++)
        {
            final long id = i == 0 ? first : world.create();
            transformables.add(id).setPosition(i, 0.0);
            final Movable movable = movables.add(id);
            movable.setVelocity(i % 2 == 0 ? 20.0 : -1.0, 1.0);
            movable.setMaxSpeed(10.0);
        }
        world.flush();
        steps(world);

        final long before = bean.getCurrentThreadAllocatedBytes();
        steps(world);
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertEquals(OBJECTS, world.getObjectCount());
        assertTrue(transformables.get(first).getY() > 0.0);
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
