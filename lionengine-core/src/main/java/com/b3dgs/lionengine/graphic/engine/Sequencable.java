/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.engine;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.InputDeviceKeyListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.graphic.Renderable;
import com.b3dgs.lionengine.graphic.Screen;

/**
 * Represents something that can be sequencable, updated at a specified rate.
 */
public interface Sequencable extends Updatable, Renderable
{
    /**
     * Start sequence.
     * 
     * @param screen The screen used for the sequence (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void start(Screen screen);

    /**
     * Add a key listener.
     * 
     * @param listener The listener to add (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void addKeyListener(InputDeviceKeyListener listener);

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
     * @param type The input device type (must not be <code>null</code>).
     * @return The input instance reference.
     * @throws LionEngineException If invalid argument or device not found.
     */
    <T extends InputDevice> T getInputDevice(Class<T> type);

    /**
     * Get the next sequence.
     * 
     * @return The next sequence to be executed, <code>null</code> if none.
     */
    Sequencable getNextSequence();

    /**
     * Called when sequence is closing.
     * 
     * @param hasNextSequence <code>true</code> if there is a next sequence, <code>false</code> else (then application
     *            will end definitely).
     */
    void onTerminated(boolean hasNextSequence);
}
