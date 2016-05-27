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

import com.b3dgs.lionengine.Shape;

/**
 * Rectangle interface.
 */
public interface Rectangle extends Shape
{
    /**
     * Check if the rectangle intersects the other.
     * 
     * @param rectangle The rectangle to test with.
     * @return <code>true</code> if intersect, <code>false</code> else.
     */
    boolean intersects(Rectangle rectangle);

    /**
     * Check if the rectangle contains the other.
     * 
     * @param rectangle The rectangle to test with.
     * @return <code>true</code> if contains, <code>false</code> else.
     */
    boolean contains(Rectangle rectangle);

    /**
     * Check if the rectangle contains the point.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return <code>true</code> if contains, <code>false</code> else.
     */
    boolean contains(double x, double y);

    /**
     * Translate rectangle using specified vector.
     * 
     * @param vx The horizontal translation vector.
     * @param vy The vertical translation vector.
     */
    void translate(double vx, double vy);

    /**
     * Sets the location and size.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param w The rectangle width.
     * @param h The rectangle height.
     */
    void set(double x, double y, double w, double h);

    /**
     * Get the min x location.
     * 
     * @return The min x location.
     */
    double getMinX();

    /**
     * Get the min y location.
     * 
     * @return The min y location.
     */
    double getMinY();

    /**
     * Get the max x location.
     * 
     * @return The max x location.
     */
    double getMaxX();

    /**
     * Get the max y location.
     * 
     * @return The max y location.
     */
    double getMaxY();

    /**
     * Get the real width.
     * 
     * @return The real width.
     */
    double getWidthReal();

    /**
     * Get the real width.
     * 
     * @return The real width.
     */
    double getHeightReal();
}
