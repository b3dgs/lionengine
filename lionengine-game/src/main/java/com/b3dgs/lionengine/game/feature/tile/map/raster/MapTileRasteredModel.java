/*
 * Copyright (C) 2013-2021 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map.raster;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Featurable;
import com.b3dgs.lionengine.game.feature.FeatureAbstract;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileSurface;
import com.b3dgs.lionengine.graphic.Graphic;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.Drawable;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;
import com.b3dgs.lionengine.graphic.raster.RasterImage;

/**
 * Rastered map tile implementation.
 */
public class MapTileRasteredModel extends FeatureAbstract implements MapTileRastered
{
    /** List of rastered sheets. */
    private final Map<Integer, List<SpriteTiled>> rasterSheets = new TreeMap<>();

    /** Map tile surface. */
    private MapTileSurface map;
    /** Raster media. */
    private Media raster;
    /** Rasters count. */
    private int count;

    /**
     * Create feature.
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link MapTileSurface}</li>
     * </ul>
     */
    public MapTileRasteredModel()
    {
        super();
    }

    /**
     * Get the associated raster sheets for a sheet number. Create it if needed.
     * 
     * @param sheet The sheet number
     * @return The raster sheets collection found.
     */
    private List<SpriteTiled> getRasters(Integer sheet)
    {
        return rasterSheets.computeIfAbsent(sheet, s ->
        {
            final List<SpriteTiled> rasters = new ArrayList<>(RasterImage.MAX_RASTERS);
            rasterSheets.put(s, rasters);
            return rasters;
        });
    }

    /*
     * MapTileRastered
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        map = provider.getFeature(MapTileSurface.class);
    }

    @Override
    public boolean loadSheets()
    {
        if (raster == null || !raster.exists())
        {
            return false;
        }
        final int th = map.getTileHeight();
        final int sheetsCount = map.getSheetsNumber();

        for (int sheetId = 0; sheetId < sheetsCount; sheetId++)
        {
            final Integer sheet = Integer.valueOf(sheetId);
            final RasterImage rasterImage = new RasterImage(map.getSheet(sheetId).getSurface(), raster, th);
            rasterImage.loadRasters(true, sheet.toString());

            count = -1;
            final List<SpriteTiled> rastersSheet = getRasters(sheet);
            for (final ImageBuffer bufferRaster : rasterImage.getRasters())
            {
                final SpriteTiled sheetRaster = Drawable.loadSpriteTiled(bufferRaster, map.getTileWidth(), th);
                rastersSheet.add(sheetRaster);
                count++;
            }
        }
        return true;
    }

    @Override
    public void renderTile(Graphic g, Tile tile, int x, int y)
    {
        final SpriteTiled raster = getRasterSheet(tile.getSheetKey(), getRasterIndex(tile.getInTileY()));
        raster.setLocation(x, y);
        raster.setTile(tile.getNumber());
        raster.render(g);
    }

    @Override
    public int getRasterIndex(int ty)
    {
        return UtilMath.clamp((ty - 1) / RasterImage.LINES_PER_RASTER, 0, RasterImage.MAX_RASTERS - 1);
    }

    @Override
    public SpriteTiled getRasterSheet(Integer sheet, int rasterIndex)
    {
        return rasterSheets.get(sheet).get(UtilMath.clamp(rasterIndex, 0, count));
    }

    @Override
    public void setRaster(Media raster)
    {
        Check.notNull(raster);

        this.raster = raster;
    }
}
