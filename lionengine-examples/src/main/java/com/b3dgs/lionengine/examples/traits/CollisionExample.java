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
import com.b3dgs.lionengine.traits.CollisionListener;

/**
 * Two balls meet and cross a trigger zone with the standard {@code Collidable} trait and {@code Collision} system
 * (REQ-STD-005, REQ-STD-007, REQ-STD-008, REQ-STD-010, ADR-0030, ADR-0034, ADR-0082).
 *
 * <p>
 * {@link Arena} builds the world. Each step is one {@code World.update} with the constant step: the previous position
 * is captured, the movement moves each ball by 2.5 cm toward the other, then the collision finds the overlapping boxes
 * and reports, after its detection, the pairs that stopped overlapping, then those that started, each with the lower
 * ID first. After 60 steps the balls only touch the zone, edges excluded, so both enter it at step 61; they meet at
 * step 101, pass through each other (detection only), separate at step 141 and leave the zone at step 181, the steps
 * given by the {@code double} arithmetic of the movement. The ghost in the zone is never reported. Headless;
 * {@link #run(Log)} logs the events.
 * </p>
 *
 * @since 11.0.0
 */
public final class CollisionExample
{
    /** Steps played by {@link #run(Log)}: four seconds at 60 updates per second. */
    public static final int STEPS = 240;

    /**
     * Private constructor.
     */
    private CollisionExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Runs the example: plays {@link #STEPS} steps and logs each collision event.
     *
     * @param log The log, at level {@link LogLevel#INFO} or finer to see the events.
     * @return The arena after the last step.
     */
    public static Arena run(Log log)
    {
        final Arena arena = new Arena(new CollisionListener()
        {
            @Override
            public void onEnter(long objectA, long objectB)
            {
                log.log(LogLevel.INFO, "Enter, lower ID:", objectA);
                log.log(LogLevel.INFO, "Enter, higher ID:", objectB);
            }

            @Override
            public void onExit(long objectA, long objectB)
            {
                log.log(LogLevel.INFO, "Exit, lower ID:", objectA);
                log.log(LogLevel.INFO, "Exit, higher ID:", objectB);
            }
        });
        for (int step = 1; step <= STEPS; step++)
        {
            arena.step();
            log.log(LogLevel.INFO, "Step:", step);
        }
        return arena;
    }
}
