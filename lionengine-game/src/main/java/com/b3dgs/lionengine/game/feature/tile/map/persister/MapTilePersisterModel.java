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
package com.b3dgs.lionengine.game.feature.tile.map.persister;

import java.io.IOException;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.ListenableModel;
import com.b3dgs.lionengine.Medias;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.FeatureAbstract;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTileSurface;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * Handle the map persistence by providing saving and loading functions.
 */
public class MapTilePersisterModel extends FeatureAbstract implements MapTilePersister
{
    /** Number of horizontal tiles to make a bloc. Used to reduce saved map file size. */
    protected static final int BLOC_SIZE = Constant.UNSIGNED_BYTE;

    /** Listeners. */
    private final ListenableModel<MapTilePersisterListener> listenable = new ListenableModel<>();

    /** Map surface reference. */
    private MapTileSurface map;

    /**
     * Create feature.
     */
    public MapTilePersisterModel()
    {
        super();
    }

    /**
     * Save tile. Data are saved this way:
     * 
     * <pre>
     * (integer) index number
     * (integer) tile location x % MapTile.BLOC_SIZE
     * (integer tile location y
     * </pre>
     * 
     * @param file The file writer reference.
     * @param tile The tile to save.
     * @throws IOException If error on writing.
     */
    protected void saveTile(FileWriting file, Tile tile) throws IOException
    {
        file.writeInteger(tile.getNumber());
        file.writeInteger(tile.getInTileX() % BLOC_SIZE);
        file.writeInteger(tile.getInTileY());
    }

    /**
     * Load tile. Data are loaded this way:
     * 
     * <pre>
     * (integer) index number
     * (integer) tile location x
     * (integer tile location y
     * </pre>
     * 
     * @param file The file reader reference.
     * @param i The last loaded tile number.
     * @throws IOException If error on reading.
     */
    protected void loadTile(FileReading file, int i) throws IOException
    {
        final int number = file.readInteger();
        final int tx = file.readInteger() + i * BLOC_SIZE;
        final int ty = file.readInteger();

        map.setTile(tx, ty, number);
    }

    /**
     * Count the active tiles.
     * 
     * @param widthInTile The horizontal tiles.
     * @param step The step number.
     * @param s The s value.
     * @return The active tiles.
     */
    private int countTiles(int widthInTile, int step, int s)
    {
        int count = 0;
        for (int tx = 0; tx < widthInTile; tx++)
        {
            for (int ty = 0; ty < map.getInTileHeight(); ty++)
            {
                if (map.getTile(tx + s * step, ty) != null)
                {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * Save the active tiles.
     * 
     * @param file The output file.
     * @param widthInTile The horizontal tiles.
     * @param step The step number.
     * @param s The s value.
     * @throws IOException If error on saving.
     */
    private void saveTiles(FileWriting file, int widthInTile, int step, int s) throws IOException
    {
        for (int tx = 0; tx < widthInTile; tx++)
        {
            for (int ty = 0; ty < map.getInTileHeight(); ty++)
            {
                final Tile tile = map.getTile(tx + s * step, ty);
                if (tile != null)
                {
                    saveTile(file, tile);
                }
            }
        }
    }

    /*
     * MapTilePersister
     */

    @Override
    public void prepare(FeatureProvider provider)
    {
        super.prepare(provider);

        map = provider.getFeature(MapTileSurface.class);
    }

    @Override
    public void addListener(MapTilePersisterListener listener)
    {
        listenable.addListener(listener);
    }

    @Override
    public void removeListener(MapTilePersisterListener listener)
    {
        listenable.removeListener(listener);
    }

    /**
     * Save map to specified file as binary data. Data are saved this way (using specific types to save space):
     * 
     * <pre>
     * <code>(String)</code> sheets configuration file
     * <code>(short)</code> width in tiles
     * <code>(short)</code> height in tiles
     * <code>(byte)</code> tile width (use of byte because tile width &lt; 255)
     * <code>(byte)</code> tile height (use of byte because tile height &lt; 255)
     * <code>(short)</code> number of {@value #BLOC_SIZE} horizontal blocs (widthInTile / {@value #BLOC_SIZE})
     * for each blocs tile
     *   <code>(short)</code> number of tiles in this bloc
     *   for each tile in this bloc
     *     call tile.save(file)
     * </pre>
     * 
     * @param output The output level file.
     * @throws IOException If error on writing.
     */
    @Override
    public void save(FileWriting output) throws IOException
    {
        Check.notNull(output);

        final int widthInTile = map.getInTileWidth();

        // Header
        final boolean hasConfig = map.getMedia() != null;
        output.writeBoolean(hasConfig);
        if (hasConfig)
        {
            output.writeString(map.getMedia().getPath());
        }

        output.writeInteger(map.getTileWidth());
        output.writeInteger(map.getTileHeight());
        output.writeInteger(widthInTile);
        output.writeInteger(map.getInTileHeight());

        final int step = BLOC_SIZE;
        final int x = Math.min(step, widthInTile);
        final int t = (int) Math.ceil(widthInTile / (double) step);

        output.writeShort((short) t);
        for (int s = 0; s < t; s++)
        {
            final int count = countTiles(x, step, s);
            output.writeShort((short) count);
            saveTiles(output, Math.min(widthInTile, BLOC_SIZE), step, s);
        }
    }

    /**
     * Load a map from a specified file as binary data.
     * <p>
     * Data are loaded this way (see {@link #save(FileWriting)} order):
     * </p>
     * 
     * <pre>
     * <code>(String)</code> sheets file configuration
     * <code>(short)</code> width in tiles
     * <code>(short)</code> height in tiles
     * <code>(byte)</code> tile width
     * <code>(byte)</code> tile height
     * <code>(short)</code> number of {@value #BLOC_SIZE} horizontal blocs (widthInTile / {@value #BLOC_SIZE})
     * for each blocs tile
     *   <code>(short)</code> number of tiles in this bloc
     *   for each tile in this bloc
     *     create blank tile
     *     call load(file)
     *     call setTile(...) to update map with this new tile
     * </pre>
     * 
     * @param input The input level file.
     * @throws IOException If error on reading.
     */
    @Override
    public void load(FileReading input) throws IOException
    {
        Check.notNull(input);

        if (input.readBoolean())
        {
            map.loadSheets(Medias.create(input.readString()));
        }
        map.create(input.readInteger(), input.readInteger(), input.readInteger(), input.readInteger());

        final int t = input.readShort();
        for (int v = 0; v < t; v++)
        {
            final int n = input.readShort();
            for (int h = 0; h < n; h++)
            {
                loadTile(input, v);
            }
        }

        for (int i = 0; i < listenable.size(); i++)
        {
            listenable.get(i).notifyMapLoaded();
        }
    }
}
