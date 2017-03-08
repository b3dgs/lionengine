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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.io.InputDevicePointer;

/**
 * Mouse pointer mock.
 */
public class MouseMock implements InputDevicePointer
{
    private int click;
    private int clicked;
    private int x;
    private int y;
    private int mx;
    private int my;

    /**
     * Set the current click.
     * 
     * @param click The current click.
     */
    public void setClick(int click)
    {
        this.click = click;
        clicked = click;
    }

    /**
     * Move mouse.
     * 
     * @param mx Horizontal movement.
     * @param my Vertical movement.
     */
    public void move(int mx, int my)
    {
        this.mx = mx;
        this.my = my;
        x += mx;
        y += my;
    }

    @Override
    public void update(double extrp)
    {
        // Nothing to do
    }

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
        return click;
    }

    @Override
    public boolean hasClicked(int click)
    {
        return this.click == click;
    }

    @Override
    public boolean hasClickedOnce(int click)
    {
        final boolean once = clicked == click;
        clicked = -1;
        return once;
    }

    @Override
    public boolean hasMoved()
    {
        return mx != 0 || my != 0;
    }
}
