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
 * Throws an object that falls with the standard {@code Gravity} trait and burns out with the standard {@code Lifetime}
 * trait (REQ-STD-003, REQ-STD-006, REQ-STD-007, REQ-STD-008, ADR-0017, ADR-0034, ADR-0081).
 *
 * <p>
 * {@link Fall} builds the world. Each step is one {@code World.update} with the constant step: the previous position
 * is captured, the gravity lowers the vertical velocity by 0.1 m/s, never below the terminal velocity of -3 m/s, the
 * movement adds {@code velocity * step} to the position, and the lifetime counts down by the step. The ember rises for
 * 10 steps, falls, reaches its terminal velocity after 40 steps, and its lifetime of 1.5 s expires at step 90: the
 * flush of that step removes it with its traits. Headless; {@link #run(Log)} logs the steps.
 * </p>
 *
 * @since 11.0.0
 */
public final class FallExample
{
    /** Steps played by {@link #run(Log)}: two seconds at 60 updates per second. */
    public static final int STEPS = 120;

    /**
     * Private constructor.
     */
    private FallExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Runs the example: plays {@link #STEPS} steps and logs the ember after each one while it burns.
     *
     * @param log The log, at level {@link LogLevel#INFO} or finer to see the steps.
     * @return The fall after the last step.
     */
    public static Fall run(Log log)
    {
        final Fall fall = new Fall();
        for (int step = 0; step < STEPS; step++)
        {
            fall.step();
            log.log(LogLevel.INFO, "Step:", step);
            if (fall.isBurning())
            {
                log.log(LogLevel.INFO, "Ember y:", fall.getEmber().getY());
                log.log(LogLevel.INFO, "Ember vertical speed:", fall.getEmberMovable().getVelocityY());
                log.log(LogLevel.INFO, "Ember lifetime:", fall.getEmberLifetime().getDuration());
            }
            else
            {
                log.log(LogLevel.INFO, "Ember burned out, objects:", fall.getObjectCount());
            }
        }
        return fall;
    }
}
