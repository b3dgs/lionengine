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
package com.b3dgs.lionengine.io;

/**
 * Listen to device changes.
 */
public interface DeviceControllerListener
{
    /**
     * Called on device changed.
     * 
     * @param name The device name.
     * @param push The push value.
     * @param c The char representation.
     * @param flag <code>true</code> if enabled, <code>false</code> if disabled.
     */
    void onDeviceChanged(String name, Integer push, char c, boolean flag);
}
