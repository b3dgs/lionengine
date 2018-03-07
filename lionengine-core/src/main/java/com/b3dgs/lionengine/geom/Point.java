/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.geom;

/**
 * Represents a point using int precision.
 */
public class Point
{
    /** Point horizontal. */
    private int x;
    /** Point vertical. */
    private int y;

    /**
     * Create a point set at 0.
     */
    public Point()
    {
        this(0, 0);
    }

    /**
     * Create a point.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public Point(int x, int y)
    {
        super();

        this.x = x;
        this.y = y;
    }

    /**
     * Translate int using specified values.
     * 
     * @param vx The horizontal force.
     * @param vy The vertical force.
     */
    public void translate(int vx, int vy)
    {
        x += vx;
        y += vy;
    }

    /**
     * Set the new int.
     * 
     * @param x The new horizontal location.
     * @param y The new vertical location.
     */
    public void set(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Set the new horizontal location.
     * 
     * @param x The new horizontal location.
     */
    public void setX(int x)
    {
        this.x = x;
    }

    /**
     * Set the new vertical location.
     * 
     * @param y The new vertical location.
     */
    public void setY(int y)
    {
        this.y = y;
    }

    /**
     * Get the horizontal location.
     * 
     * @return The horizontal location.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Get the vertical location.
     * 
     * @return The vertical location.
     */
    public int getY()
    {
        return y;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || getClass() != object.getClass())
        {
            return false;
        }
        final Point other = (Point) object;
        return x == other.x && y == other.y;
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append(getClass().getSimpleName())
                                  .append(" [x=")
                                  .append(x)
                                  .append(", y=")
                                  .append(y)
                                  .append("]")
                                  .toString();
    }
}
