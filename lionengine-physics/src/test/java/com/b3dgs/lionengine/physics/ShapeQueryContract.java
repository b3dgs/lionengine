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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.management.ManagementFactory;

import org.junit.jupiter.api.Test;

import com.sun.management.ThreadMXBean;

/**
 * Contract suite of {@link ShapeQuery} (REQ-PHYS-001, REQ-MAP-008, ADR-0045, ADR-0082, ADR-0083).
 *
 * <p>
 * Published in the {@code physics} test JAR: every shape provider binds it by extending this class and creating a
 * provider holding given solid shapes, so that all providers honor the same contract (first {@link BoxListQuery}, then
 * the tile map of {@code tilemap}). The shapes of the suite are tiles of 1 m on a grid whose origin is {@code (0, 0)}:
 * boxes of size {@code 1 x 1} centered on {@code (column + 0.5, row + 0.5)}, columns and rows in [0, 8), so a tile
 * map of 1 m per tile placed at the origin represents them.
 * </p>
 *
 * <p>
 * The suite checks the found shapes by the strict overlap of {@link Box#overlaps(Box)} (touching is not found), the
 * written boxes, the count returned beyond the buffer, the boxes left unchanged after the written count, the null
 * arguments, an order that depends only on the content and the area, and that a query allocates nothing (JVM only,
 * per-thread allocation counter, 1 KiB tolerance over {@value #QUERIES} queries).
 * </p>
 */
public abstract class ShapeQueryContract
{
    /** Queries of the allocation measure. */
    private static final int QUERIES = 10_000;
    /** Allocation tolerance, in bytes. */
    private static final long ALLOCATION_TOLERANCE = 1024L;
    /** A marker value no shape of the suite has. */
    private static final double MARKER = 99.0;

    /**
     * Creates a provider holding solid tiles.
     *
     * @param solids The solid tiles: boxes of {@code 1 x 1} centered on {@code (column + 0.5, row + 0.5)}, columns and
     *            rows in [0, 8), each at most once; not kept.
     * @return The provider.
     */
    protected abstract ShapeQuery create(Box... solids);

    /**
     * Returns a tile of the suite.
     *
     * @param column The column, in [0, 8).
     * @param row The row, in [0, 8).
     * @return The box of the tile.
     */
    protected static Box tile(int column, int row)
    {
        return new Box(column + 0.5, row + 0.5, 1.0, 1.0);
    }

    /**
     * Returns a buffer of boxes holding the marker.
     *
     * @param length The length.
     * @return The buffer.
     */
    private static Box[] buffer(int length)
    {
        final Box[] boxes = new Box[length];
        for (int i = 0; i < length; i++)
        {
            boxes[i] = new Box(MARKER, MARKER, 1.0, 1.0);
        }
        return boxes;
    }

    /**
     * Checks that the written boxes are exactly the expected tiles, in any order, and that the rest of the buffer is
     * unchanged.
     *
     * @param results The buffer.
     * @param count The written count.
     * @param expected The expected tiles.
     */
    private static void assertTiles(Box[] results, int count, Box... expected)
    {
        assertEquals(expected.length, count);
        for (final Box tile : expected)
        {
            boolean found = false;
            for (int i = 0; i < count; i++)
            {
                found |= results[i].getX() == tile.getX()
                         && results[i].getY() == tile.getY()
                         && results[i].getWidth() == tile.getWidth()
                         && results[i].getHeight() == tile.getHeight();
            }
            assertTrue(found, () -> "Tile not found at " + tile.getX() + ", " + tile.getY());
        }
        for (int i = count; i < results.length; i++)
        {
            assertEquals(MARKER, results[i].getX());
        }
    }

    /**
     * Queries an area with a buffer of six boxes and checks the found tiles.
     *
     * @param query The query.
     * @param area The area.
     * @param expected The expected tiles.
     */
    private static void assertFound(ShapeQuery query, Box area, Box... expected)
    {
        final Box[] results = buffer(6);
        assertTiles(results, query.query(area, results), expected);
    }

    /**
     * An empty provider finds nothing.
     */
    @Test
    void testEmpty()
    {
        final ShapeQuery query = create();
        final Box[] results = buffer(2);

        assertTiles(results, query.query(new Box(4.0, 4.0, 8.0, 8.0), results));
    }

    /**
     * The overlapping tiles are found and written; the other ones are not.
     */
    @Test
    void testOverlapping()
    {
        final ShapeQuery query = create(tile(0, 0), tile(1, 0), tile(2, 0), tile(1, 1), tile(5, 5));

        assertFound(query, new Box(1.5, 0.5, 1.5, 0.5), tile(0, 0), tile(1, 0), tile(2, 0));
        assertFound(query, new Box(1.5, 1.0, 0.5, 0.5), tile(1, 0), tile(1, 1));
        assertFound(query, new Box(5.5, 5.5, 0.1, 0.1), tile(5, 5));
        assertFound(query, new Box(4.0, 4.0, 8.0, 8.0), tile(0, 0), tile(1, 0), tile(2, 0), tile(1, 1), tile(5, 5));
    }

    /**
     * A tile that only touches the area by an edge or a corner is not found, on each side.
     */
    @Test
    void testTouching()
    {
        final ShapeQuery query = create(tile(3, 3));
        final Box[] results = buffer(1);

        assertTiles(results, query.query(new Box(3.5, 2.5, 1.0, 1.0), results));
        assertTiles(results, query.query(new Box(3.5, 4.5, 1.0, 1.0), results));
        assertTiles(results, query.query(new Box(2.5, 3.5, 1.0, 1.0), results));
        assertTiles(results, query.query(new Box(4.5, 3.5, 1.0, 1.0), results));
        assertTiles(results, query.query(new Box(2.5, 2.5, 1.0, 1.0), results));
        assertTiles(results, query.query(new Box(4.75, 4.75, 1.5, 1.5), results));
        assertTiles(results, query.query(new Box(3.5, 2.5, 1.0, 1.000001), results), tile(3, 3));
    }

    /**
     * The count is returned even beyond the buffer; the buffer receives that many found tiles; an empty buffer
     * receives none.
     */
    @Test
    void testCountBeyondBuffer()
    {
        final ShapeQuery query = create(tile(0, 0), tile(1, 0), tile(2, 0), tile(3, 0));
        final Box area = new Box(2.0, 0.5, 4.0, 0.5);
        final Box[] small = buffer(2);
        final Box[] full = buffer(4);

        assertEquals(4, query.query(area, small));
        assertEquals(4, query.query(area, full));
        assertEquals(4, query.query(area, new Box[0]));
        for (int i = 0; i < small.length; i++)
        {
            assertEquals(full[i].getX(), small[i].getX());
            assertEquals(full[i].getY(), small[i].getY());
        }
    }

    /**
     * The order depends only on the content and the area: the same query twice gives the same boxes in the same order.
     */
    @Test
    void testOrder()
    {
        final ShapeQuery query = create(tile(2, 1), tile(0, 0), tile(1, 1), tile(2, 0), tile(0, 1));
        final Box area = new Box(1.5, 1.0, 3.0, 2.0);
        final Box[] first = buffer(5);
        final Box[] second = buffer(5);

        assertEquals(5, query.query(area, first));
        assertEquals(5, query.query(area, second));
        for (int i = 0; i < first.length; i++)
        {
            assertEquals(first[i].getX(), second[i].getX());
            assertEquals(first[i].getY(), second[i].getY());
        }
    }

    /**
     * The provider keeps neither the area nor the buffer: changing them after a query changes no later result.
     */
    @Test
    void testKeepsNothing()
    {
        final ShapeQuery query = create(tile(1, 1));
        final Box area = new Box(1.5, 1.5, 0.5, 0.5);
        final Box[] results = buffer(1);
        assertEquals(1, query.query(area, results));

        area.set(6.5, 6.5, 0.5, 0.5);
        results[0].set(MARKER, MARKER, 1.0, 1.0);
        final Box[] other = buffer(1);

        assertTiles(other, query.query(new Box(1.5, 1.5, 0.5, 0.5), other), tile(1, 1));
        assertEquals(MARKER, results[0].getX());
    }

    /**
     * A {@code null} argument is refused.
     */
    @Test
    void testNull()
    {
        final ShapeQuery query = create(tile(0, 0));
        final Box[] results = buffer(1);
        final Box area = new Box(0.5, 0.5, 1.0, 1.0);

        assertThrows(IllegalArgumentException.class, () -> query.query(null, results));
        assertThrows(IllegalArgumentException.class, () -> query.query(area, null));
    }

    /**
     * Queries allocate nothing.
     */
    @Test
    void testAllocatesNothing()
    {
        final ThreadMXBean bean = (ThreadMXBean) ManagementFactory.getThreadMXBean();
        final ShapeQuery query = create(tile(0, 0), tile(1, 0), tile(2, 0), tile(1, 1));
        final Box area = new Box(1.5, 0.75, 2.0, 1.0);
        final Box[] results = buffer(4);
        int found = query.query(area, results);

        final long before = bean.getCurrentThreadAllocatedBytes();
        for (int i = 0; i < QUERIES; i++)
        {
            found += query.query(area, results);
        }
        final long allocated = bean.getCurrentThreadAllocatedBytes() - before;

        final int total = found;
        assertTrue(allocated < ALLOCATION_TOLERANCE, () -> "Allocated bytes: " + allocated);
        assertEquals(4 * (QUERIES + 1), total);
    }
}
