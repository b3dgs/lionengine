/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.example.game.controlpanel;

import com.b3dgs.lionengine.ColorRgba;

/**
 * List of minimap tile colors.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
enum TileColorType
{
    /** The dark ground. */
    grassDark(new ColorRgba(9, 43, 8)),
    /** The dark - normal ground. */
    grassDarkBorder(new ColorRgba(11, 45, 8)),
    /** The normal ground. */
    grass(new ColorRgba(17, 51, 8)),
    /** The normal - light. */
    grassLightBorder(new ColorRgba(19, 52, 8)),
    /** The light ground. */
    grassLight(new ColorRgba(23, 55, 8)),
    /** The light - lighter ground. */
    grassBrightBorder(new ColorRgba(23, 59, 6)),
    /** The lighter ground. */
    grassBright(new ColorRgba(32, 61, 5)),
    /** The road. */
    road(new ColorRgba(82, 40, 19)),
    /** The bridge. */
    bridge(new ColorRgba(66, 30, 15)),
    /** The cut tree. */
    treeCut(new ColorRgba(128, 72, 32)),
    /** Tree border. */
    treeBorder(new ColorRgba(10, 28, 9)),
    /** Tree center. */
    tree(new ColorRgba(14, 25, 9)),
    /** Border ground. */
    border(new ColorRgba(35, 52, 85)),
    /** Water. */
    water(new ColorRgba(27, 40, 101));

    /** Colors list. */
    private final ColorRgba color;

    /**
     * Constructor.
     * 
     * @param colors List of colors.
     */
    private TileColorType(ColorRgba colors)
    {
        color = colors;
    }

    /**
     * Get the color theme.
     * 
     * @return Theme color
     */
    public ColorRgba get()
    {
        return color;
    }
}
