/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.warcraft.menu;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.Click;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.warcraft.Sfx;
import com.b3dgs.lionengine.game.Cursor;

/**
 * Choice selection button (left & right).
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class Choice
{
    /** Button surface. */
    private final SpriteTiled surface;
    /** Button horizontal location. */
    private final int x;
    /** Button vertical location. */
    private final int y;
    /** Button width. */
    private final int w;
    /** Button height. */
    private final int h;
    /** <code>true</code> = right side, <code>false</code> = left. */
    private final boolean right;
    /** Mouse over state. */
    private boolean over;

    /**
     * Constructor.
     * 
     * @param surface The surface reference.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param right The button side.
     */
    Choice(SpriteTiled surface, int x, int y, boolean right)
    {
        this.surface = surface;
        this.x = x;
        this.y = y;
        this.right = right;
        w = surface.getTileWidth();
        h = surface.getTileHeight();
        over = false;
    }

    /**
     * Update routine.
     * 
     * @param cursor The cursor reference.
     * @return <code>true</code> if pressed, <code>false</code> else.
     */
    boolean update(Cursor cursor)
    {
        final int cx = cursor.getLocationX();
        final int cy = cursor.getLocationY();
        boolean pressed = false;
        over = cx >= x && cy >= y && cx <= x + w && cy <= y + h;
        if (!Menu.clicked && over && cursor.getClick() == Click.LEFT)
        {
            Sfx.CLICK.play();
            pressed = true;
            Menu.clicked = true;
        }
        return pressed;
    }

    /**
     * Render routine.
     * 
     * @param g The graphics output.
     */
    void render(Graphic g)
    {
        int a = 0;
        if (right)
        {
            a += 2;
        }
        if (over)
        {
            surface.render(g, 1 + a, x, y);
        }
        else
        {
            surface.render(g, 0 + a, x, y);
        }
    }

    /**
     * Get the button side.
     * 
     * @return The button side.
     */
    int getSide()
    {
        return right ? 1 : -1;
    }
}
