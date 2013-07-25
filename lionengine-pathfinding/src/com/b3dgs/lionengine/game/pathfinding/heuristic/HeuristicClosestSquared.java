package com.b3dgs.lionengine.game.pathfinding.heuristic;

import com.b3dgs.lionengine.game.pathfinding.Heuristic;

/**
 * A heuristic that uses the tile that is closest to the target as the next best tile. In this case the square root is
 * removed and the distance squared is used instead
 */
public class HeuristicClosestSquared
        implements Heuristic
{
    @Override
    public double getCost(int sx, int sy, int dx, int dy)
    {
        final double x = dx - sx;
        final double y = dy - sy;
        return x * x + y * y;
    }
}
