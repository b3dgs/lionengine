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
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.physics.Box;

/**
 * Tests of {@link JumpExample}, {@link Jumping} and {@link Ledge}.
 */
final class JumpExampleTest
{
    /** Hero half size, in meters. */
    private static final double HALF = Jumping.SIZE / 2.0;
    /** Vertical velocity at the end of a jump step: the impulse, then the gravity of the step. */
    private static final double JUMP_VELOCITY = Jumping.IMPULSE + Jumping.ACCELERATION * Jumping.STEP_SECONDS;
    /** The step the first jump lands at. */
    private static final int FULL_LANDING = 89;
    /** The step the cut jump lands at. */
    private static final int CUT_LANDING = 158;
    /** The step whose collision first leaves the hero in the air past the ledge. */
    private static final int LEDGE_STEP = 257;
    /** The step the coyote jump lands on the lower floor at. */
    private static final int LOWER_LANDING = 329;

    /** The world under test. */
    private final Jumping jumping = new Jumping();

    /**
     * Plays steps up to a step.
     *
     * @param last The last step to play.
     * @return The highest hero center Y over the played steps, in meters.
     */
    private double playTo(int last)
    {
        double top = jumping.getHero().getY();
        while (jumping.getSteps() < last)
        {
            jumping.step();
            top = Math.max(top, jumping.getHero().getY());
        }
        return top;
    }

    /**
     * Step by step: the full jump, the held landing that does not jump again, the cut jump lower than the full one,
     * the coyote jump after the ledge and the buffered jump on landing.
     */
    @Test
    void testStepByStep()
    {
        for (int step = 1; step < Jumping.PRESS_JUMP; step++)
        {
            jumping.step();
            assertTrue(jumping.getHeroJumpable().isGrounded(), "Step " + step);
            assertEquals(Jumping.START_Y, jumping.getHero().getY());
        }
        jumping.step();
        assertEquals(JUMP_VELOCITY, jumping.getHeroMovable().getVelocityY());
        assertFalse(jumping.getHeroJumpable().isGrounded());
        final double fullTop = playTo(FULL_LANDING - 1);
        assertFalse(jumping.getHeroJumpable().isGrounded());
        jumping.step();
        assertTrue(jumping.getHeroJumpable().isGrounded());
        assertEquals(Jumping.START_Y, jumping.getHero().getY());
        for (int step = FULL_LANDING + 1; step < Jumping.PRESS_CUT; step++)
        {
            jumping.step();
            assertTrue(jumping.getHeroJumpable().isGrounded(), "Held or released on the ground: step " + step);
            assertEquals(0.0, jumping.getHeroMovable().getVelocityY());
        }

        playTo(Jumping.RELEASE_CUT - 1);
        final double ascending = jumping.getHeroMovable().getVelocityY();
        jumping.step();
        assertEquals(ascending * 0.5 + Jumping.ACCELERATION * Jumping.STEP_SECONDS,
                     jumping.getHeroMovable().getVelocityY());
        final double cutTop = playTo(CUT_LANDING);
        assertTrue(jumping.getHeroJumpable().isGrounded());
        assertTrue(fullTop - Jumping.START_Y > 1.2, () -> "Full jump: " + fullTop);
        assertTrue(cutTop - Jumping.START_Y < 0.7, () -> "Cut jump: " + cutTop);

        playTo(LEDGE_STEP);
        assertFalse(jumping.getHeroJumpable().isGrounded());
        assertTrue(jumping.getHero().getX() - HALF > Ledge.LEDGE_X);
        playTo(Jumping.PRESS_COYOTE - 1);
        assertTrue(jumping.getHero().getY() < Jumping.START_Y);
        assertTrue(jumping.getHeroJumpable().getCoyoteRemaining() > 0.0);
        jumping.step();
        assertEquals(JUMP_VELOCITY, jumping.getHeroMovable().getVelocityY(), "Coyote jump");

        playTo(Jumping.PRESS_BUFFER);
        assertFalse(jumping.getHeroJumpable().isGrounded());
        assertEquals(Jumping.STEP_SECONDS * 6.0, jumping.getHeroJumpable().getBufferRemaining(), 1e-12);
        playTo(LOWER_LANDING);
        assertTrue(jumping.getHeroJumpable().isGrounded());
        assertEquals(Ledge.LOWER_TOP + HALF, jumping.getHero().getY());
        jumping.step();
        assertEquals(JUMP_VELOCITY, jumping.getHeroMovable().getVelocityY(), "Buffered jump");
        assertFalse(jumping.getHeroJumpable().isGrounded());
    }

    /**
     * The example runs: the hero ends on the lower floor, grounded, still walking, the jump held.
     */
    @Test
    void testRun()
    {
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);
        final Jumping played = JumpExample.run(log);

        assertEquals(JumpExample.STEPS, played.getSteps());
        assertEquals(Ledge.LOWER_TOP + HALF, played.getHero().getY());
        assertEquals(Jumping.WALK_SPEED, played.getHeroMovable().getVelocityX());
        assertEquals(0.0, played.getHeroMovable().getVelocityY());
        assertTrue(played.getHeroJumpable().isGrounded());
        assertTrue(played.getHeroJumpable().isJumpHeld());
    }

    /**
     * The timeline holds the jump between each press and its release, and from the buffered press on.
     */
    @Test
    void testTimeline()
    {
        assertFalse(Jumping.isHeld(Jumping.PRESS_JUMP - 1));
        assertTrue(Jumping.isHeld(Jumping.PRESS_JUMP));
        assertFalse(Jumping.isHeld(Jumping.RELEASE_JUMP));
        assertTrue(Jumping.isHeld(Jumping.PRESS_CUT));
        assertFalse(Jumping.isHeld(Jumping.RELEASE_CUT));
        assertTrue(Jumping.isHeld(Jumping.PRESS_COYOTE));
        assertFalse(Jumping.isHeld(Jumping.RELEASE_COYOTE));
        assertTrue(Jumping.isHeld(Jumping.PRESS_BUFFER));
        assertTrue(Jumping.isHeld(JumpExample.STEPS));
    }

    /**
     * The ledge finds its overlapping tiles only, counts beyond the buffer, and refuses {@code null}.
     */
    @Test
    void testLedge()
    {
        final Ledge ledge = new Ledge();
        final Box[] results =
        {
            new Box(), new Box()
        };

        assertEquals(2, ledge.query(new Box(2.0, 1.5, 0.5, 0.5), results));
        assertEquals(0, ledge.query(new Box(5.0, 1.25, 0.5, 0.5), results));
        assertEquals(Ledge.UPPER_TILES, ledge.query(new Box(2.0, 1.5, 4.0, 0.5), results));
        final Box area = new Box();
        assertThrows(IllegalArgumentException.class, () -> ledge.query(null, results));
        assertThrows(IllegalArgumentException.class, () -> ledge.query(area, null));
    }
}
