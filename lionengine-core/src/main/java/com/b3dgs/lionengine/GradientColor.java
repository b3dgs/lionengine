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
 * Represents a gradient color.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class GradientColor
{
    /** First horizontal point. */
    private final int x1;
    /** First vertical point. */
    private final int y1;
    /** First color. */
    private final ColorRgba color1;
    /** Last horizontal point. */
    private final int x2;
    /** Last vertical point. */
    private final int y2;
    /** Last color. */
    private final ColorRgba color2;

    /**
     * Constructor.
     * 
     * @param x1 The first horizontal location.
     * @param y1 The first vertical location.
     * @param color1 The first color.
     * @param x2 The last horizontal location.
     * @param y2 The last vertical location.
     * @param color2 The last color.
     */
    public GradientColor(int x1, int y1, ColorRgba color1, int x2, int y2, ColorRgba color2)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.color1 = color1;
        this.x2 = x2;
        this.y2 = y2;
        this.color2 = color2;
    }

    /**
     * Get the first horizontal location.
     * 
     * @return The first horizontal location.
     */
    public int getX1()
    {
        return x1;
    }

    /**
     * Get the first vertical location.
     * 
     * @return The first vertical location.
     */
    public int getY1()
    {
        return y1;
    }

    /**
     * Get the first color.
     * 
     * @return The first color.
     */
    public ColorRgba getColor1()
    {
        return color1;
    }

    /**
     * Get the last horizontal location.
     * 
     * @return The last horizontal location.
     */
    public int getX2()
    {
        return x2;
    }

    /**
     * Get the last vertical location.
     * 
     * @return The last vertical location.
     */
    public int getY2()
    {
        return y2;
    }

    /**
     * Get the last color.
     * 
     * @return The last color.
     */
    public ColorRgba getColor2()
    {
        return color2;
    }
}
