/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.Updatable;

/**
 * Represents the device used to control.
 */
public interface DeviceController extends Updatable, Listenable<DeviceControllerListener>
{
    /**
     * Add horizontal control from action.
     * 
     * @param device The device reference.
     * @param action The action reference.
     */
    void addHorizontal(InputDevice device, DeviceAction action);

    /**
     * Add vertical control from action.
     * 
     * @param device The device reference.
     * @param action The action reference.
     */
    void addVertical(InputDevice device, DeviceAction action);

    /**
     * Add fire control from action.
     * 
     * @param name The device name.
     * @param device The device reference.
     * @param index The fire index.
     * @param code The device code.
     * @param action The action reference.
     */
    void addFire(String name, InputDevice device, Integer index, Integer code, DeviceAction action);

    /**
     * Set visibility.
     * 
     * @param visible <code>true</code> if visible, <code>false</code> else.
     */
    void setVisible(boolean visible);

    /**
     * Set axis disabled flag.
     * 
     * @param device The device reference.
     * @param horizontal The horizontal axis flag.
     * @param vertical The vertical axis flag.
     */
    void setDisabled(String device, boolean horizontal, boolean vertical);

    /**
     * Get current horizontal axis value.
     * 
     * @return The horizontal axis value (between -1.0 and 1.0 included).
     */
    double getHorizontalDirection();

    /**
     * Get current horizontal axis value.
     * 
     * @return The horizontal axis value (between -1.0 and 1.0 included).
     */
    double getVerticalDirection();

    /**
     * Check if at least one index is fired.
     * 
     * @return <code>true</code> if any fired, <code>false</code> if none.
     */
    boolean isFired();

    /**
     * Get last fired index.
     * 
     * @return The last fired index, <code>null</code> if none.
     */
    Integer getFired();

    /**
     * Check if index is fired.
     * 
     * @param index The index value.
     * @return <code>true</code> if fired, <code>false</code> else.
     */
    boolean isFired(Integer index);

    /**
     * Check if index is fired. Will return <code>false</code> if kept fired.
     * 
     * @param index The index value.
     * @return <code>true</code> if fired, <code>false</code> else.
     */
    boolean isFiredOnce(Integer index);

    /**
     * Check if index is fired.
     * 
     * @param mapper The mapper reference.
     * @return <code>true</code> if fired, <code>false</code> else.
     */
    default boolean isFired(DeviceMapper mapper)
    {
        return isFired(mapper.getIndex());
    }

    /**
     * Check if index is fired. Will return <code>false</code> if kept fired.
     * 
     * @param mapper The mapper reference.
     * @return <code>true</code> if fired, <code>false</code> else.
     */
    default boolean isFiredOnce(DeviceMapper mapper)
    {
        return isFiredOnce(mapper.getIndex());
    }
}
