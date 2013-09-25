package com.b3dgs.lionengine.example.d_rts.a_navmaptile;

import com.b3dgs.lionengine.game.rts.map.MapTileRts;

/**
 * Map implementation.
 */
final class Map
        extends MapTileRts<TileCollision, Tile>
{
    /**
     * Constructor.
     */
    Map()
    {
        super(16, 16);
    }

    /*
     * MapTileRts
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
