package com.b3dgs.lionengine.example.e_shmup.a_scrollingmap;

import com.b3dgs.lionengine.game.shmup.MapTileShmup;

/**
 * Map implementation.
 */
final class Map
        extends MapTileShmup<TileCollision, Tile>
{
    /**
     * Constructor. Tiles are stored in 'tiles' directory, with a size of 16*16
     */
    Map()
    {
        super(24, 28);
    }

    /*
     * MapTileShmup
     */

    @Override
    public Tile createTile(int width, int height)
    {
        return new Tile(width, height);
    }
}
