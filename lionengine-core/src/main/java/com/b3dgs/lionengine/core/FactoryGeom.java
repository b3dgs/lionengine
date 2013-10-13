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

import com.b3dgs.lionengine.Line;
import com.b3dgs.lionengine.Polygon;
import com.b3dgs.lionengine.Rectangle;

/**
 * Represents the geometry factory.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
interface FactoryGeom
{
    /**
     * Create a line.
     * 
     * @return The created line.
     */
    Line createLine();

    /**
     * Create a line.
     * 
     * @param x1 The x coordinate of the start point.
     * @param y1 The y coordinate of the start point.
     * @param x2 The x coordinate of the end point.
     * @param y2 The y coordinate of the end point.
     * @return The created line.
     */
    Line createLine(double x1, double y1, double x2, double y2);

    /**
     * Create a polygon.
     * 
     * @return The created polygon.
     */
    Polygon createPolygon();

    /**
     * Create a rectangle.
     * 
     * @return The created rectangle.
     */
    Rectangle createRectangle();

    /**
     * Create a rectangle.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param w The rectangle width.
     * @param h The rectangle height.
     * @return The created rectangle.
     */
    Rectangle createRectangle(double x, double y, double w, double h);

    /**
     * Create a transform.
     * 
     * @return The created transform.
     */
    Transform createTransform();
}
