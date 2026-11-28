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

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;

/**
 * Moves objects with the standard {@code Transformable} and {@code Movable} traits and the {@code Movement} system
 * (REQ-STD-001, REQ-STD-002, REQ-STD-007, REQ-STD-008, ADR-0017, ADR-0030, ADR-0034, ADR-0080).
 *
 * <p>
 * {@link Flight} builds the world. Each step is one {@code World.update} with the constant step: the previous
 * positions are captured first, the thrust raises the rocket velocity, then the movement clamps every speed to its
 * maximum (direction kept) and adds {@code velocity * step} to every position. After one second the drone has flown
 * 3 m right and 4 m up; the rocket reached its maximum speed of 5 m/s after 50 steps and stays there. Each step also
 * logs the position rendering would draw halfway to the next step ({@code alpha = 0.5}). Headless; {@link #run(Log)}
 * logs the steps.
 * </p>
 *
 * @since 11.0.0
 */
public final class MovementExample
{
    /** Steps played by {@link #run(Log)}: one second at 60 updates per second. */
    public static final int STEPS = 60;
    /** The interpolation factor of the logged positions. */
    public static final double ALPHA = 0.5;

    /**
     * Private constructor.
     */
    private MovementExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Runs the example: plays {@link #STEPS} steps and logs the positions after each one.
     *
     * @param log The log, at level {@link LogLevel#INFO} or finer to see the steps.
     * @return The flight after the last step.
     */
    public static Flight run(Log log)
    {
        final Flight flight = new Flight();
        for (int step = 0; step < STEPS; step++)
        {
            flight.step();
            log.log(LogLevel.INFO, "Step:", step);
            log.log(LogLevel.INFO, "Drone x:", flight.getDrone().getX());
            log.log(LogLevel.INFO, "Drone y:", flight.getDrone().getY());
            log.log(LogLevel.INFO, "Rocket x:", flight.getRocket().getX());
            log.log(LogLevel.INFO, "Rocket drawn x:", Flight.interpolateX(flight.getRocket(), ALPHA));
            log.log(LogLevel.INFO, "Rocket speed:", flight.getRocketMovable().getVelocityX());
        }
        return flight;
    }
}
