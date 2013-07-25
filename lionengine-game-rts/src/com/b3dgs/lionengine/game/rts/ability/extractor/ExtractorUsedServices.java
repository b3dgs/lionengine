package com.b3dgs.lionengine.game.rts.ability.extractor;

/**
 * List of services needed by the extractor.
 * 
 * @param <R> The resource enum type used.
 */
public interface ExtractorUsedServices<R extends Enum<R>>
        extends ExtractorListener<R>
{
    /**
     * Check if extractor can extract (called while going to resources location).
     * <p>
     * For example:
     * <ul>
     * <li>The owner is close enough to the resource location</li>
     * </ul>
     * </p>
     * 
     * @return The extraction check condition result.
     */
    boolean canExtract();

    /**
     * Check if extractor can bring back its extraction (called while bring back resources).
     * 
     * @return The carry condition result.
     */
    boolean canCarry();

    /**
     * Get the extraction capacity in unit (the maximum number of unit extractible per extraction).
     * 
     * @return The extraction capacity.
     */
    int getExtractionCapacity();

    /**
     * Get the extraction speed.
     * 
     * @return The extraction speed.
     */
    int getExtractionSpeed();

    /**
     * Get the drop off speed.
     * 
     * @return The drop off speed.
     */
    int getDropOffSpeed();
}
