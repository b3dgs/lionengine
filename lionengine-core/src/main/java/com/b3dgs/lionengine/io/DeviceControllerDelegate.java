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

import java.util.function.Supplier;

import com.b3dgs.lionengine.InputDevice;

/**
 * Represents the device used to control.
 * 
 * @param provider The provider reference.
 */
public record DeviceControllerDelegate(Supplier<DeviceController> provider) implements DeviceController
{
    @Override
    public void addListener(DeviceControllerListener listener)
    {
        provider.get().addListener(listener);
    }

    @Override
    public void removeListener(DeviceControllerListener listener)
    {
        provider.get().removeListener(listener);
    }

    @Override
    public void addHorizontal(InputDevice device, DeviceAction action)
    {
        provider.get().addHorizontal(device, action);
    }

    @Override
    public void addVertical(InputDevice device, DeviceAction action)
    {
        provider.get().addVertical(device, action);
    }

    @Override
    public void addFire(String name, InputDevice device, Integer index, Integer code, DeviceAction action)
    {
        provider.get().addFire(name, device, index, code, action);
    }

    @Override
    public void setVisible(boolean visible)
    {
        provider.get().setVisible(visible);
    }

    @Override
    public void setDisabled(String device, boolean horizontal, boolean vertical)
    {
        provider.get().setDisabled(device, horizontal, vertical);
    }

    @Override
    public double getHorizontalDirection()
    {
        return provider.get().getHorizontalDirection();
    }

    @Override
    public double getVerticalDirection()
    {
        return provider.get().getVerticalDirection();
    }

    @Override
    public boolean isFired()
    {
        return provider.get().isFired();
    }

    @Override
    public Integer getFired()
    {
        return provider.get().getFired();
    }

    @Override
    public boolean isFired(Integer index)
    {
        return provider.get().isFired(index);
    }

    @Override
    public boolean isFiredOnce(Integer index)
    {
        return provider.get().isFiredOnce(index);
    }

    @Override
    public void update(double extrp)
    {
        provider.get().update(extrp);
    }
}
