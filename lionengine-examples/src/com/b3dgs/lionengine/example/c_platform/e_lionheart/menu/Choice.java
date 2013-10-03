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
package com.b3dgs.lionengine.example.c_platform.e_lionheart.menu;

import com.b3dgs.lionengine.Align;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Text;

/**
 * Represents a choice in the menu.
 */
final class Choice
{
    /** Horizontal location. */
    final int x;
    /** Vertical location. */
    final int y;
    /** Next menu pointer. */
    final MenuType next;
    /** Text reference. */
    private final Text text;
    /** Choice name. */
    private final String name;
    /** Text align. */
    private final Align align;

    /**
     * Constructor.
     * 
     * @param text The text reference.
     * @param factorH The horizontal factor value.
     * @param factorV The vertical factor value.
     * @param name The choice name.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param align The text align.
     */
    Choice(Text text, double factorH, double factorV, String name, int x, int y, Align align)
    {
        this(text, factorH, factorV, name, x, y, align, null);
    }

    /**
     * Constructor.
     * 
     * @param text The text reference.
     * @param factorH The horizontal factor value.
     * @param factorV The vertical factor value.
     * @param name The choice name.
     * @param x The horizontal location.
     * @param y The vertical location.
     * @param align The text align.
     * @param next The next menu pointer.
     */
    Choice(Text text, double factorH, double factorV, String name, int x, int y, Align align, MenuType next)
    {
        this.text = text;
        this.name = name;
        this.x = (int) (160 * factorH) - (160 - x);
        this.y = (int) (y * factorV);
        this.align = align;
        this.next = next;
    }

    /**
     * Render the choice.
     * 
     * @param g The graphic output.
     */
    void render(Graphic g)
    {
        text.draw(g, x, y, align, name);
    }
}
