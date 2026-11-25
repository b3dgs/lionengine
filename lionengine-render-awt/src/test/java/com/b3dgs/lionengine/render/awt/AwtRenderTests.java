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
package com.b3dgs.lionengine.render.awt;

import java.awt.Canvas;
import java.util.Collections;

import com.b3dgs.lionengine.LogSink;
import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.ScreenMode;
import com.b3dgs.lionengine.platform.awt.AwtSurface;
import com.b3dgs.lionengine.render.ScalingPolicy;
import com.b3dgs.lionengine.testsupport.HeadlessPlatform;

/**
 * Shared fixtures of the AWT renderer tests.
 */
final class AwtRenderTests
{
    /** Tag of the tests needing a real display (testing.md, platform tests). */
    static final String PLATFORM = "platform";
    /** Windowed request with {@code FIT} scaling and no filter. */
    static final DisplayRequest REQUEST = new DisplayRequest(ScreenMode.WINDOWED,
                                                             null,
                                                             ScalingPolicy.FIT,
                                                             Collections.emptyList());
    /** Sink writing nothing. */
    static final LogSink SINK = (level, message, error) ->
    {
        // Nothing to write.
    };

    /**
     * Creates a headless platform with an AWT surface whose canvas is never shown.
     *
     * @param width The logical frame width.
     * @param height The logical frame height.
     * @return The platform.
     */
    static HeadlessPlatform platform(int width, int height)
    {
        final HeadlessPlatform platform = new HeadlessPlatform(width, height, REQUEST, SINK);
        final Canvas canvas = new Canvas();
        platform.setCapability(AwtSurface.class, () -> canvas);
        return platform;
    }

    /**
     * Private constructor.
     */
    private AwtRenderTests()
    {
        throw new AssertionError();
    }
}
