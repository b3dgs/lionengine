package com.b3dgs.lionengine.game.rts.ability.producer;

import com.b3dgs.lionengine.game.rts.EntityRts;

/**
 * List of event linked to the production.
 * 
 * @param <T> The entity enum type used.
 * @param <C> The cost type used.
 * @param <P> The producible type used.
 * @param <E> The entity type used.
 */
public interface ProducerListener<T extends Enum<T>, C extends ProductionCostRts, P extends Producible<T, C>, E extends EntityRts>
{
    /**
     * Notify listener that current element can not be produced.
     * 
     * @param producible The element that would have been under production.
     */
    void notifyCanNotProduce(P producible);

    /**
     * Notify listener that production is starting for this element.
     * 
     * @param producible The element going to be produced
     * @param entity The entity instance from element.
     */
    void notifyStartProduction(P producible, E entity);

    /**
     * Notify listener that this element is currently under production.
     * 
     * @param producible The element under production.
     * @param entity The entity instance from element.
     */
    void notifyProducing(P producible, E entity);

    /**
     * Notify listener that this element has been produced.
     * 
     * @param producible The element produced.
     * @param entity The entity instance from element.
     */
    void notifyProduced(P producible, E entity);
}
