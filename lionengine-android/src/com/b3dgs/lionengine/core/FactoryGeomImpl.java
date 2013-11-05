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
 * Geometry factory implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class FactoryGeomImpl
        implements FactoryGeom
{
    /**
     * Constructor.
     */
    FactoryGeomImpl()
    {
        // Nothing to do
    }

    /*
     * FactoryGeom
     */

    @Override
    public Line createLine()
    {
        return new LineImpl(0, 0, 0, 0);
    }

    @Override
    public Line createLine(double x1, double y1, double x2, double y2)
    {
        return new LineImpl(x1, y1, x2, y2);
    }

    @Override
    public Polygon createPolygon()
    {
        return new PolygonImpl();
    }

    @Override
    public Rectangle createRectangle()
    {
        return new RectangleImpl(0, 0, 0, 0);
    }

    @Override
    public Rectangle createRectangle(double x, double y, double w, double h)
    {
        return new RectangleImpl(x, y, w, h);
    }

    @Override
    public Transform createTransform()
    {
        return new TransformImpl();
    }
}
