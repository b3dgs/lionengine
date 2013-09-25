package com.b3dgs.lionengine.example.tilecollision;

import com.b3dgs.lionengine.Graphic;
import com.b3dgs.lionengine.game.platform.CameraPlatform;
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

    /**
     * Render the tile collision.
     * 
     * @param g The graphic output.
     * @param camera The camera reference.
     */
    public void renderCollision(Graphic g, CameraPlatform camera)
    {
        final int x = camera.getViewpointX(getX());
        final int y = camera.getViewpointY(getY() + getHeight());
        g.drawRect(x, y, getWidth(), getHeight(), false);
    }

    /*
     * TilePlatform
     */

    @Override
    public Double getCollisionX(Localizable localizable)
    {
        // From left
        final int left = getX();
        final int half = getWidth() / 2;
        if (localizable.getLocationOldX() <= left + half && localizable.getLocationX() >= left)
        {
            return Double.valueOf(left);
        }
        // From right
        final int right = getX() + getWidth() - 1;
        if (localizable.getLocationX() <= right && localizable.getLocationOldX() >= right - half)
        {
            return Double.valueOf(right);
        }
        return null;
    }

    @Override
    public Double getCollisionY(Localizable localizable)
    {
        // From top
        final int top = getY() + getHeight() - 1;
        if (localizable.getLocationOldY() >= top && localizable.getLocationY() <= top)
        {
            return Double.valueOf(top);
        }
        return null;
    }
}
