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
package com.b3dgs.lionengine.graphics;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Test {@link Animation} and {@link AnimationLoop}: animations built in code with one duration for every frame or one
 * per frame, copies of the arrays given, invalid values, equality and text (REQ-ANIM-001, ADR-0026, ADR-0033).
 */
final class AnimationTest
{
    /** Frames of the walk. */
    private static final int[] WALK =
    {
        0, 1, 2, 1
    };

    /**
     * An animation whose frames all last the same duration gives each frame that duration.
     */
    @Test
    void testFrameDuration()
    {
        final Animation walk = new Animation(WALK, 0.1, AnimationLoop.LOOP);

        assertEquals(4, walk.getFrameCount());
        assertEquals(2, walk.getFrame(2));
        assertEquals(1, walk.getFrame(3));
        assertEquals(0.1, walk.getFrameDuration(0));
        assertEquals(0.1, walk.getFrameDuration(3));
        assertEquals(AnimationLoop.LOOP, walk.getLoop());
    }

    /**
     * An animation with a duration per frame keeps each duration; the arrays given are copied.
     */
    @Test
    void testFrameDurations()
    {
        final int[] frames =
        {
            3, 4, 5
        };
        final double[] durations =
        {
            0.1, 0.25, 0.5
        };
        final Animation jump = new Animation(frames, durations, AnimationLoop.ONCE);
        frames[0] = 9;
        durations[0] = 9.0;

        assertEquals(3, jump.getFrame(0));
        assertEquals(0.1, jump.getFrameDuration(0));
        assertEquals(0.25, jump.getFrameDuration(1));
        assertEquals(0.5, jump.getFrameDuration(2));
        assertEquals(AnimationLoop.ONCE, jump.getLoop());
    }

    /**
     * Animations of equal frames, durations and loop mode are equal, however their durations were given.
     */
    @Test
    void testEquality()
    {
        final Animation walk = new Animation(WALK, 0.1, AnimationLoop.LOOP);
        final Animation same = new Animation(WALK, new double[]
        {
            0.1, 0.1, 0.1, 0.1
        }, AnimationLoop.LOOP);

        assertEquals(walk, same);
        assertEquals(walk.hashCode(), same.hashCode());
        assertNotEquals(walk, new Animation(WALK, 0.1, AnimationLoop.PING_PONG));
        assertNotEquals(walk, new Animation(WALK, 0.2, AnimationLoop.LOOP));
        assertNotEquals(walk, new Animation(new int[]
        {
            0, 1
        }, 0.1, AnimationLoop.LOOP));
        assertEquals("Animation[frames=[0, 1, 2, 1], durations=[0.1, 0.1, 0.1, 0.1], loop=loop]", walk.toString());
    }

    /**
     * The loop modes have the names of the sheet definition.
     */
    @Test
    void testLoopNames()
    {
        assertEquals("once", AnimationLoop.ONCE.getName());
        assertEquals("loop", AnimationLoop.LOOP.getName());
        assertEquals("ping-pong", AnimationLoop.PING_PONG.getName());
        assertEquals(AnimationLoop.PING_PONG, AnimationLoop.fromName("ping-pong"));
        assertNull(AnimationLoop.fromName("PING_PONG"));
        assertThrows(IllegalArgumentException.class, () -> AnimationLoop.fromName(null));
    }

    /**
     * Missing, empty or negative frames, a missing loop mode and durations not one per frame are refused.
     */
    @Test
    void testInvalid()
    {
        final double[] two =
        {
            0.1, 0.1
        };

        assertThrows(IllegalArgumentException.class, () -> new Animation(null, 0.1, AnimationLoop.LOOP));
        assertThrows(IllegalArgumentException.class, () -> new Animation(new int[0], 0.1, AnimationLoop.LOOP));
        assertThrows(IllegalArgumentException.class, () -> new Animation(new int[]
        {
            0, -1
        }, 0.1, AnimationLoop.LOOP));
        assertThrows(IllegalArgumentException.class, () -> new Animation(WALK, 0.1, null));
        assertThrows(IllegalArgumentException.class, () -> new Animation(WALK, (double[]) null, AnimationLoop.LOOP));
        assertThrows(IllegalArgumentException.class, () -> new Animation(WALK, two, AnimationLoop.LOOP));
    }

    /**
     * A duration that is not positive and finite is refused, alone or in a list.
     *
     * @param duration The invalid duration.
     */
    @ParameterizedTest
    @ValueSource(doubles =
    {
        0.0, -0.0, -0.1, Double.NaN, Double.POSITIVE_INFINITY
    })
    void testInvalidDuration(double duration)
    {
        final double[] durations =
        {
            0.1, duration, 0.1, 0.1
        };

        assertThrows(IllegalArgumentException.class, () -> new Animation(WALK, duration, AnimationLoop.LOOP));
        assertThrows(IllegalArgumentException.class, () -> new Animation(WALK, durations, AnimationLoop.LOOP));
    }

    /**
     * A frame index outside the animation is refused.
     *
     * @param index The invalid index.
     */
    @ParameterizedTest
    @ValueSource(ints =
    {
        -1, 4
    })
    void testIndexOutside(int index)
    {
        final Animation walk = new Animation(WALK, 0.1, AnimationLoop.LOOP);

        assertThrows(IndexOutOfBoundsException.class, () -> walk.getFrame(index));
        assertThrows(IndexOutOfBoundsException.class, () -> walk.getFrameDuration(index));
    }
}
