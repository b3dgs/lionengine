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
import com.b3dgs.lionengine.game.trait.Rasterable;

/**
 * Rastered version of a map tile game.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class MapTileRasteredModel
        implements MapTileRastered
{
    /** Map tile reference. */
    private final MapTile map;
    /** List of rastered sheets. */
    private final TreeMap<Integer, List<SpriteTiled>> rasterSheets;
    /** File describing the raster. */
    private Media rasterFile;
    /** Rasters smooth flag. */
    private boolean smooth;
    /** Loaded state. */
    private boolean rasterLoaded;

    /**
     * Create a map tile rastered.
     * 
     * @param map The map reference.
     */
    public MapTileRasteredModel(MapTile map)
    {
        this.map = map;
        rasterSheets = new TreeMap<>();
        rasterLoaded = false;
    }

    /**
     * Render a specific tile to specified location.
     * 
     * @param g The graphic output.
     * @param tile The tile to render.
     * @param sheet The tile sheet.
     * @param number The tile number.
     * @param x The location x.
     * @param y The location y.
     */
    protected void renderingTile(Graphic g, Tile tile, Integer sheet, int number, int x, int y)
    {
        final SpriteTiled raster;
        if (rasterLoaded)
        {
            raster = getRasterSheet(sheet, getRasterIndex(tile.getY()));
        }
        else
        {
            raster = map.getSheet(sheet);
        }
        raster.setLocation(x, y);
        raster.setTile(number);
        raster.render(g);
    }

    /**
     * Load raster from data.
     * 
     * @param directory The current tile directory.
     * @param sheet The current sheet.
     * @param rasters The rasters data.
     * @throws LionEngineException If arguments are invalid.
     */
    private void loadRaster(String directory, Integer sheet, int[][] rasters) throws LionEngineException
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
                addRasterSheet(directory, sheet, i, color[0], color[1], color[2], colorNext[0], colorNext[1],
                        colorNext[2]);
            }
        }
    }

    /**
     * Add a raster sheet.
     * 
     * @param directory The current tiles directory.
     * @param sheet The current sheet.
     * @param rasterID The raster id.
     * @param fr The first red.
     * @param fg The first green.
     * @param fb The first blue.
     * @param er The end red.
     * @param eg The end green.
     * @param eb The end blue.
     * @throws LionEngineException If arguments are invalid.
     */
    private void addRasterSheet(String directory, Integer sheet, int rasterID, int fr, int fg, int fb, int er, int eg,
            int eb) throws LionEngineException
    {
        final SpriteTiled original = map.getSheet(sheet);
        final ImageBuffer buf = original.getSurface();
        final ImageBuffer rasterBuf = Core.GRAPHIC.getRasterBuffer(buf, fr, fg, fb, er, eg, eb, map.getTileHeight());

        List<SpriteTiled> rasters = rasterSheets.get(sheet);
        if (rasters == null)
        {
            rasters = new ArrayList<>(Rasterable.MAX_RASTERS);
            rasterSheets.put(sheet, rasters);
        }
        final SpriteTiled raster = Drawable.loadSpriteTiled(rasterBuf, map.getTileWidth(), map.getTileHeight());
        rasters.add(raster);
    }

    /*
     * MapTileRastered
     */

    @Override
    public void loadSheets(Media directory) throws LionEngineException
    {
        final String path = directory.getPath();
        if (!rasterLoaded && rasterFile != null)
        {
            final Collection<Integer> sheets = map.getSheets();
            final Iterator<Integer> itr = sheets.iterator();
            final int[][] rasters = Core.GRAPHIC.loadRaster(rasterFile);

            while (itr.hasNext())
            {
                final Integer sheet = itr.next();
                loadRaster(path, sheet, rasters);
            }
            rasterLoaded = true;
        }
    }

    @Override
    public void setRaster(Media raster, boolean smooth)
    {
        rasterFile = raster;
        this.smooth = smooth;
    }

    @Override
    public int getRasterIndex(int ty)
    {
        final int value = ty / map.getTileHeight();
        int index = value % Rasterable.MAX_RASTERS_R;
        if (!smooth && index > Rasterable.MAX_RASTERS_M)
        {
            index = Rasterable.MAX_RASTERS_M - (index - Rasterable.MAX_RASTERS);
        }
        return index;
    }

    @Override
    public SpriteTiled getRasterSheet(Integer sheet, int rasterID)
    {
        return rasterSheets.get(sheet).get(rasterID);
    }

    @Override
    public MapTile getMap()
    {
        return map;
    }
}
