/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core;

/**
 * Represents a directional device, supporting multiple axis.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface InputDeviceDirectional
        extends InputDevice
{
    /**
     * Set the horizontal positive control code.
     * 
     * @param code The horizontal positive control code.
     */
    void setHorizontalControlPositive(Integer code);

    /**
     * Set the horizontal negative control code.
     * 
     * @param code The horizontal negative control code.
     */
    void setHorizontalControlNegative(Integer code);

    /**
     * Set the vertical positive control code.
     * 
     * @param code The vertical positive control code.
     */
    void setVerticalControlPositive(Integer code);

    /**
     * Set the vertical negative control code.
     * 
     * @param code The vertical negative control code.
     */
    void setVerticalControlNegative(Integer code);

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
