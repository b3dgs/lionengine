package com.b3dgs.lionengine.game.purview;

import com.b3dgs.lionengine.drawable.SpriteAnimated;

/**
 * Represents a surface that can be rastered.
 */
public interface Rasterable
{
    /** Maximum rasters. */
    public static final int MAX_RASTERS = 15;
    /** Maximum rasters R. */
    public static final int MAX_RASTERS_R = Rasterable.MAX_RASTERS * 2;
    /** Maximum rasters M. */
    public static final int MAX_RASTERS_M = Rasterable.MAX_RASTERS - 1;

    /**
     * Get raster index from location.
     * 
     * @param y The current y location.
     * @return The raster index based on vertical location.
     */
    int getRasterIndex(double y);

    /**
     * Get raster animation from raster index.
     * 
     * @param rasterIndex The raster index (>= 0).
     * @return The raster animated sprite.
     */
    SpriteAnimated getRasterAnim(int rasterIndex);

    /**
     * Check if raster if activated.
     * 
     * @return <code>true</code> if rastered, <code>false</code> else.
     */
    boolean isRastered();
}
