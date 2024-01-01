/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic;

import java.util.Collection;

import com.b3dgs.lionengine.Context;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Resolution;

/**
 * Representation of the screen device, supporting different screen rendering type and input devices.
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
     * @param icons The icons file name.
     */
    void setIcons(Collection<Media> icons);

    /**
     * Get current graphic.
     * 
     * @return The current graphic.
     */
    Graphic getGraphic();

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
