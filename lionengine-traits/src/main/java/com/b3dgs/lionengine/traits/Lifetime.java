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
 * Standard trait: the remaining lifetime of an object (REQ-STD-006, ADR-0011, ADR-0014, ADR-0034, ADR-0081).
 *
 * <p>
 * Data only, no logic. One field, a {@code double}: the duration ({@link #getDuration()}), in seconds, the time the
 * object has left to live, counted down in place by {@link LifetimeSystem} by the constant step; positive or zero,
 * {@link Double#POSITIVE_INFINITY} for an object that never expires.
 * </p>
 *
 * <p>
 * A new or reset instance never expires ({@link Double#POSITIVE_INFINITY}): the game sets the duration it wants. A
 * duration of {@code 0}, like any duration at or under the step, expires at the next step.
 * </p>
 *
 * <p>
 * Interactions: {@link LifetimeSystem} (slot {@value LifetimeSystem#SLOT}) subtracts the step from the duration of each
 * object having a {@link Lifetime}, then requests the removal of the object when the duration is at or under
 * {@code 0}; the removal, with every trait of the object, is applied at the flush that ends the step, so systems after
 * slot {@value LifetimeSystem#SLOT} still see the object in that step, and it is requested once. After the expiry and
 * until the flush the duration reads {@code 0} or less. Registered in a world with {@link #TYPE}; instances are pooled
 * by the world and must not be kept after their removal. Runtime thread only.
 * </p>
 *
 * <p>
 * Memory: 24 bytes per instance on the desktop JVM (64-bit HotSpot, compressed class pointers), measured with the
 * per-thread allocation counter by {@code TraitMemoryTest} (ADR-0034); the pool also holds one reference per instance.
 * </p>
 *
 * @since 11.0.0
 */
public final class Lifetime
{
    /**
     * The trait type to register in a world: creates instances that never expire, and resets them to that state on
     * removal. Shared by every world, which each register it once.
     */
    public static final TraitType<Lifetime> TYPE = new TraitType<>()
    {
        @Override
        public Lifetime create()
        {
            return new Lifetime();
        }

        @Override
        public void reset(Lifetime trait)
        {
            trait.duration = Double.POSITIVE_INFINITY;
        }
    };

    /** Remaining duration, in seconds. */
    private double duration = Double.POSITIVE_INFINITY;

    /**
     * Creates a lifetime in its reset state: it never expires.
     */
    public Lifetime()
    {
        super();
    }

    /**
     * Returns the remaining duration.
     *
     * @return The time the object has left to live, in seconds, {@link Double#POSITIVE_INFINITY} if it never expires;
     *         {@code 0} or less once expired, until the flush removes the object.
     */
    public double getDuration()
    {
        return duration;
    }

    /**
     * Sets the remaining duration, counted down from the next step.
     *
     * @param duration The time the object has left to live, in seconds, positive or zero,
     *            {@link Double#POSITIVE_INFINITY} for never.
     * @throws IllegalArgumentException If the duration is negative or not a number.
     */
    public void setDuration(double duration)
    {
        if (duration < 0.0 || Double.isNaN(duration))
        {
            throw new IllegalArgumentException("Duration is negative or not a number: " + duration);
        }
        this.duration = duration;
    }

    /**
     * Counts the duration down by one step.
     *
     * @param stepSeconds The step, in seconds.
     * @return {@code true} if the object expired: the remaining duration is at or under {@code 0}.
     */
    boolean countDown(double stepSeconds)
    {
        duration -= stepSeconds;
        return duration <= 0.0;
    }
}
