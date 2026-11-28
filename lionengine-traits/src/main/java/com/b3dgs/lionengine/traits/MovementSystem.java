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

/**
 * Standard system {@code Movement}: moves each object by its velocity (REQ-STD-002, REQ-STD-008, REQ-STD-011,
 * ADR-0011, ADR-0017, ADR-0030, ADR-0034, ADR-0080).
 *
 * <p>
 * At each step, for each object having a {@link Transformable} and a {@link Movable} and none of the excluded trait
 * types, in the iteration order of its query:
 * </p>
 * <ol>
 * <li>clamps the velocity to the maximum speed of the {@link Movable}: if its magnitude exceeds it, the velocity is
 * scaled down to it, direction kept, and stored;</li>
 * <li>adds {@code velocity * stepSeconds} to the position of the {@link Transformable} (explicit Euler integration
 * with the constant step, ADR-0017).</li>
 * </ol>
 *
 * <p>
 * Units: meters, meters per second, seconds. The result depends only on the trait values and the step, never on the
 * iteration order, rendering or a frontend; the same values and steps give the same positions on every platform
 * ({@code double} arithmetic and {@link Math#sqrt(double)}, correctly rounded).
 * </p>
 *
 * <p>
 * Slot: {@value #SLOT} (ADR-0034), after {@code Jump} (1000) and {@code Gravity} (2000), which change velocities, and
 * before {@code Collision} (4000). The previous position is captured before, by {@link TransformCaptureSystem}.
 * </p>
 *
 * <p>
 * Preconditions: the storages are the ones {@link World#register(com.b3dgs.lionengine.ecs.TraitType)} returned in the
 * given world; the system is added to that world only, with {@link World#addSystem(GameSystem, int)} and
 * {@link #SLOT}. It creates one query at construction and allocates nothing per step. Runtime thread only.
 * </p>
 *
 * @since 11.0.0
 */
public final class MovementSystem implements GameSystem
{
    /** The slot of the system (ADR-0034). */
    public static final int SLOT = 3000;

    /** No excluded trait type. */
    static final TraitStorage<?>[] NO_EXCLUDED = new TraitStorage<?>[0];

    /** The transformables. */
    private final TraitStorage<Transformable> transformables;
    /** The movables. */
    private final TraitStorage<Movable> movables;
    /** The moved objects. */
    private final Query moved;

    /**
     * Creates the system, without excluded trait type.
     *
     * @param world The world the system is added to.
     * @param transformables The storage of {@link Transformable#TYPE} in the world.
     * @param movables The storage of {@link Movable#TYPE} in the world.
     * @throws IllegalArgumentException If an argument is {@code null} or a storage belongs to another world.
     * @throws IllegalStateException If called during a flush, or if the world failed.
     */
    public MovementSystem(World world, TraitStorage<Transformable> transformables, TraitStorage<Movable> movables)
    {
        this(world, transformables, movables, NO_EXCLUDED);
    }

    /**
     * Creates the system, skipping the objects that carry any of the excluded trait types (REQ-STD-011), for example
     * network proxies (ADR-0038).
     *
     * @param world The world the system is added to.
     * @param transformables The storage of {@link Transformable#TYPE} in the world.
     * @param movables The storage of {@link Movable#TYPE} in the world.
     * @param excluded The storages of the excluded trait types in the world, possibly none; the array is not kept.
     * @throws IllegalArgumentException If an argument is {@code null}, the excluded set contains {@code null},
     *             {@code transformables} or {@code movables}, or a storage belongs to another world.
     * @throws IllegalStateException If called during a flush, or if the world failed.
     */
    public MovementSystem(World world,
                          TraitStorage<Transformable> transformables,
                          TraitStorage<Movable> movables,
                          TraitStorage<?>[] excluded)
    {
        check(world, transformables, "Transformable");
        check(world, movables, "Movable");
        checkExcluded(excluded);
        this.transformables = transformables;
        this.movables = movables;
        moved = world.createQuery(new TraitStorage<?>[]
        {
            transformables, movables
        }, excluded);
    }

    /**
     * Checks the world and a storage of a standard trait.
     *
     * @param world The world.
     * @param storage The storage.
     * @param name The trait name, for the message.
     * @throws IllegalArgumentException If the world or the storage is {@code null}.
     */
    static void check(World world, TraitStorage<?> storage, String name)
    {
        if (world == null)
        {
            throw new IllegalArgumentException("World is null");
        }
        if (storage == null)
        {
            throw new IllegalArgumentException(name + " storage is null");
        }
    }

    /**
     * Checks the excluded set.
     *
     * @param excluded The excluded set.
     * @throws IllegalArgumentException If the excluded set is {@code null}.
     */
    static void checkExcluded(TraitStorage<?>[] excluded)
    {
        if (excluded == null)
        {
            throw new IllegalArgumentException("Excluded trait types are null");
        }
    }

    /**
     * Returns the moved objects.
     *
     * @return The query of the objects having a {@link Transformable}, a {@link Movable} and no excluded trait.
     */
    public Query getQuery()
    {
        return moved;
    }

    /**
     * Clamps the velocity of each moved object, then integrates it into its position.
     *
     * @param stepSeconds The constant step, in seconds, positive and finite (ADR-0017).
     */
    @Override
    public void update(double stepSeconds)
    {
        final int count = moved.getCount();
        for (int i = 0; i < count; i++)
        {
            final long id = moved.getId(i);
            final Movable movable = movables.get(id);
            movable.clamp();
            transformables.get(id)
                          .translate(movable.getVelocityX() * stepSeconds, movable.getVelocityY() * stepSeconds);
        }
    }
}
