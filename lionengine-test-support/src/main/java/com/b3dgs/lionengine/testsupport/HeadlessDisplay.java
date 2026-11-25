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
package com.b3dgs.lionengine.testsupport;

import com.b3dgs.lionengine.platform.DisplayMode;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.DisplayState;
import com.b3dgs.lionengine.platform.EffectiveResult;
import com.b3dgs.lionengine.platform.PresentationGeometry;
import com.b3dgs.lionengine.render.RenderBackend;

/**
 * Display state of the headless platform: surface, display mode, effective result and presentation geometry
 * (ADR-0006, ADR-0046, REQ-DISPLAY-007, REQ-DISPLAY-008).
 *
 * <p>
 * Every screen mode and display mode is accepted; the scaling and the output filters are resolved by the shared
 * {@link DisplayState}: the filters are those the attached render backend implements, none without a backend.
 * Runtime thread only.
 * </p>
 */
final class HeadlessDisplay
{
    /** Shared display state. */
    private final DisplayState state;
    /** Display mode of the imaginary monitor. */
    private DisplayMode displayMode;

    /**
     * Creates the display state and applies the initial request.
     *
     * @param logicalWidth The logical frame width in pixels.
     * @param logicalHeight The logical frame height in pixels.
     * @param request The initial request.
     */
    HeadlessDisplay(int logicalWidth, int logicalHeight, DisplayRequest request)
    {
        state = new DisplayState(logicalWidth, logicalHeight);
        displayMode = new DisplayMode(logicalWidth, logicalHeight, DisplayMode.UNKNOWN, DisplayMode.UNKNOWN);
        apply(request);
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
     * Applies a request: the screen mode and the display mode are accepted as requested.
     *
     * @param request The request.
     */
    void apply(DisplayRequest request)
    {
        if (request.displayMode() != null)
        {
            displayMode = request.displayMode();
        }
        state.resolve(request, request.screenMode(), displayMode, "");
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
    EffectiveResult getEffectiveResult()
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
