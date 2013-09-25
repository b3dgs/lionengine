package com.b3dgs.lionengine.example.d_rts.e_skills;

import com.b3dgs.lionengine.game.rts.map.TileRts;

/**
 * Tile implementation, using TileModel. Nothing special here, just to show the default inheritance.
 */
public final class Tile
        extends TileRts<TileCollision, ResourceType>
{
    /**
     * Constructor.
     * 
     * @param width The tile width.
     * @param height The tile height.
     * @param pattern The tile pattern.
     * @param number The tile number.
     * @param collision The tile collision.
     */
    public Tile(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        super(width, height, pattern, number, collision);
    }

    /*
     * TileRts
     */

    @Override
    public void checkResourceType(TileCollision collision)
    {
        switch (collision.getGroup())
        {
            case TREE:
                setResourceType(ResourceType.WOOD);
                break;
            default:
                setResourceType(ResourceType.NONE);
                break;
        }
    }

    @Override
    public boolean checkBlocking(TileCollision collision)
    {
        return TileCollisionGroup.GROUND != collision.getGroup();
    }

    @Override
    public boolean hasResources()
    {
        return getResourceType() != ResourceType.NONE;
    }
}
