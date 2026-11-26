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
        final AwtDisplay display = new AwtDisplay(WIDTH, HEIGHT, MODE, false, request);

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
     * Without exclusive full screen, {@code FULL_SCREEN} falls back to {@code FULL_WINDOW}; display mode changes are
     * not implemented yet: the current mode is kept; both are reported (ADR-0060).
     */
    @Test
    void testFallbackAndDisplayMode()
    {
        final DisplayMode other = new DisplayMode(640, 480, 60, 32);
        final AwtDisplay display = new AwtDisplay(WIDTH,
                                                  HEIGHT,
                                                  MODE,
                                                  false,
                                                  request(ScreenMode.FULL_SCREEN,
                                                          other,
                                                          ScalingPolicy.STRETCH,
                                                          Collections.emptyList()));

        final EffectiveResult result = display.getResult();
        assertEquals(ScreenMode.FULL_WINDOW, result.effective().screenMode());
        assertEquals(ScreenMode.FULL_WINDOW, display.getScreenMode());
        assertEquals(MODE, result.effective().displayMode());
        assertEquals(ScalingPolicy.STRETCH, result.effective().scalingPolicy());
        assertTrue(result.degraded());
        assertEquals("FULL_SCREEN not supported, FULL_WINDOW used; "
                     + "Display mode change not implemented yet, the current mode is kept",
                     result.reason());
        assertTrue(display.isSupported(ScreenMode.WINDOWED));
        assertTrue(display.isSupported(ScreenMode.FULL_WINDOW));
        assertFalse(display.isSupported(ScreenMode.FULL_SCREEN));
    }

    /**
     * With exclusive full screen, the three screen modes are supported and applied as requested.
     */
    @Test
    void testFullScreenSupported()
    {
        final AwtDisplay display = new AwtDisplay(WIDTH,
                                                  HEIGHT,
                                                  MODE,
                                                  true,
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
        final AwtDisplay display = new AwtDisplay(WIDTH,
                                                  HEIGHT,
                                                  MODE,
                                                  false,
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
     * Without backend, no filter is applied; with one, those it implements are applied in order and the others are
     * reported; {@code NONE} is no filter.
     */
    @Test
    void testFilters()
    {
        final DisplayRequest request = request(ScreenMode.WINDOWED,
                                               null,
                                               ScalingPolicy.FIT,
                                               List.of(OutputFilter.NONE,
                                                       OutputFilter.CRT,
                                                       OutputFilter.BILINEAR,
                                                       OutputFilter.HQ2X));
        final AwtDisplay display = new AwtDisplay(WIDTH, HEIGHT, MODE, false, request);
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
        public void present(CommandBuffer commands)
        {
            // Draws nothing.
        }
    }
}
