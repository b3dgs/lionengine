package com.b3dgs.lionengine.example.d_rts.d_ability;

import com.b3dgs.lionengine.game.rts.ability.producer.Producible;

/**
 * Producible entity implementation.
 */
public final class ProducibleEntity
        extends Producible<TypeEntity, ProductionCost>
{
    /**
     * Constructor
     * 
     * @param id The entity id.
     * @param cost The production cost.
     * @param tw The production width.
     * @param th The production height.
     */
    ProducibleEntity(TypeEntity id, ProductionCost cost, int tw, int th)
    {
        super(id, cost, tw, th);
    }
}
