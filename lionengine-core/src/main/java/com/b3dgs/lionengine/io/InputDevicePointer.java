/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.io;

import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.Updatable;

/**
 * Represents a pointer device, supporting location and click number.
 */
public interface InputDevicePointer extends InputDevice, Updatable
{
    /**
     * Get the horizontal location.
     * 
     * @return The horizontal location.
     */
    int getX();

    /**
     * Get the vertical location.
     * 
     * @return The vertical location.
     */
    int getY();

    /**
     * Get the horizontal movement.
     * 
     * @return The horizontal movement.
     */
    int getMoveX();

    /**
     * Get the vertical movement.
     * 
     * @return The vertical movement.
     */
    int getMoveY();

    /**
     * Get the click number.
     * 
     * @return The click number.
     */
    int getClick();

    /**
     * Check if click is pressed.
     * 
     * @param click The click to check.
     * @return The pressed state.
     */
    boolean hasClicked(int click);

    /**
     * Check if click is pressed once only (ignore 'still clicked').
     * 
     * @param click The click to check.
     * @return The pressed state.
     */
    boolean hasClickedOnce(int click);

    /**
     * Check if pointer moved.
     * 
     * @return <code>true</code> if moved, <code>false</code> else.
     */
    boolean hasMoved();
}
