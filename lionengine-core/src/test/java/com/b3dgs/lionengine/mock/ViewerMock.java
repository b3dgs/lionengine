/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.mock;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Shape;
import com.b3dgs.lionengine.Viewer;

/**
 * Viewer mock.
 */
public class ViewerMock implements Viewer
{
    /**
     * Create mock.
     */
    public ViewerMock()
    {
        super();
    }

    /*
     * Viewer
     */

    @Override
    public double getX()
    {
        return 0;
    }

    @Override
    public double getY()
    {
        return 0;
    }

    @Override
    public int getWidth()
    {
        return 0;
    }

    @Override
    public int getHeight()
    {
        return 0;
    }

    @Override
    public void follow(Localizable localizable)
    {
        // Nothing to do
    }

    @Override
    public double getViewpointX(double x)
    {
        return x;
    }

    @Override
    public double getViewpointY(double y)
    {
        return y;
    }

    @Override
    public int getViewX()
    {
        return 0;
    }

    @Override
    public int getViewY()
    {
        return 0;
    }

    @Override
    public boolean isViewable(Shape shape, int marginX, int marginY)
    {
        return false;
    }
}
