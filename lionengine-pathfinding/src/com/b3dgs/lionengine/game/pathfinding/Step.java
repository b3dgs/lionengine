package com.b3dgs.lionengine.game.pathfinding;

/**
 * Path step.
 */
final class Step
{
    /** Step location x. */
    private final int x;
    /** Step location y. */
    private final int y;

    /**
     * Constructor.
     * 
     * @param x The location x.
     * @param y The location y.
     */
    Step(int x, int y)
    {
        this.x = x;
        this.y = y;
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

    @Override
    public int hashCode()
    {
        return x * y;
    }

    @Override
    public boolean equals(Object other)
    {
        if (other instanceof Step)
        {
            final Step o = (Step) other;
            return o.getX() == x && o.getY() == y;
        }
        return false;
    }
}
