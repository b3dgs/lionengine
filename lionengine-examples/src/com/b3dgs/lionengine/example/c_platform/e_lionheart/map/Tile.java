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
     * Check if tile is a border.
     * 
     * @return <code>true</code> if border, <code>false</code> else.
     */
    public boolean isBorder()
    {
        return getCollision().isBorder();
    }

    /**
     * Check if tile if left part of this group.
     * 
     * @param group The group to check.
     * @return <code>true</code> if left.
     */
    public boolean isLeft(TileCollisionGroup group)
    {
        return isGroup(group) && getCollision().isLeft();
    }

    /**
     * Check if tile if right part of this group.
     * 
     * @param group The group to check.
     * @return <code>true</code> if right.
     */
    public boolean isRight(TileCollisionGroup group)
    {
        return isGroup(group) && !getCollision().isLeft();
    }

    /**
     * Get the y location on the tile in tilt case.
     * 
     * @param group The collision group.
     * @param localizable The localizable.
     * @param startY The starting location y.
     * @param offset The offset value.
     * @return The vertical relative location on tile.
     */
    private double getOnTileTiltY(TileCollisionGroup group, Localizable localizable, int startY, int offset)
    {
        final boolean left = isLeft(group);
        final double x = localizable.getLocationX() - getX() - (left ? 0 : getWidth());
        return startY + x * group.getFactor() * (left ? 1 : -1) + offset;
    }

    /**
     * Get the collision y value (<code>null</code> if none).
     * 
     * @param group The collision group.
     * @param localizable The localizable.
     * @param startY The starting location y.
     * @param offset The offset value.
     * @param mOldY The old y offset test.
     * @param mY The y offset test.
     * @param cY The collision offset y.
     * @return The collision y value.
     */
    private Double getCollisionY(TileCollisionGroup group, Localizable localizable, int startY, int offset, int mOldY,
            int mY, int cY)
    {
        final double y = getOnTileTiltY(group, localizable, startY, offset);
        if (localizable.getLocationOldY() >= y + mOldY && localizable.getLocationY() <= y + mY)
        {
            return Double.valueOf(y + cY);
        }
        return null;
    }

    /**
     * Get ground collision.
     * 
     * @param localizable The localizable.
     * @param offset The offset value.
     * @return The collision.
     */
    private Double getGround(Localizable localizable, int offset)
    {
        final int top = getTop();
        final int bottom = top - 2;
        if (localizable.getLocationOldY() >= bottom && localizable.getLocationY() <= top)
        {
            return Double.valueOf(top + offset);
        }
        return null;
    }

    /**
     * Get the slope collision.
     * 
     * @param c The collision type.
     * @param localizable The localizable.
     * @param offset The offset.
     * @param border The collision border.
     * @return The collision.
     */
    private Double getSlope(TileCollision c, Localizable localizable, int offset, TileCollision border)
    {
        final double y = getOnTileTiltY(c.getGroup(), localizable, getTop(), offset);
        if (getCollision() == border && y > super.getTop())
        {
            return getCollisionY(c.getGroup(), localizable, getTop(), offset, -halfTileHeight, 0,
                    (int) -y + super.getTop());
        }
        return getCollisionY(c.getGroup(), localizable, getTop(), offset, -halfTileHeight, 0, 0);
    }

    /**
     * Get the slide right collision.
     * 
     * @param c The collision type.
     * @param localizable The localizable.
     * @param offset The offset.
     * @return The collision.
     */
    private Double getSlide(TileCollision c, Localizable localizable, int offset)
    {
        final int startY = isLeft(c.getGroup()) ? getBottom() : getTop();
        return getCollisionY(c.getGroup(), localizable, startY, offset, -28 - halfTileHeight, -14, -21);
    }

    /**
     * Get the liana steep collision.
     * 
     * @param c The collision type.
     * @param localizable The localizable.
     * @param offset The offset.
     * @return The collision.
     */
    private Double getLianaSteep(TileCollision c, Localizable localizable, int offset)
    {
        final int startY = getTop() + (isLeft(c.getGroup()) ? 0 : 2);
        return getCollisionY(c.getGroup(), localizable, startY, offset, -10, 5, -2);
    }

    /**
     * Get the liana leaning right collision.
     * 
     * @param c The collision type.
     * @param localizable The localizable.
     * @param offset The offset.
     * @return The collision.
     */
    private Double getLianaLeaning(TileCollision c, Localizable localizable, int offset)
    {
        return getCollisionY(c.getGroup(), localizable, getBottom(), offset, -halfTileHeight, 0, -1);
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
        catch (final IllegalArgumentException
                     | NullPointerException exception)
        {
            return TileCollision.NONE;
        }
    }

    @Override
    public int getTop()
    {
        return super.getBottom() + 2;
    }

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
        final double x = localizable.getLocationX() - getX() - getWidth();
        final TileCollision c = getCollision();
        switch (c)
        {
            case GROUND:
            case GROUND_SPIKE:
            case BORDER_LEFT:
            case BORDER_CENTER:
            case BORDER_RIGHT:
                return getGround(localizable, 0);

            case SLOPE_RIGHT_1:
            case SLOPE_RIGHT_BORDER_UP:
                return getSlope(c, localizable, halfTileHeight, TileCollision.SLOPE_RIGHT_BORDER_UP);
            case SLOPE_RIGHT_BORDER_DOWN:
            case SLOPE_RIGHT_2:
                return getSlope(c, localizable, 0, null);
            case SLOPE_RIGHT_3:
                return getSlope(c, localizable, -halfTileHeight, null);

            case SLOPE_LEFT_1:
            case SLOPE_LEFT_BORDER_UP:
                return getSlope(c, localizable, halfTileHeight, TileCollision.SLOPE_LEFT_BORDER_UP);
            case SLOPE_LEFT_BORDER_DOWN:
            case SLOPE_LEFT_2:
                return getSlope(c, localizable, 0, null);
            case SLOPE_LEFT_3:
                return getSlope(c, localizable, -halfTileHeight, null);

            case SLIDE_RIGHT_1:
                return getSlide(c, localizable, halfTileHeight);
            case SLIDE_RIGHT_2:
                return getSlide(c, localizable, 23);
            case SLIDE_RIGHT_3:
                return getSlide(c, localizable, -halfTileHeight);
            case SLIDE_RIGHT_GROUND_SLIDE:
                if (x > -halfTileHeight)
                {
                    return getSlide(c, localizable, halfTileHeight);
                }
                return getGround(localizable, 0);

            case SLIDE_LEFT_1:
                return getSlide(c, localizable, halfTileHeight);
            case SLIDE_LEFT_2:
                return getSlide(c, localizable, 23);
            case SLIDE_LEFT_3:
                return getSlide(c, localizable, -halfTileHeight);
            case SLIDE_LEFT_GROUND_SLIDE:
                return getGround(localizable, 0);

            case LIANA_HORIZONTAL:
                return getGround(localizable, -2);

            case LIANA_STEEP_RIGHT_2:
                return getLianaSteep(c, localizable, 0);

            case LIANA_STEEP_LEFT_1:
                return getLianaSteep(c, localizable, -14);
            case LIANA_STEEP_LEFT_2:
                return getLianaSteep(c, localizable, 0);

            case LIANA_LEANING_RIGHT_1:
                return getLianaLeaning(c, localizable, halfTileHeight);
            case LIANA_LEANING_RIGHT_2:
                return getLianaLeaning(c, localizable, 0);
            case LIANA_LEANING_RIGHT_3:
                return getLianaLeaning(c, localizable, -halfTileHeight);

            case LIANA_LEANING_LEFT_1:
                return getLianaLeaning(c, localizable, halfTileHeight);
            case LIANA_LEANING_LEFT_2:
                return getLianaLeaning(c, localizable, 0);
            case LIANA_LEANING_LEFT_3:
                return getLianaLeaning(c, localizable, -halfTileHeight);

            default:
                return null;
        }
    }
}
