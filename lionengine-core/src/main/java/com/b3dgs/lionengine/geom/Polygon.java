/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.b3dgs.lionengine.Constant;

/**
 * Polygon representation.
 */
public final class Polygon
{
    /** Minimum number of points. */
    private static final int MIN_POINTS = 4;

    /** Last computed bounds. */
    private final Rectangle bounds = new Rectangle();
    /** Horizontal coordinates. */
    private double[] xpoints = new double[MIN_POINTS];
    /** Vertical coordinates. */
    private double[] ypoints = new double[MIN_POINTS];
    /** Total number of points. */
    private int npoints;

    /**
     * Create a blank polygon.
     */
    public Polygon()
    {
        super();
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
        updateBounds();
    }

    /**
     * Reset the polygon.
     */
    public void reset()
    {
        xpoints = new double[MIN_POINTS];
        ypoints = new double[MIN_POINTS];
        npoints = 0;
        bounds.set(0.0, 0.0, 0.0, 0.0);
    }

    /**
     * Get the polygon area bounds.
     * 
     * @return The polygon area bounds.
     */
    public Area getArea()
    {
        return bounds;
    }

    /**
     * Check if the area intersects the other.
     * 
     * @param area The area to test with (can be <code>null</code>).
     * @return <code>true</code> if intersect, <code>false</code> else.
     */
    public boolean intersects(Area area)
    {
        return bounds.intersects(area);
    }

    /**
     * Check if the polygon contains the area.
     * 
     * @param area The area to test with (can be <code>null</code>).
     * @return <code>true</code> if contains, <code>false</code> else.
     */
    public boolean contains(Area area)
    {
        return bounds.contains(area);
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
     * Update the bounds.
     */
    private void updateBounds()
    {
        if (npoints >= MIN_POINTS)
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

            bounds.set(boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY);
        }
    }

    /*
     * Object
     */

    @Override
    public String toString()
    {
        final StringBuilder builder = new StringBuilder(Constant.HUNDRED).append(getClass().getSimpleName())
                                                                         .append(" [");
        for (int i = 0; i < npoints; i++)
        {
            builder.append(" p=").append(i + 1).append(" x=").append(xpoints[i]).append(", y=").append(ypoints[i]);
        }
        return builder.append("]").toString();
    }
}
