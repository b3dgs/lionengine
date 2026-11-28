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
 * Standard trait: the jump of an object and its contact with the ground (REQ-STD-004, REQ-STD-010, ADR-0011, ADR-0013,
 * ADR-0030, decision 7, ADR-0034, ADR-0083).
 *
 * <p>
 * Data only, no logic. Fields:
 * </p>
 * <ul>
 * <li>the impulse ({@link #getImpulse()}), in meters per second, positive or zero (Y up, ADR-0013): the upward
 * velocity a jump gives;</li>
 * <li>the grounded flag ({@link #isGrounded()}): the object stands on a solid tile. {@link CollisionSystem} (slot
 * {@value CollisionSystem#SLOT}) writes it at each step for the objects it pushes out of the tiles: {@code true} when
 * the step pushed the object up out of a tile below it (a contact below), {@code false} otherwise. The game may also
 * set it, for example for an object standing on another object (ADR-0030, decision 7);</li>
 * <li>the coyote time ({@link #getCoyoteTime()}), in seconds, positive or zero, default {@value #DEFAULT_COYOTE_TIME}:
 * how long after leaving the ground a jump is still accepted;</li>
 * <li>the jump buffer ({@link #getJumpBuffer()}), in seconds, positive or zero, default
 * {@value #DEFAULT_JUMP_BUFFER}: how long a jump requested in the air is kept until the object meets the ground;</li>
 * <li>the variable-height cut factor ({@link #getCutFactor()}), in [0, 1], default {@value #DEFAULT_CUT_FACTOR}, and
 * its switch ({@link #isVariableHeight()}), off by default: when on, releasing the jump while ascending multiplies the
 * upward velocity by the factor (ADR-0034).</li>
 * </ul>
 *
 * <p>
 * A new or reset instance has no impulse ({@code 0}), is not grounded, has the default coyote time, jump buffer and cut
 * factor, and no variable height.
 * </p>
 *
 * <p>
 * Interactions: {@link CollisionSystem} writes the grounded flag of the objects it pushes out of the tiles (non-trigger
 * objects having {@link Transformable}, {@link Collidable} and {@link Movable}, with a {@link
 * com.b3dgs.lionengine.physics.ShapeQuery} set); the {@code Jump} system (slot 1000), which applies the impulse, the
 * coyote time, the jump buffer and the cut, comes with standard-traits batch 5: until then the game reads the flag and
 * sets the velocity of the {@link Movable} itself. Registered in a world with {@link #TYPE}; instances are pooled by
 * the world and must not be kept after their removal. Runtime thread only.
 * </p>
 *
 * <p>
 * Memory: 48 bytes per instance on the desktop JVM (64-bit HotSpot, compressed class pointers), measured with the
 * per-thread allocation counter by {@code TraitMemoryTest} (ADR-0034); the pool also holds one reference per instance.
 * </p>
 *
 * @since 11.0.0
 */
public final class Jumpable
{
    /** The default coyote time, in seconds (ADR-0030, decision 7). */
    public static final double DEFAULT_COYOTE_TIME = 0.1;
    /** The default jump buffer, in seconds (ADR-0030, decision 7). */
    public static final double DEFAULT_JUMP_BUFFER = 0.1;
    /** The default variable-height cut factor (ADR-0034). */
    public static final double DEFAULT_CUT_FACTOR = 0.5;

    /**
     * The trait type to register in a world: creates instances in the reset state and resets them to it on removal.
     * Shared by every world, which each register it once.
     */
    public static final TraitType<Jumpable> TYPE = new TraitType<>()
    {
        @Override
        public Jumpable create()
        {
            return new Jumpable();
        }

        @Override
        public void reset(Jumpable trait)
        {
            trait.impulse = 0.0;
            trait.coyoteTime = DEFAULT_COYOTE_TIME;
            trait.jumpBuffer = DEFAULT_JUMP_BUFFER;
            trait.cutFactor = DEFAULT_CUT_FACTOR;
            trait.variableHeight = false;
            trait.grounded = false;
        }
    };

    /** Upward velocity of a jump, in meters per second. */
    private double impulse;
    /** Time a jump is accepted after leaving the ground, in seconds. */
    private double coyoteTime = DEFAULT_COYOTE_TIME;
    /** Time a jump request is kept in the air, in seconds. */
    private double jumpBuffer = DEFAULT_JUMP_BUFFER;
    /** Factor of the upward velocity on an early release. */
    private double cutFactor = DEFAULT_CUT_FACTOR;
    /** Variable height switch. */
    private boolean variableHeight;
    /** Contact below with the ground. */
    private boolean grounded;

    /**
     * Creates a jumpable in its reset state.
     */
    public Jumpable()
    {
        super();
    }

    /**
     * Returns the impulse.
     *
     * @return The upward velocity of a jump, in meters per second, positive or zero.
     */
    public double getImpulse()
    {
        return impulse;
    }

    /**
     * Sets the impulse.
     *
     * @param impulse The upward velocity of a jump, in meters per second, positive or zero and finite.
     * @throws IllegalArgumentException If the impulse is negative or not finite.
     */
    public void setImpulse(double impulse)
    {
        checkPositive(impulse, "Jump impulse");
        this.impulse = impulse;
    }

    /**
     * Returns the grounded flag.
     *
     * @return {@code true} if the object stands on the ground: written by {@link CollisionSystem} at each step for the
     *         objects it pushes out of the tiles, or set by the game.
     */
    public boolean isGrounded()
    {
        return grounded;
    }

    /**
     * Sets the grounded flag; {@link CollisionSystem} overwrites it at its next step for the objects it pushes out of
     * the tiles.
     *
     * @param grounded {@code true} if the object stands on the ground.
     */
    public void setGrounded(boolean grounded)
    {
        this.grounded = grounded;
    }

    /**
     * Returns the coyote time.
     *
     * @return The time a jump is still accepted after leaving the ground, in seconds, positive or zero.
     */
    public double getCoyoteTime()
    {
        return coyoteTime;
    }

    /**
     * Sets the coyote time.
     *
     * @param coyoteTime The time a jump is still accepted after leaving the ground, in seconds, positive or zero and
     *            finite ({@link #DEFAULT_COYOTE_TIME} by default).
     * @throws IllegalArgumentException If the time is negative or not finite.
     */
    public void setCoyoteTime(double coyoteTime)
    {
        checkPositive(coyoteTime, "Coyote time");
        this.coyoteTime = coyoteTime;
    }

    /**
     * Returns the jump buffer.
     *
     * @return The time a jump request is kept in the air, in seconds, positive or zero.
     */
    public double getJumpBuffer()
    {
        return jumpBuffer;
    }

    /**
     * Sets the jump buffer.
     *
     * @param jumpBuffer The time a jump request is kept in the air, in seconds, positive or zero and finite
     *            ({@link #DEFAULT_JUMP_BUFFER} by default).
     * @throws IllegalArgumentException If the time is negative or not finite.
     */
    public void setJumpBuffer(double jumpBuffer)
    {
        checkPositive(jumpBuffer, "Jump buffer");
        this.jumpBuffer = jumpBuffer;
    }

    /**
     * Returns the variable-height cut factor.
     *
     * @return The factor of the upward velocity on an early release, in [0, 1].
     */
    public double getCutFactor()
    {
        return cutFactor;
    }

    /**
     * Sets the variable-height cut factor; it applies only while {@link #isVariableHeight()} is {@code true}.
     *
     * @param cutFactor The factor of the upward velocity on an early release, in [0, 1] ({@link #DEFAULT_CUT_FACTOR} by
     *            default).
     * @throws IllegalArgumentException If the factor is outside [0, 1] or not a number.
     */
    public void setCutFactor(double cutFactor)
    {
        if (cutFactor < 0.0 || cutFactor > 1.0 || Double.isNaN(cutFactor))
        {
            throw new IllegalArgumentException("Cut factor is outside [0, 1]: " + cutFactor);
        }
        this.cutFactor = cutFactor;
    }

    /**
     * Returns the variable-height switch.
     *
     * @return {@code true} if an early release while ascending cuts the upward velocity by the cut factor.
     */
    public boolean isVariableHeight()
    {
        return variableHeight;
    }

    /**
     * Sets the variable-height switch.
     *
     * @param variableHeight {@code true} so that an early release while ascending cuts the upward velocity by the cut
     *            factor, {@code false} (default) for a fixed height.
     */
    public void setVariableHeight(boolean variableHeight)
    {
        this.variableHeight = variableHeight;
    }

    /**
     * Checks a value positive or zero and finite.
     *
     * @param value The value.
     * @param name The value name, for the message.
     * @throws IllegalArgumentException If the value is negative or not finite.
     */
    private static void checkPositive(double value, String name)
    {
        if (value < 0.0 || !Double.isFinite(value))
        {
            throw new IllegalArgumentException(name + " is negative or not finite: " + value);
        }
    }
}
