/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.rasterable;

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

    /** Raster image. */
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

        final FramesConfig framesData = FramesConfig.imports(getRoot());
        final int vf = framesData.getVertical();

        if (hasNode(NODE_RASTER))
        {
            final int rasterHeight = getInteger(ATTRIBUTE_RASTER_HEIGHT, NODE_RASTER);
            final boolean smooth = getBoolean(ATTRIBUTE_RASTER_SMOOTH, NODE_RASTER);

            final Media rasterFile;
            if (rasterMedia != null)
            {
                rasterFile = rasterMedia;
            }
            else
            {
                rasterFile = Medias.create(getString("file", NODE_RASTER));
            }

            raster = new RasterImage(getSurface(), rasterFile, rasterHeight, smooth);

            final int frameHeight = getSurface().getHeight() / vf;
            raster.loadRasters(frameHeight, false, UtilFile.removeExtension(config.getName()));
        }
        else
        {
            raster = new RasterImage(getSurface(), config, 1, false);
        }
    }

    /**
     * Get the rasters as read only.
     * 
     * @return The rasters.
     */
    public List<ImageBuffer> getRasters()
    {
        return Collections.unmodifiableList(raster.getRasters());
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
