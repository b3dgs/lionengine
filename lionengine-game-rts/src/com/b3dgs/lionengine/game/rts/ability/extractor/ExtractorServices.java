package com.b3dgs.lionengine.game.rts.ability.extractor;

import com.b3dgs.lionengine.game.Tiled;

/**
 * This interface represents the ability of resource extraction. It is also possible to specify work time.
 * 
 * @param <R> The resource enum type used.
 */
public interface ExtractorServices<R extends Enum<R>>
{
    /**
     * Update extraction (extract and go back).
     * 
     * @param extrp The extrapolation value.
     */
    void updateExtraction(double extrp);

    /**
     * Set the resources location in tile.
     * 
     * @param extractible The extractible entity.
     */
    void setResource(Extractible<R> extractible);

    /**
     * Set the resources location in tile.
     * 
     * @param type The resource type to extract.
     * @param tx The horizontal tile.
     * @param ty The vertical tile.
     * @param tw The width in tile.
     * @param th The height in tile.
     */
    void setResource(R type, int tx, int ty, int tw, int th);

    /**
     * Start extraction.
     */
    void startExtraction();

    /**
     * Stop any action related to extraction.
     */
    void stopExtraction();

    /**
     * Get the resource location in tile.
     * 
     * @return The resource location in tile.
     */
    Tiled getResourceLocation();

    /**
     * Get resource type.
     * 
     * @return The resource type.
     */
    R getResourceType();

    /**
     * Check if extractor is currently working on extraction.
     * 
     * @return <code>true</code> if extracting, <code>false</code> else.
     */
    boolean isExtracting();
}
