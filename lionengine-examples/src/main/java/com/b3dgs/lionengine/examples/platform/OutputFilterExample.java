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
package com.b3dgs.lionengine.examples.platform;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.platform.DisplayListener;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.EffectiveResult;
import com.b3dgs.lionengine.platform.Platform;
import com.b3dgs.lionengine.render.OutputFilter;

/**
 * Requests output filters for the presented frame and reads which ones the render backend applies (REQ-FILTER-001,
 * REQ-FILTER-002, REQ-DISPLAY-008, ADR-0006, ADR-0062).
 *
 * <p>
 * A chain holds at most one scaling filter ({@link OutputFilter#NONE}, {@link OutputFilter#BILINEAR},
 * {@link OutputFilter#HQ2X}), then post filters such as {@link OutputFilter#CRT}. A request never fails for a missing
 * filter: a scaling filter falls back ({@code HQ2X} to bilinear, then to {@code NONE}), a post filter is skipped, and
 * the effective result tells what was applied and why. The screen mode, display mode and scaling of the current
 * request are kept.
 * </p>
 *
 * @since 11.0.0
 */
public final class OutputFilterExample
{
    /**
     * Private constructor.
     */
    private OutputFilterExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Requests the {@code HQ2X} scaler followed by the CRT effect, keeping the other settings, and observes the
     * outcome.
     *
     * @param platform The started platform, with its render backend attached.
     * @param listener The listener told of the effective result once the request is applied.
     * @return The accepted request.
     */
    public static DisplayRequest requestRetroLook(Platform platform, DisplayListener listener)
    {
        platform.addDisplayListener(listener);
        final DisplayRequest current = platform.getEffectiveResult().requested();
        return platform.apply(new DisplayRequest(current.screenMode(),
                                                 current.displayMode(),
                                                 current.scalingPolicy(),
                                                 List.of(OutputFilter.HQ2X, OutputFilter.CRT)));
    }

    /**
     * Lists the identifiers of the filters applied, for a settings menu; {@code NONE} is implied when no scaling filter
     * is listed.
     *
     * @param result The effective result.
     * @return The identifiers, in the chain order.
     */
    public static List<String> applied(EffectiveResult result)
    {
        final List<String> ids = new ArrayList<>();
        for (final OutputFilter filter : result.effective().filters())
        {
            ids.add(filter.id());
        }
        return ids;
    }
}
