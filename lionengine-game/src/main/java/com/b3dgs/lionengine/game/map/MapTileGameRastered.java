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
package com.b3dgs.lionengine.game.map;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import com.b3dgs.lionengine.ColorRgba;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.core.Core;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.purview.Rasterable;

/**
 * Rastered version of a map tile game.
 * 
 * @param <T> Tile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public abstract class MapTileGameRastered<T extends TileGame>
        extends MapTileGame<T>
{
    /** List of rastered patterns. */
    private final TreeMap<Integer, List<SpriteTiled>> rasterPatterns;
    /** File describing the raster. */
    private Media rasterFile;
    /** Rasters smooth flag. */
    private boolean smooth;
    /** Loaded state. */
    private boolean rasterLoaded;

    /**
     * Constructor base.
     * 
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     * @param collisions The collisions list.
     */
    public MapTileGameRastered(int tileWidth, int tileHeight, CollisionTile[] collisions)
    {
        super(tileWidth, tileHeight, collisions);
        rasterPatterns = new TreeMap<>();
        rasterLoaded = false;
    }

    /**
     * Set raster file and smoothed flag.
     * 
     * @param raster The raster media (may be <code>null</code>).
     * @param smooth <code>true</code> for a smoothed raster (may be slower), <code>false</code> else.
     */
    public void setRaster(Media raster, boolean smooth)
    {
        this.rasterFile = raster;
        this.smooth = smooth;
    }

    /**
     * Get raster index from input tile (depending of its height).
     * 
     * @param ty The vertical tile location.
     * @return The raster index.
     */
    public int getRasterIndex(int ty)
    {
        final int value = ty / getTileHeight();
        int index = value % Rasterable.MAX_RASTERS_R;
        if (!smooth && index > Rasterable.MAX_RASTERS_M)
        {
            index = Rasterable.MAX_RASTERS_M - (index - Rasterable.MAX_RASTERS);
        }
        return index;
    }

    /**
     * Get a tilesheet from its pattern and raster id.
     * 
     * @param pattern The pattern number
     * @param rasterID The raster id.
     * @return The tilesheet reference.
     */
    public SpriteTiled getRasterPattern(Integer pattern, int rasterID)
    {
        return rasterPatterns.get(pattern).get(rasterID);
    }

    /**
     * Load raster from data.
     * 
     * @param directory The current tile directory.
     * @param pattern The current pattern.
     * @param rasters The rasters data.
     * @throws LionEngineException If arguments are invalid.
     */
    private void loadRaster(String directory, Integer pattern, int[][] rasters) throws LionEngineException
    {
        final int[] color = new int[rasters.length];
        final int[] colorNext = new int[rasters.length];
        final int max = smooth ? 2 : 1;
        final int maxRasters = Rasterable.MAX_RASTERS;

        for (int m = 0; m < max; m++)
        {
            for (int i = 1; i <= maxRasters; i++)
            {
                for (int c = 0; c < rasters.length; c++)
                {
                    final int[] data = rasters[c];
                    if (smooth)
                    {
                        if (m == 0)
                        {
                            color[c] = ColorRgba.getRasterColor(i, data, maxRasters);
                            colorNext[c] = ColorRgba.getRasterColor(i + 1, data, maxRasters);
                        }
                        else
                        {
                            color[c] = ColorRgba.getRasterColor(maxRasters - i, data, maxRasters);
                            colorNext[c] = ColorRgba.getRasterColor(maxRasters - i - 1, data, maxRasters);
                        }
                    }
                    else
                    {
                        color[c] = ColorRgba.getRasterColor(i, data, maxRasters);
                        colorNext[c] = color[c];
                    }
                }
                addRasterPattern(directory, pattern, i, color[0], color[1], color[2], colorNext[0], colorNext[1],
                        colorNext[2]);
            }
        }
    }

    /**
     * Add a raster pattern.
     * 
     * @param directory The current tiles directory.
     * @param pattern The current pattern.
     * @param rasterID The raster id.
     * @param fr The first red.
     * @param fg The first green.
     * @param fb The first blue.
     * @param er The end red.
     * @param eg The end green.
     * @param eb The end blue.
     * @throws LionEngineException If arguments are invalid.
     */
    private void addRasterPattern(String directory, Integer pattern, int rasterID, int fr, int fg, int fb, int er,
            int eg, int eb) throws LionEngineException
    {
        final SpriteTiled original = super.getPattern(pattern);
        final ImageBuffer buf = original.getSurface();
        final ImageBuffer rasterBuf = Core.GRAPHIC.getRasterBuffer(buf, fr, fg, fb, er, eg, eb, getTileHeight());

        addRasterPattern(pattern, rasterBuf, getTileWidth(), getTileHeight());
    }

    /**
     * Add a raster pattern.
     * 
     * @param pattern The current pattern.
     * @param surface The surface reference.
     * @param tw The tile width.
     * @param th The tile height.
     * @throws LionEngineException If arguments are invalid.
     */
    private void addRasterPattern(Integer pattern, ImageBuffer surface, int tw, int th) throws LionEngineException
    {
        List<SpriteTiled> rasters = rasterPatterns.get(pattern);
        if (rasters == null)
        {
            rasters = new ArrayList<>(Rasterable.MAX_RASTERS);
            rasterPatterns.put(pattern, rasters);
        }
        final SpriteTiled raster = Drawable.loadSpriteTiled(surface, tw, th);
        rasters.add(raster);
    }

    /*
     * MapTileGame
     */

    @Override
    public void loadPatterns(Media directory) throws LionEngineException
    {
        super.loadPatterns(directory);
        final String path = directory.getPath();
        if (!rasterLoaded && rasterFile != null)
        {
            final Collection<Integer> patterns = getPatterns();
            final Iterator<Integer> itr = patterns.iterator();
            final int[][] rasters = Core.GRAPHIC.loadRaster(rasterFile);

            while (itr.hasNext())
            {
                final Integer pattern = itr.next();
                loadRaster(path, pattern, rasters);
            }
            rasterLoaded = true;
        }
    }

    @Override
    protected void renderingTile(Graphic g, T tile, Integer pattern, int number, int x, int y)
    {
        final SpriteTiled ts;
        if (rasterLoaded)
        {
            ts = getRasterPattern(pattern, getRasterIndex(tile.getY()));
        }
        else
        {
            ts = super.getPattern(pattern);
        }
        ts.render(g, number, x, y);
    }
}
