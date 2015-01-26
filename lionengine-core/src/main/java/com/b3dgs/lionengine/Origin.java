/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
        switch (this)
        {
            case TOP_LEFT:
                return x;
            case MIDDLE:
                return width > 0 ? x - width / 2 : x;
            case CENTER_TOP:
                return width > 0 ? x - width / 2 : x;
            case CENTER_BOTTOM:
                return width > 0 ? x - width / 2 : x;
            default:
                throw new LionEngineException(ERROR_ENUM, name());
        }
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
        switch (this)
        {
            case TOP_LEFT:
                return y;
            case MIDDLE:
                return height > 0 ? y - height / 2 : y;
            case CENTER_TOP:
                return y;
            case CENTER_BOTTOM:
                return y - height;
            default:
                throw new LionEngineException(ERROR_ENUM, name());
        }
    }
}
