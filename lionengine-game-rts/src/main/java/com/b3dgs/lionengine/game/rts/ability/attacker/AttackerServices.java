/*
 * Copyright (C) 2013-2014 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.b3dgs.lionengine.game.rts.ability.attacker;

import com.b3dgs.lionengine.game.rts.entity.EntityRts;

/**
 * List of services provided by an attacker.
 * 
 * @param <E> The entity type used.
 * @param <A> The attacker type used.
 * @param <W> The weapon type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface AttackerServices<E extends EntityRts, A extends AttackerUsedServices<E>, W extends WeaponServices<E, A>>
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
     * Get a weapon from its id.
     * 
     * @param id The weapon id.
     * @return The weapon reference.
     */
    W getWeapon(int id);

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
