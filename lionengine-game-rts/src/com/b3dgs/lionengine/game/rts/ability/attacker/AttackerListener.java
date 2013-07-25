package com.b3dgs.lionengine.game.rts.ability.attacker;

import com.b3dgs.lionengine.game.rts.EntityRts;

/**
 * List of events listened from an attacker.
 * 
 * @param <E> The entity type used.
 */
public interface AttackerListener<E extends EntityRts>
{
    /**
     * Notify while attacker is reaching target.
     * 
     * @param target The target to reach.
     */
    void notifyReachingTarget(E target);

    /**
     * Notify when attacker is going to attack.
     * 
     * @param target The target reference.
     */
    void notifyAttackStarted(E target);

    /**
     * Notify when attacker has done his attack.
     * 
     * @param target The target reference.
     * @param damages The final damages.
     */
    void notifyAttackEnded(int damages, E target);

    /**
     * Notify when attacker has played his attack completely.
     */
    void notifyAttackAnimEnded();

    /**
     * Notify while attacker is waiting for next attack.
     */
    void notifyPreparingAttack();

    /**
     * Notify when attacker lost its target.
     * 
     * @param target The target lost.
     */
    void notifyTargetLost(E target);
}
