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

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.InboxSpy;
import com.b3dgs.lionengine.render.ColorEffect;
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
            public boolean isSupported(ColorEffect effect)
            {
                return true;
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

        state.resolve(request(ScalingPolicy.INTEGER), null, "");

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

        state.resolve(request(ScalingPolicy.INTEGER), null, "Platform reason");

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

        state.resolve(request(ScalingPolicy.INTEGER, OutputFilter.CRT), null, "");
        assertEquals("INTEGER scaling falls back to FIT: the surface is smaller than the logical frame; "
                     + "Output filters not applied: no render backend attached",
                     state.getResult().reason());

        state.resolve(request(ScalingPolicy.FIT, OutputFilter.CRT), null, "Platform reason");
        assertEquals("Platform reason; Output filters not applied: no render backend attached",
                     state.getResult().reason());

        state.resolve(request(ScalingPolicy.FIT), null, "");
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

        state.resolve(request(ScalingPolicy.FIT, OutputFilter.NONE), null, "");
        assertFalse(state.getResult().degraded());

        state.resolve(request(ScalingPolicy.FIT, OutputFilter.CRT, OutputFilter.HQ2X), null, "");

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

        state.resolve(request(ScalingPolicy.FIT, OutputFilter.NONE, OutputFilter.CRT, OutputFilter.BILINEAR), null, "");

        assertEquals(List.of(OutputFilter.BILINEAR), state.getResult().effective().filters());
        assertTrue(state.getResult().degraded());
        assertEquals("Output filter not implemented by the render backend, skipped: lionengine:crt",
                     state.getResult().reason());
    }

    /**
     * An unsupported screen mode falls back to the first supported mode of its order, reported first among the
     * reasons; the display mode is the one given by the platform (ADR-0006, ADR-0060).
     */
    @Test
    void testPlatformModes()
    {
        final DisplayState state = new DisplayState(WIDTH, HEIGHT);
        final DisplayMode mode = new DisplayMode(1920, 1080, 60, 32);
        state.setSupported(EnumSet.of(ScreenMode.WINDOWED));

        state.resolve(new DisplayRequest(ScreenMode.FULL_SCREEN, null, ScalingPolicy.FIT, List.of()),
                      mode,
                      "Platform reason");

        assertEquals(ScreenMode.FULL_SCREEN, state.getResult().requested().screenMode());
        assertEquals(ScreenMode.WINDOWED, state.getResult().effective().screenMode());
        assertEquals(mode, state.getResult().effective().displayMode());
        assertTrue(state.getResult().degraded());
        assertEquals("FULL_SCREEN not supported, WINDOWED used; Platform reason", state.getResult().reason());
    }

    /**
     * Each requested mode falls back in the order of ADR-0006, to the first supported one; a supported mode is kept.
     */
    @Test
    void testFallbackOrders()
    {
        final DisplayState state = new DisplayState(WIDTH, HEIGHT);

        assertEquals(ScreenMode.FULL_SCREEN, state.selectScreenMode(ScreenMode.FULL_SCREEN));
        state.setSupported(EnumSet.of(ScreenMode.WINDOWED, ScreenMode.FULL_WINDOW));
        assertEquals(ScreenMode.FULL_WINDOW, state.selectScreenMode(ScreenMode.FULL_SCREEN));
        state.setSupported(EnumSet.of(ScreenMode.FULL_WINDOW, ScreenMode.FULL_SCREEN));
        assertEquals(ScreenMode.FULL_WINDOW, state.selectScreenMode(ScreenMode.WINDOWED));
        state.setSupported(EnumSet.of(ScreenMode.FULL_SCREEN));
        assertEquals(ScreenMode.FULL_SCREEN, state.selectScreenMode(ScreenMode.WINDOWED));
        assertEquals(ScreenMode.FULL_SCREEN, state.selectScreenMode(ScreenMode.FULL_WINDOW));
        state.setSupported(EnumSet.of(ScreenMode.WINDOWED, ScreenMode.FULL_SCREEN));
        assertEquals(ScreenMode.WINDOWED, state.selectScreenMode(ScreenMode.FULL_WINDOW));
        state.setSupported(EnumSet.of(ScreenMode.WINDOWED));
        assertEquals(ScreenMode.WINDOWED, state.selectScreenMode(ScreenMode.FULL_SCREEN));
        assertTrue(state.isSupported(ScreenMode.WINDOWED));
        assertFalse(state.isSupported(ScreenMode.FULL_WINDOW));
        assertThrows(IllegalArgumentException.class, () -> state.setSupported(EnumSet.noneOf(ScreenMode.class)));
        assertThrows(IllegalArgumentException.class, () -> state.setSupported(null));
        assertThrows(IllegalArgumentException.class, () -> state.isSupported(null));
    }

    /**
     * The posted result reaches the listeners added by its delivery, in order; a listener added during the delivery
     * is called from the next one; a removed listener is not called.
     */
    @Test
    void testListeners()
    {
        final DisplayState state = new DisplayState(WIDTH, HEIGHT);
        final List<String> calls = new ArrayList<>();
        final InboxSpy inbox = new InboxSpy();
        final DisplayListener second = result -> calls.add("second " + result.effective().scalingPolicy());
        state.addListener(result ->
        {
            calls.add("first " + result.effective().scalingPolicy());
            state.addListener(second);
        });
        final DisplayListener removed = result -> calls.add("removed");
        state.addListener(removed);
        state.removeListener(removed);
        state.resolve(request(ScalingPolicy.FIT), null, "");

        assertTrue(state.post(inbox));
        state.resolve(request(ScalingPolicy.STRETCH), null, "");
        inbox.deliverAll();
        assertEquals(List.of("first FIT"), calls);
        state.post(inbox);
        inbox.deliverAll();

        assertEquals(List.of("first FIT", "first STRETCH", "second STRETCH"), calls);
        assertThrows(IllegalArgumentException.class, () -> state.addListener(null));
    }

    /**
     * A display mode matches a request of the same size, and of the same refresh rate and bit depth where they are
     * known (ADR-0061).
     */
    @Test
    void testMatches()
    {
        final DisplayMode mode = new DisplayMode(1920, 1080, 60, 32);

        assertTrue(DisplayState.matches(mode, mode));
        assertTrue(DisplayState.matches(new DisplayMode(1920, 1080, 0, 0), mode));
        assertTrue(DisplayState.matches(new DisplayMode(1920, 1080, 60, 0), mode));
        assertTrue(DisplayState.matches(new DisplayMode(1920, 1080, 0, 32), mode));
        assertFalse(DisplayState.matches(new DisplayMode(1920, 1080, 75, 0), mode));
        assertFalse(DisplayState.matches(new DisplayMode(1920, 1080, 0, 16), mode));
        assertFalse(DisplayState.matches(new DisplayMode(1920, 1000, 60, 32), mode));
        assertFalse(DisplayState.matches(new DisplayMode(1900, 1080, 60, 32), mode));
        assertFalse(DisplayState.matches(mode, new DisplayMode(1920, 1080, 0, 0)));
    }

    /**
     * The display modes are copied in order without duplicates; none is selected until they are set, and a request
     * without display mode keeps the current one (ADR-0061).
     */
    @Test
    void testDisplayModes()
    {
        final DisplayState state = new DisplayState(WIDTH, HEIGHT);
        final DisplayMode large = new DisplayMode(1920, 1080, 60, 32);
        final DisplayMode small = new DisplayMode(640, 480, 60, 32);
        final DisplayMode requested = new DisplayMode(800, 600, 60, 32);
        assertTrue(state.getDisplayModes().isEmpty());
        assertEquals(large, state.selectDisplayMode(requested, large));

        final List<DisplayMode> modes = new ArrayList<>(List.of(small, large, small));
        state.setDisplayModes(modes);
        modes.clear();

        assertEquals(List.of(small, large), state.getDisplayModes());
        assertThrows(UnsupportedOperationException.class, () -> state.getDisplayModes().add(small));
        assertEquals(large, state.selectDisplayMode(null, large));
        assertEquals(small, state.selectDisplayMode(requested, large));
        assertThrows(IllegalArgumentException.class, () -> state.selectDisplayMode(requested, null));
        assertThrows(IllegalArgumentException.class, () -> state.setDisplayModes(null));
        assertThrows(IllegalArgumentException.class, () -> state.setDisplayModes(List.of()));
        assertThrows(IllegalArgumentException.class, () -> state.setDisplayModes(Collections.singletonList(null)));
    }

    /**
     * The selection takes the exact mode, then the nearest size, then the nearest refresh rate, then the nearest bit
     * depth, then the first offered; an unknown requested value is compared to the current mode's (ADR-0006,
     * ADR-0061).
     */
    @Test
    void testSelectDisplayMode()
    {
        final DisplayState state = new DisplayState(WIDTH, HEIGHT);
        final DisplayMode hd60 = new DisplayMode(1280, 720, 60, 32);
        final DisplayMode hd75 = new DisplayMode(1280, 720, 75, 32);
        final DisplayMode hd75low = new DisplayMode(1280, 720, 75, 16);
        final DisplayMode full = new DisplayMode(1920, 1080, 60, 32);
        final DisplayMode wide = new DisplayMode(1366, 768, 60, 32);
        state.setDisplayModes(List.of(full, hd75low, hd75, hd60, wide));

        assertEquals(hd75, state.selectDisplayMode(hd75, full));
        assertEquals(hd60, state.selectDisplayMode(new DisplayMode(1280, 720, 0, 0), full));
        assertEquals(hd75, state.selectDisplayMode(new DisplayMode(1280, 720, 0, 0), hd75));
        assertEquals(hd75, state.selectDisplayMode(new DisplayMode(1280, 720, 72, 0), full));
        assertEquals(hd75low, state.selectDisplayMode(new DisplayMode(1280, 720, 75, 15), full));
        assertEquals(hd75low, state.selectDisplayMode(new DisplayMode(1280, 720, 75, 24), full));
        assertEquals(wide, state.selectDisplayMode(new DisplayMode(1360, 760, 60, 32), full));
        assertEquals(full, state.selectDisplayMode(new DisplayMode(3840, 2160, 60, 32), full));
        assertEquals(hd60, state.selectDisplayMode(new DisplayMode(640, 480, 60, 32), full));
    }

    /**
     * A display mode given by the platform that does not match the request is reported after the screen-mode
     * fallback and before the platform's reasons; an unknown requested value is no degradation (ADR-0061).
     */
    @Test
    void testDisplayModeReason()
    {
        final DisplayState state = new DisplayState(WIDTH, HEIGHT);
        final DisplayMode mode = new DisplayMode(1920, 1080, 60, 32);
        state.setSupported(EnumSet.of(ScreenMode.WINDOWED));

        state.resolve(new DisplayRequest(ScreenMode.FULL_SCREEN,
                                         new DisplayMode(800, 600, 75, 32),
                                         ScalingPolicy.FIT,
                                         List.of()),
                      mode,
                      "Platform reason");

        assertEquals("FULL_SCREEN not supported, WINDOWED used; "
                     + "Display mode 800x600 75Hz 32bits not available, 1920x1080 60Hz 32bits used; Platform reason",
                     state.getResult().reason());

        state.resolve(new DisplayRequest(ScreenMode.WINDOWED,
                                         new DisplayMode(1920, 1080, 0, 0),
                                         ScalingPolicy.FIT,
                                         List.of()),
                      mode,
                      "");

        assertFalse(state.getResult().degraded());
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
