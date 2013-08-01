package com.b3dgs.lionengine.game.platform.map;

import com.b3dgs.lionengine.game.maptile.TileGame;
import com.b3dgs.lionengine.game.purview.Localizable;

/**
 * Specific tile for platform game.
 * 
 * @param <C> The collision type used.
 */
public abstract class TilePlatform<C extends Enum<C>>
        extends TileGame<C>
{
    /**
     * Create a new blank tile.
     * 
     * @param width The tile width.
     * @param height The tile height.
     */
    public TilePlatform(int width, int height)
    {
        super(width, height);
    }

    /**
     * Get the horizontal collision location between the tile and the localizable.
     * 
     * @param localizable The localizable object searching the collision.
     * @return The collision x (<code>null</code> if none).
     */
    public abstract Double getCollisionX(Localizable localizable);

    /**
     * Get the vertical collision location between the tile and the localizable.
     * 
     * @param localizable The localizable object searching the collision.
     * @return The collision y (<code>null</code> if none).
     */
    public abstract Double getCollisionY(Localizable localizable);

    /**
     * Check if there is a collision between the localizable and the tile.
     * 
     * @param localizable The localizable.
     * @return <code>true</code> if collide, <code>false</code> else.
     */
    public boolean hasCollision(Localizable localizable)
    {
        return getCollision() != null && (getCollisionX(localizable) != null || getCollisionY(localizable) != null);
    }
}
