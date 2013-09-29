package com.b3dgs.lionengine.example.e_shmup.c_tyrian.background;

import com.b3dgs.lionengine.utility.UtilityRandom;

/**
 * Star implementation.
 */
final class Star
{
    /** Horizontal vector. */
    private final double vx;
    /** Vertical vector. */
    private final double vy;
    /** Star id. */
    private final int id;
    /** Horizontal location. */
    private double x;
    /** Vertical location. */
    private double y;

    /**
     * Constructor.
     * 
     * @param x The location x.
     * @param y The location y.
     * @param vx The horizontal vector.
     * @param vy The vertical vector.
     * @param id The id.
     */
    Star(double x, double y, double vx, double vy, int id)
    {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.id = id;
    }

    /**
     * Update the star.
     * 
     * @param extrp The extrapolation value.
     */
    public void update(double extrp)
    {
        x += vx * extrp;
        y += vy * extrp;
        if (y > 210)
        {
            y = -10;
            x = UtilityRandom.getRandomInteger(-20, 340);
        }
    }

    /**
     * Get the id.
     * 
     * @return The id.
     */
    public int getId()
    {
        return id;
    }

    /**
     * Get the x location.
     * 
     * @return The x location.
     */
    public double getX()
    {
        return x;
    }

    /**
     * Get the y location.
     * 
     * @return The y location.
     */
    public double getY()
    {
        return y;
    }
}
