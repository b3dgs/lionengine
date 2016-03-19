/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.background;

import com.b3dgs.lionengine.graphic.Graphic;

/**
 * Represents the background component interface, which will contain background elements.
 */
public interface BackgroundComponent
{
    /**
     * Update component.
     * 
     * @param extrp The extrapolation value.
     * @param x The horizontal offset.
     * @param y The vertical offset.
     * @param speed The scrolling speed.
     */
    void update(double extrp, int x, int y, double speed);

    /**
     * Render component.
     * 
     * @param g The graphic output.
     */
    void render(Graphic g);
}
