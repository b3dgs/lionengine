/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic;

import com.b3dgs.lionengine.Origin;
import com.b3dgs.lionengine.Viewer;

/**
 * Mock graphic.
 */
public class GraphicMock implements Graphic
{
    /** Color value. */
    private ColorRgba color = ColorRgba.WHITE;
    /** Graphic used. */
    private Object g;

    /**
     * Create mock.
     */
    public GraphicMock()
    {
        super();
    }

    /*
     * Graphic
     */

    @Override
    public void clear(int x, int y, int width, int height)
    {
        // Mock
    }

    @Override
    public void dispose()
    {
        // Mock
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy)
    {
        // Mock
    }

    @Override
    public void drawImage(ImageSurface image, int x, int y)
    {
        // Mock
    }

    @Override
    public void drawImage(ImageSurface image, Transform op, int x, int y)
    {
        // Mock
    }

    @Override
    public void drawImage(ImageSurface image, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2)
    {
        // Mock
    }

    @Override
    public void drawRect(int x, int y, int width, int height, boolean fill)
    {
        // Mock
    }

    @Override
    public void drawRect(Viewer viewer, Origin origin, double x, double y, int width, int height, boolean fill)
    {
        // Mock
    }

    @Override
    public void drawGradient(int x, int y, int width, int height)
    {
        // Mock
    }

    @Override
    public void drawGradient(Viewer viewer, Origin origin, double x, double y, int width, int height)
    {
        // Mock
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2)
    {
        // Mock
    }

    @Override
    public void drawLine(Viewer viewer, double x1, double y1, double x2, double y2)
    {
        // Mock
    }

    @Override
    public void drawOval(int x, int y, int width, int height, boolean fill)
    {
        // Mock
    }

    @Override
    public void drawOval(Viewer viewer, Origin origin, double x, double y, int width, int height, boolean fill)
    {
        // Mock
    }

    @Override
    public void setColor(ColorRgba color)
    {
        this.color = color;
    }

    @Override
    public void setColorGradient(ColorGradient gradientColor)
    {
        // Mock
    }

    @Override
    public void setGraphic(Object graphic)
    {
        g = graphic;
    }

    @Override
    public Object getGraphic()
    {
        return g;
    }

    @Override
    public ColorRgba getColor()
    {
        return color;
    }
}
