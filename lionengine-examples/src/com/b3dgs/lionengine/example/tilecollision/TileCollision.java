package com.b3dgs.lionengine.example.tilecollision;

import java.util.ArrayList;
import java.util.List;

/**
 * List of tile collisions.
 */
enum TileCollision
{
    /** Ground collision. */
    GROUND,
    /** No collision. */
    NONE;

    /** Horizontal collisions list. */
    static final List<TileCollision> COLLISION = new ArrayList<>(1);

    /**
     * Static init.
     */
    static
    {
        TileCollision.COLLISION.add(TileCollision.GROUND);
    }
}
