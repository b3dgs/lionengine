package com.b3dgs.lionengine.example.c_platform.e_lionheart.map;

import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Tile ground implementation.
 */
public final class TileGround
        extends Tile
{
    /**
     * @see Tile#Tile(int, int, Integer, int, TileCollision)
     */
    public TileGround(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        super(width, height, pattern, number, collision);
    }

    /*
     * TilePlatform
     */

    @Override
    public Double getCollisionX(Localizable localizable)
    {
        final double x = localizable.getLocationX() - getX() - getWidth();
        final TileCollision c = getCollision();
        switch (c)
        {
            case GROUND_SPIKE:
                if (x > -16 && x < -13)
                {
                    return Double.valueOf(getX());
                }
                else if (x > -3 && x < 1)
                {
                    return Double.valueOf(getX() + getWidth());
                }
                return null;

            default:
                return null;
        }
    }

    @Override
    public Double getCollisionY(Localizable localizable)
    {
        final TileCollision c = getCollision();
        switch (c)
        {
            case GROUND:
            case GROUND_SPIKE:
            case BORDER_LEFT:
            case BORDER_CENTER:
            case BORDER_RIGHT:
                return getGround(localizable, 0);

            default:
                return null;
        }
    }
}
