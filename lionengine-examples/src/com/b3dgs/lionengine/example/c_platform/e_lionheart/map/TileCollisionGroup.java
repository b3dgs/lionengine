package com.b3dgs.lionengine.example.c_platform.e_lionheart.map;

/**
 * List of collisions group types.
 */
public enum TileCollisionGroup
{
    /** No group. */
    NONE(0.0),
    /** Flat. */
    FLAT(1.0),
    /** Slope. */
    SLOPE(2.0),
    /** Slide. */
    SLIDE(3.0);

    /** Factor (vertical reducer compared to horizontal). */
    private double factor;

    /**
     * Constructor.
     * 
     * @param factor The factor value.
     */
    private TileCollisionGroup(double factor)
    {
        this.factor = factor;
    }

    /**
     * Get the factor value.
     * 
     * @return The factor value.
     */
    public double getFactor()
    {
        return factor;
    }
}
