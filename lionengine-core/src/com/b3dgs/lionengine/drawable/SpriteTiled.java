package com.b3dgs.lionengine.drawable;

import java.awt.image.BufferedImage;

import com.b3dgs.lionengine.Graphic;

/**
 * Tiled sprite are mainly used for tile based levels. It works by loading an image, and split it into different images.
 */
public interface SpriteTiled
        extends Sprite
{
    /**
     * Render a tile to the specified coordinates.
     * 
     * @param g The graphic output.
     * @param tile The tile to render (>= 0).
     * @param x The abscissa.
     * @param y The ordinate.
     */
    void render(Graphic g, int tile, int x, int y);

    /**
     * Get the number of horizontal tiles.
     * 
     * @return The number of horizontal tiles.
     */
    int getTilesHorizontal();

    /**
     * Get the number of vertical tiles.
     * 
     * @return The number of vertical tiles.
     */
    int getTilesVertical();

    /**
     * Get the number of tiles.
     * 
     * @return The number of tiles.
     */
    int getTilesNumber();

    /**
     * Get current tile width.
     * 
     * @return The tile width.
     */
    int getTileWidth();

    /**
     * Get current tile height.
     * 
     * @return The tile height.
     */
    int getTileHeight();

    /**
     * Get original tile width.
     * 
     * @return The tile width.
     */
    int getTileWidthOriginal();

    /**
     * Get original tile height.
     * 
     * @return The tile height.
     */
    int getTileHeightOriginal();

    /**
     * Get a tile (store it on a new buffered image, no reference, can be slow).
     * 
     * @param tile The desired tile.
     * @return The tile's surface.
     */
    BufferedImage getTile(int tile);

    /**
     * Get a tile (as reference, faster).
     * 
     * @param tile The desired tile.
     * @return The tile's surface.
     */
    BufferedImage getTileReference(int tile);

    /**
     * Get instanced version of current tiled sprite (shares the same surface).
     * 
     * @return The cloned tiled sprite.
     */
    @Override
    SpriteTiled instanciate();
}
