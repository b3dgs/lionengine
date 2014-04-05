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
package com.b3dgs.lionengine.geom;

/**
 * Line implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class LineImpl
        implements Line
{
    /** The X coordinate of the start point of the line segment. */
    private double x1;
    /** The Y coordinate of the start point of the line segment. */
    private double y1;
    /** The X coordinate of the end point of the line segment. */
    private double x2;
    /** The Y coordinate of the end point of the line segment. */
    private double y2;

    /**
     * Constructor.
     * 
     * @param x1 The x coordinate of the start point.
     * @param y1 The y coordinate of the start point.
     * @param x2 The x coordinate of the end point.
     * @param y2 The y coordinate of the end point.
     */
    LineImpl(double x1, double y1, double x2, double y2)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    /*
     * Line
     */

    @Override
    public void set(double x1, double y1, double x2, double y2)
    {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    @Override
    public double getX1()
    {
        return x1;
    }

    @Override
    public double getX2()
    {
        return x2;
    }

    @Override
    public double getY1()
    {
        return y1;
    }

    @Override
    public double getY2()
    {
        return y2;
    }
}
