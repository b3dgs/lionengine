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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;

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
     * @param point The original point.
     * @return The created point.
     * @throws LionEngineException If point is invalid.
     */
    public static Point createPoint(Point point)
    {
        Check.notNull(point);
        return new PointImpl(point.getX(), point.getY());
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
     * @param coord The original coord.
     * @return The created coord.
     * @throws LionEngineException If coord is invalid.
     */
    public static Coord createCoord(Coord coord)
    {
        Check.notNull(coord);
        return new CoordImpl(coord.getX(), coord.getY());
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
     * Get the intersection point of two lines.
     * 
     * @param l1 The first line.
     * @param l2 The second line.
     * @return The intersection point.
     */
    public static Coord intersection(Line l1, Line l2)
    {
        final int x1 = (int) l1.getX1();
        final int x2 = (int) l1.getX2();
        final int y1 = (int) l1.getY1();
        final int y2 = (int) l1.getY2();

        final int x3 = (int) l2.getX1();
        final int x4 = (int) l2.getX2();
        final int y3 = (int) l2.getY1();
        final int y4 = (int) l2.getY2();

        final int d = (x1 - x2) * (y3 - y4) - (y1 - y2) * (x3 - x4);
        if (0 == d)
        {
            return Geom.createCoord(0.0, 0.0);
        }

        final int xi = ((x3 - x4) * (x1 * y2 - y1 * x2) - (x1 - x2) * (x3 * y4 - y3 * x4)) / d;
        final int yi = ((y3 - y4) * (x1 * y2 - y1 * x2) - (y1 - y2) * (x3 * y4 - y3 * x4)) / d;

        return Geom.createCoord(xi, yi);
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
     * @param line The original line.
     * @return The created line.
     * @throws LionEngineException If line is invalid.
     */
    public static Line createLine(Line line)
    {
        Check.notNull(line);
        return new LineImpl(line.getX1(), line.getY1(), line.getX2(), line.getY2());
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
     * @param rectangle The original rectangle.
     * @return The created rectangle.
     * @throws LionEngineException If rectangle is invalid.
     */
    public static Rectangle createRectangle(Rectangle rectangle)
    {
        Check.notNull(rectangle);
        return new RectangleImpl(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
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
        throw new LionEngineException(LionEngineException.ERROR_PRIVATE_CONSTRUCTOR);
    }
}
