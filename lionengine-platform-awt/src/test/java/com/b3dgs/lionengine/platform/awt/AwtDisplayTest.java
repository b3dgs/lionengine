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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.platform.DisplayMode;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.EffectiveResult;
import com.b3dgs.lionengine.platform.PresentationGeometry;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.render.ColorEffect;
import com.b3dgs.lionengine.render.CommandBuffer;
import com.b3dgs.lionengine.render.CustomFilter;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.RenderBackend;
import com.b3dgs.lionengine.render.ScalingPolicy;
import com.b3dgs.lionengine.render.TextureStore;

/**
 * Test of {@link AwtDisplay}: display state of the AWT platform, headless.
 */
final class AwtDisplayTest
{
    /** Logical frame width. */
    private static final int WIDTH = 320;
    /** Logical frame height. */
    private static final int HEIGHT = 240;
    /** Display mode of the monitor. */
    private static final DisplayMode MODE = new DisplayMode(1920, 1080, 60, 32);
    /** Smaller display mode offered by the monitor. */
    private static final DisplayMode SMALL = new DisplayMode(1280, 720, 60, 32);
    /** Display mode offered by the monitor at another refresh rate. */
    private static final DisplayMode FAST = new DisplayMode(1280, 720, 75, 32);
    /** Display modes of the monitor. */
    private static final List<DisplayMode> MODES = List.of(MODE, FAST, SMALL);

    /**
     * Creates the display state of a monitor offering {@link #MODES}, in {@link #MODE}.
     *
     * @param fullScreen {@code true} if the monitor supports exclusive full screen.
     * @param request The initial request.
     * @return The display state.
     */
    private static AwtDisplay display(boolean fullScreen, DisplayRequest request)
    {
        return new AwtDisplay(WIDTH, HEIGHT, MODES, MODE, fullScreen, request);
    }

    /**
     * Creates a request.
     *
     * @param screenMode The screen mode.
     * @param displayMode The display mode, {@code null} to keep it.
     * @param policy The scaling policy.
     * @param filters The filters.
     * @return The request.
     */
    private static DisplayRequest request(ScreenMode screenMode,
                                          DisplayMode displayMode,
                                          ScalingPolicy policy,
                                          List<OutputFilter> filters)
    {
        return new DisplayRequest(screenMode, displayMode, policy, filters);
    }

    /**
     * AWT's unknown refresh rate and multiple bit depth become {@link DisplayMode#UNKNOWN}.
     */
    @Test
    void testConvert()
    {
        assertEquals(new DisplayMode(800, 600, DisplayMode.UNKNOWN, DisplayMode.UNKNOWN),
                     AwtDisplay.convert(new java.awt.DisplayMode(800,
                                                                 600,
                                                                 java.awt.DisplayMode.BIT_DEPTH_MULTI,
                                                                 java.awt.DisplayMode.REFRESH_RATE_UNKNOWN)));
        assertEquals(MODE, AwtDisplay.convert(new java.awt.DisplayMode(1920, 1080, 32, 60)));
    }

    /**
     * A windowed request keeping the display mode is applied as requested; the surface starts at the logical size.
     */
    @Test
    void testWindowed()
    {
        final DisplayRequest request = request(ScreenMode.WINDOWED, MODE, ScalingPolicy.FIT, Collections.emptyList());
        final AwtDisplay display = display(false, request);

        final EffectiveResult result = display.getResult();
        assertSame(request, result.requested());
        assertEquals(request, result.effective());
        assertFalse(result.degraded());
        assertEquals("", result.reason());
        assertEquals(WIDTH, display.getSurfaceWidth());
        assertEquals(HEIGHT, display.getSurfaceHeight());
        assertSame(MODE, display.getDisplayMode());
        assertEquals(new PresentationGeometry(0, 0, WIDTH, HEIGHT, 1.0, 1.0), display.getGeometry());
    }

    /**
     * Without exclusive full screen, {@code FULL_SCREEN} falls back to {@code FULL_WINDOW}; a display mode is changed
     * only in {@code FULL_SCREEN}, so the desktop mode is kept; both are reported (ADR-0060, ADR-0061).
     */
    @Test
    void testFallbackAndDisplayMode()
    {
        final DisplayMode other = new DisplayMode(640, 480, 60, 32);
        final AwtDisplay display = display(false,
                                           request(ScreenMode.FULL_SCREEN,
                                                   other,
                                                   ScalingPolicy.STRETCH,
                                                   Collections.emptyList()));

        final EffectiveResult result = display.getResult();
        assertEquals(ScreenMode.FULL_WINDOW, result.effective().screenMode());
        assertEquals(ScreenMode.FULL_WINDOW, display.getScreenMode());
        assertEquals(MODE, display.getTarget());
        assertEquals(MODE, result.effective().displayMode());
        assertEquals(ScalingPolicy.STRETCH, result.effective().scalingPolicy());
        assertTrue(result.degraded());
        assertEquals("FULL_SCREEN not supported, FULL_WINDOW used; "
                     + "Display mode 640x480 60Hz 32bits not available, 1920x1080 60Hz 32bits used; "
                     + "Display mode change needs FULL_SCREEN, the desktop mode is kept",
                     result.reason());
        assertTrue(display.isSupported(ScreenMode.WINDOWED));
        assertTrue(display.isSupported(ScreenMode.FULL_WINDOW));
        assertFalse(display.isSupported(ScreenMode.FULL_SCREEN));
        assertEquals(MODES, display.getDisplayModes());
    }

    /**
     * In {@code FULL_SCREEN}, the target is the offered mode nearest to the request, the current refresh rate kept
     * when the request does not give one; the result reports the mode in effect once the window realized it, and the
     * refusal of the system when it differs (ADR-0061).
     */
    @Test
    void testFullScreenDisplayMode()
    {
        final DisplayRequest request = request(ScreenMode.FULL_SCREEN,
                                               new DisplayMode(1300, 700, DisplayMode.UNKNOWN, DisplayMode.UNKNOWN),
                                               ScalingPolicy.FIT,
                                               Collections.emptyList());
        final AwtDisplay display = display(true, request);
        assertEquals(SMALL, display.getTarget());
        assertEquals(MODE, display.getResult().effective().displayMode());
        assertEquals("Display mode 1300x700 0Hz 0bits not available, 1920x1080 60Hz 32bits used",
                     display.getResult().reason());

        display.setCurrent(SMALL);

        assertEquals(SMALL, display.getDisplayMode());
        assertEquals(SMALL, display.getResult().effective().displayMode());
        assertEquals("Display mode 1300x700 0Hz 0bits not available, 1280x720 60Hz 32bits used",
                     display.getResult().reason());

        display.apply(request(ScreenMode.FULL_SCREEN, FAST, ScalingPolicy.FIT, Collections.emptyList()));
        assertEquals(FAST, display.getTarget());
        display.setCurrent(SMALL);

        assertEquals("Display mode 1280x720 75Hz 32bits not available, 1280x720 60Hz 32bits used; "
                     + "Display mode change refused by the system, the current mode is kept",
                     display.getResult().reason());

        display.apply(request(ScreenMode.FULL_SCREEN, null, ScalingPolicy.FIT, Collections.emptyList()));
        assertEquals(SMALL, display.getTarget());
        assertFalse(display.getResult().degraded());

        display.apply(request(ScreenMode.WINDOWED, null, ScalingPolicy.FIT, Collections.emptyList()));
        assertEquals(MODE, display.getTarget());
        display.setCurrent(MODE);
        assertFalse(display.getResult().degraded());
        assertEquals(MODE, display.getResult().effective().displayMode());
    }

    /**
     * With exclusive full screen, the three screen modes are supported and applied as requested.
     */
    @Test
    void testFullScreenSupported()
    {
        final AwtDisplay display = display(true,
                                           request(ScreenMode.FULL_SCREEN,
                                                          null,
                                                          ScalingPolicy.FIT,
                                                          Collections.emptyList()));

        assertTrue(display.isSupported(ScreenMode.FULL_SCREEN));
        assertEquals(ScreenMode.FULL_SCREEN, display.getScreenMode());
        assertFalse(display.getResult().degraded());
    }

    /**
     * A resize applies the request again: {@code INTEGER} falls back to {@code FIT} on a small surface, then not.
     */
    @Test
    void testResize()
    {
        final AwtDisplay display = display(false,
                                           request(ScreenMode.WINDOWED,
                                                          null,
                                                          ScalingPolicy.INTEGER,
                                                          Collections.emptyList()));
        assertTrue(display.resize(300, 200));
        assertFalse(display.resize(300, 200));

        assertEquals(ScalingPolicy.FIT, display.getResult().effective().scalingPolicy());
        assertTrue(display.getResult().degraded());

        display.resize(1000, 800);

        assertEquals(ScalingPolicy.INTEGER, display.getResult().effective().scalingPolicy());
        assertFalse(display.getResult().degraded());
        assertEquals(new PresentationGeometry(20, 40, 960, 720, 3.0, 3.0), display.getGeometry());
    }

    /**
     * Without backend, no filter is applied; with one, a missing scaling filter falls back to one it implements, a
     * missing post filter is skipped, both reported; {@code NONE} is no filter.
     */
    @Test
    void testFilters()
    {
        final DisplayRequest request = request(ScreenMode.WINDOWED,
                                               null,
                                               ScalingPolicy.FIT,
                                               List.of(OutputFilter.HQ2X, OutputFilter.CRT));
        final AwtDisplay display = display(false, request);
        assertTrue(display.getResult().effective().filters().isEmpty());
        assertTrue(display.getResult().reason().contains("no render backend"));

        display.attach(new BilinearBackend());

        final EffectiveResult result = display.getResult();
        assertEquals(List.of(OutputFilter.BILINEAR), result.effective().filters());
        assertTrue(result.reason().contains(OutputFilter.CRT.id()));
        assertTrue(result.reason().contains(OutputFilter.HQ2X.id()));

        display.apply(request(ScreenMode.WINDOWED, null, ScalingPolicy.FIT, List.of(OutputFilter.NONE)));
        assertFalse(display.getResult().degraded());
    }

    /**
     * Backend implementing {@code NONE} and bilinear filtering, drawing nothing.
     */
    private static final class BilinearBackend implements RenderBackend
    {
        /**
         * Creates the backend.
         */
        BilinearBackend()
        {
            super();
        }

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
            return OutputFilter.NONE.equals(filter) || OutputFilter.BILINEAR.equals(filter);
        }

        @Override
        public void register(CustomFilter filter)
        {
            // No custom filter in this test.
        }

        @Override
        public void present(CommandBuffer commands)
        {
            // Draws nothing.
        }
    }
}
