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
package com.b3dgs.lionengine.example.tyrian.menu;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Mouse;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.tyrian.Sfx;

/**
 * Menu button implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
abstract class Button
{
    /** Button surface. */
    private final SpriteTiled surface;
    /** Button id. */
    private final int id;
    /** Horizontal location. */
    private final int x;
    /** Vertical location. */
    private final int y;
    /** Button width. */
    private final int width;
    /** Button height. */
    private final int height;
    /** Mouse over flag. */
    private boolean over;
    /** Has over flag. */
    private boolean overed;

    /**
     * Constructor.
     * 
     * @param surface The surface reference.
     * @param id The id.
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    Button(SpriteTiled surface, int id, int x, int y)
    {
        this.surface = surface;
        this.id = id;
        this.x = x;
        this.y = y;
        width = this.surface.getTileWidth();
        height = this.surface.getTileHeight();
    }

    /**
     * Called when clicked on button.
     */
    protected abstract void clicked();

    /**
     * Update the button.
     * 
     * @param extrp The extrapolation value.
     * @param mouse The mouse reference.
     * @param click The click expected.
     */
    public void update(double extrp, Mouse mouse, int click)
    {
        final int mx = mouse.getOnWindowX();
        final int my = mouse.getOnWindowY();
        if (mx >= x && mx <= x + width && my > y && my < y + height)
        {
            over = true;
            if (mouse.hasClickedOnce(click))
            {
                onClick();
            }
            onOver();
        }
        else
        {
            over = false;
            overed = false;
        }
    }

    /**
     * Render the button.
     * 
     * @param g The graphic output.
     */
    public void render(Graphic g)
    {
        if (over)
        {
            surface.render(g, id + 1, x, y);
        }
        else
        {
            surface.render(g, id, x, y);
        }
    }

    /**
     * Called when cursor is over button.
     */
    private void onOver()
    {
        if (!overed)
        {
            Sfx.SELECT.play();
            overed = true;
        }
    }

    /**
     * Called when click occurred.
     */
    private void onClick()
    {
        Sfx.CLICK.play();
        clicked();
    }
}
