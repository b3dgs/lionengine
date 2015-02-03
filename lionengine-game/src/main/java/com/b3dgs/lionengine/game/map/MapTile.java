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
import com.b3dgs.lionengine.Viewer;
import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.collision.CollisionCategory;
import com.b3dgs.lionengine.game.collision.CollisionFormula;
import com.b3dgs.lionengine.game.collision.CollisionGroup;
import com.b3dgs.lionengine.game.collision.CollisionResult;
import com.b3dgs.lionengine.game.trait.Transformable;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;

/**
 * Describe a map using tile for its representation. This is the lower level interface to describe a 2D map using tiles.
 * Each tiles are stored vertically and then horizontally. A pattern represents a tilesheet number (number of surface
 * containing tiles). A map can have one or more patterns. The map picks its resources from a patterns folder, which
 * must contains the following files (and its tiles sheets image):
 * <ul>
 * <li>{@value #SHEETS_FILE_NAME} - describes the patterns used. Must be used like this:
 * 
 * <pre>
 * {@code <lionengine:sheets xmlns:lionengine="http://lionengine.b3dgs.com">}
 *     {@code <lionengine:sheet>0.png</lionengine:sheet>}
 *     {@code <lionengine:sheet>1.png</lionengine:sheet>}
 *     ...
 * {@code </lionengine:sheets>}
 * </pre>
 * 
 * </li>
 * <li>{@value #FORMULAS_FILE_NAME} - defines the {@link CollisionFormula} which can be used.</li>
 * <li>{@value #GROUPS_FILE_NAME} - defines the {@link CollisionGroup} for each tiles.</li>
 * </ul>
 * 
 * @param <T> The tile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see TileGame
 * @see MapTileGame
 * @see CollisionFormula
 */
public interface MapTile<T extends TileGame>
{
    /** Tile sheets data file name. */
    String SHEETS_FILE_NAME = "sheets.xml";
    /** Collision formulas file name. */
    String FORMULAS_FILE_NAME = "formulas.xml";
    /** Collision groups file name. */
    String GROUPS_FILE_NAME = "groups.xml";
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
    T createTile();

    /**
     * Create the collision draw surface. Must be called after map creation to enable collision rendering.
     */
    void createCollisionDraw();

    /**
     * Generate the minimap from the current map.
     */
    void createMiniMap();

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
     * Load a map from a level rip and the associated tiles directory. A file called {@value #SHEETS_FILE_NAME} and
     * {@value #GROUPS_FILE_NAME} has to be in the same directory.
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
     * Load map collision from an external file.
     * 
     * @param collisionFormulas The collision formulas descriptor.
     * @param collisionGroups The tile collision groups descriptor.
     * @throws LionEngineException If error when reading collisions.
     */
    void loadCollisions(Media collisionFormulas, Media collisionGroups) throws LionEngineException;

    /**
     * Append an existing map, starting at the specified offsets. Offsets start at the beginning of the map (0, 0).
     * A call to {@link #append(MapTile, int, int)} at ({@link #getWidthInTile()}, {@link #getHeightInTile()}) will add
     * the new map at the top-right.
     * 
     * @param map The map to append.
     * @param offsetX The horizontal offset in tile (>= 0).
     * @param offsetY The vertical offset in tile (>= 0).
     */
    void append(MapTile<T> map, int offsetX, int offsetY);

    /**
     * Remove all tiles from map.
     */
    void clear();

    /**
     * Clear the cached collision image created with {@link #createCollisionDraw()}.
     */
    void clearCollisionDraw();

    /**
     * Add a collision formula.
     * 
     * @param formula The collision formula reference.
     */
    void addCollisionFormula(CollisionFormula formula);

    /**
     * Add a collision group.
     * 
     * @param group The collision group reference.
     */
    void addCollisionGroup(CollisionGroup group);

    /**
     * Remove a collision formula.
     * 
     * @param formula The collision formula to remove.
     */
    void removeCollisionFormula(CollisionFormula formula);

    /**
     * Remove a collision group.
     * 
     * @param group The collision group to remove.
     */
    void removeCollisionGroup(CollisionGroup group);

    /**
     * Remove all collision formulas.
     */
    void removeCollisionFormulas();

    /**
     * Remove all collision groups.
     */
    void removeCollisionGroups();

    /**
     * Search first tile hit by the transformable that contains collision, applying a ray tracing from its old location
     * to its current. This way, the transformable can not pass through a collidable tile.
     * 
     * @param transformable The transformable reference.
     * @param category The collisions category to search in.
     * @return The collision result, <code>null</code> if nothing found.
     */
    CollisionResult<T> computeCollision(Transformable transformable, CollisionCategory category);

    /**
     * Save the current collisions to the collision file.
     */
    void saveCollisions();

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
     * Collisions are not saved, because it is possible to retrieve them from {@value #GROUPS_FILE_NAME}.
     * 
     * @param file The output file.
     * @throws IOException If error on writing.
     */
    void save(FileWriting file) throws IOException;

    /**
     * Render map from camera viewpoint, showing a specified area.
     * 
     * @param g The graphic output.
     * @param viewer The camera viewpoint.
     */
    void render(Graphic g, Viewer viewer);

    /**
     * Render minimap on graphic output at specified location.
     * 
     * @param g The graphic output.
     * @param x The location x.
     * @param y The location y.
     */
    void renderMiniMap(Graphic g, int x, int y);

    /**
     * Set a tile at specified map indexes.
     * 
     * @param v The vertical index.
     * @param h The horizontal index.
     * @param tile The tile reference.
     */
    void setTile(int v, int h, T tile);

    /**
     * Get tile from specified map location (in tile index). If the returned tile is equal to <code>null</code>, this
     * means that there is not tile at this location. It is not an error, just a way to avoid useless tile storage.
     * 
     * @param tx The horizontal tile index location.
     * @param ty The vertical tile index location.
     * @return The tile reference.
     */
    T getTile(int tx, int ty);

    /**
     * Get the tile at the localizable.
     * 
     * @param localizable The localizable reference.
     * @param offsetX The horizontal offset search.
     * @param offsetY The vertical offset search.
     * @return The tile found at the localizable.
     */
    T getTile(Localizable localizable, int offsetX, int offsetY);

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
     * Get the collision formula from its name.
     * 
     * @param name The collision formula name.
     * @return The collision formula from name reference.
     * @throws LionEngineException If formula not found.
     */
    CollisionFormula getCollisionFormula(String name) throws LionEngineException;

    /**
     * Get the collision group from its name.
     * 
     * @param name The collision group name.
     * @return The supported collision group reference.
     * @throws LionEngineException If group not found.
     */
    CollisionGroup getCollisionGroup(String name) throws LionEngineException;

    /**
     * Get the collision formulas list.
     * 
     * @return The collision formulas list.
     */
    Collection<CollisionFormula> getCollisionFormulas();

    /**
     * Get the collision groups list.
     * 
     * @return The collision groups list.
     */
    Collection<CollisionGroup> getCollisionGroups();

    /**
     * Get minimap surface reference.
     * 
     * @return The minimap surface reference.
     */
    ImageBuffer getMiniMap();

    /**
     * Check if map has been created.
     * 
     * @return <code>true</code> if created, <code>false</code> else.
     */
    boolean isCreated();
}
