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

import com.b3dgs.lionengine.Shape;

/**
 * Area representation.
 */
public interface Area extends Shape
{
    /**
     * Check if the area intersects the other.
     * 
     * @param area The area to test with (can be <code>null</code>).
     * @return <code>true</code> if intersect, <code>false</code> else.
     */
    boolean intersects(Area area);

    /**
     * Check if the area contains the other.
     * 
     * @param area The area to test with (can be <code>null</code>).
     * @return <code>true</code> if contains, <code>false</code> else.
     */
    boolean contains(Area area);

    /**
     * Check if the area contains the point.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return <code>true</code> if contains, <code>false</code> else.
     */
    boolean contains(double x, double y);

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
