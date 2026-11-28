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

import com.b3dgs.lionengine.ecs.TraitType;

/**
 * Standard trait: the velocity of an object and its speed limit (REQ-STD-002, ADR-0011, ADR-0013, ADR-0014, ADR-0030,
 * ADR-0034, ADR-0080).
 *
 * <p>
 * Data only, no logic. Fields, all {@code double}:
 * </p>
 * <ul>
 * <li>the velocity ({@link #getVelocityX()}, {@link #getVelocityY()}), in meters per second, Y up (ADR-0013);</li>
 * <li>the maximum speed ({@link #getMaxSpeed()}), in meters per second: a limit on the <b>magnitude</b> of the
 * velocity, positive or zero, {@link Double#POSITIVE_INFINITY} for no limit.</li>
 * </ul>
 *
 * <p>
 * A new or reset instance has a zero velocity and no limit ({@link Double#POSITIVE_INFINITY}). The velocity is not
 * checked: the game keeps it finite.
 * </p>
 *
 * <p>
 * Interactions: {@link MovementSystem} (slot {@value MovementSystem#SLOT}) clamps the velocity of each object having a
 * {@link Movable} and a {@link Transformable} to the maximum speed, direction kept, then integrates it into the
 * position. Systems that set velocities run before it (game decisions between 1000 and 2000, ADR-0034); a velocity
 * read after it is clamped. The clamp also keeps the displacement per step bounded, which the collision of later
 * batches needs (ADR-0030, decision 6). Registered in a world with {@link #TYPE}; instances are pooled by the world and
 * must not be kept after their removal. Runtime thread only.
 * </p>
 *
 * <p>
 * Memory: 40 bytes per instance on the desktop JVM (64-bit HotSpot, compressed class pointers), measured with the
 * per-thread allocation counter by {@code TraitMemoryTest} (ADR-0034); the pool also holds one reference per instance.
 * </p>
 *
 * @since 11.0.0
 */
public final class Movable
{
    /**
     * The trait type to register in a world: creates instances with a zero velocity and no speed limit, and resets
     * them to that state on removal. Shared by every world, which each register it once.
     */
    public static final TraitType<Movable> TYPE = new TraitType<>()
    {
        @Override
        public Movable create()
        {
            return new Movable();
        }

        @Override
        public void reset(Movable trait)
        {
            trait.velocityX = 0.0;
            trait.velocityY = 0.0;
            trait.maxSpeed = Double.POSITIVE_INFINITY;
        }
    };

    /** Horizontal velocity, in meters per second. */
    private double velocityX;
    /** Vertical velocity, in meters per second, Y up. */
    private double velocityY;
    /** Maximum magnitude of the velocity, in meters per second. */
    private double maxSpeed = Double.POSITIVE_INFINITY;

    /**
     * Creates a movable in its reset state: zero velocity, no speed limit.
     */
    public Movable()
    {
        super();
    }

    /**
     * Returns the horizontal velocity.
     *
     * @return The horizontal velocity, in meters per second.
     */
    public double getVelocityX()
    {
        return velocityX;
    }

    /**
     * Returns the vertical velocity.
     *
     * @return The vertical velocity, in meters per second, Y up.
     */
    public double getVelocityY()
    {
        return velocityY;
    }

    /**
     * Sets the velocity; {@link MovementSystem} clamps it to the maximum speed when it moves the object.
     *
     * @param velocityX The horizontal velocity, in meters per second.
     * @param velocityY The vertical velocity, in meters per second, Y up.
     */
    public void setVelocity(double velocityX, double velocityY)
    {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    /**
     * Returns the maximum speed.
     *
     * @return The maximum magnitude of the velocity, in meters per second, {@link Double#POSITIVE_INFINITY} for none.
     */
    public double getMaxSpeed()
    {
        return maxSpeed;
    }

    /**
     * Sets the maximum speed.
     *
     * @param maxSpeed The maximum magnitude of the velocity, in meters per second, positive or zero,
     *            {@link Double#POSITIVE_INFINITY} for none.
     * @throws IllegalArgumentException If the maximum speed is negative or not a number.
     */
    public void setMaxSpeed(double maxSpeed)
    {
        if (maxSpeed < 0.0 || Double.isNaN(maxSpeed))
        {
            throw new IllegalArgumentException("Maximum speed is negative or not a number: " + maxSpeed);
        }
        this.maxSpeed = maxSpeed;
    }

    /**
     * Scales the velocity down to the maximum speed if its magnitude exceeds it, direction kept.
     */
    void clamp()
    {
        final double squared = velocityX * velocityX + velocityY * velocityY;
        if (squared > maxSpeed * maxSpeed)
        {
            final double factor = maxSpeed / Math.sqrt(squared);
            velocityX *= factor;
            velocityY *= factor;
        }
    }
}
