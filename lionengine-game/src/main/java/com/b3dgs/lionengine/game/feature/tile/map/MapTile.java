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
package com.b3dgs.lionengine.game.feature.tile.map;

import java.util.Collection;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.SurfaceTile;
import com.b3dgs.lionengine.game.Featurable;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.game.feature.tile.TilesExtractor;
import com.b3dgs.lionengine.graphic.SpriteTiled;

/**
 * Describe a map using tile for its representation. This is the lower level interface to describe a 2D map using tiles.
 * Each tiles are stored vertically and then horizontally.
 * <p>
 * A sheet id represents a tilesheet number (surface number containing tiles). A map can have one or more
 * sheets. The map picks its resources from a sheets folder, which must contains the files images.
 * </p>
 * 
 * @see MapTileGame
 * @see Minimap
 * @see Tile
 */
public interface MapTile extends SurfaceTile, Featurable
{
    /**
     * Create and prepare map memory area. Must be called before assigning tiles ({@link #setTile(Tile)}).
     * Previous map data (if existing) will be cleared ({@link #clear()}).
     * 
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     * @param widthInTile The map width in tile (must be strictly positive).
     * @param heightInTile The map height in tile (must be strictly positive).
     * @throws LionEngineException If size is invalid.
     * @see #create(Media)
     * @see #create(Media, Media)
     */
    void create(int tileWidth, int tileHeight, int widthInTile, int heightInTile);

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
    void create(Media levelrip, int tileWidth, int tileHeight, int horizontalTiles);

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
    void create(Media levelrip);

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
    void create(Media levelrip, Media sheetsConfig);

    /**
     * Create a tile.
     * 
     * @param sheet The sheet number (must be positive or equal to 0).
     * @param number The tile number on sheet (must be positive or equal to 0).
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The created tile.
     * @throws LionEngineException If invalid arguments.
     */
    Tile createTile(Integer sheet, int number, double x, double y);

    /**
     * Load map sheets (tiles surfaces). Must be called before rendering map.
     * Clear previous sheets if has.
     * 
     * @param sheets The sheets reference.
     * @throws LionEngineException If inconsistent tile size.
     */
    void loadSheets(Collection<SpriteTiled> sheets);

    /**
     * Load map sheets (tiles surfaces) from directory. Must be called before rendering map.
     * Clears previous sheets if has.
     * 
     * @param sheetsConfig The file that define the sheets configuration.
     * @throws LionEngineException If error when reading sheets.
     */
    void loadSheets(Media sheetsConfig);

    /**
     * Append an existing map, starting at the specified offsets. Offsets start at the beginning of the map (0, 0).
     * A call to {@link #append(MapTile, int, int)} at ({@link #getInTileWidth()}, {@link #getInTileHeight()}) will add
     * the new map at the top-right.
     * 
     * @param map The map to append.
     * @param offsetX The horizontal offset in tile (positive).
     * @param offsetY The vertical offset in tile (positive).
     */
    void append(MapTile map, int offsetX, int offsetY);

    /**
     * Append existing maps.
     * 
     * @param maps The maps to append.
     * @param offsetX The horizontal offset factor in tile (positive).
     * @param offsetY The vertical offset factor in tile (positive).
     * @param randX The horizontal random offset in tile.
     * @param randY The vertical random offset in tile.
     */
    void append(Collection<MapTile> maps, int offsetX, int offsetY, int randX, int randY);

    /**
     * Remove all tiles from map and clear internal data. Keep existing loaded tile sheets ({@link #loadSheets(Media)}).
     */
    void clear();

    /**
     * Add a tile set listener.
     * 
     * @param listener The listener reference.
     */
    void addListener(TileSetListener listener);

    /**
     * Remove a tile set listener.
     * 
     * @param listener The listener reference.
     */
    void removeListener(TileSetListener listener);

    /**
     * Set a tile at specified map location.
     * <p>
     * The tile location must be between 0 and map size ({@link #getInTileWidth()}, {@link #getInTileHeight()}).
     * </p>
     * <p>
     * If a tile exists at the tile location, it will be removed.
     * </p>
     * 
     * @param tile The tile reference.
     * @throws LionEngineException If outside map range.
     */
    void setTile(Tile tile);

    /**
     * Get tile from specified map location (in tile index). If the returned tile is equal to <code>null</code>, this
     * means that there is not tile at this location. It is not an error, just a way to avoid useless tile storage.
     * 
     * @param tx The horizontal tile index location.
     * @param ty The vertical tile index location.
     * @return The tile found at this location, <code>null</code> if none.
     */
    Tile getTile(int tx, int ty);

    /**
     * Get the tile at the localizable.
     * 
     * @param localizable The localizable reference.
     * @param offsetX The horizontal offset search.
     * @param offsetY The vertical offset search.
     * @return The tile found at the localizable, <code>null</code> if none.
     */
    Tile getTile(Localizable localizable, int offsetX, int offsetY);

    /**
     * Get the tile at the location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     * @return The tile found at the location, <code>null</code> if none.
     */
    Tile getTileAt(double x, double y);

    /**
     * Get the tile neighbor, directly adjacent to it.
     * 
     * @param tile The tile reference.
     * @return The adjacent tiles in no special order.
     */
    Collection<Tile> getNeighbors(Tile tile);

    /**
     * Get the list of tiles from old location to current.
     * 
     * @param ox The old horizontal location.
     * @param oy The old vertical location.
     * @param x The current horizontal location.
     * @param y The current vertical location.
     * @return The tiles found.
     */
    Collection<Tile> getTilesHit(double ox, double oy, double x, double y);

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
    SpriteTiled getSheet(Integer sheet);

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
     * Get number of horizontal tiles.
     * 
     * @return The number of horizontal tiles.
     */
    int getInTileWidth();

    /**
     * Get number of vertical tiles.
     * 
     * @return The number of vertical tiles.
     */
    int getInTileHeight();

    /**
     * Get the radius in tile.
     * 
     * @return The radius in tile.
     */
    int getInTileRadius();

    /**
     * Check if map has been created.
     * 
     * @return <code>true</code> if created, <code>false</code> else.
     */
    boolean isCreated();
}
