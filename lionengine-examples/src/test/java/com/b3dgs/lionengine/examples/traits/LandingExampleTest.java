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
 * Tests of {@link LandingExample}, {@link Landing} and {@link Ground}.
 */
final class LandingExampleTest
{
    /** Tolerance on accumulated values. */
    private static final double EPSILON = 1e-9;
    /** Hero half size, in meters. */
    private static final double HALF = Landing.SIZE / 2.0;
    /** The step the hero lands at. */
    private static final int LANDING_STEP = 36;
    /** The step the hero meets the wall at. */
    private static final int WALL_STEP = 164;

    /**
     * Step by step: the hero falls in the air, lands exactly on the floor at step {@value #LANDING_STEP}, grounded, its
     * vertical velocity zeroed; walks across the floor seams at 2 m/s from the next step, grounded at each step; meets
     * the wall at step {@value #WALL_STEP} and stays against it, its horizontal velocity zeroed.
     */
    @Test
    void testStepByStep()
    {
        final Landing landing = new Landing();
        double y = Landing.START_Y;
        double speed = 0.0;
        for (int step = 1; step < LANDING_STEP; step++)
        {
            landing.step();
            speed += Landing.ACCELERATION * Landing.STEP_SECONDS;
            y += speed * Landing.STEP_SECONDS;
            assertFalse(landing.isGrounded(), "Step " + step);
            assertEquals(y, landing.getHero().getY(), EPSILON, "Height at step " + step);
            assertEquals(Landing.START_X, landing.getHero().getX());
        }

        landing.step();
        assertTrue(landing.isGrounded());
        assertEquals(Ground.FLOOR_TOP + HALF, landing.getHero().getY());
        assertEquals(0.0, landing.getHeroMovable().getVelocityY());

        double x = Landing.START_X;
        for (int step = LANDING_STEP + 1; step < WALL_STEP; step++)
        {
            landing.step();
            x += Landing.WALK_SPEED * Landing.STEP_SECONDS;
            assertTrue(landing.isGrounded(), "Step " + step);
            assertEquals(x, landing.getHero().getX(), EPSILON, "X at step " + step);
            assertEquals(Ground.FLOOR_TOP + HALF, landing.getHero().getY());
            assertEquals(Landing.WALK_SPEED, landing.getHeroMovable().getVelocityX());
        }

        landing.step();
        assertEquals(Ground.WALL_LEFT, landing.getHero().getX() + HALF);
        assertEquals(0.0, landing.getHeroMovable().getVelocityX());
        assertTrue(landing.isGrounded());
    }

    /**
     * The example runs: the hero ends against the wall, on the floor, grounded.
     */
    @Test
    void testRun()
    {
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);
        final Landing landing = LandingExample.run(log);

        assertEquals(Ground.WALL_LEFT - HALF, landing.getHero().getX());
        assertEquals(Ground.FLOOR_TOP + HALF, landing.getHero().getY());
        assertEquals(0.0, landing.getHeroMovable().getVelocityX());
        assertTrue(landing.isGrounded());
    }

    /**
     * The ground finds its overlapping tiles only, counts beyond the buffer, and refuses {@code null}.
     */
    @Test
    void testGround()
    {
        final Ground ground = new Ground();
        final Box[] results =
        {
            new Box(), new Box()
        };

        assertEquals(2, ground.query(new Box(6.5, 1.0, 0.5, 0.5), results));
        assertEquals(0, ground.query(new Box(3.0, 1.25, 0.5, 0.5), results));
        assertEquals(Ground.FLOOR_TILES, ground.query(new Box(4.0, 0.5, 8.0, 0.5), results));
        final Box area = new Box();
        assertThrows(IllegalArgumentException.class, () -> ground.query(null, results));
        assertThrows(IllegalArgumentException.class, () -> ground.query(area, null));
    }
}
