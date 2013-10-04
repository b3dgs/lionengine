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

import java.awt.geom.Rectangle2D;

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
    private final Rectangle2D rectangle2d;

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
        rectangle2d = new Rectangle2D.Double(x, y, w, h);
    }

    /**
     * Get the rectangle 2D.
     * 
     * @return The rectangle 2D.
     */
    Rectangle2D getRectangle2D()
    {
        return rectangle2d;
    }

    /*
     * Rectangle
     */

    @Override
    public boolean intersects(Rectangle rectangle)
    {
        return rectangle2d.intersects(((RectangleImpl) rectangle).rectangle2d);
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
        rectangle2d.setRect(x, y, w, h);
    }

    @Override
    public double getX()
    {
        return rectangle2d.getX();
    }

    @Override
    public double getY()
    {
        return rectangle2d.getY();
    }

    @Override
    public double getMinX()
    {
        return rectangle2d.getMinX();
    }

    @Override
    public double getMinY()
    {
        return rectangle2d.getMinY();
    }

    @Override
    public double getMaxX()
    {
        return rectangle2d.getMaxX();
    }

    @Override
    public double getMaxY()
    {
        return rectangle2d.getMaxY();
    }

    @Override
    public double getWidth()
    {
        return rectangle2d.getWidth();
    }

    @Override
    public double getHeight()
    {
        return rectangle2d.getHeight();
    }
}
