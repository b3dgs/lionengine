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
package com.b3dgs.lionengine.core;

import java.awt.AWTException;
import java.awt.HeadlessException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.LionEngineException;

/**
 * Represents the mouse input. Gives informations such as mouse click and cursor location.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Mouse
        implements InputDevicePointer, MouseListener, MouseMotionListener, MouseWheelListener
{
    /** Left click. */
    public static final int LEFT = MouseEvent.BUTTON1;
    /** Middle click. */
    public static final int MIDDLE = MouseEvent.BUTTON2;
    /** Right click. */
    public static final int RIGHT = MouseEvent.BUTTON3;
    /** Robot error. */
    private static final String ERROR_ROBOT = "No mouse robot available !";
    /** Button error. */
    private static final String ERROR_BUTTON = "Button out of range: ";

    /** Clicks flags. */
    private final boolean[] clicks;
    /** Clicked flags. */
    private final boolean[] clicked;
    /** Robot instance reference. */
    private final Robot robot;
    /** Screen horizontal ratio. */
    private double xRatio;
    /** Screen vertical ratio. */
    private double yRatio;
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
    private int oldX;
    /** Old location y. */
    private int oldY;
    /** Screen center x. */
    private int centerX;
    /** Screen center y. */
    private int centerY;
    /** Last click number. */
    private int lastClick;
    /** Moved flag. */
    private boolean moved;

    /**
     * Constructor.
     * 
     * @throws LionEngineException If environment is not compatible with mouse input.
     */
    Mouse() throws LionEngineException
    {
        super();
        try
        {
            final int mouseButtons = Math.max(0, MouseInfo.getNumberOfButtons()) + 1;
            clicks = new boolean[mouseButtons];
            clicked = new boolean[mouseButtons];
        }
        catch (final HeadlessException exception)
        {
            throw new LionEngineException(exception);
        }
        final PointerInfo a = MouseInfo.getPointerInfo();
        final Point b = a.getLocation();
        x = (int) b.getX();
        y = (int) b.getY();
        centerX = x;
        centerY = y;
        wx = 0;
        wy = 0;
        mx = 0;
        my = 0;
        oldX = x;
        oldY = y;
        Robot r = null;
        try
        {
            r = new Robot();
        }
        catch (final AWTException exception)
        {
            Verbose.critical(Mouse.class, "constructor", Mouse.ERROR_ROBOT);
        }
        robot = r;
    }

    /**
     * Update the mouse.
     */
    public void update()
    {
        mx = x - oldX;
        my = y - oldY;
        oldX = x;
        oldY = y;
    }

    /**
     * Lock mouse at its center.
     */
    public void lock()
    {
        lock(centerX, centerY);
    }

    /**
     * Lock mouse at specified location.
     * 
     * @param x The location x.
     * @param y The location y.
     */
    public void lock(int x, int y)
    {
        if (robot != null)
        {
            robot.mouseMove(x, y);
            this.x = centerX;
            this.y = centerY;
            oldX = centerX;
            oldY = centerY;
        }
    }

    /**
     * Perform a click.
     * 
     * @param click The click to perform.
     */
    public void doClick(int click)
    {
        if (robot != null)
        {
            int event;
            switch (click)
            {
                case Mouse.LEFT:
                    event = InputEvent.BUTTON1_MASK;
                    break;
                case Mouse.MIDDLE:
                    event = InputEvent.BUTTON2_MASK;
                    break;
                case Mouse.RIGHT:
                    event = InputEvent.BUTTON3_MASK;
                    break;
                default:
                    event = -1;
                    break;
            }
            if (event > -1)
            {
                robot.mousePress(event);
                robot.mouseRelease(event);
            }
        }
    }

    /**
     * Perform a click at specified coordinate.
     * 
     * @param click The click to perform.
     * @param x The location x.
     * @param y The location y.
     */
    public void doClickAt(int click, int x, int y)
    {
        if (robot != null)
        {
            robot.mouseMove(x, y);
            doClick(click);
        }
    }

    /**
     * Set the config.
     * 
     * @param config The config.
     */
    public void setConfig(Config config)
    {
        xRatio = config.getOutput().getWidth() / (double) config.getSource().getWidth();
        yRatio = config.getOutput().getHeight() / (double) config.getSource().getHeight();
    }

    /**
     * Set mouse center for lock operation.
     * 
     * @param x The location x.
     * @param y The location y.
     */
    public void setCenter(int x, int y)
    {
        centerX = x;
        centerY = y;
    }

    /**
     * Get location on screen x.
     * 
     * @return The location on screen x.
     */
    public int getOnScreenX()
    {
        return x;
    }

    /**
     * Get location on screen y.
     * 
     * @return The location on screen y.
     */
    public int getOnScreenY()
    {
        return y;
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

    /*
     * InputDevicePointer
     */

    @Override
    public int getX()
    {
        return (int) (wx / xRatio);
    }

    @Override
    public int getY()
    {
        return (int) (wy / yRatio);
    }

    @Override
    public int getMoveX()
    {
        return mx;
    }

    @Override
    public int getMoveY()
    {
        return my;
    }

    @Override
    public int getClick()
    {
        return lastClick;
    }

    @Override
    public boolean hasClicked(int click)
    {
        return clicks[click];
    }

    @Override
    public boolean hasClickedOnce(int click)
    {
        if (clicks[click] && !clicked[click])
        {
            clicked[click] = true;
            return true;
        }
        return false;
    }

    @Override
    public boolean hasMoved()
    {
        if (moved)
        {
            moved = false;
            return true;
        }
        return false;
    }

    /*
     * MouseListener
     */

    @Override
    public void mousePressed(MouseEvent event)
    {
        lastClick = event.getButton();
        try
        {
            clicks[lastClick] = true;
        }
        catch (final ArrayIndexOutOfBoundsException exception)
        {
            Verbose.warning(Mouse.class, "mouseReleased", Mouse.ERROR_BUTTON, String.valueOf(lastClick));
        }
    }

    @Override
    public void mouseReleased(MouseEvent event)
    {
        lastClick = 0;
        final int button = event.getButton();
        try
        {
            clicks[button] = false;
            clicked[button] = false;
        }
        catch (final ArrayIndexOutOfBoundsException exception)
        {
            Verbose.warning(Mouse.class, "mouseReleased", Mouse.ERROR_BUTTON, String.valueOf(button));
        }
    }

    @Override
    public void mouseClicked(MouseEvent event)
    {
        // Nothing to do
    }

    @Override
    public void mouseMoved(MouseEvent event)
    {
        moved = true;
        updateCoord(event);
    }

    @Override
    public void mouseDragged(MouseEvent event)
    {
        moved = true;
        updateCoord(event);
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent event)
    {
        // Nothing to do
    }

    @Override
    public void mouseEntered(MouseEvent event)
    {
        // Nothing to do
    }

    @Override
    public void mouseExited(MouseEvent event)
    {
        // Nothing to do
    }
}
