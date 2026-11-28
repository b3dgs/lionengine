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
 * An object falls onto a solid floor and stops, grounded, then walks into a wall, with the tile push-out of the
 * standard {@code Collision} system (REQ-STD-007, REQ-STD-008, REQ-STD-010, REQ-MAP-008, ADR-0030, ADR-0083).
 *
 * <p>
 * {@link Landing} builds the world. Each step is one {@code World.update} with the constant step: the previous position
 * is captured, the walk system sets the walking speed once the hero is grounded, the gravity accelerates it down, the
 * movement moves it, then the collision pushes it out of the solid tiles of {@link Ground}. The hero falls from 3 m
 * for 36 steps and lands on the floor at its 36th step, its box exactly on the floor top, its vertical velocity zeroed,
 * grounded; it stays grounded at each later step, the gravity moving it down into the floor and the push-out back on
 * it. It walks from the next step on at 2 m/s, across the seams of the floor tiles, and at step 164 meets the wall,
 * which stops it with its box touching the wall and its horizontal velocity zeroed; it stays there. Headless;
 * {@link #run(Log)} logs the steps.
 * </p>
 *
 * @since 11.0.0
 */
public final class LandingExample
{
    /** Steps played by {@link #run(Log)}: four seconds at 60 updates per second. */
    public static final int STEPS = 240;

    /**
     * Private constructor.
     */
    private LandingExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Runs the example: plays {@link #STEPS} steps and logs the hero after each one.
     *
     * @param log The log, at level {@link LogLevel#INFO} or finer to see the steps.
     * @return The landing after the last step.
     */
    public static Landing run(Log log)
    {
        final Landing landing = new Landing();
        for (int step = 1; step <= STEPS; step++)
        {
            landing.step();
            log.log(LogLevel.INFO, "Step:", step);
            log.log(LogLevel.INFO, "Hero x:", landing.getHero().getX());
            log.log(LogLevel.INFO, "Hero y:", landing.getHero().getY());
            log.log(LogLevel.INFO, landing.isGrounded() ? "Hero grounded" : "Hero in the air");
        }
        return landing;
    }
}
