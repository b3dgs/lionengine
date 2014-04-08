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

import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseWheelListener;
import org.eclipse.swt.widgets.Display;

/**
 * Mouse input implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public final class Mouse
        implements InputDevicePointer, MouseListener, MouseMoveListener, MouseWheelListener
{
    /** Left click. */
    public static final int LEFT = 1;
    /** Middle click. */
    public static final int MIDDLE = 2;
    /** Right click. */
    public static final int RIGHT = 3;

    /** Clicks flags. */
    private final boolean[] clicks;
    /** Clicked flags. */
    private final boolean[] clicked;
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
    /** Last click number. */
    private int lastClick;
    /** Moved flag. */
    private boolean moved;

    /**
     * Constructor.
     * 
     * @param display The display reference.
     */
    Mouse(Display display)
    {
        super();
        final int mouseButtons = Math.max(0, 9) + 1;
        clicks = new boolean[mouseButtons];
        clicked = new boolean[mouseButtons];
        display.getCursorLocation();
        x = 0;
        y = 0;
        wx = 0;
        wy = 0;
        mx = 0;
        my = 0;
        oldX = x;
        oldY = y;
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
    public void mouseScrolled(MouseEvent event)
    {
        // Nothing to do
    }

    @Override
    public void mouseDoubleClick(MouseEvent event)
    {
        // Nothing to do
    }

    @Override
    public void mouseDown(MouseEvent event)
    {
        lastClick = event.button;
        try
        {
            clicks[lastClick] = true;
        }
        catch (final ArrayIndexOutOfBoundsException exception)
        {
            Verbose.warning(Mouse.class, "mouseReleased", "Button out of range: ", String.valueOf(lastClick));
        }
    }

    @Override
    public void mouseUp(MouseEvent event)
    {
        lastClick = 0;
        final int button = event.button;
        try
        {
            clicks[button] = false;
            clicked[button] = false;
        }
        catch (final ArrayIndexOutOfBoundsException exception)
        {
            Verbose.warning(Mouse.class, "mouseReleased", "Button out of range: ", String.valueOf(button));
        }
    }

    @Override
    public void mouseMove(MouseEvent event)
    {
        moved = true;
        updateCoord(event);
    }
}
