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
package com.b3dgs.lionengine.examples.traits;

import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.ecs.WorldConfig;
import com.b3dgs.lionengine.traits.Gravity;
import com.b3dgs.lionengine.traits.GravitySystem;
import com.b3dgs.lionengine.traits.Lifetime;
import com.b3dgs.lionengine.traits.LifetimeSystem;
import com.b3dgs.lionengine.traits.Movable;
import com.b3dgs.lionengine.traits.MovementSystem;
import com.b3dgs.lionengine.traits.TransformCaptureSystem;
import com.b3dgs.lionengine.traits.Transformable;

/**
 * The world of {@link FallExample}: an ember thrown up that falls back under gravity, at most at its terminal
 * velocity, and burns out after its lifetime (REQ-STD-003, REQ-STD-006, REQ-STD-008, ADR-0034, ADR-0081).
 *
 * <p>
 * The game registers the four standard trait types with their {@code TYPE} constants, creates the standard systems with
 * the storages the registration returned and adds them at their slots: {@link TransformCaptureSystem} first,
 * {@link GravitySystem} at {@value GravitySystem#SLOT}, {@link MovementSystem} at {@value MovementSystem#SLOT} and
 * {@link LifetimeSystem} at {@value LifetimeSystem#SLOT}. Positions are in meters with Y up, velocities in meters per
 * second, the acceleration in meters per second squared (negative pulls down), the lifetime in seconds.
 * </p>
 *
 * @since 11.0.0
 */
public final class Fall
{
    /** The constant step, in seconds. */
    public static final double STEP_SECONDS = 1.0 / 60.0;
    /** Ember initial vertical velocity, in meters per second: thrown up. */
    public static final double THROW_VELOCITY = 1.0;
    /** Ember horizontal velocity, in meters per second. */
    public static final double DRIFT_VELOCITY = 0.5;
    /** Gravity acceleration, in meters per second squared: 0.1 m/s lost per step. */
    public static final double ACCELERATION = -6.0;
    /** Terminal velocity, in meters per second, a magnitude: reached after 40 steps. */
    public static final double TERMINAL_VELOCITY = 3.0;
    /** Ember lifetime, in seconds. */
    public static final double LIFETIME = 1.5;
    /** Ember starting height, in meters. */
    public static final double HEIGHT = 10.0;
    /** Ember size, in meters. */
    public static final double SIZE = 0.25;

    /** The world. */
    private final World world = new World(new WorldConfig(1, 1));
    /** The transformables. */
    private final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
    /** The movables. */
    private final TraitStorage<Movable> movables = world.register(Movable.TYPE);
    /** The gravities. */
    private final TraitStorage<Gravity> gravities = world.register(Gravity.TYPE);
    /** The lifetimes. */
    private final TraitStorage<Lifetime> lifetimes = world.register(Lifetime.TYPE);
    /** The ember. */
    private final long ember = world.create();

    /**
     * Creates the world, the ember and the systems, and flushes.
     */
    public Fall()
    {
        final Transformable transformable = transformables.add(ember);
        transformable.setPosition(0.0, HEIGHT);
        transformable.setPreviousPosition(0.0, HEIGHT);
        transformable.setSize(SIZE, SIZE);
        movables.add(ember).setVelocity(DRIFT_VELOCITY, THROW_VELOCITY);
        final Gravity gravity = gravities.add(ember);
        gravity.setAcceleration(ACCELERATION);
        gravity.setTerminalVelocity(TERMINAL_VELOCITY);
        lifetimes.add(ember).setDuration(LIFETIME);
        world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);
        world.addSystem(new GravitySystem(world, movables, gravities), GravitySystem.SLOT);
        world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
        world.addSystem(new LifetimeSystem(world, lifetimes), LifetimeSystem.SLOT);
        world.flush();
    }

    /**
     * Plays one step: capture, gravity, movement, lifetime, flush.
     */
    public void step()
    {
        world.update(STEP_SECONDS);
    }

    /**
     * Tells whether the ember still burns: its removal is applied by the flush of the step its lifetime expires.
     *
     * @return {@code true} while the ember exists.
     */
    public boolean isBurning()
    {
        return world.isAlive(ember);
    }

    /**
     * Returns the ember position and size.
     *
     * @return The ember transformable, as of the last step; only while {@link #isBurning()}.
     */
    public Transformable getEmber()
    {
        return transformables.get(ember);
    }

    /**
     * Returns the ember velocity.
     *
     * @return The ember movable, as of the last step; only while {@link #isBurning()}.
     */
    public Movable getEmberMovable()
    {
        return movables.get(ember);
    }

    /**
     * Returns the ember remaining lifetime.
     *
     * @return The ember lifetime, as of the last step; only while {@link #isBurning()}.
     */
    public Lifetime getEmberLifetime()
    {
        return lifetimes.get(ember);
    }

    /**
     * Returns the objects of the world.
     *
     * @return The number of objects, {@code 0} once the ember burned out.
     */
    public int getObjectCount()
    {
        return world.getObjectCount();
    }
}
