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

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.render.FrameScaling;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.RenderBackend;

/**
 * Display state shared by the platform implementations: the surface size, the attached render backend, and the
 * resolution of a request into its effective result and presentation geometry (ADR-0006, ADR-0046,
 * REQ-DISPLAY-004, REQ-DISPLAY-007, REQ-DISPLAY-008, REQ-FILTER-002).
 *
 * <p>
 * A platform decides what it can do with the screen mode and the display mode, then calls
 * {@link #resolve(DisplayRequest, ScreenMode, DisplayMode, String)} with what it applies and the reasons it already
 * has. This class adds the scaling fallback ({@code INTEGER} to {@code FIT}, ADR-0006) and the output filters: the
 * effective filters are those the attached backend supports, in the order requested, without {@code NONE}; the others
 * are skipped and explained. The scaling is computed on the current surface size and never changes the logical
 * frame.
 * </p>
 *
 * <p>
 * Runtime thread only. Not thread-safe. Contains no frontend type, so it holds for every platform.
 * </p>
 *
 * @since 11.0.0
 */
public final class DisplayState
{
    /** Separator of the reasons. */
    private static final String SEPARATOR = "; ";
    /** Separator before the first reason: none. */
    private static final String NO_SEPARATOR = "";
    /** Reason of the scaling fallback. */
    private static final String REASON_INTEGER = "INTEGER scaling falls back to FIT: the surface is smaller than the "
                                                 + "logical frame";
    /** Reason of the filters requested without backend. */
    private static final String REASON_NO_BACKEND = "Output filters not applied: no render backend attached";
    /** Reason of a filter the backend does not implement, followed by its identifier. */
    private static final String REASON_SKIPPED = "Output filter not implemented by the render backend, skipped: ";

    /** Logical frame width in pixels. */
    private final int logicalWidth;
    /** Logical frame height in pixels. */
    private final int logicalHeight;
    /** Surface width in device pixels. */
    private int surfaceWidth;
    /** Surface height in device pixels. */
    private int surfaceHeight;
    /** Attached render backend, {@code null} until attached. */
    private RenderBackend backend;
    /** Last result, {@code null} until the first resolution. */
    private EffectiveResult result;
    /** Last presentation geometry, {@code null} until the first resolution. */
    private PresentationGeometry geometry;
    /**
     * Separator written before the next reason of the current resolution: {@link #NO_SEPARATOR} while no reason is
     * written, {@link #SEPARATOR} after. Tracked instead of testing the builder for emptiness, because
     * {@code CharSequence.isEmpty()} is not available on Android API 21 (ADR-0044).
     */
    private String separator;

    /**
     * Creates a state whose surface has the logical frame size. Nothing is resolved yet.
     *
     * @param logicalWidth The logical frame width in pixels (positive).
     * @param logicalHeight The logical frame height in pixels (positive).
     * @throws IllegalArgumentException If a size is not positive.
     */
    public DisplayState(int logicalWidth, int logicalHeight)
    {
        if (logicalWidth <= 0 || logicalHeight <= 0)
        {
            throw new IllegalArgumentException("Invalid logical frame: " + logicalWidth + 'x' + logicalHeight);
        }
        this.logicalWidth = logicalWidth;
        this.logicalHeight = logicalHeight;
        surfaceWidth = logicalWidth;
        surfaceHeight = logicalHeight;
    }

    /**
     * Sets the surface size; the platform then resolves its request again.
     *
     * @param width The surface width in device pixels, not negative.
     * @param height The surface height in device pixels, not negative.
     */
    public void setSurface(int width, int height)
    {
        surfaceWidth = width;
        surfaceHeight = height;
    }

    /**
     * Sets the render backend that implements the filters; the platform then resolves its request again.
     *
     * @param backend The backend (never {@code null}).
     */
    public void setBackend(RenderBackend backend)
    {
        this.backend = backend;
    }

    /**
     * Resolves a request into the result and the geometry, read with their getters.
     *
     * @param request The request (never {@code null}).
     * @param screenMode The screen mode the platform applies (never {@code null}).
     * @param displayMode The display mode the platform applies, {@code null} if unknown.
     * @param reasons The reasons already found by the platform, joined by {@code "; "}, empty if none (never
     *            {@code null}).
     */
    public void resolve(DisplayRequest request, ScreenMode screenMode, DisplayMode displayMode, String reasons)
    {
        final FrameScaling scaling = FrameScaling.of(request.scalingPolicy(),
                                                     logicalWidth,
                                                     logicalHeight,
                                                     surfaceWidth,
                                                     surfaceHeight);
        final StringBuilder reason = new StringBuilder(reasons);
        if (reasons.isEmpty())
        {
            separator = NO_SEPARATOR;
        }
        else
        {
            separator = SEPARATOR;
        }
        if (scaling.policy() != request.scalingPolicy())
        {
            addReason(reason, REASON_INTEGER);
        }
        final List<OutputFilter> filters = selectFilters(request.filters(), reason);
        final DisplayRequest effective = new DisplayRequest(screenMode, displayMode, scaling.policy(), filters);
        final String text = reason.toString();
        result = new EffectiveResult(request, effective, !text.isEmpty(), text);
        geometry = new PresentationGeometry(scaling.x(),
                                            scaling.y(),
                                            scaling.width(),
                                            scaling.height(),
                                            scaling.scaleX(),
                                            scaling.scaleY());
    }

    /**
     * Appends a reason after the previous ones of the current resolution.
     *
     * @param reasons The reasons so far.
     * @param reason The reason to add (not empty).
     */
    private void addReason(StringBuilder reasons, String reason)
    {
        reasons.append(separator).append(reason);
        separator = SEPARATOR;
    }

    /**
     * Keeps the filters the backend implements.
     *
     * @param requested The requested filters.
     * @param reason The reasons, completed with each skipped filter.
     * @return The effective filters, without {@code NONE}.
     */
    private List<OutputFilter> selectFilters(List<OutputFilter> requested, StringBuilder reason)
    {
        final List<OutputFilter> selected = new ArrayList<>(requested.size());
        boolean missing = false;
        for (final OutputFilter filter : requested)
        {
            if (!OutputFilter.NONE.equals(filter))
            {
                if (backend == null)
                {
                    missing = true;
                }
                else if (backend.isSupported(filter))
                {
                    selected.add(filter);
                }
                else
                {
                    addReason(reason, REASON_SKIPPED + filter.id());
                }
            }
        }
        if (missing)
        {
            addReason(reason, REASON_NO_BACKEND);
        }
        return selected;
    }

    /**
     * Returns the surface width.
     *
     * @return The surface width in device pixels.
     */
    public int getSurfaceWidth()
    {
        return surfaceWidth;
    }

    /**
     * Returns the surface height.
     *
     * @return The surface height in device pixels.
     */
    public int getSurfaceHeight()
    {
        return surfaceHeight;
    }

    /**
     * Returns the result of the last resolution.
     *
     * @return The result, {@code null} before the first resolution.
     */
    public EffectiveResult getResult()
    {
        return result;
    }

    /**
     * Returns the presentation geometry of the last resolution.
     *
     * @return The geometry, {@code null} before the first resolution.
     */
    public PresentationGeometry getGeometry()
    {
        return geometry;
    }
}
