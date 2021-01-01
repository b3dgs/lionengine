/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.geom;

/**
 * Area representation.
 * <p>
 * This class is Thread-Safe.
 * </p>
 */
final class AreaImpl implements Area
{
    /** Min to string size. */
    private static final int MIN_LENGHT = 48;

    /** Horizontal coordinate. */
    private final double x;
    /** Vertical coordinate. */
    private final double y;
    /** Width. */
    private final double width;
    /** Height. */
    private final double height;

    /**
     * Create a area.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param width The area width.
     * @param height The area height.
     */
    AreaImpl(double x, double y, double width, double height)
    {
        super();

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    /*
     * Area
     */

    @Override
    public boolean intersects(Area area)
    {
        if (area == null)
        {
            return false;
        }
        return area.getX() + area.getWidthReal() > x
               && area.getY() + area.getHeightReal() > y
               && area.getX() < x + width
               && area.getY() < y + height;
    }

    @Override
    public boolean contains(Area area)
    {
        if (area == null)
        {
            return false;
        }
        final boolean outside = area.getX() < x
                                || area.getY() < y
                                || area.getX() + area.getWidthReal() > x + width
                                || area.getY() + area.getHeightReal() > y + height;
        return !outside;
    }

    @Override
    public boolean contains(double x, double y)
    {
        final boolean outside = x < this.x || y < this.y || x > this.x + width || y > this.y + height;
        return !outside;
    }

    @Override
    public double getWidthReal()
    {
        return width;
    }

    @Override
    public double getHeightReal()
    {
        return height;
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
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(width);
        result = prime * result + (int) (temp ^ temp >>> 32);
        temp = Double.doubleToLongBits(height);
        result = prime * result + (int) (temp ^ temp >>> 32);
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
        final AreaImpl other = (AreaImpl) object;
        return Double.compare(x, other.x) == 0
               && Double.compare(y, other.y) == 0
               && Double.compare(width, other.width) == 0
               && Double.compare(height, other.height) == 0;
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
