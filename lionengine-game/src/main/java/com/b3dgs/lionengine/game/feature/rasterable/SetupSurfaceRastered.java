/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b3dgs.lionengine.Check;
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
    /** Rasterable external attribute. */
    private static final String ATT_RASTER_EXTERN = "extern";
    /** Rasterable offset attribute. */
    private static final String ATT_RASTER_OFFSET = "offset";

    /** Raster image. */
    private final RasterImage raster;
    /** External load enabled. */
    private final boolean externEnabled;
    /** Raster offset. */
    private final int offset;
    /** Externally loaded. */
    private boolean externLoaded;

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

        externEnabled = getBoolean(false, ATT_RASTER_EXTERN, NODE_RASTERABLE);
        offset = getInteger(1, ATT_RASTER_OFFSET, NODE_RASTERABLE);

        if (hasNode(NODE_RASTERABLE) && !externEnabled)
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
     * Load raster with media externally.
     * 
     * @param save <code>true</code> to save generated (if) rasters, <code>false</code> else.
     * @param media The raster media (must not be <code>null</code>).
     */
    public void load(boolean save, Media media)
    {
        load(save, media, Collections.emptyList());
    }

    /**
     * Load raster with media externally.
     * 
     * @param save <code>true</code> to save generated (if) rasters, <code>false</code> else.
     * @param media The raster media (must not be <code>null</code>).
     * @param allowed The allowed raster indexes.
     */
    public synchronized void load(boolean save, Media media, Collection<Integer> allowed)
    {
        Check.notNull(media);

        if (!externLoaded)
        {
            synchronized (this)
            {
                if (externEnabled && !externLoaded)
                {
                    raster.loadRasters(save, media, UtilFile.removeExtension(getMedia().getName()), allowed);
                    externLoaded = true;
                }
            }
        }
    }

    /**
     * Get the rasters as read only.
     * 
     * @return The rasters.
     */
    public List<ImageBuffer> getRasters()
    {
        return raster.getRasters();
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
     * Get the raster offset.
     * 
     * @return The raster offset.
     */
    public int getRasterOffset()
    {
        return offset;
    }

    /**
     * Check if external loading is enabled.
     * 
     * @return <code>true</code> if enabled, <code>false</code> else.
     */
    public boolean isExtern()
    {
        return externEnabled;
    }
}
