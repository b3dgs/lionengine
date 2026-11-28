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

import java.util.Arrays;

/**
 * A growable list of object ID pairs, sorted in place, for {@link CollisionSystem} (ADR-0082).
 *
 * <p>
 * Each pair is {@code (a, b)} with {@code a < b}. {@link #sort()} orders the pairs by {@code a}, then by {@code b}, by
 * heapsort on the parallel arrays: no allocation, {@code O(n log n)}, the same order on every platform. The arrays
 * double when an addition needs more, never shrink.
 * </p>
 */
final class PairList
{
    /** First IDs, per pair. */
    private long[] first;
    /** Second IDs, per pair. */
    private long[] second;
    /** Number of pairs. */
    private int count;

    /**
     * Creates an empty list.
     *
     * @param capacity The pairs held without growing, positive.
     */
    PairList(int capacity)
    {
        first = new long[capacity];
        second = new long[capacity];
    }

    /**
     * Removes every pair, keeping the capacity.
     */
    void clear()
    {
        count = 0;
    }

    /**
     * Adds a pair at the end.
     *
     * @param a The lower ID.
     * @param b The higher ID.
     */
    void add(long a, long b)
    {
        if (count == first.length)
        {
            first = Arrays.copyOf(first, count * 2);
            second = Arrays.copyOf(second, count * 2);
        }
        first[count] = a;
        second[count] = b;
        count++;
    }

    /**
     * Returns the number of pairs.
     *
     * @return The pairs.
     */
    int getCount()
    {
        return count;
    }

    /**
     * Returns the lower ID of a pair.
     *
     * @param pair The pair, in [0, {@link #getCount()}).
     * @return The lower ID.
     */
    long getFirst(int pair)
    {
        return first[pair];
    }

    /**
     * Returns the higher ID of a pair.
     *
     * @param pair The pair, in [0, {@link #getCount()}).
     * @return The higher ID.
     */
    long getSecond(int pair)
    {
        return second[pair];
    }

    /**
     * Compares a pair of this list with a pair of another list.
     *
     * @param pair The pair of this list.
     * @param other The other list.
     * @param otherPair The pair of the other list.
     * @return A negative number, zero or a positive number when the pair is before, equal to or after the other one.
     */
    int compare(int pair, PairList other, int otherPair)
    {
        final int result = Long.compare(first[pair], other.first[otherPair]);
        if (result != 0)
        {
            return result;
        }
        return Long.compare(second[pair], other.second[otherPair]);
    }

    /**
     * Sorts the pairs by their lower ID, then by their higher ID (heapsort, in place).
     */
    void sort()
    {
        for (int root = count / 2 - 1; root >= 0; root--)
        {
            sift(root, count);
        }
        for (int end = count - 1; end > 0; end--)
        {
            swap(0, end);
            sift(0, end);
        }
    }

    /**
     * Moves a pair down the heap to its place.
     *
     * @param root The pair moved.
     * @param end The size of the heap.
     */
    private void sift(int root, int end)
    {
        int parent = root;
        int child = 2 * parent + 1;
        while (child < end)
        {
            if (child + 1 < end && compare(child, this, child + 1) < 0)
            {
                child++;
            }
            if (compare(parent, this, child) >= 0)
            {
                break;
            }
            swap(parent, child);
            parent = child;
            child = 2 * parent + 1;
        }
    }

    /**
     * Swaps two pairs.
     *
     * @param i The first pair.
     * @param j The second pair.
     */
    private void swap(int i, int j)
    {
        final long a = first[i];
        final long b = second[i];
        first[i] = first[j];
        second[i] = second[j];
        first[j] = a;
        second[j] = b;
    }
}
