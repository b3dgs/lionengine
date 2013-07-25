package com.b3dgs.lionengine.example.d_rts.b_cursor;

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

    @Override
    public Tile createTile(int width, int height)
    {
        return new Tile(width, height);
    }
}
