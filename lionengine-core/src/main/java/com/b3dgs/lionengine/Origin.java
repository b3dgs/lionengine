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
package com.b3dgs.lionengine;

/**
 * List of origin point types.
 * <p>
 * This class is Thread-Safe.
 * </p>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public enum Origin
{
    /** Top left origin point. */
    TOP_LEFT,
    /** Center center origin point. */
    MIDDLE,
    /** Center bottom origin point. */
    CENTER_TOP,
    /** Center bottom origin point. */
    CENTER_BOTTOM;

    /** Unsupported enum. */
    private static final String ERROR_ENUM = "Unknown enum type: ";

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
        switch (this)
        {
            case TOP_LEFT:
                result = x;
                break;
            case MIDDLE:
            case CENTER_TOP:
            case CENTER_BOTTOM:
                if (width > 0)
                {
                    result = x - width / 2.0;
                }
                else
                {
                    result = x;
                }
                break;
            default:
                throw new LionEngineException(ERROR_ENUM, name());
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
        switch (this)
        {
            case TOP_LEFT:
                result = y;
                break;
            case MIDDLE:
                if (height > 0)
                {
                    result = y - height / 2.0;
                }
                else
                {
                    result = y;
                }
                break;
            case CENTER_TOP:
                result = y;
                break;
            case CENTER_BOTTOM:
                result = y - height;
                break;
            default:
                throw new LionEngineException(ERROR_ENUM, name());
        }
        return result;
    }
}
