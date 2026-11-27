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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.b3dgs.lionengine.render.FilterKind;
import com.b3dgs.lionengine.render.OutputFilter;
import com.b3dgs.lionengine.render.ScalingPolicy;

/**
 * Display settings requested from the platform, or applied by it (ADR-0002, ADR-0006, ADR-0046, REQ-DISPLAY-008).
 *
 * <p>
 * The platform owns the display state: screen mode, display mode, scaling policy and the ordered output filter chain,
 * from the logical frame to the output surface. A request is best effort; what was actually applied is reported as an
 * {@link EffectiveResult}. Immutable and thread-safe.
 * </p>
 *
 * @param screenMode The screen mode.
 * @param displayMode The display mode, {@code null} to keep the current one.
 * @param scalingPolicy The scaling policy.
 * @param filters The ordered output filter chain, unmodifiable.
 * @since 11.0.0
 */
public record DisplayRequest(ScreenMode screenMode,
                             DisplayMode displayMode,
                             ScalingPolicy scalingPolicy,
                             List<OutputFilter> filters)
{
    /** Error message of an invalid request. */
    private static final String ERROR_REQUEST = "Invalid display request";
    /** Error message of a chain of another shape. */
    private static final String ERROR_CHAIN = "Invalid filter chain, at most one scaling filter before the post "
                                              + "filters and no filter twice: ";

    /**
     * Creates a request.
     *
     * @param screenMode The screen mode (never {@code null}).
     * @param displayMode The display mode, {@code null} to keep the current one.
     * @param scalingPolicy The scaling policy (never {@code null}).
     * @param filters The ordered output filter chain, copied (never {@code null}, no {@code null} element; empty for
     *            {@link OutputFilter#NONE} alone): at most one scaling filter, before every post filter, and no filter
     *            twice (ADR-0062); without a scaling filter the frame is scaled with {@link OutputFilter#NONE}.
     * @throws IllegalArgumentException If a non-nullable argument is {@code null}, a filter is {@code null}, or the
     *             chain has another shape.
     */
    public DisplayRequest(ScreenMode screenMode,
                          DisplayMode displayMode,
                          ScalingPolicy scalingPolicy,
                          List<OutputFilter> filters)
    {
        if (screenMode == null || scalingPolicy == null || filters == null)
        {
            throw new IllegalArgumentException(ERROR_REQUEST);
        }
        final List<OutputFilter> copy = new ArrayList<>(filters.size());
        final Set<OutputFilter> seen = new HashSet<>();
        boolean scaling = false;
        boolean post = false;
        for (final OutputFilter filter : filters)
        {
            if (filter == null)
            {
                throw new IllegalArgumentException(ERROR_REQUEST);
            }
            if (filter.kind() == FilterKind.SCALING && (scaling || post) || !seen.add(filter))
            {
                throw new IllegalArgumentException(ERROR_CHAIN + filters);
            }
            scaling |= filter.kind() == FilterKind.SCALING;
            post |= filter.kind() == FilterKind.POST;
            copy.add(filter);
        }
        this.screenMode = screenMode;
        this.displayMode = displayMode;
        this.scalingPolicy = scalingPolicy;
        this.filters = Collections.unmodifiableList(copy);
    }
}
