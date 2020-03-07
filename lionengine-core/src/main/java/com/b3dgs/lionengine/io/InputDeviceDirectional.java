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
package com.b3dgs.lionengine.io;

import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents a directional device, supporting multiple axis.
 */
public interface InputDeviceDirectional extends InputDevice
{
    /**
     * Set the horizontal positive control code.
     * 
     * @param code The horizontal positive control code (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void setHorizontalControlPositive(Integer code);

    /**
     * Set the horizontal negative control code.
     * 
     * @param code The horizontal negative control code (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void setHorizontalControlNegative(Integer code);

    /**
     * Set the vertical positive control code.
     * 
     * @param code The vertical positive control code (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void setVerticalControlPositive(Integer code);

    /**
     * Set the vertical negative control code.
     * 
     * @param code The vertical negative control code (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    void setVerticalControlNegative(Integer code);

    /**
     * Get the horizontal positive control code.
     * 
     * @return The horizontal positive control code.
     */
    Integer getHorizontalControlPositive();

    /**
     * Get the horizontal negative control code.
     * 
     * @return The horizontal negative control code.
     */
    Integer getHorizontalControlNegative();

    /**
     * Get the vertical positive control code.
     * 
     * @return The vertical positive control code.
     */
    Integer getVerticalControlPositive();

    /**
     * Get the vertical negative control code.
     * 
     * @return The vertical negative control code.
     */
    Integer getVerticalControlNegative();

    /**
     * Get the horizontal direction.
     * 
     * @return <code>negative</code> if left, <code>positive</code> if right, 0 if none.
     */
    double getHorizontalDirection();

    /**
     * Get the horizontal direction.
     * 
     * @return <code>positive</code> if up, <code>negative</code> if down, 0 if none.
     */
    double getVerticalDirection();
}
