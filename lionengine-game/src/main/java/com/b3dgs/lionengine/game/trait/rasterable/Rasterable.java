/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.trait.rasterable;

import com.b3dgs.lionengine.Renderable;
import com.b3dgs.lionengine.Updatable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.trait.Trait;

/**
 * Represents a surface that can be rastered.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface Rasterable extends Trait, Updatable, Renderable
{
    /** Maximum rasters. */
    int MAX_RASTERS = 15;
    /** Maximum rasters R. */
    int MAX_RASTERS_R = Rasterable.MAX_RASTERS * 2;
    /** Maximum rasters M. */
    int MAX_RASTERS_M = Rasterable.MAX_RASTERS - 1;

    /**
     * Get raster index from location.
     * 
     * @param y The current y location.
     * @return The raster index based on vertical location.
     */
    int getRasterIndex(double y);

    /**
     * Get raster animation from raster index.
     * 
     * @param rasterIndex The raster index (positive).
     * @return The raster animated sprite.
     */
    SpriteAnimated getRasterAnim(int rasterIndex);

    /**
     * Check if raster is activated.
     * 
     * @return <code>true</code> if rastered, <code>false</code> else.
     */
    boolean isRastered();
}
