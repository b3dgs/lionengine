/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.platform.background;

import java.util.ArrayList;
import java.util.List;

import com.b3dgs.lionengine.UtilityMath;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.UtilityImage;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.Sprite;

/**
 * Specific background element, supporting raster effects.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class BackgroundElementRastered
        extends BackgroundElement
{
    /**
     * Get filtered rgb from data.
     * 
     * @param rgb The input rgb.
     * @param fr The first red.
     * @param fg The first green.
     * @param fb The first blue.
     * @return The filtered color.
     */
    private static int filterRGB(int rgb, int fr, int fg, int fb)
    {
        if (-16711423 == rgb || 0 == rgb || 16711935 == rgb)
        {
            return rgb;
        }

        int nr = (rgb & 0xFF0000) + fr;
        if (nr < 0x000000)
        {
            nr = 0x000000;
        }
        if (nr > 0xFF0000)
        {
            nr = 0xFF0000;
        }

        int ng = (rgb & 0x00FF00) + fg;
        if (ng < 0x000000)
        {
            ng = 0x000000;
        }
        if (ng > 0x00FF00)
        {
            ng = 0x00FF00;
        }

        int nb = (rgb & 0x0000FF) + fb;
        if (nb < 0x000000)
        {
            nb = 0x000000;
        }
        if (nb > 0x0000FF)
        {
            nb = 0x0000FF;
        }

        final int a = rgb & 0xFF000000;
        final int r = nr;
        final int g = ng;
        final int b = nb;

        return a | r | g | b;
    }

    /** Rasters list. */
    private final List<Sprite> rasters;

    /**
     * Constructor.
     * 
     * @param mainX The main location x.
     * @param mainY The main location y.
     * @param sprite The sprite to be rastered.
     * @param rastersNumber The number of rasters.
     */
    public BackgroundElementRastered(int mainX, int mainY, Sprite sprite, int rastersNumber)
    {
        super(mainX, mainY, sprite);
        rasters = new ArrayList<>(rastersNumber);
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
        return rasters.get(UtilityMath.fixBetween(id, 0, rasters.size() - 1));
    }

    /**
     * Add a raster with specified color code.
     * 
     * @param sprite The original sprite.
     * @param fr The red color offset.
     * @param fg The green color offset.
     * @param fb The blue color offset.
     */
    protected void addRaster(Sprite sprite, int fr, int fg, int fb)
    {
        final ImageBuffer buf = sprite.getSurface();
        final ImageBuffer rasterBuf = UtilityImage.createImageBuffer(buf.getWidth(), buf.getHeight(),
                buf.getTransparency());

        for (int i = 0; i < rasterBuf.getWidth(); i++)
        {
            for (int j = 0; j < rasterBuf.getHeight(); j++)
            {
                rasterBuf.setRgb(i, j, BackgroundElementRastered.filterRGB(buf.getRgb(i, j), fr, fg, fb));
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
