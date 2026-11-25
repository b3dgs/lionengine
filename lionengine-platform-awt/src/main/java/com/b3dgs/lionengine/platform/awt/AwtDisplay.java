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

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.platform.DisplayMode;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.EffectiveResult;
import com.b3dgs.lionengine.platform.PresentationGeometry;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.render.FrameScaling;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.RenderBackend;

/**
 * Display state of the AWT platform: surface, display mode, effective result and presentation geometry (ADR-0006,
 * ADR-0022, ADR-0043, ADR-0046, REQ-DISPLAY-007, REQ-DISPLAY-008).
 *
 * <p>
 * Until the display-mode batches: the window is always {@link ScreenMode#WINDOWED}, and the display mode is the
 * current mode of the default monitor; another request is reported as degraded. The scaling comes from the scaling
 * function of {@code render-api}; the output filters are those the attached render backend implements. Runtime thread
 * only.
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

    /** Logical frame width in pixels. */
    private final int logicalWidth;
    /** Logical frame height in pixels. */
    private final int logicalHeight;
    /** Display mode of the default monitor. */
    private final DisplayMode displayMode;
    /** Surface width in device pixels. */
    private int surfaceWidth;
    /** Surface height in device pixels. */
    private int surfaceHeight;
    /** Attached render backend, {@code null} if none. */
    private RenderBackend backend;
    /** Effective result of the current request. */
    private EffectiveResult result;
    /** Current presentation geometry. */
    private PresentationGeometry geometry;

    /**
     * Creates the display state, the surface having the logical frame size, and applies the initial request.
     *
     * @param logicalWidth The logical frame width in pixels.
     * @param logicalHeight The logical frame height in pixels.
     * @param displayMode The display mode of the default monitor.
     * @param request The initial request.
     */
    AwtDisplay(int logicalWidth, int logicalHeight, DisplayMode displayMode, DisplayRequest request)
    {
        this.logicalWidth = logicalWidth;
        this.logicalHeight = logicalHeight;
        this.displayMode = displayMode;
        surfaceWidth = logicalWidth;
        surfaceHeight = logicalHeight;
        apply(request);
    }

    /**
     * Applies a request on the current surface: effective result and presentation geometry.
     *
     * @param request The request.
     */
    void apply(DisplayRequest request)
    {
        final List<String> reasons = new ArrayList<>();
        if (request.screenMode() != ScreenMode.WINDOWED)
        {
            reasons.add("Screen mode not implemented yet, WINDOWED used instead of " + request.screenMode());
        }
        if (request.displayMode() != null && !request.displayMode().equals(displayMode))
        {
            reasons.add("Display mode change not implemented yet, the current mode is kept");
        }
        final FrameScaling scaling = FrameScaling.of(request.scalingPolicy(),
                                                     logicalWidth,
                                                     logicalHeight,
                                                     surfaceWidth,
                                                     surfaceHeight);
        if (scaling.policy() != request.scalingPolicy())
        {
            reasons.add("INTEGER scaling falls back to FIT: the surface is smaller than the logical frame");
        }
        final List<OutputFilter> filters = selectFilters(request.filters(), reasons);
        final DisplayRequest effective = new DisplayRequest(ScreenMode.WINDOWED,
                                                            displayMode,
                                                            scaling.policy(),
                                                            filters);
        result = new EffectiveResult(request, effective, !reasons.isEmpty(), String.join(SEPARATOR, reasons));
        geometry = new PresentationGeometry(scaling.x(),
                                            scaling.y(),
                                            scaling.width(),
                                            scaling.height(),
                                            scaling.scaleX(),
                                            scaling.scaleY());
    }

    /**
     * Selects the filters the backend implements, in order, without {@link OutputFilter#NONE}.
     *
     * @param requested The requested filters.
     * @param reasons The reasons, completed with the filters not applied.
     * @return The filters applied.
     */
    private List<OutputFilter> selectFilters(List<OutputFilter> requested, List<String> reasons)
    {
        final List<OutputFilter> selected = new ArrayList<>(requested.size());
        for (final OutputFilter filter : requested)
        {
            if (backend == null)
            {
                if (!OutputFilter.NONE.equals(filter))
                {
                    reasons.add("Output filters not applied: no render backend attached");
                    break;
                }
            }
            else if (!backend.isSupported(filter))
            {
                reasons.add("Output filter not implemented by the render backend, skipped: " + filter.id());
            }
            else if (!OutputFilter.NONE.equals(filter))
            {
                selected.add(filter);
            }
        }
        return selected;
    }

    /**
     * Changes the surface size and applies the current request again.
     *
     * @param width The surface width in device pixels.
     * @param height The surface height in device pixels.
     */
    void resize(int width, int height)
    {
        surfaceWidth = width;
        surfaceHeight = height;
        apply(result.requested());
    }

    /**
     * Attaches the render backend and applies the current request again with its filters.
     *
     * @param backend The render backend.
     */
    void attach(RenderBackend backend)
    {
        this.backend = backend;
        apply(result.requested());
    }

    /**
     * Returns the display mode of the default monitor.
     *
     * @return The display mode.
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
        return surfaceWidth;
    }

    /**
     * Returns the surface height.
     *
     * @return The surface height in device pixels.
     */
    int getSurfaceHeight()
    {
        return surfaceHeight;
    }

    /**
     * Returns the effective result of the current request.
     *
     * @return The effective result.
     */
    EffectiveResult getResult()
    {
        return result;
    }

    /**
     * Returns the current presentation geometry.
     *
     * @return The geometry.
     */
    PresentationGeometry getGeometry()
    {
        return geometry;
    }
}
