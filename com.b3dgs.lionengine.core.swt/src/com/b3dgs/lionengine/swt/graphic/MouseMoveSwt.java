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
package com.b3dgs.lionengine.swt.graphic;

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;

/**
 * Mouse input implementation.
 */
public final class MouseMoveSwt implements MouseMoveListener
{
    /** On screen monitor location x. */
    private int x;
    /** On screen monitor location y. */
    private int y;
    /** On local window location x. */
    private int wx;
    /** On local window location y. */
    private int wy;
    /** Move value x. */
    private double mx;
    /** Move value y. */
    private double my;
    /** Old location x. */
    private double oldX = x;
    /** Old location y. */
    private double oldY = y;
    /** Moved flag. */
    private boolean moved;

    /**
     * Constructor.
     */
    public MouseMoveSwt()
    {
        super();
    }

    /**
     * Update movement record.
     */
    public void update()
    {
        mx = x - oldX;
        my = y - oldY;
        oldX = x;
        oldY = y;
    }

    /**
     * Get the horizontal location.
     * 
     * @return The horizontal location.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Get the vertical location.
     * 
     * @return The vertical location.
     */
    public int getY()
    {
        return y;
    }

    /**
     * Get the horizontal relative location.
     * 
     * @return The horizontal relative location.
     */
    public int getWx()
    {
        return wx;
    }

    /**
     * Get the vertical relative location.
     * 
     * @return The vertical relative location.
     */
    public int getWy()
    {
        return wy;
    }

    /**
     * Get the horizontal movement.
     * 
     * @return The horizontal movement.
     */
    public double getMx()
    {
        return mx;
    }

    /**
     * Get the vertical movement.
     * 
     * @return The vertical movement.
     */
    public double getMy()
    {
        return my;
    }

    /**
     * Check if has moved.
     * 
     * @return <code>true</code> if moved, <code>false</code> else.
     */
    public boolean hasMoved()
    {
        if (moved)
        {
            moved = false;
            return true;
        }
        return false;
    }

    /**
     * Update coordinate from event.
     * 
     * @param event event consumed.
     */
    private void updateCoord(MouseEvent event)
    {
        oldX = x;
        oldY = y;
        x = event.x;
        y = event.y;
        wx = event.x;
        wy = event.y;
        mx = x - oldX;
        my = y - oldY;
    }

    @Override
    public void mouseMove(MouseEvent event)
    {
        moved = true;
        updateCoord(event);
    }
}
