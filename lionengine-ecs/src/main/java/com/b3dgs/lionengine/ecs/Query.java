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

import java.util.Arrays;

/**
 * A cached view of the objects of a world that have all the trait types of an include set and none of an exclude set
 * (REQ-ECS-005, REQ-ECS-006, REQ-ECS-012, REQ-ECS-013, ADR-0011, ADR-0030, ADR-0034, ADR-0076).
 *
 * <p>
 * Created by {@link World#createQuery(TraitStorage...)} or {@link World#createQuery(TraitStorage[], TraitStorage[])},
 * usually once at initialization, and kept by the system that iterates it. It lives as long as its world.
 * </p>
 *
 * <p>
 * <b>Content.</b> The query holds the IDs of its matching objects in a dense list, filled at its creation from the
 * state of the last flush, then updated incrementally by each {@link World#flush()}: an object joins when it gains the
 * last missing included trait or loses the last excluded one, and leaves when it loses an included trait, gains an
 * excluded one, or is removed. Replacing a trait changes nothing. Pending objects and objects removed by a flush are
 * never listed.
 * </p>
 *
 * <p>
 * <b>Iteration.</b> Iterate with an index loop from {@code 0} to {@link #getCount()} over {@link #getId(int)}: it
 * allocates nothing. The list changes only at the flush, so structural changes requested inside the loop
 * ({@link World#create()}, {@link World#remove(long)}, {@link TraitStorage#add(long)},
 * {@link TraitStorage#remove(long)}) are recorded and the iteration stays valid: every object listed at the start is
 * visited once, even if its removal is requested. Do not call {@link World#flush()} inside the loop. The order is
 * deterministic for the same sequence of operations, but it is not the creation order and a removal reorders it
 * (swap-remove): game logic must not depend on it. {@link #copySortedIds(long[])} gives the IDs in ascending order
 * when an order matters.
 * </p>
 *
 * <p>
 * <b>Memory.</b> A query holds a {@code long} and an {@code int} per object index of its world (12 bytes per object of
 * the object capacity), grown with the object table, never shrunk; the flush updates it without allocating.
 * </p>
 *
 * <p>
 * Not thread-safe: used on the runtime thread only, with its world (ADR-0018).
 * </p>
 *
 * @since 11.0.0
 */
public final class Query
{
    /** Dense position of an object index outside the query. */
    private static final int NONE = -1;
    /** Shift from a type ID to its mask word. */
    private static final int WORD_SHIFT = 6;

    /** Included types, per mask word. */
    private final long[] include;
    /** Excluded types, per mask word. */
    private final long[] exclude;
    /** Included and excluded types, per mask word. */
    private final long[] involved;
    /** Matching object IDs, packed. */
    private long[] ids;
    /** Dense position per object index, {@link #NONE} outside the query. */
    private int[] positions;
    /** Number of matching objects. */
    private int count;

    /**
     * Creates an empty query.
     *
     * @param include The included types, per mask word.
     * @param exclude The excluded types, per mask word, as many words.
     * @param objectCapacity The object capacity of the world, positive.
     */
    Query(long[] include, long[] exclude, int objectCapacity)
    {
        this.include = include;
        this.exclude = exclude;
        involved = new long[include.length];
        for (int w = 0; w < include.length; w++)
        {
            involved[w] = include[w] | exclude[w];
        }
        ids = new long[objectCapacity];
        positions = new int[objectCapacity];
        Arrays.fill(positions, NONE);
    }

    /**
     * Returns the number of matching objects.
     *
     * @return The objects of the query, as of the last flush.
     */
    public int getCount()
    {
        return count;
    }

    /**
     * Returns the ID of a matching object.
     *
     * @param position The position in the query, in [0, {@link #getCount()}).
     * @return The object ID, alive.
     * @throws IllegalArgumentException If the position is outside its range.
     */
    public long getId(int position)
    {
        if (position < 0 || position >= count)
        {
            throw new IllegalArgumentException("Query position outside [0, " + count + "): " + position);
        }
        return ids[position];
    }

    /**
     * Copies the IDs of the matching objects in ascending order into a buffer provided by the caller.
     *
     * <p>
     * The explicit sort of ADR-0030 and ADR-0034, for an order that does not depend on the operation sequence. The
     * first {@link #getCount()} elements of the buffer receive the IDs, the others are left unchanged. Sorted in place
     * (heapsort), allocates nothing; costs {@code O(n log n)} for {@code n} IDs.
     * </p>
     *
     * @param buffer The buffer, at least {@link #getCount()} long.
     * @return The number of IDs copied, {@link #getCount()}.
     * @throws IllegalArgumentException If the buffer is {@code null} or shorter than {@link #getCount()}; the buffer
     *             is left unchanged.
     */
    public int copySortedIds(long[] buffer)
    {
        if (buffer == null)
        {
            throw new IllegalArgumentException("ID buffer is null");
        }
        if (buffer.length < count)
        {
            throw new IllegalArgumentException("ID buffer of length " + buffer.length + " for " + count + " IDs");
        }
        System.arraycopy(ids, 0, buffer, 0, count);
        sort(buffer, count);
        return count;
    }

    /**
     * Tells whether a trait type is included or excluded, so that a change of it may change the query.
     *
     * @param typeId The type ID.
     * @return {@code true} if the type is in the include or the exclude set.
     */
    boolean involves(int typeId)
    {
        final int word = typeId >>> WORD_SHIFT;
        return word < involved.length && (involved[word] & 1L << typeId) != 0L;
    }

    /**
     * Updates the membership of an object after a change of its mask, at the flush or at the creation of the query.
     *
     * @param masks The trait masks.
     * @param objectId The object ID, alive.
     * @param index The object index.
     */
    void update(TraitMasks masks, long objectId, int index)
    {
        final int position = positions[index];
        if (matches(masks, index))
        {
            if (position == NONE)
            {
                ids[count] = objectId;
                positions[index] = count;
                count++;
            }
        }
        else if (position != NONE)
        {
            count--;
            if (position != count)
            {
                final long moved = ids[count];
                ids[position] = moved;
                positions[ObjectTable.index(moved)] = position;
            }
            positions[index] = NONE;
        }
    }

    /**
     * Grows the per-object arrays to an object capacity, at the flush or by {@link World#reserve(int)}. Never shrinks.
     *
     * @param objectCapacity The object capacity.
     */
    void growObjects(int objectCapacity)
    {
        final int old = positions.length;
        if (objectCapacity > old)
        {
            final long[] grownIds = new long[objectCapacity];
            System.arraycopy(ids, 0, grownIds, 0, count);
            ids = grownIds;
            final int[] grown = new int[objectCapacity];
            System.arraycopy(positions, 0, grown, 0, old);
            Arrays.fill(grown, old, objectCapacity, NONE);
            positions = grown;
        }
    }

    /**
     * Tells whether a mask matches: every included bit set, no excluded bit set.
     *
     * @param masks The trait masks.
     * @param index The object index.
     * @return {@code true} if the object belongs to the query.
     */
    private boolean matches(TraitMasks masks, int index)
    {
        for (int w = 0; w < include.length; w++)
        {
            final long word = masks.getWord(index, w);
            if ((word & include[w]) != include[w] || (word & exclude[w]) != 0L)
            {
                return false;
            }
        }
        return true;
    }

    /**
     * Sorts the first elements of an array in ascending order, in place, without allocating (heapsort).
     *
     * @param values The array.
     * @param length The number of elements to sort.
     */
    static void sort(long[] values, int length)
    {
        for (int i = length / 2 - 1; i >= 0; i--)
        {
            siftDown(values, i, length);
        }
        for (int end = length - 1; end > 0; end--)
        {
            final long top = values[0];
            values[0] = values[end];
            values[end] = top;
            siftDown(values, 0, end);
        }
    }

    /**
     * Moves an element down a max-heap until its children are not greater.
     *
     * @param values The heap.
     * @param start The position of the element.
     * @param length The heap length.
     */
    private static void siftDown(long[] values, int start, int length)
    {
        final long value = values[start];
        int parent = start;
        int child = 2 * parent + 1;
        while (child < length)
        {
            if (child + 1 < length && values[child + 1] > values[child])
            {
                child++;
            }
            if (values[child] <= value)
            {
                break;
            }
            values[parent] = values[child];
            parent = child;
            child = 2 * parent + 1;
        }
        values[parent] = value;
    }
}
