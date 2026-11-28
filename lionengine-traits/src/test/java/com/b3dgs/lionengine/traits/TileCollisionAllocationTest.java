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
 * Allocation measure of the steady step of {@link CollisionSystem} with the tile push-out, on walking and hopping
 * objects landing, hitting walls and leaving the ground: nothing is allocated per step (REQ-STD-010, REQ-ECS-009,
 * ADR-0030, ADR-0083).
 *
 * <p>
 * Measured with the per-thread allocation counter of the JVM over 1,000 steps of the 1,000 objects of
 * {@link TileBenchmarkWorld}, after a warm-up that grows the capacities and the tile buffer, with the 1 KiB tolerance
 * of the other allocation tests.
 * </p>
 */
final class TileCollisionAllocationTest
{
    /** Objects. */
    private static final int OBJECTS = 1000;
    /** Measured steps. */
    private static final int STEPS = 1000;
    /** Allocation tolerance, in bytes, over every measured step. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /**
     * Steps of 1,000 objects pushed out of the tiles, after a warm-up, allocate nothing.
     */
    @Test
    void testSteadyStateAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final TileBenchmarkWorld scene = new TileBenchmarkWorld(OBJECTS);
        scene.populate();
        for (int step = 0; step < STEPS; step++)
        {
            scene.getWorld().update(TileBenchmarkWorld.STEP);
        }
        final long grounded = scene.getGrounded();

        final long before = bean.getCurrentThreadAllocatedBytes();
        for (int step = 0; step < STEPS; step++)
        {
            scene.getWorld().update(TileBenchmarkWorld.STEP);
        }
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertTrue(scene.getGrounded() > grounded, "No landing in the measured steps");
        assertEquals(OBJECTS, scene.getCollision().getTileQuery().getCount());
    }
}
