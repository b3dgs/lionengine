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
 * Configuration of the game loop (ADR-0017, ADR-0031, REQ-LOOP-007, REQ-LOOP-008, REQ-LOOP-011, REQ-LOOP-012,
 * REQ-RT-009).
 *
 * <p>
 * The fixed step lasts {@link #stepNanos()} nanoseconds: {@code round(1e9 / updateRate)}, so the step in seconds
 * given to updates, {@link #stepSeconds()}, always agrees with the simulated time. Every value is checked at
 * construction; {@link #DEFAULT} holds the documented defaults.
 * </p>
 *
 * <p>
 * Immutable and thread-safe.
 * </p>
 *
 * @param updateRate The number of fixed steps per second of simulated time, in Hz, in
 *            [{@link #MIN_UPDATE_RATE}, {@link #MAX_UPDATE_RATE}].
 * @param lagCapSeconds The real time added to the simulation by one iteration at most, in seconds, from one step to
 *            {@link #MAX_LAG_CAP_SECONDS}; the excess is dropped and reported.
 * @param timeScale The initial time scale, in [{@link #MIN_TIME_SCALE}, {@link #MAX_TIME_SCALE}]; {@code 1.0} is real
 *            time.
 * @param snapToleranceSeconds The vsync snapping tolerance, in seconds, not negative and below half a step: an
 *            elapsed time this close to 1 to 4 steps is snapped to that multiple; {@code 0} disables snapping. From
 *            half a step, every elapsed time would be snapped and the simulation would drift from real time.
 * @param inboxCapacity The number of events the inbox holds, positive.
 * @since 11.0.0
 */
public record LoopConfig(int updateRate,
                         double lagCapSeconds,
                         double timeScale,
                         double snapToleranceSeconds,
                         int inboxCapacity)
{
    /** Default update rate: 60 Hz. */
    public static final int DEFAULT_UPDATE_RATE = 60;
    /** Lowest update rate: 1 Hz. */
    public static final int MIN_UPDATE_RATE = 1;
    /** Highest update rate: 1,000 Hz. */
    public static final int MAX_UPDATE_RATE = 1000;
    /** Default lag cap: 0.25 s. */
    public static final double DEFAULT_LAG_CAP_SECONDS = 0.25;
    /** Highest lag cap: 1 s. */
    public static final double MAX_LAG_CAP_SECONDS = 1.0;
    /** Default time scale: 1.0, real time. */
    public static final double DEFAULT_TIME_SCALE = 1.0;
    /** Lowest time scale: 0.1. */
    public static final double MIN_TIME_SCALE = 0.1;
    /** Highest time scale: 4.0. */
    public static final double MAX_TIME_SCALE = 4.0;
    /** Default vsync snapping tolerance: 0.25 ms. */
    public static final double DEFAULT_SNAP_TOLERANCE_SECONDS = 0.000_25;
    /** Default inbox capacity: 1,024 events. */
    public static final int DEFAULT_INBOX_CAPACITY = 1024;
    /** Configuration with every default value. */
    public static final LoopConfig DEFAULT = new LoopConfig(DEFAULT_UPDATE_RATE,
                                                            DEFAULT_LAG_CAP_SECONDS,
                                                            DEFAULT_TIME_SCALE,
                                                            DEFAULT_SNAP_TOLERANCE_SECONDS,
                                                            DEFAULT_INBOX_CAPACITY);

    /** Nanoseconds per second. */
    static final double NANOS_PER_SECOND = 1e9;

    /**
     * Checks a time scale.
     *
     * @param scale The time scale.
     * @throws IllegalArgumentException If the scale is outside [{@link #MIN_TIME_SCALE}, {@link #MAX_TIME_SCALE}] or
     *             not a number.
     */
    static void checkTimeScale(double scale)
    {
        if (!(scale >= MIN_TIME_SCALE && scale <= MAX_TIME_SCALE))
        {
            throw new IllegalArgumentException("Time scale outside [0.1, 4.0]: " + scale);
        }
    }

    /**
     * Converts seconds to nanoseconds, rounded.
     *
     * @param seconds The duration in seconds.
     * @return The duration in nanoseconds.
     */
    static long toNanos(double seconds)
    {
        return Math.round(seconds * NANOS_PER_SECOND);
    }

    /**
     * Creates a configuration.
     *
     * @param updateRate The number of fixed steps per second, in Hz, in [{@link #MIN_UPDATE_RATE},
     *            {@link #MAX_UPDATE_RATE}].
     * @param lagCapSeconds The lag cap in seconds, from one step to {@link #MAX_LAG_CAP_SECONDS}.
     * @param timeScale The initial time scale, in [{@link #MIN_TIME_SCALE}, {@link #MAX_TIME_SCALE}].
     * @param snapToleranceSeconds The snapping tolerance in seconds, not negative and below half a step; {@code 0}
     *            disables it.
     * @param inboxCapacity The inbox capacity, positive.
     * @throws IllegalArgumentException If a value is outside its range or not a number.
     */
    public LoopConfig
    {
        if (updateRate < MIN_UPDATE_RATE || updateRate > MAX_UPDATE_RATE)
        {
            throw new IllegalArgumentException("Update rate outside [1, 1000] Hz: " + updateRate);
        }
        final long stepNanos = Math.round(NANOS_PER_SECOND / updateRate);
        final long lagCapNanos = toNanos(lagCapSeconds);
        if (lagCapSeconds > MAX_LAG_CAP_SECONDS || lagCapNanos < stepNanos)
        {
            throw new IllegalArgumentException("Lag cap outside [one step, 1 s]: " + lagCapSeconds);
        }
        checkTimeScale(timeScale);
        final long toleranceNanos = toNanos(snapToleranceSeconds);
        if (Double.isNaN(snapToleranceSeconds) || snapToleranceSeconds < 0.0 || toleranceNanos >= (stepNanos + 1L) / 2L)
        {
            throw new IllegalArgumentException("Snapping tolerance outside [0, half a step): " + snapToleranceSeconds);
        }
        if (inboxCapacity < 1)
        {
            throw new IllegalArgumentException("Inbox capacity not positive: " + inboxCapacity);
        }
    }

    /**
     * Returns the duration of one fixed step.
     *
     * @return {@code round(1e9 / updateRate)}, in nanoseconds.
     */
    public long stepNanos()
    {
        return Math.round(NANOS_PER_SECOND / updateRate);
    }

    /**
     * Returns the duration of one fixed step, as given to the updates.
     *
     * @return {@link #stepNanos()} divided by {@code 1e9}, in seconds.
     */
    public double stepSeconds()
    {
        return stepNanos() / NANOS_PER_SECOND;
    }
}
