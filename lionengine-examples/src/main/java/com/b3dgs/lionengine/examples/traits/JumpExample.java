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
 * A hero jumps with the standard {@code Jump} system: a full jump from a solid floor, a jump cut by an early release,
 * a jump within the coyote time after walking off a ledge, and a jump pressed just before landing, kept by the jump
 * buffer (REQ-STD-004, REQ-STD-007, REQ-STD-008, ADR-0030, ADR-0034, ADR-0084).
 *
 * <p>
 * {@link Jumping} builds the world. Each step is one {@code World.update} with the constant step: the previous position
 * is captured, the pad system writes the jump held flag from its timeline, the jump system applies the impulse
 * (5 m/s), the walk system sets the walking speed, the gravity accelerates the hero down, the movement moves it, then
 * the collision pushes it out of the tiles of {@link Ledge} and writes its grounded flag. The hero stands on the raised
 * floor; it jumps at step {@value Jumping#PRESS_JUMP} and rises about 1.25 m; holding the jump on landing does not jump
 * again. Pressed at step {@value Jumping#PRESS_CUT} and released at step {@value Jumping#RELEASE_CUT} while ascending,
 * its upward velocity is halved and the jump stays lower. From step {@value Jumping#WALK_START} it walks right off the
 * ledge, and a press at step {@value Jumping#PRESS_COYOTE}, a few steps after leaving the ground, still jumps. Falling
 * to the lower floor, it presses at step {@value Jumping#PRESS_BUFFER}, a few steps before landing: it jumps again at
 * the step after the landing. Headless; {@link #run(Log)} logs the steps.
 * </p>
 *
 * @since 11.0.0
 */
public final class JumpExample
{
    /** Steps played by {@link #run(Log)}: seven seconds at 60 updates per second. */
    public static final int STEPS = 420;

    /**
     * Private constructor.
     */
    private JumpExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Runs the example: plays {@link #STEPS} steps and logs the hero after each one.
     *
     * @param log The log, at level {@link LogLevel#INFO} or finer to see the steps.
     * @return The jumping world after the last step.
     */
    public static Jumping run(Log log)
    {
        final Jumping jumping = new Jumping();
        for (int step = 1; step <= STEPS; step++)
        {
            jumping.step();
            log.log(LogLevel.INFO, "Step:", step);
            log.log(LogLevel.INFO, "Hero x:", jumping.getHero().getX());
            log.log(LogLevel.INFO, "Hero y:", jumping.getHero().getY());
            log.log(LogLevel.INFO, "Hero vertical velocity:", jumping.getHeroMovable().getVelocityY());
            log.log(LogLevel.INFO, jumping.getHeroJumpable().isGrounded() ? "Hero grounded" : "Hero in the air");
        }
        return jumping;
    }
}
