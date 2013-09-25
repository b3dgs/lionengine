package com.b3dgs.lionengine.example.c_platform.c_entitymap;

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
     * Adjust the collision.
     */
    void adjustCollisions()
    {
        for (int tx = 0; tx < getWidthInTile(); tx++)
        {
            for (int ty = 0; ty < getHeightInTile(); ty++)
            {
                final Tile tile = getTile(tx, ty);
                final Tile top = getTile(tx, ty + 1);
                if (top != null && tile != null && tile.getCollision() != TileCollision.NONE
                        && top.getCollision() == tile.getCollision())
                {
                    tile.setCollision(TileCollision.WALL);
                }
            }
        }
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
        try
        {
            return TileCollision.valueOf(collision);
        }
        catch (final NullPointerException exception)
        {
            return TileCollision.NONE;
        }
    }
}
