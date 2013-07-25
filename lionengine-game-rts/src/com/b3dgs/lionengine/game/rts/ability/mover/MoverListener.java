package com.b3dgs.lionengine.game.rts.ability.mover;

/**
 * Mover events listener.
 */
public interface MoverListener
{
    /**
     * Notify listener when mover starting to move.
     */
    void notifyStartMove();

    /**
     * Notify listener while mover is moving.
     */
    void notifyMoving();

    /**
     * Notify listener when mover has arrived.
     */
    void notifyArrived();
}
