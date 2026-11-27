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

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.RandomSource;
import com.sun.management.ThreadMXBean;

/**
 * Allocation measure of the world's random source: drawing during {@link World#update(double)} and reseeding between
 * two updates allocate nothing (REQ-DET-003, ADR-0078).
 *
 * <p>
 * Measured with the per-thread allocation counter of the JVM, after a warm-up of the same steps; the tolerance absorbs
 * the counter's own noise, far below one allocation per draw.
 * </p>
 */
final class RandomAllocationTest
{
    /** Measured steps. */
    private static final int STEPS = 1000;
    /** Draws of each kind per step. */
    private static final int DRAWS = 1000;
    /** Allocation tolerance, in bytes, over every measured step. */
    private static final long ALLOCATION_TOLERANCE = 1024L;
    /** The step, in seconds. */
    private static final double STEP = 1.0 / 60.0;

    /** The world. */
    private final World world = new World();
    /** The checksum of the draws, so that they are not optimized away. */
    private long sum;

    /**
     * Steady steps allocate nothing: each update runs a system drawing 1,000 values of each kind ({@code long},
     * bounded {@code int}, {@code double}, {@code boolean}), and the world is reseeded after every tenth update.
     */
    @Test
    void testDrawsAllocateNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final RandomSource random = world.getRandomSource();
        world.addSystem(step -> draw(random), 0);
        steps();

        final long before = bean.getCurrentThreadAllocatedBytes();
        steps();
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertNotEquals(0L, sum);
    }

    /**
     * Draws values of each kind.
     *
     * @param random The world's generator.
     */
    private void draw(RandomSource random)
    {
        for (int i = 0; i < DRAWS; i++)
        {
            sum += random.nextLong();
            sum += random.nextInt(1000);
            sum += (long) (random.nextDouble() * 1000.0);
            if (random.nextBoolean())
            {
                sum++;
            }
        }
    }

    /**
     * Runs the measured steps, reseeding after every tenth update.
     */
    private void steps()
    {
        for (int step = 0; step < STEPS; step++)
        {
            world.update(STEP);
            if (step % 10 == 9)
            {
                world.reseed(step);
            }
        }
    }
}
