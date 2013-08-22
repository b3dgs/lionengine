package com.b3dgs.lionengine.game.pathfinding;

/**
 * Node in the path.
 */
class Node
        implements Comparable<Node>
{
    /** Node parent. */
    private Node parent;
    /** Node location x. */
    private int x;
    /** Node location y. */
    private int y;
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
    Node(int x, int y)
    {
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
     * Set location x.
     * 
     * @param x The location x.
     */
    public void setX(int x)
    {
        this.x = x;
    }

    /**
     * Set location y.
     * 
     * @param y The location y.
     */
    public void setY(int y)
    {
        this.y = y;
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

        if (f < of)
        {
            return -1;
        }
        else if (f > of)
        {
            return 1;
        }
        else
        {
            return 0;
        }
    }

    @Override
    public boolean equals(Object object)
    {
        if (object instanceof Node)
        {
            final Node node = (Node) object;
            return x == node.x && y == node.y && depth == node.depth;
        }
        return false;
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
}
