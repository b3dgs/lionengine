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
package com.b3dgs.lionengine;

/**
 * List of origin point types.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public enum Origin
{
    /** Top left origin point. */
    TOP_LEFT,
    /** Bottom left origin point. */
    BOTTOM_LEFT,
    /** Bottom right origin point. */
    BOTTOM_RIGHT,
    /** Center center origin point. */
    MIDDLE,
    /** Center bottom origin point. */
    CENTER_TOP,
    /** Center bottom origin point. */
    CENTER_BOTTOM,
    /** @deprecated Fail mock (for tests only). */
    @Deprecated
    FAIL;

    /**
     * Get the x relative to origin.
     * 
     * @param x The current horizontal value.
     * @param width The width size.
     * @return The x relative to origin value.
     */
    public double getX(double x, double width)
    {
        final double result;
        if (this == TOP_LEFT || this == BOTTOM_LEFT)
        {
            result = x;
        }
        else if (this == BOTTOM_RIGHT)
        {
            result = x - width;
        }
        else if (this == MIDDLE || this == CENTER_TOP || this == CENTER_BOTTOM)
        {
            if (width > 0)
            {
                result = x - width / 2.0;
            }
            else
            {
                result = x;
            }
        }
        else
        {
            throw new LionEngineException(this);
        }
        return result;
    }

    /**
     * Get the y relative to origin.
     * 
     * @param y The current vertical value.
     * @param height The height size.
     * @return The y relative to origin value.
     */
    public double getY(double y, double height)
    {
        final double result;
        if (this == TOP_LEFT || this == CENTER_TOP)
        {
            result = y;
        }
        else if (this == MIDDLE)
        {
            if (height > 0)
            {
                result = y - height / 2.0;
            }
            else
            {
                result = y;
            }
        }
        else if (this == BOTTOM_LEFT || this == BOTTOM_RIGHT || this == CENTER_BOTTOM)
        {
            result = y - height;
        }
        else
        {
            throw new LionEngineException(this);
        }
        return result;
    }
}
