/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.awt;

import java.awt.event.MouseEvent;

import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.core.InputDevicePointer;

/**
 * Represents the mouse input. Gives informations such as mouse click and cursor location.
 */
public interface Mouse extends InputDevicePointer, Updatable
{
    /** Left click. */
    int LEFT = MouseEvent.BUTTON1;
    /** Middle click. */
    int MIDDLE = MouseEvent.BUTTON2;
    /** Right click. */
    int RIGHT = MouseEvent.BUTTON3;

    /**
     * Add an action that will be triggered on pressed state.
     * <p>
     * Alternative usage with classic programming style can be achieved with {@link #hasClicked(int)} or
     * {@link #hasClickedOnce(int)}.
     * </p>
     * 
     * @param click The action key.
     * @param action The action reference.
     */
    void addActionPressed(int click, EventAction action);

    /**
     * Add an action that will be triggered on released state.
     * <p>
     * Alternative usage with classic programming style can be achieved with {@link #hasClicked(int)} or
     * {@link #hasClickedOnce(int)}.
     * </p>
     * 
     * @param click The action key.
     * @param action The action reference.
     */
    void addActionReleased(int click, EventAction action);

    /**
     * Lock mouse at its center.
     */
    void lock();

    /**
     * Lock mouse at specified location.
     * 
     * @param x The location x.
     * @param y The location y.
     */
    void lock(int x, int y);

    /**
     * Perform a click.
     * 
     * @param click The click to perform.
     */
    void doClick(int click);

    /**
     * Perform a click at specified coordinate.
     * 
     * @param click The click to perform.
     * @param x The location x.
     * @param y The location y.
     */
    void doClickAt(int click, int x, int y);

    /**
     * Set mouse center for lock operation.
     * 
     * @param x The location x.
     * @param y The location y.
     */
    void setCenter(int x, int y);

    /**
     * Get location on screen x.
     * 
     * @return The location on screen x.
     */
    int getOnScreenX();

    /**
     * Get location on screen y.
     * 
     * @return The location on screen y.
     */
    int getOnScreenY();
}
