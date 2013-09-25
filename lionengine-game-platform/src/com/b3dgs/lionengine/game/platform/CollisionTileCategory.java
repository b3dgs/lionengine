package com.b3dgs.lionengine.game.platform;

import java.util.List;

/**
 * Collision tile category.
 * 
 * @param <C> The collision type used.
 */
public interface CollisionTileCategory<C extends Enum<C>>
{
    /**
     * Get the list of collisions to test.
     * 
     * @return The collisions list.
     */
    List<C> getCollisions();
}
