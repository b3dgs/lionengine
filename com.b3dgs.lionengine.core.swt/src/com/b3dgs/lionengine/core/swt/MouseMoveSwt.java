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
package com.b3dgs.lionengine.core.swt;

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
    private int mx;
    /** Move value y. */
    private int my;
    /** Old location x. */
    private int oldX = x;
    /** Old location y. */
    private int oldY = y;
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
    public int getMx()
    {
        return mx;
    }

    /**
     * Get the vertical movement.
     * 
     * @return The vertical movement.
     */
    public int getMy()
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

    /*
     * MouseMoveListener
     */

    @Override
    public void mouseMove(MouseEvent event)
    {
        moved = true;
        updateCoord(event);
    }
}
