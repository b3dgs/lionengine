/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core;

import android.graphics.Rect;

import com.b3dgs.lionengine.Rectangle;

/**
 * Rectangle implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class RectangleImpl
        implements Rectangle
{
    /** Rectangle 2D. */
    private final Rect rectangle2d;

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
        rectangle2d = new Rect((int) x, (int) y, (int) (x + w), (int) (y + h));
    }

    /**
     * Get the rectangle 2D.
     * 
     * @return The rectangle 2D.
     */
    Rect getRectangle2D()
    {
        return rectangle2d;
    }

    /*
     * Rectangle
     */

    @Override
    public boolean intersects(Rectangle rectangle)
    {
        return rectangle2d.intersect(((RectangleImpl) rectangle).rectangle2d);
    }

    @Override
    public boolean contains(Rectangle rectangle)
    {
        return rectangle2d.contains(((RectangleImpl) rectangle).rectangle2d);
    }

    @Override
    public boolean contains(int x, int y)

    {
        return rectangle2d.contains(x, y);
    }

    @Override
    public void set(double x, double y, double w, double h)
    {
        rectangle2d.set((int) x, (int) y, (int) (x + w), (int) (y + h));
    }

    @Override
    public double getX()
    {
        return rectangle2d.left;
    }

    @Override
    public double getY()
    {
        return rectangle2d.top;
    }

    @Override
    public double getMinX()
    {
        return rectangle2d.left;
    }

    @Override
    public double getMinY()
    {
        return rectangle2d.top;
    }

    @Override
    public double getMaxX()
    {
        return rectangle2d.right;
    }

    @Override
    public double getMaxY()
    {
        return rectangle2d.bottom;
    }

    @Override
    public double getWidth()
    {
        return rectangle2d.right - rectangle2d.left;
    }

    @Override
    public double getHeight()
    {
        return rectangle2d.top - rectangle2d.bottom;
    }
}
