package com.b3dgs.lionengine.game.rts;

/**
 * Entity activities listener.
 * 
 * @param <E> The entity type used.
 */
public interface EntityRtsListener<E extends EntityRts>
{
    /**
     * Called when the entity has moved.
     * 
     * @param entity The entity reference.
     */
    void entityMoved(E entity);
}
