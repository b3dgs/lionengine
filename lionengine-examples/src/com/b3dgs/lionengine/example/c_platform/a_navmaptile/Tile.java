package com.b3dgs.lionengine.example.c_platform.a_navmaptile;

import com.b3dgs.lionengine.game.platform.map.TilePlatform;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Tile implementation.
 */
final class Tile
        extends TilePlatform<TileCollision>
{
    /**
     * @see TilePlatform#TilePlatform(int, int, Integer, int, Enum)
     */
    public Tile(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        super(width, height, pattern, number, collision);
    }

    /*
     * TilePlatform
     */

    @Override
    public Double getCollisionX(Localizable localizable)
    {
        return null;
    }

    @Override
    public Double getCollisionY(Localizable localizable)
    {
        return null;
    }
}
