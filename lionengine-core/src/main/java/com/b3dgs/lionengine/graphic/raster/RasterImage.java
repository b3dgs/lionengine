/*
 * Copyright (C) 2013-2024 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.graphic.raster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.UtilFile;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.ImageFormat;
import com.b3dgs.lionengine.graphic.drawable.ImageInfo;

/**
 * Represents a rastered image.
 */
public class RasterImage
{
    /** Maximum rasters. */
    public static final int MAX_RASTERS = 47;
    /** Number of line used by a raster. */
    public static final int LINES_PER_RASTER = 2;

    /** List of rasters. */
    private final List<ImageBuffer> rasters = new ArrayList<>(MAX_RASTERS);
    /** Original image. */
    private final ImageBuffer surface;
    /** Raster filename. */
    private Media rasterFile;
    /** Raster height. */
    private final int rasterHeight;

    /**
     * Create a rastered image.
     * 
     * @param media The image media (must not be <code>null</code>).
     * @param rasterFile The raster media (must not be <code>null</code>).
     * @param rasterHeight The height used by the raster (must be strictly superior to 0).
     * @throws LionEngineException If invalid media or raster file or height.
     */
    public RasterImage(Media media, Media rasterFile, int rasterHeight)
    {
        this(Graphics.getImageBuffer(media), rasterFile, rasterHeight);
    }

    /**
     * Create a rastered image.
     * 
     * @param surface The surface reference (must not be <code>null</code>).
     * @param rasterFile The raster media (must not be <code>null</code>).
     * @param rasterHeight The height used by the raster (must be strictly superior to 0).
     * @throws LionEngineException If invalid media or raster file or height.
     */
    public RasterImage(ImageBuffer surface, Media rasterFile, int rasterHeight)
    {
        super();

        Check.notNull(surface);
        Check.notNull(rasterFile);
        Check.superiorStrict(rasterHeight, 0);

        this.surface = surface;
        this.rasterFile = rasterFile;
        this.rasterHeight = rasterHeight;
    }

    /**
     * Load rasters.
     * 
     * @param save <code>true</code> to save generated (if) rasters, <code>false</code> else.
     * @param suffix The folder suffix, if save is <code>true</code> (must not be <code>null</code>).
     * @throws LionEngineException If the raster data from the media are invalid.
     */
    public void loadRasters(boolean save, String suffix)
    {
        Check.notNull(suffix);

        loadFromPalette(save, suffix, Collections.emptyList());
    }

    /**
     * Load rasters.
     * 
     * @param save <code>true</code> to save generated (if) rasters, <code>false</code> else.
     * @param media The raster media (must not be <code>null</code>).
     * @param suffix The folder suffix, if save is <code>true</code> (must not be <code>null</code>).
     * @param allowed The allowed raster indexes.
     * @throws LionEngineException If the raster data from the media are invalid.
     */
    public void loadRasters(boolean save, Media media, String suffix, Collection<Integer> allowed)
    {
        Check.notNull(suffix);

        rasterFile = media;
        loadFromPalette(save, suffix, allowed);
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
     * Load raster from palette image.
     * 
     * @param save <code>true</code> to save generated (if) rasters, <code>false</code> else.
     * @param suffix The folder suffix, if save is <code>true</code> (must not be <code>null</code>).
     * @param allowed The ignored raster indexes.
     */
    private void loadFromPalette(boolean save, String suffix, Collection<Integer> allowed)
    {
        final String folder = UtilFile.removeExtension(rasterFile.getName()) + Constant.UNDERSCORE + suffix;
        int rastersNumber = getRasterNumber(folder);
        ImageBuffer rasterPalette = null;
        ImageBuffer[] rastersBuffer = null;

        for (int i = 0; i < rastersNumber; i++)
        {
            if (allowed == null || allowed.isEmpty() || allowed.contains(Integer.valueOf(i)))
            {
                final String file = i + Constant.DOT + ImageFormat.PNG;
                final Media rasterMedia = Medias.create(rasterFile.getParentPath(), folder, file);
                final ImageBuffer rasterBuffer;
                if (rasterMedia.exists())
                {
                    rasterBuffer = Graphics.getImageBuffer(rasterMedia);
                    rasterBuffer.prepare();
                }
                else
                {
                    // CHECKSTYLE IGNORE LINE: NestedIfDepth
                    if (rastersBuffer == null)
                    {
                        surface.prepare();
                        rasterPalette = Graphics.getImageBuffer(rasterFile);
                        rastersBuffer = Graphics.getRasterBuffer(surface, rasterPalette);
                        rastersNumber = rasterPalette.getHeight() - 1;
                    }
                    rasterBuffer = rastersBuffer[i];
                    // CHECKSTYLE IGNORE LINE: NestedIfDepth
                    if (save)
                    {
                        Graphics.saveImage(rasterBuffer, rasterMedia);
                    }
                }
                rasters.add(rasterBuffer);
            }
            else
            {
                rasters.add(surface);
            }
        }
    }

    /**
     * Get raster number in folder.
     * 
     * @param folder The folder reference.
     * @return The raster number found.
     */
    private int getRasterNumber(String folder)
    {
        final Collection<Media> files = Medias.create(rasterFile.getParentPath(), folder).getMedias();
        if (files.isEmpty())
        {
            return ImageInfo.get(rasterFile).getHeight() - 1;
        }
        return files.size();
    }
}
