package com.b3dgs.lionengine.example.d_rts.a_navmaptile;

import com.b3dgs.lionengine.game.rts.map.TileRts;

/**
 * Tile implementation, using TileRts. Nothing special here, just to show the default inheritance.
 */
final class Tile
        extends TileRts<TileCollision, ResourceType>
{
    /**
     * Constructor.
     * 
     * @param width The tile width.
     * @param height The tile height.
     */
    Tile(int width, int height)
    {
        super(width, height);
    }

    /*
     * TileRts
     */

    @Override
    public TileCollision getCollisionFrom(String collision)
    {
        return TileCollision.NONE;
    }

    @Override
    public void checkResourceType(TileCollision collision)
    {
        setResourceType(ResourceType.NONE);
    }

    @Override
    public boolean checkBlocking(TileCollision collision)
    {
        return true;
    }

    @Override
    public boolean hasResources()
    {
        return getResourceType() != ResourceType.NONE;
    }
}
