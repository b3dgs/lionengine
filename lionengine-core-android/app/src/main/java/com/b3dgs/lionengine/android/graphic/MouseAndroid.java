/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 *
 * This program is free software: you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
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
package com.b3dgs.lionengine.android.graphic;

import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;

import com.b3dgs.lionengine.InputDevice;
import com.b3dgs.lionengine.InputDeviceListener;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.android.Mouse;

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

    /** Push listener. */
    private final ListenableModel<InputDeviceListener> listeners = new ListenableModel<>();
    private final Integer index = Integer.valueOf(0);
    /** Clicks state. */
    private final boolean[] clicks = new boolean[CLICK_3 + 1];
    /** Clicks state. */
    private final boolean[] clicked = new boolean[clicks.length];
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
    private boolean click1;
    private boolean click2;

    /**
     * Internal constructor.
     */
    MouseAndroid()
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
        else if (clicks[CLICK_1])
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
        lastClickMove = getClickMove();
        if (lastClickMove > NO_CLICK_MOVE)
        {
            if (Double.compare(oldX[lastClickMove], x[lastClickMove]) != 0
                || Double.compare(oldY[lastClickMove], y[lastClickMove]) != 0)
            {
                // moved = true;
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
        int id = event.getActionIndex();
        final int n = listeners.size();
        switch (event.getActionMasked())
        {
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                if (id == 0 && click1)
                {
                    click1 = false;
                    mx = 0;
                    my = 0;
                }
                else if (id == 1 && click2 || id == 0 && click2)
                {
                    click2 = false;
                    id = 1;
                }
                clicks[id] = false;
                clicked[id] = false;
                lastClick--;

                for (int i = 0; i < n; i++)
                {
                    listeners.get(i).onDeviceChanged(index, id, (char) id, false);
                }
                break;
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                clicks[id] = true;
                if (id == 0)
                {
                    click1 = true;
                }
                else if (id == 1)
                {
                    click2 = true;
                }
                lastClick++;

                for (int i = 0; i < n; i++)
                {
                    listeners.get(i).onDeviceChanged(index, id, (char) id, true);
                }
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
     * @param source The source reference.
     */
    void setConfig(int width, int height, Resolution source)
    {
        xRatio = width / (double) source.getWidth();
        yRatio = height / (double) source.getHeight();
    }

    /*
     * Mouse
     */

    @Override
    public void addListener(InputDeviceListener listener)
    {
        listeners.addListener(listener);
    }

    @Override
    public void removeListener(InputDeviceListener listener)
    {
        listeners.removeListener(listener);
    }

    @Override
    public InputDevice getCurrent(int id)
    {
        return this;
    }

    @Override
    public void setVisible(boolean visible)
    {
        // Void
    }

    @Override
    public void lock(int x, int y)
    {
        // Nothing to do
    }

    @Override
    public void unlock()
    {
        // Nothing to do
    }

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
    public double getX()
    {
        return (x[0] / xRatio);
    }

    @Override
    public double getY()
    {
        return (y[0] / yRatio);
    }

    @Override
    public int getX(int click)
    {
        return (int) (x[click - 1] / xRatio);
    }

    @Override
    public int getY(int click)
    {
        return (int) (y[click - 1] / yRatio);
    }

    @Override
    public double getMoveX()
    {
        return mx;
    }

    @Override
    public double getMoveY()
    {
        return my;
    }

    @Override
    public Integer getPushed()
    {
        return lastClick;
    }

    @Override
    public boolean isPushed(Integer click)
    {
        return UtilMath.isBetween(click, 1, clicks.length) && clicks[click - 1];
    }

    @Override
    public boolean isPushed()
    {
        return lastClick > 0;
    }

    @Override
    public boolean isPushedOnce(Integer click)
    {
        int clickInternal = click - 1;
        if (UtilMath.isBetween(click, 0, clicks.length-1) && clicks[clickInternal] && !clicked[clickInternal])
        {
            clicked[clickInternal] = true;
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

    @Override
    public String getName()
    {
        return Mouse.class.getSimpleName();
    }
}
