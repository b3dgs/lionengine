package com.b3dgs.lionengine.example.d_rts.a_navmaptile;

import com.b3dgs.lionengine.game.rts.map.TileRts;

/**
 * Tile implementation, using TileRts. Nothing special here, just to show the default inheritance.
 */
final class Tile
        extends TileRts<TypeCollision, TypeResource>
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
    public TypeCollision getCollisionFrom(String collision, String type)
    {
        return TypeCollision.NONE;
    }

    @Override
    public void checkResourceType(TypeCollision collision)
    {
        setResourceType(TypeResource.NONE);
    }

    @Override
    public boolean checkBlocking(TypeCollision collision)
    {
        return true;
    }

    @Override
    public boolean hasResources()
    {
        return getResourceType() != TypeResource.NONE;
    }
}
