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
package com.b3dgs.lionengine.game.purview.model;

import java.util.List;

import com.b3dgs.lionengine.UtilityMath;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.SetupSurfaceRasteredGame;
import com.b3dgs.lionengine.game.purview.Rasterable;

/**
 * Default rasterable implementation.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class RasterableModel
        implements Rasterable
{
    /** List of rastered frames. */
    private final List<SpriteAnimated> rastersAnim;
    /** Rastered flag. */
    private final boolean rastered;
    /** Smooth raster flag. */
    private final boolean smooth;
    /** Tile height. */
    private final int tileHeight;

    /**
     * Constructor.
     * 
     * @param setup The setup reference.
     * @param tileHeight The tile height value.
     */
    public RasterableModel(SetupSurfaceRasteredGame setup, int tileHeight)
    {
        rastersAnim = setup.rastersAnim;
        rastered = setup.rasterFile != null;
        smooth = setup.smoothRaster;
        this.tileHeight = tileHeight;
    }

    /*
     * Rasterable
     */

    @Override
    public int getRasterIndex(double y)
    {
        final double value = y / tileHeight;
        final int i = (int) value % Rasterable.MAX_RASTERS_R;
        int index = i;

        if (!smooth && index > Rasterable.MAX_RASTERS_M)
        {
            index = Rasterable.MAX_RASTERS_M - (index - Rasterable.MAX_RASTERS);
        }
        return UtilityMath.fixBetween(index, 0, Rasterable.MAX_RASTERS);
    }

    @Override
    public SpriteAnimated getRasterAnim(int rasterIndex)
    {
        return rastersAnim.get(rasterIndex);
    }

    @Override
    public boolean isRastered()
    {
        return rastered;
    }
}
