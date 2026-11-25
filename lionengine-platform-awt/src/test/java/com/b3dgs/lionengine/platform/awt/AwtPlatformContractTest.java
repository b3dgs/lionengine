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

import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;

import com.b3dgs.lionengine.platform.DisplayRequest;
import com.b3dgs.lionengine.platform.Platform;
import com.b3dgs.lionengine.platform.PlatformContract;
import com.b3dgs.lionengine.platform.PlatformEvent;

/**
 * Binding of the {@link PlatformContract} suite on the AWT platform, with a real window (platform test,
 * REQ-PLATFORM-001).
 *
 * <p>
 * Close, background and foreground are dispatched to the window as AWT window events, and a resize resizes the window,
 * so that they follow the path of the AWT event thread; focus and context events are posted as the converter posts
 * them. The frames run on the test thread, which plays the runtime thread.
 * </p>
 */
@Tag(AwtTests.PLATFORM)
final class AwtPlatformContractTest extends PlatformContract
{
    /**
     * Skips the suite without display.
     */
    @BeforeAll
    static void requireDisplay()
    {
        assumeFalse(GraphicsEnvironment.isHeadless());
    }

    /** Platforms created by the test. */
    private final List<AwtPlatform> platforms = new ArrayList<>();

    /**
     * Closes the platforms created by the test and waits for their windows to be disposed.
     */
    @AfterEach
    void closePlatforms()
    {
        for (final AwtPlatform platform : platforms)
        {
            platform.close();
        }
        AwtTests.flush();
    }

    @Override
    protected Platform createPlatform(int logicalWidth, int logicalHeight, DisplayRequest request)
    {
        final AwtPlatform platform = new AwtPlatform(logicalWidth, logicalHeight, request, AwtTests.SINK, true);
        platforms.add(platform);
        return platform;
    }

    @Override
    protected void post(Platform platform, PlatformEvent.Kind kind)
    {
        final AwtPlatform awt = (AwtPlatform) platform;
        final int id = switch (kind)
        {
            case CLOSE_REQUESTED -> WindowEvent.WINDOW_CLOSING;
            case BACKGROUND -> WindowEvent.WINDOW_ICONIFIED;
            case FOREGROUND -> WindowEvent.WINDOW_DEICONIFIED;
            default -> 0;
        };
        if (id == 0)
        {
            awt.post(kind, platform.getSurfaceWidth(), platform.getSurfaceHeight());
        }
        else
        {
            final Frame window = awt.getWindow();
            AwtTests.onEventThread(() -> window.dispatchEvent(new WindowEvent(window, id)));
        }
    }

    @Override
    protected void resize(Platform platform, int surfaceWidth, int surfaceHeight)
    {
        final Frame window = ((AwtPlatform) platform).getWindow();
        AwtTests.onEventThread(() ->
        {
            final Insets insets = window.getInsets();
            final double scaleX = window.getGraphicsConfiguration().getDefaultTransform().getScaleX();
            final double scaleY = window.getGraphicsConfiguration().getDefaultTransform().getScaleY();
            window.setSize(AwtWindow.toUnits(surfaceWidth, scaleX) + insets.left + insets.right,
                           AwtWindow.toUnits(surfaceHeight, scaleY) + insets.top + insets.bottom);
            window.validate();
        });
        AwtTests.flush();
    }

    @Override
    protected void frame(Platform platform)
    {
        AwtTests.flush();
        ((AwtPlatform) platform).iterate();
    }
}
