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
package com.b3dgs.lionengine.game.feature.rasterable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.game.FramesConfig;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteAnimated;
import com.b3dgs.lionengine.graphic.raster.RasterColor;
import com.b3dgs.lionengine.graphic.raster.RasterImage;

/**
 * Define a structure used to create multiple rastered surface, sharing the same data.
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
    private final List<SpriteAnimated> rastersAnim = new ArrayList<>(RasterColor.MAX_RASTERS);
    /** List of rasters animation. */
    private final RasterImage raster;

    /**
     * Create a setup.
     * 
     * @param config The config media.
     * @throws LionEngineException If error when opening the media or invalid raster file.
     */
    public SetupSurfaceRastered(Media config)
    {
        this(config, null);
    }

    /**
     * Create a setup.
     * 
     * @param config The config media.
     * @param rasterMedia The raster media.
     * @throws LionEngineException If error when opening the media or invalid raster file.
     */
    public SetupSurfaceRastered(Media config, Media rasterMedia)
    {
        super(config);

        final Media rasterFile;
        if (rasterMedia != null)
        {
            rasterFile = rasterMedia;
        }
        else
        {
            rasterFile = Medias.create(getString("file", NODE_RASTER));
        }

        final int rasterHeight = getInteger(ATTRIBUTE_RASTER_HEIGHT, NODE_RASTER);
        final boolean smooth = getBoolean(ATTRIBUTE_RASTER_SMOOTH, NODE_RASTER);
        raster = new RasterImage(getSurface(), rasterFile, rasterHeight, smooth);

        final FramesConfig framesData = FramesConfig.imports(getRoot());
        final int hf = framesData.getHorizontal();
        final int vf = framesData.getVertical();
        final int frameHeight = getSurface().getHeight() / vf;
        raster.loadRasters(frameHeight, true, UtilFile.removeExtension(config.getName()));

        for (final ImageBuffer buffer : raster.getRasters())
        {
            final SpriteAnimated sprite = Drawable.loadSpriteAnimated(buffer, hf, vf);
            rastersAnim.add(sprite);
        }
    }

    /**
     * Get the rasters as read only.
     * 
     * @return The rasters.
     */
    public List<SpriteAnimated> getRasters()
    {
        return Collections.unmodifiableList(rastersAnim);
    }

    /**
     * Get the raster file.
     * 
     * @return The raster file.
     */
    public Media getFile()
    {
        return raster.getFile();
    }

    /**
     * Get the raster height.
     * 
     * @return The raster height.
     */
    public int getRasterHeight()
    {
        return raster.getHeight();
    }

    /**
     * Get smooth raster flag.
     * 
     * @return <code>true</code> if smooth enabled, <code>false</code> else.
     */
    public boolean hasSmooth()
    {
        return raster.hasSmooth();
    }
}
