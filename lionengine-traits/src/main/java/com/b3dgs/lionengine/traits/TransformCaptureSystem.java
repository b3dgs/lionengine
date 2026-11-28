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
 * Standard system: captures the previous position of every {@link Transformable} at the start of the step
 * (REQ-STD-001, REQ-STD-011, ADR-0017, ADR-0030, decision 8, ADR-0034, ADR-0080).
 *
 * <p>
 * At each step, for each object having a {@link Transformable} and none of the excluded trait types, copies the
 * position into the previous position, so that rendering can interpolate between them with {@code alpha} and the
 * previous position is the one the step started from, whatever system moves the object afterwards. Objects without a
 * {@link Movable} are captured too, so that an object moved by game code is interpolated the same way.
 * </p>
 *
 * <p>
 * Slot: {@value #SLOT} ({@link Integer#MIN_VALUE}), before every other slot. Systems of equal slots run in
 * registration order, so the game registers this system before any other system it might give the same slot. A game
 * that does not render with interpolation may leave it out.
 * </p>
 *
 * <p>
 * Preconditions: the storages are the ones {@link World#register(com.b3dgs.lionengine.ecs.TraitType)} returned in the
 * given world; the system is added to that world only. It creates one query at construction and allocates nothing per
 * step. Runtime thread only.
 * </p>
 *
 * @since 11.0.0
 */
public final class TransformCaptureSystem implements GameSystem
{
    /** The slot of the system, before every other one (ADR-0080). */
    public static final int SLOT = Integer.MIN_VALUE;

    /** The transformables. */
    private final TraitStorage<Transformable> transformables;
    /** The captured objects. */
    private final Query captured;

    /**
     * Creates the system, without excluded trait type.
     *
     * @param world The world the system is added to.
     * @param transformables The storage of {@link Transformable#TYPE} in the world.
     * @throws IllegalArgumentException If an argument is {@code null} or the storage belongs to another world.
     * @throws IllegalStateException If called during a flush, or if the world failed.
     */
    public TransformCaptureSystem(World world, TraitStorage<Transformable> transformables)
    {
        this(world, transformables, MovementSystem.NO_EXCLUDED);
    }

    /**
     * Creates the system, skipping the objects that carry any of the excluded trait types (REQ-STD-011), for example
     * network proxies (ADR-0038).
     *
     * @param world The world the system is added to.
     * @param transformables The storage of {@link Transformable#TYPE} in the world.
     * @param excluded The storages of the excluded trait types in the world, possibly none; the array is not kept.
     * @throws IllegalArgumentException If an argument is {@code null}, the excluded set contains {@code null} or
     *             {@code transformables}, or a storage belongs to another world.
     * @throws IllegalStateException If called during a flush, or if the world failed.
     */
    public TransformCaptureSystem(World world,
                                  TraitStorage<Transformable> transformables,
                                  TraitStorage<?>[] excluded)
    {
        MovementSystem.check(world, transformables, "Transformable");
        this.transformables = transformables;
        captured = world.createQuery(new TraitStorage<?>[]
        {
            transformables
        }, MovementSystem.checkExcluded(excluded));
    }

    /**
     * Returns the captured objects.
     *
     * @return The query of the objects having a {@link Transformable} and no excluded trait.
     */
    public Query getQuery()
    {
        return captured;
    }

    /**
     * Copies the position of each captured object into its previous position.
     *
     * @param stepSeconds The constant step, in seconds; not used, the capture does not depend on it.
     */
    @Override
    public void update(double stepSeconds)
    {
        final int count = captured.getCount();
        for (int i = 0; i < count; i++)
        {
            transformables.get(captured.getId(i)).capture();
        }
    }
}
