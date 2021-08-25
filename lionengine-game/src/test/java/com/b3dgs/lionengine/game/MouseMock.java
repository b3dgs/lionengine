/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game;

import com.b3dgs.lionengine.io.DevicePointer;

/**
 * Mouse pointer mock.
 */
public class MouseMock implements DevicePointer
{
    private Integer click;
    private Integer clicked;
    private int x;
    private int y;
    private double mx;
    private double my;

    /**
     * Set the current click.
     * 
     * @param click The current click.
     */
    public void setClick(Integer click)
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
    public void move(double mx, double my)
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
    public double getX()
    {
        return x;
    }

    @Override
    public double getY()
    {
        return y;
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
    public boolean isPushed()
    {
        return click != null;
    }

    @Override
    public Integer getPushed()
    {
        return click;
    }

    @Override
    public boolean isPushed(Integer click)
    {
        return this.click == click;
    }

    @Override
    public boolean isPushedOnce(Integer click)
    {
        final boolean once = clicked.equals(click);
        clicked = Integer.valueOf(0);
        return once;
    }

    @Override
    public void lock(int x, int y)
    {
        this.x = x;
        this.y = y;
    }

    @Override
    public String getName()
    {
        return MouseMock.class.getSimpleName();
    }
}
