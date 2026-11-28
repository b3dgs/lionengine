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

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.RandomSource;

/**
 * Tests of {@link PairList}: growth, comparison and the in-place sort by the lower ID, then the higher ID (ADR-0082).
 */
final class PairListTest
{
    /**
     * Random pairs added beyond the capacity are kept and sorted in ascending order; clearing keeps nothing.
     */
    @Test
    void testSort()
    {
        final RandomSource random = new RandomSource(7L);
        final PairList pairs = new PairList(1);
        for (int i = 0; i < 500; i++)
        {
            final long a = random.nextInt(50);
            pairs.add(a, a + 1 + random.nextInt(50));
        }

        pairs.sort();

        assertEquals(500, pairs.getCount());
        for (int i = 1; i < pairs.getCount(); i++)
        {
            final int pair = i;
            assertTrue(pairs.compare(pair - 1, pairs, pair) <= 0, () -> "Pair " + pair);
            assertTrue(pairs.getFirst(pair) < pairs.getSecond(pair));
        }

        pairs.clear();

        assertEquals(0, pairs.getCount());
    }

    /**
     * Pairs compare by their lower ID first, then by their higher ID, across lists.
     */
    @Test
    void testCompare()
    {
        final PairList one = new PairList(2);
        one.add(1L, 5L);
        one.add(2L, 3L);
        final PairList other = new PairList(2);
        other.add(1L, 6L);
        other.add(2L, 3L);

        assertTrue(one.compare(0, other, 0) < 0);
        assertTrue(other.compare(0, one, 0) > 0);
        assertTrue(one.compare(0, other, 1) < 0);
        assertEquals(0, one.compare(1, other, 1));
        assertEquals(1L, one.getFirst(0));
        assertEquals(5L, one.getSecond(0));
    }

    /**
     * An empty or single list sorts to itself.
     */
    @Test
    void testSortSmall()
    {
        final PairList pairs = new PairList(1);
        pairs.sort();
        pairs.add(3L, 4L);
        pairs.sort();

        assertEquals(1, pairs.getCount());
        assertEquals(3L, pairs.getFirst(0));
    }
}
