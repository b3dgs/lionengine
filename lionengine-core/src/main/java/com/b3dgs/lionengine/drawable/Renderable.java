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
package com.b3dgs.lionengine.drawable;

import com.b3dgs.lionengine.Graphic;

/**
 * Represents an element that can be rendered on screen.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Graphic
 */
public interface Renderable
{
    /**
     * Render element on current graphic output, at specified coordinates.
     * 
     * @param g The graphic output.
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    void render(Graphic g, int x, int y);

    /**
     * Get the element width.
     * 
     * @return The element width.
     */
    int getWidth();

    /**
     * Get the element height.
     * 
     * @return The element height.
     */
    int getHeight();
}
