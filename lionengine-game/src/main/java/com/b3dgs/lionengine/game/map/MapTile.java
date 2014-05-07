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
import java.util.Set;

import com.b3dgs.lionengine.core.Graphic;
import com.b3dgs.lionengine.core.ImageBuffer;
import com.b3dgs.lionengine.core.Media;
import com.b3dgs.lionengine.file.FileReading;
import com.b3dgs.lionengine.file.FileWriting;
import com.b3dgs.lionengine.game.CameraGame;

/**
 * Describe a map using tile for its representation. This is the lower level interface to describe a 2D map using tiles.
 * Each tiles are stored vertically and then horizontally. A pattern represents a tilesheet number (number of surface
 * containing tiles). A map can have one or more patterns.
 * 
 * @param <C> Tile collision type.
 * @param <T> Tile type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 * @see TileGame
 * @see MapTileGame
 */
public interface MapTile<C extends Enum<C>, T extends TileGame<C>>
{
    /**
     * Create and prepare map memory area. Must be called before assigning tiles.
     * 
     * @param widthInTile The map width in tile.
     * @param heightInTile The map height in tile.
     */
    void create(int widthInTile, int heightInTile);

    /**
     * Generate the minimap from the current map.
     */
    void createMiniMap();

    /**
     * Load a map from a level rip and the associated tiles directory.
     * 
     * @param levelrip The file containing the levelrip as an image.
     * @param patternsDirectory The directory containing tiles themes.
     */
    void load(Media levelrip, Media patternsDirectory);

    /**
     * Load map patterns (tiles surfaces) from theme name. Must be called after map creation. A file called
     * collisions.txt has to be in the same directory, as collisions are loaded at the same time.
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
     * Append an existing map, starting at the specified offsets. Offsets start at the beginning of the map (0, 0).
     * A call to {@link #append(MapTile, int, int)} at ({@link #getWidthInTile()}, {@link #getHeightInTile()}) will add
     * the new map at the top-right.
     * 
     * @param map The map to append.
     * @param offsetX The horizontal offset in tile (>= 0).
     * @param offsetY The vertical offset in tile (>= 0).
     */
    void append(MapTile<C, T> map, int offsetX, int offsetY);

    /**
     * Remove all tiles from map.
     */
    void clear();

    /**
     * Save map to specified file as binary data.
     * 
     * @param file The output file.
     * @throws IOException If error on writing.
     */
    void save(FileWriting file) throws IOException;

    /**
     * Load a map from a specified file as binary data.
     * 
     * @param file The input file.
     * @throws IOException If error on reading.
     */
    void load(FileReading file) throws IOException;

    /**
     * Render map from camera viewpoint, showing a specified area.
     * 
     * @param g The graphic output.
     * @param camera The camera viewpoint.
     */
    void render(Graphic g, CameraGame camera);

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
     * Get tile from specified map location (in tile index). If the returned tile is equal to <code>null</code>, this
     * means that there is not tile at this location. It is not an error, just a way to avoid useless tile storage.
     * 
     * @param tx The horizontal tile index location.
     * @param ty The vertical tile index location.
     * @return The tile reference.
     */
    T getTile(int tx, int ty);

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
