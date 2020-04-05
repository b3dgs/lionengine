/*
 * Copyright (C) 2013-2020 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.feature.tile.map;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Listenable;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.Surface;
import com.b3dgs.lionengine.Verbose;
import com.b3dgs.lionengine.game.feature.FeaturableAbstract;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TilesExtractor;
import com.b3dgs.lionengine.graphic.ImageBuffer;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * Abstract representation of a standard tile based map. This class uses a List of List to store tiles, a TreeMap to
 * store sheets references ({@link SpriteTiled}), and collisions.
 * <p>
 * The way to prepare a map is the following:
 * </p>
 * 
 * <pre>
 * {@link #create(int, int, int, int)} // prepare memory to store tiles
 * {@link #loadSheets(Media)} // load tile sheets
 * </pre>
 * 
 * <p>
 * Or import a map from a level rip with {@link #create(Media, Media)}.
 * </p>
 */
public class MapTileGame extends FeaturableAbstract implements MapTile, Listenable<TileSetListener>
{
    /** Map tile surface. */
    protected final MapTileSurface mapSurface;
    /** Tile set listeners. */
    private final ListenableModel<TileSetListener> listenable = new ListenableModel<>();

    /**
     * Create model.
     */
    public MapTileGame()
    {
        super();

        mapSurface = addFeatureAndGet(new MapTileSurfaceModel());
    }

    /**
     * Create a map from a level rip which should be an image file (*.PNG, *.BMP) that represents the full map.
     * The file will be read pixel by pixel to recognize tiles and their location. Data structure will be created (
     * {@link #create(int, int, int, int)}).
     * 
     * @param levelrip The file describing the levelrip as a single image.
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     * @param horizontalTiles The number of horizontal tiles on sheets.
     * @throws LionEngineException If error when importing map.
     * @see TilesExtractor
     * @see LevelRipConverter
     */
    public void create(Media levelrip, int tileWidth, int tileHeight, int horizontalTiles)
    {
        final TilesExtractor tilesExtractor = new TilesExtractor();
        final Collection<ImageBuffer> buffers = tilesExtractor.extract(tileWidth, tileHeight, Arrays.asList(levelrip));
        loadSheets(SheetsExtractor.extract(buffers, horizontalTiles));

        for (final ImageBuffer tileBuffer : buffers)
        {
            tileBuffer.dispose();
        }

        LevelRipConverter.start(levelrip, tileWidth, tileHeight, mapSurface);
    }

    /**
     * Create a map from a level rip which should be an image file (*.PNG, *.BMP) that represents the full map.
     * The file will be read pixel by pixel to recognize tiles and their location. Data structure will be created (
     * {@link #create(int, int, int, int)}).
     * <p>
     * {@link TileSheetsConfig#FILENAME} and {@link com.b3dgs.lionengine.game.feature.tile.TileGroupsConfig#FILENAME}
     * will be used as default, by calling {@link #create(Media, Media)}.
     * </p>
     * 
     * @param levelrip The file describing the levelrip as a single image.
     * @throws LionEngineException If error when importing map.
     * @see TilesExtractor
     * @see LevelRipConverter
     */
    public void create(Media levelrip)
    {
        create(levelrip, Medias.create(levelrip.getParentPath(), TileSheetsConfig.FILENAME));
    }

    /**
     * Create a map from a level rip and the associated tiles configuration file.
     * A level rip is an image file (*.PNG, *.BMP) that represents the full map.
     * The file will be read pixel by pixel to recognize tiles and their location. Data structure will be created (
     * {@link #create(int, int, int, int)}).
     * 
     * @param levelrip The file describing the levelrip as a single image.
     * @param sheetsConfig The file that define the sheets configuration.
     * @throws LionEngineException If error when importing map.
     * @see TilesExtractor
     * @see LevelRipConverter
     */
    public void create(Media levelrip, Media sheetsConfig)
    {
        clear();

        loadSheets(sheetsConfig);

        final TileSheetsConfig config = TileSheetsConfig.imports(sheetsConfig);
        final int tileWidth = config.getTileWidth();
        final int tileHeight = config.getTileHeight();

        final int errors = LevelRipConverter.start(levelrip, tileWidth, tileHeight, mapSurface);
        if (errors > 0)
        {
            Verbose.warning(getClass(), "create", "Number of missing tiles: ", String.valueOf(errors));
        }
    }

    /*
     * MapTile
     */

    @Override
    public void create(int tileWidth, int tileHeight, int widthInTile, int heightInTile)
    {
        mapSurface.create(tileWidth, tileHeight, widthInTile, heightInTile);
    }

    @Override
    public void loadSheets(List<SpriteTiled> sheets)
    {
        mapSurface.loadSheets(sheets);
    }

    @Override
    public void loadSheets(Media sheetsConfig)
    {
        mapSurface.loadSheets(sheetsConfig);
    }

    @Override
    public void clear()
    {
        mapSurface.clear();
    }

    @Override
    public void addListener(TileSetListener listener)
    {
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(TileSetListener listener)
    {
        listenable.removeListener(listener);
    }

    @Override
    public void setTile(int tx, int ty, int number)
    {
        Tile tile = mapSurface.getTile(tx, ty);
        final int oldNum;
        if (tile == null)
        {
            oldNum = -1;
        }
        else
        {
            oldNum = tile.getNumber();
        }

        mapSurface.setTile(tx, ty, number);
        tile = mapSurface.getTile(tx, ty);

        if (number != oldNum)
        {
            for (int i = 0; i < listenable.size(); i++)
            {
                listenable.get(i).onTileSet(tile);
            }
        }
    }

    @Override
    public Tile getTile(int tx, int ty)
    {
        return mapSurface.getTile(tx, ty);
    }

    @Override
    public Tile getTile(Localizable localizable, int offsetX, int offsetY)
    {
        return mapSurface.getTile(localizable, offsetX, offsetY);
    }

    @Override
    public Tile getTileAt(double x, double y)
    {
        return mapSurface.getTileAt(x, y);
    }

    @Override
    public Collection<Tile> getNeighbors(Tile tile)
    {
        return mapSurface.getNeighbors(tile);
    }

    @Override
    public Collection<Tile> getTilesHit(double ox, double oy, double x, double y)
    {
        return mapSurface.getTilesHit(ox, oy, x, y);
    }

    @Override
    public int getInTileX(Localizable localizable)
    {
        return mapSurface.getInTileX(localizable);
    }

    @Override
    public int getInTileY(Localizable localizable)
    {
        return mapSurface.getInTileY(localizable);
    }

    @Override
    public int getInTileWidth(Surface surface)
    {
        return mapSurface.getInTileWidth(surface);
    }

    @Override
    public int getInTileHeight(Surface surface)
    {
        return mapSurface.getInTileHeight(surface);
    }

    @Override
    public SpriteTiled getSheet(int sheetId)
    {
        return mapSurface.getSheet(sheetId);
    }

    @Override
    public int getSheetsNumber()
    {
        return mapSurface.getSheetsNumber();
    }

    @Override
    public int getTilesPerSheet()
    {
        return mapSurface.getTilesPerSheet();
    }

    @Override
    public int getTilesNumber()
    {
        return mapSurface.getTilesNumber();
    }

    @Override
    public int getTileWidth()
    {
        return mapSurface.getTileWidth();
    }

    @Override
    public int getTileHeight()
    {
        return mapSurface.getTileHeight();
    }

    @Override
    public int getInTileWidth()
    {
        return mapSurface.getInTileWidth();
    }

    @Override
    public int getInTileHeight()
    {
        return mapSurface.getInTileHeight();
    }

    @Override
    public int getInTileRadius()
    {
        return mapSurface.getInTileRadius();
    }

    @Override
    public boolean isCreated()
    {
        return mapSurface.isCreated();
    }

    @Override
    public int getWidth()
    {
        return mapSurface.getWidth();
    }

    @Override
    public int getHeight()
    {
        return mapSurface.getHeight();
    }

    @Override
    public Media getMedia()
    {
        return mapSurface.getMedia();
    }
}
