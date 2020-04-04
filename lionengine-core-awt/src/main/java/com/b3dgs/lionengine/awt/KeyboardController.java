/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.awt;

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.io.InputDeviceControl;

/**
 * Keyboard input controller.
 */
public final class KeyboardController implements InputDeviceControl
{
    private final Keyboard keyboard;
    private final Map<Integer, Integer> fire = new HashMap<>();

    /**
     * Create controller.
     * 
     * @param keyboard The keyboard reference.
     */
    public KeyboardController(Keyboard keyboard)
    {
        super();

        this.keyboard = keyboard;
    }

    @Override
    public void setHorizontalControlPositive(Integer code)
    {
        keyboard.setHorizontalControlPositive(code);
    }

    @Override
    public void setHorizontalControlNegative(Integer code)
    {
        keyboard.setHorizontalControlNegative(code);
    }

    @Override
    public void setVerticalControlPositive(Integer code)
    {
        keyboard.setVerticalControlPositive(code);
    }

    @Override
    public void setVerticalControlNegative(Integer code)
    {
        keyboard.setVerticalControlNegative(code);
    }

    @Override
    public Integer getHorizontalControlPositive()
    {
        return keyboard.getHorizontalControlPositive();
    }

    @Override
    public Integer getHorizontalControlNegative()
    {
        return keyboard.getHorizontalControlNegative();
    }

    @Override
    public Integer getVerticalControlPositive()
    {
        return keyboard.getVerticalControlPositive();
    }

    @Override
    public Integer getVerticalControlNegative()
    {
        return keyboard.getVerticalControlNegative();
    }

    @Override
    public double getHorizontalDirection()
    {
        return keyboard.getHorizontalDirection();
    }

    @Override
    public double getVerticalDirection()
    {
        return keyboard.getVerticalDirection();
    }

    @Override
    public void setFireButton(Integer index, Integer code)
    {
        fire.put(index, code);
    }

    @Override
    public boolean isUpButtonOnce()
    {
        return keyboard.isPressedOnce(keyboard.getVerticalControlPositive());
    }

    @Override
    public boolean isDownButtonOnce()
    {
        return keyboard.isPressedOnce(keyboard.getVerticalControlNegative());
    }

    @Override
    public boolean isLeftButtonOnce()
    {
        return keyboard.isPressedOnce(keyboard.getHorizontalControlNegative());
    }

    @Override
    public boolean isRightButtonOnce()
    {
        return keyboard.isPressedOnce(keyboard.getHorizontalControlPositive());
    }

    @Override
    public boolean isFireButton(Integer index)
    {
        final Integer code = fire.get(index);
        if (code != null)
        {
            return keyboard.isPressed(code);
        }
        return false;
    }

    @Override
    public boolean isFireButtonOnce(Integer index)
    {
        final Integer code = fire.get(index);
        if (code != null)
        {
            return keyboard.isPressedOnce(code);
        }
        return false;
    }
}
