package com.b3dgs.lionengine.example.d_rts.d_ability;

import com.b3dgs.lionengine.game.rts.map.TileRts;

/**
 * Tile implementation. Nothing special here, just to show the default inheritance.
 */
final class Tile
        extends TileRts<TileCollision, TypeResource>
{
    /**
     * Standard complete constructor.
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
    public TileCollision getCollisionFrom(String collision, String type)
    {
        try
        {
            return TileCollision.valueOf(type);
        }
        catch (IllegalArgumentException
               | NullPointerException exception)
        {
            return TileCollision.NONE;
        }
    }

    @Override
    public void checkResourceType(TileCollision collision)
    {
        switch (collision)
        {
            case TREE:
                setResourceType(TypeResource.WOOD);
                break;
            default:
                setResourceType(TypeResource.NONE);
                break;
        }
    }

    @Override
    public boolean checkBlocking(TileCollision collision)
    {
        // Block if not ground
        return TileCollision.GROUND != collision;
    }

    @Override
    public boolean hasResources()
    {
        return getResourceType() != TypeResource.NONE;
    }
}
