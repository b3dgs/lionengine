package com.b3dgs.lionengine.example.d_rts.f_warcraft;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.example.d_rts.f_warcraft.type.TypeResource;
import com.b3dgs.lionengine.game.Alterable;
import com.b3dgs.lionengine.game.rts.PlayerRts;

/**
 * Player implementation.
 */
public final class Player
        extends PlayerRts
{
    /** Gold resource. */
    private final ResourceProgressive gold;
    /** Wood resource. */
    private final ResourceProgressive wood;
    /** Population capacity. */
    private final Alterable population;

    /**
     * Constructor.
     */
    Player()
    {
        super();
        gold = new ResourceProgressive(1000);
        wood = new ResourceProgressive(1000);
        population = new Alterable(0, true);
    }

    /**
     * Update progressive calculation.
     * 
     * @param extrp The extrapolation value.
     */
    public void update(double extrp)
    {
        gold.update(extrp, 5.0);
        wood.update(extrp, 5.0);
    }

    /**
     * Increase the resource depending of its type.
     * 
     * @param type The resource type.
     * @param quantity The resource quantity.
     */
    public void increase(TypeResource type, int quantity)
    {
        switch (type)
        {
            case GOLD:
                gold.increase(quantity);
                break;
            case WOOD:
                wood.increase(quantity);
                break;
            default:
                throw new LionEngineException("Unknown resource !");
        }
    }

    /**
     * Spend the resources.
     * 
     * @param cost The cost to spend.
     */
    public void spend(ProductionCost cost)
    {
        gold.decrease(cost.getGold());
        wood.decrease(cost.getWood());
    }

    /**
     * Check if the player can spend the specified amount of resource for the producible.
     * 
     * @param cost The cost to check.
     * @return <code>true</code> if can spend, <code>false</code> else.
     */
    public boolean canSpend(ProductionCost cost)
    {

        return gold.isEnough(cost.getGold()) && wood.isEnough(cost.getWood());
    }

    /**
     * Change the population value with an offset.
     * 
     * @param value The offset value.
     */
    public void changePopulation(int value)
    {
        population.increase(value);
    }

    /**
     * Change the population capacity with an offset.
     * 
     * @param value The offset value.
     */
    public void changePopulationCapacity(int value)
    {
        population.setMax(population.getMax() + value);
    }

    /**
     * Get the current population.
     * 
     * @return The current population.
     */
    public int getPopulation()
    {
        return population.getCurrent();
    }

    /**
     * Get population capacity.
     * 
     * @return The population capacity.
     */
    public int getPopulationCapacity()
    {
        return population.getMax();
    }

    /**
     * Check population capacity.
     * 
     * @return <code>true</code> if pop full, <code>false</code> else.
     */
    public boolean isPopFull()
    {
        return population.isFull();
    }

    /**
     * Get the current amount of gold.
     * 
     * @return The current amount of gold.
     */
    public int getGold()
    {
        return gold.getCurrent();
    }

    /**
     * Get the current amount of wood.
     * 
     * @return The current amount of wood.
     */
    public int getWood()
    {
        return wood.getCurrent();
    }
}
