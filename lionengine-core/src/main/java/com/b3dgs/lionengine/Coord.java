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
 * Represents a coordinate using double precision.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class Coord
{
    /** Coordinate horizontal. */
    private double x;
    /** Coordinate vertical. */
    private double y;

    /**
     * Create a coordinate set to <code>(0.0, 0.0)</code> by default.
     */
    public Coord()
    {
        this(0.0, 0.0);
    }

    /**
     * Constructor.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public Coord(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Translate coordinate using specified values.
     * 
     * @param vx The horizontal force.
     * @param vy The vertical force.
     */
    public void translate(double vx, double vy)
    {
        x += vx;
        y += vy;
    }

    /**
     * Set the new coordinate.
     * 
     * @param x The new horizontal location.
     * @param y The new vertical location.
     */
    public void set(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Set the new horizontal location.
     * 
     * @param x The new horizontal location.
     */
    public void setX(double x)
    {
        this.x = x;
    }

    /**
     * Set the new vertical location.
     * 
     * @param y The new vertical location.
     */
    public void setY(double y)
    {
        this.y = y;
    }

    /**
     * Get the horizontal location.
     * 
     * @return The horizontal location.
     */
    public double getX()
    {
        return x;
    }

    /**
     * Get the vertical location.
     * 
     * @return The vertical location.
     */
    public double getY()
    {
        return y;
    }
}
