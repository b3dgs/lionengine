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
package com.b3dgs.lionengine.core.android;

import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.io.android.Mouse;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Mouse input implementation.
 */
public final class MouseAndroid implements Mouse
{
    /** One click. */
    private static final int CLICK_1 = 0;
    /** Two click. */
    private static final int CLICK_2 = 1;
    /** Three click. */
    private static final int CLICK_3 = 2;
    /** No click move. */
    private static final int NO_CLICK_MOVE = -1;

    /** Clicks state. */
    private final boolean[] clicks = new boolean[CLICK_3 + 1];
    /** Clicks state. */
    private final boolean[] clicksOld = new boolean[clicks.length];
    /** Screen horizontal ratio. */
    private double xRatio;
    /** Screen vertical ratio. */
    private double yRatio;
    /** On screen monitor location x. */
    private final double[] x = new double[clicks.length];
    /** On screen monitor location y. */
    private final double[] y = new double[clicks.length];
    /** Move value x. */
    private double mx;
    /** Move value y. */
    private double my;
    /** Old location x. */
    private final double[] oldX = new double[clicks.length];
    /** Old location y. */
    private final double[] oldY = new double[clicks.length];
    /** Last click number. */
    private int lastClick;
    /** Last click move. */
    private int lastClickMove;
    /** Moved flag. */
    private boolean moved;

    /**
     * Internal constructor.
     */
    public MouseAndroid()
    {
        super();
    }

    /**
     * Get click dedicated to move.
     * 
     * @return The click move.
     */
    private int getClickMove()
    {
        final int clickMove;
        if (clicks[CLICK_1] && !clicks[CLICK_2])
        {
            clickMove = CLICK_1;
        }
        else if (clicks[CLICK_1] && clicks[CLICK_2])
        {
            clickMove = CLICK_2;
        }
        else
        {
            clickMove = NO_CLICK_MOVE;
        }
        return clickMove;
    }

    /**
     * Update click location on screen.
     * 
     * @param event The move event.
     */
    private void updateCoord(MotionEvent event)
    {
        for (int i = 0; i < clicks.length; i++)
        {
            final PointerCoords coord = new PointerCoords();
            final int index = event.findPointerIndex(i);
            if (index != -1)
            {
                event.getPointerCoords(index, coord);

                oldX[i] = x[i];
                oldY[i] = y[i];
                x[i] = coord.x;
                y[i] = coord.y;
                if (clicks[i] != clicksOld[i])
                {
                    oldX[i] = x[i];
                    oldY[i] = y[i];
                }
            }
        }
    }

    /**
     * Update click move on screen.
     */
    private void updateMove()
    {
        lastClickMove = Math.max(lastClickMove, getClickMove());
        if (lastClickMove > NO_CLICK_MOVE && clicks[lastClickMove])
        {
            if (Double.compare(oldX[lastClickMove], x[lastClickMove]) != 0
                || Double.compare(oldY[lastClickMove], y[lastClickMove]) != 0)
            {
                moved = true;
            }
            mx = (x[lastClickMove] - oldX[lastClickMove]) / xRatio;
            my = (y[lastClickMove] - oldY[lastClickMove]) / yRatio;
        }
    }

    /**
     * Update cursor from event.
     * 
     * @param event event consumed.
     */
    void updateEvent(MotionEvent event)
    {
        System.arraycopy(clicks, 0, clicksOld, 0, clicks.length);
        switch (event.getAction())
        {
            case MotionEvent.ACTION_UP:
                clicks[CLICK_1] = false;
                clicks[CLICK_2] = false;
                clicks[CLICK_3] = false;
                lastClick = 0;
                lastClickMove = NO_CLICK_MOVE;
                break;
            case MotionEvent.ACTION_POINTER_1_UP:
                clicks[CLICK_1] = false;
                lastClick--;
                break;
            case MotionEvent.ACTION_POINTER_2_UP:
                clicks[CLICK_2] = false;
                lastClick--;
                break;
            case MotionEvent.ACTION_POINTER_3_UP:
                clicks[CLICK_3] = false;
                lastClick--;
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_1_DOWN:
                clicks[CLICK_1] = true;
                lastClick++;
                break;
            case MotionEvent.ACTION_POINTER_2_DOWN:
                clicks[CLICK_2] = true;
                lastClick++;
                break;
            case MotionEvent.ACTION_POINTER_3_DOWN:
                clicks[CLICK_3] = true;
                lastClick++;
                break;
            default:
                break;
        }
        updateCoord(event);
        updateMove();
    }

    /**
     * Set the config.
     * 
     * @param width The screen width.
     * @param height The screen height.
     * @param config The config reference.
     */
    public void setConfig(int width, int height, Config config)
    {
        xRatio = width / (double) config.getSource().getWidth();
        yRatio = height / (double) config.getSource().getHeight();
    }

    /*
     * Mouse
     */

    @Override
    public int getOnScreenX()
    {
        return (int) x[0];
    }

    @Override
    public int getOnScreenY()
    {
        return (int) y[0];
    }

    @Override
    public int getOnWindowX()
    {
        return (int) (x[0] / xRatio);
    }

    @Override
    public int getOnWindowY()
    {
        return (int) (y[0] / yRatio);
    }

    /*
     * InputDevicePointer
     */

    @Override
    public int getX()
    {
        return (int) (x[0] / xRatio);
    }

    @Override
    public int getY()
    {
        return (int) (y[0] / yRatio);
    }

    @Override
    public int getMoveX()
    {
        return (int) mx;
    }

    @Override
    public int getMoveY()
    {
        return (int) my;
    }

    @Override
    public int getClick()
    {
        return lastClick;
    }

    @Override
    public boolean hasClicked(int click)
    {
        if (!UtilMath.isBetween(click, 1, clicks.length))
        {
            return false;
        }
        return clicks[click - 1];
    }

    @Override
    public boolean hasClickedOnce(int click)
    {
        if (!UtilMath.isBetween(click, 1, clicks.length))
        {
            return false;
        }
        return clicks[click - 1];
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
        // Nothing to do
    }
}
