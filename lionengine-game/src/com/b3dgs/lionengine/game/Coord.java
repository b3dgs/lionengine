package com.b3dgs.lionengine.game;

/**
 * Represents a coordinate using double precision.
 */
public final class Coord
{
    /** Coordinate horizontal. */
    private double x;
    /** Coordinate vertical. */
    private double y;

    /**
     * Create a coordinate set to <code>(0.0, 0.0)</code> by default.
     */
    public Coord()
    {
        this(0.0, 0.0);
    }

    /**
     * Create a coordinate with a specified default location.
     * 
     * @param x The horizontal location.
     * @param y The vertical location.
     */
    public Coord(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Set the new coordinate.
     * 
     * @param x The new horizontal location.
     * @param y The new vertical location.
     */
    public void set(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    /**
     * Translate coordinate using specified values.
     * 
     * @param vx The horizontal force.
     * @param vy The vertical force.
     */
    public void translate(double vx, double vy)
    {
        x += vx;
        y += vy;
    }

    /**
     * Set the new horizontal location.
     * 
     * @param x The new horizontal location.
     */
    public void setX(double x)
    {
        this.x = x;
    }

    /**
     * Set the new vertical location.
     * 
     * @param y The new vertical location.
     */
    public void setY(double y)
    {
        this.y = y;
    }

    /**
     * Get the horizontal location.
     * 
     * @return The horizontal location.
     */
    public double getX()
    {
        return x;
    }

    /**
     * Get the vertical location.
     * 
     * @return The vertical location.
     */
    public double getY()
    {
        return y;
    }
}
