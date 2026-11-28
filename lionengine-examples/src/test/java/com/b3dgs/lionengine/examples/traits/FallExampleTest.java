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
package com.b3dgs.lionengine.examples.traits;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;

/**
 * Tests of {@link FallExample} and {@link Fall}.
 */
final class FallExampleTest
{
    /** Tolerance on accumulated values. */
    private static final double EPSILON = 1e-9;

    /**
     * Step by step: the vertical velocity loses 0.1 m/s per step down to the terminal velocity, the position follows
     * it, the lifetime counts down; the ember exists until the step its lifetime expires, then the world is empty.
     */
    @Test
    void testStepByStep()
    {
        final Fall fall = new Fall();
        final int expiry = expiryStep();
        double y = Fall.HEIGHT;
        double remaining = Fall.LIFETIME;
        for (int step = 1; step < expiry; step++)
        {
            fall.step();
            final double speed = Math.max(Fall.THROW_VELOCITY + step * Fall.ACCELERATION * Fall.STEP_SECONDS,
                                          -Fall.TERMINAL_VELOCITY);
            y += speed * Fall.STEP_SECONDS;
            remaining -= Fall.STEP_SECONDS;

            assertTrue(fall.isBurning(), "Step " + step);
            assertEquals(speed, fall.getEmberMovable().getVelocityY(), EPSILON, "Speed at step " + step);
            assertEquals(y, fall.getEmber().getY(), EPSILON, "Height at step " + step);
            assertEquals(Fall.DRIFT_VELOCITY * step * Fall.STEP_SECONDS, fall.getEmber().getX(), EPSILON);
            assertEquals(remaining, fall.getEmberLifetime().getDuration());
        }
        assertEquals(-Fall.TERMINAL_VELOCITY, fall.getEmberMovable().getVelocityY());

        fall.step();

        assertFalse(fall.isBurning());
        assertEquals(0, fall.getObjectCount());
    }

    /**
     * The example runs: the ember burns out at step 90 (one more if the rounding of the count-down leaves a
     * remainder) and the world stays empty.
     */
    @Test
    void testRun()
    {
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);
        final Fall fall = FallExample.run(log);
        final int expiry = expiryStep();

        assertTrue(expiry == 90 || expiry == 91, () -> "Expiry: " + expiry);
        assertFalse(fall.isBurning());
        assertEquals(0, fall.getObjectCount());
    }

    /**
     * Returns the step at which the lifetime expires, by the subtraction of the lifetime system.
     *
     * @return The number of the expiring step, from 1.
     */
    private static int expiryStep()
    {
        double remaining = Fall.LIFETIME;
        int steps = 0;
        do
        {
            remaining -= Fall.STEP_SECONDS;
            steps++;
        }
        while (remaining > 0.0);
        return steps;
    }
}
