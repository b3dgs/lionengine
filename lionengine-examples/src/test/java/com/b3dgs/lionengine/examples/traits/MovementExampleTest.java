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

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.traits.Transformable;

/**
 * Tests of {@link MovementExample} and {@link Flight}.
 */
final class MovementExampleTest
{
    /** Tolerance on accumulated positions, in meters. */
    private static final double EPSILON = 1e-9;
    /** Steps until the rocket reaches its maximum speed: 5 m/s at 0.1 m/s per step. */
    private static final int ACCELERATION_STEPS = 50;

    /**
     * Step by step: the drone moves by its velocity, the rocket speeds up by 0.1 m/s per step until its maximum speed
     * clamps it; the previous position is the position of the step start; the interpolated position lies between.
     */
    @Test
    void testStepByStep()
    {
        final Flight flight = new Flight();
        double rocketX = 0.0;
        for (int step = 1; step <= MovementExample.STEPS; step++)
        {
            final double previous = flight.getRocket().getX();
            flight.step();
            final double speed = Math.min(step * Flight.THRUST * Flight.STEP_SECONDS, Flight.ROCKET_MAX_SPEED);
            rocketX += speed * Flight.STEP_SECONDS;

            final Transformable rocket = flight.getRocket();
            assertEquals(speed, flight.getRocketMovable().getVelocityX(), EPSILON, "Speed at step " + step);
            assertEquals(rocketX, rocket.getX(), EPSILON, "Rocket at step " + step);
            assertEquals(previous, rocket.getPreviousX(), "Previous at step " + step);
            assertEquals((previous + rocket.getX()) / 2.0,
                         Flight.interpolateX(rocket, MovementExample.ALPHA),
                         EPSILON);
            assertEquals(Flight.DRONE_VELOCITY_X * step * Flight.STEP_SECONDS, flight.getDrone().getX(), EPSILON);
            assertEquals(Flight.DRONE_VELOCITY_Y * step * Flight.STEP_SECONDS, flight.getDrone().getY(), EPSILON);
        }
        assertEquals(Flight.ROCKET_MAX_SPEED, flight.getRocketMovable().getVelocityX(), EPSILON);
        assertEquals(0.0, flight.getRocket().getY());
        assertEquals(Flight.SIZE, flight.getRocket().getWidth());
    }

    /**
     * The example runs: after one second the drone is at (3, 4) and the rocket covered 127.5 steps of 0.1 m/s while
     * accelerating, then 10 steps at 5 m/s.
     */
    @Test
    void testRun()
    {
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);
        final Flight flight = MovementExample.run(log);

        assertEquals(3.0, flight.getDrone().getX(), EPSILON);
        assertEquals(4.0, flight.getDrone().getY(), EPSILON);
        final double accelerating = 0.1 * (ACCELERATION_STEPS * (ACCELERATION_STEPS + 1) / 2) * Flight.STEP_SECONDS;
        final double cruising = (MovementExample.STEPS - ACCELERATION_STEPS) * Flight.ROCKET_MAX_SPEED
                                * Flight.STEP_SECONDS;
        assertEquals(accelerating + cruising, flight.getRocket().getX(), EPSILON);
    }
}
