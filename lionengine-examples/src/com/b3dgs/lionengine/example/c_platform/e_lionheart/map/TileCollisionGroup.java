package com.b3dgs.lionengine.example.c_platform.e_lionheart.map;

/**
 * List of collisions group types.
 */
public enum TileCollisionGroup
{
    /** No group. */
    NONE(0.0),
    /** Flat. */
    FLAT(0.0),
    /** Slope. */
    SLOPE(0.5),
    /** Slide. */
    SLIDE(2.0),
    /** Liana. */
    LIANA_HORIZONTAL(1.0),
    /** Liana leaning. */
    LIANA_LEANING(0.5),
    /** Liana steep. */
    LIANA_STEEP(1.0);

    /** Factor (vertical factor compared to horizontal). */
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
