package com.b3dgs.lionengine.game.pathfinding;

/**
 * Heuristic interface. Define a function that is used to perform the cost of a path.
 */
public interface Heuristic
{
    /**
     * This controls the order in which tiles are searched while attempting to find a path to the target location. The
     * lower the cost the more likely the tile will be searched.
     * 
     * @param sx The x coordinate of the tile being evaluated.
     * @param sy The y coordinate of the tile being evaluated.
     * @param dx The x coordinate of the target location.
     * @param dy The y coordinate of the target location.
     * @return The cost associated with the given tile.
     */
    double getCost(int sx, int sy, int dx, int dy);
}
