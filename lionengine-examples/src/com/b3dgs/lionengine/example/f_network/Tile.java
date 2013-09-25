package com.b3dgs.lionengine.example.f_network;

import com.b3dgs.lionengine.game.platform.map.TilePlatform;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Tile implementation.
 */
class Tile
        extends TilePlatform<TileCollision>
{
    /**
     * @see TilePlatform#TilePlatform(int, int, Integer, int, Enum)
     */
    public Tile(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        super(width, height, pattern, number, collision);
    }

    @Override
    public Double getCollisionX(Localizable localizable)
    {
        final int top = getTop();

        if (getCollision() == TileCollision.WALL || localizable.getLocationOldY() < top)
        {
            // From left
            if (localizable.getLocationOldX() < localizable.getLocationX())
            {
                final int left = getLeft();
                if (localizable.getLocationX() >= left)
                {
                    return Double.valueOf(left - 5);
                }
            }
            // From right
            if (localizable.getLocationOldX() > localizable.getLocationX())
            {
                final int right = getRight();
                if (localizable.getLocationX() <= right)
                {
                    return Double.valueOf(right + 5);
                }
            }
        }
        return null;
    }

    @Override
    public Double getCollisionY(Localizable localizable)
    {
        // From top
        final int top = getTop();
        final int bottom = getTop() - 2;
        if (localizable.getLocationOldY() >= bottom && localizable.getLocationY() <= top)
        {
            return Double.valueOf(top);
        }
        return null;
    }
}
