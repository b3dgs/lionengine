/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.core.android;

import android.view.MotionEvent;

import com.b3dgs.lionengine.core.Config;

/**
 * Mouse input implementation.
 */
public final class MouseAndroid implements Mouse
{
    /** Clicked flags. */
    private boolean click;
    /** Screen horizontal ratio. */
    private double xRatio;
    /** Screen vertical ratio. */
    private double yRatio;
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
    /** Last click number. */
    private int lastClick;
    /** Moved flag. */
    private boolean moved;

    /**
     * Internal constructor.
     */
    public MouseAndroid()
    {
        super();
        x = 0;
        y = 0;
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
    void updateCoord(MotionEvent event)
    {
        oldX = x;
        oldY = y;
        x = (int) event.getX();
        y = (int) event.getY();
        mx = x - oldX;
        my = y - oldY;
        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
                click = true;
                lastClick = 1;
                break;
            case MotionEvent.ACTION_UP:
                click = false;
                lastClick = 0;
                break;
            default:
                click = true;
                break;
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

    /*
     * Mouse
     */

    @Override
    public int getMouseClick()
    {
        return lastClick;
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

    @Override
    public int getOnWindowX()
    {
        return (int) (x / xRatio);
    }

    @Override
    public int getOnWindowY()
    {
        return (int) (y / yRatio);
    }

    /*
     * InputDevicePointer
     */

    @Override
    public int getX()
    {
        return (int) (x / xRatio);
    }

    @Override
    public int getY()
    {
        return (int) (y / yRatio);
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
        return this.click;
    }

    @Override
    public boolean hasClickedOnce(int click)
    {
        return this.click;
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
