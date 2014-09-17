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
package com.b3dgs.lionengine.game.strategy.ability.attacker;

import java.util.HashMap;
import java.util.Map;

import com.b3dgs.lionengine.LionEngineException;
import com.b3dgs.lionengine.game.strategy.ability.AbilityModel;
import com.b3dgs.lionengine.game.strategy.entity.EntityStrategy;

/**
 * Default attacker model implementation.
 * 
 * @param <E> The entity type used.
 * @param <A> The attacker type used.
 * @param <W> The weapon type used.
 * @author Pierre-Alexandre (contact@b3dgs.com)
 */
public class AttackerModel<E extends EntityStrategy, A extends AttackerUsedServices<E>, W extends WeaponServices<E, A>>
        extends AbilityModel<AttackerListener<E>, A>
        implements AttackerServices<E, A, W>
{
    /** Weapons list. */
    private final Map<Integer, W> weapons;
    /** Current weapon id. */
    private Integer weaponId;

    /**
     * Constructor.
     * 
     * @param user The ability user reference.
     */
    public AttackerModel(A user)
    {
        super(user);
        weapons = new HashMap<>(1);
    }

    /**
     * Get the weapon instance from its key.
     * 
     * @param id The weapon id.
     * @return The weapon instance.
     * @throws LionEngineException If invalid weapon id.
     */
    private W getWeapon(Integer id) throws LionEngineException
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
    public void addWeapon(W weapon, int id) throws LionEngineException
    {
        final Integer key = Integer.valueOf(id);
        weapon.setUser(user);
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
    public W getWeapon(int id) throws LionEngineException
    {
        return getWeapon(Integer.valueOf(id));
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
}
