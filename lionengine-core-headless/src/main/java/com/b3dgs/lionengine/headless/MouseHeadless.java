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
package com.b3dgs.lionengine.headless;

/**
 * Mouse headless implementation.
 */
public final class MouseHeadless implements Mouse
{
    /** Left click. */
    public static final int LEFT = 0;
    /** Middle click. */
    public static final int MIDDLE = 1;
    /** Right click. */
    public static final int RIGHT = 2;
    /** Max buttons. */
    public static final int MAX_BUTTONS = 3;

    /** Clicks flags. */
    private final boolean[] clicks = new boolean[MAX_BUTTONS];
    /** Clicked flags. */
    private final boolean[] clicked = new boolean[MAX_BUTTONS];
    /** Last click number. */
    private int lastClick;
    /** On screen monitor location x. */
    private int x;
    /** On screen monitor location y. */
    private int y;
    /** Move value x. */
    private int mx;
    /** Move value y. */
    private int my;
    /** Old location x. */
    private int oldX;
    /** Old location y. */
    private int oldY;
    /** Moved flag. */
    private boolean moved;

    /**
     * Constructor.
     */
    public MouseHeadless()
    {
        super();
    }

    /**
     * Called on mouse pressed.
     * 
     * @param event The associated event.
     */
    public void mousePressed(MouseEvent event)
    {
        lastClick = event.getClick();
        clicks[lastClick] = true;
    }

    /**
     * Called on mouse released.
     * 
     * @param event The associated event.
     */
    public void mouseReleased(MouseEvent event)
    {
        lastClick = 0;

        final int button = event.getClick();
        clicks[button] = false;
        clicked[button] = false;
    }

    /**
     * Called on mouse moved.
     * 
     * @param event The associated event.
     */
    public void mouseMoved(MouseEvent event)
    {
        moved = true;
        updateCoord(event);
    }

    /**
     * Called on mouse dragged.
     * 
     * @param event The associated event.
     */
    public void mouseDragged(MouseEvent event)
    {
        moved = true;
        updateCoord(event);
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
        x = event.getX();
        y = event.getY();
        mx = x - oldX;
        my = y - oldY;
    }

    /*
     * Mouse
     */

    @Override
    public void doClick(int click)
    {
        if (click < clicks.length)
        {
            clicks[click] = true;
            lastClick = click;
        }
    }

    @Override
    public void doSetMouse(int x, int y)
    {
        this.x = x;
        this.y = y;
        oldX = x;
        oldY = y;
    }

    @Override
    public void doMoveMouse(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public void doClickAt(int click, int x, int y)
    {
        this.x = x;
        this.y = y;
        clicks[click] = true;
    }

    @Override
    public int getOnScreenX()
    {
        return x;
    }

    @Override
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
        return x;
    }

    @Override
    public int getY()
    {
        return y;
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
        if (click < clicks.length)
        {
            return clicks[click];
        }
        return false;
    }

    @Override
    public boolean hasClickedOnce(int click)
    {
        if (click < clicks.length && clicks[click] && !clicked[click])
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
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        mx = x - oldX;
        my = y - oldY;
        oldX = x;
        oldY = y;
    }
}
