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

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The traits of one type in one world: a sparse set of pooled instances (REQ-ECS-003, REQ-ECS-004, REQ-ECS-006,
 * REQ-ECS-012, REQ-ECS-013, ADR-0011, ADR-0030, ADR-0034, ADR-0075).
 *
 * <p>
 * Returned by {@link World#register(TraitType)}, once per trait type and world; keep it in a field and use it for every
 * access to that trait type. Its {@link #getTypeId() type ID} is the {@code int} the world gave the type.
 * </p>
 *
 * <p>
 * <b>Deferred changes.</b> {@link #add(long)} and {@link #remove(long)} record a command in the log of the world, with
 * its object creations and removals, and change nothing before the next {@link World#flush()}, which applies every
 * command in recorded order. {@link #add(long)} returns the instance at once, in its reset state, so that the caller
 * fills it before the flush. Adding a trait to an object that already has one of this type <em>replaces</em> it at the
 * flush: the last addition wins, the earlier instance is reset and pooled. An addition followed by a removal of the
 * same step both apply: the object has no trait after the flush. A removal of a trait the object does not have at that
 * point of the flush changes nothing; an addition to an object removed by an earlier command of the same flush is
 * discarded. Removing an object removes all its traits.
 * </p>
 *
 * <p>
 * <b>Accessors.</b> {@link #has(long)} and {@link #get(long)} read the state of the last flush. They take an alive
 * object: a pending object fails with {@link IllegalStateException}, a stale or never-valid ID with
 * {@link IllegalArgumentException}. Trait data may be read and changed at any time; only adding and removing traits is
 * deferred. A reference to a trait must not be kept after its removal: the instance is reset and reused.
 * </p>
 *
 * <p>
 * <b>Pooling and capacity.</b> The attached instances are packed in a dense array, in no meaningful order: a removal
 * moves the last instance into the freed position (swap-remove). Removed instances are reset with
 * {@link TraitType#reset(Object)} and kept in a pool; the storage creates {@link WorldConfig#traitCapacity()}
 * instances at registration. The dense array holds that many traits without growing; the flush doubles it when more
 * are attached, and it never shrinks; {@link #reserve(int)} grows it ahead. An addition that finds the pool empty
 * creates an instance, a warm-up cost. Once sized for the busiest step, {@link #add(long)}, {@link #remove(long)} and
 * the flush allocate nothing.
 * </p>
 *
 * <p>
 * Not thread-safe: used on the runtime thread only, with its world (ADR-0018).
 * </p>
 *
 * @param <T> The trait class.
 * @since 11.0.0
 */
public final class TraitStorage<T>
{
    /** Dense position of an object index without a trait. */
    private static final int NONE = -1;

    /** The world. */
    private final World world;
    /** The trait type. */
    private final TraitType<T> type;
    /** The type ID. */
    private final int typeId;
    /** Attached instances, packed. */
    private final ArrayList<T> dense;
    /** Free instances, reset. */
    private final ArrayList<T> pool;
    /** Instances returned by {@link #add(long)} since the last flush, by staged slot. */
    private final ArrayList<T> staged;
    /** Object ID per dense position. */
    private long[] denseIds;
    /** Dense position per object index, {@link #NONE} without a trait. */
    private int[] sparse;
    /** Capacity of the dense array. */
    private int capacity;
    /** Instances created so far: attached, free and staged. */
    private int owned;

    /**
     * Creates a storage and fills its pool.
     *
     * @param world The world.
     * @param type The trait type.
     * @param typeId The type ID.
     * @param traitCapacity The trait capacity, positive.
     * @param objectCapacity The object capacity of the world, positive.
     * @throws IllegalStateException If the type creates a {@code null} instance.
     */
    TraitStorage(World world, TraitType<T> type, int typeId, int traitCapacity, int objectCapacity)
    {
        this.world = world;
        this.type = type;
        this.typeId = typeId;
        capacity = traitCapacity;
        dense = new ArrayList<>(traitCapacity);
        pool = new ArrayList<>(traitCapacity);
        staged = new ArrayList<>(traitCapacity);
        denseIds = new long[traitCapacity];
        sparse = new int[objectCapacity];
        Arrays.fill(sparse, NONE);
        fillPool(traitCapacity);
    }

    /**
     * Adds a trait of this type to an object, at the next {@link World#flush()}.
     *
     * <p>
     * The object may be alive or pending: a trait added to a pending object is attached by the flush that creates it.
     * The returned instance is in its reset state; fill it now, it is attached as is. If the object already has a
     * trait of this type at the flush, the new one replaces it. No allocation while the pool and the capacities fit.
     * </p>
     *
     * @param objectId The ID of an alive or pending object of the world.
     * @return The instance attached at the flush; not to be kept after its removal.
     * @throws IllegalArgumentException If the ID is stale or never named an object of the world.
     * @throws IllegalStateException If {@link WorldConfig#MAX_OBJECT_CAPACITY} commands are already recorded since the
     *             last flush, or if the pool is empty and the type creates a {@code null} instance.
     */
    public T add(long objectId)
    {
        world.checkRecordable(objectId);
        if (pool.isEmpty())
        {
            fillPool(owned + 1);
        }
        final T trait = pool.remove(pool.size() - 1);
        world.recordTrait(StructuralCommands.ADD_TRAIT, objectId, typeId, staged.size());
        staged.add(trait);
        return trait;
    }

    /**
     * Removes the trait of this type from an object, at the next {@link World#flush()}.
     *
     * <p>
     * The object may be alive or pending. If it has no trait of this type at that point of the flush, nothing
     * changes. No allocation while the capacities fit.
     * </p>
     *
     * @param objectId The ID of an alive or pending object of the world.
     * @throws IllegalArgumentException If the ID is stale or never named an object of the world.
     * @throws IllegalStateException If {@link WorldConfig#MAX_OBJECT_CAPACITY} commands are already recorded since the
     *             last flush.
     */
    public void remove(long objectId)
    {
        world.checkRecordable(objectId);
        world.recordTrait(StructuralCommands.REMOVE_TRAIT, objectId, typeId, 0);
    }

    /**
     * Tells whether an object has a trait of this type, as of the last flush.
     *
     * @param objectId The ID of an alive object of the world.
     * @return {@code true} if the object has a trait of this type.
     * @throws IllegalStateException If the object is pending, waiting for the flush.
     * @throws IllegalArgumentException If the ID is stale or never named an object of the world.
     */
    public boolean has(long objectId)
    {
        return sparse[world.checkAlive(objectId)] != NONE;
    }

    /**
     * Returns the trait of this type of an object, as of the last flush.
     *
     * <p>
     * Its data may be read and changed at once; the reference must not be kept after the trait is removed.
     * </p>
     *
     * @param objectId The ID of an alive object of the world that has a trait of this type.
     * @return The trait.
     * @throws IllegalStateException If the object is pending, waiting for the flush, or has no trait of this type
     *             ({@link #has(long)} tells).
     * @throws IllegalArgumentException If the ID is stale or never named an object of the world.
     */
    public T get(long objectId)
    {
        final int position = sparse[world.checkAlive(objectId)];
        if (position == NONE)
        {
            throw new IllegalStateException("Object has no trait of type " + typeId + ": " + objectId);
        }
        return dense.get(position);
    }

    /**
     * Grows the dense array and the pool ahead, so that the next flushes and additions do not have to.
     *
     * <p>
     * Allocates now, outside the steps: the dense array grows to the capacity and the pool is filled until the storage
     * owns that many instances. Never shrinks: a value below the current capacity changes nothing more.
     * </p>
     *
     * @param traitCapacity The number of traits to hold without growing, in [1,
     *            {@link WorldConfig#MAX_TRAIT_CAPACITY}].
     * @throws IllegalArgumentException If the capacity is outside its range.
     * @throws IllegalStateException If the type creates a {@code null} instance.
     */
    public void reserve(int traitCapacity)
    {
        WorldConfig.checkTraitCapacity(traitCapacity);
        growDense(traitCapacity);
        fillPool(traitCapacity);
    }

    /**
     * Returns the type ID.
     *
     * @return The {@code int} the world gave the type at registration: {@code 0} for the first type, then
     *         consecutive.
     */
    public int getTypeId()
    {
        return typeId;
    }

    /**
     * Returns the trait type.
     *
     * @return The type registered.
     */
    public TraitType<T> getType()
    {
        return type;
    }

    /**
     * Returns the number of attached traits.
     *
     * @return The objects having a trait of this type, as of the last flush.
     */
    public int getCount()
    {
        return dense.size();
    }

    /**
     * Returns the trait capacity.
     *
     * @return The number of traits held without growing: the configured capacity, or more after a growth.
     */
    public int getCapacity()
    {
        return capacity;
    }

    /**
     * Returns the number of free instances, for the tests of pooling.
     *
     * @return The reset instances waiting in the pool.
     */
    int getPoolSize()
    {
        return pool.size();
    }

    /**
     * Returns the object ID at a dense position, for the queries.
     *
     * @param position The position, in [0, {@link #getCount()}).
     * @return The object ID.
     */
    long getId(int position)
    {
        return denseIds[position];
    }

    /**
     * Returns the trait at a dense position, for the queries.
     *
     * @param position The position, in [0, {@link #getCount()}).
     * @return The trait.
     */
    T getAt(int position)
    {
        return dense.get(position);
    }

    /**
     * Tells whether an object index has a trait of this type, at the flush.
     *
     * @param index The object index, within the object capacity.
     * @return {@code true} if a trait is attached.
     */
    boolean contains(int index)
    {
        return sparse[index] != NONE;
    }

    /**
     * Grows the sparse array to an object capacity, at the flush or by {@link World#reserve(int)}. Never shrinks.
     *
     * @param objectCapacity The object capacity.
     */
    void growObjects(int objectCapacity)
    {
        final int old = sparse.length;
        if (objectCapacity > old)
        {
            final int[] grown = new int[objectCapacity];
            System.arraycopy(sparse, 0, grown, 0, old);
            Arrays.fill(grown, old, objectCapacity, NONE);
            sparse = grown;
        }
    }

    /**
     * Doubles the dense array until the attached and staged traits fit, before the flush applies its commands.
     */
    void prepareFlush()
    {
        long needed = (long) dense.size() + staged.size();
        needed = Math.min(needed, WorldConfig.MAX_TRAIT_CAPACITY);
        long grown = capacity;
        while (grown < needed)
        {
            grown *= 2L;
        }
        growDense((int) Math.min(grown, WorldConfig.MAX_TRAIT_CAPACITY));
    }

    /**
     * Attaches a staged instance to an object without a trait of this type, at the flush.
     *
     * @param objectId The object ID, alive.
     * @param index The object index.
     * @param slot The staged slot.
     */
    void attach(long objectId, int index, int slot)
    {
        final int position = dense.size();
        dense.add(staged.get(slot));
        denseIds[position] = objectId;
        sparse[index] = position;
    }

    /**
     * Replaces the instance of an object that has a trait of this type by a staged one, at the flush; the old instance
     * is reset and pooled.
     *
     * @param index The object index.
     * @param slot The staged slot.
     */
    void replace(int index, int slot)
    {
        release(dense.set(sparse[index], staged.get(slot)));
    }

    /**
     * Detaches the trait of an object, at the flush: the last instance moves into its position, and it is reset and
     * pooled.
     *
     * @param index The object index, with a trait of this type.
     */
    void detach(int index)
    {
        final int position = sparse[index];
        final int last = dense.size() - 1;
        final T trait = dense.get(position);
        if (position != last)
        {
            final long movedId = denseIds[last];
            dense.set(position, dense.get(last));
            denseIds[position] = movedId;
            sparse[ObjectTable.index(movedId)] = position;
        }
        dense.remove(last);
        sparse[index] = NONE;
        release(trait);
    }

    /**
     * Discards a staged instance whose object is not alive any more, at the flush: it is reset and pooled.
     *
     * @param slot The staged slot.
     */
    void discard(int slot)
    {
        release(staged.get(slot));
    }

    /**
     * Forgets the staged instances, at the end of the flush, once each one is attached or pooled.
     */
    void endFlush()
    {
        staged.clear();
    }

    /**
     * Resets an instance and puts it back in the pool.
     *
     * @param trait The instance, detached.
     */
    private void release(T trait)
    {
        type.reset(trait);
        pool.add(trait);
    }

    /**
     * Grows the dense array to a capacity. Never shrinks.
     *
     * @param newCapacity The capacity.
     */
    private void growDense(int newCapacity)
    {
        if (newCapacity > capacity)
        {
            final long[] grown = new long[newCapacity];
            System.arraycopy(denseIds, 0, grown, 0, dense.size());
            denseIds = grown;
            dense.ensureCapacity(newCapacity);
            capacity = newCapacity;
        }
    }

    /**
     * Creates instances into the pool until the storage owns a number of them.
     *
     * @param total The number of instances to own.
     * @throws IllegalStateException If the type creates a {@code null} instance.
     */
    private void fillPool(int total)
    {
        pool.ensureCapacity(total);
        staged.ensureCapacity(total);
        while (owned < total)
        {
            final T trait = type.create();
            if (trait == null)
            {
                throw new IllegalStateException("Trait type " + typeId + " created a null instance");
            }
            pool.add(trait);
            owned++;
        }
    }
}
