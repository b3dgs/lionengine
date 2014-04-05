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
 * Geometry factory.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Geom
{
    /**
     * Create a point.
     * 
     * @return The created point.
     */
    public static Point createPoint()
    {
        return new PointImpl(0, 0);
    }

    /**
     * Create a point.
     * 
     * @param x The x coordinate of the point.
     * @param y The y coordinate of the point.
     * @return The created point.
     */
    public static Point createPoint(int x, int y)
    {
        return new PointImpl(x, y);
    }

    /**
     * Create a coord.
     * 
     * @return The created coord.
     */
    public static Coord createCoord()
    {
        return new CoordImpl(0.0, 0.0);
    }

    /**
     * Create a coord.
     * 
     * @param x The x coordinate.
     * @param y The y coordinate.
     * @return The created coord.
     */
    public static Coord createCoord(double x, double y)
    {
        return new CoordImpl(x, y);
    }

    /**
     * Create a line.
     * 
     * @return The created line.
     */
    public static Line createLine()
    {
        return new LineImpl(0, 0, 0, 0);
    }

    /**
     * Create a line.
     * 
     * @param x1 The x coordinate of the start point.
     * @param y1 The y coordinate of the start point.
     * @param x2 The x coordinate of the end point.
     * @param y2 The y coordinate of the end point.
     * @return The created line.
     */
    public static Line createLine(double x1, double y1, double x2, double y2)
    {
        return new LineImpl(x1, y1, x2, y2);
    }

    /**
     * Create a rectangle.
     * 
     * @return The created rectangle.
     */
    public static Rectangle createRectangle()
    {
        return new RectangleImpl(0, 0, 0, 0);
    }

    /**
     * Create a rectangle.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param w The rectangle width.
     * @param h The rectangle height.
     * @return The created rectangle.
     */
    public static Rectangle createRectangle(double x, double y, double w, double h)
    {
        return new RectangleImpl(x, y, w, h);
    }

    /**
     * Create a polygon.
     * 
     * @return The created polygon.
     */
    public static Polygon createPolygon()
    {
        return new PolygonImpl();
    }

    /**
     * Private constructor.
     */
    private Geom()
    {
        throw new RuntimeException();
    }
}
