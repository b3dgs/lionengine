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
package com.b3dgs.lionengine.graphic;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents a gradient color.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
public final class ColorGradient
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
     * Create a gradient color.
     * 
     * @param x1 The first horizontal location.
     * @param y1 The first vertical location.
     * @param color1 The first color.
     * @param x2 The last horizontal location.
     * @param y2 The last vertical location.
     * @param color2 The last color.
     * @throws LionEngineException If <code>null</code> arguments.
     */
    public ColorGradient(int x1, int y1, ColorRgba color1, int x2, int y2, ColorRgba color2)
    {
        super();

        Check.notNull(color1);
        Check.notNull(color2);

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

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + color1.hashCode();
        result = prime * result + x1;
        result = prime * result + y1;
        result = prime * result + color2.hashCode();
        result = prime * result + x2;
        result = prime * result + y2;
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final ColorGradient other = (ColorGradient) object;
        return x1 == other.x1
               && x2 == other.x2
               && y1 == other.y1
               && y2 == other.y2
               && color1.equals(other.color1)
               && color2.equals(other.color2);
    }
}
