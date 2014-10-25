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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

/**
 * Polygon implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class PolygonImpl
        implements Polygon
{
    /** Minimum number of points. */
    private static final int MIN = 4;

    /** The array of coordinates X. */
    private int[] xpoints;
    /** The array of coordinates Y. */
    private int[] ypoints;
    /** The total number of points. */
    private int npoints;
    /** The bounds. */
    private Rectangle bounds;

    /**
     * Internal constructor.
     */
    PolygonImpl()
    {
        xpoints = new int[MIN];
        ypoints = new int[MIN];
    }

    /**
     * Update the polygon bounds.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    private void updateBounds(int x, int y)
    {
        final double nw;
        final double nh;
        if (x < bounds.getX())
        {
            nw = bounds.getWidth() + (bounds.getX() - x);
        }
        else
        {
            nw = Math.max(bounds.getWidth(), x - bounds.getX());
        }

        if (y < bounds.getY())
        {
            nh = bounds.getHeight() + (bounds.getY() - y);
        }
        else
        {
            nh = Math.max(bounds.getHeight(), y - bounds.getY());
        }
        bounds.set(x, y, nw, nh);
    }

    /**
     * Calculate and create the bounds.
     * 
     * @param xpoints The horizontal points.
     * @param ypoints The vertical points.
     * @param npoints The points number.
     */
    private void calculateBounds(int[] xpoints, int[] ypoints, int npoints)
    {
        int boundsMinX = Integer.MAX_VALUE;
        int boundsMinY = Integer.MAX_VALUE;
        int boundsMaxX = Integer.MIN_VALUE;
        int boundsMaxY = Integer.MIN_VALUE;

        for (int i = 0; i < npoints; i++)
        {
            final int x = xpoints[i];
            boundsMinX = Math.min(boundsMinX, x);
            boundsMaxX = Math.max(boundsMaxX, x);
            final int y = ypoints[i];
            boundsMinY = Math.min(boundsMinY, y);
            boundsMaxY = Math.max(boundsMaxY, y);
        }
        bounds = Geom.createRectangle(boundsMinX, boundsMinY, boundsMaxX - boundsMinX, boundsMaxY - boundsMinY);
    }

    /*
     * Polygon
     */

    @Override
    public void addPoint(int x, int y)
    {
        if (npoints >= xpoints.length || npoints >= ypoints.length)
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

    @Override
    public void reset()
    {
        npoints = 0;
        bounds = null;
    }

    @Override
    public Rectangle getRectangle()
    {
        calculateBounds(xpoints, ypoints, npoints);
        return bounds;
    }

    @Override
    public boolean intersects(Rectangle rectangle)
    {
        return rectangle.intersects(bounds);
    }

    @Override
    public boolean contains(Rectangle rectangle)
    {
        return rectangle.contains(bounds);
    }

    @Override
    public Collection<Line> getPoints()
    {
        final Collection<Line> list = new ArrayList<>(npoints);
        for (int i = 0; i < npoints / 2; i++)
        {
            list.add(Geom.createLine(xpoints[i], ypoints[i], xpoints[i + npoints / 2], ypoints[i + npoints / 2]));
        }
        return list;
    }
}
