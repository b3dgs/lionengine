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

import java.awt.Canvas;
import java.awt.Component;
import java.awt.Container;
import java.awt.Frame;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.LockSupport;

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
@Tag(AwtFixtures.PLATFORM)
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

    /** Time given to the window system to apply a size, in nanoseconds. */
    private static final long RESIZE_TIMEOUT_NANOS = 5_000_000_000L;
    /** Pause between two checks of the size, in nanoseconds. */
    private static final long RESIZE_CHECK_NANOS = 20_000_000L;

    /**
     * Returns the canvas of a window.
     *
     * @param container The window or one of its containers.
     * @return The first canvas found, depth first.
     */
    private static Canvas findCanvas(Container container)
    {
        for (final Component component : container.getComponents())
        {
            if (component instanceof final Canvas canvas)
            {
                return canvas;
            }
            if (component instanceof final Container child)
            {
                final Canvas canvas = findCanvas(child);
                if (canvas != null)
                {
                    return canvas;
                }
            }
        }
        return null;
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
        AwtFixtures.flush();
    }

    @Override
    protected Platform createPlatform(int logicalWidth, int logicalHeight, DisplayRequest request)
    {
        final AwtPlatform platform = new AwtPlatform(logicalWidth, logicalHeight, request, AwtFixtures.SINK, true);
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
            AwtFixtures.onEventThread(() -> window.dispatchEvent(new WindowEvent(window, id)));
        }
    }

    /**
     * {@inheritDoc}
     *
     * <p>
     * The window system applies a size asynchronously: a configure event of an earlier resize can arrive after
     * {@link Frame#setSize(int, int)} and put the window back to the earlier size. The size is therefore applied until
     * the canvas has it on two consecutive checks, so that the last resize event posted to the platform carries the
     * requested size.
     * </p>
     */
    @Override
    protected void resize(Platform platform, int surfaceWidth, int surfaceHeight)
    {
        final Frame window = ((AwtPlatform) platform).getWindow();
        final Canvas canvas = findCanvas(window);
        final long deadline = System.nanoTime() + RESIZE_TIMEOUT_NANOS;
        int stable = 0;
        while (stable < 2)
        {
            final boolean[] applied = new boolean[1];
            AwtFixtures.onEventThread(() ->
            {
                final Insets insets = window.getInsets();
                final double scaleX = window.getGraphicsConfiguration().getDefaultTransform().getScaleX();
                final double scaleY = window.getGraphicsConfiguration().getDefaultTransform().getScaleY();
                final int width = AwtWindow.toUnits(surfaceWidth, scaleX);
                final int height = AwtWindow.toUnits(surfaceHeight, scaleY);
                applied[0] = canvas.getWidth() == width && canvas.getHeight() == height;
                if (!applied[0])
                {
                    window.setSize(width + insets.left + insets.right, height + insets.top + insets.bottom);
                    window.validate();
                }
            });
            stable = applied[0] ? stable + 1 : 0;
            if (System.nanoTime() > deadline)
            {
                throw new AssertionError("The window did not keep the size " + surfaceWidth + "x" + surfaceHeight);
            }
            // Gives the window system time to send its configure events before the next check.
            LockSupport.parkNanos(RESIZE_CHECK_NANOS);
        }
        AwtFixtures.flush();
    }

    @Override
    protected void frame(Platform platform)
    {
        AwtFixtures.flush();
        ((AwtPlatform) platform).iterate();
    }
}
