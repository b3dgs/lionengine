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

import com.b3dgs.lionengine.ecs.GameSystem;
import com.b3dgs.lionengine.ecs.Query;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.physics.Box;
import com.b3dgs.lionengine.physics.BroadPhase;

/**
 * Standard system {@code Collision}: finds the objects whose boxes overlap and reports the pairs that start and stop
 * overlapping (REQ-STD-005, REQ-STD-008, REQ-STD-010, REQ-STD-011, ADR-0011, ADR-0021, ADR-0030, ADR-0034,
 * ADR-0082).
 *
 * <p>
 * At each step, for the objects having a {@link Transformable} and a {@link Collidable} and none of the excluded trait
 * types:
 * </p>
 * <ol>
 * <li>builds the box of each object, centered on {@code (x + offsetX, y + offsetY)} with the size of its
 * {@link Collidable}, and finds the overlapping boxes with a uniform-grid {@link BroadPhase} of the cell size of the
 * system (one per world, {@link BroadPhase#DEFAULT_CELL_SIZE} by default); boxes that only touch by an edge or a corner
 * do not overlap ({@link Box#overlaps(Box)});</li>
 * <li>keeps the pairs whose layers and masks accept each other: each mask includes the other's layer;</li>
 * <li>sorts them by {@code (objectA, objectB)}, {@code objectA < objectB}: the pairs of the step, read by
 * {@link #getPairCount()}, {@link #getObjectA(int)} and {@link #getObjectB(int)};</li>
 * <li>compares them with the pairs of its previous step and reports to its {@link CollisionListener}s every pair that
 * stopped overlapping ({@link CollisionListener#onExit(long, long)}), then every pair that started
 * ({@link CollisionListener#onEnter(long, long)}), each in ascending order. A pair whose object left the query since
 * the previous step (removed, or its {@link Collidable} or {@link Transformable} removed, or an excluded trait added)
 * is an exit.</li>
 * </ol>
 *
 * <p>
 * Object versus object is detection only (ADR-0030): no object is moved or stopped, trigger or not, and every
 * accepted pair is reported, triggers included; the game reacts in its listeners or in systems between 4000 and 5000.
 * No swept detection: an object moving more than half of the smallest box per step can pass through another one
 * unseen; the {@link Movable} speed clamp keeps the displacement bounded (ADR-0030, decision 6).
 * </p>
 *
 * <p>
 * Units: meters. Determinism: the pairs and the events depend only on the boxes, layers, masks and IDs, never on the
 * iteration order of the query, rendering or a frontend; they are the same on every platform (exact {@code double}
 * comparisons, sort by ID).
 * </p>
 *
 * <p>
 * Slot: {@value #SLOT} (ADR-0034), after {@link MovementSystem} (3000), so the boxes are those of the moved positions,
 * and before {@link LifetimeSystem} (5000); game reactions to collisions run between 4000 and 5000.
 * </p>
 *
 * <p>
 * Preconditions: the storages are the ones {@link World#register(com.b3dgs.lionengine.ecs.TraitType)} returned in the
 * given world; the system is added to that world only, once, with {@link World#addSystem(GameSystem, int)} and
 * {@link #SLOT}; the positions, sizes and offsets are finite. It creates one query and one broad phase at construction
 * and allocates nothing per step once its capacities fit the busiest step: they start at the trait capacity of the
 * world for the objects and the pairs and double when a step needs more, a warm-up cost. Runtime thread only.
 * </p>
 *
 * @since 11.0.0
 */
public final class CollisionSystem implements GameSystem
{
    /** The slot of the system (ADR-0034). */
    public static final int SLOT = 4000;

    /** No listener. */
    private static final CollisionListener[] NO_LISTENERS = new CollisionListener[0];

    /** The transformables. */
    private final TraitStorage<Transformable> transformables;
    /** The collidables. */
    private final TraitStorage<Collidable> collidables;
    /** The colliding objects. */
    private final Query colliding;
    /** The broad phase. */
    private final BroadPhase broadPhase;
    /** The box of the object being added. */
    private final Box box = new Box();
    /** The object ID, per broad-phase index. */
    private long[] ids;
    /** The pairs of the last step, sorted. */
    private PairList pairs;
    /** The pairs being found. */
    private PairList found;
    /** The listeners, in registration order, replaced on each change. */
    private CollisionListener[] listeners = NO_LISTENERS;

    /**
     * Creates the system, with the default cell size and without excluded trait type.
     *
     * @param world The world the system is added to.
     * @param transformables The storage of {@link Transformable#TYPE} in the world.
     * @param collidables The storage of {@link Collidable#TYPE} in the world.
     * @throws IllegalArgumentException If an argument is {@code null} or a storage belongs to another world.
     * @throws IllegalStateException If called during a flush, or if the world failed.
     */
    public CollisionSystem(World world,
                           TraitStorage<Transformable> transformables,
                           TraitStorage<Collidable> collidables)
    {
        this(world, transformables, collidables, BroadPhase.DEFAULT_CELL_SIZE, MovementSystem.NO_EXCLUDED);
    }

    /**
     * Creates the system, with the default cell size, skipping the objects that carry any of the excluded trait types
     * (REQ-STD-011), for example network proxies (ADR-0038).
     *
     * @param world The world the system is added to.
     * @param transformables The storage of {@link Transformable#TYPE} in the world.
     * @param collidables The storage of {@link Collidable#TYPE} in the world.
     * @param excluded The storages of the excluded trait types in the world, possibly none; the array is not kept.
     * @throws IllegalArgumentException If an argument is {@code null}, the excluded set contains {@code null},
     *             {@code transformables} or {@code collidables}, or a storage belongs to another world.
     * @throws IllegalStateException If called during a flush, or if the world failed.
     */
    public CollisionSystem(World world,
                           TraitStorage<Transformable> transformables,
                           TraitStorage<Collidable> collidables,
                           TraitStorage<?>[] excluded)
    {
        this(world, transformables, collidables, BroadPhase.DEFAULT_CELL_SIZE, excluded);
    }

    /**
     * Creates the system with the cell size of its world, skipping the objects that carry any of the excluded trait
     * types (REQ-STD-011).
     *
     * @param world The world the system is added to.
     * @param transformables The storage of {@link Transformable#TYPE} in the world.
     * @param collidables The storage of {@link Collidable#TYPE} in the world.
     * @param cellSize The cell size of the broad phase, in meters, positive and finite
     *            ({@link BroadPhase#DEFAULT_CELL_SIZE} by default).
     * @param excluded The storages of the excluded trait types in the world, possibly none; the array is not kept.
     * @throws IllegalArgumentException If an argument is {@code null}, the cell size is not positive and finite, the
     *             excluded set contains {@code null}, {@code transformables} or {@code collidables}, or a storage
     *             belongs to another world.
     * @throws IllegalStateException If called during a flush, or if the world failed.
     */
    public CollisionSystem(World world,
                           TraitStorage<Transformable> transformables,
                           TraitStorage<Collidable> collidables,
                           double cellSize,
                           TraitStorage<?>[] excluded)
    {
        MovementSystem.check(world, transformables, "Transformable");
        MovementSystem.check(world, collidables, "Collidable");
        MovementSystem.checkExcluded(excluded);
        final int capacity = Math.max(collidables.getCapacity(), 1);
        broadPhase = new BroadPhase(cellSize, capacity);
        this.transformables = transformables;
        this.collidables = collidables;
        colliding = world.createQuery(new TraitStorage<?>[]
        {
            transformables, collidables
        }, excluded);
        ids = new long[capacity];
        pairs = new PairList(capacity);
        found = new PairList(capacity);
    }

    /**
     * Returns the colliding objects.
     *
     * @return The query of the objects having a {@link Transformable}, a {@link Collidable} and no excluded trait.
     */
    public Query getQuery()
    {
        return colliding;
    }

    /**
     * Returns the cell size of the broad phase.
     *
     * @return The cell size, in meters.
     */
    public double getCellSize()
    {
        return broadPhase.getCellSize();
    }

    /**
     * Adds a listener at the end of the list; it receives the events from the next one.
     *
     * @param listener The listener.
     * @throws IllegalArgumentException If the listener is {@code null} or already registered.
     */
    public void addListener(CollisionListener listener)
    {
        if (listener == null || indexOf(listener) >= 0)
        {
            throw new IllegalArgumentException("Collision listener is null or already registered");
        }
        final CollisionListener[] grown = new CollisionListener[listeners.length + 1];
        System.arraycopy(listeners, 0, grown, 0, listeners.length);
        grown[listeners.length] = listener;
        listeners = grown;
    }

    /**
     * Removes a listener; it receives no event from the next one.
     *
     * @param listener The listener.
     * @throws IllegalArgumentException If the listener is not registered.
     */
    public void removeListener(CollisionListener listener)
    {
        final int index = indexOf(listener);
        if (index < 0)
        {
            throw new IllegalArgumentException("Collision listener not registered");
        }
        final CollisionListener[] shrunk = new CollisionListener[listeners.length - 1];
        System.arraycopy(listeners, 0, shrunk, 0, index);
        System.arraycopy(listeners, index + 1, shrunk, index, shrunk.length - index);
        listeners = shrunk;
    }

    /**
     * Returns the number of overlapping pairs.
     *
     * @return The pairs found by the last step of the system, {@code 0} before the first one.
     */
    public int getPairCount()
    {
        return pairs.getCount();
    }

    /**
     * Returns the lower object of an overlapping pair.
     *
     * @param pair The pair, in [0, {@link #getPairCount()}), in ascending order of {@code (objectA, objectB)}.
     * @return The lower object ID of the pair, as of the last step of the system.
     * @throws IllegalArgumentException If the pair is outside its range.
     */
    public long getObjectA(int pair)
    {
        checkPair(pair);
        return pairs.getFirst(pair);
    }

    /**
     * Returns the higher object of an overlapping pair.
     *
     * @param pair The pair, in [0, {@link #getPairCount()}), in ascending order of {@code (objectA, objectB)}.
     * @return The higher object ID of the pair, as of the last step of the system.
     * @throws IllegalArgumentException If the pair is outside its range.
     */
    public long getObjectB(int pair)
    {
        checkPair(pair);
        return pairs.getSecond(pair);
    }

    /**
     * Finds the overlapping pairs and reports the exits, then the enters, to the listeners.
     *
     * @param stepSeconds The constant step, in seconds, positive and finite (ADR-0017); not used, the boxes being those
     *            of the positions moved before.
     * @throws IllegalArgumentException If the position of a colliding object is not finite.
     */
    @Override
    public void update(double stepSeconds)
    {
        addBoxes();
        final int candidates = broadPhase.findPairs();
        found.clear();
        for (int i = 0; i < candidates; i++)
        {
            final long a = ids[broadPhase.getFirst(i)];
            final long b = ids[broadPhase.getSecond(i)];
            if (collidables.get(a).accepts(collidables.get(b)))
            {
                found.add(Math.min(a, b), Math.max(a, b));
            }
        }
        found.sort();
        report(pairs, found, false);
        report(found, pairs, true);
        final PairList last = pairs;
        pairs = found;
        found = last;
    }

    /**
     * Adds the box of every colliding object to the broad phase, the object of index {@code i} at position {@code i}
     * of the query.
     */
    private void addBoxes()
    {
        broadPhase.clear();
        final int count = colliding.getCount();
        if (ids.length < count)
        {
            ids = new long[Math.max(count, ids.length * 2)];
        }
        for (int i = 0; i < count; i++)
        {
            final long id = colliding.getId(i);
            ids[i] = id;
            collidables.get(id).toBox(transformables.get(id), box);
            broadPhase.add(box);
        }
    }

    /**
     * Reports the pairs of a sorted list that are not in another sorted list.
     *
     * @param from The pairs reported if absent from {@code against}.
     * @param against The pairs compared with.
     * @param enter {@code true} to report enters, {@code false} to report exits.
     */
    private void report(PairList from, PairList against, boolean enter)
    {
        final int count = from.getCount();
        final int otherCount = against.getCount();
        int j = 0;
        for (int i = 0; i < count; i++)
        {
            while (j < otherCount && against.compare(j, from, i) < 0)
            {
                j++;
            }
            if (j == otherCount || against.compare(j, from, i) != 0)
            {
                deliver(from.getFirst(i), from.getSecond(i), enter);
            }
        }
    }

    /**
     * Delivers an event to the listeners current at its start.
     *
     * @param objectA The lower ID.
     * @param objectB The higher ID.
     * @param enter {@code true} for an enter, {@code false} for an exit.
     */
    private void deliver(long objectA, long objectB, boolean enter)
    {
        for (final CollisionListener listener : listeners)
        {
            if (enter)
            {
                listener.onEnter(objectA, objectB);
            }
            else
            {
                listener.onExit(objectA, objectB);
            }
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
        if (pair < 0 || pair >= pairs.getCount())
        {
            throw new IllegalArgumentException("Pair outside [0, " + pairs.getCount() + "): " + pair);
        }
    }

    /**
     * Returns the position of a listener, by identity.
     *
     * @param listener The listener looked for, possibly {@code null}.
     * @return The position, {@code -1} if absent.
     */
    private int indexOf(CollisionListener listener)
    {
        for (int i = 0; i < listeners.length; i++)
        {
            if (listeners[i] == listener)
            {
                return i;
            }
        }
        return -1;
    }
}
