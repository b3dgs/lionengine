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
 * Rectangle implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class RectangleImpl
        implements Rectangle
{
    /** The coordinate X. */
    private double x;
    /** The coordinate Y. */
    private double y;
    /** The width . */
    private double width;
    /** The height. */
    private double height;

    /**
     * Constructor.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param w The rectangle width.
     * @param h The rectangle height.
     */
    RectangleImpl(double x, double y, double w, double h)
    {
        this.x = x;
        this.y = y;
        width = w;
        height = h;
    }

    /*
     * Rectangle
     */

    @Override
    public boolean intersects(Rectangle rectangle)
    {
        if (rectangle == null)
        {
            return false;
        }
        return rectangle.getX() + rectangle.getWidth() > x && rectangle.getY() + rectangle.getHeight() > y
                && rectangle.getX() < x + width && rectangle.getY() < y + height;
    }

    @Override
    public boolean contains(Rectangle rectangle)
    {
        if (rectangle == null)
        {
            return false;
        }
        return rectangle.getX() >= x && rectangle.getY() >= y && rectangle.getX() + rectangle.getWidth() <= x + width
                && rectangle.getY() + rectangle.getHeight() <= y + height;
    }

    @Override
    public boolean contains(int x, int y)
    {
        return x >= this.x && y >= this.y && x < this.x + width && y < this.y + height;
    }

    @Override
    public void set(double x, double y, double w, double h)
    {
        this.x = x;
        this.y = y;
        width = w;
        height = h;
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

    @Override
    public double getMinX()
    {
        return x;
    }

    @Override
    public double getMinY()
    {
        return y;
    }

    @Override
    public double getMaxX()
    {
        return x + width;
    }

    @Override
    public double getMaxY()
    {
        return y + height;
    }

    @Override
    public double getWidth()
    {
        return width;
    }

    @Override
    public double getHeight()
    {
        return height;
    }
}
