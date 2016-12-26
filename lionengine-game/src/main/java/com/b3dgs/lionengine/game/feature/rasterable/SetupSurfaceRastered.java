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
package com.b3dgs.lionengine.game.feature.rasterable;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.Setup;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Raster;
import com.b3dgs.lionengine.graphic.RasterColor;
import com.b3dgs.lionengine.graphic.SpriteAnimated;
import com.b3dgs.lionengine.util.UtilConversion;

/**
 * Define a structure used to create multiple rastered surface, sharing the same data.
 * 
 * @see com.b3dgs.lionengine.game.Configurer
 */
public class SetupSurfaceRastered extends Setup
{
    /** Raster node. */
    private static final String NODE_RASTER = Constant.XML_PREFIX + "raster";
    /** Raster height attribute. */
    private static final String ATTRIBUTE_RASTER_HEIGHT = "height";
    /** Raster smooth attribute. */
    private static final String ATTRIBUTE_RASTER_SMOOTH = "smooth";

    /** List of rasters animation. */
    private final List<SpriteAnimated> rastersAnim;
    /** Raster filename. */
    private final Media rasterFile;
    /** Raster smooth flag. */
    private final boolean rasterSmooth;
    /** Vertical frames. */
    private final int vf;
    /** Horizontal frames. */
    private final int hf;
    /** Frame height. */
    private final int frameHeight;
    /** Raster height. */
    private final int rasterHeight;

    /**
     * Create a setup.
     * 
     * @param config The config media.
     * @param rasterFile The raster media.
     * @throws LionEngineException If error when opening the media or invalid raster file.
     */
    public SetupSurfaceRastered(Media config, Media rasterFile)
    {
        super(config);
        Check.notNull(config);
        Check.notNull(rasterFile);

        this.rasterFile = rasterFile;

        rasterHeight = getInteger(ATTRIBUTE_RASTER_HEIGHT, NODE_RASTER);
        rasterSmooth = getBoolean(ATTRIBUTE_RASTER_SMOOTH, NODE_RASTER);
        rastersAnim = new ArrayList<SpriteAnimated>(RasterColor.MAX_RASTERS);

        final FramesConfig framesData = FramesConfig.imports(getRoot());
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
     * Get the raster height.
     * 
     * @return The raster height.
     */
    public int getRasterHeight()
    {
        return rasterHeight;
    }

    /**
     * Check if smooth raster.
     * 
     * @return <code>true</code> if smooth enabled, <code>false</code> else.
     */
    public boolean hasSmooth()
    {
        return rasterSmooth;
    }

    /**
     * Load rasters.
     *
     * @throws LionEngineException If the raster data from the media are invalid.
     */
    private void loadRasters()
    {
        final Raster raster = Raster.load(rasterFile);
        final int max = UtilConversion.boolToInt(rasterSmooth) + 1;

        for (int m = 0; m < max; m++)
        {
            for (int i = 1; i <= RasterColor.MAX_RASTERS; i++)
            {
                final RasterColor red = RasterColor.load(raster.getRed(), m, i, rasterSmooth);
                final RasterColor green = RasterColor.load(raster.getGreen(), m, i, rasterSmooth);
                final RasterColor blue = RasterColor.load(raster.getBlue(), m, i, rasterSmooth);

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
