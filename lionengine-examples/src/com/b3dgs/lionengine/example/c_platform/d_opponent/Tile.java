package com.b3dgs.lionengine.example.c_platform.d_opponent;

import com.b3dgs.lionengine.game.platform.map.TilePlatform;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Tile implementation, using TileModel.
 */
final class Tile
        extends TilePlatform<TileCollision>
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
     * TilePlatform
     */

    @Override
    public TileCollision getCollisionFrom(String collision)
    {
        try
        {
            return TileCollision.valueOf(collision);
        }
        catch (final NullPointerException exception)
        {
            return TileCollision.NONE;
        }
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
                    return Double.valueOf(left);
                }
            }
            // From right
            if (localizable.getLocationOldX() > localizable.getLocationX())
            {
                final int right = getRight();
                if (localizable.getLocationX() <= right)
                {
                    return Double.valueOf(right);
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

    @Override
    public int getTop()
    {
        return super.getTop() - 8;
    }
}
