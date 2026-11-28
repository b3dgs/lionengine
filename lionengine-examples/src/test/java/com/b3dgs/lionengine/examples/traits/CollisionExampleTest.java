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
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.LogLevel;
import com.b3dgs.lionengine.physics.Box;
import com.b3dgs.lionengine.traits.CollisionListener;

/**
 * Tests of {@link CollisionExample} and {@link Arena}.
 */
final class CollisionExampleTest
{
    /**
     * Step by step: the events are those of the box overlaps computed with the same arithmetic, at the expected steps,
     * exits before enters, lower ID first; the ghost never appears and the balls are never moved by the collision.
     */
    @Test
    void testStepByStep()
    {
        final List<String> events = new ArrayList<>();
        final int[] step =
        {
            0
        };
        final Arena arena = new Arena(new CollisionListener()
        {
            @Override
            public void onEnter(long objectA, long objectB)
            {
                events.add(step[0] + " enter " + objectA + " " + objectB);
            }

            @Override
            public void onExit(long objectA, long objectB)
            {
                events.add(step[0] + " exit " + objectA + " " + objectB);
            }
        });
        final long left = arena.getLeft();
        final long right = arena.getRight();
        final long zone = arena.getZone();
        final List<String> expected = expected(left, right, zone);
        double x = -Arena.START;
        for (step[0] = 1; step[0] <= CollisionExample.STEPS; step[0]++)
        {
            arena.step();
            x += Arena.SPEED * Arena.STEP_SECONDS;
            assertEquals(x, arena.getTransformable(left).getX());
            assertEquals(-x, arena.getTransformable(right).getX());
        }

        assertEquals(expected, events);
        assertEquals(List.of("61 enter " + left + " " + zone,
                             "61 enter " + right + " " + zone,
                             "101 enter " + left + " " + right,
                             "141 exit " + left + " " + right,
                             "181 exit " + left + " " + zone,
                             "181 exit " + right + " " + zone),
                     events);
        assertTrue(left < right && right < zone && zone < arena.getGhost());
        assertEquals(0, arena.getPairCount());
    }

    /**
     * The example runs and ends with the balls swapped, apart and out of the zone.
     */
    @Test
    void testRun()
    {
        final Log log = new Log();
        log.setLevel(LogLevel.INFO);
        final Arena arena = CollisionExample.run(log);

        assertEquals(0, arena.getPairCount());
        assertEquals(Arena.START, arena.getTransformable(arena.getLeft()).getX(), 1e-9);
        assertEquals(-Arena.START, arena.getTransformable(arena.getRight()).getX(), 1e-9);
    }

    /**
     * Computes the expected events by the box overlaps, with the arithmetic of the movement.
     *
     * @param left The left ball.
     * @param right The right ball.
     * @param zone The zone.
     * @return The events, exits before enters in each step, in ascending pair order.
     */
    private static List<String> expected(long left, long right, long zone)
    {
        final Box zoneBox = new Box(0.0, 0.0, Arena.ZONE_WIDTH, Arena.ZONE_HEIGHT);
        final List<String> expected = new ArrayList<>();
        boolean meeting = false;
        boolean inZone = false;
        double x = -Arena.START;
        for (int step = 1; step <= CollisionExample.STEPS; step++)
        {
            x += Arena.SPEED * Arena.STEP_SECONDS;
            final Box leftBox = new Box(x, 0.0, Arena.BALL_SIZE, Arena.BALL_SIZE);
            final Box rightBox = new Box(-x, 0.0, Arena.BALL_SIZE, Arena.BALL_SIZE);
            final boolean meets = leftBox.overlaps(rightBox);
            final boolean enters = leftBox.overlaps(zoneBox);
            assertEquals(enters, rightBox.overlaps(zoneBox));
            final List<String> exits = new ArrayList<>();
            final List<String> enter = new ArrayList<>();
            if (meets != meeting)
            {
                (meets ? enter : exits).add(step + (meets ? " enter " : " exit ") + left + " " + right);
            }
            if (enters != inZone)
            {
                (enters ? enter : exits).add(step + (enters ? " enter " : " exit ") + left + " " + zone);
                (enters ? enter : exits).add(step + (enters ? " enter " : " exit ") + right + " " + zone);
            }
            expected.addAll(exits);
            expected.addAll(enter);
            meeting = meets;
            inZone = enters;
        }
        return expected;
    }
}
