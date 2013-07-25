package com.b3dgs.lionengine.game.rts.ability.producer;

import com.b3dgs.lionengine.game.entity.FactoryEntityGame;
import com.b3dgs.lionengine.game.rts.EntityRts;

/**
 * List of services used by the producer.
 * 
 * @param <T> The entity enum type used.
 * @param <C> The cost type used.
 * @param <P> The producible type used.
 * @param <E> The entity type used.
 */
public interface ProducerUsedServices<T extends Enum<T>, C extends ProductionCostRts, P extends Producible<T, C>, E extends EntityRts>
        extends ProducerListener<T, C, P, E>
{
    /**
     * Condition to start production check (able to produce).
     * <p>
     * Example:
     * </p>
     * <ul>
     * <li>Did the player have enough resources ?</li>
     * <li>Is the owner alive ?</li>
     * </ul>
     * 
     * @param producible The producible to check.
     * @return <code>true</code> if able to produce, <code>false</code> else.
     */
    boolean canProduce(P producible);

    /**
     * Condition to make production start.
     * <p>
     * For example:
     * <ul>
     * <li>The owner has enough resources regarding the producible cost</li>
     * <li>The owner is close enough to the building location</li>
     * </ul>
     * </p>
     * 
     * @param producible The productible reference.
     * @return <code>true</code> if can start production, <code>false</code> else.
     */
    boolean canBeProduced(P producible);

    /**
     * Get entity to produce from its id. The common usage is to return a new entity instance by using the factory (
     * {@link FactoryEntityGame#createEntity(Enum)}.
     * 
     * @param id The entity id.
     * @return The entity reference.
     */
    E getEntityToProduce(T id);

    /**
     * Get the number of steps done per seconds (the production speed).
     * 
     * @return The number of steps per seconds.
     */
    int getStepsPerSecond();

    /**
     * Get the player id.
     * 
     * @return The player id.
     */
    int getPlayerId();
}
