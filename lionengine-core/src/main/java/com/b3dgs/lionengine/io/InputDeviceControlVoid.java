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
package com.b3dgs.lionengine.io;

/**
 * Void device.
 */
public class InputDeviceControlVoid implements InputDeviceControl
{
    /** Void instance. */
    private static final InputDeviceControlVoid INSTANCE = new InputDeviceControlVoid();

    /**
     * Get device instance.
     * 
     * @return The device instance.
     */
    public static final InputDeviceControl getInstance()
    {
        return INSTANCE;
    }

    /**
     * Create void device.
     */
    public InputDeviceControlVoid()
    {
        super();
    }

    @Override
    public void setHorizontalControlPositive(Integer code)
    {
        // Void
    }

    @Override
    public void setHorizontalControlNegative(Integer code)
    {
        // Void
    }

    @Override
    public void setVerticalControlPositive(Integer code)
    {
        // Void
    }

    @Override
    public void setVerticalControlNegative(Integer code)
    {
        // Void
    }

    @Override
    public Integer getHorizontalControlPositive()
    {
        return null;
    }

    @Override
    public Integer getHorizontalControlNegative()
    {
        return null;
    }

    @Override
    public Integer getVerticalControlPositive()
    {
        return null;
    }

    @Override
    public Integer getVerticalControlNegative()
    {
        return null;
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
    public void setFireButton(Integer code)
    {
        // Void
    }

    @Override
    public boolean isUpButtonOnce()
    {
        return false;
    }

    @Override
    public boolean isDownButtonOnce()
    {
        return false;
    }

    @Override
    public boolean isLeftButtonOnce()
    {
        return false;
    }

    @Override
    public boolean isRightButtonOnce()
    {
        return false;
    }

    @Override
    public boolean isFireButton()
    {
        return false;
    }

    @Override
    public boolean isFireButtonOnce()
    {
        return false;
    }
}
