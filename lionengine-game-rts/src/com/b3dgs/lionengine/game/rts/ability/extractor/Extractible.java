package com.b3dgs.lionengine.game.rts.ability.extractor;

import com.b3dgs.lionengine.game.Tiled;

/**
 * Represents an entity that can be extractible, such as a Gold Mine.
 * 
 * @param <R> The resource enum type used.
 */
public interface Extractible<R extends Enum<R>>
        extends Tiled
{
    /**
     * Extract the specified quantity if possible.
     * 
     * @param quantity The quantity to extract.
     * @return The extracted quantity.
     */
    int extractResource(int quantity);

    /**
     * Get the current resource quantity.
     * 
     * @return The current resource quantity.
     */
    int getResourceQuantity();

    /**
     * Get the resource type that can be extracted.
     * 
     * @return The resource type.
     */
    R getResourceType();
}
