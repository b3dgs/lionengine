/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.Resolution;

/**
 * Represents something that can be sequencable, updated at a specified rate.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Sequencable
{
    /**
     * Start the next sequence and wait for current sequence to end before next sequence continues. This function should
     * be used to synchronize two sequences (eg: load a next sequence while being in a menu). Do not forget to call
     * {@link #end()} in order to give control to the next sequence.
     * 
     * @param wait <code>true</code> to wait for the next sequence to be loaded, <code>false</code> else.
     * @param nextSequenceClass The next sequence class reference (must not be <code>null</code>).
     * @param arguments The arguments list.
     */
    void start(boolean wait, Class<? extends Sequence> nextSequenceClass, Object... arguments);

    /**
     * Terminate sequence.
     */
    void end();

    /**
     * Terminate sequence, and set the next sequence.
     * 
     * @param nextSequenceClass The next sequence class reference.
     * @param arguments The sequence arguments list if needed by its constructor.
     */
    void end(Class<? extends Sequence> nextSequenceClass, Object... arguments);

    /**
     * Add a key listener.
     * 
     * @param listener The listener to add.
     */
    void addKeyListener(InputDeviceKeyListener listener);

    /**
     * Set the extrapolation flag.
     * 
     * @param extrapolated <code>true</code> will activate it, <code>false</code> will disable it.
     */
    void setExtrapolated(boolean extrapolated);

    /**
     * Set the new resolution used by the sequence.
     * 
     * @param newSource The new resolution used.
     */
    void setResolution(Resolution newSource);

    /**
     * Set the system cursor visibility.
     * 
     * @param visible <code>true</code> if visible, <code>false</code> else.
     */
    void setSystemCursorVisible(boolean visible);

    /**
     * Get the configuration.
     * 
     * @return The configuration.
     */
    Config getConfig();

    /**
     * Get current frame rate (number of image per second).
     * 
     * @return The current number of image per second.
     */
    int getFps();

    /**
     * Get the input device instance from its type.
     * 
     * @param <T> The input device.
     * @param type The input device type.
     * @return The input instance reference, <code>null</code> if not found.
     */
    <T extends InputDevice> T getInputDevice(Class<T> type);
}
