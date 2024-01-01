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
package com.b3dgs.lionengine.swt;

import com.b3dgs.lionengine.io.DevicePointer;

/**
 * Mouse input.
 */
public interface Mouse extends DevicePointer
{
    /**
     * Add an action that will be triggered on pressed state.
     * 
     * @param click The action key.
     * @param action The action reference.
     */
    void addActionPressed(Integer click, EventAction action);

    /**
     * Add an action that will be triggered on released state.
     * 
     * @param click The action key.
     * @param action The action reference.
     */
    void addActionReleased(Integer click, EventAction action);

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
