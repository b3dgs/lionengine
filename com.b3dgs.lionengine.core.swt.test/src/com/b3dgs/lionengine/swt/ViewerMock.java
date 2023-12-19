/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.swt;

import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Shape;
import com.b3dgs.lionengine.Viewer;

/**
 * Viewer mock.
 */
public final class ViewerMock implements Viewer
{
    /** Viewer X. */
    private int x;
    /** Viewer Y. */
    private int y;

    /**
     * Create mock.
     */
    public ViewerMock()
    {
        super();
    }

    /**
     * Set the viewer location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public void set(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    /*
     * Viewer
     */

    @Override
    public double getX()
    {
        return x;
    }

    @Override
    public double getY()
    {
        return y;
    }

    @Override
    public int getWidth()
    {
        return 320;
    }

    @Override
    public int getHeight()
    {
        return 240;
    }

    @Override
    public double getViewpointX(double x)
    {
        return x - getX();
    }

    @Override
    public double getViewpointY(double y)
    {
        return getY() + getHeight() - y - getViewY();
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
    public int getScreenHeight()
    {
        return 240;
    }

    @Override
    public boolean isViewable(Localizable shape, int marginX, int marginY)
    {
        return false;
    }

    @Override
    public boolean isViewable(Shape shape, int marginX, int marginY)
    {
        return false;
    }
}
