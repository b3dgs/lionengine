/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * 
 * @param x The horizontal location.
 * @param y The vertical location.
 * @param width The area width.
 * @param height The area height.
 */
record AreaImpl(double x, double y, double width, double height) implements Area
{
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
}
