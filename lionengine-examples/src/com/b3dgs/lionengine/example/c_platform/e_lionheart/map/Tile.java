package com.b3dgs.lionengine.example.c_platform.e_lionheart.map;

import com.b3dgs.lionengine.game.platform.map.TilePlatform;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Tile implementation.
 */
public final class Tile
        extends TilePlatform<TypeTileCollision>
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
    public boolean isGroup(TypeTileCollisionGroup group)
    {
        return getCollision().getGroup() == group;
    }

    /**
     * Check if tile is a left slope.
     * 
     * @return <code>true</code> if left slope, <code>false</code> else.
     */
    public boolean isSlopeLeft()
    {
        final TypeTileCollision c = getCollision();
        return c == TypeTileCollision.SLOPE_LEFT_1 || c == TypeTileCollision.SLOPE_LEFT_2
                || c == TypeTileCollision.SLOPE_LEFT_3 || c == TypeTileCollision.SLOPE_LEFT_BORDER_DOWN
                || c == TypeTileCollision.SLOPE_LEFT_BORDER_UP;
    }

    /**
     * Check if tile is a right slope.
     * 
     * @return <code>true</code> if right slope, <code>false</code> else.
     */
    public boolean isSlopeRight()
    {
        final TypeTileCollision c = getCollision();
        return c == TypeTileCollision.SLOPE_RIGHT_1 || c == TypeTileCollision.SLOPE_RIGHT_2
                || c == TypeTileCollision.SLOPE_RIGHT_3 || c == TypeTileCollision.SLOPE_RIGHT_BORDER_DOWN
                || c == TypeTileCollision.SLOPE_RIGHT_BORDER_UP;
    }

    /**
     * Check if tile is a left liana steep.
     * 
     * @return <code>true</code> if left liana steep, <code>false</code> else.
     */
    public boolean isLianaSteepLeft()
    {
        final TypeTileCollision c = getCollision();
        return c == TypeTileCollision.LIANA_STEEP_LEFT_1 || c == TypeTileCollision.LIANA_STEEP_LEFT_2;
    }

    /**
     * Check if tile is a left liana steep.
     * 
     * @return <code>true</code> if left liana steep, <code>false</code> else.
     */
    public boolean isLianaSteepRight()
    {
        final TypeTileCollision c = getCollision();
        return c == TypeTileCollision.LIANA_STEEP_RIGHT_1 || c == TypeTileCollision.LIANA_STEEP_RIGHT_2;
    }

    /**
     * Check if tile is a left slide.
     * 
     * @return <code>true</code> if a left slide, <code>false</code> else.
     */
    public boolean isSlideLeft()
    {
        final TypeTileCollision c = getCollision();
        return c == TypeTileCollision.SLIDE_LEFT_1 || c == TypeTileCollision.SLIDE_LEFT_2
                || c == TypeTileCollision.SLIDE_LEFT_3;
    }

    /**
     * Check if tile is a right slide.
     * 
     * @return <code>true</code> if a left slide, <code>false</code> else.
     */
    public boolean isSlideRight()
    {
        final TypeTileCollision c = getCollision();
        return c == TypeTileCollision.SLIDE_RIGHT_1 || c == TypeTileCollision.SLIDE_RIGHT_2
                || c == TypeTileCollision.SLIDE_RIGHT_3;
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
        double y = getTop() - x * TypeTileCollisionGroup.SLOPE.getFactor() + offset;
        if (getCollision() == TypeTileCollision.SLOPE_RIGHT_BORDER_UP && y > super.getTop())
        {
            y = super.getTop();
        }
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
        final double x = localizable.getLocationX() - getX();
        double y = getTop() + x * TypeTileCollisionGroup.SLOPE.getFactor() + offset;
        if (getCollision() == TypeTileCollision.SLOPE_LEFT_BORDER_UP && y > super.getTop())
        {
            y = super.getTop();
        }
        if (localizable.getLocationOldY() >= y - halfTileHeight && localizable.getLocationY() <= y)
        {
            return Double.valueOf(y);
        }
        return null;
    }

    /**
     * Get the slide right collision.
     * 
     * @param localizable The localizable.
     * @param offset The offset.
     * @return The collision.
     */
    private Double getSlideRight(Localizable localizable, int offset)
    {
        final double x = localizable.getLocationX() - getX() - getWidth();
        final double y = getTop() - x * TypeTileCollisionGroup.SLIDE.getFactor() + offset;
        if (localizable.getLocationOldY() >= y - 28 - halfTileHeight && localizable.getLocationY() <= y - 14)
        {
            return Double.valueOf(y - 21);
        }
        return null;
    }

    /**
     * Get the slide left collision.
     * 
     * @param localizable The localizable.
     * @param offset The offset.
     * @return The collision.
     */
    private Double getSlideLeft(Localizable localizable, int offset)
    {
        final double x = localizable.getLocationX() - getX();
        final double y = getBottom() + x * TypeTileCollisionGroup.SLIDE.getFactor() + offset;
        if (localizable.getLocationOldY() >= y - 28 - halfTileHeight && localizable.getLocationY() <= y - 14)
        {
            return Double.valueOf(y - 21);
        }
        return null;
    }

    /**
     * Get liana horizontal collision.
     * 
     * @param localizable The localizable.
     * @return The collision.
     */
    private Double getLianaHorizontal(Localizable localizable)
    {
        final int top = getTop();
        final int bottom = top - 2;
        if (localizable.getLocationOldY() >= bottom && localizable.getLocationY() <= top)
        {
            return Double.valueOf(top - 1);
        }
        return null;
    }

    /**
     * Get the liana steep left collision.
     * 
     * @param localizable The localizable.
     * @return The collision.
     */
    private Double getLianaSteepLeft(Localizable localizable)
    {
        final double x = localizable.getLocationX() - getX();
        final double y = getTop() + x * TypeTileCollisionGroup.LIANA_STEEP.getFactor();
        if (localizable.getLocationOldY() >= y - 10 && localizable.getLocationY() <= y + 5)
        {
            return Double.valueOf(y - 2);
        }
        return null;
    }

    /*
     * TilePlatform
     */

    @Override
    public TypeTileCollision getCollisionFrom(String collision, String type)
    {
        try
        {
            return TypeTileCollision.valueOf(collision);
        }
        catch (final IllegalArgumentException
                     | NullPointerException exception)
        {
            return TypeTileCollision.NONE;
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
            case SLOPE_RIGHT_BORDER_UP:
                return getSlopeRight(localizable, halfTileHeight);
            case SLOPE_RIGHT_BORDER_DOWN:
            case SLOPE_RIGHT_2:
                return getSlopeRight(localizable, 0);
            case SLOPE_RIGHT_3:
                return getSlopeRight(localizable, -halfTileHeight);

            case SLOPE_LEFT_1:
            case SLOPE_LEFT_BORDER_UP:
                return getSlopeLeft(localizable, halfTileHeight);
            case SLOPE_LEFT_BORDER_DOWN:
            case SLOPE_LEFT_2:
                return getSlopeLeft(localizable, 0);
            case SLOPE_LEFT_3:
                return getSlopeLeft(localizable, -halfTileHeight);

            case SLIDE_RIGHT_1:
                return getSlideRight(localizable, halfTileHeight);
            case SLIDE_RIGHT_2:
                return getSlideRight(localizable, 23);
            case SLIDE_RIGHT_3:
                return getSlideRight(localizable, -halfTileHeight);

            case SLIDE_LEFT_1:
                return getSlideLeft(localizable, halfTileHeight);
            case SLIDE_LEFT_2:
                return getSlideLeft(localizable, 23);
            case SLIDE_LEFT_3:
                return getSlideLeft(localizable, -halfTileHeight);

            case LIANA_HORIZONTAL:
                return getLianaHorizontal(localizable);

            case LIANA_STEEP_LEFT_2:
                return getLianaSteepLeft(localizable);
            default:
                return null;
        }
    }
}
