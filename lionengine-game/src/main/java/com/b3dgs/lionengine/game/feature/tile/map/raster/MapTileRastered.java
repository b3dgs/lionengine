/*
 * Copyright (C) 2013-2017 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.raster;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileRenderer;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * Map tile rastered representation. This will allow to apply a raster effect to map rendering, improving the number of
 * color variation depending of the height.
 * <p>
 * Here the usage:
 * </p>
 * 
 * <pre>
 * {@link #loadSheets(Media, boolean)}
 * </pre>
 */
@FeatureInterface
public interface MapTileRastered extends Feature, MapTileRenderer
{
    /**
     * Load tile sheets as rastered.
     * 
     * @param rasterConfig The raster file that define the colors.
     * @param smooth <code>true</code> for a smoothed raster (may be slower), <code>false</code> else.
     * @throws LionEngineException If error when reading sheets.
     */
    void loadSheets(Media rasterConfig, boolean smooth);

    /**
     * Get raster index from input tile (depending of its height).
     * 
     * @param ty The vertical tile location in tile.
     * @return The raster index.
     */
    int getRasterIndex(int ty);

    /**
     * Get a tilesheet from its sheet and raster index.
     * 
     * @param sheet The sheet number
     * @param rasterIndex The raster index.
     * @return The tilesheet reference.
     */
    SpriteTiled getRasterSheet(Integer sheet, int rasterIndex);
}
