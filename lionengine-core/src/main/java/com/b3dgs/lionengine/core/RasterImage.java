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
package com.b3dgs.lionengine.core;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.ImageFormat;
import com.b3dgs.lionengine.graphic.Raster;
import com.b3dgs.lionengine.graphic.RasterColor;
import com.b3dgs.lionengine.util.UtilConversion;
import com.b3dgs.lionengine.util.UtilFile;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Represents a rastered image.
 */
public class RasterImage
{
    /** List of rasters. */
    private final List<ImageBuffer> rasters = new ArrayList<ImageBuffer>(RasterColor.MAX_RASTERS);
    /** Original image. */
    private final ImageBuffer surface;
    /** Raster filename. */
    private final Media rasterFile;
    /** Raster smooth flag. */
    private final boolean rasterSmooth;
    /** Raster height. */
    private final int rasterHeight;

    /**
     * Create a rastered image.
     * 
     * @param media The image media.
     * @param rasterFile The raster media.
     * @param rasterHeight The height used by the raster (must be strictly superior to 0).
     * @param smooth <code>true</code> for smoothed raster, <code>false</code> else.
     * @throws LionEngineException If invalid media or raster file or height.
     */
    public RasterImage(Media media, Media rasterFile, int rasterHeight, boolean smooth)
    {
        this(Graphics.getImageBuffer(media), rasterFile, rasterHeight, smooth);
    }

    /**
     * Create a rastered image.
     * 
     * @param surface The surface reference.
     * @param rasterFile The raster media.
     * @param rasterHeight The height used by the raster (must be strictly superior to 0).
     * @param smooth <code>true</code> for smoothed raster, <code>false</code> else.
     * @throws LionEngineException If invalid media or raster file or height.
     */
    public RasterImage(ImageBuffer surface, Media rasterFile, int rasterHeight, boolean smooth)
    {
        super();

        Check.notNull(surface);
        Check.notNull(rasterFile);
        Check.superiorStrict(rasterHeight, 0);

        this.surface = surface;
        this.rasterFile = rasterFile;
        this.rasterHeight = rasterHeight;
        rasterSmooth = smooth;
    }

    /**
     * Load rasters.
     * 
     * @param imageHeight The local image height.
     * @throws LionEngineException If the raster data from the media are invalid.
     */
    public void loadRasters(int imageHeight)
    {
        loadRasters(imageHeight, false, Constant.EMPTY_STRING);
    }

    /**
     * Load rasters.
     * 
     * @param imageHeight The local image height.
     * @param save <code>true</code> to save generated (if) rasters, <code>false</code> else.
     * @param prefix The folder prefix (if save is <code>true</code>).
     * @throws LionEngineException If the raster data from the media are invalid.
     */
    public void loadRasters(int imageHeight, boolean save, String prefix)
    {
        final Raster raster = Raster.load(rasterFile);
        final int max = UtilConversion.boolToInt(rasterSmooth) + 1;

        for (int m = 0; m < max; m++)
        {
            for (int i = 1; i <= RasterColor.MAX_RASTERS; i++)
            {
                final String folder = prefix + Constant.UNDERSCORE + UtilFile.removeExtension(rasterFile.getName());
                final String file = String.valueOf(i + m * RasterColor.MAX_RASTERS) + Constant.DOT + ImageFormat.PNG;
                final Media rasterMedia = Medias.create(rasterFile.getParentPath(), folder, file);

                final ImageBuffer rasterBuffer = createRaster(rasterMedia, raster, m, i, imageHeight, save);
                rasters.add(rasterBuffer);
            }
        }
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
     * Get all rasters.
     * 
     * @return The rasters.
     */
    public List<ImageBuffer> getRasters()
    {
        return rasters;
    }

    /**
     * Get the raster from its ID.
     * 
     * @param id The raster ID (must be superior or equal to 0).
     * @return The image buffer representing the raster.
     */
    public ImageBuffer getRaster(int id)
    {
        return rasters.get(UtilMath.clamp(id, 0, rasters.size() - 1));
    }

    /**
     * Get the raster height.
     * 
     * @return The raster height.
     */
    public int getHeight()
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
     * Create raster from data or load from cache.
     * 
     * @param rasterMedia The raster media?
     * @param raster The raster data.
     * @param m The smooth index.
     * @param i The raster index.
     * @param imageHeight The image height.
     * @param save <code>true</code> to save generated raster, <code>false</code> else.
     * @return The created raster.
     */
    private ImageBuffer createRaster(Media rasterMedia, Raster raster, int m, int i, int imageHeight, boolean save)
    {
        final ImageBuffer rasterBuffer;
        if (rasterMedia.exists())
        {
            rasterBuffer = Graphics.getImageBuffer(rasterMedia);
            rasterBuffer.prepare();
        }
        else
        {
            final RasterColor red = RasterColor.load(raster.getRed(), m, i, rasterSmooth);
            final RasterColor green = RasterColor.load(raster.getGreen(), m, i, rasterSmooth);
            final RasterColor blue = RasterColor.load(raster.getBlue(), m, i, rasterSmooth);

            rasterBuffer = Graphics.getRasterBuffer(surface,
                                                    red.getStart(),
                                                    green.getStart(),
                                                    blue.getStart(),
                                                    red.getEnd(),
                                                    green.getEnd(),
                                                    blue.getEnd(),
                                                    imageHeight);

            if (save)
            {
                Graphics.saveImage(rasterBuffer, rasterMedia);
            }
        }
        return rasterBuffer;
    }
}
