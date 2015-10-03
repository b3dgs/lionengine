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
package com.b3dgs.lionengine.geom;

/**
 * Coordinate implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class CoordImpl implements Coord
{
    /** Coordinate horizontal. */
    private double x;
    /** Coordinate vertical. */
    private double y;

    /**
     * Internal constructor.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    CoordImpl(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /*
     * Coord
     */

    @Override
    public void translate(double vx, double vy)
    {
        x += vx;
        y += vy;
    }

    @Override
    public void set(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public void setX(double x)
    {
        this.x = x;
    }

    @Override
    public void setY(double y)
    {
        this.y = y;
    }

    @Override
    public double getX()
    {
        return x;
    }

    @Override
    public double getY()
    {
        return y;
    }
}
