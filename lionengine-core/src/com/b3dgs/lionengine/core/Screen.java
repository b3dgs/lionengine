/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.awt.event.KeyListener;

import com.b3dgs.lionengine.input.Keyboard;
import com.b3dgs.lionengine.input.Mouse;

/**
 * Representation of the screen device, supporting fullscreen and windowed mode. It uses a double buffer for any
 * rendering.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
interface Screen
{
    /**
     * Show the screen.
     */
    void show();

    /**
     * Must be called when all rendering are done. It switch buffers before rendering.
     */
    void update();

    /**
     * Close main frame. Dispose all graphics resources.
     */
    void dispose();

    /**
     * Start the main frame if has.
     */
    void start();

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
     * Add a key listener.
     * 
     * @param listener The listener to add.
     */
    void addKeyListener(KeyListener listener);

    /**
     * Link keyboard to the screen (listening to).
     * 
     * @param keyboard The keyboard reference.
     */
    void addKeyboard(Keyboard keyboard);

    /**
     * Link keyboard to the screen (listening to).
     * 
     * @param mouse The mouse reference.
     */
    void addMouse(Mouse mouse);

    /**
     * Set sequence reference.
     * 
     * @param sequence The sequence reference.
     */
    void setSequence(Sequence sequence);

    /**
     * Set window icon from file.
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
     * Get the config.
     * 
     * @return The config.
     */
    Config getConfig();

    /**
     * Get main frame location x.
     * 
     * @return The main frame location x.
     */
    int getLocationX();

    /**
     * Get main frame location y.
     * 
     * @return The main frame location y.
     */
    int getLocationY();

}
