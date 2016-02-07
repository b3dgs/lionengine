/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.core;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Representation of the screen device, supporting different screen rendering type and input devices.
 * A screen is connected to a {@link Sequence} in order to render it.
 * 
 * @see InputDevice
 * @see Graphic
 * @see Sequence
 * @see Graphics
 */
public interface Screen extends Context
{
    /**
     * Start the main frame if has.
     */
    void start();

    /**
     * Wait until screen get ready.
     * 
     * @throws LionEngineException If screen not ready before time out.
     */
    void awaitReady();

    /**
     * Pre update, specially called before the main {@link #update()} in order to prepare it if necessary.
     */
    void preUpdate();

    /**
     * Must be called when all rendering are done. It switches buffers before rendering.
     */
    void update();

    /**
     * Close main frame. Dispose all graphics resources.
     */
    void dispose();

    /**
     * Give focus to screen.
     */
    void requestFocus();

    /**
     * Hide window mouse pointer.
     */
    void hideCursor();

    /**
     * Show window mouse pointer.
     */
    void showCursor();

    /**
     * Add a screen listener.
     * 
     * @param listener The screen listener to add.
     */
    void addListener(ScreenListener listener);

    /**
     * Remove a screen listener.
     * 
     * @param listener The screen listener to remove.
     */
    void removeListener(ScreenListener listener);

    /**
     * Add a key listener.
     * 
     * @param listener The listener to add.
     */
    void addKeyListener(InputDeviceKeyListener listener);

    /**
     * Set icon from file.
     * 
     * @param filename The icon file name.
     */
    void setIcon(String filename);

    /**
     * Get current graphic.
     * 
     * @return The current graphic.
     */
    Graphic getGraphic();

    /**
     * Get main frame location x.
     * 
     * @return The main frame location x.
     */
    int getX();

    /**
     * Get main frame location y.
     * 
     * @return The main frame location y.
     */
    int getY();

    /**
     * Get the maximum time in milliseconds for screen to be ready.
     * 
     * @return The maximum time in milliseconds for screen to get ready.
     */
    long getReadyTimeOut();

    /**
     * Check if screen is ready.
     * 
     * @return <code>true</code> if ready, <code>false</code> else.
     */
    boolean isReady();

    /**
     * Call when resolution source has been changed.
     * 
     * @param source The new resolution source.
     */
    void onSourceChanged(Resolution source);
}
