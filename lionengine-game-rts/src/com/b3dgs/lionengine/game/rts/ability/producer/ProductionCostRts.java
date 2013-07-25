package com.b3dgs.lionengine.game.rts.ability.producer;

/**
 * Represents the cost of a production. Designed to contain its production steps, and cost in resource.
 */
public abstract class ProductionCostRts
{
    /** Needed production time. */
    private final int steps;

    /**
     * Create a new cost.
     * 
     * @param steps The production steps number.
     */
    public ProductionCostRts(int steps)
    {
        this.steps = steps;
    }

    /**
     * Get required steps for this production.
     * 
     * @return The production time.
     */
    public int getSteps()
    {
        return steps;
    }
}
