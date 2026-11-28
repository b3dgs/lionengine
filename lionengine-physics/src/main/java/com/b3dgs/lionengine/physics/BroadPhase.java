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

import java.util.Arrays;

/**
 * The uniform-grid broad phase: finds the overlapping pairs among a set of boxes (REQ-PHYS-001, REQ-STD-005,
 * ADR-0021, ADR-0030, ADR-0034, ADR-0082).
 *
 * <p>
 * <b>Use.</b> Each step the owner calls {@link #clear()}, adds every box with {@link #add(Box)}, which copies its
 * bounds and gives it an index (0, then consecutive), then calls {@link #findPairs()}, and reads the pairs by
 * {@link #getFirst(int)} and {@link #getSecond(int)}: the indices of two boxes that overlap by the test of
 * {@link Box#overlaps(Box)} (touching edges and corners excluded), the first lower than the second, each pair once.
 * Plain data: no ECS, tile map or platform type; the owner maps the indices to its objects.
 * </p>
 *
 * <p>
 * <b>Grid.</b> The plane is cut into square cells of {@link #getCellSize()} meters, cell {@code (i, j)} covering
 * {@code [i * cellSize, (i + 1) * cellSize)} on X and the same on Y; a box is entered in every cell its bounds cover.
 * The grid is unbounded: the cells are hashed into a table, so boxes may be anywhere, negative coordinates included.
 * Only the boxes sharing a cell are compared, and a pair sharing several cells is reported only by the cell holding the
 * lower-left corner of their overlap, so no pair is reported twice. The cell size is set per world by the collision
 * system, {@link #DEFAULT_CELL_SIZE} by default, to calibrate by benchmark (ADR-0030); a box larger than a cell is
 * entered in each of the cells it covers.
 * </p>
 *
 * <p>
 * <b>Order and determinism.</b> The order of the pairs depends only on the boxes added and the sequence of operations
 * since the creation of the broad phase, so it is the same on every platform; it is not sorted. Integer cell
 * coordinates, an integer hash and exact {@code double} comparisons, no identity hash.
 * </p>
 *
 * <p>
 * <b>Capacity and memory.</b> The broad phase holds the given number of boxes, four cell entries per box and as many
 * pairs as boxes without growing; each table doubles when an addition or a pair needs more, a documented warm-up cost,
 * and never shrinks. Once the capacities fit the busiest step, {@link #clear()}, {@link #add(Box)} and
 * {@link #findPairs()} allocate nothing. Memory per box of capacity: 32 bytes of bounds, four cell entries of 16 bytes,
 * at most eight hash buckets of 8 bytes (one or two buckets per entry) and one pair of 8 bytes, at most 168 bytes; at
 * most {@link #MAX_ENTRIES} cell entries.
 * </p>
 *
 * <p>
 * Cost: {@code O(n + e + k)} for {@code n} boxes, {@code e} cell entries and {@code k} candidate pairs sharing a cell,
 * against {@code O(n²)} for comparing every pair.
 * </p>
 *
 * <p>
 * Not thread-safe: owned by one collision system and used on the runtime thread only.
 * </p>
 *
 * @since 11.0.0
 */
public final class BroadPhase
{
    /** The default cell size, in meters (ADR-0030, decision 6). */
    public static final double DEFAULT_CELL_SIZE = 2.0;
    /** The highest number of cell entries. */
    public static final int MAX_ENTRIES = 1 << 30;

    /** No entry. */
    private static final int NONE = -1;
    /** Initial cell entries per box of capacity. */
    private static final int ENTRIES_PER_BOX = 4;
    /** Hash multiplier of the cell column (golden ratio). */
    private static final int HASH_X = 0x9E37_79B1;
    /** Hash multiplier of the cell row. */
    private static final int HASH_Y = 0x85EB_CA77;
    /** Shift of the final hash mix. */
    private static final int HASH_SHIFT = 16;

    /** The cell size, in meters. */
    private final double cellSize;
    /** Left bounds, per box. */
    private double[] minX;
    /** Bottom bounds, per box. */
    private double[] minY;
    /** Right bounds, per box. */
    private double[] maxX;
    /** Top bounds, per box. */
    private double[] maxY;
    /** Number of boxes. */
    private int boxCount;
    /** Box index, per entry. */
    private int[] entryBox;
    /** Cell column, per entry. */
    private int[] entryColumn;
    /** Cell row, per entry. */
    private int[] entryRow;
    /** Next entry of the same bucket, per entry, {@link #NONE} at the end. */
    private int[] entryNext;
    /** Number of entries. */
    private int entryCount;
    /** First entry, per bucket, {@link #NONE} if empty. */
    private int[] heads;
    /** The non-empty buckets, in order of first use. */
    private int[] used;
    /** Number of non-empty buckets. */
    private int usedCount;
    /** First box index, per pair. */
    private int[] first;
    /** Second box index, per pair. */
    private int[] second;
    /** Number of pairs. */
    private int pairCount;

    /**
     * Creates an empty broad phase.
     *
     * @param cellSize The cell size, in meters, positive and finite ({@link #DEFAULT_CELL_SIZE} by default).
     * @param capacity The number of boxes held without growing, positive.
     * @throws IllegalArgumentException If the cell size is not positive and finite, or the capacity is not positive
     *             or above {@code MAX_ENTRIES / 4}.
     */
    public BroadPhase(double cellSize, int capacity)
    {
        super();
        if (!(cellSize > 0.0 && cellSize < Double.POSITIVE_INFINITY))
        {
            throw new IllegalArgumentException("Cell size is not positive and finite: " + cellSize);
        }
        if (capacity < 1 || capacity > MAX_ENTRIES / ENTRIES_PER_BOX)
        {
            throw new IllegalArgumentException("Broad phase capacity out of range: " + capacity);
        }
        this.cellSize = cellSize;
        minX = new double[capacity];
        minY = new double[capacity];
        maxX = new double[capacity];
        maxY = new double[capacity];
        first = new int[capacity];
        second = new int[capacity];
        allocateEntries(capacity * ENTRIES_PER_BOX);
    }

    /**
     * Returns the cell size.
     *
     * @return The side of a cell, in meters.
     */
    public double getCellSize()
    {
        return cellSize;
    }

    /**
     * Removes every box and every pair, keeping the capacities.
     */
    public void clear()
    {
        for (int i = 0; i < usedCount; i++)
        {
            heads[used[i]] = NONE;
        }
        usedCount = 0;
        entryCount = 0;
        boxCount = 0;
        pairCount = 0;
    }

    /**
     * Adds a box: copies its bounds and enters it in every cell they cover.
     *
     * @param box The box, read only and not kept.
     * @return The index of the box, the number of boxes added before it since the last {@link #clear()}.
     * @throws IllegalArgumentException If the box is {@code null}.
     * @throws IllegalStateException If the box would bring the cell entries above {@link #MAX_ENTRIES}.
     */
    public int add(Box box)
    {
        if (box == null)
        {
            throw new IllegalArgumentException("Box is null");
        }
        final int column0 = cell(box.getMinX());
        final int column1 = cell(box.getMaxX());
        final int row0 = cell(box.getMinY());
        final int row1 = cell(box.getMaxY());
        final long columns = (long) column1 - column0 + 1L;
        final long rows = (long) row1 - row0 + 1L;
        if (entryCount + columns * rows > MAX_ENTRIES)
        {
            throw new IllegalStateException("Broad phase above " + MAX_ENTRIES + " cell entries: " + box);
        }
        if (boxCount == minX.length)
        {
            growBoxes();
        }
        final int index = boxCount;
        minX[index] = box.getMinX();
        minY[index] = box.getMinY();
        maxX[index] = box.getMaxX();
        maxY[index] = box.getMaxY();
        boxCount++;
        // Counted from the first cell, so that a range ending at Integer.MAX_VALUE does not overflow.
        for (int c = 0; c < columns; c++)
        {
            for (int r = 0; r < rows; r++)
            {
                enter(index, column0 + c, row0 + r);
            }
        }
        return index;
    }

    /**
     * Returns the number of boxes.
     *
     * @return The boxes added since the last {@link #clear()}.
     */
    public int getBoxCount()
    {
        return boxCount;
    }

    /**
     * Finds the overlapping pairs among the boxes added since the last {@link #clear()}, replacing the previous pairs.
     *
     * @return The number of pairs found, also {@link #getPairCount()}.
     */
    public int findPairs()
    {
        pairCount = 0;
        for (int i = 0; i < usedCount; i++)
        {
            scan(heads[used[i]]);
        }
        return pairCount;
    }

    /**
     * Returns the number of pairs.
     *
     * @return The pairs of the last {@link #findPairs()}, {@code 0} after {@link #clear()}.
     */
    public int getPairCount()
    {
        return pairCount;
    }

    /**
     * Returns the first box of a pair.
     *
     * @param pair The pair, in [0, {@link #getPairCount()}).
     * @return The lower box index of the pair.
     * @throws IllegalArgumentException If the pair is outside its range.
     */
    public int getFirst(int pair)
    {
        checkPair(pair);
        return first[pair];
    }

    /**
     * Returns the second box of a pair.
     *
     * @param pair The pair, in [0, {@link #getPairCount()}).
     * @return The higher box index of the pair.
     * @throws IllegalArgumentException If the pair is outside its range.
     */
    public int getSecond(int pair)
    {
        checkPair(pair);
        return second[pair];
    }

    /**
     * Returns the cell coordinate of a value.
     *
     * @param value The value, in meters, finite.
     * @return The floor of {@code value / cellSize}, saturated to the {@code int} range.
     */
    private int cell(double value)
    {
        return (int) Math.floor(value / cellSize);
    }

    /**
     * Returns the bucket of a cell.
     *
     * @param column The cell column.
     * @param row The cell row.
     * @return The bucket index, in the table.
     */
    private int bucket(int column, int row)
    {
        final int hash = column * HASH_X + row * HASH_Y;
        return (hash ^ (hash >>> HASH_SHIFT)) & (heads.length - 1);
    }

    /**
     * Enters a box in a cell.
     *
     * @param box The box index.
     * @param column The cell column.
     * @param row The cell row.
     */
    private void enter(int box, int column, int row)
    {
        if (entryCount == entryBox.length)
        {
            growEntries();
        }
        final int entry = entryCount;
        entryBox[entry] = box;
        entryColumn[entry] = column;
        entryRow[entry] = row;
        entryCount++;
        link(entry);
    }

    /**
     * Links an entry at the head of its bucket.
     *
     * @param entry The entry.
     */
    private void link(int entry)
    {
        final int bucket = bucket(entryColumn[entry], entryRow[entry]);
        if (heads[bucket] == NONE)
        {
            used[usedCount] = bucket;
            usedCount++;
        }
        entryNext[entry] = heads[bucket];
        heads[bucket] = entry;
    }

    /**
     * Compares the entries of a bucket that share a cell.
     *
     * @param head The first entry of the bucket.
     */
    private void scan(int head)
    {
        for (int e = head; e != NONE; e = entryNext[e])
        {
            for (int f = entryNext[e]; f != NONE; f = entryNext[f])
            {
                if (entryColumn[e] == entryColumn[f] && entryRow[e] == entryRow[f])
                {
                    test(entryBox[e], entryBox[f], entryColumn[e], entryRow[e]);
                }
            }
        }
    }

    /**
     * Tests two boxes sharing a cell, and keeps the pair if they overlap and the cell holds the lower-left corner of
     * their overlap.
     *
     * @param a The first box index.
     * @param b The second box index.
     * @param column The shared cell column.
     * @param row The shared cell row.
     */
    private void test(int a, int b, int column, int row)
    {
        if (Box.overlaps(minX[a], maxX[a], minX[b], maxX[b])
            && Box.overlaps(minY[a], maxY[a], minY[b], maxY[b])
            && cell(Math.max(minX[a], minX[b])) == column
            && cell(Math.max(minY[a], minY[b])) == row)
        {
            if (pairCount == first.length)
            {
                growPairs();
            }
            first[pairCount] = Math.min(a, b);
            second[pairCount] = Math.max(a, b);
            pairCount++;
        }
    }

    /**
     * Checks a pair position.
     *
     * @param pair The pair.
     * @throws IllegalArgumentException If the pair is outside [0, {@link #getPairCount()}).
     */
    private void checkPair(int pair)
    {
        if (pair < 0 || pair >= pairCount)
        {
            throw new IllegalArgumentException("Pair outside [0, " + pairCount + "): " + pair);
        }
    }

    /**
     * Allocates the entries and the table, empty.
     *
     * @param capacity The entry capacity.
     */
    private void allocateEntries(int capacity)
    {
        entryBox = new int[capacity];
        entryColumn = new int[capacity];
        entryRow = new int[capacity];
        entryNext = new int[capacity];
        final int buckets;
        if (capacity >= MAX_ENTRIES)
        {
            buckets = MAX_ENTRIES;
        }
        else
        {
            buckets = Integer.highestOneBit(capacity) << 1;
        }
        heads = new int[buckets];
        Arrays.fill(heads, NONE);
        used = new int[buckets];
        usedCount = 0;
    }

    /**
     * Doubles the box capacity, keeping the boxes.
     */
    private void growBoxes()
    {
        final int capacity = minX.length * 2;
        minX = Arrays.copyOf(minX, capacity);
        minY = Arrays.copyOf(minY, capacity);
        maxX = Arrays.copyOf(maxX, capacity);
        maxY = Arrays.copyOf(maxY, capacity);
    }

    /**
     * Doubles the entry capacity and the table, keeping the entries, relinked in their order.
     */
    private void growEntries()
    {
        final int[] boxes = entryBox;
        final int[] columns = entryColumn;
        final int[] rows = entryRow;
        allocateEntries(Math.min(boxes.length * 2, MAX_ENTRIES));
        System.arraycopy(boxes, 0, entryBox, 0, entryCount);
        System.arraycopy(columns, 0, entryColumn, 0, entryCount);
        System.arraycopy(rows, 0, entryRow, 0, entryCount);
        for (int entry = 0; entry < entryCount; entry++)
        {
            link(entry);
        }
    }

    /**
     * Doubles the pair capacity, keeping the pairs.
     */
    private void growPairs()
    {
        final int capacity = first.length * 2;
        first = Arrays.copyOf(first, capacity);
        second = Arrays.copyOf(second, capacity);
    }
}
