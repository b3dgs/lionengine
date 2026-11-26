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

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.util.EnumSet;
import java.util.Set;

import com.b3dgs.lionengine.Inbox;
import com.b3dgs.lionengine.platform.DisplayListener;
import com.b3dgs.lionengine.platform.DisplayMode;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.DisplayState;
import com.b3dgs.lionengine.platform.EffectiveResult;
import com.b3dgs.lionengine.platform.PresentationGeometry;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.render.RenderBackend;

/**
 * Display state of the AWT platform: surface, screen modes, display mode, effective result and presentation geometry
 * (ADR-0006, ADR-0022, ADR-0043, ADR-0046, ADR-0060, REQ-DISPLAY-001, REQ-DISPLAY-002, REQ-DISPLAY-007,
 * REQ-DISPLAY-008).
 *
 * <p>
 * {@code WINDOWED} and {@code FULL_WINDOW} are supported, and {@code FULL_SCREEN} when the default monitor supports
 * exclusive full screen; the platform realizes the effective screen mode on its window. Until display-mode batch 2,
 * the display mode is the current mode of the default monitor; another one is reported as degraded. The screen-mode
 * fallback, the scaling and the output filters are resolved by the shared {@link DisplayState}. Runtime thread only.
 * </p>
 */
final class AwtDisplay
{
    /**
     * Creates the display state of the default monitor and applies the initial request.
     *
     * @param logicalWidth The logical frame width in pixels.
     * @param logicalHeight The logical frame height in pixels.
     * @param request The initial request.
     * @return The display state.
     */
    static AwtDisplay create(int logicalWidth, int logicalHeight, DisplayRequest request)
    {
        final GraphicsDevice device = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        return new AwtDisplay(logicalWidth,
                              logicalHeight,
                              convert(device.getDisplayMode()),
                              device.isFullScreenSupported(),
                              request);
    }

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
     * @param fullScreen {@code true} if the default monitor supports exclusive full screen.
     * @param request The initial request.
     */
    AwtDisplay(int logicalWidth,
               int logicalHeight,
               DisplayMode displayMode,
               boolean fullScreen,
               DisplayRequest request)
    {
        state = new DisplayState(logicalWidth, logicalHeight);
        final Set<ScreenMode> modes = EnumSet.of(ScreenMode.WINDOWED, ScreenMode.FULL_WINDOW);
        if (fullScreen)
        {
            modes.add(ScreenMode.FULL_SCREEN);
        }
        state.setSupported(modes);
        this.displayMode = displayMode;
        apply(request);
    }

    /**
     * Applies a request: the screen mode with its fallback, the current display mode of the monitor.
     *
     * @param request The request.
     */
    void apply(DisplayRequest request)
    {
        String reasons = "";
        if (request.displayMode() != null && !request.displayMode().equals(displayMode))
        {
            reasons = "Display mode change not implemented yet, the current mode is kept";
        }
        state.resolve(request, displayMode, reasons);
    }

    /**
     * Applies a new surface size and resolves the request again.
     *
     * @param width The surface width in device pixels.
     * @param height The surface height in device pixels.
     * @return {@code true} if the effective result changed, to be announced to the display listeners.
     */
    boolean resize(int width, int height)
    {
        final EffectiveResult previous = state.getResult();
        state.setSurface(width, height);
        apply(previous.requested());
        return !previous.equals(state.getResult());
    }

    /**
     * Tells whether a screen mode is supported.
     *
     * @param mode The screen mode.
     * @return {@code true} if supported.
     */
    boolean isSupported(ScreenMode mode)
    {
        return state.isSupported(mode);
    }

    /**
     * Returns the effective screen mode.
     *
     * @return The screen mode of the last result, to realize on the window.
     */
    ScreenMode getScreenMode()
    {
        return state.getResult().effective().screenMode();
    }

    /**
     * Adds a display listener.
     *
     * @param listener The listener.
     */
    void addListener(DisplayListener listener)
    {
        state.addListener(listener);
    }

    /**
     * Removes a display listener.
     *
     * @param listener The listener.
     */
    void removeListener(DisplayListener listener)
    {
        state.removeListener(listener);
    }

    /**
     * Posts the current result to the display listeners through the inbox.
     *
     * @param inbox The inbox of the loop.
     */
    void post(Inbox inbox)
    {
        state.post(inbox);
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
