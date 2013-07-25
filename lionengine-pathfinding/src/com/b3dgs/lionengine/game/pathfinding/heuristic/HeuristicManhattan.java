package com.b3dgs.lionengine.game.pathfinding.heuristic;

import com.b3dgs.lionengine.game.pathfinding.Heuristic;

/**
 * A heuristic that drives the search based on the Manhattan distance between the current location and the target.
 */
public class HeuristicManhattan
        implements Heuristic
{
    /** Minimum cost value. */
    private final int minimumCost;

    /**
     * Constructor.
     * 
     * @param minimumCost The minimum cost value.
     */
    public HeuristicManhattan(int minimumCost)
    {
        this.minimumCost = minimumCost;
    }

    @Override
    public double getCost(int sx, int sy, int dx, int dy)
    {
        return minimumCost * (Math.abs(sx - dx) + Math.abs(sy - dy));
    }
}
