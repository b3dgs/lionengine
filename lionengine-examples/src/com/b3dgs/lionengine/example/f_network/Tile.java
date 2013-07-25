package com.b3dgs.lionengine.example.f_network;

import com.b3dgs.lionengine.game.platform.map.TilePlatform;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Tile implementation, using TileModel.
 */
public class Tile
        extends TilePlatform<TileCollision>
{
    /**
     * Create a new blank tile.
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
        catch (final Exception e)
        {
            return TileCollision.NONE;
        }
    }

    /**
     * Get y collision location.
     * 
     * @param yOld The old y.
     * @param y The current y.
     * @param x The current x.
     * @return y The collision location.
     */
    public Integer getCollisionLocationY(double yOld, double y, double x)
    {
        switch (getCollision())
        {
            case GROUND:
                return applyCollisionVertical(yOld, y);
            case BLOCK:
                return applyCollisionVertical(yOld, y);
            case NONE:
                break;
            case WALL:
                break;
            default:
                break;
        }
        return null;
    }

    /**
     * Get x collision location.
     * 
     * @param xOld The old x.
     * @param x The current x.
     * @param y The current y.
     * @param offsetX The offset y.
     * @return The x collision location.
     */
    public Integer getCollisionLocationX(double xOld, double x, double y, double offsetX)
    {
        switch (getCollision())
        {
            case BLOCK:
                if (y < getY() + 16)
                {
                    return applyCollisionHorizontal(xOld, x, offsetX);
                }
                break;
            case WALL:
                return applyCollisionHorizontal(xOld, x, offsetX);
            case GROUND:
                break;
            case NONE:
                break;
            default:
                break;
        }
        return null;
    }

    /**
     * Define the vertical collision.
     * 
     * @param yOld The old y.
     * @param y The current y.
     * @return The vertical collision location.
     */
    private Integer applyCollisionVertical(double yOld, double y)
    {
        // Collision takes effect only on top of the tile
        if (yOld >= getY() + 16 && y <= getY() + 16)
        {
            return Integer.valueOf(getY() + 16);
        }
        return null;
    }

    /**
     * Define the horizontal collision.
     * 
     * @param xOld The old x.
     * @param x The current x.
     * @param offset The offset value.
     * @return The horizontal collision location.
     */
    private Integer applyCollisionHorizontal(double xOld, double x, double offset)
    {
        // Left part of the tile
        if (xOld < x && x > getX() - offset && x <= getX() + 16 - offset)
        {
            return Integer.valueOf(getX());
        }
        // Right part of the tile
        else if (xOld > x && x > getX() - offset && x <= getX() + 16 - offset)
        {
            return Integer.valueOf(getX() + 16);
        }
        else
        {
            return null;
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
        return null;
    }
}
