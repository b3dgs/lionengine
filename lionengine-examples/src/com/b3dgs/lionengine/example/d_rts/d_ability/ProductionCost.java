package com.b3dgs.lionengine.example.d_rts.d_ability;

import com.b3dgs.lionengine.game.rts.ability.producer.ProductionCostRts;

/**
 * The production cost representation.
 */
public final class ProductionCost
        extends ProductionCostRts
{
    /** The gold cost. */
    private final int gold;
    /** The wood cost. */
    private final int wood;

    /**
     * Constructor.
     * 
     * @param steps The steps number.
     * @param gold The gold number.
     * @param wood The wood number.
     */
    ProductionCost(int steps, int gold, int wood)
    {
        super(steps);
        this.gold = gold;
        this.wood = wood;
    }

    /**
     * Get required gold for this production.
     * 
     * @return The production gold cost.
     */
    public int getGold()
    {
        return gold;
    }

    /**
     * Get required wood for this production.
     * 
     * @return The production wood cost.
     */
    public int getWood()
    {
        return wood;
    }
}
