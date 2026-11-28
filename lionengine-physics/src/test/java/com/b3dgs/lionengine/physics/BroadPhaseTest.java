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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import com.b3dgs.lionengine.RandomSource;

/**
 * Tests of {@link BroadPhase}: the pairs against a brute-force reference on random boxes, each pair once, edges,
 * growth, determinism and validation (REQ-PHYS-001, REQ-STD-005, ADR-0030, ADR-0082).
 */
final class BroadPhaseTest
{
    /** Random scenes per cell size. */
    private static final int SCENES = 20;
    /** Seed of the random scenes. */
    private static final long SEED = 0x4C49_4F4EL;

    /**
     * On random boxes from a seeded source (sizes from 0 to 3 m, some larger than the cell, negative coordinates,
     * some centers on cell edges), the broad phase finds exactly the pairs of the brute-force comparison, each once,
     * first lower than second.
     *
     * @param cellSize The cell size, in meters.
     */
    @ParameterizedTest
    @ValueSource(doubles =
    {
        0.25, 1.0, 2.0, 5.0, 100.0
    })
    void testAgainstBruteForce(double cellSize)
    {
        final RandomSource random = new RandomSource(SEED);
        final BroadPhase broadPhase = new BroadPhase(cellSize, 1);
        for (int scene = 0; scene < SCENES; scene++)
        {
            final int count = 1 + random.nextInt(200);
            final Box[] boxes = randomBoxes(random, count);
            broadPhase.clear();
            for (int i = 0; i < count; i++)
            {
                assertEquals(i, broadPhase.add(boxes[i]));
            }

            final int pairs = broadPhase.findPairs();

            assertEquals(count, broadPhase.getBoxCount());
            assertEquals(pairs, broadPhase.getPairCount());
            assertEquals(bruteForce(boxes), found(broadPhase));
        }
    }

    /**
     * Boxes touching by an edge or a corner, on a cell edge or not, form no pair; overlapping boxes across a cell edge
     * form one pair.
     */
    @Test
    void testEdges()
    {
        final BroadPhase broadPhase = new BroadPhase(BroadPhase.DEFAULT_CELL_SIZE, 4);
        broadPhase.add(new Box(1.0, 1.0, 2.0, 2.0));
        broadPhase.add(new Box(3.0, 1.0, 2.0, 2.0));
        broadPhase.add(new Box(3.0, 3.0, 2.0, 2.0));
        broadPhase.add(new Box(0.5, 2.5, 1.0, 1.0));

        assertEquals(0, broadPhase.findPairs());

        broadPhase.add(new Box(2.0, 2.0, 0.5, 0.5));

        assertEquals(3, broadPhase.findPairs());
        assertEquals(Set.of("0-4", "1-4", "2-4"), found(broadPhase));
    }

    /**
     * A box covering many cells overlapping a box covering many cells is still reported once.
     */
    @Test
    void testLargeBoxes()
    {
        final BroadPhase broadPhase = new BroadPhase(1.0, 1);
        broadPhase.add(new Box(0.0, 0.0, 10.0, 10.0));
        broadPhase.add(new Box(-1.0, 1.0, 9.0, 7.0));
        broadPhase.add(new Box(-100.0, -100.0, 0.5, 0.5));

        assertEquals(1, broadPhase.findPairs());
        assertEquals(0, broadPhase.getFirst(0));
        assertEquals(1, broadPhase.getSecond(0));
    }

    /**
     * The same boxes in the same order give the same pairs in the same order, step after step; clearing removes the
     * boxes and the pairs.
     */
    @Test
    void testDeterministicAndClear()
    {
        final RandomSource random = new RandomSource(SEED);
        final Box[] boxes = randomBoxes(random, 300);
        final BroadPhase broadPhase = new BroadPhase(BroadPhase.DEFAULT_CELL_SIZE, 300);
        final int[] order = pairsInOrder(broadPhase, boxes);

        for (int step = 0; step < 3; step++)
        {
            assertEquals(java.util.Arrays.toString(order), java.util.Arrays.toString(pairsInOrder(broadPhase, boxes)));
        }

        broadPhase.clear();

        assertEquals(0, broadPhase.getBoxCount());
        assertEquals(0, broadPhase.getPairCount());
        assertEquals(0, broadPhase.findPairs());
    }

    /**
     * The cell size is kept; an invalid cell size or capacity is refused.
     *
     * @param cellSize The cell size.
     * @param capacity The capacity.
     */
    @ParameterizedTest
    @CsvSource(
    {
        "0, 1", "-1, 1", "NaN, 1", "Infinity, 1", "1, 0", "1, -1", "1, 268435457"
    })
    void testInvalidConstruction(double cellSize, int capacity)
    {
        assertThrows(IllegalArgumentException.class, () -> new BroadPhase(cellSize, capacity));
    }

    /**
     * The accessors check their arguments; a box over too many cells is refused and leaves the broad phase unchanged.
     */
    @Test
    void testChecks()
    {
        final BroadPhase broadPhase = new BroadPhase(0.5, 2);
        assertEquals(0.5, broadPhase.getCellSize());
        broadPhase.add(new Box(0.0, 0.0, 1.0, 1.0));
        broadPhase.add(new Box(0.25, 0.0, 1.0, 1.0));
        assertEquals(1, broadPhase.findPairs());
        final Box huge = new Box(0.0, 0.0, 1.0e6, 1.0e6);

        assertThrows(IllegalArgumentException.class, () -> broadPhase.add(null));
        assertThrows(IllegalStateException.class, () -> broadPhase.add(huge));
        assertThrows(IllegalArgumentException.class, () -> broadPhase.getFirst(-1));
        assertThrows(IllegalArgumentException.class, () -> broadPhase.getFirst(1));
        assertThrows(IllegalArgumentException.class, () -> broadPhase.getSecond(1));
        assertEquals(2, broadPhase.getBoxCount());
        assertEquals(1, broadPhase.findPairs());
    }

    /**
     * Boxes far away from the origin, at saturated cell coordinates, are still compared exactly.
     */
    @Test
    void testFarAway()
    {
        final BroadPhase broadPhase = new BroadPhase(1.0, 1);
        broadPhase.add(new Box(1.0e12, -1.0e12, 1.0, 1.0));
        broadPhase.add(new Box(1.0e12 + 0.5, -1.0e12, 1.0, 1.0));
        broadPhase.add(new Box(1.0e12 + 5.0, -1.0e12, 1.0, 1.0));

        assertEquals(1, broadPhase.findPairs());
        assertFalse(found(broadPhase).contains("0-2"));
        assertTrue(found(broadPhase).contains("0-1"));
    }

    /**
     * Creates random boxes: centers in [-20, 20) m, a quarter on cell edges of 1 m, sizes in [0, 3) m.
     *
     * @param random The random source.
     * @param count The number of boxes.
     * @return The boxes.
     */
    private static Box[] randomBoxes(RandomSource random, int count)
    {
        final Box[] boxes = new Box[count];
        for (int i = 0; i < count; i++)
        {
            double x = random.nextDouble() * 40.0 - 20.0;
            double y = random.nextDouble() * 40.0 - 20.0;
            if (random.nextInt(4) == 0)
            {
                x = Math.floor(x);
                y = Math.floor(y);
            }
            boxes[i] = new Box(x, y, random.nextDouble() * 3.0, random.nextDouble() * 3.0);
        }
        return boxes;
    }

    /**
     * Compares every pair of boxes.
     *
     * @param boxes The boxes.
     * @return The overlapping pairs, as {@code "first-second"}.
     */
    private static Set<String> bruteForce(Box[] boxes)
    {
        final Set<String> pairs = new TreeSet<>();
        for (int i = 0; i < boxes.length; i++)
        {
            for (int j = i + 1; j < boxes.length; j++)
            {
                if (boxes[i].overlaps(boxes[j]))
                {
                    pairs.add(i + "-" + j);
                }
            }
        }
        return pairs;
    }

    /**
     * Returns the pairs found, checking that each is ordered and reported once.
     *
     * @param broadPhase The broad phase, after {@link BroadPhase#findPairs()}.
     * @return The pairs, as {@code "first-second"}.
     */
    private static Set<String> found(BroadPhase broadPhase)
    {
        final Set<String> pairs = new TreeSet<>();
        for (int p = 0; p < broadPhase.getPairCount(); p++)
        {
            final int first = broadPhase.getFirst(p);
            final int second = broadPhase.getSecond(p);
            assertTrue(first < second, () -> first + " >= " + second);
            assertTrue(pairs.add(first + "-" + second), () -> "Twice: " + first + "-" + second);
        }
        return pairs;
    }

    /**
     * Adds boxes after a clear and returns the pairs in their order.
     *
     * @param broadPhase The broad phase.
     * @param boxes The boxes.
     * @return The pairs, first and second interleaved.
     */
    private static int[] pairsInOrder(BroadPhase broadPhase, Box[] boxes)
    {
        broadPhase.clear();
        for (final Box box : boxes)
        {
            broadPhase.add(box);
        }
        final int[] order = new int[broadPhase.findPairs() * 2];
        for (int p = 0; p < broadPhase.getPairCount(); p++)
        {
            order[2 * p] = broadPhase.getFirst(p);
            order[2 * p + 1] = broadPhase.getSecond(p);
        }
        return order;
    }
}
