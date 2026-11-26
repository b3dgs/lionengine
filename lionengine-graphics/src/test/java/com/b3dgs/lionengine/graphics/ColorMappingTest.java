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
package com.b3dgs.lionengine.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import com.sun.management.ThreadMXBean;

/**
 * Test {@link ColorMapping}: exact key colors replaced by target colors, entries replaced and removed, the order of
 * the entries, a single pass over the source colors, equality and the canonical text (REQ-SPRITE-005, ADR-0026,
 * ADR-0057).
 */
final class ColorMappingTest
{
    /** Magenta, the key color of the team. */
    private static final int MAGENTA = 0xFFFF00FF;
    /** Red, a team color. */
    private static final int RED = 0xFFFF0000;
    /** Blue, a team color. */
    private static final int BLUE = 0xFF0000FF;
    /** Pixels mapped per allocation measurement. */
    private static final int ITERATIONS = 100_000;
    /** Bytes tolerated for the measurement itself: far below one allocation per call. */
    private static final long ALLOCATION_TOLERANCE = 1024L;

    /**
     * The empty mapping has no entry and changes no pixel.
     */
    @Test
    void testNone()
    {
        assertTrue(ColorMapping.NONE.isIdentity());
        assertEquals(0, ColorMapping.NONE.size());
        assertEquals(MAGENTA, ColorMapping.NONE.apply(MAGENTA));
        assertEquals("mapping()", ColorMapping.NONE.toString());
    }

    /**
     * A pixel equal to a key color on its four channels becomes the target color, alpha included; any other pixel,
     * the same color with another alpha included, is kept.
     */
    @Test
    void testExactMatch()
    {
        final ColorMapping mapping = ColorMapping.NONE.with(MAGENTA, RED).with(0x80FF00FF, 0x40FF0000);

        assertEquals(RED, mapping.apply(MAGENTA));
        assertEquals(0x40FF0000, mapping.apply(0x80FF00FF));
        assertEquals(0x7FFF00FF, mapping.apply(0x7FFF00FF));
        assertEquals(0xFFFE00FF, mapping.apply(0xFFFE00FF));
        assertEquals(0x00000000, mapping.apply(0x00000000));
        assertEquals(BLUE, mapping.apply(BLUE));
    }

    /**
     * Each pixel is mapped once, from the source colors: a key color that is also a target does not chain, so two
     * colors can be swapped.
     */
    @Test
    void testSinglePass()
    {
        final ColorMapping swap = ColorMapping.NONE.with(RED, BLUE).with(BLUE, RED);
        final ColorMapping chain = ColorMapping.NONE.with(MAGENTA, RED).with(RED, BLUE);

        assertEquals(BLUE, swap.apply(RED));
        assertEquals(RED, swap.apply(BLUE));
        assertEquals(RED, chain.apply(MAGENTA));
        assertEquals(BLUE, chain.apply(RED));
    }

    /**
     * A new entry for a key color replaces its target; a key color mapped to itself removes its entry and is never
     * kept; the mappings are immutable.
     */
    @Test
    void testReplaceAndRemove()
    {
        final ColorMapping red = ColorMapping.NONE.with(MAGENTA, RED);
        final ColorMapping blue = red.with(MAGENTA, BLUE);
        final ColorMapping removed = blue.with(MAGENTA, MAGENTA);

        assertEquals(RED, red.apply(MAGENTA));
        assertEquals(BLUE, blue.apply(MAGENTA));
        assertEquals(1, blue.size());
        assertEquals(ColorMapping.NONE, removed);
        assertTrue(removed.isIdentity());
        assertSame(red, red.with(BLUE, BLUE));
        assertEquals(ColorMapping.NONE, ColorMapping.NONE.with(RED, BLUE).with(BLUE, RED).with(RED, RED).with(BLUE,
                                                                                                             BLUE));
    }

    /**
     * Entries are kept by ascending key color read as an unsigned number, whatever the order they were given in, so
     * mappings of the same entries are equal and have the same text.
     */
    @Test
    void testOrderAndEquality()
    {
        final ColorMapping first = ColorMapping.NONE.with(0x80FF00FF, 0x40FF0000).with(MAGENTA, RED).with(0x00000001,
                                                                                                           BLUE);
        final ColorMapping second = ColorMapping.NONE.with(MAGENTA, RED).with(0x00000001, BLUE).with(0x80FF00FF,
                                                                                                       0x40FF0000);

        assertEquals(3, first.size());
        assertEquals(0x00000001, first.getKey(0));
        assertEquals(0x80FF00FF, first.getKey(1));
        assertEquals(MAGENTA, first.getKey(2));
        assertEquals(BLUE, first.getTarget(0));
        assertEquals(RED, first.getTarget(2));
        assertEquals(first, second);
        assertEquals(first.hashCode(), second.hashCode());
        assertEquals(first.toString(), second.toString());
        assertNotEquals(first, first.with(MAGENTA, BLUE));
        assertNotEquals(first, first.with(BLUE, RED));
        assertEquals("mapping(#00000001=#FF0000FF,#80FF00FF=#40FF0000,#FFFF00FF=#FFFF0000)", first.toString());
    }

    /**
     * An entry index outside the mapping is refused.
     *
     * @param index The invalid index.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        -1, 1
    })
    void testIndexOutside(int index)
    {
        final ColorMapping mapping = ColorMapping.NONE.with(MAGENTA, RED);

        assertThrows(IndexOutOfBoundsException.class, () -> mapping.getKey(index));
        assertThrows(IndexOutOfBoundsException.class, () -> mapping.getTarget(index));
    }

    /**
     * Mapping a pixel allocates nothing.
     */
    @Test
    void testApplyAllocatesNothing()
    {
        ColorMapping mapping = ColorMapping.NONE;
        for (int i = 0; i < 16; i++)
        {
            mapping = mapping.with(0xFF000000 | i, 0xFF000000 | i << 8);
        }
        final ColorMapping colors = mapping;
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        int sum = 0;

        final long before = bean.getCurrentThreadAllocatedBytes();
        for (int i = 0; i < ITERATIONS; i++)
        {
            sum += colors.apply(0xFF000000 | i & 0x1F);
        }
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        assertTrue(allocated < ALLOCATION_TOLERANCE, "Allocated " + allocated + " bytes");
        assertNotEquals(0, sum);
    }
}
