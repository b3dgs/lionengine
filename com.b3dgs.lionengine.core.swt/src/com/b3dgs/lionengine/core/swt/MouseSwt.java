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

import com.b3dgs.lionengine.Config;
import com.b3dgs.lionengine.io.swt.EventAction;
import com.b3dgs.lionengine.io.swt.Mouse;

/**
 * Mouse input implementation.
 */
public final class MouseSwt implements Mouse
{
    /** Move click. */
    private final MouseClickSwt clicker = new MouseClickSwt();
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
    public void addActionPressed(int click, EventAction action)
    {
        clicker.addActionPressed(click, action);
    }

    @Override
    public void addActionReleased(int click, EventAction action)
    {
        clicker.addActionReleased(click, action);
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
    public int getX()
    {
        return (int) (mover.getWx() / xRatio);
    }

    @Override
    public int getY()
    {
        return (int) (mover.getWy() / yRatio);
    }

    @Override
    public int getMoveX()
    {
        return mover.getMx();
    }

    @Override
    public int getMoveY()
    {
        return mover.getMy();
    }

    @Override
    public int getClick()
    {
        return clicker.getClick();
    }

    @Override
    public boolean hasClicked(int click)
    {
        return clicker.hasClicked(click);
    }

    @Override
    public boolean hasClickedOnce(int click)
    {
        return clicker.hasClickedOnce(click);
    }

    @Override
    public boolean hasMoved()
    {
        return mover.hasMoved();
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
