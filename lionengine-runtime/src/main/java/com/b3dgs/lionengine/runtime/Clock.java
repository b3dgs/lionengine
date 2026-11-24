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
 * Monotonic time source of the game loop (ADR-0017, ADR-0031, REQ-LOOP-007, REQ-TEST-001).
 *
 * <p>
 * Production uses {@code System.nanoTime}; tests use the controllable clock of the {@code runtime} test JAR
 * (ADR-0045), so that loop behavior is verified without real waiting.
 * </p>
 *
 * <p>
 * Thread: called on the runtime thread only.
 * </p>
 *
 * @since 11.0.0
 */
@FunctionalInterface
public interface Clock
{
    /**
     * Returns the current time of the clock.
     *
     * @return The time in nanoseconds; only differences between two values are meaningful, and a later call never
     *         returns a smaller value.
     */
    long nanoTime();
}
