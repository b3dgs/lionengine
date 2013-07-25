package com.b3dgs.lionengine.game.rts.ability.mover;

import com.b3dgs.lionengine.game.Orientation;
import com.b3dgs.lionengine.game.Tiled;
import com.b3dgs.lionengine.game.pathfinding.Pathfindable;

/**
 * Represents the ability of moving on a PathBasedMap. It includes pathfinding handling.
 */
public interface MoverServices
        extends Pathfindable
{
    /**
     * Get the current orientation when moving.
     * 
     * @return The orientation movement.
     */
    Orientation getMovementOrientation();

    /**
     * Assign a specified location; will move automatically until reach it.
     * 
     * @param tiled The tiled to reach
     */
    void setDestination(Tiled tiled);

    /**
     * Adjust orientation to face to specified tile.
     * 
     * @param tx The horizontal tile to face.
     * @param ty The vertical tile to face.
     */
    void pointTo(int tx, int ty);

    /**
     * Adjust orientation to face to specified entity.
     * 
     * @param tiled The tiled to face to.
     */
    void pointTo(Tiled tiled);
}
