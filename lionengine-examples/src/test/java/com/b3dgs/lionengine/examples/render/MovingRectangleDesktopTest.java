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
package com.b3dgs.lionengine.examples.render;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeFalse;

import java.awt.Canvas;
import java.awt.Container;
import java.awt.EventQueue;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.b3dgs.lionengine.Log;
import com.b3dgs.lionengine.platform.awt.AwtSurface;
import com.b3dgs.lionengine.runtime.LoopState;

/**
 * Runs {@link MovingRectangleDesktop} in a real window until the square has moved, then closes the window (platform
 * test, REQ-DOC-001, REQ-PLATFORM-001).
 *
 * <p>
 * A close listener vetoes the close requests until the square has moved 30 pixels, half a second at 60 pixels per
 * second: the test closes the window repeatedly until the game accepts.
 * </p>
 */
@Tag("platform")
final class MovingRectangleDesktopTest
{
    /** Distance the square moves before the game accepts to close, in pixels. */
    private static final double DISTANCE = 30.0;
    /** Longest run of the game, in nanoseconds. */
    private static final long TIMEOUT = TimeUnit.SECONDS.toNanos(10L);
    /** Wait between two close requests, in milliseconds. */
    private static final long RETRY_MILLIS = 50L;

    /**
     * Skips the test without display.
     */
    @BeforeAll
    static void requireDisplay()
    {
        assumeFalse(GraphicsEnvironment.isHeadless());
    }

    /**
     * Returns the window of a canvas, read on the AWT event thread.
     *
     * @param canvas The canvas.
     * @return The window, {@code null} until it is opened.
     * @throws InterruptedException If interrupted.
     * @throws InvocationTargetException If the read fails.
     */
    private static Window window(Canvas canvas) throws InterruptedException, InvocationTargetException
    {
        final AtomicReference<Window> window = new AtomicReference<>();
        EventQueue.invokeAndWait(() ->
        {
            final Container parent = canvas.getParent();
            if (parent instanceof final Window opened && opened.isShowing())
            {
                window.set(opened);
            }
        });
        return window.get();
    }

    /**
     * The game runs in a window, the square moves, and closing the window stops the loop, closes the window and
     * releases the texture.
     *
     * @throws InterruptedException If interrupted.
     * @throws InvocationTargetException If the window cannot be read or closed.
     */
    @Test
    void testRunAndClose() throws InterruptedException, InvocationTargetException
    {
        final MovingRectangleDesktop.Game game = MovingRectangleDesktop.create(new Log());
        game.platform().addCloseListener(() -> game.square().getX() >= DISTANCE);
        final Canvas canvas = game.platform().getCapability(AwtSurface.class).getCanvas();
        final Thread thread = new Thread(game::run);
        final long end = System.nanoTime() + TIMEOUT;

        thread.start();
        while (thread.isAlive())
        {
            assertTrue(System.nanoTime() < end, "The game did not stop in time");
            final Window window = window(canvas);
            if (window != null)
            {
                EventQueue.invokeAndWait(() -> window.dispatchEvent(new WindowEvent(window,
                                                                                    WindowEvent.WINDOW_CLOSING)));
            }
            thread.join(RETRY_MILLIS);
        }

        assertEquals(LoopState.STOPPED, game.loop().getState());
        assertTrue(game.square().getX() >= DISTANCE);
        assertFalse(game.scenes().hasActive());
        EventQueue.invokeAndWait(() -> assertFalse(canvas.isDisplayable()));
    }
}
