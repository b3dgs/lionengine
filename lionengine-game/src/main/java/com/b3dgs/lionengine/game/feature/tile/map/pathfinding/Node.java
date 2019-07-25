/*
 * Copyright (C) 2013-2019 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
 * Node in the path.
 */
public final class Node implements Comparable<Node>
{
    /** Minimum to string length. */
    private static final int MIN_LENGTH = 45;

    /** Node location x. */
    private final int x;
    /** Node location y. */
    private final int y;
    /** Node parent. */
    private Node parent;
    /** Node depth. */
    private int depth;
    /** Node cost. */
    private double cost;
    /** Node heuristic value. */
    private double heuristic;

    /**
     * Constructor.
     * 
     * @param x The location x.
     * @param y The location y.
     */
    public Node(int x, int y)
    {
        super();

        this.x = x;
        this.y = y;
    }

    /**
     * Set the node parent.
     * 
     * @param parent The node parent.
     * @return depth The from parent.
     */
    public int setParent(Node parent)
    {
        if (parent != null)
        {
            depth = parent.getDepth() + 1;
        }
        this.parent = parent;
        return depth;
    }

    /**
     * Set cost.
     * 
     * @param cost The node cost.
     */
    public void setCost(double cost)
    {
        this.cost = cost;
    }

    /**
     * Set heuristic value.
     * 
     * @param heuristic The heuristic value.
     */
    public void setHeuristic(double heuristic)
    {
        this.heuristic = heuristic;
    }

    /**
     * Set node depth.
     * 
     * @param depth The node depth.
     */
    public void setDepth(int depth)
    {
        this.depth = depth;
    }

    /**
     * Get location x.
     * 
     * @return The location x.
     */
    public int getX()
    {
        return x;
    }

    /**
     * Get location y.
     * 
     * @return The location y.
     */
    public int getY()
    {
        return y;
    }

    /**
     * Get cost.
     * 
     * @return The cost.
     */
    public double getCost()
    {
        return cost;
    }

    /**
     * Get node parent reference.
     * 
     * @return The node parent reference.
     */
    public Node getParent()
    {
        return parent;
    }

    /**
     * Get heuristic value.
     * 
     * @return The heuristic value.
     */
    public double getHeuristic()
    {
        return heuristic;
    }

    /**
     * Get node depth.
     * 
     * @return The node depth.
     */
    public int getDepth()
    {
        return depth;
    }

    /*
     * Comparable
     */

    @Override
    public int compareTo(Node other)
    {
        final double f = getHeuristic() + getCost();
        final double of = other.getHeuristic() + other.getCost();

        return Double.compare(f, of);
    }

    @Override
    public boolean equals(Object object)
    {
        if (this == object)
        {
            return true;
        }
        if (object == null || object.getClass() != getClass())
        {
            return false;
        }
        final Node node = (Node) object;
        return x == node.x && y == node.y && depth == node.depth;
    }

    @Override
    public int hashCode()
    {
        int hash = 12;
        hash = hash * 17 + x;
        hash = hash * 31 + y;
        hash = hash * 14 + depth;
        return hash;
    }

    @Override
    public String toString()
    {
        return new StringBuilder(MIN_LENGTH).append(getClass().getSimpleName())
                                            .append(" [x=")
                                            .append(x)
                                            .append(", y=")
                                            .append(y)
                                            .append(", depth=")
                                            .append(depth)
                                            .append(", cost=")
                                            .append(cost)
                                            .append(", heuristic=")
                                            .append(heuristic)
                                            .append("]")
                                            .toString();
    }
}
