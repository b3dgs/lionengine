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
package com.b3dgs.lionengine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Contract suite of {@link LoopDriver} and {@link LoopControl} (ADR-0040, ADR-0045, REQ-LOOP-001, REQ-LOOP-002,
 * REQ-LOOP-010).
 *
 * <p>
 * Published in the {@code core} test JAR: every loop implementation binds it by extending this class and creating its
 * driver, so that all implementations honor the same contract. The created driver must run its iterations without
 * failing.
 * </p>
 */
public abstract class LoopDriverContract
{
    /**
     * Creates a driver in its initial state.
     *
     * @return The driver, never iterated.
     */
    protected abstract LoopDriver createDriver();

    /**
     * A driver runs from its first iteration.
     */
    @Test
    void testRunningAfterFirstIteration()
    {
        final LoopDriver driver = createDriver();
        assertFalse(driver.isRunning());

        driver.iterate();
        assertTrue(driver.isRunning());
    }

    /**
     * A stop request is honored at the end of the iteration; iterating a stopped loop fails.
     */
    @Test
    void testStop()
    {
        final LoopDriver driver = createDriver();
        driver.iterate();

        driver.stop();
        assertTrue(driver.isRunning());
        driver.iterate();
        assertFalse(driver.isRunning());
        assertThrows(IllegalStateException.class, driver::iterate);
    }

    /**
     * Stop is idempotent.
     */
    @Test
    void testStopTwice()
    {
        final LoopDriver driver = createDriver();
        driver.stop();
        driver.stop();
        driver.iterate();
        driver.stop();

        assertFalse(driver.isRunning());
    }

    /**
     * A paused loop is running; pause and resume are accepted at any time.
     */
    @Test
    void testPauseKeepsRunning()
    {
        final LoopDriver driver = createDriver();
        driver.pause();
        driver.iterate();
        assertTrue(driver.isRunning());

        driver.resume();
        driver.iterate();
        assertTrue(driver.isRunning());
    }

    /**
     * Time scales inside [0.1, 4.0] are accepted.
     *
     * @param scale The time scale.
     */
    @ParameterizedTest
    @ValueSource(doubles = {0.1, 0.25, 1.0, 4.0})
    void testValidTimeScale(double scale)
    {
        final LoopDriver driver = createDriver();

        assertDoesNotThrow(() -> driver.setTimeScale(scale));
    }

    /**
     * Time scales outside [0.1, 4.0] are rejected.
     *
     * @param scale The time scale.
     */
    @ParameterizedTest
    @ValueSource(doubles = {0.0, 0.099, 4.001, -1.0, Double.NaN, Double.POSITIVE_INFINITY})
    void testInvalidTimeScale(double scale)
    {
        final LoopDriver driver = createDriver();

        assertThrows(IllegalArgumentException.class, () -> driver.setTimeScale(scale));
    }
}
