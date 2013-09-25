package com.b3dgs.lionengine.example.tilecollision;

import com.b3dgs.lionengine.game.platform.map.MapTilePlatform;

/**
 * Map implementation.
 */
final class Map
        extends MapTilePlatform<TileCollision, Tile>
{
    /**
     * Constructor.
     */
    Map()
    {
        super(16, 16);
    }

    /**
     * Create a block tile.
     * 
     * @param v The vertical location.
     * @param h The horizontal location.
     */
    public void createBlock(int v, int h)
    {
        final Tile tile = createTile(getTileWidth(), getTileHeight(), Integer.valueOf(0), 0, TileCollision.GROUND);
        setTile(v, h, tile);
    }

    /*
     * MapTilePlatform
     */

    @Override
    public Tile createTile(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        return new Tile(width, height, pattern, number, collision);
    }

    @Override
    public TileCollision getCollisionFrom(String collision)
    {
        return TileCollision.valueOf(collision);
    }
}
