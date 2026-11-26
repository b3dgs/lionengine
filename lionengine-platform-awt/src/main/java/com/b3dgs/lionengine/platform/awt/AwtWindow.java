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
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.geom.AffineTransform;
import java.lang.reflect.InvocationTargetException;

import com.b3dgs.lionengine.platform.DisplayMode;
import com.b3dgs.lionengine.platform.ScreenMode;

/**
 * Window of the AWT platform and its canvas (ADR-0008, ADR-0043).
 *
 * <p>
 * The canvas is created with the platform, at the logical frame size in device pixels; the window is created, shown
 * and disposed on the AWT event thread. Rendering is active: repaint events are ignored, and the canvas has two
 * buffers once shown.
 * </p>
 *
 * <p>
 * Screen modes (ADR-0060), realized on the AWT event thread when they change: {@code WINDOWED} is a decorated window
 * of the logical frame size, centered; {@code FULL_WINDOW} an undecorated window covering the default monitor;
 * {@code FULL_SCREEN} an undecorated window made the full-screen window of the default monitor. Changing the
 * decoration hides and shows the window again, and the canvas gets a new buffer strategy.
 * </p>
 *
 * <p>
 * Display modes (ADR-0061): in {@code FULL_SCREEN}, the target display mode is set on the default monitor when the
 * monitor supports display changes; the desktop mode, read when the window opens, is set back before the window leaves
 * full screen and when it is disposed. Each realization gives back the display mode in effect.
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
            throw new IllegalStateException("Failure on the AWT event thread", exception);
        }
    }

    /**
     * Checks that the environment has a display.
     *
     * @throws IllegalStateException If the environment is headless.
     */
    static void checkDisplay()
    {
        if (GraphicsEnvironment.isHeadless())
        {
            throw new IllegalStateException("The AWT platform needs a display");
        }
    }

    /**
     * Creates the window of the default monitor, its canvas at the logical frame size in device pixels.
     *
     * @param width The logical frame width in pixels.
     * @param height The logical frame height in pixels.
     * @return The window, not shown.
     */
    static AwtWindow create(int width, int height)
    {
        final AffineTransform transform = getDevice().getDefaultConfiguration().getDefaultTransform();
        return new AwtWindow(width, height, transform.getScaleX(), transform.getScaleY());
    }

    /** Canvas of the surface. */
    private final Canvas canvas = new Canvas();
    /** Window, {@code null} until opened. */
    private Frame frame;
    /** Screen mode of the window, applied once shown. */
    private ScreenMode screenMode = ScreenMode.WINDOWED;
    /** Display mode of the default monitor when the window opened, {@code null} until opened. */
    private java.awt.DisplayMode desktop;
    /** Display mode in effect after the last realization, written on the AWT event thread. */
    private DisplayMode current;

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
     * Returns the default monitor.
     *
     * @return The default screen device.
     */
    private static GraphicsDevice getDevice()
    {
        return GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
    }

    /**
     * Creates and shows the window on the AWT event thread, and waits for it.
     *
     * @param receiver The receiver of the window and canvas events.
     * @param mode The screen mode of the window, supported by the platform.
     * @param target The display mode to set in {@code FULL_SCREEN}.
     * @return The display mode in effect.
     * @throws IllegalStateException If the window cannot be opened or the wait is interrupted.
     */
    DisplayMode open(AwtWindowEvents.Receiver receiver, ScreenMode mode, DisplayMode target)
    {
        onEventThread(() -> show(receiver, mode, target));
        return current;
    }

    /**
     * Realizes a screen mode and a display mode on the shown window, at once on the AWT event thread, and waits for
     * it; called by the runtime thread at the frame boundary. The window is shown again only if the screen mode
     * changes; the display mode is set only in {@code FULL_SCREEN}.
     *
     * @param mode The screen mode, supported by the platform.
     * @param target The display mode to set in {@code FULL_SCREEN}.
     * @return The display mode in effect.
     */
    DisplayMode realize(ScreenMode mode, DisplayMode target)
    {
        onEventThread(() ->
        {
            if (mode != screenMode)
            {
                change(mode);
            }
            update(mode, target);
        });
        return current;
    }

    /**
     * Sets the target display mode in {@code FULL_SCREEN}, reads the display mode in effect, and gives the canvas a
     * new buffer strategy if the display mode changed; on the AWT event thread.
     *
     * @param mode The screen mode realized.
     * @param target The display mode to set in {@code FULL_SCREEN}.
     */
    private void update(ScreenMode mode, DisplayMode target)
    {
        final GraphicsDevice device = getDevice();
        if (mode == ScreenMode.FULL_SCREEN && device.isDisplayChangeSupported())
        {
            for (final java.awt.DisplayMode candidate : device.getDisplayModes())
            {
                if (AwtDisplay.convert(candidate).equals(target))
                {
                    setDisplayMode(device, candidate);
                    break;
                }
            }
        }
        final DisplayMode effective = AwtDisplay.convert(device.getDisplayMode());
        if (current != null && !effective.equals(current))
        {
            canvas.createBufferStrategy(BUFFERS);
        }
        current = effective;
    }

    /**
     * Sets a display mode on the monitor if it is not the current one; a refusal of the system keeps the current mode,
     * which the caller reads and reports.
     *
     * @param device The monitor.
     * @param mode The display mode.
     */
    private static void setDisplayMode(GraphicsDevice device, java.awt.DisplayMode mode)
    {
        if (!mode.equals(device.getDisplayMode()))
        {
            try
            {
                device.setDisplayMode(mode);
            }
            catch (IllegalArgumentException | UnsupportedOperationException _)
            {
                // Refused: the current mode stays in effect and is reported in the effective result
            }
        }
    }

    /**
     * Leaves full screen if the window is the full-screen window, after setting the desktop mode back; on the AWT
     * event thread.
     *
     * @param device The monitor.
     */
    private void leaveFullScreen(GraphicsDevice device)
    {
        if (device.getFullScreenWindow() == frame)
        {
            if (device.isDisplayChangeSupported())
            {
                setDisplayMode(device, desktop);
            }
            device.setFullScreenWindow(null);
        }
    }

    /**
     * Changes the screen mode of the shown window, on the AWT event thread.
     *
     * @param mode The screen mode.
     */
    private void change(ScreenMode mode)
    {
        final GraphicsDevice device = getDevice();
        leaveFullScreen(device);
        frame.dispose();
        frame.setUndecorated(mode != ScreenMode.WINDOWED);
        if (mode == ScreenMode.FULL_WINDOW)
        {
            frame.setBounds(device.getDefaultConfiguration().getBounds());
        }
        else
        {
            frame.pack();
            frame.setLocationRelativeTo(null);
        }
        frame.setVisible(true);
        if (mode == ScreenMode.FULL_SCREEN)
        {
            device.setFullScreenWindow(frame);
        }
        canvas.createBufferStrategy(BUFFERS);
        screenMode = mode;
    }

    /**
     * Creates and shows the window on the default monitor, centered, with the canvas as its content, then realizes
     * the screen mode and the display mode; called on the AWT event thread.
     *
     * @param receiver The receiver of the window and canvas events.
     * @param mode The screen mode of the window.
     * @param target The display mode to set in {@code FULL_SCREEN}.
     */
    private void show(AwtWindowEvents.Receiver receiver, ScreenMode mode, DisplayMode target)
    {
        desktop = getDevice().getDisplayMode();
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
        if (mode != ScreenMode.WINDOWED)
        {
            change(mode);
        }
        update(mode, target);
    }

    /**
     * Disposes the window on the AWT event thread, after the events already queued, leaving full screen and setting
     * the desktop mode back first; no effect if it was not opened.
     */
    void dispose()
    {
        final Frame opened = frame;
        if (opened != null)
        {
            EventQueue.invokeLater(() ->
            {
                leaveFullScreen(getDevice());
                opened.dispose();
            });
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
