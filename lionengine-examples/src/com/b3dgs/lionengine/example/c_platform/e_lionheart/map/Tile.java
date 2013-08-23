package com.b3dgs.lionengine.example.c_platform.e_lionheart.map;

import com.b3dgs.lionengine.game.platform.map.TilePlatform;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Tile implementation.
 */
public final class Tile
        extends TilePlatform<TileCollision>
{
    /** Half tile height, corresponding to the collision height location on tile. */
    private final int halfTileHeight;

    /**
     * Standard blank constructor.
     * 
     * @param width The tile width.
     * @param height The tile height.
     */
    public Tile(int width, int height)
    {
        super(width, height);
        halfTileHeight = height / 2;
    }

    /**
     * Check if tile is a border.
     * 
     * @return <code>true</code> if border, <code>false</code> else.
     */
    public boolean isBorder()
    {
        return getCollision().isBorder();
    }

    /**
     * Check if tile is from this group.
     * 
     * @param group The group to check to.
     * @return <code>true</code> if from this group, <code>false</code> else.
     */
    public boolean isGroup(TileCollisionGroup group)
    {
        return getCollision().getGroup() == group;
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
        final double x = localizable.getLocationX() - getX() - getWidth();
        final double y = getTop() - x / TileCollisionGroup.SLOPE.getFactor() + offset;
        if (localizable.getLocationOldY() >= y - halfTileHeight && localizable.getLocationY() <= y)
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
        final double y = getTop() + x / TileCollisionGroup.SLOPE.getFactor() + offset;
        if (localizable.getLocationOldY() >= y - halfTileHeight && localizable.getLocationY() <= y)
        {
            return Double.valueOf(y);
        }
        return null;
    }

    /*
     * TilePlatform
     */

    @Override
    public TileCollision getCollisionFrom(String collision, String type)
    {
        try
        {
            return TileCollision.valueOf(collision);
        }
        catch (final IllegalArgumentException
                     | NullPointerException exception)
        {
            return TileCollision.NONE;
        }
    }

    @Override
    public int getTop()
    {
        return super.getTop() - halfTileHeight;
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
            case BORDER_LEFT:
            case BORDER_CENTER:
            case BORDER_RIGHT:
                return getGround(localizable);

            case SLOPE_RIGHT_1:
                return getSlopeRight(localizable, halfTileHeight);
            case SLOPE_RIGHT_BORDER_DOWN:
            case SLOPE_RIGHT_BORDER_UP:
            case SLOPE_RIGHT_2:
                return getSlopeRight(localizable, 0);
            case SLOPE_RIGHT_3:
                return getSlopeRight(localizable, -halfTileHeight);

            case SLOPE_LEFT_1:
                return getSlopeLeft(localizable, halfTileHeight);
            case SLOPE_LEFT_BORDER_DOWN:
            case SLOPE_LEFT_BORDER_UP:
            case SLOPE_LEFT_2:
                return getSlopeLeft(localizable, 0);
            case SLOPE_LEFT_3:
                return getSlopeLeft(localizable, -halfTileHeight);

            default:
                return null;
        }
    }
}
