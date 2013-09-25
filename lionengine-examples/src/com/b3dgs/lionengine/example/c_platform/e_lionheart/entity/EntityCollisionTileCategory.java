package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity;

import java.util.List;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TileCollision;
import com.b3dgs.lionengine.game.platform.CollisionTileCategory;

/**
 * List of entity collision categories on tile.
 */
public enum EntityCollisionTileCategory implements CollisionTileCategory<TileCollision>
{
    /** Default ground center collision. */
    GROUND_CENTER(TileCollision.COLLISION_VERTICAL);

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
