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
 * Point implementation.
 */
final class PointImpl implements Point
{
    /** Point horizontal. */
    private int x;
    /** Point vertical. */
    private int y;

    /**
     * Internal constructor.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    PointImpl(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /*
     * Point
     */

    @Override
    public void translate(int vx, int vy)
    {
        x += vx;
        y += vy;
    }

    @Override
    public void set(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public void setX(int x)
    {
        this.x = x;
    }

    @Override
    public void setY(int y)
    {
        this.y = y;
    }

    @Override
    public int getX()
    {
        return x;
    }

    @Override
    public int getY()
    {
        return y;
    }

    /*
     * Object
     */

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
        {
            return true;
        }
        if (obj == null || getClass() != obj.getClass())
        {
            return false;
        }
        final PointImpl other = (PointImpl) obj;
        return x == other.x && y == other.y;
    }
}
