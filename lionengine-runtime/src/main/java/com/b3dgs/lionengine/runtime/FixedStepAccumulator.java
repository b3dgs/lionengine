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
package com.b3dgs.lionengine.runtime;

/**
 * Fixed-step time accumulator of the game loop (ADR-0017, ADR-0031, REQ-LOOP-007, REQ-LOOP-008, REQ-LOOP-011,
 * REQ-LOOP-012).
 *
 * <p>
 * Adding an elapsed time snaps it to 1 to 4 steps when within the tolerance, caps it at the lag cap (the excess is
 * returned as dropped), multiplies it by the time scale and rounds it to {@code long} nanoseconds, carrying the
 * rounding remainder to the next addition. Steps are then taken while the accumulator holds at least one.
 * </p>
 *
 * <p>
 * Runtime thread only. Allocates nothing.
 * </p>
 */
final class FixedStepAccumulator
{
    /** Largest number of steps an elapsed time is snapped to. */
    private static final long MAX_SNAPPED_STEPS = 4L;

    /** Duration of one step, in nanoseconds. */
    private final long stepNanos;
    /** Largest real time added by one addition, in nanoseconds. */
    private final long lagCapNanos;
    /** Snapping tolerance, in nanoseconds; {@code 0} disables snapping. */
    private final long snapToleranceNanos;
    /** Time scale applied to the added time. */
    private double timeScale;
    /** Accumulated simulation time not yet consumed by steps, in nanoseconds. */
    private long accumulator;
    /** Rounding remainder of the scaled time, carried to the next addition, in nanoseconds. */
    private double remainder;

    /**
     * Creates an empty accumulator.
     *
     * @param config The loop configuration.
     */
    FixedStepAccumulator(LoopConfig config)
    {
        stepNanos = config.stepNanos();
        lagCapNanos = LoopConfig.toNanos(config.lagCapSeconds());
        snapToleranceNanos = LoopConfig.toNanos(config.snapToleranceSeconds());
        timeScale = config.timeScale();
    }

    /**
     * Sets the time scale applied to the next additions.
     *
     * @param scale The time scale, already checked.
     */
    void setTimeScale(double scale)
    {
        timeScale = scale;
    }

    /**
     * Returns the time scale.
     *
     * @return The time scale.
     */
    double getTimeScale()
    {
        return timeScale;
    }

    /**
     * Snaps an elapsed time to 1 to 4 steps when it is within the tolerance of that multiple.
     *
     * @param elapsedNanos The elapsed real time, in nanoseconds, not negative.
     * @return The snapped time, or the elapsed time unchanged.
     */
    long snap(long elapsedNanos)
    {
        final long steps = (elapsedNanos + stepNanos / 2) / stepNanos;
        long snapped = elapsedNanos;
        final long distance = Math.abs(elapsedNanos - steps * stepNanos);
        if (steps >= 1L && steps <= MAX_SNAPPED_STEPS && distance <= snapToleranceNanos)
        {
            snapped = steps * stepNanos;
        }
        return snapped;
    }

    /**
     * Returns the part of a real elapsed time kept by the lag cap.
     *
     * @param elapsedNanos The elapsed real time, in nanoseconds, not negative.
     * @return The elapsed time, at most the lag cap, in nanoseconds.
     */
    long cap(long elapsedNanos)
    {
        return Math.min(elapsedNanos, lagCapNanos);
    }

    /**
     * Adds an elapsed real time: snaps it, caps it, scales it and accumulates it.
     *
     * @param elapsedNanos The elapsed real time, in nanoseconds, not negative.
     * @return The real time dropped by the lag cap, in nanoseconds, {@code 0} if none.
     */
    long add(long elapsedNanos)
    {
        final long snapped = snap(elapsedNanos);
        final long kept = cap(snapped);
        final double scaled = kept * timeScale + remainder;
        final long scaledNanos = Math.round(scaled);
        remainder = scaled - scaledNanos;
        accumulator += scaledNanos;
        return snapped - kept;
    }

    /**
     * Takes one step if the accumulator holds at least one.
     *
     * @return {@code true} if a step was taken, {@code false} if less than one step is accumulated.
     */
    boolean takeStep()
    {
        final boolean step = accumulator >= stepNanos;
        if (step)
        {
            accumulator -= stepNanos;
        }
        return step;
    }

    /**
     * Returns the presentation factor.
     *
     * @return The accumulated time divided by the step, in {@code [0, 1)} once the steps are taken.
     */
    double getAlpha()
    {
        return accumulator / (double) stepNanos;
    }

    /**
     * Clears the accumulated time and the remainder, on resume.
     */
    void clear()
    {
        accumulator = 0L;
        remainder = 0.0;
    }
}
