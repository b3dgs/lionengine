package com.b3dgs.lionengine.example.d_rts.c_controlpanel;

import com.b3dgs.lionengine.example.d_rts.b_cursor.TileCollision;
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
        try
        {
            return TileCollision.valueOf(collision);
        }
        catch (IllegalArgumentException
               | NullPointerException exception)
        {
            return TileCollision.NONE;
        }
    }
}
