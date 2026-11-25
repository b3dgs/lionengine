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

import com.b3dgs.lionengine.platform.DisplayMode;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.DisplayState;
import com.b3dgs.lionengine.platform.EffectiveResult;
import com.b3dgs.lionengine.platform.PresentationGeometry;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.render.RenderBackend;

/**
 * Display state of the AWT platform: surface, display mode, effective result and presentation geometry (ADR-0006,
 * ADR-0022, ADR-0043, ADR-0046, REQ-DISPLAY-007, REQ-DISPLAY-008).
 *
 * <p>
 * Until the display-mode batches: the window is always {@link ScreenMode#WINDOWED}, and the display mode is the
 * current mode of the default monitor; another request is reported as degraded. The scaling and the output filters
 * are resolved by the shared {@link DisplayState}. Runtime thread only.
 * </p>
 */
final class AwtDisplay
{
    /** Separator of the reasons. */
    private static final String SEPARATOR = "; ";

    /**
     * Converts an AWT display mode: sizes in device pixels, an unknown refresh rate ({@code 0}) and a multiple bit
     * depth ({@code -1}) become {@link DisplayMode#UNKNOWN} (ADR-0022).
     *
     * @param mode The AWT display mode.
     * @return The display mode.
     */
    static DisplayMode convert(java.awt.DisplayMode mode)
    {
        return new DisplayMode(mode.getWidth(),
                               mode.getHeight(),
                               Math.max(DisplayMode.UNKNOWN, mode.getRefreshRate()),
                               Math.max(DisplayMode.UNKNOWN, mode.getBitDepth()));
    }

    /** Shared display state. */
    private final DisplayState state;
    /** Current display mode of the default monitor. */
    private final DisplayMode displayMode;

    /**
     * Creates the display state and applies the initial request.
     *
     * @param logicalWidth The logical frame width in pixels.
     * @param logicalHeight The logical frame height in pixels.
     * @param displayMode The current display mode of the default monitor.
     * @param request The initial request.
     */
    AwtDisplay(int logicalWidth, int logicalHeight, DisplayMode displayMode, DisplayRequest request)
    {
        state = new DisplayState(logicalWidth, logicalHeight);
        this.displayMode = displayMode;
        apply(request);
    }

    /**
     * Applies a request: the window stays windowed on the current display mode, whatever is requested.
     *
     * @param request The request.
     */
    void apply(DisplayRequest request)
    {
        final StringBuilder reasons = new StringBuilder();
        if (request.screenMode() != ScreenMode.WINDOWED)
        {
            reasons.append("Screen mode not implemented yet, WINDOWED used instead of ").append(request.screenMode());
        }
        if (request.displayMode() != null && !request.displayMode().equals(displayMode))
        {
            if (!reasons.isEmpty())
            {
                reasons.append(SEPARATOR);
            }
            reasons.append("Display mode change not implemented yet, the current mode is kept");
        }
        state.resolve(request, ScreenMode.WINDOWED, displayMode, reasons.toString());
    }

    /**
     * Sets the surface size and applies the requested display again.
     *
     * @param width The surface width in device pixels.
     * @param height The surface height in device pixels.
     */
    void resize(int width, int height)
    {
        state.setSurface(width, height);
        apply(state.getResult().requested());
    }

    /**
     * Attaches the render backend and applies the requested display again.
     *
     * @param backend The render backend.
     */
    void attach(RenderBackend backend)
    {
        state.setBackend(backend);
        apply(state.getResult().requested());
    }

    /**
     * Returns the display mode.
     *
     * @return The current display mode of the default monitor.
     */
    DisplayMode getDisplayMode()
    {
        return displayMode;
    }

    /**
     * Returns the surface width.
     *
     * @return The surface width in device pixels.
     */
    int getSurfaceWidth()
    {
        return state.getSurfaceWidth();
    }

    /**
     * Returns the surface height.
     *
     * @return The surface height in device pixels.
     */
    int getSurfaceHeight()
    {
        return state.getSurfaceHeight();
    }

    /**
     * Returns the effective result of the last request.
     *
     * @return The result.
     */
    EffectiveResult getResult()
    {
        return state.getResult();
    }

    /**
     * Returns the presentation geometry.
     *
     * @return The geometry.
     */
    PresentationGeometry getGeometry()
    {
        return state.getGeometry();
    }
}
