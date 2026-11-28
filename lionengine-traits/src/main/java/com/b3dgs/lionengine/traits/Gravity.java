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
 * Standard trait: the gravity pulling an object and its terminal velocity (REQ-STD-003, ADR-0011, ADR-0013, ADR-0014,
 * ADR-0030, decision 7, ADR-0034, ADR-0081).
 *
 * <p>
 * Data only, no logic. Fields, all {@code double}:
 * </p>
 * <ul>
 * <li>the acceleration ({@link #getAcceleration()}), in meters per second squared, along Y only: negative pulls down
 * (Y up, ADR-0013), positive pulls up, {@code 0} pulls nowhere;</li>
 * <li>the terminal velocity ({@link #getTerminalVelocity()}), in meters per second: a <b>magnitude</b>, positive or
 * zero, {@link Double#POSITIVE_INFINITY} for no limit. It limits the vertical velocity in the direction of the
 * acceleration only: with a negative acceleration the gravity never brings the vertical velocity below
 * {@code -terminalVelocity}, with a positive one never above {@code +terminalVelocity}.</li>
 * </ul>
 *
 * <p>
 * A new or reset instance has no acceleration ({@code 0}) and no limit ({@link Double#POSITIVE_INFINITY}). The
 * acceleration is not checked: the game keeps it finite.
 * </p>
 *
 * <p>
 * Interactions: {@link GravitySystem} (slot {@value GravitySystem#SLOT}) adds {@code acceleration * stepSeconds} to the
 * vertical velocity of the {@link Movable} of each object having both traits, never beyond the terminal velocity; an
 * object without {@link Movable} is not accelerated. A vertical velocity already beyond the terminal velocity, set by
 * the game (a dash, a dive), is neither cut nor increased by the gravity; {@link Movable#getMaxSpeed()} still clamps
 * the whole speed in {@link MovementSystem}. Registered in a world with {@link #TYPE}; instances are pooled by the
 * world and must not be kept after their removal. Runtime thread only.
 * </p>
 *
 * <p>
 * Memory: 32 bytes per instance on the desktop JVM (64-bit HotSpot, compressed class pointers), measured with the
 * per-thread allocation counter by {@code TraitMemoryTest} (ADR-0034); the pool also holds one reference per instance.
 * </p>
 *
 * @since 11.0.0
 */
public final class Gravity
{
    /**
     * The trait type to register in a world: creates instances with no acceleration and no terminal velocity, and
     * resets them to that state on removal. Shared by every world, which each register it once.
     */
    public static final TraitType<Gravity> TYPE = new TraitType<>()
    {
        @Override
        public Gravity create()
        {
            return new Gravity();
        }

        @Override
        public void reset(Gravity trait)
        {
            trait.acceleration = 0.0;
            trait.terminalVelocity = Double.POSITIVE_INFINITY;
        }
    };

    /** Vertical acceleration, in meters per second squared, negative pulls down. */
    private double acceleration;
    /** Limit of the vertical velocity in the direction of the acceleration, in meters per second, a magnitude. */
    private double terminalVelocity = Double.POSITIVE_INFINITY;

    /**
     * Creates a gravity in its reset state: no acceleration, no terminal velocity.
     */
    public Gravity()
    {
        super();
    }

    /**
     * Returns the acceleration.
     *
     * @return The vertical acceleration, in meters per second squared, negative pulls down.
     */
    public double getAcceleration()
    {
        return acceleration;
    }

    /**
     * Sets the acceleration.
     *
     * @param acceleration The vertical acceleration, in meters per second squared, negative pulls down (Y up); kept
     *            finite by the game.
     */
    public void setAcceleration(double acceleration)
    {
        this.acceleration = acceleration;
    }

    /**
     * Returns the terminal velocity.
     *
     * @return The limit of the vertical velocity in the direction of the acceleration, in meters per second, a
     *         magnitude, {@link Double#POSITIVE_INFINITY} for none.
     */
    public double getTerminalVelocity()
    {
        return terminalVelocity;
    }

    /**
     * Sets the terminal velocity.
     *
     * @param terminalVelocity The limit of the vertical velocity in the direction of the acceleration, in meters per
     *            second, a magnitude, positive or zero, {@link Double#POSITIVE_INFINITY} for none.
     * @throws IllegalArgumentException If the terminal velocity is negative or not a number.
     */
    public void setTerminalVelocity(double terminalVelocity)
    {
        if (terminalVelocity < 0.0 || Double.isNaN(terminalVelocity))
        {
            throw new IllegalArgumentException("Terminal velocity is negative or not a number: " + terminalVelocity);
        }
        this.terminalVelocity = terminalVelocity;
    }

    /**
     * Returns a vertical velocity accelerated for one step, never beyond the terminal velocity in the direction of the
     * acceleration; a velocity already beyond it is kept.
     *
     * @param velocityY The vertical velocity, in meters per second.
     * @param stepSeconds The step, in seconds.
     * @return The accelerated vertical velocity, in meters per second.
     */
    double accelerate(double velocityY, double stepSeconds)
    {
        final double accelerated = velocityY + acceleration * stepSeconds;
        final double result;
        if (acceleration < 0.0 && accelerated < -terminalVelocity)
        {
            result = Math.min(velocityY, -terminalVelocity);
        }
        else if (acceleration > 0.0 && accelerated > terminalVelocity)
        {
            result = Math.max(velocityY, terminalVelocity);
        }
        else
        {
            result = accelerated;
        }
        return result;
    }
}
