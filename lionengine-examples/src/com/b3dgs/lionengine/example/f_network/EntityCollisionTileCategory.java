package com.b3dgs.lionengine.example.f_network;

import java.util.List;

import com.b3dgs.lionengine.game.platform.CollisionTileCategory;

/**
 * List of entity collision categories on tile.
 */
enum EntityCollisionTileCategory implements CollisionTileCategory<TileCollision>
{
    /** Default ground center collision. */
    GROUND_CENTER(TileCollision.COLLISION_VERTICAL),
    /** Ground leg left. */
    LEG_LEFT(TileCollision.COLLISION_VERTICAL),
    /** Ground leg right. */
    LEG_RIGHT(TileCollision.COLLISION_VERTICAL),
    /** Horizontal knee left. */
    KNEE_LEFT(TileCollision.COLLISION_HORIZONTAL),
    /** Horizontal knee right. */
    KNEE_RIGHT(TileCollision.COLLISION_HORIZONTAL);

    /** The collisions list. */
    private final List<TileCollision> collisions;

    /**
     * Constructor.
     * 
     * @param collisions The collisions list.
     */
    private EntityCollisionTileCategory(List<TileCollision> collisions)
    {
        this.collisions = collisions;
    }

    @Override
    public List<TileCollision> getCollisions()
    {
        return collisions;
    }
}
