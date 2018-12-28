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
package com.b3dgs.lionengine;

/**
 * Represents the execution context.
 */
public interface Context
{
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
     * Get the config.
     * 
     * @return The config.
     */
    Config getConfig();

    /**
     * Get the input device instance from its type.
     * 
     * @param <T> The input device.
     * @param type The input device type (must not be <code>null</code>).
     * @return The input instance reference.
     * @throws LionEngineException If device not found.
     */
    <T extends InputDevice> T getInputDevice(Class<T> type);
}
