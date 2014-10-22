/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.pathfinding;

import java.util.ArrayList;
import java.util.Collection;

import com.b3dgs.lionengine.UtilMath;
import com.b3dgs.lionengine.game.CoordTile;
import com.b3dgs.lionengine.game.pathfinding.heuristic.HeuristicClosest;
import com.b3dgs.lionengine.game.pathfinding.map.MapTilePath;
import com.b3dgs.lionengine.game.pathfinding.map.TilePath;

/**
 * A path finder implementation that uses the AStar heuristic based algorithm to determine a path.
 * 
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
final class PathFinderImpl
        implements PathFinder
{
    /** Closed list. */
    private final Collection<Node> closed = new ArrayList<>(8);
    /** Open list. */
    private final SortedList<Node> open = new SortedList<>();
    /** Map reference. */
    private final MapTilePath<? extends TilePath> map;
    /** Max distance to search. */
    private final int maxSearchDistance;
    /** Nodes array. */
    private final Node[][] nodes;
    /** Diagonal movement flag. */
    private final boolean allowDiagMovement;
    /** Heuristic used. */
    private final Heuristic heuristic;

    /**
     * Constructor.
     * 
     * @param map The map to be searched.
     * @param maxSearchDistance The maximum depth we'll search before giving up.
     * @param allowDiagMovement <code>true</code> if the search should try diagonal movement, <code>false</code> else.
     */
    PathFinderImpl(MapTilePath<? extends TilePath> map, int maxSearchDistance, boolean allowDiagMovement)
    {
        this(map, maxSearchDistance, allowDiagMovement, new HeuristicClosest());
    }

    /**
     * Constructor.
     * 
     * @param heuristic The heuristic used to determine the search order of the map.
     * @param map The map to be searched.
     * @param maxSearchDistance The maximum depth we'll search before giving up.
     * @param allowDiagMovement <code>true</code> if the search should try diagonal movement, <code>false</code> else.
     */
    PathFinderImpl(MapTilePath<? extends TilePath> map, int maxSearchDistance, boolean allowDiagMovement,
            Heuristic heuristic)
    {
        this.heuristic = heuristic;
        this.map = map;
        this.maxSearchDistance = maxSearchDistance;
        this.allowDiagMovement = allowDiagMovement;
        nodes = new Node[map.getHeightInTile()][map.getWidthInTile()];

        for (int y = 0; y < map.getHeightInTile(); y++)
        {
            for (int x = 0; x < map.getWidthInTile(); x++)
            {
                nodes[y][x] = new Node(x, y);
            }
        }
    }

    /**
     * Get the cost to move through a given location.
     * 
     * @param mover The entity that is being moved.
     * @param sx The x coordinate of the tile whose cost is being determined.
     * @param sy The y coordinate of the tile whose cost is being determined.
     * @param dx The x coordinate of the target location.
     * @param dy The y coordinate of the target location.
     * @return The cost of movement through the given tile.
     */
    public double getMovementCost(Pathfindable mover, int sx, int sy, int dx, int dy)
    {
        return map.getCost(mover, sx, sy, dx, dy);
    }

    /**
     * Get the heuristic cost for the given location. This determines in which order the locations are processed.
     * 
     * @param x The x coordinate of the tile whose cost is being determined
     * @param y The y coordinate of the tile whose cost is being determined
     * @param dx The x coordinate of the target location
     * @param dy The y coordinate of the target location
     * @return The heuristic cost assigned to the tile
     */
    public double getHeuristicCost(int x, int y, int dx, int dy)
    {
        return heuristic.getCost(x, y, dx, dy);
    }

    /**
     * Check if a given location is valid for the supplied mover.
     * 
     * @param mover The mover that would hold a given location.
     * @param sx The starting x coordinate.
     * @param sy The starting y coordinate.
     * @param x The x coordinate of the location to check.
     * @param y The y coordinate of the location to check.
     * @param ignoreRef The ignore map reference array checking.
     * @return <code>true</code> if the location is valid for the given mover, <code>false</code> else.
     */
    protected boolean isValidLocation(Pathfindable mover, int sx, int sy, int x, int y, boolean ignoreRef)
    {
        boolean invalid = x < 0 || y < 0 || x >= map.getWidthInTile() || y >= map.getHeightInTile();

        if (!invalid && (sx != x || sy != y))
        {
            invalid = map.isBlocked(mover, x, y, ignoreRef);
        }

        return !invalid;
    }

    /**
     * Get the first element from the open list. This is the next one to be searched.
     * 
     * @return The first element in the open list.
     */
    private Node getFirstInOpen()
    {
        return open.first();
    }

    /**
     * Add a node to the open list.
     * 
     * @param node The node to be added to the open list.
     */
    private void addToOpen(Node node)
    {
        open.add(node);
    }

    /**
     * Check if a node is in the open list.
     * 
     * @param node The node to check for.
     * @return <code>true</code> if the node given is in the open list, <code>false</code> else.
     */
    private boolean inOpenList(Node node)
    {
        return open.contains(node);
    }

    /**
     * Remove a node from the open list.
     * 
     * @param node The node to remove from the open list.
     */
    private void removeFromOpen(Node node)
    {
        open.remove(node);
    }

    /**
     * Add a node to the closed list.
     * 
     * @param node The node to add to the closed list.
     */
    private void addToClosed(Node node)
    {
        closed.add(node);
    }

    /**
     * Check if the node supplied is in the closed list.
     * 
     * @param node The node to search for.
     * @return <code>true</code> if the node specified is in the closed list, <code>false</code> else.
     */
    private boolean inClosedList(Node node)
    {
        return closed.contains(node);
    }

    /**
     * Remove a node from the closed list.
     * 
     * @param node The node to remove from the closed list.
     */
    private void removeFromClosed(Node node)
    {
        closed.remove(node);
    }

    /**
     * Update the open and closed list to find the path.
     * 
     * @param mover The entity that will be moving along the path.
     * @param sx The x coordinate of the start location.
     * @param sy The y coordinate of the start location.
     * @param dx The x coordinate of the destination location.
     * @param dy The y coordinate of the destination location.
     * @param ignoreRef The ignore map array reference checking (<code>true</code> to ignore references).
     * @param current The current node.
     * @param maxDepth The last max depth.
     * @return The next max depth.
     */
    private int updateList(Pathfindable mover, int sx, int sy, int dx, int dy, boolean ignoreRef, Node current,
            int maxDepth)
    {
        int nextDepth = maxDepth;
        for (int y = -1; y < 2; y++)
        {
            for (int x = -1; x < 2; x++)
            {
                if (x == 0 && y == 0)
                {
                    continue;
                }
                if (!allowDiagMovement)
                {
                    if (x != 0 && y != 0)
                    {
                        continue;
                    }
                }
                final int xp = x + current.getX();
                final int yp = y + current.getY();

                if (isValidLocation(mover, sx, sy, xp, yp, ignoreRef))
                {
                    nextDepth = updateNeighbour(mover, dx, dy, current, xp, yp, maxDepth);
                }
            }
        }
        return nextDepth;
    }

    /**
     * Update the current neighbor on search.
     * 
     * @param mover The entity that will be moving along the path.
     * @param dx The x coordinate of the destination location.
     * @param dy The y coordinate of the destination location.
     * @param current The current node.
     * @param xp The x coordinate of the destination location.
     * @param yp The y coordinate of the destination location.
     * @param maxDepth The last max depth.
     * @return The next max depth.
     */
    private int updateNeighbour(Pathfindable mover, int dx, int dy, Node current, int xp, int yp, int maxDepth)
    {
        int nextDepth = maxDepth;
        final double nextStepCost = current.getCost() + getMovementCost(mover, current.getX(), current.getY(), xp, yp);
        final Node neighbour = nodes[yp][xp];

        if (nextStepCost < neighbour.getCost())
        {
            if (inOpenList(neighbour))
            {
                removeFromOpen(neighbour);
            }
            if (inClosedList(neighbour))
            {
                removeFromClosed(neighbour);
            }
        }
        if (!inOpenList(neighbour) && !inClosedList(neighbour))
        {
            neighbour.setCost(nextStepCost);
            neighbour.setHeuristic(getHeuristicCost(xp, yp, dx, dy));
            nextDepth = Math.max(maxDepth, neighbour.setParent(current));
            addToOpen(neighbour);
        }
        return nextDepth;
    }

    /*
     * PathFinder
     */

    @Override
    public Path findPath(Pathfindable mover, int sx, int sy, int dx, int dy, boolean ignoreRef)
    {
        if (map.isBlocked(mover, dx, dy, false) && UtilMath.getDistance(sx, sy, dx, dy) <= 1)
        {
            return null;
        }
        if (map.isBlocked(mover, dx, dy, ignoreRef))
        {
            final CoordTile tile = map.getClosestAvailableTile(dx, dy, map.getHeightInTile(), sx, sy);
            if (tile == null)
            {
                return null;
            }
            return findPath(mover, sx, sy, tile.getX(), tile.getY(), ignoreRef);
        }
        nodes[sy][sx].setCost(0);
        nodes[sy][sx].setDepth(0);
        closed.clear();
        open.clear();
        open.add(nodes[sy][sx]);
        nodes[dy][dx].setParent(null);

        int maxDepth = 0;
        while (maxDepth < maxSearchDistance && open.size() != 0)
        {
            final Node current = getFirstInOpen();
            if (current == nodes[dy][dx])
            {
                break;
            }
            removeFromOpen(current);
            addToClosed(current);
            maxDepth = updateList(mover, sx, sy, dx, dy, ignoreRef, current, maxDepth);
        }
        if (nodes[dy][dx].getParent() == null)
        {
            return null;
        }
        final Path path = new Path();
        Node target = nodes[dy][dx];

        while (target != nodes[sy][sx])
        {
            path.prependStep(target.getX(), target.getY());
            target = target.getParent();
        }
        path.prependStep(sx, sy);

        return path;
    }
}
