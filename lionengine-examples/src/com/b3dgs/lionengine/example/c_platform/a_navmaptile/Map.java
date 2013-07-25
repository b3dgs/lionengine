package com.b3dgs.lionengine.example.c_platform.a_navmaptile;

import com.b3dgs.lionengine.game.platform.map.MapTilePlatform;

/**
 * Map implementation.
 */
class Map
        extends MapTilePlatform<TileCollision, Tile>
{
    /**
     * Map constructor. Tiles are stored in 'tiles' directory, with a size of 16*16
     */
    public Map()
    {
        super(16, 16);
    }

    @Override
    public Tile createTile(int width, int height)
    {
        return new Tile(width, height);
    }
}
