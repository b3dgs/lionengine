/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.Graphics;
import com.b3dgs.lionengine.drawable.Drawable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.Services;
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.tile.Tile;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.Raster;
import com.b3dgs.lionengine.util.UtilConversion;

/**
 * Rastered map tile implementation.
 */
public class MapTileRasteredModel extends FeatureModel implements MapTileRastered
{
    /** List of rastered sheets. */
    private final Map<Integer, List<SpriteTiled>> rasterSheets = new TreeMap<Integer, List<SpriteTiled>>();
    /** Map tile reference. */
    private MapTile map;
    /** Rasters smooth flag. */
    private boolean smooth;

    /**
     * Create a map tile rastered.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * </ul>
     */
    public MapTileRasteredModel()
    {
        super();
    }

    /**
     * Load raster from data.
     * 
     * @param sheet The current sheet.
     * @param raster The raster data.
     * @throws LionEngineException If arguments are invalid.
     */
    private void loadRaster(Integer sheet, Raster raster)
    {
        final int max = UtilConversion.boolToInt(smooth) + 1;
        for (int m = 0; m < max; m++)
        {
            for (int i = 1; i <= Rasterable.MAX_RASTERS; i++)
            {
                final RasterColor red = RasterColor.load(raster.getRed(), m, i, smooth);
                final RasterColor green = RasterColor.load(raster.getGreen(), m, i, smooth);
                final RasterColor blue = RasterColor.load(raster.getBlue(), m, i, smooth);

                addRasterSheet(sheet, red, green, blue);
            }
        }
    }

    /**
     * Add a raster sheet.
     * 
     * @param sheet The current sheet.
     * @param red The red color transition.
     * @param green The green color transition.
     * @param blue The blue color transition.
     * @throws LionEngineException If arguments are invalid.
     */
    private void addRasterSheet(Integer sheet, RasterColor red, RasterColor green, RasterColor blue)
    {
        final SpriteTiled original = map.getSheet(sheet);
        final ImageBuffer buf = original.getSurface();
        final ImageBuffer rasterBuf = Graphics.getRasterBuffer(buf,
                                                               red.getStart(),
                                                               green.getStart(),
                                                               blue.getStart(),
                                                               red.getEnd(),
                                                               green.getEnd(),
                                                               blue.getEnd(),
                                                               map.getTileHeight());

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
    public void prepare(Featurable owner, Services services)
    {
        super.prepare(owner, services);

        map = services.get(MapTile.class);
    }

    @Override
    public void loadSheets(Media rasterConfig, boolean smooth)
    {
        this.smooth = smooth;

        final Raster raster = Raster.load(rasterConfig);
        final Collection<Integer> sheets = map.getSheets();
        final Iterator<Integer> itr = sheets.iterator();

        while (itr.hasNext())
        {
            final Integer sheet = itr.next();
            loadRaster(sheet, raster);
        }
    }

    @Override
    public void renderTile(Graphic g, MapTile map, Tile tile, int x, int y)
    {
        final Integer sheet = tile.getSheet();
        final int number = tile.getNumber();
        final SpriteTiled raster = getRasterSheet(sheet, getRasterIndex(tile.getInTileY()));
        raster.setLocation(x, y);
        raster.setTile(number);
        raster.render(g);
    }

    @Override
    public int getRasterIndex(int ty)
    {
        int index = ty % Rasterable.MAX_RASTERS_R;
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
