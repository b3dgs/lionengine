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
package com.b3dgs.lionengine.render.awt;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.sun.management.ThreadMXBean;

/**
 * Test {@link Scale2x} on reference images (REQ-FILTER-004, ADR-0063).
 */
final class Scale2xTest
{
    /** First color. */
    private static final int A = 0xFF112233;
    /** Second color. */
    private static final int B = 0xFFAABBCC;
    /** Bytes tolerated for the allocation measurement itself. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /** Filter of the test. */
    private final Scale2x filter = new Scale2x();

    /**
     * Scales an image.
     *
     * @param source The source pixels.
     * @param width The source width.
     * @param height The source height.
     * @return The target pixels.
     */
    private int[] scale(int[] source, int width, int height)
    {
        final int[] target = new int[4 * width * height];
        filter.apply(source, width, height, target, 2 * width, 2 * height);
        return target;
    }

    /**
     * A checkerboard gets its diagonals smoothed, each target pixel computed by hand from the rules of AdvMAME2x.
     */
    @Test
    void testDiagonals()
    {
        final int[] source =
        {
            A, B,
            B, A
        };
        final int[] expected =
        {
            A, A, B, B,
            A, B, A, B,
            B, A, B, A,
            B, B, A, A
        };

        assertArrayEquals(expected, scale(source, 2, 2));
    }

    /**
     * A uniform image and straight edges are only doubled: the rules apply where both pairs of neighbours differ.
     */
    @Test
    void testStraightEdges()
    {
        final int[] uniform = new int[9];
        Arrays.fill(uniform, A);
        final int[] doubled = new int[36];
        Arrays.fill(doubled, A);
        final int[] line =
        {
            A, B, A
        };
        final int[] lineDoubled =
        {
            A, A, B, B, A, A,
            A, A, B, B, A, A
        };

        assertArrayEquals(doubled, scale(uniform, 3, 3));
        assertArrayEquals(lineDoubled, scale(line, 3, 1));
    }

    /**
     * The target must be twice the source, and the buffers large enough.
     */
    @Test
    void testInvalidSizes()
    {
        final int[] source = new int[4];
        final int[] target = new int[16];
        final int[] small = new int[8];

        assertThrows(IllegalArgumentException.class, () -> filter.apply(source, 2, 2, target, 3, 4));
        assertThrows(IllegalArgumentException.class, () -> filter.apply(source, 2, 2, target, 4, 3));
        assertThrows(IllegalArgumentException.class, () -> filter.apply(source, 2, 2, small, 4, 4));
        assertThrows(IllegalArgumentException.class, () -> filter.apply(source, 4, 2, target, 8, 4));
    }

    /**
     * Scaling a frame allocates nothing, once warmed up.
     */
    @Test
    void testAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final int width = 64;
        final int height = 48;
        final int[] source = new int[width * height];
        for (int i = 0; i < source.length; i++)
        {
            source[i] = i % 3 == 0 ? A : B;
        }
        final int[] target = new int[4 * source.length];
        filter.apply(source, width, height, target, 2 * width, 2 * height);

        final long before = bean.getCurrentThreadAllocatedBytes();
        for (int i = 0; i < 10; i++)
        {
            filter.apply(source, width, height, target, 2 * width, 2 * height);
        }
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
    }
}
