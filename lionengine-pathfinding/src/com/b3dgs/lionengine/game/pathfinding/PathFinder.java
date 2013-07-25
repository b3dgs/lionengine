package com.b3dgs.lionengine.game.pathfinding;

/**
 * A description of an implementation that can find a path from one location on a tile map to another based on
 * information provided by that tile map.
 */
public interface PathFinder
{
    /**
     * Find a path from the starting location provided (sx, sy) to the destination location (dx, dy) avoiding blockages
     * and attempting to honour costs provided by the tile map.
     * 
     * @param mover The entity that will be moving along the path.
     * @param sx The x coordinate of the start location.
     * @param sy The y coordinate of the start location.
     * @param dx The x coordinate of the destination location.
     * @param dy The y coordinate of the destination location.
     * @param ignoreRef The ignore map array reference checking (<code>true</code> to ignore references).
     * @return The path found from start to end, or null if no path can be found.
     */
    Path findPath(Pathfindable mover, int sx, int sy, int dx, int dy, boolean ignoreRef);
}
