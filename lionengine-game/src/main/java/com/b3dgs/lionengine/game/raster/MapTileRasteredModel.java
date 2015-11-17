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
package com.b3dgs.lionengine.game.raster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.ImageBuffer;
import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.object.Services;
import com.b3dgs.lionengine.game.object.SetupSurfaceRastered;
import com.b3dgs.lionengine.game.tile.Tile;

/**
 * Rastered map tile implementation.
 */
public class MapTileRasteredModel implements MapTileRastered
{
    /** List of rastered sheets. */
    private final Map<Integer, List<SpriteTiled>> rasterSheets = new TreeMap<Integer, List<SpriteTiled>>();
    /** Map tile reference. */
    private final MapTile map;
    /** Rasters smooth flag. */
    private boolean smooth;

    /**
     * Create a map tile rastered.
     * <p>
     * The {@link Services} must provide the following services:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * </ul>
     * 
     * @param services The services reference.
     * @throws LionEngineException If services not found.
     */
    public MapTileRasteredModel(Services services)
    {
        map = services.get(MapTile.class);
    }

    /**
     * Load raster from data.
     * 
     * @param sheet The current sheet.
     * @param rasters The rasters data.
     * @throws LionEngineException If arguments are invalid.
     */
    private void loadRaster(Integer sheet, int[][] rasters)
    {
        final int[] color = new int[rasters.length];
        final int[] colorNext = new int[rasters.length];
        final int max = UtilConversion.boolToInt(smooth) + 1;

        for (int m = 0; m < max; m++)
        {
            for (int i = 1; i <= Rasterable.MAX_RASTERS; i++)
            {
                SetupSurfaceRastered.loadRaster(rasters, color, colorNext, m, i, smooth);
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
    {
        final SpriteTiled original = map.getSheet(sheet);
        final ImageBuffer buf = original.getSurface();
        final ImageBuffer rasterBuf = Graphics.getRasterBuffer(buf, fr, fg, fb, er, eg, eb, map.getTileHeight());

        List<SpriteTiled> rasters = rasterSheets.get(sheet);
        if (rasters == null)
        {
            rasters = new ArrayList<SpriteTiled>(Rasterable.MAX_RASTERS);
            rasterSheets.put(sheet, rasters);
        }
        final SpriteTiled raster = Drawable.loadSpriteTiled(rasterBuf, map.getTileWidth(), map.getTileHeight());
        rasters.add(raster);
    }

    /*
     * MapTileRastered
     */

    @Override
    public void loadSheets(Media rasterConfig, boolean smooth)
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
    public void renderTile(Graphic g, Tile tile, int x, int y)
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
}
