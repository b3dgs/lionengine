/*
 * Copyright (C) 2013-2023 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */
package com.b3dgs.lionengine.game.feature.tile.map.pathfinding;

/**
 * A heuristic that drives the search based on the Manhattan distance between the current location and the target.
 */
public final class HeuristicManhattan implements Heuristic
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
        super();

        this.minimumCost = minimumCost;
    }

    @Override
    public double getCost(int sx, int sy, int dx, int dy)
    {
        return minimumCost * (Math.abs(sx - (double) dx) + Math.abs(sy - (double) dy));
    }
}
