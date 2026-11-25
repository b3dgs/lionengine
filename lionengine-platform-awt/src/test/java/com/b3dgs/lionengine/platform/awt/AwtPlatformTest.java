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
package com.b3dgs.lionengine.platform.awt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.awt.GraphicsEnvironment;
import java.util.Collections;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.LogSink;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.render.ScalingPolicy;

/**
 * Test of {@link AwtPlatform} without display: arguments and frame pacing.
 */
final class AwtPlatformTest
{
    /** Windowed request. */
    private static final DisplayRequest REQUEST = new DisplayRequest(ScreenMode.WINDOWED,
                                                                     null,
                                                                     ScalingPolicy.FIT,
                                                                     Collections.emptyList());
    /** Sink writing nothing. */
    private static final LogSink SINK = (level, message, error) ->
    {
        // Nothing to write.
    };

    /**
     * Invalid creation arguments are rejected before the display is used.
     */
    @Test
    void testArguments()
    {
        assertThrows(IllegalArgumentException.class, () -> new AwtPlatform(0, 1, REQUEST, SINK));
        assertThrows(IllegalArgumentException.class, () -> new AwtPlatform(1, 0, REQUEST, SINK));
        assertThrows(IllegalArgumentException.class, () -> new AwtPlatform(1, 1, null, SINK));
        assertThrows(IllegalArgumentException.class, () -> new AwtPlatform(1, 1, REQUEST, null));
    }

    /**
     * Without display, the platform cannot be created.
     */
    @Test
    void testHeadless()
    {
        assumeTrue(GraphicsEnvironment.isHeadless());

        assertThrows(IllegalStateException.class, () -> new AwtPlatform(1, 1, REQUEST, SINK));
    }

    /**
     * Frames follow each other by one period; a frame late by more than one period starts a new schedule.
     */
    @Test
    void testNextDeadline()
    {
        assertEquals(110L, AwtPlatform.nextDeadline(100L, 10L, 105L));
        assertEquals(110L, AwtPlatform.nextDeadline(100L, 10L, 115L));
        assertEquals(110L, AwtPlatform.nextDeadline(100L, 10L, 120L));
        assertEquals(121L, AwtPlatform.nextDeadline(100L, 10L, 121L));
    }
}
