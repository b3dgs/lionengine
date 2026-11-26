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
import java.util.Collections;
import java.util.List;

import com.b3dgs.lionengine.platform.DisplayListener;
import com.b3dgs.lionengine.platform.DisplayMode;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.Platform;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.render.ScalingPolicy;

/**
 * Lists the display modes of the monitor for a settings menu, then requests one in full screen and reads what the
 * platform applied (REQ-DISPLAY-003, REQ-DISPLAY-008, ADR-0006, ADR-0061).
 *
 * <p>
 * {@link Platform#getDisplayModes()} gives the modes of the default monitor: size in pixels, refresh rate in Hz and
 * color depth in bits, {@code 0} when the system does not know it. A display mode changes the monitor only in
 * {@code FULL_SCREEN} on the desktop. A request never fails: a mode that is not listed is replaced by the nearest one
 * (size, then refresh rate, then depth), and the effective result says which mode is in effect and why it differs.
 * </p>
 *
 * @since 11.0.0
 */
public final class DisplayModeExample
{
    /**
     * Private constructor.
     */
    private DisplayModeExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Describes the display modes of the monitor, for a settings menu.
     *
     * @param platform The platform.
     * @return One line per mode, in the order of the platform, such as {@code "1920x1080 60 Hz 32 bits"}; an unknown
     *         refresh rate or depth is left out.
     */
    public static List<String> describe(Platform platform)
    {
        final List<String> lines = new ArrayList<>();
        for (final DisplayMode mode : platform.getDisplayModes())
        {
            final StringBuilder line = new StringBuilder().append(mode.width()).append('x').append(mode.height());
            if (mode.isRefreshRateKnown())
            {
                line.append(' ').append(mode.refreshRate()).append(" Hz");
            }
            if (mode.isBitDepthKnown())
            {
                line.append(' ').append(mode.bitDepth()).append(" bits");
            }
            lines.add(line.toString());
        }
        return lines;
    }

    /**
     * Requests a display mode in full screen, keeping the scaling of the current request, and observes the outcome.
     *
     * @param platform The started platform.
     * @param mode The display mode chosen in the menu.
     * @param listener The listener told of the effective result once the request is applied.
     * @return The accepted request.
     */
    public static DisplayRequest requestDisplayMode(Platform platform, DisplayMode mode, DisplayListener listener)
    {
        platform.addDisplayListener(listener);
        final ScalingPolicy scaling = platform.getEffectiveResult().requested().scalingPolicy();
        return platform.apply(new DisplayRequest(ScreenMode.FULL_SCREEN, mode, scaling, Collections.emptyList()));
    }
}
