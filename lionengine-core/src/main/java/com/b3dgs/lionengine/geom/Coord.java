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
 * Represents a coordinate using double precision.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Coord
{
    /**
     * Translate coordinate using specified values.
     * 
     * @param vx The horizontal force.
     * @param vy The vertical force.
     */
    void translate(double vx, double vy);

    /**
     * Set the new coordinate.
     * 
     * @param x The new horizontal location.
     * @param y The new vertical location.
     */
    void set(double x, double y);

    /**
     * Set the new horizontal location.
     * 
     * @param x The new horizontal location.
     */
    void setX(double x);

    /**
     * Set the new vertical location.
     * 
     * @param y The new vertical location.
     */
    void setY(double y);

    /**
     * Get the horizontal location.
     * 
     * @return The horizontal location.
     */
    double getX();

    /**
     * Get the vertical location.
     * 
     * @return The vertical location.
     */
    double getY();

}
