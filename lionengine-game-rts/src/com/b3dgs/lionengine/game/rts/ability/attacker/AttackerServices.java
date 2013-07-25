package com.b3dgs.lionengine.game.rts.ability.attacker;

import com.b3dgs.lionengine.game.rts.EntityRts;

/**
 * List of services provided by an attacker.
 * 
 * @param <E> The entity type used.
 * @param <W> The weapon type used.
 */
public interface AttackerServices<E extends EntityRts, W extends WeaponServices<E>>
{
    /**
     * Add a weapon.
     * 
     * @param weapon The weapon instance.
     * @param id The weapon index number.
     */
    void addWeapon(W weapon, int id);

    /**
     * Remove a weapon.
     * 
     * @param id The weapon to remove from its index number.
     */
    void removeWeapon(int id);

    /**
     * Set the current weapon to use.
     * 
     * @param weaponId The weapon to use from its index number.
     */
    void setWeapon(int weaponId);

    /**
     * Define a target to attack. Attacker will move to target location. When the attacker is close enough to the
     * target, it will perform an attack. When the attack is finished, it will prepare another attack until the target
     * died.
     * 
     * @param entity The target to attack.
     */
    void attack(E entity);

    /**
     * Stop current weapon attack.
     */
    void stopAttack();

    /**
     * Update attack routine.
     * 
     * @param extrp The extrapolation value.
     */
    void updateAttack(double extrp);

    /**
     * Check if currently attacking.
     * 
     * @return <code>true</code> if attacking, <code>false</code> else.
     */
    boolean isAttacking();
}
