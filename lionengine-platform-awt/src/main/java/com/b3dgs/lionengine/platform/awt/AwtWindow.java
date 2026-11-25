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

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;
import java.lang.reflect.InvocationTargetException;

/**
 * Window of the AWT platform and its canvas (ADR-0008, ADR-0043).
 *
 * <p>
 * The canvas is created with the platform, at the logical frame size in device pixels; the window is created, shown
 * and disposed on the AWT event thread. Rendering is active: repaint events are ignored, and the canvas has two
 * buffers once shown.
 * </p>
 */
final class AwtWindow
{
    /** Number of buffers of the canvas. */
    private static final int BUFFERS = 2;

    /**
     * Converts a size in device pixels to AWT units.
     *
     * @param size The size in device pixels.
     * @param scale The scale from AWT units to device pixels.
     * @return The size in AWT units.
     */
    static int toUnits(int size, double scale)
    {
        return (int) Math.round(size / scale);
    }

    /**
     * Tells whether the calling thread is the AWT event thread.
     *
     * @return {@code true} on the AWT event thread.
     */
    static boolean isEventThread()
    {
        return EventQueue.isDispatchThread();
    }

    /**
     * Runs an action on the AWT event thread and waits for it.
     *
     * @param action The action.
     * @throws IllegalStateException If the action fails or the wait is interrupted.
     */
    private static void onEventThread(Runnable action)
    {
        try
        {
            EventQueue.invokeAndWait(action);
        }
        catch (final InterruptedException exception)
        {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting for the AWT event thread", exception);
        }
        catch (final InvocationTargetException exception)
        {
            throw new IllegalStateException("Failure on the AWT event thread", exception.getCause());
        }
    }

    /** Canvas of the surface. */
    private final Canvas canvas = new Canvas();
    /** Window, {@code null} until opened. */
    private Frame frame;

    /**
     * Creates the canvas, not shown.
     *
     * @param width The canvas width in device pixels.
     * @param height The canvas height in device pixels.
     * @param scaleX The horizontal scale from AWT units to device pixels.
     * @param scaleY The vertical scale from AWT units to device pixels.
     */
    AwtWindow(int width, int height, double scaleX, double scaleY)
    {
        canvas.setPreferredSize(new Dimension(toUnits(width, scaleX), toUnits(height, scaleY)));
        canvas.setIgnoreRepaint(true);
    }

    /**
     * Creates and shows the window on the AWT event thread, and waits for it.
     *
     * @param receiver The receiver of the window and canvas events.
     * @throws IllegalStateException If the window cannot be opened or the wait is interrupted.
     */
    void open(AwtWindowEvents.Receiver receiver)
    {
        onEventThread(() -> show(receiver));
    }

    /**
     * Creates and shows the window on the default monitor, centered, with the canvas as its content; called on the AWT
     * event thread.
     *
     * @param receiver The receiver of the window and canvas events.
     */
    private void show(AwtWindowEvents.Receiver receiver)
    {
        final Frame opened = new Frame();
        final AwtWindowEvents events = new AwtWindowEvents(canvas, receiver);
        opened.addWindowListener(events);
        opened.addWindowFocusListener(events);
        canvas.addComponentListener(events);
        opened.setIgnoreRepaint(true);
        opened.add(canvas);
        opened.pack();
        opened.setLocationRelativeTo(null);
        opened.setVisible(true);
        canvas.createBufferStrategy(BUFFERS);
        frame = opened;
    }

    /**
     * Disposes the window on the AWT event thread, after the events already queued; no effect if it was not opened.
     */
    void dispose()
    {
        final Frame opened = frame;
        if (opened != null)
        {
            EventQueue.invokeLater(opened::dispose);
        }
    }

    /**
     * Returns the canvas.
     *
     * @return The canvas.
     */
    Canvas getCanvas()
    {
        return canvas;
    }

    /**
     * Returns the window.
     *
     * @return The window, {@code null} until opened.
     */
    Frame getFrame()
    {
        return frame;
    }
}
