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
package com.b3dgs.lionengine.game.feature.tile.map.raster;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.core.RasterImage;
import com.b3dgs.lionengine.core.drawable.Drawable;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.RasterColor;
import com.b3dgs.lionengine.graphic.SpriteTiled;

/**
 * Rastered map tile implementation.
 */
public class MapTileRasteredModel extends FeatureModel implements MapTileRastered
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
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * </ul>
     * 
     * @param services The services reference.
     */
    public MapTileRasteredModel(Services services)
    {
        super();

        map = services.get(MapTile.class);
    }

    /**
     * Get the associated raster sheets for a sheet number. Create it if needed.
     * 
     * @param sheet The sheet number
     * @return The raster sheets collection found.
     */
    private List<SpriteTiled> getRasters(Integer sheet)
    {
        List<SpriteTiled> rasters = rasterSheets.get(sheet);
        if (rasters == null)
        {
            rasters = new ArrayList<SpriteTiled>(RasterColor.MAX_RASTERS);
            rasterSheets.put(sheet, rasters);
        }
        return rasters;
    }

    /*
     * MapTileRastered
     */

    @Override
    public void loadSheets(Media rasterConfig, boolean smooth)
    {
        final Collection<Integer> sheets = map.getSheets();
        final Iterator<Integer> itr = sheets.iterator();
        final int th = map.getTileHeight();

        while (itr.hasNext())
        {
            final Integer sheet = itr.next();
            final RasterImage raster = new RasterImage(map.getSheet(sheet).getSurface(), rasterConfig, th, smooth);
            raster.loadRasters(map.getTileHeight(), false, sheet.toString());

            final List<SpriteTiled> rastersSheet = getRasters(sheet);
            for (final ImageBuffer bufferRaster : raster.getRasters())
            {
                final SpriteTiled sheetRaster = Drawable.loadSpriteTiled(bufferRaster, map.getTileWidth(), th);
                rastersSheet.add(sheetRaster);
            }
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
        int index = ty % RasterColor.MAX_RASTERS_R;
        if (!smooth && index > RasterColor.MAX_RASTERS_M)
        {
            index = RasterColor.MAX_RASTERS_M - (index - RasterColor.MAX_RASTERS);
        }
        return index;
    }

    @Override
    public SpriteTiled getRasterSheet(Integer sheet, int rasterIndex)
    {
        return rasterSheets.get(sheet).get(rasterIndex);
    }
}
