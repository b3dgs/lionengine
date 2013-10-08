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

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.audio.Wav;
import com.b3dgs.lionengine.core.Click;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.example.warcraft.ResourcesLoader;
import com.b3dgs.lionengine.example.warcraft.type.TypeMenu;
import com.b3dgs.lionengine.game.Cursor;

/**
 * Button menu implementation.
 */
final class Button
{
    /** Click sound. */
    private final Wav click;
    /** Button surface. */
    private final SpriteTiled surface;
    /** Button text. */
    private final String text;
    /** Button horizontal location. */
    private final int x;
    /** Button vertical location. */
    private final int y;
    /** Button width. */
    private final int w;
    /** Button height. */
    private final int h;
    /** Button destination menu. */
    private final TypeMenu menu;
    /** Mouse over flag. */
    private boolean over;

    /**
     * Constructor.
     * 
     * @param surface The surface reference.
     * @param text The text reference.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param menu The destination menu.
     */
    Button(SpriteTiled surface, String text, int x, int y, TypeMenu menu)
    {
        this.surface = surface;
        this.text = text;
        this.x = x;
        this.y = y;
        this.menu = menu;
        click = ResourcesLoader.SOUND_CLICK;
        w = surface.getTileWidth();
        h = surface.getTileHeight();
        over = false;
    }

    /**
     * Update routine.
     * 
     * @param cursor The cursor reference.
     */
    void update(Cursor cursor)
    {
        final int cx = cursor.getLocationX();
        final int cy = cursor.getLocationY();
        over = cx >= x && cy >= y && cx <= x + w && cy <= y + h;
        if (!Menu.clicked && over && cursor.getClick() == Click.LEFT)
        {
            click.play();
            Menu.clicked = true;
            Menu.menu = menu;
            over = false;
        }
    }

    /**
     * Render routine.
     * 
     * @param g The graphics output.
     */
    void render(Graphic g)
    {
        if (over)
        {
            surface.render(g, 1, x, y);
            Menu.FONT.setColor(Menu.COLOR_OVER);
        }
        else
        {
            surface.render(g, 0, x, y);
            Menu.FONT.setColor(Menu.COLOR);
        }
        Menu.FONT.setColor(Menu.COLOR);
        Menu.FONT.draw(g, x + w / 2, y + 4, Align.CENTER, text);
        Menu.FONT.setColor(Menu.COLOR_HEAD);
        Menu.FONT.draw(g, x + w / 2 - Menu.FONT.getStringWidth(g, text) / 2, y + 4, text.substring(0, 1));
    }
}
