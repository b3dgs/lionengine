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
package com.b3dgs.lionengine.game;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityImage;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.purview.Configurable;
import com.b3dgs.lionengine.game.purview.Rasterable;
import com.b3dgs.lionengine.game.purview.model.ConfigurableModel;

/**
 * Define a structure used to create multiple rastered surface, sharing the same data.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Configurable
 */
public class SetupSurfaceRasteredGame
        extends SetupSurfaceGame
{
    /** List of rasters animation. */
    public final List<SpriteAnimated> rastersAnim;
    /** Raster filename. */
    public final Media rasterFile;
    /** Raster smooth flag. */
    public final boolean smoothRaster;
    /** Vertical frames. */
    private final int vf;
    /** Horizontal frames. */
    private final int hf;
    /** frame height. */
    private final int frameHeight;

    /**
     * Constructor.
     * 
     * @param config The config media.
     * @param rasterFile The raster media.
     * @param smoothRaster The raster smooth flag.
     */
    public SetupSurfaceRasteredGame(Media config, Media rasterFile, boolean smoothRaster)
    {
        this(new ConfigurableModel(), config, false, rasterFile, smoothRaster);
    }

    /**
     * Constructor.
     * 
     * @param configurable The configurable reference.
     * @param config The config media.
     * @param alpha The alpha use flag.
     * @param rasterFile The raster media.
     * @param smoothRaster The raster smooth flag.
     */
    public SetupSurfaceRasteredGame(Configurable configurable, Media config, boolean alpha, Media rasterFile,
            boolean smoothRaster)
    {
        super(configurable, config, alpha);
        this.rasterFile = rasterFile;
        this.smoothRaster = smoothRaster;
        if (rasterFile != null)
        {
            rastersAnim = new ArrayList<>(Rasterable.MAX_RASTERS);
            hf = configurable.getDataInteger("horizontal", "frames");
            vf = configurable.getDataInteger("vertical", "frames");
            frameHeight = surface.getHeight() / vf;
            loadRasters();
        }
        else
        {
            rastersAnim = null;
            hf = 0;
            vf = 0;
            frameHeight = 0;
        }
    }

    /**
     * Load rasters.
     */
    private void loadRasters()
    {
        final int[][] rasters = UtilityImage.loadRaster(rasterFile);
        final int[] color = new int[rasters.length];
        final int[] colorNext = new int[rasters.length];
        final int max = smoothRaster ? 2 : 1;

        for (int m = 0; m < max; m++)
        {
            for (int i = 1; i <= Rasterable.MAX_RASTERS; i++)
            {
                for (int c = 0; c < rasters.length; c++)
                {
                    if (smoothRaster)
                    {
                        if (m == 0)
                        {
                            color[c] = UtilityImage.getRasterColor(i, rasters[c], Rasterable.MAX_RASTERS);
                            colorNext[c] = UtilityImage.getRasterColor(i + 1, rasters[c], Rasterable.MAX_RASTERS);
                        }
                        else
                        {
                            color[c] = UtilityImage.getRasterColor(Rasterable.MAX_RASTERS - i, rasters[c],
                                    Rasterable.MAX_RASTERS);
                            colorNext[c] = UtilityImage.getRasterColor(Rasterable.MAX_RASTERS - i - 1, rasters[c],
                                    Rasterable.MAX_RASTERS);
                        }
                    }
                    else
                    {
                        color[c] = UtilityImage.getRasterColor(i, rasters[c], Rasterable.MAX_RASTERS);
                        colorNext[c] = color[c];
                    }
                }
                addRaster(color[0], color[1], color[2], colorNext[0], colorNext[1], colorNext[2]);
            }
        }
    }

    /**
     * Add raster.
     * 
     * @param fr The first red.
     * @param fg The first green.
     * @param fb The first blue.
     * @param er The end red.
     * @param eg The end green.
     * @param eb The end blue.
     */
    private void addRaster(int fr, int fg, int fb, int er, int eg, int eb)
    {
        final ImageBuffer rasterBuf = UtilityImage.getRasterBuffer(surface, fr, fg, fb, er, eg, eb, frameHeight);
        final SpriteAnimated raster = Drawable.loadSpriteAnimated(rasterBuf, hf, vf);
        rastersAnim.add(raster);
    }
}
