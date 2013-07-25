package com.b3dgs.lionengine.game;

/**
 * Represents a coordinate in tile.
 */
public final class CoordTile
{
    /** Horizontal tile location. */
    private final int tx;
    /** Vertical tile location. */
    private final int ty;

    /**
     * Constructor.
     * 
     * @param tx The horizontal tile location.
     * @param ty The vertical tile location.
     */
    public CoordTile(int tx, int ty)
    {
        this.tx = tx;
        this.ty = ty;
    }

    /**
     * Get the horizontal tile location.
     * 
     * @return The horizontal tile location.
     */
    public int getX()
    {
        return tx;
    }

    /**
     * Get the vertical tile location.
     * 
     * @return The vertical tile location.
     */
    public int getY()
    {
        return ty;
    }
}
