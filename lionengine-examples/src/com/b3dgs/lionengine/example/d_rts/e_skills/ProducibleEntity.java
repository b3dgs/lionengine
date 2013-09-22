package com.b3dgs.lionengine.example.d_rts.e_skills;

import com.b3dgs.lionengine.game.rts.ability.producer.Producible;

/**
 * Producible entity implementation.
 */
public final class ProducibleEntity
        extends Producible<EntityType, ProductionCost>
{
    /**
     * Constructor.
     * 
     * @param id The entity id.
     * @param cost The production cost.
     * @param tw The production width.
     * @param th The production height.
     */
    ProducibleEntity(EntityType id, ProductionCost cost, int tw, int th)
    {
        super(id, cost, tw, th);
    }
}
