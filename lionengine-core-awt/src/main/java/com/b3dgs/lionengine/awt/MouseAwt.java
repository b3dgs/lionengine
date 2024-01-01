/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.InputDeviceListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Resolution;

/**
 * Mouse implementation.
 */
public final class MouseAwt implements Mouse
{
    /** Left click. */
    public static final Integer LEFT = Integer.valueOf(MouseEvent.BUTTON1);
    /** Middle click. */
    public static final Integer MIDDLE = Integer.valueOf(MouseEvent.BUTTON2);
    /** Right click. */
    public static final Integer RIGHT = Integer.valueOf(MouseEvent.BUTTON3);

    /** Push listener. */
    private final ListenableModel<InputDeviceListener> listeners = new ListenableModel<>();
    /** Move click. */
    private final MouseClickAwt clicker = new MouseClickAwt(listeners);
    /** Mouse move. */
    private final MouseMoveAwt mover = new MouseMoveAwt();
    /** Screen horizontal ratio. */
    private double xRatio;
    /** Screen vertical ratio. */
    private double yRatio;
    /** Perform robot release. */
    private Integer release;
    /** Do robot release. */
    private boolean doRelease;

    /**
     * Constructor.
     */
    public MouseAwt()
    {
        super();
    }

    /**
     * Set the resolution used. This will compute mouse horizontal and vertical ratio.
     * 
     * @param output The resolution output (must not be <code>null</code>).
     * @param source The resolution source (must not be <code>null</code>).
     * @throws LionEngineException If invalid argument.
     */
    public void setResolution(Resolution output, Resolution source)
    {
        Check.notNull(output);
        Check.notNull(source);

        xRatio = output.getWidth() / (double) source.getWidth();
        yRatio = output.getHeight() / (double) source.getHeight();
    }

    /**
     * Get the click handler.
     * 
     * @return The click handler.
     */
    public MouseListener getClicker()
    {
        return clicker;
    }

    /**
     * Get the movement handler.
     * 
     * @return The movement handler.
     */
    public MouseMotionListener getMover()
    {
        return mover;
    }

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
    public void addActionPressed(Integer click, EventAction action)
    {
        clicker.addActionPressed(click, action);
    }

    @Override
    public void addActionReleased(Integer click, EventAction action)
    {
        clicker.addActionReleased(click, action);
    }

    @Override
    public void lock()
    {
        lock(mover.getCx(), mover.getCy());
    }

    @Override
    public void lock(int x, int y)
    {
        mover.setCenter(x, y);
        mover.lock();
    }

    @Override
    public void unlock()
    {
        mover.unlock();
    }

    @Override
    public void doClick(Integer click)
    {
        clicker.robotPress(click);
        release = click;
    }

    @Override
    public void doSetMouse(int x, int y)
    {
        mover.robotTeleport(x, y);
    }

    @Override
    public void doMoveMouse(int x, int y)
    {
        mover.robotMove(x, y);
    }

    @Override
    public void doClickAt(Integer click, int x, int y)
    {
        mover.robotTeleport(x, y);
        clicker.robotPress(click);
        release = click;
    }

    @Override
    public void setCenter(int x, int y)
    {
        mover.setCenter(x, y);
    }

    @Override
    public Integer getPushed()
    {
        return clicker.getClick();
    }

    @Override
    public int getOnScreenX()
    {
        return mover.getX();
    }

    @Override
    public int getOnScreenY()
    {
        return mover.getY();
    }

    @Override
    public double getX()
    {
        return mover.getWx() / xRatio;
    }

    @Override
    public double getY()
    {
        return mover.getWy() / yRatio;
    }

    @Override
    public double getMoveX()
    {
        return mover.getMx();
    }

    @Override
    public double getMoveY()
    {
        return mover.getMy();
    }

    @Override
    public boolean isPushed()
    {
        return clicker.isClicked();
    }

    @Override
    public boolean isPushed(Integer index)
    {
        return clicker.hasClicked(index);
    }

    @Override
    public boolean isPushedOnce(Integer index)
    {
        return clicker.hasClickedOnce(index);
    }

    @Override
    public void update(double extrp)
    {
        mover.update();
        if (release != null && !doRelease)
        {
            doRelease = true;
        }
        else if (doRelease)
        {
            clicker.robotRelease(release);
            release = null;
            doRelease = false;
        }
    }
}
