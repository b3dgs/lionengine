/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.UtilConversion;
import com.b3dgs.lionengine.game.FeatureProvider;
import com.b3dgs.lionengine.game.feature.Featurable;
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
    protected static final int BLOC_SIZE = Constant.UNSIGNED_BYTE - 1;

    /** Listeners. */
    private final ListenableModel<MapTilePersisterListener> listenable = new ListenableModel<>();

    /** Map surface reference. */
    private MapTileSurface map;

    /**
     * Create feature.
     * <p>
     * The {@link Featurable} must have:
     * </p>
     * <ul>
     * <li>{@link MapTileSurface}</li>
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
     * (integer) index number
     * (byte) tile location x % MapTile.BLOC_SIZE
     * (byte tile location y % MapTile.BLOC_SIZE
     * </pre>
     * 
     * @param file The file writer reference.
     * @param tile The tile to save.
     * @throws IOException If error on writing.
     */
    protected void saveTile(FileWriting file, Tile tile) throws IOException
    {
        file.writeInteger(tile.getNumber());
        file.writeByte(UtilConversion.fromUnsignedByte((short) (tile.getInTileX() % BLOC_SIZE)));
        file.writeByte(UtilConversion.fromUnsignedByte((short) (tile.getInTileY() % BLOC_SIZE)));
    }

    /**
     * Load tile. Data are loaded this way:
     * 
     * <pre>
     * (integer) index number
     * (byte) tile location x
     * (byte tile location y
     * </pre>
     * 
     * @param file The file reader reference.
     * @param sx The last loaded tile number x.
     * @param sy The last loaded tile number y.
     * @throws IOException If error on reading.
     */
    protected void loadTile(FileReading file, int sx, int sy) throws IOException
    {
        final int number = file.readInteger();
        final int tx = UtilConversion.toUnsignedByte(file.readByte()) + sx * BLOC_SIZE;
        final int ty = UtilConversion.toUnsignedByte(file.readByte()) + sy * BLOC_SIZE;

        map.setTile(tx, ty, number);
    }

    /**
     * Count the active tiles.
     * 
     * @param widthInTile The horizontal tiles.
     * @param heightInTile The vertical tiles.
     * @param step The step number.
     * @param sx The sx value.
     * @param sy The sy value.
     * @return The active tiles.
     */
    private int countTiles(int widthInTile, int heightInTile, int step, int sx, int sy)
    {
        int count = 0;
        for (int tx = 0; tx < widthInTile; tx++)
        {
            for (int ty = 0; ty < heightInTile; ty++)
            {
                if (map.getTile(tx + sx * step, ty + sy * step) != null)
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
     * @param heightInTile The vertical tiles.
     * @param step The step number.
     * @param sx The sx value.
     * @param sy The sy value.
     * @throws IOException If error on saving.
     */
    private void saveTiles(FileWriting file, int widthInTile, int heightInTile, int step, int sx, int sy)
            throws IOException
    {
        for (int tx = 0; tx < widthInTile; tx++)
        {
            for (int ty = 0; ty < heightInTile; ty++)
            {
                final Tile tile = map.getTile(tx + sx * step, ty + sy * step);
                if (tile != null)
                {
                    saveTile(file, tile);
                }
            }
        }
    }

    /**
     * Load tiles in bloc.
     * 
     * @param input The input level file.
     * @param sx The horizontal bloc.
     * @param sy The vertical bloc.
     * @throws IOException If error.
     */
    private void loadTiles(FileReading input, int sx, int sy) throws IOException
    {
        final char tiles = input.readChar();
        for (int t = 0; t < tiles; t++)
        {
            loadTile(input, sx, sy);
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
     * <code>(char)</code> tile width
     * <code>(char)</code> tile height
     * <code>(int)</code> width in tiles
     * <code>(int)</code> height in tiles
     * <code>(char)</code> number of {@value #BLOC_SIZE} horizontal blocs (widthInTile / {@value #BLOC_SIZE})
     * for each blocs tile
     * <code>(char)</code> number of {@value #BLOC_SIZE} vertical blocs (heightInTile / {@value #BLOC_SIZE})
     * for each blocs tile
     *   <code>(char)</code> number of tiles in this bloc
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
        final int heightInTile = map.getInTileHeight();

        output.writeChar((char) map.getTileWidth());
        output.writeChar((char) map.getTileHeight());
        output.writeInteger(widthInTile);
        output.writeInteger(heightInTile);

        final int step = BLOC_SIZE;
        final int x = Math.min(step, widthInTile);
        final int y = Math.min(step, heightInTile);
        final int tx = (int) Math.ceil(widthInTile / (double) step);
        final int ty = (int) Math.ceil(heightInTile / (double) step);

        output.writeChar((char) tx);
        output.writeChar((char) ty);

        for (int sx = 0; sx < tx; sx++)
        {
            for (int sy = 0; sy < ty; sy++)
            {
                final int tiles = countTiles(x, y, step, sx, sy);
                output.writeChar((char) tiles);
                saveTiles(output, Math.min(widthInTile, BLOC_SIZE), Math.min(heightInTile, BLOC_SIZE), step, sx, sy);
            }
        }
    }

    /**
     * Load a map from a specified file as binary data.
     * <p>
     * Data are loaded this way (see {@link #save(FileWriting)} order):
     * </p>
     * 
     * <pre>
     * <code>(char)</code> tile width
     * <code>(char)</code> tile height
     * <code>(int)</code> width in tiles
     * <code>(int)</code> height in tiles
     * <code>(char)</code> number of {@value #BLOC_SIZE} horizontal blocs (widthInTile / {@value #BLOC_SIZE})
     * for each blocs tile
     * <code>(char)</code> number of {@value #BLOC_SIZE} vertical blocs (heightInTile / {@value #BLOC_SIZE})
     * for each blocs tile
     *   <code>(char)</code> number of tiles in this bloc
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

        map.create(input.readChar(), input.readChar(), input.readInteger(), input.readInteger());

        final int n = listenable.size();
        for (int i = 0; i < n; i++)
        {
            listenable.get(i).notifyMapLoadStart();
        }

        final int tx = input.readChar();
        final int ty = input.readChar();

        for (int sx = 0; sx < tx; sx++)
        {
            for (int sy = 0; sy < ty; sy++)
            {
                loadTiles(input, sx, sy);
            }
        }

        for (int i = 0; i < n; i++)
        {
            listenable.get(i).notifyMapLoaded();
        }
    }
}
