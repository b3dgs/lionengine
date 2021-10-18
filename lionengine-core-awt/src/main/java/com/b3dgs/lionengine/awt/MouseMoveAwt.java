/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.awt;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.geom.Point;

/**
 * Mouse implementation.
 */
final class MouseMoveAwt implements MouseMotionListener
{
    /** Robot margin size. */
    private static final int ROBOT_MARGIN = 64;
    /** Robot error. */
    private static final String ERROR_ROBOT = "No mouse robot available !";

    /**
     * Create a mouse robot.
     * 
     * @return The created robot, <code>null</code> if not available.
     */
    private static Robot createRobot()
    {
        try
        {
            return new Robot();
        }
        catch (final AWTException exception)
        {
            Verbose.exception(exception, ERROR_ROBOT);
            return null;
        }
    }

    /**
     * Get the buttons number.
     * 
     * @return The buttons number.
     */
    private static Point getCursorLocation()
    {
        try
        {
            final PointerInfo a = MouseInfo.getPointerInfo();
            final java.awt.Point b = a.getLocation();
            return new Point((int) b.getX(), (int) b.getY());
        }
        catch (final HeadlessException exception)
        {
            Verbose.exception(exception);
            return new Point();
        }
    }

    /** Robot instance reference (can be <code>null</code>). */
    private final Robot robot = createRobot();
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
    private double oldX;
    /** Old location y. */
    private double oldY;
    /** Screen center x. */
    private int centerX;
    /** Screen center y. */
    private int centerY;
    /** Moved flag. */
    private boolean moved;
    /** Lock flag. */
    private boolean lock;

    /**
     * Internal constructor.
     */
    MouseMoveAwt()
    {
        super();

        final Point point = getCursorLocation();
        x = point.getX();
        y = point.getY();
        centerX = x;
        centerY = y;
        oldX = x;
        oldY = y;
    }

    /**
     * Move mouse with robot.
     * 
     * @param nx The new X.
     * @param ny The new Y.
     */
    void robotMove(int nx, int ny)
    {
        oldX = x;
        oldY = y;
        x = nx;
        y = ny;
        wx = nx;
        wy = ny;
        mx = x - oldX;
        my = y - oldY;
        moved = true;
    }

    /**
     * Teleport mouse with robot.
     * 
     * @param nx The new X.
     * @param ny The new Y.
     */
    void robotTeleport(int nx, int ny)
    {
        oldX = nx;
        oldY = ny;
        x = nx;
        y = ny;
        wx = nx;
        wy = ny;
        mx = 0;
        my = 0;
        moved = false;
    }

    /**
     * Set the center.
     * 
     * @param x The horizontal center.
     * @param y The vertical center.
     */
    void setCenter(int x, int y)
    {
        centerX = x;
        centerY = y;
    }

    /**
     * Activate lock position at center.
     */
    void lock()
    {
        lock = true;
    }

    /**
     * Disable locking.
     */
    void unlock()
    {
        lock = false;
    }

    /**
     * Update movement record.
     */
    void update()
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
    int getX()
    {
        return x;
    }

    /**
     * Get the vertical location.
     * 
     * @return The vertical location.
     */
    int getY()
    {
        return y;
    }

    /**
     * Get the horizontal relative location.
     * 
     * @return The horizontal relative location.
     */
    int getWx()
    {
        return wx;
    }

    /**
     * Get the vertical relative location.
     * 
     * @return The vertical relative location.
     */
    int getWy()
    {
        return wy;
    }

    /**
     * Get the horizontal movement.
     * 
     * @return The horizontal movement.
     */
    double getMx()
    {
        return mx;
    }

    /**
     * Get the vertical movement.
     * 
     * @return The vertical movement.
     */
    double getMy()
    {
        return my;
    }

    /**
     * Get the horizontal center location.
     * 
     * @return The horizontal center location.
     */
    int getCx()
    {
        return centerX;
    }

    /**
     * Get the vertical center location.
     * 
     * @return The vertical center location.
     */
    int getCy()
    {
        return centerY;
    }

    /**
     * Check if has moved.
     * 
     * @return <code>true</code> if moved, <code>false</code> else.
     */
    boolean hasMoved()
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
        x = event.getXOnScreen();
        y = event.getYOnScreen();
        wx = event.getX();
        wy = event.getY();
        mx = x - oldX;
        my = y - oldY;
    }

    /**
     * Check if cursor is outside margin.
     * 
     * @return <code>true</code> if outside, <code>false</code> else.
     */
    private boolean isOutsideMargin()
    {
        return x < centerX - ROBOT_MARGIN
               || x > centerX + ROBOT_MARGIN
               || y < centerY - ROBOT_MARGIN
               || y > centerY + ROBOT_MARGIN;
    }

    /*
     * MouseListener
     */

    @Override
    public void mouseMoved(MouseEvent event)
    {
        moved = true;
        updateCoord(event);

        if (lock && isOutsideMargin())
        {
            if (robot != null)
            {
                robot.mouseMove(centerX, centerY);
                robotTeleport(centerX, centerY);
            }
            else
            {
                robotMove(centerX, centerY);
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent event)
    {
        moved = true;
        updateCoord(event);
    }
}
