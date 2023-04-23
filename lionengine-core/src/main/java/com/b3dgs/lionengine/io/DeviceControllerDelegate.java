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

import java.util.function.Supplier;

import com.b3dgs.lionengine.InputDevice;

/**
 * Represents the device used to control.
 */
public class DeviceControllerDelegate implements DeviceController
{
    /** The provider reference. */
    private final Supplier<DeviceController> provider;

    /**
     * Create delegate.
     * 
     * @param provider The provider reference.
     */
    public DeviceControllerDelegate(Supplier<DeviceController> provider)
    {
        super();

        this.provider = provider;
    }

    /**
     * Get current control.
     * 
     * @return The current control.
     */
    private DeviceController get()
    {
        return provider.get();
    }

    @Override
    public void addListener(DeviceControllerListener listener)
    {
        get().addListener(listener);
    }

    @Override
    public void removeListener(DeviceControllerListener listener)
    {
        get().removeListener(listener);
    }

    @Override
    public void addHorizontal(InputDevice device, DeviceAction action)
    {
        get().addHorizontal(device, action);
    }

    @Override
    public void addVertical(InputDevice device, DeviceAction action)
    {
        get().addVertical(device, action);
    }

    @Override
    public void addFire(String name, InputDevice device, Integer index, Integer code, DeviceAction action)
    {
        get().addFire(name, device, index, code, action);
    }

    @Override
    public void setVisible(boolean visible)
    {
        get().setVisible(visible);
    }

    @Override
    public void setDisabled(String device, boolean horizontal, boolean vertical)
    {
        get().setDisabled(device, horizontal, vertical);
    }

    @Override
    public double getHorizontalDirection()
    {
        return get().getHorizontalDirection();
    }

    @Override
    public double getVerticalDirection()
    {
        return get().getVerticalDirection();
    }

    @Override
    public boolean isFired()
    {
        return get().isFired();
    }

    @Override
    public Integer getFired()
    {
        return get().getFired();
    }

    @Override
    public boolean isFired(Integer index)
    {
        return get().isFired(index);
    }

    @Override
    public boolean isFiredOnce(Integer index)
    {
        return get().isFiredOnce(index);
    }

    @Override
    public void update(double extrp)
    {
        get().update(extrp);
    }
}
