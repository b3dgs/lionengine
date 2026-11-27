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

/**
 * The object table of a world: the generation and the state of every object index, and the queue of free indices
 * (REQ-ECS-001, REQ-ECS-002, REQ-ECS-011, ADR-0030, ADR-0074).
 *
 * <p>
 * An object ID packs a generation in its high 32 bits and an index in its low 32 bits. Generations go from
 * {@link #FIRST_GENERATION} to the highest generation of the table ({@link #MAX_GENERATION} by default), so every ID is
 * positive and {@code 0} is never one. An index is <em>pending</em> from its allocation to the flush that activates
 * it, then <em>alive</em> until the flush that releases it; a released index takes the next generation and joins the
 * back of the free queue, so the oldest free index is reused first. An index whose generation reached the highest one
 * is retired instead: it is never reused, so no ID is ever issued twice.
 * </p>
 *
 * <p>
 * The arrays have the capacity of the table. Indices allocated beyond it, before the flush that grows the table, are
 * pending with the first generation by construction, and are recorded only by the count of issued indices. Nothing is
 * allocated outside {@link #grow(int)}.
 * </p>
 *
 * <p>
 * Not thread-safe: owned by the runtime thread with its world.
 * </p>
 */
final class ObjectTable
{
    /** Generation of an index never used before. */
    static final int FIRST_GENERATION = 1;
    /** Highest generation: after it, an index is retired. */
    static final int MAX_GENERATION = Integer.MAX_VALUE;

    /** Bits of the index, the low half of an ID. */
    private static final long INDEX_MASK = 0xFFFF_FFFFL;
    /** Shift of the generation, the high half of an ID. */
    private static final int GENERATION_SHIFT = 32;
    /** State of an index not issued, free or retired. */
    private static final byte FREE = 0;
    /** State of an index allocated and waiting for the flush. */
    private static final byte PENDING = 1;
    /** State of an index whose object is alive. */
    private static final byte ALIVE = 2;

    /**
     * Packs an ID.
     *
     * @param generation The generation, positive.
     * @param index The index, zero or positive.
     * @return The ID.
     */
    static long pack(int generation, int index)
    {
        return (long) generation << GENERATION_SHIFT | index & INDEX_MASK;
    }

    /**
     * Returns the index of an ID.
     *
     * @param id The ID.
     * @return Its low 32 bits.
     */
    static int index(long id)
    {
        return (int) (id & INDEX_MASK);
    }

    /**
     * Returns the generation of an ID.
     *
     * @param id The ID.
     * @return Its high 32 bits.
     */
    static int generation(long id)
    {
        return (int) (id >>> GENERATION_SHIFT);
    }

    /** Highest generation of this table. */
    private final int maxGeneration;
    /** Current generation per index. */
    private int[] generations;
    /** State per index. */
    private byte[] states;
    /** Free indices, a ring buffer in release order. */
    private int[] freeQueue;
    /** Position of the oldest free index in {@link #freeQueue}. */
    private int freeHead;
    /** Number of free indices. */
    private int freeCount;
    /** Number of indices issued so far, from {@code 0}. */
    private int issued;
    /** Number of alive objects. */
    private int aliveCount;

    /**
     * Creates a table.
     *
     * @param capacity The initial capacity, positive.
     * @param maxGeneration The highest generation, at least {@link #FIRST_GENERATION}.
     */
    ObjectTable(int capacity, int maxGeneration)
    {
        this.maxGeneration = maxGeneration;
        generations = new int[capacity];
        states = new byte[capacity];
        freeQueue = new int[capacity];
        fillFirstGeneration(0);
    }

    /**
     * Allocates an index: the oldest free index, otherwise the next index never used. The index is pending.
     *
     * @return The ID of the pending object.
     * @throws IllegalStateException If {@link WorldConfig#MAX_OBJECT_CAPACITY} indices are already in use.
     */
    long allocate()
    {
        final int index;
        if (freeCount > 0)
        {
            index = freeQueue[freeHead];
            freeHead = (freeHead + 1) % freeQueue.length;
            freeCount--;
            states[index] = PENDING;
        }
        else
        {
            if (issued == WorldConfig.MAX_OBJECT_CAPACITY)
            {
                throw new IllegalStateException("Object capacity exhausted: 2^30 objects");
            }
            index = issued;
            issued++;
            if (index < states.length)
            {
                states[index] = PENDING;
            }
        }
        return pack(getGeneration(index), index);
    }

    /**
     * Activates a pending object, at the flush.
     *
     * @param id The ID of a pending object of this table, within its capacity.
     */
    void activate(long id)
    {
        states[index(id)] = ALIVE;
        aliveCount++;
    }

    /**
     * Releases an alive object, at the flush: its index takes the next generation and joins the free queue, or is
     * retired after the highest generation. An ID that is not alive any more, removed twice, is ignored.
     *
     * @param id The ID.
     * @return {@code true} if the object was alive and is released, {@code false} if nothing changed.
     */
    boolean release(long id)
    {
        final boolean released = isAlive(id);
        if (released)
        {
            final int index = index(id);
            states[index] = FREE;
            aliveCount--;
            final int generation = generations[index];
            if (generation < maxGeneration)
            {
                generations[index] = generation + 1;
                freeQueue[(freeHead + freeCount) % freeQueue.length] = index;
                freeCount++;
            }
        }
        return released;
    }

    /**
     * Tells whether an ID names an alive object. Never throws.
     *
     * @param id The ID, any value.
     * @return {@code true} if the object is alive, {@code false} if it is pending, removed, or the ID was never valid.
     */
    boolean isAlive(long id)
    {
        final int index = index(id);
        return index >= 0
               && index < states.length
               && states[index] == ALIVE
               && generations[index] == generation(id);
    }

    /**
     * Tells whether an ID names a pending object. Never throws.
     *
     * @param id The ID, any value.
     * @return {@code true} if the object is created and waits for the flush.
     */
    boolean isPending(long id)
    {
        final int index = index(id);
        final boolean pending;
        if (index < 0 || index >= issued)
        {
            pending = false;
        }
        else if (index >= states.length)
        {
            pending = generation(id) == FIRST_GENERATION;
        }
        else
        {
            pending = states[index] == PENDING && generations[index] == generation(id);
        }
        return pending;
    }

    /**
     * Grows the table, if it is smaller than the capacity. Never shrinks.
     *
     * @param capacity The capacity to reach, at most {@link WorldConfig#MAX_OBJECT_CAPACITY}.
     */
    void grow(int capacity)
    {
        final int old = states.length;
        if (capacity > old)
        {
            final int[] newGenerations = new int[capacity];
            final byte[] newStates = new byte[capacity];
            final int[] newFree = new int[capacity];
            System.arraycopy(generations, 0, newGenerations, 0, old);
            System.arraycopy(states, 0, newStates, 0, old);
            for (int i = 0; i < freeCount; i++)
            {
                newFree[i] = freeQueue[(freeHead + i) % old];
            }
            generations = newGenerations;
            states = newStates;
            freeQueue = newFree;
            freeHead = 0;
            fillFirstGeneration(old);
            final int pendingEnd = Math.min(issued, capacity);
            for (int i = old; i < pendingEnd; i++)
            {
                states[i] = PENDING;
            }
        }
    }

    /**
     * Doubles the capacity until every issued index fits, at the flush.
     */
    void growToIssued()
    {
        long capacity = states.length;
        while (capacity < issued)
        {
            capacity *= 2L;
        }
        grow((int) Math.min(capacity, WorldConfig.MAX_OBJECT_CAPACITY));
    }

    /**
     * Returns the capacity.
     *
     * @return The number of indices held without growing.
     */
    int getCapacity()
    {
        return states.length;
    }

    /**
     * Returns the number of alive objects.
     *
     * @return The number of objects activated and not released.
     */
    int getAlive()
    {
        return aliveCount;
    }

    /**
     * Returns the current generation of an issued index.
     *
     * @param index The index, below the number of issued indices.
     * @return Its generation.
     */
    private int getGeneration(int index)
    {
        final int generation;
        if (index < generations.length)
        {
            generation = generations[index];
        }
        else
        {
            generation = FIRST_GENERATION;
        }
        return generation;
    }

    /**
     * Sets the first generation from an index to the end of the table.
     *
     * @param from The first index to set.
     */
    private void fillFirstGeneration(int from)
    {
        for (int i = from; i < generations.length; i++)
        {
            generations[i] = FIRST_GENERATION;
        }
    }
}
