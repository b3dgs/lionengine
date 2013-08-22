package com.b3dgs.lionengine.game.rts.ability.producer;

import com.b3dgs.lionengine.game.Tiled;

/**
 * Represents a producible entity.
 * 
 * @param <T> The entity enum type used.
 * @param <C> The cost type used.
 */
public class Producible<T extends Enum<T>, C extends ProductionCostRts>
        implements Tiled
{
    /** Entity id. */
    private final T id;
    /** Production cost. */
    private final C cost;
    /** Production width. */
    private final int tw;
    /** Production height. */
    private final int th;
    /** Production location x. */
    private int tx;
    /** Production location y. */
    private int ty;

    /**
     * Create a productible structure.
     * 
     * @param id The entity id.
     * @param cost The production cost.
     * @param tw The production width.
     * @param th The production height.
     */
    public Producible(T id, C cost, int tw, int th)
    {
        this.id = id;
        this.cost = cost;
        this.tw = tw;
        this.th = th;
    }

    /**
     * Set the production location.
     * 
     * @param tx The horizontal location.
     * @param ty The vertical location.
     */
    public void setLocation(int tx, int ty)
    {
        this.tx = tx;
        this.ty = ty;
    }

    /**
     * Get the id.
     * 
     * @return The id.
     */
    public T getId()
    {
        return id;
    }

    /**
     * Get the cost.
     * 
     * @return The cost.
     */
    public C getCost()
    {
        return cost;
    }

    /*
     * Tiled
     */

    @Override
    public int getLocationInTileX()
    {
        return tx;
    }

    @Override
    public int getLocationInTileY()
    {
        return ty;
    }

    @Override
    public int getWidthInTile()
    {
        return tw;
    }

    @Override
    public int getHeightInTile()
    {
        return th;
    }
}
