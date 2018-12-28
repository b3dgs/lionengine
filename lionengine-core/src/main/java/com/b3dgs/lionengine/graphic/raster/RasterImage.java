/*
 * Copyright (C) 2013-2018 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.raster;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.ImageFormat;

/**
 * Represents a rastered image.
 */
public class RasterImage
{
    /** Maximum rasters. */
    public static final int MAX_RASTERS = 15;
    /** Maximum rasters R. */
    public static final int MAX_RASTERS_R = MAX_RASTERS * 2 - 1;
    /** Maximum rasters M. */
    public static final int MAX_RASTERS_M = MAX_RASTERS - 1;

    /**
     * Get raster color.
     * 
     * @param i The color offset.
     * @param data The raster data (must not be <code>null</code>).
     * @return The rastered color.
     * @throws LionEngineException If invalid arguments.
     */
    private static double getRasterFactor(int i, RasterData data)
    {
        Check.notNull(data);

        final double force = data.getForce();
        final double amplitude = data.getAmplitude();
        final int offset = data.getOffset();

        if (0 == data.getType())
        {
            return force * UtilMath.sin(i * amplitude + offset);
        }
        return force * UtilMath.cos(i * amplitude + offset);
    }

    /** List of rasters. */
    private final List<ImageBuffer> rasters = new ArrayList<>(MAX_RASTERS);
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
     * @param media The image media (must not be <code>null</code>).
     * @param rasterFile The raster media (must not be <code>null</code>).
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
     * @param surface The surface reference (must not be <code>null</code>).
     * @param rasterFile The raster media (must not be <code>null</code>).
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
     * @param prefix The folder prefix, if save is <code>true</code> (must not be <code>null</code>).
     * @throws LionEngineException If the raster data from the media are invalid.
     */
    public void loadRasters(int imageHeight, boolean save, String prefix)
    {
        Check.notNull(prefix);

        final Raster raster = Raster.load(rasterFile);
        final int max = UtilConversion.boolToInt(rasterSmooth) + 1;

        for (int m = 0; m < max; m++)
        {
            for (int i = 0; i < MAX_RASTERS; i++)
            {
                final String folder = prefix + Constant.UNDERSCORE + UtilFile.removeExtension(rasterFile.getName());
                final String file = String.valueOf(i + m * MAX_RASTERS) + Constant.DOT + ImageFormat.PNG;
                final Media rasterMedia = Medias.create(rasterFile.getParentPath(), folder, file);

                final ImageBuffer rasterBuffer = createRaster(rasterMedia, raster, i, save);
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
     * Get all rasters as read only.
     * 
     * @return The rasters.
     */
    public List<ImageBuffer> getRasters()
    {
        return Collections.unmodifiableList(rasters);
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
     * @param rasterMedia The raster media.
     * @param raster The raster data.
     * @param i The raster index.
     * @param save <code>true</code> to save generated raster, <code>false</code> else.
     * @return The created raster.
     */
    private ImageBuffer createRaster(Media rasterMedia, Raster raster, int i, boolean save)
    {
        final ImageBuffer rasterBuffer;
        if (rasterMedia.exists())
        {
            rasterBuffer = Graphics.getImageBuffer(rasterMedia);
            rasterBuffer.prepare();
        }
        else
        {
            final double fr = getRasterFactor(i, raster.getRed());
            final double fg = getRasterFactor(i, raster.getGreen());
            final double fb = getRasterFactor(i, raster.getBlue());

            rasterBuffer = Graphics.getRasterBuffer(surface, fr, fg, fb);

            if (save)
            {
                Graphics.saveImage(rasterBuffer, rasterMedia);
            }
        }
        return rasterBuffer;
    }
}
