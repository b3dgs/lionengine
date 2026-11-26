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
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.Platform;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.render.ScalingPolicy;

/**
 * Lists the screen modes a platform supports, then requests full screen and reads what the platform applied
 * (REQ-DISPLAY-001, REQ-DISPLAY-002, REQ-DISPLAY-008, ADR-0006, ADR-0060).
 *
 * <p>
 * A settings menu offers the modes of {@link Platform#isSupported(ScreenMode)}. A request never fails: it is applied
 * at the next frame boundary, and an unsupported screen mode falls back ({@code FULL_SCREEN} to {@code FULL_WINDOW},
 * then {@code WINDOWED}). The game learns the outcome through a {@link DisplayListener}, called on the runtime thread
 * when the inbox delivers it, with the effective result: what was requested, what was applied, the {@code degraded}
 * flag and the reason. The result also stays readable with {@link Platform#getEffectiveResult()}.
 * </p>
 *
 * @since 11.0.0
 */
public final class ScreenModeExample
{
    /**
     * Private constructor.
     */
    private ScreenModeExample()
    {
        throw new UnsupportedOperationException();
    }

    /**
     * Lists the screen modes the platform supports, for a settings menu.
     *
     * @param platform The platform.
     * @return The supported modes, in the order of {@link ScreenMode}.
     */
    public static List<ScreenMode> listSupported(Platform platform)
    {
        final List<ScreenMode> modes = new ArrayList<>();
        for (final ScreenMode mode : ScreenMode.values())
        {
            if (platform.isSupported(mode))
            {
                modes.add(mode);
            }
        }
        return modes;
    }

    /**
     * Requests full screen, keeping the scaling of the current request, and observes the outcome.
     *
     * @param platform The started platform.
     * @param listener The listener told of the effective result once the request is applied.
     * @return The accepted request.
     */
    public static DisplayRequest requestFullScreen(Platform platform, DisplayListener listener)
    {
        platform.addDisplayListener(listener);
        final ScalingPolicy scaling = platform.getEffectiveResult().requested().scalingPolicy();
        return platform.apply(new DisplayRequest(ScreenMode.FULL_SCREEN, null, scaling, Collections.emptyList()));
    }
}
