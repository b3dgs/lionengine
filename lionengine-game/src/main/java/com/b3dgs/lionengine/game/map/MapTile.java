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

import java.io.IOException;
import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.core.Renderable;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;

/**
 * Describe a map using tile for its representation. This is the lower level interface to describe a 2D map using tiles.
 * Each tiles are stored vertically and then horizontally. A sheet id represents a tilesheet number (surface number
 * containing tiles). A map can have one or more sheets. The map picks its resources from a sheets folder, which
 * must contains the following files (and its tiles sheets image):
 * <ul>
 * <li>{@value #SHEETS_FILE_NAME} - describes the sheets used.
 * </ul>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Tile
 * @see MapTileGame
 * @see MapTileFeature
 */
public interface MapTile
        extends Renderable, Featurable<MapTileFeature>
{
    /** Tile sheets data file name. */
    String SHEETS_FILE_NAME = "sheets.xml";
    /** Tile sheet node. */
    String NODE_TILE_SHEET = "lionengine:sheet";
    /** Number of horizontal tiles to make a bloc. */
    int BLOC_SIZE = 256;

    /**
     * Create and prepare map memory area. Must be called before assigning tiles.
     * 
     * @param widthInTile The map width in tile (must be strictly positive).
     * @param heightInTile The map height in tile (must be strictly positive).
     */
    void create(int widthInTile, int heightInTile);

    /**
     * Create a tile.
     * 
     * @return The created tile.
     */
    Tile createTile();

    /**
     * Load a map from a level rip and the associated tiles directory.
     * 
     * @param levelrip The file describing the levelrip as a single image.
     * @param sheetsDir The directory containing tiles sheets.
     * @throws LionEngineException If error when importing map.
     */
    void load(Media levelrip, Media sheetsDir) throws LionEngineException;

    /**
     * Load map sheets (tiles surfaces) from directory. Must be called after map creation.
     * 
     * @param directory The sheets directory.
     * @throws LionEngineException If error when reading sheets.
     */
    void loadSheets(Media directory) throws LionEngineException;

    /**
     * Load a map from a specified file as binary data.
     * <p>
     * Data are loaded this way (see {@link #save(FileWriting)} order):
     * </p>
     * 
     * <pre>
     * <code>(String)</code> sheets directory
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
     * @param file The input file.
     * @throws IOException If error on reading.
     * @throws LionEngineException If error when reading map file.
     */
    void load(FileReading file) throws IOException, LionEngineException;

    /**
     * Save map to specified file as binary data. Data are saved this way (using specific types to save space):
     * 
     * <pre>
     * <code>(String)</code> theme
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
     * @param file The output file.
     * @throws IOException If error on writing.
     */
    void save(FileWriting file) throws IOException;

    /**
     * Append an existing map, starting at the specified offsets. Offsets start at the beginning of the map (0, 0).
     * A call to {@link #append(MapTile, int, int)} at ({@link #getWidthInTile()}, {@link #getHeightInTile()}) will add
     * the new map at the top-right.
     * 
     * @param map The map to append.
     * @param offsetX The horizontal offset in tile (>= 0).
     * @param offsetY The vertical offset in tile (>= 0).
     */
    void append(MapTile map, int offsetX, int offsetY);

    /**
     * Remove all tiles from map.
     */
    void clear();

    /**
     * Set a tile at specified map location.
     * 
     * @param tx The horizontal tile index location.
     * @param ty The vertical tile index location.
     * @param tile The tile reference.
     */
    void setTile(int tx, int ty, Tile tile);

    /**
     * Get tile from specified map location (in tile index). If the returned tile is equal to <code>null</code>, this
     * means that there is not tile at this location. It is not an error, just a way to avoid useless tile storage.
     * 
     * @param tx The horizontal tile index location.
     * @param ty The vertical tile index location.
     * @return The tile found at this location, <code>null</code> if none.
     * @throws LionEngineException If outside map range.
     */
    Tile getTile(int tx, int ty) throws LionEngineException;

    /**
     * Get the tile at the localizable.
     * 
     * @param localizable The localizable reference.
     * @param offsetX The horizontal offset search.
     * @param offsetY The vertical offset search.
     * @return The tile found at the localizable, <code>null</code> if none.
     * @throws LionEngineException If outside map range.
     */
    Tile getTile(Localizable localizable, int offsetX, int offsetY) throws LionEngineException;

    /**
     * Get location x relative to map referential as tile.
     * 
     * @param localizable The localizable reference.
     * @return The location x relative to map referential as tile.
     */
    int getInTileX(Localizable localizable);

    /**
     * Get location y relative to map referential as tile.
     * 
     * @param localizable The localizable reference.
     * @return The location y relative to map referential as tile.
     */
    int getInTileY(Localizable localizable);

    /**
     * Get the sheets directory media.
     * 
     * @return The directory containing tile sheets.
     */
    Media getSheetsDirectory();

    /**
     * Get list of sheets id.
     * 
     * @return The set of sheets id.
     */
    Collection<Integer> getSheets();

    /**
     * Get sheet from its id.
     * 
     * @param sheet The sheet id.
     * @return The sheet found.
     * @throws LionEngineException If sheet not found.
     */
    SpriteTiled getSheet(Integer sheet) throws LionEngineException;

    /**
     * Get the number of used sheets.
     * 
     * @return The number of used sheets.
     */
    int getSheetsNumber();

    /**
     * Get number of active tiles (which are not <code>null</code>).
     * 
     * @return The number of non <code>null</code> tile.
     */
    int getTilesNumber();

    /**
     * Get width of a tile.
     * 
     * @return The tile width.
     */
    int getTileWidth();

    /**
     * Get height of a tile.
     * 
     * @return The tile height.
     */
    int getTileHeight();

    /**
     * Get number of horizontal tiles.
     * 
     * @return The number of horizontal tiles.
     */
    int getWidthInTile();

    /**
     * Get number of vertical tiles.
     * 
     * @return The number of vertical tiles.
     */
    int getHeightInTile();

    /**
     * Check if map has been created.
     * 
     * @return <code>true</code> if created, <code>false</code> else.
     */
    boolean isCreated();
}
