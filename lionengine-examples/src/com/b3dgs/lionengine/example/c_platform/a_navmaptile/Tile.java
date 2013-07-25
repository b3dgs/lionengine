package com.b3dgs.lionengine.example.c_platform.a_navmaptile;

import com.b3dgs.lionengine.game.maptile.TileGame;

/**
 * Tile implementation, using TileModel. Nothing special here, just to show the default inheritance.
 */
class Tile
        extends TileGame<TileCollision>
{
    /**
     * Standard blank constructor.
     * 
     * @param width The tile width.
     * @param height The tile height.
     */
    public Tile(int width, int height)
    {
        super(width, height);
    }

    @Override
    public TileCollision getCollisionFrom(String collision, String type)
    {
        return TileCollision.NONE;
    }
}
