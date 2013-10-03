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

import java.awt.geom.Rectangle2D;

import com.b3dgs.lionengine.utility.UtilityMath;

/**
 * Polygon implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class PolygonImpl
        implements Polygon
{
    /** Polygon. */
    private final java.awt.Polygon polygon;

    /**
     * Constructor.
     */
    PolygonImpl()
    {
        polygon = new java.awt.Polygon();
    }

    /*
     * Polygon
     */

    @Override
    public void addPoint(int x, int y)
    {
        polygon.addPoint(x, y);
    }

    @Override
    public void reset()
    {
        polygon.reset();
    }

    @Override
    public Rectangle getRectangle()
    {
        final Rectangle2D bounds = polygon.getBounds2D();
        return UtilityMath.createRectangle(bounds.getX(), bounds.getY(), bounds.getWidth(), bounds.getHeight());
    }

    @Override
    public boolean intersects(Rectangle rectangle)
    {
        return polygon.intersects(((RectangleImpl) rectangle).getRectangle2D());
    }

    @Override
    public boolean contains(Rectangle rectangle)
    {
        return polygon.contains(((RectangleImpl) rectangle).getRectangle2D());
    }
}
