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
package com.b3dgs.lionengine.render;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

/**
 * Test the scaling function {@link FrameScaling#of(ScalingPolicy, int, int, int, int)} for each policy, including the
 * {@code INTEGER} fallback and an empty surface (REQ-DISPLAY-004, REQ-DISPLAY-007).
 */
final class FrameScalingTest
{
    /** Logical frame width. */
    private static final int WIDTH = 320;
    /** Logical frame height. */
    private static final int HEIGHT = 240;
    /** Scale of the tests. */
    private static final double THREE = 3.0;

    /**
     * {@code FIT} keeps the aspect ratio, uses the smaller axis ratio and centers the frame.
     */
    @Test
    void testFit()
    {
        assertEquals(new FrameScaling(ScalingPolicy.FIT, 160, 0, 960, 720, THREE, THREE),
                     FrameScaling.of(ScalingPolicy.FIT, WIDTH, HEIGHT, 1280, 720));
        assertEquals(new FrameScaling(ScalingPolicy.FIT, 0, 60, 640, 480, 2.0, 2.0),
                     FrameScaling.of(ScalingPolicy.FIT, WIDTH, HEIGHT, 640, 600));
        assertEquals(new FrameScaling(ScalingPolicy.FIT, 0, 0, WIDTH, HEIGHT, 1.0, 1.0),
                     FrameScaling.of(ScalingPolicy.FIT, WIDTH, HEIGHT, WIDTH, HEIGHT));
    }

    /**
     * {@code FIT} with a non-integer scale rounds the presented size to the nearest pixel, never beyond the surface,
     * and puts the extra pixel of an odd margin on the right or bottom.
     */
    @Test
    void testFitRounding()
    {
        final double scale = 1001.0 / WIDTH;
        final FrameScaling scaling = FrameScaling.of(ScalingPolicy.FIT, WIDTH, HEIGHT, 1001, 800);

        assertEquals(new FrameScaling(ScalingPolicy.FIT, 0, 24, 1001, 751, scale, scale), scaling);

        final FrameScaling small = FrameScaling.of(ScalingPolicy.FIT, WIDTH, HEIGHT, 161, 120);
        assertEquals(0, small.x());
        assertEquals(160, small.width());
        assertEquals(120, small.height());
    }

    /**
     * {@code INTEGER} uses the largest integer scale that fits, centered.
     */
    @Test
    void testInteger()
    {
        assertEquals(new FrameScaling(ScalingPolicy.INTEGER, 160, 0, 960, 720, THREE, THREE),
                     FrameScaling.of(ScalingPolicy.INTEGER, WIDTH, HEIGHT, 1280, 720));
        assertEquals(new FrameScaling(ScalingPolicy.INTEGER, 81, 3, 640, 480, 2.0, 2.0),
                     FrameScaling.of(ScalingPolicy.INTEGER, WIDTH, HEIGHT, 803, 487));
    }

    /**
     * {@code INTEGER} falls back to {@code FIT} when the surface is smaller than the logical frame, and reports it.
     */
    @Test
    void testIntegerFallback()
    {
        assertEquals(new FrameScaling(ScalingPolicy.FIT, 0, 87, 300, 225, 300.0 / WIDTH, 300.0 / WIDTH),
                     FrameScaling.of(ScalingPolicy.INTEGER, WIDTH, HEIGHT, 300, 400));
        assertEquals(ScalingPolicy.FIT, FrameScaling.of(ScalingPolicy.INTEGER, WIDTH, HEIGHT, 1000, 200).policy());
    }

    /**
     * {@code STRETCH} fills the surface with a scale per axis.
     */
    @Test
    void testStretch()
    {
        assertEquals(new FrameScaling(ScalingPolicy.STRETCH, 0, 0, 1280, 720, 4.0, THREE),
                     FrameScaling.of(ScalingPolicy.STRETCH, WIDTH, HEIGHT, 1280, 720));
    }

    /**
     * An empty surface gives an empty rectangle and scales of {@code 1}; {@code INTEGER} still reports its fallback.
     */
    @Test
    void testEmptySurface()
    {
        assertEquals(new FrameScaling(ScalingPolicy.FIT, 0, 0, 0, 0, 1.0, 1.0),
                     FrameScaling.of(ScalingPolicy.FIT, WIDTH, HEIGHT, 0, 720));
        assertEquals(new FrameScaling(ScalingPolicy.STRETCH, 0, 0, 0, 0, 1.0, 1.0),
                     FrameScaling.of(ScalingPolicy.STRETCH, WIDTH, HEIGHT, 1280, 0));
        assertEquals(ScalingPolicy.FIT, FrameScaling.of(ScalingPolicy.INTEGER, WIDTH, HEIGHT, 0, 0).policy());
    }

    /**
     * Invalid arguments are rejected.
     */
    @Test
    void testInvalid()
    {
        assertThrows(IllegalArgumentException.class, () -> FrameScaling.of(null, WIDTH, HEIGHT, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> FrameScaling.of(ScalingPolicy.FIT, 0, HEIGHT, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> FrameScaling.of(ScalingPolicy.FIT, WIDTH, 0, 1, 1));
        assertThrows(IllegalArgumentException.class, () -> FrameScaling.of(ScalingPolicy.FIT, WIDTH, HEIGHT, -1, 1));
        assertThrows(IllegalArgumentException.class, () -> FrameScaling.of(ScalingPolicy.FIT, WIDTH, HEIGHT, 1, -1));
        assertThrows(IllegalArgumentException.class, () -> new FrameScaling(null, 0, 0, 0, 0, 1.0, 1.0));
        assertThrows(IllegalArgumentException.class,
                     () -> new FrameScaling(ScalingPolicy.FIT, 0, 0, -1, 0, 1.0, 1.0));
        assertThrows(IllegalArgumentException.class,
                     () -> new FrameScaling(ScalingPolicy.FIT, 0, 0, 0, -1, 1.0, 1.0));
        assertThrows(IllegalArgumentException.class,
                     () -> new FrameScaling(ScalingPolicy.FIT, 0, 0, 0, 0, 0.0, 1.0));
        assertThrows(IllegalArgumentException.class,
                     () -> new FrameScaling(ScalingPolicy.FIT, 0, 0, 0, 0, 1.0, Double.POSITIVE_INFINITY));
    }
}
