package com.b3dgs.lionengine.example.c_platform.e_lionheart.map;

import com.b3dgs.lionengine.game.platform.map.TilePlatform;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Tile implementation.
 */
public class Tile
        extends TilePlatform<TileCollision>
{
    /**
     * Standard blank constructor.
     * 
     * @param width The tile width.
     * @param height The tile height.
     */
    public Tile(int width, int height)
    {
        super(width, height);
    }

    @Override
    public TileCollision getCollisionFrom(String collision, String type)
    {
        try
        {
            return TileCollision.valueOf(collision);
        }
        catch (final NullPointerException exception)
        {
            return TileCollision.NONE;
        }
        catch (final IllegalArgumentException exception)
        {
            return TileCollision.NONE;
        }
    }

    @Override
    public Double getCollisionX(Localizable localizable)
    {
        return null;
    }

    @Override
    public Double getCollisionY(Localizable localizable)
    {
        switch (getCollision())
        {
            case GROUND:
                return getGround(localizable);
            case BORDER_LEFT:
                return getGround(localizable);
            case BORDER_CENTER:
                return getGround(localizable);
            case BORDER_RIGHT:
                return getGround(localizable);
            case SLOPE_RIGHT_1:
                return getSlopeRight(localizable, 8);
            case SLOPE_RIGHT_2:
                return getSlopeRight(localizable, 0);
            case SLOPE_RIGHT_3:
                return getSlopeRight(localizable, -8);
            case SLOPE_LEFT_1:
                return getSlopeLeft(localizable, 8);
            case SLOPE_LEFT_2:
                return getSlopeLeft(localizable, 0);
            case SLOPE_LEFT_3:
                return getSlopeLeft(localizable, -8);
            default:
                return null;
        }
    }

    /**
     * Get ground collision.
     * 
     * @param localizable The localizable.
     * @return The collision.
     */
    private Double getGround(Localizable localizable)
    {
        final int top = getTop();
        final int bottom = getTop() - 2;
        if (localizable.getLocationOldY() >= bottom && localizable.getLocationY() <= top)
        {
            return Double.valueOf(top);
        }
        return null;
    }

    /**
     * Get the slope right collision.
     * 
     * @param localizable The localizable.
     * @param offset The offset.
     * @return The collision.
     */
    private Double getSlopeRight(Localizable localizable, int offset)
    {
        final double x = localizable.getLocationX() - getX() - 16;
        final double y = getTop() - x / 2.0 + offset;
        if (localizable.getLocationOldY() >= y - getHeight() / 2 && localizable.getLocationY() <= y)
        {
            return Double.valueOf(y);
        }
        return null;
    }

    /**
     * Get the slope left collision.
     * 
     * @param localizable The localizable.
     * @param offset The offset.
     * @return The collision.
     */
    private Double getSlopeLeft(Localizable localizable, int offset)
    {
        final double x = localizable.getLocationIntX() - getX();
        final double y = getTop() + x / 2.0 + offset;
        if (localizable.getLocationOldY() >= y - getHeight() / 2 && localizable.getLocationY() <= y)
        {
            return Double.valueOf(y);
        }
        return null;
    }

    @Override
    public int getTop()
    {
        return super.getTop() - 8;
    }
}
