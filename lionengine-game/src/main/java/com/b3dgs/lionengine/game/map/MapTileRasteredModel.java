/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.trait.rasterable.Rasterable;

/**
 * Rastered map tile implementation.
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
    /** Rasters smooth flag. */
    private boolean smooth;

    /**
     * Create a map tile rastered.
     * 
     * @param map The map reference.
     */
    public MapTileRasteredModel(MapTile map)
    {
        this.map = map;
        rasterSheets = new TreeMap<>();
    }

    /**
     * Load raster from data.
     * 
     * @param sheet The current sheet.
     * @param rasters The rasters data.
     * @throws LionEngineException If arguments are invalid.
     */
    private void loadRaster(Integer sheet, int[][] rasters) throws LionEngineException
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
                addRasterSheet(sheet, color[0], color[1], color[2], colorNext[0], colorNext[1], colorNext[2]);
            }
        }
    }

    /**
     * Add a raster sheet.
     * 
     * @param sheet The current sheet.
     * @param fr The first red.
     * @param fg The first green.
     * @param fb The first blue.
     * @param er The end red.
     * @param eg The end green.
     * @param eb The end blue.
     * @throws LionEngineException If arguments are invalid.
     */
    private void addRasterSheet(Integer sheet, int fr, int fg, int fb, int er, int eg, int eb)
            throws LionEngineException
    {
        final SpriteTiled original = map.getSheet(sheet);
        final ImageBuffer buf = original.getSurface();
        final ImageBuffer rasterBuf = Graphics.getRasterBuffer(buf, fr, fg, fb, er, eg, eb, map.getTileHeight());

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
    public void loadSheets(Media sheetsConfig, Media rasterConfig, boolean smooth) throws LionEngineException
    {
        this.smooth = smooth;

        final int[][] rasters = Graphics.loadRaster(rasterConfig);
        final Collection<Integer> sheets = map.getSheets();
        final Iterator<Integer> itr = sheets.iterator();

        while (itr.hasNext())
        {
            final Integer sheet = itr.next();
            loadRaster(sheet, rasters);
        }
    }

    @Override
    public void renderTile(Graphic g, Tile tile, int x, int y) throws LionEngineException
    {
        final Integer sheet = tile.getSheet();
        final int number = tile.getNumber();
        final SpriteTiled raster = getRasterSheet(sheet, getRasterIndex(tile.getY()));
        raster.setLocation(x, y);
        raster.setTile(number);
        raster.render(g);
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
    public SpriteTiled getRasterSheet(Integer sheet, int rasterIndex)
    {
        return rasterSheets.get(sheet).get(rasterIndex);
    }

    @Override
    public MapTile getMap()
    {
        return map;
    }
}
