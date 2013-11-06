/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.platform.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.UtilityImage;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.purview.Rasterable;

/**
 * Rastered version of a platform tile map.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @param <C> Collision type.
 * @param <T> Tile type used.
 */
public abstract class MapTilePlatformRastered<C extends Enum<C>, T extends TilePlatform<C>>
        extends MapTilePlatform<C, T>
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
     * Constructor.
     * 
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     */
    public MapTilePlatformRastered(int tileWidth, int tileHeight)
    {
        super(tileWidth, tileHeight);
        rasterPatterns = new TreeMap<>();
        rasterLoaded = false;
    }

    /**
     * Set raster file and smoothed flag.
     * 
     * @param raster The raster media.
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
     * @param tile The input tile.
     * @return The raster index.
     */
    public int getRasterIndex(T tile)
    {
        final int value = tile.getY() / getTileHeight();
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
     */
    private void loadRaster(String directory, Integer pattern, int[][] rasters)
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
                            color[c] = UtilityImage.getRasterColor(i, data, maxRasters);
                            colorNext[c] = UtilityImage.getRasterColor(i + 1, data, maxRasters);
                        }
                        else
                        {
                            color[c] = UtilityImage.getRasterColor(maxRasters - i, data, maxRasters);
                            colorNext[c] = UtilityImage.getRasterColor(maxRasters - i - 1, data, maxRasters);
                        }
                    }
                    else
                    {
                        color[c] = UtilityImage.getRasterColor(i, data, maxRasters);
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
     */
    private void addRasterPattern(String directory, Integer pattern, int rasterID, int fr, int fg, int fb, int er,
            int eg, int eb)
    {
        final SpriteTiled original = super.getPattern(pattern);
        final ImageBuffer buf = original.getSurface();
        final ImageBuffer rasterBuf = UtilityImage.getRasterBuffer(buf, fr, fg, fb, er, eg, eb, getTileHeight());

        addRasterPattern(pattern, rasterBuf, getTileWidth(), getTileHeight());
    }

    /**
     * Add a raster pattern.
     * 
     * @param pattern The current pattern.
     * @param surface The surface reference.
     * @param tw The tile width.
     * @param th The tile height.
     */
    private void addRasterPattern(Integer pattern, ImageBuffer surface, int tw, int th)
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
     * MapTilePlatform
     */

    @Override
    public void loadPatterns(Media directory)
    {
        super.loadPatterns(directory);
        final String path = directory.getPath();
        if (!rasterLoaded)
        {
            if (rasterFile != null)
            {
                final Set<Integer> patterns = getPatterns();
                final Iterator<Integer> itr = patterns.iterator();
                final int[][] rasters = UtilityImage.loadRaster(rasterFile);

                while (itr.hasNext())
                {
                    final Integer pattern = itr.next();
                    loadRaster(path, pattern, rasters);
                }
            }
            rasterLoaded = true;
        }
    }

    @Override
    protected void renderingTile(Graphic g, T tile, int x, int y, int screenHeight)
    {
        final SpriteTiled ts;
        if (rasterFile != null)
        {
            ts = getRasterPattern(tile.getPattern(), getRasterIndex(tile));
        }
        else
        {
            ts = super.getPattern(tile.getPattern());
        }
        ts.render(g, tile.getNumber(), x, y + screenHeight);
    }
}
