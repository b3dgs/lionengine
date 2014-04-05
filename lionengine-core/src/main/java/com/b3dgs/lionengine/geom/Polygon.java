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

import java.util.List;

/**
 * Polygon interface.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Polygon
{
    /**
     * Add a point to the polygon.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    void addPoint(int x, int y);

    /**
     * Reset the polygon.
     */
    void reset();

    /**
     * Get the polygon rectangle bounds.
     * 
     * @return The polygon rectangle bounds.
     */
    Rectangle getRectangle();

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
     * Get the points.
     * 
     * @return The points.
     */
    List<Line> getPoints();
}
