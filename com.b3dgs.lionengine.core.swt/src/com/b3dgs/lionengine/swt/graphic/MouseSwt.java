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

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.InputDeviceListener;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.swt.EventAction;
import com.b3dgs.lionengine.swt.Mouse;

/**
 * Mouse input implementation.
 */
public final class MouseSwt implements Mouse
{
    /** Left click. */
    public static final Integer LEFT = Integer.valueOf(1);
    /** Middle click. */
    public static final Integer MIDDLE = Integer.valueOf(2);
    /** Right click. */
    public static final Integer RIGHT = Integer.valueOf(3);

    /** Push listener. */
    private final ListenableModel<InputDeviceListener> listeners = new ListenableModel<>();
    /** Move click. */
    private final MouseClickSwt clicker = new MouseClickSwt(listeners);
    /** Mouse move. */
    private final MouseMoveSwt mover = new MouseMoveSwt();
    /** Screen horizontal ratio. */
    private double xRatio;
    /** Screen vertical ratio. */
    private double yRatio;

    /**
     * Constructor.
     */
    public MouseSwt()
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
    public void setConfig(Resolution output, Resolution source)
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
    public MouseClickSwt getClicker()
    {
        return clicker;
    }

    /**
     * Get the movement handler.
     * 
     * @return The movement handler.
     */
    public MouseMoveSwt getMover()
    {
        return mover;
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
    public void lock(int x, int y)
    {
        // TODO to be implemented !
    }

    @Override
    public void unlock()
    {
        // TODO to be implemented !
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

    /*
     * InputDevicePointer
     */

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
    public Integer getPushed()
    {
        return clicker.getClick();
    }

    @Override
    public boolean isPushed(Integer click)
    {
        return clicker.hasClicked(click);
    }

    @Override
    public boolean isPushedOnce(Integer click)
    {
        return clicker.hasClickedOnce(click);
    }

    @Override
    public String getName()
    {
        return Mouse.class.getSimpleName();
    }

    /*
     * Updatable
     */

    @Override
    public void update(double extrp)
    {
        mover.update();
    }
}
