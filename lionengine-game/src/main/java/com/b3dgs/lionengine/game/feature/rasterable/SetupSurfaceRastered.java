/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.SurfaceConfig;
import com.b3dgs.lionengine.game.feature.Setup;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.raster.RasterImage;

/**
 * Define a structure used to create multiple rastered surface, sharing the same data.
 */
public class SetupSurfaceRastered extends Setup
{
    /** Rasterable node. */
    private static final String NODE_RASTERABLE = Constant.XML_PREFIX + "rasterable";
    /** Rasterable height attribute. */
    private static final String ATT_RASTER_HEIGHT = "height";
    /** Rasterable file attribute. */
    private static final String ATT_RASTER_FILE = "file";

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

        if (hasNode(NODE_RASTERABLE))
        {
            final int rasterHeight = getInteger(ATT_RASTER_HEIGHT, NODE_RASTERABLE);

            final Media rasterFile;
            if (rasterMedia != null)
            {
                rasterFile = rasterMedia;
            }
            else
            {
                rasterFile = Medias.create(getString(ATT_RASTER_FILE, NODE_RASTERABLE));
            }

            raster = new RasterImage(getSurface(), rasterFile, rasterHeight);
            raster.loadRasters(false, UtilFile.removeExtension(config.getName()));
        }
        else if (hasNode(SurfaceConfig.NODE_SURFACE))
        {
            raster = new RasterImage(getSurface(), config, 1);
        }
        else
        {
            raster = new RasterImage(Graphics.createImageBuffer(1, 1), config, 1);
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
}
