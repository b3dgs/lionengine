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
package com.b3dgs.lionengine.game.raster;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.ImageBuffer;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Raster;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteAnimated;
import com.b3dgs.lionengine.game.object.FramesConfig;
import com.b3dgs.lionengine.game.object.SetupSurface;

/**
 * Define a structure used to create multiple rastered surface, sharing the same data.
 * 
 * @see com.b3dgs.lionengine.game.Configurer
 */
public class SetupSurfaceRastered extends SetupSurface
{
    /** List of rasters animation. */
    private final List<SpriteAnimated> rastersAnim;
    /** Raster filename. */
    private final Media rasterFile;
    /** Raster smooth flag. */
    private final boolean smoothRaster;
    /** Vertical frames. */
    private final int vf;
    /** Horizontal frames. */
    private final int hf;
    /** frame height. */
    private final int frameHeight;

    /**
     * Create a setup.
     * 
     * @param config The config media.
     * @param rasterFile The raster media.
     * @param smoothRaster The raster smooth flag.
     * @throws LionEngineException If error when opening the media or invalid raster file.
     */
    public SetupSurfaceRastered(Media config, Media rasterFile, boolean smoothRaster)
    {
        super(config);
        Check.notNull(rasterFile);

        this.rasterFile = rasterFile;
        this.smoothRaster = smoothRaster;

        rastersAnim = new ArrayList<SpriteAnimated>(Rasterable.MAX_RASTERS);
        final FramesConfig framesData = FramesConfig.imports(getConfigurer());
        hf = framesData.getHorizontal();
        vf = framesData.getVertical();
        frameHeight = surface.getHeight() / vf;
        loadRasters();
    }

    /**
     * Get the rasters.
     * 
     * @return The rasters.
     */
    public List<SpriteAnimated> getRasters()
    {
        return rastersAnim;
    }

    /**
     * Get the raster file.
     * 
     * @return The raster file.
     */
    public Media getFile()
    {
        return rasterFile;
    }

    /**
     * Check if smooth raster.
     * 
     * @return <code>true</code> if smooth enabled, <code>false</code> else.
     */
    public boolean hasSmooth()
    {
        return smoothRaster;
    }

    /**
     * Load rasters.
     *
     * @throws LionEngineException If the raster data from the media are invalid.
     */
    private void loadRasters()
    {
        final Raster raster = Raster.load(rasterFile);
        final int max = UtilConversion.boolToInt(smoothRaster) + 1;

        for (int m = 0; m < max; m++)
        {
            for (int i = 1; i <= Rasterable.MAX_RASTERS; i++)
            {
                final RasterColor red = RasterColor.load(raster.getRed(), m, i, smoothRaster);
                final RasterColor green = RasterColor.load(raster.getGreen(), m, i, smoothRaster);
                final RasterColor blue = RasterColor.load(raster.getBlue(), m, i, smoothRaster);

                addRaster(red, green, blue);
            }
        }
    }

    /**
     * Add raster.
     * 
     * @param red The red color transition.
     * @param green The green color transition.
     * @param blue The blue color transition.
     * @throws LionEngineException If arguments are invalid.
     */
    private void addRaster(RasterColor red, RasterColor green, RasterColor blue)
    {
        final ImageBuffer rasterBuf = Graphics.getRasterBuffer(surface,
                                                               red.getStart(),
                                                               green.getStart(),
                                                               blue.getStart(),
                                                               red.getEnd(),
                                                               green.getEnd(),
                                                               blue.getEnd(),
                                                               frameHeight);
        final SpriteAnimated raster = Drawable.loadSpriteAnimated(rasterBuf, hf, vf);
        rastersAnim.add(raster);
    }
}
