package com.b3dgs.lionengine.example.d_rts.e_skills;

import com.b3dgs.lionengine.game.rts.map.TileRts;

/**
 * Tile implementation, using TileModel. Nothing special here, just to show the default inheritance.
 */
public final class Tile
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

    @Override
    public TileCollision getCollisionFrom(String collision, String type)
    {
        return TileCollision.valueOf(type);
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
        return TileCollision.GROUND != collision;
    }

    @Override
    public boolean hasResources()
    {
        return getResourceType() != TypeResource.NONE;
    }
}
