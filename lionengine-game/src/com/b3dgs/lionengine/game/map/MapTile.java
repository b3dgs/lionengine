package com.b3dgs.lionengine.game.map;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.Media;
import com.b3dgs.lionengine.game.CameraGame;

/**
 * Describe a map using tile for its representation. This is the lower level interface to describe a 2D map using tiles.
 * Each tiles are stored vertically and then horizontally. A pattern represents a tilesheet number (number of surface
 * containing tiles). A map can have one or more patterns.
 * 
 * @param <C> Tile collision type.
 * @param <T> Tile type used.
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
     * Remove all tiles from map.
     */
    void clear();

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
     * Render map from camera viewpoint, showing a specified area.
     * 
     * @param g The graphic output.
     * @param camera The camera viewpoint.
     */
    void render(Graphic g, CameraGame camera);

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
}
