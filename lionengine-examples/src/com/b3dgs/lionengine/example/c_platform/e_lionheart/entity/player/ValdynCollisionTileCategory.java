package com.b3dgs.lionengine.example.c_platform.e_lionheart.entity.player;

import java.util.List;

import com.b3dgs.lionengine.example.c_platform.e_lionheart.map.TileCollision;
import com.b3dgs.lionengine.game.platform.CollisionTileCategory;

/**
 * List of valdyn collision categories on tile.
 */
public enum ValdynCollisionTileCategory implements CollisionTileCategory<TileCollision>
{
    /** Ground leg left. */
    LEG_LEFT(TileCollision.COLLISION_VERTICAL),
    /** Ground leg right. */
    LEG_RIGHT(TileCollision.COLLISION_VERTICAL),
    /** Horizontal knee left. */
    KNEE_LEFT(TileCollision.COLLISION_HORIZONTAL),
    /** Horizontal knee right. */
    KNEE_RIGHT(TileCollision.COLLISION_HORIZONTAL),
    /** Hand liana steep. */
    HAND_LIANA_STEEP(TileCollision.COLLISION_LIANA_STEEP),
    /** Hand liana leaning. */
    HAND_LIANA_LEANING(TileCollision.COLLISION_LIANA_LEANING);

    /** The collisions list. */
    private final List<TileCollision> collisions;

    /**
     * Constructor.
     * 
     * @param collisions The collisions list.
     */
    private ValdynCollisionTileCategory(List<TileCollision> collisions)
    {
        this.collisions = collisions;
    }

    @Override
    public List<TileCollision> getCollisions()
    {
        return collisions;
    }
}
