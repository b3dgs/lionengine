/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import java.awt.Robot;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Resolution;
import com.b3dgs.lionengine.Verbose;

/**
 * Mouse implementation.
 */
public final class MouseAwt implements Mouse
{
    /** Left click. */
    public static final int LEFT = MouseEvent.BUTTON1;
    /** Middle click. */
    public static final int MIDDLE = MouseEvent.BUTTON2;
    /** Right click. */
    public static final int RIGHT = MouseEvent.BUTTON3;
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

    /** Move click. */
    private final MouseClickAwt clicker = new MouseClickAwt();
    /** Mouse move. */
    private final MouseMoveAwt mover = new MouseMoveAwt();
    /** Robot instance reference (can be <code>null</code>). */
    private final Robot robot = createRobot();
    /** Screen horizontal ratio. */
    private double xRatio;
    /** Screen vertical ratio. */
    private double yRatio;
    /** Perform robot release. */
    private int release = -1;
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
    public void lock()
    {
        lock(mover.getCx(), mover.getCy());
    }

    @Override
    public void lock(int x, int y)
    {
        if (robot != null)
        {
            robot.mouseMove(x, y);
        }
        else
        {
            mover.robotMove(x, y);
        }
        mover.lock();
    }

    @Override
    public void doClick(int click)
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
    public void doClickAt(int click, int x, int y)
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
        if (release > -1 && !doRelease)
        {
            doRelease = true;
        }
        else if (doRelease)
        {
            clicker.robotRelease(release);
            release = -1;
            doRelease = false;
        }
    }
}
