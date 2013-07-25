package com.b3dgs.lionengine.example.d_rts.f_warcraft;

import com.b3dgs.lionengine.game.rts.ability.producer.ProductionCostRts;

/**
 * Production cost implementation.
 */
public final class ProductionCost
        extends ProductionCostRts
{
    /** The needed amount of gold. */
    private final int gold;
    /** The needed amount of wood. */
    private final int wood;

    /**
     * Create a new cost.
     * 
     * @param steps The production steps number.
     * @param gold The needed amount of gold.
     * @param wood The needed amount of wood.
     */
    ProductionCost(int steps, int gold, int wood)
    {
        super(steps);
        this.gold = gold;
        this.wood = wood;
    }

    /**
     * Get the cost in gold.
     * 
     * @return The cost in gold.
     */
    public int getGold()
    {
        return gold;
    }

    /**
     * Get the cost in wood.
     * 
     * @return The cost in wood.
     */
    public int getWood()
    {
        return wood;
    }
}
