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
 * ADR-0030, decision 7, ADR-0034, ADR-0083, ADR-0084).
 *
 * <p>
 * Data only, the jump rules being applied by {@link JumpSystem}. Settings:
 * </p>
 * <ul>
 * <li>the impulse ({@link #getImpulse()}), in meters per second, positive or zero (Y up, ADR-0013): the upward
 * velocity a jump gives;</li>
 * <li>the coyote time ({@link #getCoyoteTime()}), in seconds, positive or zero, default {@value #DEFAULT_COYOTE_TIME}:
 * how long after the last step seen grounded a jump is still accepted;</li>
 * <li>the jump buffer ({@link #getJumpBuffer()}), in seconds, positive or zero, default
 * {@value #DEFAULT_JUMP_BUFFER}: how long a jump pressed in the air is kept until the object meets the ground;</li>
 * <li>the variable-height cut factor ({@link #getCutFactor()}), in [0, 1], default {@value #DEFAULT_CUT_FACTOR}, and
 * its switch ({@link #isVariableHeight()}), off by default: when on, releasing the jump while ascending multiplies the
 * upward velocity by the factor, once per jump (ADR-0034).</li>
 * </ul>
 *
 * <p>
 * State, written at each step:
 * </p>
 * <ul>
 * <li>the grounded flag ({@link #isGrounded()}): the object stands on a solid tile. {@link CollisionSystem} (slot
 * {@value CollisionSystem#SLOT}) writes it at each step for the objects it pushes out of the tiles: {@code true} when
 * the step pushed the object up out of a tile below it (a contact below), {@code false} otherwise; {@link JumpSystem}
 * clears it when the object jumps. The game may also set it, for example for an object standing on another object
 * (ADR-0030, decision 7);</li>
 * <li>the jump held flag ({@link #isJumpHeld()}), written by the game at each step from its jump action, before
 * {@link JumpSystem} (a game system below slot {@value JumpSystem#SLOT}, REQ-MP-002): the system detects the press
 * and the release from its value at its previous step, so nothing has to be cleared;</li>
 * <li>the coyote and buffer times remaining ({@link #getCoyoteRemaining()}, {@link #getBufferRemaining()}), in
 * seconds, read only, counted down by {@link JumpSystem}.</li>
 * </ul>
 *
 * <p>
 * A new or reset instance has no impulse ({@code 0}), is not grounded, has the default coyote time, jump buffer and cut
 * factor, no variable height, the jump not held, no time remaining ({@code 0}) and no jump to cut.
 * </p>
 *
 * <p>
 * Interactions: {@link CollisionSystem} writes the grounded flag of the objects it pushes out of the tiles (non-trigger
 * objects having {@link Transformable}, {@link Collidable} and {@link Movable}, with a {@link
 * com.b3dgs.lionengine.physics.ShapeQuery} set); {@link JumpSystem} (slot {@value JumpSystem#SLOT}) reads it, written
 * by the previous step, and sets the vertical velocity of the {@link Movable}. Registered in a world with
 * {@link #TYPE}; instances are pooled by the world and must not be kept after their removal. Runtime thread only.
 * </p>
 *
 * <p>
 * Memory: 72 bytes per instance on the desktop JVM (64-bit HotSpot, compressed class pointers), measured with the
 * per-thread allocation counter by {@code TraitMemoryTest} (ADR-0034, ADR-0084); the pool also holds one reference per
 * instance.
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
            trait.clear();
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
    /** Jump held by the game. */
    private boolean jumpHeld;
    /** Jump held at the previous step of the system. */
    private boolean heldBefore;
    /** Coyote time remaining, in seconds. */
    private double coyoteRemaining;
    /** Jump buffer remaining, in seconds. */
    private double bufferRemaining;
    /** Ascending jump not cut yet. */
    private boolean cutPending;

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
     * Returns the jump held flag.
     *
     * @return {@code true} if the game holds the jump at this step.
     */
    public boolean isJumpHeld()
    {
        return jumpHeld;
    }

    /**
     * Sets the jump held flag, from the jump action of the game, at each step before {@link JumpSystem}: a change from
     * released to held is a press, from held to released a release, both seen by the next step of the system.
     *
     * @param jumpHeld {@code true} while the jump is held.
     */
    public void setJumpHeld(boolean jumpHeld)
    {
        this.jumpHeld = jumpHeld;
    }

    /**
     * Returns the coyote time remaining.
     *
     * @return The time a jump is still accepted in the air, in seconds, in [0, coyote time]: set to the coyote time at
     *         each step of {@link JumpSystem} that sees the object grounded, counted down by the step at each step that
     *         does not, set to {@code 0} by a jump.
     */
    public double getCoyoteRemaining()
    {
        return coyoteRemaining;
    }

    /**
     * Returns the jump buffer remaining.
     *
     * @return The time a pressed jump is still kept, in seconds, in [0, jump buffer]: set to the jump buffer by a
     *         press, counted down by the step at each later step of {@link JumpSystem}, set to {@code 0} by a jump.
     */
    public double getBufferRemaining()
    {
        return bufferRemaining;
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
     * Applies the jump rules of one step of {@link JumpSystem} (ADR-0084) and returns the vertical velocity.
     *
     * @param velocityY The vertical velocity, in meters per second.
     * @param stepSeconds The step, in seconds.
     * @return The vertical velocity after the jump or the cut, in meters per second.
     */
    double update(double velocityY, double stepSeconds)
    {
        final boolean pressed = jumpHeld && !heldBefore;
        final boolean released = heldBefore && !jumpHeld;
        heldBefore = jumpHeld;
        final boolean requested = countDown(pressed, stepSeconds);
        final double result;
        if (requested && (grounded || coyoteRemaining > 0.0))
        {
            result = impulse;
            grounded = false;
            coyoteRemaining = 0.0;
            bufferRemaining = 0.0;
            cutPending = true;
        }
        else
        {
            result = cut(velocityY, released);
        }
        return result;
    }

    /**
     * Counts the coyote time and the jump buffer down by one step, the coyote time restarting while grounded and the
     * buffer on a press.
     *
     * @param pressed {@code true} if the jump was pressed at this step.
     * @param stepSeconds The step, in seconds.
     * @return {@code true} if a jump is requested at this step: a press, or a buffer still remaining.
     */
    private boolean countDown(boolean pressed, double stepSeconds)
    {
        if (grounded)
        {
            coyoteRemaining = coyoteTime;
        }
        else
        {
            coyoteRemaining = Math.max(0.0, coyoteRemaining - stepSeconds);
        }
        final boolean requested;
        if (pressed)
        {
            bufferRemaining = jumpBuffer;
            requested = true;
        }
        else
        {
            bufferRemaining = Math.max(0.0, bufferRemaining - stepSeconds);
            requested = bufferRemaining > 0.0;
        }
        return requested;
    }

    /**
     * Cuts the ascending jump on a release, when the variable height is on, and ends the jump that can be cut on a
     * release or once the object no longer ascends.
     *
     * @param velocityY The vertical velocity, in meters per second.
     * @param released {@code true} if the jump was released at this step.
     * @return The vertical velocity, cut or not, in meters per second.
     */
    private double cut(double velocityY, boolean released)
    {
        double result = velocityY;
        if (cutPending && (released || velocityY <= 0.0))
        {
            if (released && variableHeight && velocityY > 0.0)
            {
                result = velocityY * cutFactor;
            }
            cutPending = false;
        }
        return result;
    }

    /**
     * Restores the reset state.
     */
    private void clear()
    {
        impulse = 0.0;
        coyoteTime = DEFAULT_COYOTE_TIME;
        jumpBuffer = DEFAULT_JUMP_BUFFER;
        cutFactor = DEFAULT_CUT_FACTOR;
        variableHeight = false;
        grounded = false;
        jumpHeld = false;
        heldBefore = false;
        coyoteRemaining = 0.0;
        bufferRemaining = 0.0;
        cutPending = false;
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
