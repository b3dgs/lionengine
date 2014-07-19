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
import java.util.List;
import java.util.Set;

import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.drawable.SpriteTiled;
import com.b3dgs.lionengine.game.CameraGame;
import com.b3dgs.lionengine.game.purview.Localizable;
import com.b3dgs.lionengine.stream.FileReading;
import com.b3dgs.lionengine.stream.FileWriting;
import com.b3dgs.lionengine.stream.XmlNode;

/**
 * Describe a map using tile for its representation. This is the lower level interface to describe a 2D map using tiles.
 * Each tiles are stored vertically and then horizontally. A pattern represents a tilesheet number (number of surface
 * containing tiles). A map can have one or more patterns.
 * 
 * @param <T> Tile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see TileGame
 * @see MapTileGame
 */
public interface MapTile<T extends TileGame>
{
    /** Number of horizontal tiles to make a bloc. */
    int BLOC_SIZE = 256;
    /** Collisions file name. */
    String COLLISIONS_FILE_NAME = "collisions.xml";

    /**
     * Create and prepare map memory area. Must be called before assigning tiles.
     * 
     * @param widthInTile The map width in tile.
     * @param heightInTile The map height in tile.
     */
    void create(int widthInTile, int heightInTile);

    /**
     * Create a tile.
     * 
     * @param width The tile width.
     * @param height The tile height.
     * @param pattern The tile pattern.
     * @param number The tile number.
     * @param collision The tile collision.
     * @return The created tile.
     */
    T createTile(int width, int height, Integer pattern, int number, CollisionTile collision);

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
     *     call tile.load(file)
     *     call this.setTile(...) to update map with this new tile
     * </pre>
     * 
     * @param file The input file.
     * @throws IOException If error on reading.
     */
    void load(FileReading file) throws IOException;

    /**
     * Load a map from a level rip and the associated tiles directory.
     * 
     * @param levelrip The file containing the levelrip as an image.
     * @param patternsDirectory The directory containing tiles themes.
     */
    void load(Media levelrip, Media patternsDirectory);

    /**
     * Load map patterns (tiles surfaces) from theme name. Must be called after map creation. A file called
     * {@value #COLLISIONS_FILE_NAME} has to be in the same directory, as collisions are loaded at the same time.
     * <p>
     * Collisions are stored this way: #NAME# = {pattern|firstTile-lastTile}, and called with: getCollision(name).
     * </p>
     * <p>
     * Patterns number and name have to be written inside a file named 'count', else, all files as .png will be loaded.
     * </p>
     * 
     * @param directory The patterns directory.
     */
    void loadPatterns(Media directory);

    /**
     * Load map collision from an external file.
     * 
     * @param media The collision container.
     */
    void loadCollisions(Media media);

    /**
     * Load tile. Data are loaded this way:
     * 
     * <pre>
     * (integer) pattern number
     * (integer) index number inside pattern
     * (integer) tile location x
     * (integer tile location y
     * </pre>
     * 
     * @param nodes The collision nodes.
     * @param file The file reader reference.
     * @param i The last loaded tile number.
     * @return The loaded tile.
     * @throws IOException If error on reading.
     */
    T loadTile(List<XmlNode> nodes, FileReading file, int i) throws IOException;

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
     * Assign the collision function to all tiles with the same collision.
     * 
     * @param collision The current collision enum.
     * @param function The function reference.
     */
    void assignCollisionFunction(CollisionTile collision, CollisionFunction function);

    /**
     * Remove a collision function.
     * 
     * @param function The function to remove.
     */
    void removeCollisionFunction(CollisionFunction function);

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
     * Collisions are not saved, because it is possible to retrieve them from {@value #COLLISIONS_FILE_NAME}.
     * 
     * @param file The output file.
     * @throws IOException If error on writing.
     */
    void save(FileWriting file) throws IOException;

    /**
     * Render map from camera viewpoint, showing a specified area.
     * 
     * @param g The graphic output.
     * @param camera The camera viewpoint.
     */
    void render(Graphic g, CameraGame camera);

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
     * @param entity The entity.
     * @param offsetX The horizontal offset search.
     * @param offsetY The vertical offset search.
     * @return The tile found at the entity.
     */
    T getTile(Localizable entity, int offsetX, int offsetY);

    /**
     * Get the first tile hit by the localizable that contains collision, applying a ray tracing from its old location
     * to its current. This way, the localizable can not pass through a collidable tile.
     * 
     * @param localizable The localizable reference.
     * @param collisions Collisions list to search for.
     * @param applyRayCast <code>true</code> to apply collision to each tile crossed, <code>false</code> to ignore and
     *            just return the first tile hit.
     * @return The first tile hit, <code>null</code> if none found.
     */
    T getFirstTileHit(Localizable localizable, Set<CollisionTile> collisions, boolean applyRayCast);

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
    Set<Integer> getPatterns();

    /**
     * Get pattern (tilesheet) from its id.
     * 
     * @param pattern The pattern id.
     * @return The pattern found.
     */
    SpriteTiled getPattern(Integer pattern);

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
     * Get collision type from its name as string. The parameter value is read from the file describing the map
     * collisions. The best way to store map collisions name is to use an enum with the same names.
     * 
     * @param collision The collision name.
     * @return The collision type.
     */
    CollisionTile getCollisionFrom(String collision);

    /**
     * Get the supported collisions list.
     * 
     * @return The supported collisions list.
     */
    CollisionTile[] getCollisions();

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
