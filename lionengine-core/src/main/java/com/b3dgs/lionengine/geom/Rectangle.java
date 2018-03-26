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

import com.b3dgs.lionengine.Shape;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Rectangle representation.
 */
public final class Rectangle implements Shape
{
    /** Min to string size. */
    private static final int MIN_LENGHT = 48;

    /** Horizontal coordinate. */
    private double x;
    /** Vertical coordinate. */
    private double y;
    /** Width. */
    private double width;
    /** Height. */
    private double height;

    /**
     * Create a blank rectangle.
     */
    public Rectangle()
    {
        this(0.0, 0.0, 0.0, 0.0);
    }

    /**
     * Create a rectangle.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param width The rectangle width.
     * @param height The rectangle height.
     */
    public Rectangle(double x, double y, double width, double height)
    {
        super();

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /**
     * Check if the rectangle intersects the other.
     * 
     * @param rectangle The rectangle to test with (can be <code>null</code>).
     * @return <code>true</code> if intersect, <code>false</code> else.
     */
    public boolean intersects(Rectangle rectangle)
    {
        if (rectangle == null)
        {
            return false;
        }
        return rectangle.getX() + rectangle.getWidthReal() > x
               && rectangle.getY() + rectangle.getHeightReal() > y
               && rectangle.getX() < x + width
               && rectangle.getY() < y + height;
    }

    /**
     * Check if the rectangle contains the other.
     * 
     * @param rectangle The rectangle to test with (can be <code>null</code>).
     * @return <code>true</code> if contains, <code>false</code> else.
     */
    public boolean contains(Rectangle rectangle)
    {
        if (rectangle == null)
        {
            return false;
        }
        return Double.compare(rectangle.getX(), x) >= 0
               && Double.compare(rectangle.getY(), y) >= 0
               && Double.compare(rectangle.getX() + rectangle.getWidthReal(), x + width) <= 0
               && Double.compare(rectangle.getY() + rectangle.getHeightReal(), y + height) <= 0;
    }

    /**
     * Check if the rectangle contains the point.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return <code>true</code> if contains, <code>false</code> else.
     */
    public boolean contains(double x, double y)
    {
        return Double.compare(x, this.x) >= 0
               && Double.compare(y, this.y) >= 0
               && Double.compare(x, this.x + width) <= 0
               && Double.compare(y, this.y + height) <= 0;
    }

    /**
     * Translate rectangle using specified vector.
     * 
     * @param vx The horizontal translation vector.
     * @param vy The vertical translation vector.
     */
    public void translate(double vx, double vy)
    {
        x += vx;
        y += vy;
    }

    /**
     * Rotate rectangle with specific angle.
     * 
     * @param angle The angle in degree.
     * @return The rotated encompassing rectangle.
     */
    public Rectangle rotate(double angle)
    {
        final double x2 = x + width;
        final double y2 = y;

        final double x3 = x2;
        final double y3 = y + height;

        final double x4 = x;
        final double y4 = y3;

        final double cx = x + width / 2.0;
        final double cy = y + height / 2.0;

        final double a = UtilMath.wrapDouble(angle, 0, 360);
        final double cos = UtilMath.cos(a);
        final double sin = UtilMath.sin(a);

        final double rx1 = cos * (x - cx) - sin * (y - cy) + cx;
        final double ry1 = sin * (x - cx) + cos * (y - cy) + cy;

        final double rx2 = cos * (x2 - cx) - sin * (y2 - cy) + cx;
        final double ry2 = sin * (x2 - cx) + cos * (y2 - cy) + cy;

        final double rx3 = cos * (x3 - cx) - sin * (y3 - cy) + cx;
        final double ry3 = sin * (x3 - cx) + cos * (y3 - cy) + cy;

        final double rx4 = cos * (x4 - cx) - sin * (y4 - cy) + cx;
        final double ry4 = sin * (x4 - cx) + cos * (y4 - cy) + cy;

        final double nx1 = Math.min(Math.min(Math.min(rx1, rx2), rx3), rx4);
        final double ny1 = Math.max(Math.max(Math.max(ry1, ry2), ry3), ry4);

        final double nx2 = Math.max(Math.max(Math.max(rx1, rx2), rx3), rx4);

        final double ny3 = Math.min(Math.min(Math.min(ry1, ry2), ry3), ry4);

        return new Rectangle(nx1, ny3, nx2 - nx1, ny1 - ny3);
    }

    /**
     * Sets the location and size.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param w The rectangle width.
     * @param h The rectangle height.
     */
    public void set(double x, double y, double w, double h)
    {
        this.x = x;
        this.y = y;
        width = w;
        height = h;
    }

    /**
     * Get the min horizontal location.
     * 
     * @return The min horizontal location.
     */
    public double getMinX()
    {
        return x;
    }

    /**
     * Get the min vertical location.
     * 
     * @return The min vertical location.
     */
    public double getMinY()
    {
        return y;
    }

    /**
     * Get the max horizontal location.
     * 
     * @return The max horizontal location.
     */
    public double getMaxX()
    {
        return x + width;
    }

    /**
     * Get the max vertical location.
     * 
     * @return The max vertical location.
     */
    public double getMaxY()
    {
        return y + height;
    }

    /**
     * Get the real width.
     * 
     * @return The real width.
     */
    public double getWidthReal()
    {
        return width;
    }

    /**
     * Get the real width.
     * 
     * @return The real width.
     */
    public double getHeightReal()
    {
        return height;
    }

    /*
     * Shape
     */

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
    public int getWidth()
    {
        return (int) width;
    }

    @Override
    public int getHeight()
    {
        return (int) height;
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
        if (obj == null || obj.getClass() != getClass())
        {
            return false;
        }
        final Rectangle other = (Rectangle) obj;
        final boolean sameSize = Double.compare(height, other.height) == 0 && Double.compare(width, other.width) == 0;
        final boolean sameCoord = Double.compare(x, other.x) == 0 && Double.compare(y, other.y) == 0;
        return sameSize && sameCoord;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGHT).append(getClass().getSimpleName())
                                            .append(" [x=")
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
