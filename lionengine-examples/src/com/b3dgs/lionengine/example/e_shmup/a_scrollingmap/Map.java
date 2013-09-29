package com.b3dgs.lionengine.example.e_shmup.a_scrollingmap;

import com.b3dgs.lionengine.game.map.MapTileGame;

/**
 * Map implementation.
 */
final class Map
        extends MapTileGame<TileCollision, Tile>
{
    /**
     * Constructor.
     */
    Map()
    {
        super(24, 28);
    }

    /*
     * MapTileShmup
     */

    @Override
    public Tile createTile(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        return new Tile(width, height, pattern, number, collision);
    }

    @Override
    public TileCollision getCollisionFrom(String collision)
    {
        return TileCollision.NONE;
    }
}
