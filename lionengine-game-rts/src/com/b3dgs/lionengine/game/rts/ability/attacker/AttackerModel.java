/*
 * Copyright (C) 2013 Byron 3D Games Studio (www.b3dgs.com) Pierre-Alexandre (contact@b3dgs.com)
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

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.rts.EntityRts;
import com.b3dgs.lionengine.game.rts.ability.AbilityModel;

/**
 * Default attacker model implementation.
 * 
 * @param <E> The entity type used.
 * @param <W> The weapon type used.
 */
public class AttackerModel<E extends EntityRts, W extends WeaponServices<E>>
        extends AbilityModel<AttackerListener<E>, AttackerUsedServices<E>>
        implements AttackerServices<E, W>, AttackerListener<E>
{
    /** Weapons list. */
    private final Map<Integer, W> weapons;
    /** Current weapon id. */
    private Integer weaponId;

    /**
     * Create a new attacker ability.
     * 
     * @param user The ability user reference.
     */
    public AttackerModel(AttackerUsedServices<E> user)
    {
        super(user);
        weapons = new HashMap<>(1);
    }

    /**
     * Get the weapon instance from its key.
     * 
     * @param id The weapon id.
     * @return The weapon instance.
     */
    private W getWeapon(Integer id)
    {
        if (!weapons.containsKey(id))
        {
            throw new LionEngineException("Weapon id not found: " + id);
        }
        return weapons.get(id);
    }

    /*
     * AttackerServices
     */

    @Override
    public void addWeapon(W weapon, int id)
    {
        final Integer key = Integer.valueOf(id);
        weapons.put(key, weapon);
        if (weaponId == null)
        {
            weaponId = key;
        }
    }

    @Override
    public void removeWeapon(int id)
    {
        weapons.remove(Integer.valueOf(id));
    }

    @Override
    public void setWeapon(int id)
    {
        this.weaponId = Integer.valueOf(id);
    }

    @Override
    public void attack(E target)
    {
        getWeapon(weaponId).attack(target);
    }

    @Override
    public void stopAttack()
    {
        getWeapon(weaponId).stopAttack();
    }

    @Override
    public void updateAttack(double extrp)
    {
        getWeapon(weaponId).updateAttack(extrp);
    }

    @Override
    public boolean isAttacking()
    {
        return getWeapon(weaponId).isAttacking();
    }

    /*
     * AttackerListener
     */

    @Override
    public void notifyReachingTarget(E target)
    {
        for (final AttackerListener<E> listener : listeners)
        {
            listener.notifyReachingTarget(target);
        }
    }

    @Override
    public void notifyAttackStarted(E target)
    {
        for (final AttackerListener<E> listener : listeners)
        {
            listener.notifyAttackStarted(target);
        }
    }

    @Override
    public void notifyAttackEnded(int damages, E target)
    {
        for (final AttackerListener<E> listener : listeners)
        {
            listener.notifyAttackEnded(damages, target);
        }
    }

    @Override
    public void notifyAttackAnimEnded()
    {
        for (final AttackerListener<E> listener : listeners)
        {
            listener.notifyAttackAnimEnded();
        }
    }

    @Override
    public void notifyPreparingAttack()
    {
        for (final AttackerListener<E> listener : listeners)
        {
            listener.notifyPreparingAttack();
        }
    }

    @Override
    public void notifyTargetLost(E target)
    {
        for (final AttackerListener<E> listener : listeners)
        {
            listener.notifyTargetLost(target);
        }
    }
}
