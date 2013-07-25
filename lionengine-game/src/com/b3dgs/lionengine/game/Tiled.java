package com.b3dgs.lionengine.game;

/**
 * Represents something that can have a tile representation (in location and in size).
 */
public interface Tiled
{
    /**
     * Get horizontal tile location.
     * 
     * @return The horizontal tile location.
     */
    int getLocationInTileX();

    /**
     * Get vertical tile location.
     * 
     * @return The vertical tile location.
     */
    int getLocationInTileY();

    /**
     * Get the width in tile.
     * 
     * @return The width in tile.
     */
    int getWidthInTile();

    /**
     * Get the height in tile.
     * 
     * @return The height in tile.
     */
    int getHeightInTile();
}
