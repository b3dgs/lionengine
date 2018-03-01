/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Polygon interface.
 */
public class Polygon
{
    /** Minimum number of points. */
    private static final int MIN = 4;

    /**
     * Calculate and create the bounds.
     * 
     * @param xpoints The horizontal points.
     * @param ypoints The vertical points.
     * @param npoints The points number.
     * @return The calculated bounds.
     */
    private static Rectangle calculateBounds(double[] xpoints, double[] ypoints, int npoints)
    {
        double boundsMinX = Double.MAX_VALUE;
        double boundsMinY = Double.MAX_VALUE;
        double boundsMaxX = -Double.MAX_VALUE;
        double boundsMaxY = -Double.MAX_VALUE;

        for (int i = 0; i < npoints; i++)
        {
            final double x = xpoints[i];
            boundsMinX = Math.min(boundsMinX, x);
            boundsMaxX = Math.max(boundsMaxX, x);

            final double y = ypoints[i];
            boundsMinY = Math.min(boundsMinY, y);
            boundsMaxY = Math.max(boundsMaxY, y);
        }

        return new Rectangle(boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY);
    }

    /** The array of coordinates X. */
    private double[] xpoints;
    /** The array of coordinates Y. */
    private double[] ypoints;
    /** The total number of points. */
    private int npoints;
    /** The bounds. */
    private Rectangle bounds;

    /**
     * Create a blank polygon.
     */
    public Polygon()
    {
        xpoints = new double[MIN];
        ypoints = new double[MIN];
    }

    /**
     * Add a point to the polygon.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void addPoint(double x, double y)
    {
        if (npoints >= xpoints.length)
        {
            final int newLength = npoints * 2;
            xpoints = Arrays.copyOf(xpoints, newLength);
            ypoints = Arrays.copyOf(ypoints, newLength);
        }
        xpoints[npoints] = x;
        ypoints[npoints] = y;
        npoints++;
        if (bounds != null)
        {
            updateBounds(x, y);
        }
    }

    /**
     * Reset the polygon.
     */
    public void reset()
    {
        npoints = 0;
        bounds = null;
    }

    /**
     * Get the polygon rectangle bounds.
     * 
     * @return The polygon rectangle bounds.
     */
    public Rectangle getRectangle()
    {
        bounds = calculateBounds(xpoints, ypoints, npoints);
        return bounds;
    }

    /**
     * Check if the rectangle intersects the other.
     * 
     * @param rectangle The rectangle to test with (can be <code>null</code>).
     * @return <code>true</code> if intersect, <code>false</code> else.
     */
    public boolean intersects(Rectangle rectangle)
    {
        return rectangle != null && rectangle.intersects(bounds);
    }

    /**
     * Check if the rectangle contains the other.
     * 
     * @param rectangle The rectangle to test with (can be <code>null</code>).
     * @return <code>true</code> if contains, <code>false</code> else.
     */
    public boolean contains(Rectangle rectangle)
    {
        return rectangle != null && rectangle.contains(bounds);
    }

    /**
     * Get the points.
     * 
     * @return The points.
     */
    public Collection<Line> getPoints()
    {
        final Collection<Line> list = new ArrayList<>(npoints);
        for (int i = 0; i < npoints / 2; i++)
        {
            list.add(new Line(xpoints[i], ypoints[i], xpoints[i + npoints / 2], ypoints[i + npoints / 2]));
        }
        return list;
    }

    /**
     * Update the polygon bounds.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    private void updateBounds(double x, double y)
    {
        final double nw;
        final double nh;
        if (x < bounds.getX())
        {
            nw = bounds.getWidthReal() + (bounds.getX() - x);
        }
        else
        {
            nw = Math.max(bounds.getWidthReal(), x - bounds.getX());
        }

        if (y < bounds.getY())
        {
            nh = bounds.getHeightReal() + (bounds.getY() - y);
        }
        else
        {
            nh = Math.max(bounds.getHeightReal(), y - bounds.getY());
        }
        bounds.set(x, y, nw, nh);
    }
}
