package com.b3dgs.lionengine.example.d_rts.d_ability;

import com.b3dgs.lionengine.game.rts.map.MapTileRts;

/**
 * Map implementation.
 */
public final class Map
        extends MapTileRts<TileCollision, Tile>
{
    /**
     * Map constructor. Tiles are stored in 'tiles' directory, with a size of 16*16.
     */
    Map()
    {
        super(16, 16);
    }

    /*
     * MapTileRts
     */

    @Override
    public Tile createTile(int width, int height)
    {
        return new Tile(width, height);
    }
}
