/*
 * Copyright (C) 2013-2022 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.InputDevice;

/**
 * Represents a directional device, supporting multiple axis.
 */
public interface DevicePush extends InputDevice
{
    /**
     * Check if pushed.
     * 
     * @return <code>true</code> if pushed, <code>false</code> else.
     */
    boolean isPushed();

    /**
     * Get current pushed index.
     * 
     * @return The pushed index, <code>null</code> if none.
     */
    Integer getPushed();

    /**
     * Check if index is currently pushed.
     * 
     * @param index The index to check.
     * @return <code>true</code> if pushed, <code>false</code> else.
     */
    boolean isPushed(Integer index);

    /**
     * Check if index is pushed only one time (will ignore continuous push).
     * 
     * @param index The index to check.
     * @return <code>true</code> if pushed, <code>false</code> else.
     */
    boolean isPushedOnce(Integer index);
}
