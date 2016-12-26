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
package com.b3dgs.lionengine.example.game.background;

import com.b3dgs.lionengine.game.background.BackgroundElementRastered;
import com.b3dgs.lionengine.graphic.Sprite;

/**
 * Rastered background element implementation.
 */
class ElementRastered extends BackgroundElementRastered
{
    /**
     * Create a rastered background element.
     * 
     * @param mainX The main location x.
     * @param mainY The main location y.
     * @param sprite The sprite to be rastered.
     * @param rastersNumber The number of rasters.
     */
    public ElementRastered(int mainX, int mainY, Sprite sprite, int rastersNumber)
    {
        super(mainX, mainY, sprite, rastersNumber);
    }

    /**
     * Raster.
     * 
     * @param sprite The sprite reference.
     * @param i The index value.
     */
    protected void swamp(Sprite sprite, int i)
    {
        final int r = 0x300000 - 0x080000 * (int) (i / 1.5);
        final int g = 0x002000 - 0x000100 * (i / 5);
        final int b = -0x000026 + 0x000007 * (int) (i * 1.5);
        addRaster(sprite, r, g, b);
    }

    @Override
    protected void load(Sprite sprite, int rastersNumber)
    {
        for (int i = 0; i < rastersNumber; i++)
        {
            swamp(sprite, (int) (i * 2.5));
        }
    }
}
