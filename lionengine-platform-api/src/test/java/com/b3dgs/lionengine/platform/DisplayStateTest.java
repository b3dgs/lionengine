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
package com.b3dgs.lionengine.platform;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.RenderBackend;
import com.b3dgs.lionengine.render.ScalingPolicy;
import com.b3dgs.lionengine.render.TextureStore;

/**
 * Test {@link DisplayState}: scaling fallback, filter selection and geometry shared by the platforms (ADR-0006,
 * ADR-0046, REQ-DISPLAY-007, REQ-DISPLAY-008, REQ-FILTER-002).
 */
final class DisplayStateTest
{
    /** Logical frame width. */
    private static final int WIDTH = 320;
    /** Logical frame height. */
    private static final int HEIGHT = 180;

    /**
     * Creates a request.
     *
     * @param policy The scaling policy.
     * @param filters The filters.
     * @return The request.
     */
    private static DisplayRequest request(ScalingPolicy policy, OutputFilter... filters)
    {
        return new DisplayRequest(ScreenMode.WINDOWED, null, policy, List.of(filters));
    }

    /**
     * Creates a backend implementing only the given filter, besides {@code NONE}.
     *
     * @param supported The supported filter.
     * @return The backend.
     */
    private static RenderBackend backend(OutputFilter supported)
    {
        return new RenderBackend()
        {
            @Override
            public TextureStore getTextureStore()
            {
                return null;
            }

            @Override
            public boolean isSupported(OutputFilter filter)
            {
                return OutputFilter.NONE.equals(filter) || supported.equals(filter);
            }

            @Override
            public void present(CommandBuffer commands)
            {
                // Nothing to draw.
            }
        };
    }

    /**
     * Nothing is resolved before the first call, and the surface starts at the logical frame size.
     */
    @Test
    void testInitial()
    {
        final DisplayState state = new DisplayState(WIDTH, HEIGHT);

        assertNull(state.getResult());
        assertNull(state.getGeometry());
        assertEquals(WIDTH, state.getSurfaceWidth());
        assertEquals(HEIGHT, state.getSurfaceHeight());
    }

    /**
     * A sound request is not degraded, and the geometry is the scaling of the surface.
     */
    @Test
    void testResolve()
    {
        final DisplayState state = new DisplayState(WIDTH, HEIGHT);
        state.setSurface(WIDTH * 2, HEIGHT * 2);

        state.resolve(request(ScalingPolicy.INTEGER), ScreenMode.WINDOWED, null, "");

        assertFalse(state.getResult().degraded());
        assertEquals("", state.getResult().reason());
        assertEquals(ScalingPolicy.INTEGER, state.getResult().effective().scalingPolicy());
        assertEquals(new PresentationGeometry(0, 0, WIDTH * 2, HEIGHT * 2, 2.0, 2.0), state.getGeometry());
    }

    /**
     * An {@code INTEGER} request on a surface smaller than the frame falls back to {@code FIT} and is explained after
     * the reasons of the platform.
     */
    @Test
    void testIntegerFallback()
    {
        final DisplayState state = new DisplayState(WIDTH, HEIGHT);
        state.setSurface(WIDTH / 2, HEIGHT / 2);

        state.resolve(request(ScalingPolicy.INTEGER), ScreenMode.WINDOWED, null, "Platform reason");

        assertTrue(state.getResult().degraded());
        assertEquals(ScalingPolicy.FIT, state.getResult().effective().scalingPolicy());
        assertEquals("Platform reason; INTEGER scaling falls back to FIT: the surface is smaller than the logical frame",
                     state.getResult().reason());
    }

    /**
     * The reasons found by the state are joined with one separator, also when the platform gives none (regression:
     * the emptiness test of the reasons called {@code CharSequence.isEmpty()}, missing on Android API 21).
     */
    @Test
    void testReasonsJoined()
    {
        final DisplayState state = new DisplayState(WIDTH, HEIGHT);
        state.setSurface(WIDTH / 2, HEIGHT / 2);

        state.resolve(request(ScalingPolicy.INTEGER, OutputFilter.CRT), ScreenMode.WINDOWED, null, "");
        assertEquals("INTEGER scaling falls back to FIT: the surface is smaller than the logical frame; "
                     + "Output filters not applied: no render backend attached",
                     state.getResult().reason());

        state.resolve(request(ScalingPolicy.FIT, OutputFilter.CRT), ScreenMode.WINDOWED, null, "Platform reason");
        assertEquals("Platform reason; Output filters not applied: no render backend attached",
                     state.getResult().reason());

        state.resolve(request(ScalingPolicy.FIT), ScreenMode.WINDOWED, null, "");
        assertFalse(state.getResult().degraded());
        assertEquals("", state.getResult().reason());
    }

    /**
     * Without backend a filter is not applied, once; {@code NONE} is never a reason.
     */
    @Test
    void testFilterWithoutBackend()
    {
        final DisplayState state = new DisplayState(WIDTH, HEIGHT);

        state.resolve(request(ScalingPolicy.FIT, OutputFilter.NONE), ScreenMode.WINDOWED, null, "");
        assertFalse(state.getResult().degraded());

        state.resolve(request(ScalingPolicy.FIT, OutputFilter.CRT, OutputFilter.HQ2X), ScreenMode.WINDOWED, null, "");

        assertTrue(state.getResult().effective().filters().isEmpty());
        assertEquals("Output filters not applied: no render backend attached", state.getResult().reason());
    }

    /**
     * With a backend, the supported filters are kept in order without {@code NONE}, the others are skipped and named.
     */
    @Test
    void testFilterWithBackend()
    {
        final DisplayState state = new DisplayState(WIDTH, HEIGHT);
        state.setBackend(backend(OutputFilter.BILINEAR));

        state.resolve(request(ScalingPolicy.FIT, OutputFilter.NONE, OutputFilter.CRT, OutputFilter.BILINEAR),
                      ScreenMode.WINDOWED,
                      null,
                      "");

        assertEquals(List.of(OutputFilter.BILINEAR), state.getResult().effective().filters());
        assertTrue(state.getResult().degraded());
        assertEquals("Output filter not implemented by the render backend, skipped: lionengine:crt",
                     state.getResult().reason());
    }

    /**
     * The effective screen mode and display mode are the ones given by the platform.
     */
    @Test
    void testPlatformModes()
    {
        final DisplayState state = new DisplayState(WIDTH, HEIGHT);
        final DisplayMode mode = new DisplayMode(1920, 1080, 60, 32);

        state.resolve(new DisplayRequest(ScreenMode.FULL_SCREEN, null, ScalingPolicy.FIT, List.of()),
                      ScreenMode.WINDOWED,
                      mode,
                      "");

        assertEquals(ScreenMode.FULL_SCREEN, state.getResult().requested().screenMode());
        assertEquals(ScreenMode.WINDOWED, state.getResult().effective().screenMode());
        assertEquals(mode, state.getResult().effective().displayMode());
    }

    /**
     * A logical frame must have a positive size.
     */
    @Test
    void testInvalid()
    {
        assertThrows(IllegalArgumentException.class, () -> new DisplayState(0, HEIGHT));
        assertThrows(IllegalArgumentException.class, () -> new DisplayState(WIDTH, -1));
    }
}
