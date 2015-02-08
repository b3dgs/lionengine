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
 * Each tiles are stored vertically and then horizontally. A pattern represents a tilesheet number (number of surface
 * containing tiles). A map can have one or more patterns. The map picks its resources from a patterns folder, which
 * must contains the following files (and its tiles sheets image):
 * <ul>
 * <li>{@value #SHEETS_FILE_NAME} - describes the patterns used.
 * </ul>
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see Tile
 * @see MapTileGame
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
     * Load a map from a specified file as binary data.
     * <p>
     * Data are loaded this way (see save(file) order):
     * </p>
     * 
     * <pre>
     * <code>(String)</code> theme
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
     * @throws LionEngineException If error when reading patterns or collisions.
     */
    void load(FileReading file) throws IOException, LionEngineException;

    /**
     * Load a map from a level rip and the associated tiles directory.
     * A file called {@value #SHEETS_FILE_NAME} has to be in the same directory.
     * 
     * @param levelrip The file containing the levelrip as an image.
     * @param patternsDirectory The directory containing tiles themes.
     * @throws LionEngineException If error when reading collisions.
     */
    void load(Media levelrip, Media patternsDirectory) throws LionEngineException;

    /**
     * Load map patterns (tiles surfaces) from theme name. Must be called after map creation. A file called
     * {@value #SHEETS_FILE_NAME} which describes the tile sheets used.
     * <p>
     * Patterns number and name have to be written inside a file named 'count', else, all files as .png will be loaded.
     * </p>
     * 
     * @param directory The patterns directory.
     * @throws LionEngineException If error when reading patterns.
     */
    void loadPatterns(Media directory) throws LionEngineException;

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
     * Set a tile at specified map indexes.
     * 
     * @param v The vertical index.
     * @param h The horizontal index.
     * @param tile The tile reference.
     */
    void setTile(int v, int h, Tile tile);

    /**
     * Get tile from specified map location (in tile index). If the returned tile is equal to <code>null</code>, this
     * means that there is not tile at this location. It is not an error, just a way to avoid useless tile storage.
     * 
     * @param tx The horizontal tile index location.
     * @param ty The vertical tile index location.
     * @return The tile reference.
     */
    Tile getTile(int tx, int ty);

    /**
     * Get the tile at the localizable.
     * 
     * @param localizable The localizable reference.
     * @param offsetX The horizontal offset search.
     * @param offsetY The vertical offset search.
     * @return The tile found at the localizable.
     */
    Tile getTile(Localizable localizable, int offsetX, int offsetY);

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
     * Get map theme.
     * 
     * @return The map tiles directory.
     */
    Media getPatternsDirectory();

    /**
     * Get list of patterns id.
     * 
     * @return The set of patterns id.
     */
    Collection<Integer> getPatterns();

    /**
     * Get pattern (tilesheet) from its id.
     * 
     * @param pattern The pattern id.
     * @return The pattern found.
     * @throws LionEngineException If pattern not found.
     */
    SpriteTiled getPattern(Integer pattern) throws LionEngineException;

    /**
     * Get the number of used pattern.
     * 
     * @return The number of used pattern.
     */
    int getNumberPatterns();

    /**
     * Get number of active tiles (which are not <code>null</code>).
     * 
     * @return The number of non <code>null</code> tile.
     */
    int getNumberTiles();

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
