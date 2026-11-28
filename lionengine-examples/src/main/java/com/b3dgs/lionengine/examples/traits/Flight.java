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

import com.b3dgs.lionengine.ecs.GameSystem;
import com.b3dgs.lionengine.ecs.TraitStorage;
import com.b3dgs.lionengine.ecs.World;
import com.b3dgs.lionengine.ecs.WorldConfig;
import com.b3dgs.lionengine.traits.Movable;
import com.b3dgs.lionengine.traits.MovementSystem;
import com.b3dgs.lionengine.traits.TransformCaptureSystem;
import com.b3dgs.lionengine.traits.Transformable;

/**
 * The world of {@link MovementExample}: a drone flying at a constant velocity and a rocket accelerated by a game
 * system up to its maximum speed (REQ-STD-001, REQ-STD-002, ADR-0034, ADR-0080).
 *
 * <p>
 * The game registers the two standard trait types with their {@code TYPE} constants, creates the standard systems
 * with the storages the registration returned and adds them at their slots: {@link TransformCaptureSystem} first,
 * then its own thrust system at {@link #THRUST_SLOT}, in the range of the decisions that set velocities (between 1000
 * and 2000, ADR-0034), then {@link MovementSystem} at {@value MovementSystem#SLOT}, which clamps each speed and moves
 * each object. Positions are in meters with Y up, velocities in meters per second.
 * </p>
 *
 * @since 11.0.0
 */
public final class Flight
{
    /** Slot of the thrust system: decisions that set velocities. */
    public static final int THRUST_SLOT = 1500;
    /** The constant step, in seconds. */
    public static final double STEP_SECONDS = 1.0 / 60.0;
    /** Drone velocity, in meters per second: a speed of 5 m/s, under its limit. */
    public static final double DRONE_VELOCITY_X = 3.0;
    /** Drone vertical velocity, in meters per second. */
    public static final double DRONE_VELOCITY_Y = 4.0;
    /** Drone maximum speed, in meters per second. */
    public static final double DRONE_MAX_SPEED = 10.0;
    /** Rocket horizontal acceleration given by the thrust, in meters per second squared. */
    public static final double THRUST = 6.0;
    /** Rocket maximum speed, in meters per second. */
    public static final double ROCKET_MAX_SPEED = 5.0;
    /** Size of both objects, in meters. */
    public static final double SIZE = 1.0;

    /** The world. */
    private final World world = new World(new WorldConfig(2, 2));
    /** The transformables. */
    private final TraitStorage<Transformable> transformables = world.register(Transformable.TYPE);
    /** The movables. */
    private final TraitStorage<Movable> movables = world.register(Movable.TYPE);
    /** The drone. */
    private final long drone = world.create();
    /** The rocket. */
    private final long rocket = world.create();
    /** The thrust system, kept to be removed or disabled by the game. */
    private final GameSystem thrustSystem = this::thrust;

    /**
     * Creates the world, the two objects at the origin and the systems, and flushes.
     */
    public Flight()
    {
        place(drone);
        final Movable droneMovable = movables.add(drone);
        droneMovable.setVelocity(DRONE_VELOCITY_X, DRONE_VELOCITY_Y);
        droneMovable.setMaxSpeed(DRONE_MAX_SPEED);
        place(rocket);
        movables.add(rocket).setMaxSpeed(ROCKET_MAX_SPEED);
        world.addSystem(new TransformCaptureSystem(world, transformables), TransformCaptureSystem.SLOT);
        world.addSystem(thrustSystem, THRUST_SLOT);
        world.addSystem(new MovementSystem(world, transformables, movables), MovementSystem.SLOT);
        world.flush();
    }

    /**
     * Plays one step: capture, thrust, movement, flush.
     */
    public void step()
    {
        world.update(STEP_SECONDS);
    }

    /**
     * Returns the drone position and size.
     *
     * @return The drone transformable, as of the last step.
     */
    public Transformable getDrone()
    {
        return transformables.get(drone);
    }

    /**
     * Returns the rocket position and size.
     *
     * @return The rocket transformable, as of the last step.
     */
    public Transformable getRocket()
    {
        return transformables.get(rocket);
    }

    /**
     * Returns the rocket velocity.
     *
     * @return The rocket movable, its velocity clamped by the last step.
     */
    public Movable getRocketMovable()
    {
        return movables.get(rocket);
    }

    /**
     * Returns the horizontal position to draw between the previous and the current step, as rendering does with the
     * interpolation factor of the loop (ADR-0017).
     *
     * @param transformable The transformable.
     * @param alpha The interpolation factor, from {@code 0} (previous position) to {@code 1} (current position).
     * @return The interpolated horizontal position, in meters; presentation only, never fed back.
     */
    public static double interpolateX(Transformable transformable, double alpha)
    {
        return transformable.getPreviousX() + (transformable.getX() - transformable.getPreviousX()) * alpha;
    }

    /**
     * Places an object at the origin: its position and its previous position, so that the first frame does not slide.
     *
     * @param id The object.
     */
    private void place(long id)
    {
        final Transformable transformable = transformables.add(id);
        transformable.setPosition(0.0, 0.0);
        transformable.setPreviousPosition(0.0, 0.0);
        transformable.setSize(SIZE, SIZE);
    }

    /**
     * The thrust system: accelerates the rocket to the right; the movement clamps its speed.
     *
     * @param stepSeconds The step, in seconds.
     */
    private void thrust(double stepSeconds)
    {
        final Movable movable = movables.get(rocket);
        movable.setVelocity(movable.getVelocityX() + THRUST * stepSeconds, movable.getVelocityY());
    }
}
