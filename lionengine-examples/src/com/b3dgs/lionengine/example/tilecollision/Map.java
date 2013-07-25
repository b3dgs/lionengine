package com.b3dgs.lionengine.example.tilecollision;

import com.b3dgs.lionengine.game.platform.map.MapTilePlatform;

/**
 * Map implementation.
 */
class Map
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
        final Tile tile = createTile(getTileWidth(), getTileHeight());
        tile.setCollision(TileCollision.GROUND);
        tile.setPattern(Integer.valueOf(0));
        tile.setNumber(0);
        setTile(v, h, tile);
    }

    @Override
    public Tile createTile(int width, int height)
    {
        return new Tile(width, height);
    }
}
