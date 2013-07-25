package com.b3dgs.lionengine.game.pathfinding.heuristic;

import com.b3dgs.lionengine.game.pathfinding.Heuristic;

/**
 * A heuristic that uses the tile that is closest to the target as the next best tile.
 */
public class HeuristicClosest
        implements Heuristic
{
    @Override
    public double getCost(int sx, int sy, int dx, int dy)
    {
        final double x = dx - sx;
        final double y = dy - sy;
        return Math.sqrt(x * x + y * y);
    }
}
