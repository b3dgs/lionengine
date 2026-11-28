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
 * Standard system {@code Jump}: applies the jump impulse of each jumping object, with the coyote time, the jump buffer
 * and the variable-height cut (REQ-STD-004, REQ-STD-008, REQ-STD-011, ADR-0011, ADR-0013, ADR-0017, ADR-0030, decision
 * 7, ADR-0034, ADR-0084).
 *
 * <p>
 * At each step, for each object having a {@link Movable} and a {@link Jumpable} and none of the excluded trait types,
 * in the iteration order of its query:
 * </p>
 * <ol>
 * <li>the jump held flag of the {@link Jumpable}, written by the game before this system, is compared with its value
 * at the previous step of this system: held now and not before is a <em>press</em>, the opposite a
 * <em>release</em>;</li>
 * <li>the coyote time remaining is set to the coyote time if the object is grounded, otherwise counted down by the step
 * (never below {@code 0}): the coyote time starts at the last step seen grounded;</li>
 * <li>a press sets the jump buffer remaining to the jump buffer and requests a jump in this step; without a press, the
 * buffer remaining is counted down by the step (never below {@code 0}) and a jump is requested while it stays above
 * {@code 0};</li>
 * <li>a requested jump is applied if the object is grounded or its coyote time remaining is above {@code 0}: the
 * vertical velocity of the {@link Movable} is <em>replaced</em> by the impulse (the horizontal velocity is kept), the
 * grounded flag is cleared, the coyote and buffer times remaining are set to {@code 0} (each window is consumed once:
 * no second jump in the air) and the jump can be cut;</li>
 * <li>otherwise, on a release while the jump can be cut, if the variable height is on and the vertical velocity is
 * above {@code 0} (ascending, Y up), the vertical velocity is multiplied by the cut factor; a release, or a vertical
 * velocity at or below {@code 0}, ends the jump that could be cut, so the cut applies at most once per jump.</li>
 * </ol>
 *
 * <p>
 * A press in the air with no coyote time left is therefore kept for the jump buffer and jumps at the first step of this
 * system that sees the object grounded within it. A held jump does not jump again on landing: only a press does.
 * The impulse is positive (Y up, ADR-0013). Times count in steps of this system: with a window {@code w} and a step
 * {@code dt}, a jump is accepted at the {@code k}-th step after the last grounded step (coyote) or after the press
 * (buffer) while {@code w - k * dt}, computed by repeated {@code double} subtraction, stays above {@code 0}; a window
 * that is an exact multiple of the step may accept one more step when the subtraction leaves a remainder.
 * </p>
 *
 * <p>
 * Interactions: the grounded flag is the one {@link CollisionSystem} (slot {@value CollisionSystem#SLOT}) wrote at the
 * previous step, true if and only if its tile push-out pushed the object up out of a floor, or the one set by the game.
 * So a buffered press meets the ground at the step after the collision landed the object. {@link GravitySystem} (slot
 * {@value GravitySystem#SLOT}) then accelerates the new velocity in the same step and {@link MovementSystem} (slot
 * {@value MovementSystem#SLOT}) clamps it to the maximum speed and integrates it; a jump whose impulse does not
 * overcome the gravity of one step leaves the object grounded. No {@link Transformable} or {@link Collidable} is
 * needed by this system. Units: meters per second, seconds. The result depends only on the trait values and the step,
 * never on the iteration order, rendering or a frontend.
 * </p>
 *
 * <p>
 * Slot: {@value #SLOT} (ADR-0034), first of the standard systems; the game writes the jump held flag below it (input
 * and intent), and its decisions that set velocities run between 1000 and 2000.
 * </p>
 *
 * <p>
 * Preconditions: the storages are the ones {@link World#register(com.b3dgs.lionengine.ecs.TraitType)} returned in the
 * given world; the system is added to that world only, with {@link World#addSystem(GameSystem, int)} and
 * {@link #SLOT}. It creates one query at construction and allocates nothing per step. An object outside the query (an
 * excluded trait added) keeps its jump state unchanged. Runtime thread only.
 * </p>
 *
 * @since 11.0.0
 */
public final class JumpSystem implements GameSystem
{
    /** The slot of the system (ADR-0034). */
    public static final int SLOT = 1000;

    /** The movables. */
    private final TraitStorage<Movable> movables;
    /** The jumpables. */
    private final TraitStorage<Jumpable> jumpables;
    /** The jumping objects. */
    private final Query jumping;

    /**
     * Creates the system, without excluded trait type.
     *
     * @param world The world the system is added to.
     * @param movables The storage of {@link Movable#TYPE} in the world.
     * @param jumpables The storage of {@link Jumpable#TYPE} in the world.
     * @throws IllegalArgumentException If an argument is {@code null} or a storage belongs to another world.
     * @throws IllegalStateException If called during a flush, or if the world failed.
     */
    public JumpSystem(World world, TraitStorage<Movable> movables, TraitStorage<Jumpable> jumpables)
    {
        this(world, movables, jumpables, MovementSystem.NO_EXCLUDED);
    }

    /**
     * Creates the system, skipping the objects that carry any of the excluded trait types (REQ-STD-011), for example
     * network proxies (ADR-0038).
     *
     * @param world The world the system is added to.
     * @param movables The storage of {@link Movable#TYPE} in the world.
     * @param jumpables The storage of {@link Jumpable#TYPE} in the world.
     * @param excluded The storages of the excluded trait types in the world, possibly none; the array is not kept.
     * @throws IllegalArgumentException If an argument is {@code null}, the excluded set contains {@code null},
     *             {@code movables} or {@code jumpables}, or a storage belongs to another world.
     * @throws IllegalStateException If called during a flush, or if the world failed.
     */
    public JumpSystem(World world,
                      TraitStorage<Movable> movables,
                      TraitStorage<Jumpable> jumpables,
                      TraitStorage<?>[] excluded)
    {
        MovementSystem.check(world, movables, "Movable");
        MovementSystem.check(world, jumpables, "Jumpable");
        MovementSystem.checkExcluded(excluded);
        this.movables = movables;
        this.jumpables = jumpables;
        jumping = world.createQuery(new TraitStorage<?>[]
        {
            movables, jumpables
        }, excluded);
    }

    /**
     * Returns the jumping objects.
     *
     * @return The query of the objects having a {@link Movable}, a {@link Jumpable} and no excluded trait.
     */
    public Query getQuery()
    {
        return jumping;
    }

    /**
     * Applies the jump rules to each jumping object.
     *
     * @param stepSeconds The constant step, in seconds, positive and finite (ADR-0017).
     */
    @Override
    public void update(double stepSeconds)
    {
        final int count = jumping.getCount();
        for (int i = 0; i < count; i++)
        {
            final long id = jumping.getId(i);
            final Movable movable = movables.get(id);
            movable.setVelocity(movable.getVelocityX(), jumpables.get(id).update(movable.getVelocityY(), stepSeconds));
        }
    }
}
