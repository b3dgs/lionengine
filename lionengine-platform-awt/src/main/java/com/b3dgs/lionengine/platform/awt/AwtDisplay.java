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
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
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
 * Display state of the AWT platform: surface, screen modes, display modes, effective result and presentation geometry
 * (ADR-0006, ADR-0022, ADR-0043, ADR-0046, ADR-0060, ADR-0061, REQ-DISPLAY-001, REQ-DISPLAY-002, REQ-DISPLAY-003,
 * REQ-DISPLAY-007, REQ-DISPLAY-008).
 *
 * <p>
 * {@code WINDOWED} and {@code FULL_WINDOW} are supported, and {@code FULL_SCREEN} when the default monitor supports
 * exclusive full screen; the platform realizes the effective screen mode on its window. The display modes are those of
 * the default monitor. A display mode is changed only in {@code FULL_SCREEN}: the target is the offered mode nearest to
 * the request, or the current one when the request has none; in the other screen modes the target is the desktop mode,
 * the mode of the monitor when the platform was created. The window realizes the target and gives back the mode in
 * effect, which the result reports, with the reason when it differs from the request. The screen-mode fallback, the
 * display-mode selection, the scaling and the output filters are resolved by the shared {@link DisplayState}. Runtime
 * thread only.
 * </p>
 */
final class AwtDisplay
{
    /** Reason of a display mode requested outside {@code FULL_SCREEN}. */
    private static final String REASON_WINDOWED = "Display mode change needs FULL_SCREEN, the desktop mode is kept";
    /** Reason of a display mode change refused by the system. */
    private static final String REASON_REFUSED = "Display mode change refused by the system, the current mode is kept";

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
        final DisplayMode desktop = convert(device.getDisplayMode());
        final List<DisplayMode> modes = new ArrayList<>();
        for (final java.awt.DisplayMode mode : device.getDisplayModes())
        {
            modes.add(convert(mode));
        }
        if (modes.isEmpty())
        {
            modes.add(desktop);
        }
        return new AwtDisplay(logicalWidth, logicalHeight, modes, desktop, device.isFullScreenSupported(), request);
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
    /** Display mode of the default monitor when the platform was created. */
    private final DisplayMode desktop;
    /** Display mode in effect on the default monitor. */
    private DisplayMode current;
    /** Last applied request. */
    private DisplayRequest request;
    /** {@code true} once the window realized the last request. */
    private boolean realized;

    /**
     * Creates the display state and applies the initial request.
     *
     * @param logicalWidth The logical frame width in pixels.
     * @param logicalHeight The logical frame height in pixels.
     * @param modes The display modes of the default monitor, not empty.
     * @param desktop The current display mode of the default monitor.
     * @param fullScreen {@code true} if the default monitor supports exclusive full screen.
     * @param request The initial request.
     */
    AwtDisplay(int logicalWidth,
               int logicalHeight,
               List<DisplayMode> modes,
               DisplayMode desktop,
               boolean fullScreen,
               DisplayRequest request)
    {
        state = new DisplayState(logicalWidth, logicalHeight);
        final Set<ScreenMode> screenModes = EnumSet.of(ScreenMode.WINDOWED, ScreenMode.FULL_WINDOW);
        if (fullScreen)
        {
            screenModes.add(ScreenMode.FULL_SCREEN);
        }
        state.setSupported(screenModes);
        state.setDisplayModes(modes);
        this.desktop = desktop;
        current = desktop;
        apply(request);
    }

    /**
     * Applies a request, before the window realizes it: the result reports the display mode in effect until then.
     *
     * @param request The request.
     */
    void apply(DisplayRequest request)
    {
        this.request = request;
        realized = false;
        resolve();
    }

    /**
     * Sets the display mode in effect once the window realized the last request, and resolves it again.
     *
     * @param mode The display mode of the default monitor.
     */
    void setCurrent(DisplayMode mode)
    {
        current = mode;
        realized = true;
        resolve();
    }

    /**
     * Returns the display mode the window must realize for the last request.
     *
     * @return The offered mode nearest to the request in {@code FULL_SCREEN} (the current one if the request has
     *         none), the desktop mode otherwise.
     */
    DisplayMode getTarget()
    {
        if (getScreenMode() == ScreenMode.FULL_SCREEN)
        {
            return state.selectDisplayMode(request.displayMode(), current);
        }
        return desktop;
    }

    /**
     * Resolves the last request with the display mode in effect, and the reason when it was not realized.
     */
    private void resolve()
    {
        String reasons = "";
        final DisplayMode requested = request.displayMode();
        if (requested != null && !DisplayState.matches(requested, current))
        {
            if (getScreenMode() != ScreenMode.FULL_SCREEN)
            {
                reasons = REASON_WINDOWED;
            }
            else if (realized && !current.equals(getTarget()))
            {
                reasons = REASON_REFUSED;
            }
        }
        state.resolve(request, current, reasons);
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
        resolve();
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
     * Returns the display modes of the default monitor.
     *
     * @return The display modes, unmodifiable.
     */
    List<DisplayMode> getDisplayModes()
    {
        return state.getDisplayModes();
    }

    /**
     * Returns the effective screen mode of the last request.
     *
     * @return The screen mode, to realize on the window.
     */
    ScreenMode getScreenMode()
    {
        return state.selectScreenMode(request.screenMode());
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
        resolve();
    }

    /**
     * Returns the display mode in effect.
     *
     * @return The display mode of the default monitor.
     */
    DisplayMode getDisplayMode()
    {
        return current;
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
