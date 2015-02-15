/*
 * Copyright (C) 2013-2015 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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
package com.b3dgs.lionengine.game.strategy.ability.attacker;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.strategy.entity.EntityStrategy;

/**
 * List of services provided by an attacker.
 * 
 * @param <E> The entity type used.
 * @param <A> The attacker type used.
 * @param <W> The weapon type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public interface AttackerServices<E extends EntityStrategy, A extends AttackerUsedServices<E>, W extends WeaponServices<E, A>>
{
    /**
     * Add a weapon.
     * 
     * @param weapon The weapon instance.
     * @param id The weapon index number.
     * @throws LionEngineException If invalid weapon id.
     */
    void addWeapon(W weapon, int id) throws LionEngineException;

    /**
     * Remove a weapon.
     * 
     * @param id The weapon to remove from its index number.
     * @throws LionEngineException If invalid weapon id.
     */
    void removeWeapon(int id) throws LionEngineException;

    /**
     * Get a weapon from its id.
     * 
     * @param id The weapon id.
     * @return The weapon reference.
     * @throws LionEngineException If invalid weapon id.
     */
    W getWeapon(int id) throws LionEngineException;

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
