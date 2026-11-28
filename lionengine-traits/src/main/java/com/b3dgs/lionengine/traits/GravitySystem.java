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
 * Standard system {@code Gravity}: accelerates the velocity of each object pulled by a gravity (REQ-STD-003,
 * REQ-STD-008, REQ-STD-011, ADR-0011, ADR-0017, ADR-0030, decision 7, ADR-0034, ADR-0081).
 *
 * <p>
 * At each step, for each object having a {@link Movable} and a {@link Gravity} and none of the excluded trait types,
 * in the iteration order of its query, adds {@code acceleration * stepSeconds} to the vertical velocity of the
 * {@link Movable} (explicit Euler with the constant step, ADR-0017), never beyond the terminal velocity of the
 * {@link Gravity} in the direction of the acceleration: with a negative acceleration (down, Y up) the result is never
 * below {@code -terminalVelocity}, with a positive one never above {@code +terminalVelocity}. A vertical velocity
 * already beyond that limit is kept, neither cut nor increased. The horizontal velocity is never changed. An object
 * without {@link Movable} is not processed; no {@link Transformable} is needed, the position is changed later by
 * {@link MovementSystem}.
 * </p>
 *
 * <p>
 * Units: meters per second, meters per second squared, seconds. The result depends only on the trait values and the
 * step, never on the iteration order, rendering or a frontend; the same values and steps give the same velocities on
 * every platform ({@code double} arithmetic, correctly rounded).
 * </p>
 *
 * <p>
 * Slot: {@value #SLOT} (ADR-0034), after {@link JumpSystem} ({@value JumpSystem#SLOT}) and the game decisions that
 * set velocities (between 1000 and 2000), before {@link MovementSystem} ({@value MovementSystem#SLOT}), which then
 * clamps the whole speed to the maximum speed of the {@link Movable} and integrates it.
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
public final class GravitySystem implements GameSystem
{
    /** The slot of the system (ADR-0034). */
    public static final int SLOT = 2000;

    /** The movables. */
    private final TraitStorage<Movable> movables;
    /** The gravities. */
    private final TraitStorage<Gravity> gravities;
    /** The pulled objects. */
    private final Query pulled;

    /**
     * Creates the system, without excluded trait type.
     *
     * @param world The world the system is added to.
     * @param movables The storage of {@link Movable#TYPE} in the world.
     * @param gravities The storage of {@link Gravity#TYPE} in the world.
     * @throws IllegalArgumentException If an argument is {@code null} or a storage belongs to another world.
     * @throws IllegalStateException If called during a flush, or if the world failed.
     */
    public GravitySystem(World world, TraitStorage<Movable> movables, TraitStorage<Gravity> gravities)
    {
        this(world, movables, gravities, MovementSystem.NO_EXCLUDED);
    }

    /**
     * Creates the system, skipping the objects that carry any of the excluded trait types (REQ-STD-011), for example
     * network proxies (ADR-0038).
     *
     * @param world The world the system is added to.
     * @param movables The storage of {@link Movable#TYPE} in the world.
     * @param gravities The storage of {@link Gravity#TYPE} in the world.
     * @param excluded The storages of the excluded trait types in the world, possibly none; the array is not kept.
     * @throws IllegalArgumentException If an argument is {@code null}, the excluded set contains {@code null},
     *             {@code movables} or {@code gravities}, or a storage belongs to another world.
     * @throws IllegalStateException If called during a flush, or if the world failed.
     */
    public GravitySystem(World world,
                         TraitStorage<Movable> movables,
                         TraitStorage<Gravity> gravities,
                         TraitStorage<?>[] excluded)
    {
        MovementSystem.check(world, movables, "Movable");
        MovementSystem.check(world, gravities, "Gravity");
        MovementSystem.checkExcluded(excluded);
        this.movables = movables;
        this.gravities = gravities;
        pulled = world.createQuery(new TraitStorage<?>[]
        {
            movables, gravities
        }, excluded);
    }

    /**
     * Returns the pulled objects.
     *
     * @return The query of the objects having a {@link Movable}, a {@link Gravity} and no excluded trait.
     */
    public Query getQuery()
    {
        return pulled;
    }

    /**
     * Accelerates the vertical velocity of each pulled object, never beyond its terminal velocity.
     *
     * @param stepSeconds The constant step, in seconds, positive and finite (ADR-0017).
     */
    @Override
    public void update(double stepSeconds)
    {
        final int count = pulled.getCount();
        for (int i = 0; i < count; i++)
        {
            final long id = pulled.getId(i);
            final Movable movable = movables.get(id);
            movable.setVelocity(movable.getVelocityX(),
                                gravities.get(id).accelerate(movable.getVelocityY(), stepSeconds));
        }
    }
}
