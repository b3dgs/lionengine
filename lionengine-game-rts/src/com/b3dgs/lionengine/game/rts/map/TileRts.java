package com.b3dgs.lionengine.game.rts.map;

import com.b3dgs.lionengine.game.pathfinding.map.TilePath;

/**
 * Representation of a default tile, used for pathfinding.
 * 
 * @param <C> The collision enum type used.
 * @param <R> The resource enum type used.
 */
public abstract class TileRts<C extends Enum<C>, R extends Enum<R>>
        extends TilePath<C>
{
    /** Resource type (<code>null</code> if none). */
    private R resourceType;

    /**
     * Create a new blank path tile.
     * 
     * @param width The tile width.
     * @param height The tile height.
     * @param pattern The tile pattern.
     * @param number The tile number.
     * @param collision The tile collision.
     */
    public TileRts(int width, int height, Integer pattern, int number, C collision)
    {
        super(width, height, pattern, number, collision);
        checkResourceType(collision);
    }

    /**
     * Check resource type from collision.
     * 
     * @param collision The tile collision.
     */
    public abstract void checkResourceType(C collision);

    /**
     * Check if tile has resources.
     * 
     * @return <code>true</code> if has resources, <code>false</code> else.
     */
    public abstract boolean hasResources();

    /**
     * Set the resource type.
     * 
     * @param type The resource type.
     */
    public void setResourceType(R type)
    {
        resourceType = type;
    }

    /**
     * Get the resource type.
     * 
     * @return The resource type (<code>null</code> if none).
     */
    public R getResourceType()
    {
        return resourceType;
    }
}
