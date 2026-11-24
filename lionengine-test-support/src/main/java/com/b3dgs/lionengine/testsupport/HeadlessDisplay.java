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

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.platform.DisplayMode;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.EffectiveResult;
import com.b3dgs.lionengine.platform.PresentationGeometry;
import com.b3dgs.lionengine.render.FrameScaling;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.RenderBackend;

/**
 * Display state of the headless platform: surface, display mode, effective result and presentation geometry
 * (ADR-0006, ADR-0046, REQ-DISPLAY-007, REQ-DISPLAY-008).
 *
 * <p>
 * Every screen mode and display mode is accepted; the scaling comes from the scaling function of {@code render-api};
 * the output filters are those the attached render backend implements, none without a backend. Runtime thread
 * only.
 * </p>
 */
final class HeadlessDisplay
{
    /** Reason of an {@code INTEGER} fallback. */
    private static final String REASON_INTEGER = "INTEGER scaling falls back to FIT: the surface is smaller than the "
                                                 + "logical frame";
    /** Reason of filters not applied. */
    private static final String REASON_FILTERS = "Output filters not applied: no render backend attached";
    /** Reason of a filter skipped by the backend. */
    private static final String REASON_SKIPPED = "Output filter not implemented by the render backend, skipped: ";
    /** Separator of the reasons. */
    private static final String REASON_SEPARATOR = "; ";

    /**
     * Appends a reason to the reasons of an effective result.
     *
     * @param reasons The reasons.
     * @param reason The reason to append.
     */
    private static void addReason(StringBuilder reasons, String reason)
    {
        if (!reasons.isEmpty())
        {
            reasons.append(REASON_SEPARATOR);
        }
        reasons.append(reason);
    }

    /** Logical frame width in pixels. */
    private final int logicalWidth;
    /** Logical frame height in pixels. */
    private final int logicalHeight;
    /** Surface width in device pixels. */
    private int surfaceWidth;
    /** Surface height in device pixels. */
    private int surfaceHeight;
    /** Display mode of the imaginary monitor. */
    private DisplayMode displayMode;
    /** Effective result of the current request. */
    private EffectiveResult effectiveResult;
    /** Current presentation geometry. */
    private PresentationGeometry geometry;
    /** Attached render backend, {@code null} if none. */
    private RenderBackend backend;

    /**
     * Creates the display state; the surface and the display mode have the logical frame size.
     *
     * @param logicalWidth The logical frame width in pixels, positive.
     * @param logicalHeight The logical frame height in pixels, positive.
     * @param request The initial request, applied at once.
     */
    HeadlessDisplay(int logicalWidth, int logicalHeight, DisplayRequest request)
    {
        this.logicalWidth = logicalWidth;
        this.logicalHeight = logicalHeight;
        surfaceWidth = logicalWidth;
        surfaceHeight = logicalHeight;
        displayMode = new DisplayMode(logicalWidth, logicalHeight, DisplayMode.UNKNOWN, DisplayMode.UNKNOWN);
        apply(request);
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
        apply(effectiveResult.requested());
    }

    /**
     * Attaches the render backend and applies the current request again with its filters.
     *
     * @param backend The render backend.
     */
    void attach(RenderBackend backend)
    {
        this.backend = backend;
        apply(effectiveResult.requested());
    }

    /**
     * Applies a request: computes its effective result and the presentation geometry on the current surface.
     *
     * @param request The request.
     */
    void apply(DisplayRequest request)
    {
        final FrameScaling scaling = FrameScaling.of(request.scalingPolicy(),
                                                     logicalWidth,
                                                     logicalHeight,
                                                     surfaceWidth,
                                                     surfaceHeight);
        if (request.displayMode() != null)
        {
            displayMode = request.displayMode();
        }
        final StringBuilder reason = new StringBuilder();
        if (scaling.policy() != request.scalingPolicy())
        {
            reason.append(REASON_INTEGER);
        }
        final List<OutputFilter> filters = filters(request, reason);
        final DisplayRequest effective = new DisplayRequest(request.screenMode(),
                                                            displayMode,
                                                            scaling.policy(),
                                                            filters);
        effectiveResult = new EffectiveResult(request, effective, !reason.isEmpty(), reason.toString());
        geometry = new PresentationGeometry(scaling.x(),
                                            scaling.y(),
                                            scaling.width(),
                                            scaling.height(),
                                            scaling.scaleX(),
                                            scaling.scaleY());
    }

    /**
     * Returns the filters applied for a request: those the backend implements, in order, without {@code NONE}.
     *
     * @param request The request.
     * @param reason The reasons of the effective result, completed with the filters not applied.
     * @return The filters applied.
     */
    private List<OutputFilter> filters(DisplayRequest request, StringBuilder reason)
    {
        final List<OutputFilter> applied = new ArrayList<>();
        boolean missing = false;
        for (final OutputFilter filter : request.filters())
        {
            if (!OutputFilter.NONE.equals(filter))
            {
                if (backend != null && backend.isSupported(filter))
                {
                    applied.add(filter);
                }
                else if (backend != null)
                {
                    addReason(reason, REASON_SKIPPED + filter.id());
                }
                else
                {
                    missing = true;
                }
            }
        }
        if (missing)
        {
            addReason(reason, REASON_FILTERS);
        }
        return applied;
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
    EffectiveResult getEffectiveResult()
    {
        return effectiveResult;
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
