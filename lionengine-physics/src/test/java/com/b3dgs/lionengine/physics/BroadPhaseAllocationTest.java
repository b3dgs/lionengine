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

package com.b3dgs.lionengine.physics;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.RandomSource;
import com.sun.management.ThreadMXBean;

/**
 * Allocation measure of the steady use of {@link BroadPhase}: clearing, adding and finding pairs allocate nothing once
 * the capacities fit (REQ-PHYS-001, REQ-ECS-009, ADR-0030, ADR-0082).
 */
final class BroadPhaseAllocationTest
{
    /** Boxes per step. */
    private static final int BOXES = 1000;
    /** Measured steps. */
    private static final int STEPS = 1000;
    /** Allocation tolerance, in bytes, over every measured step. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /**
     * Steps of 1,000 random boxes, after a warm-up step that grows the capacities, allocate nothing.
     */
    @Test
    void testSteadyStateAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final RandomSource random = new RandomSource(1L);
        final Box[] boxes = new Box[BOXES];
        for (int i = 0; i < BOXES; i++)
        {
            boxes[i] = new Box(random.nextDouble() * 60.0, random.nextDouble() * 60.0, 1.0, 1.0);
        }
        final BroadPhase broadPhase = new BroadPhase(BroadPhase.DEFAULT_CELL_SIZE, 1);
        int pairs = step(broadPhase, boxes);

        final long before = bean.getCurrentThreadAllocatedBytes();
        for (int s = 0; s < STEPS; s++)
        {
            pairs += step(broadPhase, boxes);
        }
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        final int total = pairs;
        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertTrue(total > 0, () -> "Pairs: " + total);
    }

    /**
     * Runs one step.
     *
     * @param broadPhase The broad phase.
     * @param boxes The boxes.
     * @return The pairs found.
     */
    private static int step(BroadPhase broadPhase, Box[] boxes)
    {
        broadPhase.clear();
        for (int i = 0; i < boxes.length; i++)
        {
            broadPhase.add(boxes[i]);
        }
        return broadPhase.findPairs();
    }
}
