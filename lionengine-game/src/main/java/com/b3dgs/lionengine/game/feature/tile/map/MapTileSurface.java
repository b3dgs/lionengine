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

import java.util.Collection;
import java.util.List;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.Localizable;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.Surface;
import com.b3dgs.lionengine.SurfaceTile;
import com.b3dgs.lionengine.game.Feature;
import com.b3dgs.lionengine.game.feature.FeatureInterface;
import com.b3dgs.lionengine.game.feature.tile.Tile;
import com.b3dgs.lionengine.graphic.drawable.SpriteTiled;

/**
 * Describe a map using tile for its representation. This is the lower level interface to describe a 2D map using tiles.
 * Each tiles are stored vertically and then horizontally.
 */
@FeatureInterface
public interface MapTileSurface extends Feature, SurfaceTile
{
    /**
     * Create and prepare map memory area. Previous map data (if existing) will be cleared ({@link #clear()}).
     * 
     * @param tileWidth The tile width.
     * @param tileHeight The tile height.
     * @param widthInTile The map width in tile (must be strictly positive).
     * @param heightInTile The map height in tile (must be strictly positive).
     * @throws LionEngineException If size is invalid.
     */
    void create(int tileWidth, int tileHeight, int widthInTile, int heightInTile);

    /**
     * Load map sheets (tiles surfaces). Must be called before rendering map.
     * Clear previous sheets if has.
     * 
     * @param sheets The sheets reference.
     * @throws LionEngineException If inconsistent tile size.
     */
    void loadSheets(List<SpriteTiled> sheets);

    /**
     * Load map sheets (tiles surfaces) from directory. Must be called before rendering map.
     * Clears previous sheets if has.
     * 
     * @param sheetsConfig The file that define the sheets configuration.
     * @throws LionEngineException If error when reading sheets.
     */
    void loadSheets(Media sheetsConfig);

    /**
     * Resize map with new size.
     * 
     * @param newWidth The new width in tile.
     * @param newHeight The new height in tile.
     */
    void resize(int newWidth, int newHeight);

    /**
     * Remove all tiles from map and clear internal data.
     */
    void clear();

    /**
     * Set a tile at specified map location.
     * <p>
     * The tile location must be between 0 and map size ({@link #getInTileWidth()}, {@link #getInTileHeight()}).
     * </p>
     * 
     * @param tx The horizontal tile location.
     * @param ty The vertical tile location.
     * @param number The tile number on sheet (must be positive or equal to 0).
     * @throws LionEngineException If outside map range.
     */
    void setTile(int tx, int ty, int number);

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
     * Get width relative to map referential as tile.
     * 
     * @param surface The surface reference.
     * @return The width relative to map referential as tile.
     */
    int getInTileWidth(Surface surface);

    /**
     * Get height relative to map referential as tile.
     * 
     * @param surface The surface reference.
     * @return The height relative to map referential as tile.
     */
    int getInTileHeight(Surface surface);

    /**
     * Get sheet from its id.
     * 
     * @param sheetId The sheet id.
     * @return The sheet found.
     * @throws LionEngineException If sheet not found.
     */
    SpriteTiled getSheet(int sheetId);

    /**
     * Get the number of used sheets.
     * 
     * @return The number of used sheets.
     */
    int getSheetsNumber();

    /**
     * Get the number of tiles per sheet.
     * 
     * @return The tile number per sheet.
     */
    int getTilesPerSheet();

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

    /**
     * Get the associated media.
     * 
     * @return The associated media, <code>null</code> if none.
     */
    Media getMedia();
}
