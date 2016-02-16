/*
 * Copyright (C) 2013-2016 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
import com.b3dgs.lionengine.game.map.MapTile;
import com.b3dgs.lionengine.game.tile.Tile;

/**
 * A path finder implementation that uses the AStar heuristic based algorithm to determine a path.
 */
final class PathFinderImpl implements PathFinder
{
    /** Closed list. */
    private final Collection<Node> closed = new ArrayList<Node>(8);
    /** Open list. */
    private final SortedList<Node> open = new SortedList<Node>();
    /** Map reference. */
    private final MapTile map;
    /** Map path reference. */
    private final MapTilePath mapPath;
    /** Max distance to search. */
    private final int maxSearchDistance;
    /** Nodes array. */
    private final Node[][] nodes;
    /** Heuristic used. */
    private final Heuristic heuristic;

    /**
     * Internal constructor.
     * 
     * @param map The map to be searched. Must have the {@link MapTilePath} feature.
     * @param maxSearchDistance The maximum depth we'll search before giving up.
     * @param heuristic The heuristic used to determine the search order of the map.
     */
    PathFinderImpl(MapTile map, int maxSearchDistance, Heuristic heuristic)
    {
        this.heuristic = heuristic;
        this.map = map;
        this.maxSearchDistance = maxSearchDistance;
        mapPath = map.getFeature(MapTilePath.class);
        nodes = new Node[map.getInTileHeight()][map.getInTileWidth()];

        for (int ty = 0; ty < map.getInTileHeight(); ty++)
        {
            for (int tx = 0; tx < map.getInTileWidth(); tx++)
            {
                nodes[ty][tx] = new Node(tx, ty);
            }
        }
    }

    /**
     * Get the cost to move through a given location.
     * 
     * @param pathfindable The object that is being moved.
     * @param tx The horizontal tile index.
     * @param ty The vertical tile index.
     * @return The cost of movement through the given tile.
     */
    public double getMovementCost(Pathfindable pathfindable, int tx, int ty)
    {
        return mapPath.getCost(pathfindable, tx, ty);
    }

    /**
     * Get the heuristic cost for the given location. This determines in which order the locations are processed.
     * 
     * @param stx The x coordinate of the tile whose cost is being determined
     * @param sty The y coordinate of the tile whose cost is being determined
     * @param dtx The x coordinate of the target location
     * @param dty The y coordinate of the target location
     * @return The heuristic cost assigned to the tile
     */
    public double getHeuristicCost(int stx, int sty, int dtx, int dty)
    {
        return heuristic.getCost(stx, sty, dtx, dty);
    }

    /**
     * Check if a given location is valid for the supplied mover.
     * 
     * @param mover The mover that would hold a given location.
     * @param stx The starting x coordinate.
     * @param sty The starting y coordinate.
     * @param dtx The x coordinate of the location to check.
     * @param dty The y coordinate of the location to check.
     * @param ignoreRef The ignore map reference array checking.
     * @return <code>true</code> if the location is valid for the given mover, <code>false</code> else.
     */
    protected boolean isValidLocation(Pathfindable mover, int stx, int sty, int dtx, int dty, boolean ignoreRef)
    {
        boolean invalid = dtx < 0 || dty < 0 || dtx >= map.getInTileWidth() || dty >= map.getInTileHeight();

        if (!invalid && (stx != dtx || sty != dty))
        {
            invalid = mapPath.isBlocked(mover, dtx, dty, ignoreRef);
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
     * @param stx The x coordinate of the start location.
     * @param sty The y coordinate of the start location.
     * @param dtx The x coordinate of the destination location.
     * @param dty The y coordinate of the destination location.
     * @param ignoreRef The ignore map array reference checking (<code>true</code> to ignore references).
     * @param current The current node.
     * @param maxDepth The last max depth.
     * @return The next max depth.
     */
    private int updateList(Pathfindable mover,
                           int stx,
                           int sty,
                           int dtx,
                           int dty,
                           boolean ignoreRef,
                           Node current,
                           int maxDepth)
    {
        int depth = maxDepth;
        final Tile tile = map.getTile(current.getX(), current.getY());
        final TilePath tilePath = tile.getFeature(TilePath.class);
        for (int y = -1; y < 2; y++)
        {
            for (int x = -1; x < 2; x++)
            {
                if (!(x == 0 && y == 0))
                {
                    depth = check(tilePath, depth, x, y, mover, stx, sty, dtx, dty, ignoreRef, current, maxDepth);
                }
            }
        }
        return depth;
    }

    /**
     * Update the open and closed list to find the path.
     * 
     * @param tilePath The current tile.
     * @param nextDepth The next depth value.
     * @param x The current horizontal movement.
     * @param y The current vertical movement.
     * @param mover The entity that will be moving along the path.
     * @param stx The x coordinate of the start location.
     * @param sty The y coordinate of the start location.
     * @param dtx The x coordinate of the destination location.
     * @param dty The y coordinate of the destination location.
     * @param ignoreRef The ignore map array reference checking (<code>true</code> to ignore references).
     * @param current The current node.
     * @param maxDepth The last max depth.
     * @return The next max depth.
     */
    private int check(TilePath tilePath,
                      int nextDepth,
                      int x,
                      int y,
                      Pathfindable mover,
                      int stx,
                      int sty,
                      int dtx,
                      int dty,
                      boolean ignoreRef,
                      Node current,
                      int maxDepth)
    {
        final MovementTile movement = MovementTile.from(x, y);
        if (mover.isMovementAllowed(tilePath.getCategory(), movement))
        {
            final int xp = x + current.getX();
            final int yp = y + current.getY();

            if (isValidLocation(mover, stx, sty, xp, yp, ignoreRef))
            {
                return updateNeighbour(mover, dtx, dty, current, xp, yp, maxDepth);
            }
        }
        return nextDepth;
    }

    /**
     * Update the current neighbor on search.
     * 
     * @param mover The entity that will be moving along the path.
     * @param dtx The x coordinate of the destination location.
     * @param dty The y coordinate of the destination location.
     * @param current The current node.
     * @param xp The x coordinate of the destination location.
     * @param yp The y coordinate of the destination location.
     * @param maxDepth The last max depth.
     * @return The next max depth.
     */
    private int updateNeighbour(Pathfindable mover, int dtx, int dty, Node current, int xp, int yp, int maxDepth)
    {
        int nextDepth = maxDepth;
        final double nextStepCost = current.getCost() + getMovementCost(mover, current.getX(), current.getY());
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
            neighbour.setHeuristic(getHeuristicCost(xp, yp, dtx, dty));
            nextDepth = Math.max(maxDepth, neighbour.setParent(current));
            addToOpen(neighbour);
        }
        return nextDepth;
    }

    /*
     * PathFinder
     */

    @Override
    public Path findPath(Pathfindable mover, int dtx, int dty, boolean ignoreRef)
    {
        final int stx = mover.getInTileX();
        final int sty = mover.getInTileY();

        if (mapPath.isBlocked(mover, dtx, dty, false) && UtilMath.getDistance(stx, sty, dtx, dty) <= 1)
        {
            return null;
        }
        if (mapPath.isBlocked(mover, dtx, dty, ignoreRef))
        {
            final CoordTile tile = mapPath.getClosestAvailableTile(mover, dtx, dty, stx, sty, map.getInTileRadius());
            if (tile == null)
            {
                return null;
            }
            return findPath(mover, tile.getX(), tile.getY(), ignoreRef);
        }

        nodes[sty][stx].setCost(0);
        nodes[sty][stx].setDepth(0);
        closed.clear();
        open.clear();
        open.add(nodes[sty][stx]);
        nodes[dty][dtx].setParent(null);

        int maxDepth = 0;
        while (maxDepth < maxSearchDistance && open.size() != 0)
        {
            final Node current = getFirstInOpen();
            if (current == nodes[dty][dtx])
            {
                break;
            }
            removeFromOpen(current);
            addToClosed(current);
            maxDepth = updateList(mover, stx, sty, dtx, dty, ignoreRef, current, maxDepth);
        }
        if (nodes[dty][dtx].getParent() == null)
        {
            return null;
        }
        final Path path = new Path();
        Node target = nodes[dty][dtx];

        while (target != nodes[sty][stx])
        {
            path.prependStep(target.getX(), target.getY());
            target = target.getParent();
        }
        path.prependStep(stx, sty);

        return path;
    }
}
