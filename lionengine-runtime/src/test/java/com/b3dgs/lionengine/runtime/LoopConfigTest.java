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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

/**
 * Test {@link LoopConfig} and {@link ControllableClock} (REQ-LOOP-008, REQ-LOOP-011, REQ-LOOP-012, REQ-RT-009,
 * REQ-TEST-001).
 */
final class LoopConfigTest
{
    /**
     * Invalid configurations.
     *
     * @return The test cases: rate, lag cap, scale, tolerance, capacity.
     */
    static Stream<Arguments> invalid()
    {
        return Stream.of(Arguments.of(0, 0.25, 1.0, 0.0, 1),
                         Arguments.of(1001, 0.25, 1.0, 0.0, 1),
                         Arguments.of(60, 0.016, 1.0, 0.0, 1),
                         Arguments.of(60, 1.001, 1.0, 0.0, 1),
                         Arguments.of(60, Double.NaN, 1.0, 0.0, 1),
                         Arguments.of(1, 0.25, 1.0, 0.0, 1),
                         Arguments.of(60, 0.25, 0.099, 0.0, 1),
                         Arguments.of(60, 0.25, 4.01, 0.0, 1),
                         Arguments.of(60, 0.25, Double.NaN, 0.0, 1),
                         Arguments.of(60, 0.25, 1.0, -0.001, 1),
                         Arguments.of(60, 0.25, 1.0, Double.NaN, 1),
                         Arguments.of(60, 0.25, 1.0, Double.POSITIVE_INFINITY, 1),
                         Arguments.of(60, 0.25, 1.0, 0.0, 0));
    }

    /**
     * The defaults of ADR-0031.
     */
    @Test
    void testDefaults()
    {
        final LoopConfig config = LoopConfig.DEFAULT;

        assertEquals(60, config.updateRate());
        assertEquals(0.25, config.lagCapSeconds());
        assertEquals(1.0, config.timeScale());
        assertEquals(0.000_25, config.snapToleranceSeconds());
        assertEquals(1024, config.inboxCapacity());
        assertEquals(16_666_667L, config.stepNanos());
        assertEquals(0.016_666_667, config.stepSeconds());
    }

    /**
     * The step is {@code round(1e9 / rate)} nanoseconds.
     */
    @Test
    void testStep()
    {
        assertEquals(1_000_000_000L, new LoopConfig(1, 1.0, 1.0, 0.0, 1).stepNanos());
        assertEquals(1_000_000L, new LoopConfig(1000, 0.25, 1.0, 0.0, 1).stepNanos());
        assertEquals(33_333_333L, new LoopConfig(30, 0.25, 1.0, 0.0, 1).stepNanos());
        assertEquals(0.01, new LoopConfig(100, 0.01, 1.0, 0.0, 1).stepSeconds());
    }

    /**
     * Out-of-range values are rejected.
     *
     * @param rate The update rate.
     * @param lagCap The lag cap in seconds.
     * @param scale The time scale.
     * @param tolerance The snapping tolerance in seconds.
     * @param capacity The inbox capacity.
     */
    @ParameterizedTest
    @MethodSource("invalid")
    void testInvalid(int rate, double lagCap, double scale, double tolerance, int capacity)
    {
        assertThrows(IllegalArgumentException.class, () -> new LoopConfig(rate, lagCap, scale, tolerance, capacity));
    }

    /**
     * The controllable clock only moves forward, when told.
     */
    @Test
    void testControllableClock()
    {
        final ControllableClock clock = new ControllableClock();
        assertEquals(0L, clock.nanoTime());

        clock.advance(5L);
        clock.advanceSeconds(0.5);
        assertEquals(500_000_005L, clock.nanoTime());

        assertThrows(IllegalArgumentException.class, () -> clock.advance(-1L));
    }
}
