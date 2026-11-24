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
 * Clock moved by the test, so that loop behavior is verified without real waiting (ADR-0045, REQ-TEST-001).
 *
 * <p>
 * Published in the {@code runtime} test JAR and reused by {@code test-support}. Not thread-safe: the test thread
 * moves it and the loop reads it on the same thread.
 * </p>
 */
public final class ControllableClock implements Clock
{
    /** Nanoseconds per second. */
    private static final double NANOS_PER_SECOND = 1e9;

    /** Current time, in nanoseconds. */
    private long nanos;

    /**
     * Creates a clock at time {@code 0}.
     */
    public ControllableClock()
    {
        super();
    }

    @Override
    public long nanoTime()
    {
        return nanos;
    }

    /**
     * Moves the clock forward.
     *
     * @param durationNanos The duration, in nanoseconds, not negative.
     * @throws IllegalArgumentException If the duration is negative.
     */
    public void advance(long durationNanos)
    {
        if (durationNanos < 0L)
        {
            throw new IllegalArgumentException("A monotonic clock never goes back: " + durationNanos);
        }
        nanos += durationNanos;
    }

    /**
     * Moves the clock forward.
     *
     * @param durationSeconds The duration, in seconds, not negative; rounded to the nanosecond.
     * @throws IllegalArgumentException If the duration is negative.
     */
    public void advanceSeconds(double durationSeconds)
    {
        advance(Math.round(durationSeconds * NANOS_PER_SECOND));
    }
}
