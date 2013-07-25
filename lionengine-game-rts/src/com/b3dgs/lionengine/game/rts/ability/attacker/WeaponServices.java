package com.b3dgs.lionengine.game.rts.ability.attacker;

import com.b3dgs.lionengine.game.rts.EntityRts;

/**
 * List of services provided by a weapon.
 * 
 * @param <E> The entity type used.
 */
public interface WeaponServices<E extends EntityRts>
{
    /**
     * Define a target to attack.
     * 
     * @param entity The target to attack.
     */
    void attack(E entity);

    /**
     * Stop the current attack.
     */
    void stopAttack();

    /**
     * Update attack routine.
     * 
     * @param extrp The extrapolation value.
     */
    void updateAttack(double extrp);

    /**
     * Set attack pause time between two attacks.
     * 
     * @param time The attack pause time.
     */
    void setAttackTimer(int time);

    /**
     * Represents the frame number (inside animation) which design the attack action.
     * <p>
     * Example: for a footman, the frame number when the sword is going to hurt somebody (between 1-last).
     * </p>
     * 
     * @param frame The frame attack number.
     */
    void setAttackFrame(int frame);

    /**
     * Set attack distance between entity and target.
     * 
     * @param min The minimum attack distance (in tile).
     * @param max The maximum attack distance (in tile).
     */
    void setAttackDistance(int min, int max);

    /**
     * Set attack damages between entity and target.
     * 
     * @param min The minimum attack damages.
     * @param max The maximum attack damages.
     */
    void setAttackDamages(int min, int max);

    /**
     * Get a random attack damages (between min and max).
     * 
     * @return The attack damages.
     */
    int getAttackDamages();

    /**
     * Check weapon is attacking.
     * 
     * @return <code>true</code> if currently attacking, <code>false</code> else.
     */
    boolean isAttacking();

    /**
     * Get the target entity.
     * 
     * @return The target entity.
     */
    E getTarget();
}
