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
package com.b3dgs.lionengine.example.d_rts.f_warcraft.type;

import com.b3dgs.lionengine.core.ColorRgba;

/**
 * List of minimap tile colors.
 */
public enum TypeTileColor
{
    /* Themes: Forest, Swamp */

    /** The dark ground. */
    GROUND0(new ColorRgba(9, 43, 8), new ColorRgba(45, 29, 25)),
    /** The dark - normal ground. */
    GROUND1(new ColorRgba(11, 45, 8), new ColorRgba(56, 37, 29)),
    /** The normal ground. */
    GROUND2(new ColorRgba(17, 51, 8), new ColorRgba(59, 39, 30)),
    /** The normal - light. */
    GROUND3(new ColorRgba(19, 52, 8), new ColorRgba(63, 42, 31)),
    /** The light ground. */
    GROUND4(new ColorRgba(23, 55, 8), new ColorRgba(68, 46, 34)),
    /** The light - lighter ground. */
    GROUND5(new ColorRgba(23, 59, 6), new ColorRgba(74, 50, 35)),
    /** The lighter ground. */
    GROUND6(new ColorRgba(32, 61, 5), new ColorRgba(80, 54, 38)),
    /** The road. */
    GROUND7(new ColorRgba(82, 40, 19), new ColorRgba(79, 56, 39)),
    /** The bridge. */
    GROUND8(new ColorRgba(66, 30, 15), new ColorRgba(62, 42, 29)),
    /** The cut tree. */
    GROUND9(new ColorRgba(128, 72, 32), new ColorRgba(128, 72, 12)),
    /** Tree border. */
    TREE_BORDER(new ColorRgba(10, 28, 9), new ColorRgba(42, 40, 21)),
    /** Tree center. */
    TREE(new ColorRgba(14, 25, 9), new ColorRgba(41, 44, 22)),
    /** Border ground. */
    BORDER(new ColorRgba(35, 52, 85), new ColorRgba(85, 69, 32)),
    /** Water. */
    WATER(new ColorRgba(27, 40, 101), new ColorRgba(59, 63, 0));

    /** Colors list. */
    private final ColorRgba[] color;

    /**
     * Constructor.
     * 
     * @param colors List of colors.
     */
    private TypeTileColor(ColorRgba... colors)
    {
        color = colors;
    }

    /**
     * Get the color theme.
     * 
     * @param id Theme id.
     * @return Theme color
     */
    public ColorRgba getTheme(int id)
    {
        return color[id];
    }
}
