package com.b3dgs.lionengine.example.c_platform.e_lionheart.map;

import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Tile slide implementation.
 */
public final class TileSlide
        extends Tile
{
    /**
     * @see Tile#Tile(int, int, Integer, int, TileCollision)
     */
    public TileSlide(int width, int height, Integer pattern, int number, TileCollision collision)
    {
        super(width, height, pattern, number, collision);
    }

    /**
     * Get the slide collision.
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

    /*
     * Tile
     */

    @Override
    public Double getCollisionY(Localizable localizable)
    {
        final double x = localizable.getLocationX() - getX() - getWidth();
        final TileCollision c = getCollision();
        switch (c)
        {
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
                if (x > -halfTileHeight)
                {
                    return getSlide(c, localizable, halfTileHeight);
                }
                return getGround(localizable, 0);

            default:
                return null;
        }
    }
}
