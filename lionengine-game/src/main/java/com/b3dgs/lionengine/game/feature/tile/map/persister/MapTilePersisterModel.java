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
package com.b3dgs.lionengine.game.feature.tile.map.persister;

import java.io.IOException;

import com.b3dgs.lionengine.Check;
import com.b3dgs.lionengine.Constant;
import com.b3dgs.lionengine.core.Medias;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.Services;
import com.b3dgs.lionengine.game.feature.FeatureModel;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.map.MapTile;
import com.b3dgs.lionengine.io.FileReading;
import com.b3dgs.lionengine.io.FileWriting;

/**
 * Handle the map persistence by providing saving and loading functions.
 */
public class MapTilePersisterModel extends FeatureModel implements MapTilePersister
{
    /** Error sheet missing message. */
    private static final String ERROR_SHEET_MISSING = "Sheet missing: ";
    /** Number of horizontal tiles to make a bloc. Used to reduce saved map file size. */
    private static final int BLOC_SIZE = 256;

    /** The services reference. */
    private MapTile map;

    /**
     * Create the persister.
     * <p>
     * The {@link Services} must provide:
     * </p>
     * <ul>
     * <li>{@link MapTile}</li>
     * </ul>
     */
    public MapTilePersisterModel()
    {
        super();
    }

    /**
     * Save tile. Data are saved this way:
     * 
     * <pre>
     * (integer) sheet number
     * (integer) index number inside sheet
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
        file.writeInteger(tile.getSheet().intValue());
        file.writeInteger(tile.getNumber());
        file.writeInteger(tile.getInTileX() % BLOC_SIZE);
        file.writeInteger(tile.getInTileY());
    }

    /**
     * Load tile. Data are loaded this way:
     * 
     * <pre>
     * (integer) sheet number
     * (integer) index number inside sheet
     * (integer) tile location x
     * (integer tile location y
     * </pre>
     * 
     * @param file The file reader reference.
     * @param i The last loaded tile number.
     * @return The loaded tile.
     * @throws IOException If error on reading.
     */
    protected Tile loadTile(FileReading file, int i) throws IOException
    {
        Check.notNull(file);

        final Integer sheet = Integer.valueOf(file.readInteger());
        final int number = file.readInteger();
        final int x = file.readInteger() * map.getTileWidth() + i * BLOC_SIZE * map.getTileWidth();
        final int y = file.readInteger() * map.getTileHeight();
        return map.createTile(sheet, number, x, y);
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
     * Persistable
     */

    @Override
    public void prepare(FeatureProvider provider, Services services)
    {
        super.prepare(provider, services);

        map = services.get(MapTile.class);
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
        final int widthInTile = map.getInTileWidth();

        // Header
        output.writeInteger(map.getTileWidth());
        output.writeInteger(map.getTileHeight());
        output.writeInteger(widthInTile);
        output.writeInteger(map.getInTileHeight());

        final boolean hasConfig = map.getSheetsConfig() != null;
        output.writeBoolean(hasConfig);
        if (hasConfig)
        {
            output.writeString(map.getSheetsConfig().getPath());
        }

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
        map.create(input.readInteger(), input.readInteger(), input.readInteger(), input.readInteger());
        if (input.readBoolean())
        {
            map.loadSheets(Medias.create(input.readString()));
        }

        final int t = input.readShort();
        for (int v = 0; v < t; v++)
        {
            final int n = input.readShort();
            for (int h = 0; h < n; h++)
            {
                final Tile tile = loadTile(input, v);
                if (tile.getSheet().intValue() > map.getSheetsNumber())
                {
                    throw new IOException(ERROR_SHEET_MISSING + Constant.DOUBLE_DOT + tile.getSheet());
                }
                map.setTile(tile);
            }
        }
    }
}
