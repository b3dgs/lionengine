/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 */
final class RectangleImpl implements Rectangle
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
     * Internal constructor.
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
        return rectangle.getX() + rectangle.getWidth() > x
               && rectangle.getY() + rectangle.getHeight() > y
               && rectangle.getX() < x + width
               && rectangle.getY() < y + height;
    }

    @Override
    public boolean contains(Rectangle rectangle)
    {
        if (rectangle == null)
        {
            return false;
        }
        return rectangle.getX() >= x
               && rectangle.getY() >= y
               && rectangle.getX() + rectangle.getWidth() <= x + width
               && rectangle.getY() + rectangle.getHeight() <= y + height;
    }

    @Override
    public boolean contains(double x, double y)
    {
        return x >= this.x && y >= this.y && x <= this.x + width && y <= this.y + height;
    }

    @Override
    public void translate(double vx, double vy)
    {
        x += vx;
        y += vy;
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

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(height);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(width);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ temp >>> 32);
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (!(obj instanceof RectangleImpl))
        {
            return false;
        }
        final RectangleImpl other = (RectangleImpl) obj;
        final boolean sameSize = Double.doubleToLongBits(height) == Double.doubleToLongBits(other.height)
                                 && Double.doubleToLongBits(width) == Double.doubleToLongBits(other.width);
        final boolean sameCoord = Double.doubleToLongBits(x) == Double.doubleToLongBits(other.x)
                                  && Double.doubleToLongBits(y) == Double.doubleToLongBits(other.y);
        return sameSize && sameCoord;
    }

    @Override
    public String toString()
    {
        return new StringBuilder().append("Rectangle [x=")
                                  .append(x)
                                  .append(", y=")
                                  .append(y)
                                  .append(", width=")
                                  .append(width)
                                  .append(", height=")
                                  .append(height)
                                  .append("]")
                                  .toString();
    }
}
