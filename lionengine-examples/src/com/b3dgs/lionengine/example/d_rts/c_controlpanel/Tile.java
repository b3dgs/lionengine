package com.b3dgs.lionengine.example.d_rts.c_controlpanel;

import com.b3dgs.lionengine.example.d_rts.b_cursor.TileCollision;
import com.b3dgs.lionengine.game.rts.map.TileRts;

/**
 * Tile implementation.
 */
final class Tile
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
