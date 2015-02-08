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
package com.b3dgs.lionengine.game.map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.drawable.SpriteTiled;

/**
 * Map tile rastered representation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface MapTileRastered
        extends MapTileFeature
{
    /**
     * Load map patterns as rastered.
     * 
     * @param directory The patterns directory.
     * @throws LionEngineException If error when reading patterns.
     */
    void loadPatterns(Media directory) throws LionEngineException;

    /**
     * Set raster file and smoothed flag.
     * 
     * @param raster The raster media (may be <code>null</code>).
     * @param smooth <code>true</code> for a smoothed raster (may be slower), <code>false</code> else.
     */
    void setRaster(Media raster, boolean smooth);

    /**
     * Get raster index from input tile (depending of its height).
     * 
     * @param ty The vertical tile location.
     * @return The raster index.
     */
    int getRasterIndex(int ty);

    /**
     * Get a tilesheet from its pattern and raster id.
     * 
     * @param pattern The pattern number
     * @param rasterID The raster id.
     * @return The tilesheet reference.
     */
    SpriteTiled getRasterPattern(Integer pattern, int rasterID);
}
