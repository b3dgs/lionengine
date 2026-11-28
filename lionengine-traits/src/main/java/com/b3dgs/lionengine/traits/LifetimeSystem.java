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
 * Standard system {@code Lifetime}: removes each object whose lifetime expires (REQ-STD-006, REQ-STD-008,
 * REQ-STD-011, ADR-0011, ADR-0017, ADR-0030, ADR-0034, ADR-0081).
 *
 * <p>
 * At each step, for each object having a {@link Lifetime} and none of the excluded trait types, in the iteration order
 * of its query, subtracts the step from the remaining duration, then, if it is at or under {@code 0}, requests the
 * removal of the object with {@link World#remove(long)}. The removal, with every trait of the object, is applied at the
 * flush that ends the step ({@link World#update(double)}): systems after slot {@value #SLOT} still see the object in
 * that step, and the next step no longer does, so the removal is requested once. An object never expires while its
 * duration is {@link Double#POSITIVE_INFINITY}. No other trait is needed.
 * </p>
 *
 * <p>
 * Units: seconds. With the constant step {@code dt}, an object of duration {@code d} is removed by the flush of the
 * step at which the repeated subtraction of {@code dt} from {@code d} first reaches {@code 0} or less: about
 * {@code ceil(d / dt)} steps, one more when the rounding of the {@code double} subtraction leaves a tiny positive
 * remainder. The result depends only on the duration and the step, never on the iteration order, rendering or a
 * frontend; it is the same on every platform.
 * </p>
 *
 * <p>
 * Slot: {@value #SLOT} (ADR-0034), the last standard system, after {@code Collision} (4000) and its game reactions
 * (between 4000 and 5000); late bookkeeping of the game runs after it.
 * </p>
 *
 * <p>
 * Preconditions: the storage is the one {@link World#register(com.b3dgs.lionengine.ecs.TraitType)} returned in the
 * given world; the system is added to that world only, with {@link World#addSystem(GameSystem, int)} and
 * {@link #SLOT}. It creates one query at construction and allocates nothing per step while the command capacity of
 * the world fits. Runtime thread only.
 * </p>
 *
 * @since 11.0.0
 */
public final class LifetimeSystem implements GameSystem
{
    /** The slot of the system (ADR-0034). */
    public static final int SLOT = 5000;

    /** The world. */
    private final World world;
    /** The lifetimes. */
    private final TraitStorage<Lifetime> lifetimes;
    /** The expiring objects. */
    private final Query expiring;

    /**
     * Creates the system, without excluded trait type.
     *
     * @param world The world the system is added to.
     * @param lifetimes The storage of {@link Lifetime#TYPE} in the world.
     * @throws IllegalArgumentException If an argument is {@code null} or the storage belongs to another world.
     * @throws IllegalStateException If called during a flush, or if the world failed.
     */
    public LifetimeSystem(World world, TraitStorage<Lifetime> lifetimes)
    {
        this(world, lifetimes, MovementSystem.NO_EXCLUDED);
    }

    /**
     * Creates the system, skipping the objects that carry any of the excluded trait types (REQ-STD-011), for example
     * network proxies (ADR-0038), whose removal comes from the server.
     *
     * @param world The world the system is added to.
     * @param lifetimes The storage of {@link Lifetime#TYPE} in the world.
     * @param excluded The storages of the excluded trait types in the world, possibly none; the array is not kept.
     * @throws IllegalArgumentException If an argument is {@code null}, the excluded set contains {@code null} or
     *             {@code lifetimes}, or a storage belongs to another world.
     * @throws IllegalStateException If called during a flush, or if the world failed.
     */
    public LifetimeSystem(World world, TraitStorage<Lifetime> lifetimes, TraitStorage<?>[] excluded)
    {
        MovementSystem.check(world, lifetimes, "Lifetime");
        MovementSystem.checkExcluded(excluded);
        this.world = world;
        this.lifetimes = lifetimes;
        expiring = world.createQuery(new TraitStorage<?>[]
        {
            lifetimes
        }, excluded);
    }

    /**
     * Returns the objects with a lifetime.
     *
     * @return The query of the objects having a {@link Lifetime} and no excluded trait.
     */
    public Query getQuery()
    {
        return expiring;
    }

    /**
     * Counts down the duration of each object and requests the removal of the expired ones, applied at the flush.
     *
     * @param stepSeconds The constant step, in seconds, positive and finite (ADR-0017).
     */
    @Override
    public void update(double stepSeconds)
    {
        final int count = expiring.getCount();
        for (int i = 0; i < count; i++)
        {
            final long id = expiring.getId(i);
            if (lifetimes.get(id).countDown(stepSeconds))
            {
                world.remove(id);
            }
        }
    }
}
