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
import java.awt.GraphicsConfiguration;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import com.b3dgs.lionengine.platform.PlatformEvent;

/**
 * Converts the AWT events of the window and of its canvas into platform events, on the AWT event thread
 * (ADR-0018, ADR-0031, REQ-PLATFORM-002).
 *
 * <p>
 * Closing the window is a close request; iconifying it goes to the background and restoring it comes back to the
 * foreground; the focus of the window gives the focus events; a resize of the canvas gives its new size in device
 * pixels (ADR-0043). Every event carries the current surface size.
 * </p>
 */
final class AwtWindowEvents extends WindowAdapter implements ComponentListener
{
    /**
     * Converts a size in AWT units to device pixels.
     *
     * @param size The size in AWT units.
     * @param scale The scale of the graphics configuration.
     * @return The size in device pixels.
     */
    static int toDevice(int size, double scale)
    {
        return (int) Math.round(size * scale);
    }

    /**
     * Returns the horizontal scale from AWT units to device pixels of a canvas.
     *
     * @param canvas The canvas.
     * @return The scale, {@code 1} when the canvas is not displayed.
     */
    static double getScaleX(Canvas canvas)
    {
        final GraphicsConfiguration configuration = canvas.getGraphicsConfiguration();
        return configuration == null ? 1.0 : configuration.getDefaultTransform().getScaleX();
    }

    /**
     * Returns the vertical scale from AWT units to device pixels of a canvas.
     *
     * @param canvas The canvas.
     * @return The scale, {@code 1} when the canvas is not displayed.
     */
    static double getScaleY(Canvas canvas)
    {
        final GraphicsConfiguration configuration = canvas.getGraphicsConfiguration();
        return configuration == null ? 1.0 : configuration.getDefaultTransform().getScaleY();
    }

    /** Canvas of the surface. */
    private final Canvas canvas;
    /** Receiver of the events. */
    private final Receiver receiver;

    /**
     * Creates the converter.
     *
     * @param canvas The canvas of the surface.
     * @param receiver The receiver of the events.
     */
    AwtWindowEvents(Canvas canvas, Receiver receiver)
    {
        super();
        this.canvas = canvas;
        this.receiver = receiver;
    }

    /**
     * Sends an event with the current surface size.
     *
     * @param kind The kind of event.
     */
    private void send(PlatformEvent.Kind kind)
    {
        receiver.receive(kind,
                         toDevice(canvas.getWidth(), getScaleX(canvas)),
                         toDevice(canvas.getHeight(), getScaleY(canvas)));
    }

    @Override
    public void windowClosing(WindowEvent event)
    {
        send(PlatformEvent.Kind.CLOSE_REQUESTED);
    }

    @Override
    public void windowIconified(WindowEvent event)
    {
        send(PlatformEvent.Kind.BACKGROUND);
    }

    @Override
    public void windowDeiconified(WindowEvent event)
    {
        send(PlatformEvent.Kind.FOREGROUND);
    }

    @Override
    public void windowGainedFocus(WindowEvent event)
    {
        send(PlatformEvent.Kind.FOCUS_GAINED);
    }

    @Override
    public void windowLostFocus(WindowEvent event)
    {
        send(PlatformEvent.Kind.FOCUS_LOST);
    }

    @Override
    public void componentResized(ComponentEvent event)
    {
        send(PlatformEvent.Kind.SURFACE_RESIZED);
    }

    @Override
    public void componentMoved(ComponentEvent event)
    {
        // Moving the window changes nothing for the game.
    }

    @Override
    public void componentShown(ComponentEvent event)
    {
        // The canvas is shown with its window: the window events report it.
    }

    @Override
    public void componentHidden(ComponentEvent event)
    {
        // The canvas is hidden with its window: the window events report it.
    }

    /**
     * Receiver of the converted events.
     */
    @FunctionalInterface
    interface Receiver
    {
        /**
         * Receives an event, on the AWT event thread.
         *
         * @param kind The kind of event.
         * @param surfaceWidth The surface width in device pixels.
         * @param surfaceHeight The surface height in device pixels.
         */
        void receive(PlatformEvent.Kind kind, int surfaceWidth, int surfaceHeight);
    }
}
