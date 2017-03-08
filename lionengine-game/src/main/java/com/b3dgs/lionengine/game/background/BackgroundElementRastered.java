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
package com.b3dgs.lionengine.game.background;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.graphic.Graphics;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Sprite;
import com.b3dgs.lionengine.graphic.UtilColor;
import com.b3dgs.lionengine.util.UtilMath;

/**
 * Specific background element, supporting raster effects.
 */
public abstract class BackgroundElementRastered extends BackgroundElement
{
    /** Rasters list. */
    private final List<Sprite> rasters;

    /**
     * Create a rastered background element.
     * 
     * @param mainX The main location x.
     * @param mainY The main location y.
     * @param sprite The sprite to be rastered.
     * @param rastersNumber The number of rasters.
     */
    public BackgroundElementRastered(int mainX, int mainY, Sprite sprite, int rastersNumber)
    {
        super(mainX, mainY, sprite);
        rasters = new ArrayList<Sprite>(rastersNumber);
        initialize(sprite, rastersNumber);
    }

    /**
     * Load rasters from original sprite.
     * 
     * @param sprite The original sprite.
     * @param rastersNumber The number of rasters to use.
     */
    protected abstract void load(Sprite sprite, int rastersNumber);

    /**
     * Get raster surface from its id.
     * 
     * @param id The raster id.
     * @return The raster surface.
     */
    public Sprite getRaster(int id)
    {
        return rasters.get(UtilMath.clamp(id, 0, rasters.size() - 1));
    }

    /**
     * Add a raster with specified color code.
     * 
     * @param sprite The original sprite.
     * @param fr The red color offset.
     * @param fg The green color offset.
     * @param fb The blue color offset.
     * @throws LionEngineException If arguments are invalid.
     */
    protected void addRaster(Sprite sprite, int fr, int fg, int fb)
    {
        final ImageBuffer buf = sprite.getSurface();
        final ImageBuffer rasterBuf = Graphics.getImageBuffer(buf);

        for (int i = 0; i < rasterBuf.getWidth(); i++)
        {
            for (int j = 0; j < rasterBuf.getHeight(); j++)
            {
                final int rgb = buf.getRgb(i, j);
                final int filtered = UtilColor.filterRgb(rgb, fr, fg, fb);
                rasterBuf.setRgb(i, j, filtered);
            }
        }

        final Sprite raster = Drawable.loadSpriteTiled(rasterBuf, sprite.getWidth(), sprite.getHeight());
        rasters.add(raster);
    }

    /**
     * Initialize rastered element.
     * 
     * @param sprite The sprite reference.
     * @param rastersNumber The number of rasters
     */
    private void initialize(Sprite sprite, int rastersNumber)
    {
        load(sprite, rastersNumber);
    }
}
