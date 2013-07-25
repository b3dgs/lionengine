package com.b3dgs.lionengine.game.rts.ability.producer;

import java.util.Iterator;

/**
 * Represents an ability of creating new entity.
 * 
 * @param <T> The entity enum type used.
 * @param <C> The cost type used.
 * @param <P> The producible type used.
 */
public interface ProducerServices<T extends Enum<T>, C extends ProductionCostRts, P extends Producible<T, C>>
{
    /**
     * Add an element to the production queue. It works as a FIFO (First In, First Out). Production will be stopped when
     * the list is empty. In this case, getProductionProgress() will return -1. Production list stores only entity name.
     * You have to return the corresponding instance by overriding this function: getUnitToProduce(Enum<?> id) from the
     * ProducerModel.
     * 
     * @param producible The element to produce.
     */
    void addToProductionQueue(P producible);

    /**
     * Update production routine.
     * 
     * @param extrp The extrapolation value.
     */
    void updateProduction(double extrp);

    /**
     * Skip current production.
     */
    void skipProduction();

    /**
     * Stop any production.
     */
    void stopProduction();

    /**
     * Get production progress. If it returns -1, it means that there are not any active production.
     * 
     * @return The progress value.
     */
    double getProductionProgress();

    /**
     * Get production progress percent. If it returns -1, it means that there are not any active production.
     * 
     * @return The percent of progress.
     */
    int getProductionProgressPercent();

    /**
     * Get name of current producing element.
     * 
     * @return The id of current producing element.
     */
    T getProducingElement();

    /**
     * Get production iterator.
     * 
     * @return The list of production.
     */
    Iterator<P> getProductionIterator();

    /**
     * Get size of production queue.
     * 
     * @return The number of remaining products.
     */
    int getQueueLength();

    /**
     * Return true if currently producing.
     * 
     * @return <code>true</code> if producing, <code>false</code> else.
     */
    boolean isProducing();
}
