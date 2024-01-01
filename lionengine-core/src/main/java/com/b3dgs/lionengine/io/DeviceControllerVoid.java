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

import com.b3dgs.lionengine.InputDevice;

/**
 * Represents void device.
 */
public class DeviceControllerVoid implements DeviceController
{
    private static final DeviceController INSTANCE = new DeviceControllerVoid();

    /**
     * Get void controller.
     * 
     * @return The void controller.
     */
    public static DeviceController getInstance()
    {
        return INSTANCE;
    }

    /**
     * Create model.
     */
    public DeviceControllerVoid()
    {
        super();
    }

    @Override
    public void addListener(DeviceControllerListener listener)
    {
        // Mock
    }

    @Override
    public void removeListener(DeviceControllerListener listener)
    {
        // Mock
    }

    @Override
    public void addHorizontal(InputDevice device, DeviceAction action)
    {
        // Mock
    }

    @Override
    public void addVertical(InputDevice device, DeviceAction action)
    {
        // Mock
    }

    @Override
    public void addFire(String name, InputDevice device, Integer index, Integer code, DeviceAction action)
    {
        // Mock
    }

    @Override
    public void setVisible(boolean visible)
    {
        // Mock
    }

    @Override
    public void setDisabled(String device, boolean horizontal, boolean vertical)
    {
        // Mock
    }

    @Override
    public double getHorizontalDirection()
    {
        return 0.0;
    }

    @Override
    public double getVerticalDirection()
    {
        return 0.0;
    }

    @Override
    public boolean isFired()
    {
        return false;
    }

    @Override
    public Integer getFired()
    {
        return null;
    }

    @Override
    public boolean isFired(Integer index)
    {
        return false;
    }

    @Override
    public boolean isFiredOnce(Integer index)
    {
        return false;
    }

    @Override
    public void update(double extrp)
    {
        // Mock
    }
}
